package de.tuda.stg.reclipse.graphview.model.persistence;

import de.tuda.stg.reclipse.logger.ReactiveVariable;

import de.tuda.stg.reclipse.graphview.model.BreakpointQueryRegistry;
import de.tuda.stg.reclipse.graphview.model.IRegistryListener;
import de.tuda.stg.reclipse.graphview.model.querylanguage.ReclipseQuery;
import de.tuda.stg.reclipse.graphview.model.querylanguage.ReclipseVisitorEsperImpl;

import java.util.HashMap;
import java.util.Map;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

public class LiveEsperAdapter implements IRegistryListener {

  private final EPServiceProvider provider;
  private final Map<String, EPStatement> queries = new HashMap<>();

  private final UpdateSubscriber subscriber = new UpdateSubscriber();

  public LiveEsperAdapter(final String sessionId) {
    this.provider = createProvider(sessionId);

    for (final ReclipseQuery query : BreakpointQueryRegistry.getInstance().getQueries()) {
      addQuery(query);
    }

    BreakpointQueryRegistry.getInstance().addListener(this);
  }

  private EPServiceProvider createProvider(final String sessionId) {
    final Configuration engineConfig = new Configuration();
    engineConfig.addEventType("ReactiveVariable", ReactiveVariable.class); //$NON-NLS-1$
    return EPServiceProviderManager.getProvider(sessionId + "-live", engineConfig); //$NON-NLS-1$
  }

  public void addQuery(final ReclipseQuery reclipseQuery) {
    final String conditions = createConditions(reclipseQuery);

    final String query = "select pointInTime from ReactiveVariable where " + conditions; //$NON-NLS-1$

    final EPStatement stmt = provider.getEPAdministrator().createEPL(query);
    stmt.setSubscriber(subscriber);

    queries.put(reclipseQuery.getQueryText(), stmt);
  }

  private String createConditions(final ReclipseQuery reclipseQuery) {
    final ReclipseVisitorEsperImpl visitor = new ReclipseVisitorEsperImpl();
    return visitor.visit(reclipseQuery.getParseTree());
  }

  public void removeQuery(final String queryText) {
    final EPStatement stmt = queries.remove(queryText);

    if (stmt != null) {
      stmt.destroy();
    }
  }

  public synchronized boolean sendEvent(final ReactiveVariable r) {
    subscriber.reset();
    provider.getEPRuntime().sendEvent(r);
    return subscriber.hasEventOccurred();
  }

  public void close() {
    BreakpointQueryRegistry.getInstance().removeListener(this);
    provider.destroy();
  }

  @Override
  public void onQueryAdded(final ReclipseQuery query) {
    addQuery(query);
  }

  @Override
  public void onQueryRemoved(final ReclipseQuery query) {
    removeQuery(query.getQueryText());
  }

  protected static class UpdateSubscriber {

    private int pointInTime = -1;

    public void reset() {
      pointInTime = -1;
    }

    public boolean hasEventOccurred() {
      return pointInTime >= 0;
    }

    /**
     * Called by Esper when statement matches event.
     *
     * @param result
     *          the point in time of the matching reactive variable
     */
    public void update(final int result) {
      this.pointInTime = result;
    }
  }
}
