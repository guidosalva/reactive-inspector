package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseErrorListener;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseLexer;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseParser;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseVisitorEsperImpl;
import de.tu_darmstadt.stg.reclipse.graphview.view.ReactiveTreeView;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.net.URL;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.sqlite.SQLiteConfig;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.ConfigurationDBRef;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

/**
 * Class which handles all the stuff related to Esper. It receives
 * {@link ReactiveVariable} instances, sends them to Esper and applies the
 * user-defined query on them, so that the information whether the event matched
 * the query can be retrieved afterwards.
 */
public class EsperAdapter {

  private final SessionContext ctx;
  private EPServiceProvider provider;
  protected ReactiveTreeView rtv;
  protected String queryText;
  private EPStatement stmt;
  private int pointInTime = -1;

  public EsperAdapter(final SessionContext ctx) {
    this.ctx = ctx;
    setupEsper();
  }

  private void setupEsper() {
    final Configuration engineConfig = new Configuration();
    final URL url = FileLocator.find(Platform.getBundle(Activator.PLUGIN_ID), new Path("etc/esper.cfg.xml"), null); //$NON-NLS-1$
    engineConfig.configure(url);
    engineConfig.addDatabaseReference("reclipseDBRead", createEsperDBRef()); //$NON-NLS-1$
    engineConfig.addEventType("ReactiveVariable", ReactiveVariable.class); //$NON-NLS-1$
    provider = EPServiceProviderManager.getDefaultProvider(engineConfig);
    updateEPLStatement();
  }

  private ConfigurationDBRef createEsperDBRef() {
    final DatabaseHelper dbHelper = ctx.getDbHelper();
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

  protected ReactiveTreeView getReactiveTreeView() {
    if (rtv == null) {
      Display.getDefault().syncExec(new Runnable() {

        @Override
        public void run() {
          final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
          final IViewPart view = page.findView(ReactiveTreeView.ID);
          if (view instanceof ReactiveTreeView) {
            rtv = (ReactiveTreeView) view;
          }
        }
      });
    }
    return rtv;
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
      if (stmt != null) {
        stmt.destroy();
      }
      return;
    }

    final String conditions = parseReclipseQuery();
    final String query = "select pointInTime from ReactiveVariable where " + conditions; //$NON-NLS-1$
    // delete the old statement
    if (stmt != null) {
      stmt.destroy();
    }
    stmt = provider.getEPAdministrator().createEPL(query);
    stmt.setSubscriber(this);
  }

  private void updateQueryText() {
    Display.getDefault().syncExec(new Runnable() {

      @Override
      public void run() {
        if (getReactiveTreeView() != null) {
          queryText = getReactiveTreeView().getQueryText();
        }
      }
    });
  }

  private String parseReclipseQuery() {
    final ReclipseLexer lexer = new ReclipseLexer(new ANTLRInputStream(queryText));
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    final ReclipseParser parser = new ReclipseParser(tokens);
    // redirect parsing errors to the RTV
    parser.removeErrorListeners();
    parser.addErrorListener(new ReclipseErrorListener(getReactiveTreeView()));
    final ParseTree tree = parser.query();
    final ReclipseVisitorEsperImpl visitor = new ReclipseVisitorEsperImpl(ctx);
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
