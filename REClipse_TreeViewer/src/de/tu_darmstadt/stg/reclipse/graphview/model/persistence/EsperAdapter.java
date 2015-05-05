package de.tu_darmstadt.stg.reclipse.graphview.model.persistence;

import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseLexer;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseParser;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseVisitorEsperImpl;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
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
public class EsperAdapter {

  private final DatabaseHelper dbHelper;
  private EPServiceProvider provider;
  protected String queryText;
  private EPStatement liveStmt;
  private int pointInTime = -1;

  public EsperAdapter(final DatabaseHelper dbHelper) {
    this.dbHelper = dbHelper;
    setupEsper();
  }

  private void setupEsper() {
    final Configuration engineConfig = new Configuration();
    engineConfig.addDatabaseReference("reclipseDBRead", createEsperDBRef()); //$NON-NLS-1$
    engineConfig.addEventType("ReactiveVariable", ReactiveVariable.class); //$NON-NLS-1$
    provider = EPServiceProviderManager.getDefaultProvider(engineConfig);
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

  private void updateEPLStatement() {
    final String oldQueryText = queryText;
    updateQueryText();
    // if query text has not been changed or not existing, do nothing
    if (queryText == null || queryText.equals(oldQueryText)) {
      return;
    }
    // if query text has been deleted, delete statement
    if (queryText.equals("")) { //$NON-NLS-1$
      if (liveStmt != null) {
        liveStmt.destroy();
      }
      return;
    }

    final String conditions = parseReclipseQuery();
    final String query = "select pointInTime from ReactiveVariable where " + conditions; //$NON-NLS-1$
    // delete the old statement
    if (liveStmt != null) {
      liveStmt.destroy();
    }
    liveStmt = provider.getEPAdministrator().createEPL(query);
    liveStmt.setSubscriber(this);
  }

  private void updateQueryText() {
    // TODO update query text via method parameter
  }

  private String parseReclipseQuery() {
    final ReclipseLexer lexer = new ReclipseLexer(new ANTLRInputStream(queryText));
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    final ReclipseParser parser = new ReclipseParser(tokens);

    // redirect parsing errors to the RTV
    parser.removeErrorListeners();
    // parser.addErrorListener(new
    // ReclipseErrorListener(getReactiveTreeView()));
    // TODO add listener for ui events

    final ParseTree tree = parser.query();
    final ReclipseVisitorEsperImpl visitor = new ReclipseVisitorEsperImpl(dbHelper);
    return visitor.visit(tree);
  }

  /**
   * Send the given event to the Esper engine.
   *
   * @param r
   *          the event in form of a reactive variable
   */
  public void sendEvent(final ReactiveVariable r) {
    // reset the pointInTime field, because update method is only called if
    // there IS a result
    pointInTime = -1;
    updateEPLStatement();
    provider.getEPRuntime().sendEvent(r);
  }

  /**
   * Executes a query against the current history and returns all matching
   * points in time.
   *
   * @param conditions
   *          the query conditions
   * @return matching points in time
   */
  public List<Integer> executeQuery(final String conditions) {
    final String mySqlQuery = ""; // TODO create query for new schema
    final String esperQuery = "select pointInTime from sql:reclipseDBRead [\"" + mySqlQuery + "\"]"; //$NON-NLS-1$ //$NON-NLS-2$
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

  /**
   * Called by Esper when statement matches event.
   *
   * @param pointInTime
   *          the point in time of the matching reactive variable
   */
  public void update(final int thePointInTime) {
    pointInTime = thePointInTime;
  }

  /**
   * @return the point in time of the matching reactive variable or -1 if there
   *         is no match
   */
  public int getPointInTime() {
    return pointInTime;
  }

}
