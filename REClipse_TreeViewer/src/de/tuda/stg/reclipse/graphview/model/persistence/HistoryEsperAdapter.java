package de.tuda.stg.reclipse.graphview.model.persistence;

import de.tuda.stg.reclipse.logger.ReactiveVariable;

import de.tuda.stg.reclipse.graphview.model.querylanguage.ReclipseQuery;
import de.tuda.stg.reclipse.graphview.model.querylanguage.ReclipseVisitorSQLImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.sqlite.SQLiteConfig;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.ConfigurationDBRef;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;

/**
 * Class which handles all the stuff related to Esper. It receives
 * {@link ReactiveVariable} instances, sends them to Esper and applies the
 * user-defined query on them, so that the information whether the event matched
 * the query can be retrieved afterwards.
 */
public class HistoryEsperAdapter {

  private final DatabaseHelper dbHelper;
  private final EPServiceProvider provider;

  public HistoryEsperAdapter(final DatabaseHelper dbHelper) {
    this.dbHelper = dbHelper;
    this.provider = createProvider();
  }

  private EPServiceProvider createProvider() {
    final Configuration engineConfig = new Configuration();
    engineConfig.addDatabaseReference(getDatabaseReferenceName(), createEsperDBRef());
    return EPServiceProviderManager.getProvider(dbHelper.getSessionId(), engineConfig);
  }

  private String getDatabaseReferenceName() {
    return "reclipseDB" + dbHelper.getSessionId().replace("-", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  private ConfigurationDBRef createEsperDBRef() {
    final String className = dbHelper.getJdbcClassName();
    final String url = dbHelper.getJdbcUrl();
    final String user = dbHelper.getJdbcUser();
    final String password = dbHelper.getJdbcPassword();

    final ConfigurationDBRef dbRef = new ConfigurationDBRef();

    final SQLiteConfig sqLiteConfig = new SQLiteConfig();
    sqLiteConfig.setReadOnly(true);

    dbRef.setDriverManagerConnection(className, url, user, password, sqLiteConfig.toProperties());
    dbRef.setConnectionAutoCommit(true);
    dbRef.setConnectionReadOnly(true);
    dbRef.setConnectionCatalog(""); //$NON-NLS-1$

    return dbRef;
  }

  /**
   * Executes a query against the current history and returns all matching
   * points in time.
   *
   * @param reclipseQuery
   *          the query
   * @return matching points in time
   */
  public List<Integer> executeQuery(final ReclipseQuery reclipseQuery) {
    final String sqlQuery = createSqlQuery(reclipseQuery);

    final String dbRef = getDatabaseReferenceName();
    final String esperQuery = "select pointInTime from sql:" + dbRef + " [\"" + sqlQuery + "\"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    final EPStatement stmt = provider.getEPAdministrator().createEPL(esperQuery);

    final Iterator<EventBean> iter = stmt.iterator();
    final List<Integer> result = new ArrayList<>();

    while (iter.hasNext()) {
      final EventBean evb = iter.next();
      final Object underlying = evb.getUnderlying();
      if (underlying instanceof HashMap<?, ?>) {
        for (final Object i : ((HashMap<?, ?>) underlying).values()) {
          result.add((int) i);
        }
      }
    }

    return result;
  }

  private String createSqlQuery(final ReclipseQuery reclipseQuery) {
    final ReclipseVisitorSQLImpl visitor = new ReclipseVisitorSQLImpl();
    return visitor.visit(reclipseQuery.getParseTree());
  }
}
