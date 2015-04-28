package de.tu_darmstadt.stg.reclipse.graphview.provider;

import de.tu_darmstadt.stg.reclipse.graphview.model.SessionContext;
import de.tu_darmstadt.stg.reclipse.graphview.model.persistence.DatabaseHelper;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphEntityRelationshipContentProvider;

/**
 * Provides the nodes and relationships for the graph by reading them from the
 * database via the {@link DatabaseHelper}.
 */
public class ContentProvider implements IGraphEntityRelationshipContentProvider {

  private final SessionContext ctx;

  private int lastPointInTime;

  public ContentProvider(final SessionContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public Object[] getElements(final Object inputElement) {
    // sort the objects, so we get the same order each time
    final SortedSet<ReactiveVariable> elements = new TreeSet<>();

    // input element is the point in time at which the user wants to see the
    // dependency graph
    if (inputElement instanceof Integer) {
      lastPointInTime = (Integer) inputElement;
      // make sure that point in time is in a valid range
      if (lastPointInTime < 1 || lastPointInTime > ctx.getDbHelper().getLastPointInTime()) {
        return elements.toArray();
      }
      final ArrayList<ReactiveVariable> reVars = ctx.getDbHelper().getReVars(lastPointInTime);

      for (final ReactiveVariable reVar : reVars) {
        // not all reactive variables are created yet, so show nothing for the
        // time being
        if (reVar == null) {
          return new TreeSet<>().toArray();
        }
        elements.add(reVar);
      }
    }

    return elements.toArray();
  }

  @Override
  public Object[] getRelationships(final Object src, final Object dest) {
    final ArrayList<Dependency> l = new ArrayList<>();
    if (src instanceof ReactiveVariable && dest instanceof ReactiveVariable) {
      final ReactiveVariable source = (ReactiveVariable) src;
      final ReactiveVariable destination = (ReactiveVariable) dest;
      final boolean connected = source.isConnectedWith(destination.getId());
      if (connected) {
        l.add(new Dependency(source, destination));
      }
    }
    return l.toArray();
  }

  @Override
  public void dispose() {
    // do nothing.
  }

  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // do nothing.
  }

  public int getLastPointInTime() {
    return lastPointInTime;
  }
}
