package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariableType;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Helper class which does all the work related to the database, e.g. storing
 * variables, retrieving them etc.
 */
public class DatabaseHelper {

  public static final String REACTIVE_VARIABLES_TABLE_NAME = "revars"; //$NON-NLS-1$

  private static DatabaseHelper instance = null;
  private static List<String> databaseSetupQueries = Arrays
          .asList("DROP TABLE IF EXISTS " + REACTIVE_VARIABLES_TABLE_NAME, //$NON-NLS-1$
                  "CREATE TABLE IF NOT EXISTS `" + REACTIVE_VARIABLES_TABLE_NAME + "` (`auto_increment_id` int(11) NOT NULL AUTO_INCREMENT, `id` char(36) NOT NULL, `reactiveVariableType` int(11) NOT NULL, `pointInTime` int(11) DEFAULT NULL, `dependencyGraphHistoryType` int(11) NOT NULL, `additionalInformation` varchar(200) DEFAULT NULL, `active` tinyint(1) DEFAULT NULL, `typeSimple` varchar(200) DEFAULT NULL, `typeFull` varchar(200) DEFAULT NULL, `name` varchar(200) DEFAULT NULL, `additionalKeys` varchar(500) DEFAULT NULL, `valueString` varchar(200) DEFAULT NULL, `connectedWith` varchar(500) DEFAULT NULL, PRIMARY KEY (`auto_increment_id`), KEY `pointInTime` (`pointInTime`)) ENGINE=InnoDB  DEFAULT CHARSET=latin1;"); //$NON-NLS-1$ //$NON-NLS-2$

  private final CopyOnWriteArrayList<DependencyGraphHistoryChangedListener> listeners = new CopyOnWriteArrayList<>();

  // cache this field locally, because it is queried quite often
  private static int lastPointInTime = 0;

  private DatabaseHelper(final Connection connection) {
    // clients should not be able to create instances
    // setup all required database tables
    try (final Statement stmt = connection.createStatement()) {
      for (final String sql : databaseSetupQueries) {
        stmt.executeUpdate(sql);
      }
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
  }

  public static DatabaseHelper getInstance() {
    if (instance == null) {
      try (Connection connection = getConnection()) {
        instance = new DatabaseHelper(connection);
      }
      catch (final SQLException e) {
        Activator.log(e);
      }
    }
    return instance;
  }

  public static DatabaseHelper getInstance(final Connection connection) {
    if (instance == null) {
      instance = new DatabaseHelper(connection);
    }
    return instance;
  }

  public void addDepGraphHistoryChangedListener(final DependencyGraphHistoryChangedListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  protected void fireChangedEvent() {
    for (final DependencyGraphHistoryChangedListener l : listeners) {
      l.dependencyGraphHistoryChanged();
    }
  }

  /**
   * Reads the database connection settings from the Esper configuration file
   * and returns a fresh database connection, which automatically commits.
   *
   * @return a fresh database connection
   */
  private static Connection getConnection() {
    // TODO maybe always use same DB connection
    Connection connection = null;
    try {
      final EsperConfigurationReader esperConfig = EsperConfigurationReader.getInstance();

      // establish DB connection
      Class.forName(esperConfig.getJdbcClassName());
      connection = DriverManager.getConnection(esperConfig.getJdbcUrl(), esperConfig.getJdbcUser(), esperConfig.getJdbcPassword());
      connection.setAutoCommit(true);
    }
    catch (final ClassNotFoundException e) {
      Activator.log(e);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
    return connection;
  }

  public int truncateTable(final String table) {
    try (final Connection connection = getConnection()) {
      return truncateTable(connection, table);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
    return -1;
  }

  public int truncateTable(final Connection connection, final String table) {
    try (final Statement stmt = connection.createStatement()) {
      final int result = stmt.executeUpdate("DELETE FROM " + table); //$NON-NLS-1$
      fireChangedEvent();
      return result;
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
    return -1;
  }

  /**
   * @return the last point in time of the dependency graph history
   */
  public static int getLastPointInTime() {
    return lastPointInTime;
  }

  /**
   * Resets the last point in time for a new debugging session.
   */
  public static void resetLastPointInTime() {
    lastPointInTime = 0;
  }

  public static void copyLastReVars(final DependencyGraphHistoryType newType) {
    try (Connection connection = getConnection()) {
      copyLastReVars(connection, newType);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
  }

  public static void copyLastReVars(final Connection connection, final DependencyGraphHistoryType newType) {
    lastPointInTime++;
    if (lastPointInTime == 1) {
      return;
    }
    try {
      final boolean autoCommit = connection.getAutoCommit();
      connection.setAutoCommit(false);
      final String tempTableName = REACTIVE_VARIABLES_TABLE_NAME + "_temp"; //$NON-NLS-1$

      final String tempTableQuery = "CREATE TEMPORARY TABLE " + tempTableName + " SELECT * FROM " + REACTIVE_VARIABLES_TABLE_NAME + " WHERE pointInTime = ?"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      try (final PreparedStatement tempTableStmt = connection.prepareStatement(tempTableQuery)) {
        tempTableStmt.setInt(1, lastPointInTime - 1);

        final String updateQuery = "UPDATE " + tempTableName + " SET pointInTime = ?, dependencyGraphHistoryType = ?, additionalInformation = ?, active = ?"; //$NON-NLS-1$ //$NON-NLS-2$
        try (final PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
          updateStmt.setInt(1, lastPointInTime);
          updateStmt.setInt(2, newType.ordinal());
          updateStmt.setString(3, null);
          updateStmt.setBoolean(4, false);

          final String insertQuery = "INSERT INTO " + REACTIVE_VARIABLES_TABLE_NAME + " SELECT NULL, id, reactiveVariableType, pointInTime, dependencyGraphHistoryType, additionalInformation, active, typeSimple, typeFull, name, additionalKeys, valueString, connectedWith FROM " + tempTableName; //$NON-NLS-1$ //$NON-NLS-2$
          try (Statement insertStmt = connection.createStatement()) {
            tempTableStmt.executeUpdate();
            updateStmt.executeUpdate();
            insertStmt.executeUpdate(insertQuery);
            connection.commit();
          }
        }
      }
      final String dropTempTableQuery = "DROP TEMPORARY TABLE IF EXISTS " + tempTableName; //$NON-NLS-1$
      try (final Statement stmt = connection.createStatement()) {
        stmt.executeUpdate(dropTempTableQuery);
        connection.commit();
      }
      connection.setAutoCommit(autoCommit);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
  }

  /**
   * Calls {@link #addReVar(Connection, ReactiveVariable)} with a new
   * connection.
   *
   * @param r
   *          the reactive variable to add
   * @return the row count
   */
  public int addReVar(final ReactiveVariable r) {
    try (final Connection connection = getConnection()) {
      return addReVar(connection, r);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
    return -1;
  }

  /**
   * Adds a reactive variable.
   *
   * @param connection
   *          an existing connection
   * @param r
   *          the reactive variable to add
   * @return the row count
   */
  public int addReVar(final Connection connection, final ReactiveVariable r) {
    final String addQuery = "INSERT INTO " + REACTIVE_VARIABLES_TABLE_NAME + " (`auto_increment_id`, `id`, `reactiveVariableType`, `pointInTime`, `dependencyGraphHistoryType`, `additionalInformation`, `active`, `typeSimple`, `typeFull`, `name`, `additionalKeys`, `valueString` , `connectedWith`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; //$NON-NLS-1$ //$NON-NLS-2$
    try (final PreparedStatement addStmt = connection.prepareStatement(addQuery)) {
      final Gson gson = new Gson();
      addStmt.setString(1, r.getId().toString());
      addStmt.setInt(2, r.getReactiveVariableType().ordinal());
      addStmt.setInt(3, r.getPointInTime());
      addStmt.setInt(4, r.getDependencyGraphHistoryType().ordinal());
      addStmt.setString(5, r.getAdditionalInformation());
      addStmt.setBoolean(6, r.isActive());
      addStmt.setString(7, r.getTypeSimple());
      addStmt.setString(8, r.getTypeFull());
      addStmt.setString(9, r.getName());
      addStmt.setString(10, gson.toJson(r.getAdditionalKeys()));
      addStmt.setString(11, r.getValueString());
      addStmt.setString(12, gson.toJson(r.getConnectedWith()));
      final int result = addStmt.executeUpdate();
      fireChangedEvent();
      return result;
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
    return -1;
  }

  public int deleteReVar(final UUID id, final int pointInTime) {
    try (Connection connection = getConnection()) {
      return deleteReVar(connection, id, pointInTime);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
    return -1;
  }

  public int deleteReVar(final Connection connection, final UUID id, final int pointInTime) {
    final String deleteQuery = "DELETE FROM " + REACTIVE_VARIABLES_TABLE_NAME + " WHERE id = ? AND pointInTime = ?"; //$NON-NLS-1$ //$NON-NLS-2$
    try (final PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
      deleteStmt.setString(1, id.toString());
      deleteStmt.setInt(2, pointInTime);
      final int result = deleteStmt.executeUpdate();
      fireChangedEvent();
      return result;
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
    return -1;
  }

  public static ArrayList<ReactiveVariable> getReVars(final int pointInTime) {
    try (Connection connection = getConnection()) {
      return getReVars(connection, pointInTime);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
    return null;
  }

  public static ArrayList<ReactiveVariable> getReVars(final Connection connection, final int pointInTime) {
    final String query = "SELECT * FROM " + REACTIVE_VARIABLES_TABLE_NAME + " WHERE pointInTime = ?"; //$NON-NLS-1$ //$NON-NLS-2$
    try (final PreparedStatement stmt = connection.prepareStatement(query)) {
      stmt.setInt(1, pointInTime);
      try (final ResultSet rs = stmt.executeQuery()) {
        final ArrayList<ReactiveVariable> reVars = new ArrayList<>();
        final Gson gson = new Gson();
        while (rs.next()) {
          final ReactiveVariable r = new ReactiveVariable();
          r.setId(UUID.fromString(rs.getString("id"))); //$NON-NLS-1$
          r.setReactiveVariableType(ReactiveVariableType.values()[rs.getInt("reactiveVariableType")]); //$NON-NLS-1$
          r.setPointInTime(rs.getInt("pointInTime")); //$NON-NLS-1$
          r.setDependencyGraphHistoryType(DependencyGraphHistoryType.values()[rs.getInt("dependencyGraphHistoryType")]); //$NON-NLS-1$
          r.setAdditionalInformation(rs.getString("additionalInformation")); //$NON-NLS-1$
          r.setActive(rs.getBoolean("active")); //$NON-NLS-1$
          r.setTypeSimple(rs.getString("typeSimple")); //$NON-NLS-1$
          r.setTypeFull(rs.getString("typeFull")); //$NON-NLS-1$
          r.setName(rs.getString("name")); //$NON-NLS-1$
          final String additionalKeysString = rs.getString("additionalKeys"); //$NON-NLS-1$
          Type type = TypeToken.get(new HashMap<String, Object>().getClass()).getType();
          final Map<String, Object> additionalKeys = gson.fromJson(additionalKeysString, type);
          r.setAdditionalKeys(additionalKeys);
          r.setValueString(rs.getString("valueString")); //$NON-NLS-1$
          final String connectedWithString = rs.getString("connectedWith"); //$NON-NLS-1$
          type = TypeToken.get(new ArrayList<String>().getClass()).getType();
          final ArrayList<String> connectedWith = gson.fromJson(connectedWithString, type);
          for (final String id : connectedWith) {
            r.setConnectedWith(UUID.fromString(id));
          }
          reVars.add(r);
        }
        return reVars;
      }
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
    return null;
  }

  public static boolean isNodeChildOf(final int pointInTime, final UUID childId, final UUID parentId) {
    final ArrayList<ReactiveVariable> reVarsList = getReVars(pointInTime);
    // convert list of reactive variables into map for better handling
    final HashMap<UUID, ReactiveVariable> reVars = new HashMap<>();
    for (final ReactiveVariable r : reVarsList) {
      reVars.put(r.getId(), r);
    }
    final ReactiveVariable child = reVars.get(childId);
    final ReactiveVariable parent = reVars.get(parentId);

    if (child == null || parent == null) {
      return false;
    }

    // parent is directly connected with child
    if (parent.isConnectedWith(childId)) {
      return true;
    }

    // recursively check children of parents
    final Set<UUID> potentialParents = parent.getConnectedWith();
    boolean result = false;
    for (final UUID potentialParentId : potentialParents) {
      result = result || isNodeChildOf(pointInTime, childId, potentialParentId);
    }
    return result;
  }

  public static boolean isNodeConnectionActive(final int pointInTime, final UUID srcId, final UUID destId) {
    try (Connection connection = getConnection()) {
      return isNodeConnectionActive(connection, pointInTime, srcId, destId);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
    return false;
  }

  public static boolean isNodeConnectionActive(final Connection connection, final int pointInTime, final UUID srcId, final UUID destId) {
    boolean result = false;
    final String sql = "SELECT `dependencyGraphHistoryType`, `additionalInformation` FROM " + REACTIVE_VARIABLES_TABLE_NAME + " WHERE pointInTime = ?"; //$NON-NLS-1$ //$NON-NLS-2$
    try (final PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, pointInTime);
      try (final ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          final DependencyGraphHistoryType type = DependencyGraphHistoryType.values()[rs.getInt("dependencyGraphHistoryType")]; //$NON-NLS-1$
          final String additionalInformation = rs.getString("additionalInformation"); //$NON-NLS-1$
          if (type != DependencyGraphHistoryType.NODE_ATTACHED || additionalInformation == null || additionalInformation.equals("")) { //$NON-NLS-1$
            continue;
          }
          final String[] ids = additionalInformation.split("->"); //$NON-NLS-1$
          final UUID id1 = UUID.fromString(ids[0]);
          final UUID id2 = UUID.fromString(ids[1]);
          if (id1.equals(srcId) && id2.equals(destId)) {
            result = true;
            break;
          }
        }
      }
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
    return result;
  }

  public static DependencyGraphHistoryType getDependencyGraphHistoryType(final int pointInTime) {
    try (Connection connection = getConnection()) {
      return getDependencyGraphHistoryType(connection, pointInTime);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
    return null;
  }

  public static DependencyGraphHistoryType getDependencyGraphHistoryType(final Connection connection, final int pointInTime) {
    final String sql = "SELECT `dependencyGraphHistoryType` FROM " + REACTIVE_VARIABLES_TABLE_NAME + " WHERE pointInTime = ? LIMIT 1"; //$NON-NLS-1$ //$NON-NLS-2$
    try (final PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, pointInTime);
      try (final ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          return DependencyGraphHistoryType.values()[rs.getInt("dependencyGraphHistoryType")]; //$NON-NLS-1$
        }
      }
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
    return null;
  }

  public static UUID getIdFromName(final String name) {
    try (Connection connection = getConnection()) {
      return getIdFromName(connection, name);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
    return null;
  }

  public static UUID getIdFromName(final Connection connection, final String name) {
    final String sql = "SELECT `id` FROM " + REACTIVE_VARIABLES_TABLE_NAME + " WHERE name = ? AND pointInTime = ?"; //$NON-NLS-1$ //$NON-NLS-2$
    try (final PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, name);
      stmt.setInt(2, getLastPointInTime());
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
}
