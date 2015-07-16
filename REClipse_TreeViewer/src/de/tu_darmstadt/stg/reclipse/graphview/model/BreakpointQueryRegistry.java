package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseQuery;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BreakpointQueryRegistry {

  private static final BreakpointQueryRegistry INSTANCE = new BreakpointQueryRegistry();

  private final List<ReclipseQuery> queries = new CopyOnWriteArrayList<>();
  private final List<IRegistryListener> listeners = new CopyOnWriteArrayList<>();

  private BreakpointQueryRegistry() {
  }

  public static BreakpointQueryRegistry getInstance() {
    return INSTANCE;
  }

  public void addQuery(final ReclipseQuery query) {
    queries.add(query);
    fireQueryAdded(query);
  }

  public void removeQuery(final ReclipseQuery query) {
    queries.remove(query);
    fireQueryRemoved(query);
  }

  public void removeAllQueries() {
    for (final ReclipseQuery query : queries) {
      removeQuery(query);
    }
  }

  public void addListener(final IRegistryListener listener) {
    listeners.add(listener);
  }

  public void removeListener(final IRegistryListener listener) {
    listeners.remove(listener);
  }

  private void fireQueryAdded(final ReclipseQuery query) {
    for (final IRegistryListener listener : listeners) {
      listener.onQueryAdded(query);
    }
  }

  private void fireQueryRemoved(final ReclipseQuery query) {
    for (final IRegistryListener listener : listeners) {
      listener.onQueryRemoved(query);
    }
  }

  public List<ReclipseQuery> getQueries() {
    return Collections.unmodifiableList(queries);
  }
}
