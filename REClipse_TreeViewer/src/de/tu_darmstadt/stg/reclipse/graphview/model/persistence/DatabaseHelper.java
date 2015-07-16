package de.tu_darmstadt.stg.reclipse.graphview.model.persistence;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.model.ISessionConfiguration;
import de.tu_darmstadt.stg.reclipse.graphview.model.persistence.DependencyGraph.Vertex;
import de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariableType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Helper class which does all the work related to the database, e.g. storing
 * variables, retrieving them etc.
 */
public class DatabaseHelper {

  private static final String JDBC_CLASS_NAME = "org.sqlite.JDBC"; //$NON-NLS-1$
  private static final String JDBC_USER = ""; //$NON-NLS-1$
  private static final String JDBC_PASSWORD = ""; //$NON-NLS-1$

  private static List<String> databaseSetupQueries = Arrays
          .asList("CREATE TABLE variable (idVariable  INTEGER NOT NULL PRIMARY KEY, variableId varchar(36) NOT NULL, variableName varchar(200), reactiveType integer(10), typeSimple varchar(200), typeFull varchar(200), timeFrom integer(10) NOT NULL)", //$NON-NLS-1$
                  "CREATE TABLE variable_status (idVariableStatus  INTEGER NOT NULL PRIMARY KEY, idVariable integer(10) NOT NULL, valueString varchar(200), timeFrom integer(10) NOT NULL, timeTo integer(10) NOT NULL, exception integer(1) NOT NULL)", //$NON-NLS-1$
                  "CREATE TABLE event (pointInTime  INTEGER NOT NULL PRIMARY KEY, type integer(10) NOT NULL, idVariable integer(10) NOT NULL, dependentVariable integer(10))", //$NON-NLS-1$
                  "CREATE TABLE variable_dependency (idVariableStatus integer(10) NOT NULL, dependentVariable integer(10) NOT NULL, PRIMARY KEY (idVariableStatus, dependentVariable))"); //$NON-NLS-1$

  private final List<IDependencyGraphListener> listeners = new CopyOnWriteArrayList<>();
  private final String sessionId;
  private final Map<UUID, Integer> variableMap = new HashMap<>();
  private final Map<Integer, Integer> variableStatusMap = new HashMap<>();

  private Connection connection;

  private int lastPointInTime = 0;

  public DatabaseHelper(final String sessionId, final ISessionConfiguration configuration) {
    this.sessionId = sessionId;

    establishConnection();

    try (final Statement stmt = connection.createStatement()) {
      for (final String sql : databaseSetupQueries) {
        stmt.executeUpdate(sql);
      }
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
  }

  /**
   * Reads the database connection settings from the Esper configuration file
   * and returns a fresh database connection, which automatically commits.
   *
   * @return a fresh database connection
   */
  private void establishConnection() {
    try {
      // establish DB connection
      Class.forName(JDBC_CLASS_NAME);
      final String jdbcUrl = getJdbcUrl();
      connection = DriverManager.getConnection(jdbcUrl, JDBC_USER, JDBC_PASSWORD);
      connection.setAutoCommit(true);
    }
    catch (final ClassNotFoundException e) {
      Activator.log(e);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
  }

  private void beginTx() throws PersistenceException {
    try {
      connection.setAutoCommit(false);
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }
  }

  private void closeTx() throws PersistenceException {
    try {
      connection.setAutoCommit(true);
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }
  }

  private void commit() throws PersistenceException {
    try {
      connection.commit();
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }
  }

  private void rollback() throws PersistenceException {
    try {
      connection.rollback();
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }
  }

  public Connection getConnection() {
    return connection;
  }

  public void addDependencyGraphListener(final IDependencyGraphListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  public void removeDependencyGraphListener(final IDependencyGraphListener listener) {
    listeners.remove(listener);
  }

  protected void fireChangedEvent(final DependencyGraphHistoryType type, final int pointInTime) {
    for (final IDependencyGraphListener l : listeners) {
      l.onDependencyGraphChanged(type, pointInTime);
    }
  }

  /**
   * @return the last point in time of the dependency graph history
   */
  public int getLastPointInTime() {
    return lastPointInTime;
  }

  public synchronized void logNodeCreated(final ReactiveVariable r) throws PersistenceException {
    try {
      beginTx();

      nextPointInTime();

      final int idVariable = createVariable(r);
      createVariableStatus(r, idVariable, null);
      createEvent(r, idVariable, null);

      r.setPointInTime(lastPointInTime);

      commit();
    }
    catch (PersistenceException | RuntimeException e) {
      rollback();
      throw e;
    }
    finally {
      closeTx();
    }

    fireChangedEvent(DependencyGraphHistoryType.NODE_CREATED, lastPointInTime);
  }

  public synchronized void logNodeAttached(final ReactiveVariable r, final UUID dependentId) throws PersistenceException {
    try {
      beginTx();

      nextPointInTime();

      final int idVariable = findVariableById(r.getId());
      final int dependentVariable = findVariableById(dependentId);

      final int oldVariableStatus = findActiveVariableStatus(idVariable);

      createVariableStatus(r, idVariable, oldVariableStatus, dependentVariable, null);
      createEvent(r, idVariable, dependentVariable);

      // TODO use node name instead of id in additionalInformation
      final String additionalInformation = r.getId() + "->" + dependentId; //$NON-NLS-1$
      r.setPointInTime(lastPointInTime);
      r.setAdditionalInformation(additionalInformation);
      r.setConnectedWith(dependentId);
      commit();
    }
    catch (PersistenceException | RuntimeException e) {
      rollback();
      throw e;
    }
    finally {
      closeTx();
    }

    fireChangedEvent(DependencyGraphHistoryType.NODE_ATTACHED, lastPointInTime);
  }

  public synchronized void logNodeEvaluationEnded(final ReactiveVariable r) throws PersistenceException {
    try {
      beginTx();

      nextPointInTime();

      final int idVariable = findVariableById(r.getId());
      final int oldVariableStatus = findActiveVariableStatus(idVariable);
      createVariableStatus(r, idVariable, oldVariableStatus, null);
      createEvent(r, idVariable, null);

      r.setPointInTime(lastPointInTime);

      commit();
    }
    catch (PersistenceException | RuntimeException e) {
      rollback();
      throw e;
    }
    finally {
      closeTx();
    }

    fireChangedEvent(DependencyGraphHistoryType.NODE_EVALUATION_ENDED, lastPointInTime);
  }

  public synchronized void logNodeEvaluationEndedWithException(final ReactiveVariable r, final Exception exception) throws PersistenceException {
    try {
      beginTx();

      nextPointInTime();

      final int idVariable = findVariableById(r.getId());
      final int oldVariableStatus = findActiveVariableStatus(idVariable);

      createVariableStatus(r, idVariable, oldVariableStatus, exception);
      createEvent(r, idVariable, null);

      r.setPointInTime(lastPointInTime);

      commit();
    }
    catch (PersistenceException | RuntimeException e) {
      rollback();
      throw e;
    }
    finally {
      closeTx();
    }

    fireChangedEvent(DependencyGraphHistoryType.NODE_EVALUATION_ENDED_WITH_EXCEPTION, lastPointInTime);
  }

  public synchronized void logNodeEvaluationStarted(final ReactiveVariable r) throws PersistenceException {
    try {
      beginTx();

      nextPointInTime();

      final int idVariable = findVariableById(r.getId());
      final int oldVariableStatus = findActiveVariableStatus(idVariable);
      createVariableStatus(r, idVariable, oldVariableStatus, null);
      createEvent(r, idVariable, null);

      r.setPointInTime(lastPointInTime);

      commit();
    }
    catch (PersistenceException | RuntimeException e) {
      rollback();
      throw e;
    }
    finally {
      closeTx();
    }

    fireChangedEvent(DependencyGraphHistoryType.NODE_EVALUATION_STARTED, lastPointInTime);
  }

  public synchronized void logNodeValueSet(final ReactiveVariable r) throws PersistenceException {
    try {
      beginTx();

      nextPointInTime();

      final int idVariable = findVariableById(r.getId());
      final int oldVariableStatus = findActiveVariableStatus(idVariable);
      createVariableStatus(r, idVariable, oldVariableStatus, null);
      createEvent(r, idVariable, null);

      r.setPointInTime(lastPointInTime);

      commit();
    }
    catch (PersistenceException | RuntimeException e) {
      rollback();
      throw e;
    }
    finally {
      closeTx();
    }

    fireChangedEvent(DependencyGraphHistoryType.NODE_VALUE_SET, lastPointInTime);
  }

  private void nextPointInTime() {
    lastPointInTime++;
  }

  private int getAutoIncrementKey(final Statement stmt) throws PersistenceException {
    try (final ResultSet rs = stmt.getGeneratedKeys()) {
      rs.next();
      return rs.getInt(1);
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }
  }

  public int findVariableById(final UUID id) throws PersistenceException {
    if (!variableMap.containsKey(id)) {
      throw new PersistenceException("unknown variable with id " + id); //$NON-NLS-1$
    }

    return variableMap.get(id);
  }

  private int findActiveVariableStatus(final int idVariable) throws PersistenceException {
    if (!variableStatusMap.containsKey(idVariable)) {
      throw new PersistenceException("no active status for variable " + idVariable); //$NON-NLS-1$
    }

    return variableStatusMap.get(idVariable);
  }

  private int createVariable(final ReactiveVariable variable) throws PersistenceException {
    final String insertStmt = "INSERT INTO variable (variableId, variableName, reactiveType, typeSimple, typeFull, timeFrom) VALUES (?, ?, ?, ?, ?, ?)"; //$NON-NLS-1$

    try (final PreparedStatement stmt = connection.prepareStatement(insertStmt)) {
      stmt.setString(1, variable.getId().toString());
      stmt.setString(2, variable.getName());
      stmt.setInt(3, variable.getReactiveVariableType().ordinal());
      stmt.setString(4, variable.getTypeSimple());
      stmt.setString(5, variable.getTypeFull());
      stmt.setInt(6, lastPointInTime);
      stmt.executeUpdate();

      final int key = getAutoIncrementKey(stmt);
      variableMap.put(variable.getId(), key);
      return key;
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }
  }

  private int createVariableStatus(final ReactiveVariable variable, final int idVariable, final Exception exception) throws PersistenceException {
    final String insertStmt = "INSERT INTO variable_status (idVariable, valueString, timeFrom, timeTo, exception) VALUES (?, ?, ?, ?, ?)"; //$NON-NLS-1$

    try (PreparedStatement stmt = connection.prepareStatement(insertStmt)) {

      stmt.setInt(1, idVariable);
      stmt.setInt(3, lastPointInTime);
      stmt.setInt(4, Integer.MAX_VALUE);

      if (exception != null) {
        stmt.setString(2, exception.toString());
        stmt.setBoolean(5, true);
      }
      else {
        stmt.setString(2, variable.getValueString());
        stmt.setBoolean(5, false);
      }

      stmt.executeUpdate();

      final int key = getAutoIncrementKey(stmt);
      variableStatusMap.put(idVariable, key);
      return key;
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }
  }

  private int createVariableStatus(final ReactiveVariable variable, final int idVariable, final int oldVariableStatus, final Exception exception) throws PersistenceException {
    final String updateStmt = "UPDATE variable_status SET timeTo = ? WHERE idVariableStatus = ?"; //$NON-NLS-1$

    try (PreparedStatement stmt = connection.prepareStatement(updateStmt)) {
      stmt.setInt(1, lastPointInTime - 1);
      stmt.setInt(2, oldVariableStatus);
      stmt.executeUpdate();
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }

    final int id = createVariableStatus(variable, idVariable, exception);

    final String copyStmt = "INSERT INTO variable_dependency (idVariableStatus, dependentVariable) SELECT ?, dependentVariable FROM variable_dependency WHERE idVariableStatus = ?"; //$NON-NLS-1$

    try (PreparedStatement stmt = connection.prepareStatement(copyStmt)) {
      stmt.setInt(1, id);
      stmt.setInt(2, oldVariableStatus);
      stmt.executeUpdate();
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }

    return id;
  }

  private int createVariableStatus(final ReactiveVariable variable, final int idVariable, final int oldVariableStatus, final int dependentVariable, final Exception exception)
          throws PersistenceException {
    final int id = createVariableStatus(variable, idVariable, oldVariableStatus, exception);

    final String insertStmt = "REPLACE INTO variable_dependency (idVariableStatus, dependentVariable) VALUES (?, ?)"; //$NON-NLS-1$

    try (PreparedStatement stmt = connection.prepareStatement(insertStmt)) {
      stmt.setInt(1, id);
      stmt.setInt(2, dependentVariable);
      stmt.executeUpdate();
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }

    return id;
  }

  private void createEvent(final ReactiveVariable variable, final int idVariable, final Integer dependentVariable) throws PersistenceException {
    final String insertStmt = "INSERT INTO event (pointInTime, type, idVariable, dependentVariable) VALUES (?, ? ,?, ?)"; //$NON-NLS-1$

    try (PreparedStatement stmt = connection.prepareStatement(insertStmt)) {
      stmt.setInt(1, lastPointInTime);
      stmt.setInt(2, variable.getDependencyGraphHistoryType().ordinal());
      stmt.setInt(3, idVariable);

      if (dependentVariable != null) {
        stmt.setInt(4, dependentVariable);
      }
      else {
        stmt.setNull(4, Types.INTEGER);
      }

      stmt.executeUpdate();
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }
  }

  public List<ReactiveVariable> getReVarsWithDependencies(final int pointInTime) throws PersistenceException {
    final List<ReactiveVariable> variables = new ArrayList<>();

    final String query = "SELECT variable.variableId AS variableId, variable.variableName AS variableName, variable.reactiveType AS reactiveType, event.type AS historyType, variable.typeSimple AS typeSimple, variable.typeFull AS typeFull, variable_status.valueString AS valueString, variable_status.idVariableStatus AS idVariableStatus, variable_status.exception AS exception FROM variable, event JOIN variable_status ON variable_status.idVariable = variable.idVariable WHERE event.pointInTime = ? AND variable.timeFrom <= ? AND variable_status.timeFrom <= ? AND variable_status.timeTo >= ?"; //$NON-NLS-1$
    try (final PreparedStatement stmt = connection.prepareStatement(query)) {
      stmt.setInt(1, pointInTime);
      stmt.setInt(2, pointInTime);
      stmt.setInt(3, pointInTime);
      stmt.setInt(4, pointInTime);

      try (final ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          final ReactiveVariable r = createReVar(rs, pointInTime);
          updateConnectedWith(r, rs);
          variables.add(r);
        }
      }
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }

    return variables;
  }

  private ReactiveVariable createReVar(final ResultSet rs, final int pointInTime) throws SQLException {
    final ReactiveVariable r = new ReactiveVariable();
    r.setId(UUID.fromString(rs.getString("variableId"))); //$NON-NLS-1$
    r.setName(rs.getString("variableName")); //$NON-NLS-1$
    r.setReactiveVariableType(ReactiveVariableType.values()[rs.getInt("reactiveType")]); //$NON-NLS-1$
    r.setPointInTime(pointInTime);
    //r.setDependencyGraphHistoryType(DependencyGraphHistoryType.values()[rs.getInt("historyType")]); //$NON-NLS-1$
    r.setAdditionalInformation(""); // TODO load additional information field //$NON-NLS-1$
    r.setTypeSimple(rs.getString("typeSimple")); //$NON-NLS-1$
    r.setTypeFull(rs.getString("typeFull")); //$NON-NLS-1$
    r.setAdditionalKeys(new HashMap<String, Object>()); // TODO load additional
    // keys field
    r.setValueString(rs.getString("valueString")); //$NON-NLS-1$
    r.setExceptionOccured(rs.getBoolean("exception")); //$NON-NLS-1$

    return r;
  }

  private void updateConnectedWith(final ReactiveVariable r, final ResultSet rs) throws SQLException {
    final int idVariableStatus = rs.getInt("idVariableStatus"); //$NON-NLS-1$

    final String query = "SELECT variableId FROM variable JOIN variable_dependency ON variable_dependency.dependentVariable = variable.idVariable WHERE variable_dependency.idVariableStatus = ?"; //$NON-NLS-1$
    try (final PreparedStatement stmt = connection.prepareStatement(query)) {
      stmt.setInt(1, idVariableStatus);

      try (ResultSet rs2 = stmt.executeQuery()) {
        while (rs2.next()) {
          final String id = rs2.getString(1);
          r.setConnectedWith(UUID.fromString(id));
        }
      }
    }
  }

  public UUID getIdFromName(final String name) {
    // TODO variables should be referenced by their IDs

    final String sql = "SELECT variableId FROM variable WHERE variableName = ?"; //$NON-NLS-1$
    try (final PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, name);
      try (final ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          return UUID.fromString(rs.getString("variableId")); //$NON-NLS-1$
        }
      }
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
    return null;
  }

  public DependencyGraph getDependencyGraph(final int pointInTime) throws PersistenceException {
    final List<Vertex> vertices = loadVertices(pointInTime);
    connectVertices(vertices, pointInTime);
    return new DependencyGraph(vertices);
  }

  private List<Vertex> loadVertices(final int pointInTime) throws PersistenceException {
    final List<Vertex> vertices = new ArrayList<>();

    final String query = "SELECT variable.idVariable AS idVariable, variable.variableId AS variableId, variable.variableName AS variableName, variable.reactiveType AS reactiveType, variable.typeSimple AS typeSimple, variable.typeFull AS typeFull, variable_status.valueString AS valueString, variable.timeFrom AS timeFrom, variable_status.exception AS exception FROM variable JOIN variable_status ON variable_status.idVariable = variable.idVariable WHERE variable.timeFrom <= ? AND variable_status.timeFrom <= ? AND variable_status.timeTo >= ?"; //$NON-NLS-1$
    try (final PreparedStatement stmt = connection.prepareStatement(query)) {
      stmt.setInt(1, pointInTime);
      stmt.setInt(2, pointInTime);
      stmt.setInt(3, pointInTime);

      try (final ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          final int idVariable = rs.getInt("idVariable"); //$NON-NLS-1$
          final int created = rs.getInt("timeFrom"); //$NON-NLS-1$
          final ReactiveVariable r = createReVar(rs, pointInTime);
          final Vertex vertex = new Vertex(idVariable, created, r);
          vertices.add(vertex);
        }
      }
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }

    return vertices;
  }

  private void connectVertices(final List<Vertex> vertices, final int pointInTime) throws PersistenceException {
    final Map<Integer, Vertex> vertexMap = new HashMap<>();

    for (final Vertex vertex : vertices) {
      vertexMap.put(vertex.getId(), vertex);
    }

    final String dependencyQuery = "SELECT variable_status.idVariable AS idVariable, variable_dependency.dependentVariable AS dependentVariable FROM variable_dependency JOIN variable_status ON variable_status.idVariableStatus = variable_dependency.idVariableStatus WHERE variable_status.timeFrom <= ? AND variable_status.timeTo >= ?"; //$NON-NLS-1$
    try (final PreparedStatement stmt = connection.prepareStatement(dependencyQuery)) {
      stmt.setInt(1, pointInTime);
      stmt.setInt(2, pointInTime);

      try (final ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          final int idVariable = rs.getInt("idVariable"); //$NON-NLS-1$
          final int dependentId = rs.getInt("dependentVariable"); //$NON-NLS-1$

          if (!vertexMap.containsKey(idVariable)) {
            throw new PersistenceException("vertex for variable with internal id " + idVariable + " is missing"); //$NON-NLS-1$ //$NON-NLS-2$
          }

          final Vertex v = vertexMap.get(idVariable);
          final Vertex dependent = vertexMap.get(dependentId);
          v.addConnectedVertex(dependent);
        }
      }
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }
  }

  public void close() {
    if (connection != null) {
      try {
        connection.close();
      }
      catch (final SQLException e) {
        Activator.log(e);
      }
    }
  }

  protected String getJdbcUrl() {
    // shared in-memory database
    return "jdbc:sqlite:file:" + sessionId + "?mode=memory&cache=shared"; //$NON-NLS-1$ //$NON-NLS-2$
  }

  protected String getJdbcClassName() {
    return JDBC_CLASS_NAME;
  }

  protected String getJdbcUser() {
    return JDBC_USER;
  }

  protected String getJdbcPassword() {
    return JDBC_PASSWORD;
  }

  public String getSessionId() {
    return sessionId;
  }
}
