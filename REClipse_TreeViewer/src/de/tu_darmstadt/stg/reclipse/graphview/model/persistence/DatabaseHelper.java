package de.tu_darmstadt.stg.reclipse.graphview.model.persistence;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.model.ISessionConfiguration;
import de.tu_darmstadt.stg.reclipse.graphview.model.persistence.DependencyGraph.Vertex;
import de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariableType;

import java.io.File;
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
                  "CREATE TABLE variable_status (idVariableStatus  INTEGER NOT NULL PRIMARY KEY, idVariable integer(10) NOT NULL, valueString varchar(200), timeFrom integer(10) NOT NULL, timeTo integer(10) NOT NULL)", //$NON-NLS-1$
                  "CREATE TABLE event (pointInTime  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, type integer(10) NOT NULL, idVariable integer(10) NOT NULL, dependentVariable integer(10), exception varchar(200))", //$NON-NLS-1$
                  "CREATE TABLE variable_dependency (idVariableStatus integer(10) NOT NULL, dependentVariable integer(10) NOT NULL)"); //$NON-NLS-1$

  private final List<DependencyGraphHistoryChangedListener> listeners = new CopyOnWriteArrayList<>();
  private final File dbFile;
  private final Map<UUID, Integer> variableMap = new HashMap<>();
  private final Map<Integer, Integer> variableStatusMap = new HashMap<>();

  private Connection connection;

  // cache this field locally, because it is queried quite often
  private int lastPointInTime = 0;

  public DatabaseHelper(final String id, final ISessionConfiguration configuration) {
    this.dbFile = configuration.getDatabaseFilesDir().append(id + ".db").toFile(); //$NON-NLS-1$

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
      dbFile.getParentFile().mkdirs();

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

  public void addDepGraphHistoryChangedListener(final DependencyGraphHistoryChangedListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  public void removeDepGraphHistoryChangedListener(final DependencyGraphHistoryChangedListener listener) {
    listeners.remove(listener);
  }

  protected void fireChangedEvent() {
    for (final DependencyGraphHistoryChangedListener l : listeners) {
      l.dependencyGraphHistoryChanged();
    }
  }

  /**
   * @return the last point in time of the dependency graph history
   */
  public int getLastPointInTime() {
    return lastPointInTime;
  }

  public void logNodeCreated(final ReactiveVariable r) throws PersistenceException {
    try {
      beginTx();

      nextPointInTime();

      final int idVariable = createVariable(r);
      createVariableStatus(r, idVariable);
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
  }

  public void logNodeAttached(final ReactiveVariable r, final UUID dependentId) throws PersistenceException {
    try {
      beginTx();

      nextPointInTime();

      final int idVariable = findVariableById(r.getId());
      final int dependentVariable = findVariableById(dependentId);

      final int oldVariableStatus = findActiveVariableStatus(idVariable);

      createVariableStatus(r, idVariable, oldVariableStatus, dependentVariable);
      createEvent(r, idVariable, dependentVariable);

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
  }

  public void logNodeEvaluationEnded(final ReactiveVariable r) throws PersistenceException {
    try {
      beginTx();

      nextPointInTime();

      final int idVariable = findVariableById(r.getId());
      final int oldVariableStatus = findActiveVariableStatus(idVariable);
      createVariableStatus(r, idVariable, oldVariableStatus);
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
  }

  public void logNodeEvaluationEndedWithException(final ReactiveVariable r, final Exception exception) throws PersistenceException {
    try {
      beginTx();

      nextPointInTime();

      final int idVariable = findVariableById(r.getId());
      final int oldVariableStatus = findActiveVariableStatus(idVariable);

      createVariableStatus(r, idVariable, oldVariableStatus);
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
  }

  public void logNodeEvaluationStarted(final ReactiveVariable r) throws PersistenceException {
    try {
      beginTx();

      nextPointInTime();

      final int idVariable = findVariableById(r.getId());
      final int oldVariableStatus = findActiveVariableStatus(idVariable);
      createVariableStatus(r, idVariable, oldVariableStatus);
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
  }

  public void logNodeValueSet(final ReactiveVariable r) throws PersistenceException {
    try {
      beginTx();

      nextPointInTime();

      final int idVariable = findVariableById(r.getId());
      final int oldVariableStatus = findActiveVariableStatus(idVariable);
      createVariableStatus(r, idVariable, oldVariableStatus);
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

  public int findActiveVariableStatus(final int idVariable) throws PersistenceException {
    if (!variableStatusMap.containsKey(idVariable)) {
      throw new PersistenceException("no active status for variable " + idVariable); //$NON-NLS-1$
    }

    return variableStatusMap.get(idVariable);
  }

  public int createVariable(final ReactiveVariable variable) throws PersistenceException {
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

  public int createVariableStatus(final ReactiveVariable variable, final int idVariable) throws PersistenceException {
    final String insertStmt = "INSERT INTO variable_status (idVariable, valueString, timeFrom, timeTo) VALUES (?, ?, ?, ?)"; //$NON-NLS-1$

    try (PreparedStatement stmt = connection.prepareStatement(insertStmt)) {
      stmt.setInt(1, idVariable);
      stmt.setString(2, variable.getValueString());
      stmt.setInt(3, lastPointInTime);
      stmt.setInt(4, Integer.MAX_VALUE);
      stmt.executeUpdate();

      final int key = getAutoIncrementKey(stmt);
      variableStatusMap.put(idVariable, key);
      return key;
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }
  }

  public int createVariableStatus(final ReactiveVariable variable, final int idVariable, final int oldVariableStatus) throws PersistenceException {
    final String updateStmt = "UPDATE variable_status SET timeTo = ? WHERE idVariableStatus = ?"; //$NON-NLS-1$

    try (PreparedStatement stmt = connection.prepareStatement(updateStmt)) {
      stmt.setInt(1, lastPointInTime - 1);
      stmt.setInt(2, oldVariableStatus);
      stmt.executeUpdate();
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }

    final int id = createVariableStatus(variable, idVariable);

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

  public int createVariableStatus(final ReactiveVariable variable, final int idVariable, final int oldVariableStatus, final int dependentVariable) throws PersistenceException {
    final int id = createVariableStatus(variable, idVariable, oldVariableStatus);

    final String insertStmt = "INSERT INTO variable_dependency (idVariableStatus, dependentVariable) VALUES (?, ?)"; //$NON-NLS-1$

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

  public void createEvent(final ReactiveVariable variable, final int idVariable, final Integer dependentVariable) throws PersistenceException {
    final String insertStmt = "INSERT INTO event (pointInTime, type, idVariable, dependentVariable, exception) VALUES (?, ? ,?, ?, ?)"; //$NON-NLS-1$

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

      if (variable.getDependencyGraphHistoryType() == DependencyGraphHistoryType.NODE_EVALUATION_ENDED_WITH_EXCEPTION) {
        stmt.setString(5, variable.getAdditionalInformation());
      }
      else {
        stmt.setNull(5, Types.VARCHAR);
      }

      stmt.executeUpdate();
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }
  }

  public List<ReactiveVariable> getReVarsWithDependencies(final int pointInTime) throws PersistenceException {
    final List<ReactiveVariable> variables = new ArrayList<>();

    final String query = "SELECT variable.variableId AS variableId, variable.variableName AS variableName, variable.reactiveType AS reactiveType, event.type AS historyType, variable.typeSimple AS typeSimple, variable.typeFull AS typeFull, variable_status.valueString AS valueString, variable_status.idVariableStatus AS idVariableStatus FROM variable JOIN variable_status ON variable_status.idVariable = variable.idVariable JOIN xref_event_status ON xref_event_status.idVariableStatus = variable_status.idVariableStatus JOIN event ON event.pointInTime = xref_event_status.pointInTime WHERE event.pointInTime = ?"; //$NON-NLS-1$
    try (final PreparedStatement stmt = connection.prepareStatement(query)) {
      stmt.setInt(1, pointInTime);

      try (final ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          final ReactiveVariable r = createReVar(rs, pointInTime);
          updateConnectedWith(r, rs);
          variables.add(r);
        }
      }
    }
    catch (final SQLException e) {
      throw new PersistenceException();
    }

    return variables;
  }

  private ReactiveVariable createReVar(final ResultSet rs, final int pointInTime) throws SQLException {
    final ReactiveVariable r = new ReactiveVariable();
    r.setId(UUID.fromString(rs.getString("variableId"))); //$NON-NLS-1$
    r.setName(rs.getString("variableName")); //$NON-NLS-1$
    r.setReactiveVariableType(ReactiveVariableType.values()[rs.getInt("reactiveType")]); //$NON-NLS-1$
    r.setPointInTime(pointInTime);
    r.setDependencyGraphHistoryType(DependencyGraphHistoryType.values()[rs.getInt("historyType")]); //$NON-NLS-1$
    r.setAdditionalInformation(""); // TODO load additional information field
    r.setActive(true); // TODO load active field
    r.setTypeSimple(rs.getString("typeSimple")); //$NON-NLS-1$
    r.setTypeFull(rs.getString("typeFull")); //$NON-NLS-1$
    r.setAdditionalKeys(new HashMap<String, Object>()); // TODO load additional
    // keys field
    r.setValueString(rs.getString("valueString")); //$NON-NLS-1$

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
    final Map<Integer, Vertex> verticesByVariable = new HashMap<>();
    final Map<Integer, Vertex> verticesByStatus = new HashMap<>();

    final String query = "SELECT variable.idVariable AS idVariable, variable.variableId AS variableId, variable.variableName AS variableName, variable.reactiveType AS reactiveType, event.type AS historyType, variable.typeSimple AS typeSimple, variable.typeFull AS typeFull, variable_status.valueString AS valueString, variable_status.idVariableStatus AS idVariableStatus FROM variable JOIN variable_status ON variable_status.idVariable = variable.idVariable JOIN event ON event.pointInTime = ? WHERE variable.timeFrom <= ? AND variable_status.timeFrom <= ? AND variable_status.timeTo >= ?"; //$NON-NLS-1$
    try (final PreparedStatement stmt = connection.prepareStatement(query)) {
      stmt.setInt(1, pointInTime);
      stmt.setInt(2, pointInTime);
      stmt.setInt(3, pointInTime);
      stmt.setInt(4, pointInTime);

      try (final ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          final int idVariable = rs.getInt("idVariable"); //$NON-NLS-1$
          final int idVariableStatus = rs.getInt("idVariableStatus"); //$NON-NLS-1$
          final ReactiveVariable r = createReVar(rs, pointInTime);
          final Vertex vertex = new Vertex(r.getId(), r);
          verticesByVariable.put(idVariable, vertex);
          verticesByStatus.put(idVariableStatus, vertex);
        }
      }
    }
    catch (final SQLException e) {
      throw new PersistenceException();
    }

    final String dependencyQuery = "SELECT variable_dependency.idVariableStatus AS idVariableStatus, variable_dependency.dependentVariable AS dependentVariable FROM variable_dependency JOIN variable_status ON variable_status.idVariableStatus = variable_dependency.idVariableStatus WHERE variable_status.timeFrom <= ? AND variable_status.timeTo >= ?"; //$NON-NLS-1$
    try (final PreparedStatement stmt = connection.prepareStatement(dependencyQuery)) {
      stmt.setInt(1, pointInTime);
      stmt.setInt(2, pointInTime);

      try (final ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          final int idStatus = rs.getInt("idVariableStatus"); //$NON-NLS-1$
          final int dependentId = rs.getInt("dependentVariable"); //$NON-NLS-1$

          final Vertex v = verticesByStatus.get(idStatus);
          final Vertex dependent = verticesByVariable.get(dependentId);
          v.addConnectedVertex(dependent);
        }
      }
    }
    catch (final SQLException e) {
      throw new PersistenceException(e);
    }

    final DependencyGraph dependencyGraph = new DependencyGraph();
    dependencyGraph.addVertices(verticesByVariable.values());
    return dependencyGraph;
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
    return "jdbc:sqlite:" + dbFile.getAbsolutePath(); //$NON-NLS-1$
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
}
