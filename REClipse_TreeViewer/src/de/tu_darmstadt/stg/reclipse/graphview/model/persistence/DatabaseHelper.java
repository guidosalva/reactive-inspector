package de.tu_darmstadt.stg.reclipse.graphview.model.persistence;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.model.ISessionConfiguration;
import de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

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
          .asList("CREATE TABLE variable (idVariable  INTEGER NOT NULL PRIMARY KEY, variableId varchar(36) NOT NULL, variableName varchar(200), reactiveType integer(10), typeSimple varchar(200), typeFull varchar(200), idVariableStatusActive integer(10))", //$NON-NLS-1$
                  "CREATE TABLE variable_status (idVariableStatus  INTEGER NOT NULL PRIMARY KEY, idVariable integer(10) NOT NULL, valueString varchar(200))", //$NON-NLS-1$
                  "CREATE TABLE event (pointInTime  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, type integer(10) NOT NULL, idVariable integer(10) NOT NULL, dependentVariable integer(10), exception varchar(200))", //$NON-NLS-1$
                  "CREATE TABLE xref_event_status (pointInTime integer(10) NOT NULL, idVariableStatus integer(10) NOT NULL, PRIMARY KEY (pointInTime, idVariableStatus))", //$NON-NLS-1$
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

  private int getAutoIncrementKey(final Statement stmt) throws SQLException {
    try (final ResultSet rs = stmt.getGeneratedKeys()) {
      rs.next();
      return rs.getInt(1);
    }
  }

  public int findVariableById(final UUID id) throws SQLException {
    if (!variableMap.containsKey(id)) {
      // TODO throw checked persistence exception
      throw new RuntimeException("unknown variable with id " + id);
    }

    return variableMap.get(id);
  }

  public int findActiveVariableStatus(final int idVariable) throws SQLException {
    if (!variableStatusMap.containsKey(idVariable)) {
      // TODO throw checked persistence exception
      throw new RuntimeException("no active status for variable " + idVariable);
    }

    return variableStatusMap.get(idVariable);
  }

  public int createVariable(final ReactiveVariable variable) throws SQLException {
    final String insertStmt = "INSERT INTO variable (variableId, variableName, reactiveType, typeSimple, typeFull) VALUES (?, ?, ?, ?, ?)"; //$NON-NLS-1$

    try (final PreparedStatement stmt = connection.prepareStatement(insertStmt)) {
      stmt.setString(1, variable.getId().toString());
      stmt.setString(2, variable.getName());
      stmt.setInt(3, variable.getReactiveVariableType().ordinal());
      stmt.setString(4, variable.getTypeSimple());
      stmt.setString(5, variable.getTypeFull());
      stmt.executeUpdate();

      final int key = getAutoIncrementKey(stmt);
      variableMap.put(variable.getId(), key);
      return key;
    }
  }

  public int createVariableStatus(final ReactiveVariable variable, final int idVariable) throws SQLException {
    final String insertStmt = "INSERT INTO variable_status (idVariable, valueString) VALUES (?, ?)"; //$NON-NLS-1$

    try (PreparedStatement stmt = connection.prepareStatement(insertStmt)) {
      stmt.setInt(1, idVariable);
      stmt.setString(2, variable.getValueString());
      stmt.executeUpdate();

      final int key = getAutoIncrementKey(stmt);
      variableStatusMap.put(idVariable, key);
      return key;
    }
  }

  public int createVariableStatus(final ReactiveVariable variable, final int idVariable, final int oldVariableStatus) throws SQLException {
    final int id = createVariableStatus(variable, idVariable);

    final String updateStmt = "UPDATE variable SET idVariableStatusActive = ? WHERE idVariable = ?"; //$NON-NLS-1$

    try (PreparedStatement stmt = connection.prepareStatement(updateStmt)) {
      stmt.setInt(1, id);
      stmt.setInt(2, idVariable);
      stmt.executeUpdate();
    }

    final String copyStmt = "INSERT INTO variable_dependency (idVariableStatus, dependentVariable) SELECT ?, dependentVariable FROM variable_dependency WHERE idVariableStatus = ?"; //$NON-NLS-1$

    try (PreparedStatement stmt = connection.prepareStatement(copyStmt)) {
      stmt.setInt(1, id);
      stmt.setInt(2, oldVariableStatus);
      stmt.executeUpdate();
    }

    return id;
  }

  public int createVariableStatus(final ReactiveVariable variable, final int idVariable, final int oldVariableStatus, final int dependentVariable) throws SQLException {
    final int id = createVariableStatus(variable, idVariable, oldVariableStatus);

    final String insertStmt = "INSERT INTO variable_dependency (idVariableStatus, dependentVariable) VALUES (?, ?)"; //$NON-NLS-1$

    try (PreparedStatement stmt = connection.prepareStatement(insertStmt)) {
      stmt.setInt(1, id);
      stmt.setInt(2, dependentVariable);
      stmt.executeUpdate();
    }

    return id;
  }

  public int createEvent(final ReactiveVariable variable, final int idVariable, final Integer dependentVariable) throws SQLException {
    final String insertStmt = "INSERT INTO event (type, idVariable, dependentVariable, exception) VALUES (?, ? ,?, ?)"; //$NON-NLS-1$

    try (PreparedStatement stmt = connection.prepareStatement(insertStmt)) {
      stmt.setInt(1, variable.getDependencyGraphHistoryType().ordinal());
      stmt.setInt(2, idVariable);

      if (dependentVariable != null) {
        stmt.setInt(3, dependentVariable);
      }
      else {
        stmt.setNull(3, Types.INTEGER);
      }

      if (variable.getDependencyGraphHistoryType() == DependencyGraphHistoryType.NODE_EVALUATION_ENDED_WITH_EXCEPTION) {
        stmt.setString(4, variable.getAdditionalInformation());
      }
      else {
        stmt.setNull(4, Types.VARCHAR);
      }
      stmt.executeUpdate();

      return getAutoIncrementKey(stmt);
    }
  }

  public void nextPointInTime(final int pointInTime, final int idVariableStatus, final Integer oldVariableStatus) throws SQLException {
    final String copyStmt = oldVariableStatus != null ? "INSERT INTO xref_event_status (pointInTime, idVariableStatus) SELECT ?, idVariableStatus FROM xref_event_status WHERE pointInTime = ? AND idVariableStatus != ?" //$NON-NLS-1$
            : "INSERT INTO xref_event_status (pointInTime, idVariableStatus) SELECT ?, idVariableStatus FROM xref_event_status WHERE pointInTime = ?"; //$NON-NLS-1$

    try (PreparedStatement stmt = connection.prepareStatement(copyStmt)) {
      stmt.setInt(1, pointInTime);
      stmt.setInt(2, lastPointInTime);

      if (oldVariableStatus != null) {
        stmt.setInt(3, oldVariableStatus);
      }

      stmt.executeUpdate();
    }

    final String insertStmt = "INSERT INTO xref_event_status (pointInTime, idVariableStatus) VALUES (?, ?)"; //$NON-NLS-1$
    try (PreparedStatement stmt = connection.prepareStatement(insertStmt)) {
      stmt.setInt(1, pointInTime);
      stmt.setInt(2, idVariableStatus);
      stmt.executeUpdate();
    }

    lastPointInTime = pointInTime;
  }

  public ArrayList<ReactiveVariable> getReVars(final int pointInTime) {
    // TODO update to new schema
    return null;
  }

  public UUID getIdFromName(final String name) {
    // TODO variables should be referenced by their IDs

    final String sql = "SELECT variableId FROM variables WHERE variableName = ?"; //$NON-NLS-1$
    try (final PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, name);
      try (final ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          return UUID.fromString(rs.getString("id")); //$NON-NLS-1$
        }
      }
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
    return null;
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
