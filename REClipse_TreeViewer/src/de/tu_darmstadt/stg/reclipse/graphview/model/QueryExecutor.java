package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;

/**
 * Executes the user-defined query via Esper and returns the result.
 */
public class QueryExecutor {

  private static EPServiceProvider provider = prepareProvider();

  private static EPServiceProvider prepareProvider() {
    final Configuration engineConfig = new Configuration();
    final URL url = FileLocator.find(Platform.getBundle(Activator.PLUGIN_ID), new Path("etc/esper.cfg.xml"), null); //$NON-NLS-1$
    engineConfig.configure(url);
    return EPServiceProviderManager.getDefaultProvider(engineConfig);
  }

  public static ArrayList<Integer> executeQuery(final String conditions) {
    final String mySqlQuery = "SELECT pointInTime FROM " + DatabaseHelper.REACTIVE_VARIABLES_TABLE_NAME + " WHERE " + conditions; //$NON-NLS-1$ //$NON-NLS-2$
    final String esperQuery = "select pointInTime from sql:reclipseDBRead [\"" + mySqlQuery + "\"]"; //$NON-NLS-1$ //$NON-NLS-2$
    final EPStatement stmt = provider.getEPAdministrator().createEPL(esperQuery);
    final Iterator<EventBean> iter = stmt.iterator();
    final ArrayList<Integer> result = new ArrayList<>();
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
}
