package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.graphview.provider.ContentProvider;
import de.tu_darmstadt.stg.reclipse.graphview.view.CustomGraphViewer;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filter which recursively hides nodes if they are currently collapsed.
 */
public class CollapsedNodesFilter extends ViewerFilter {

  private final Set<UUID> collapsedNodes = new HashSet<>();

  public boolean collapsedNode(final UUID id) {
    return collapsedNodes.add(id);
  }

  public boolean expandNode(final UUID id) {
    return collapsedNodes.remove(id);
  }

  public boolean toggleNode(final UUID id) {
    if (collapsedNodes.contains(id)) {
      return collapsedNodes.remove(id);
    }
    return collapsedNodes.add(id);
  }

  public boolean isNodeCollapsed(final UUID id) {
    return collapsedNodes.contains(id);
  }

  public void clear() {
    collapsedNodes.clear();
  }

  @Override
  public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
    if (viewer instanceof CustomGraphViewer && element instanceof ReactiveVariable) {
      final CustomGraphViewer cgv = (CustomGraphViewer) viewer;
      final IContentProvider provider = cgv.getContentProvider();
      if (provider instanceof ContentProvider) {
        final ContentProvider contentProvider = (ContentProvider) provider;
        final int lastPointInTime = contentProvider.getLastPointInTime();
        final ReactiveVariable child = (ReactiveVariable) element;
        for (final UUID collapsedNode : collapsedNodes) {
          if (DatabaseHelper.isNodeChildOf(lastPointInTime, child.getId(), collapsedNode)) {
            // current node is child of a collapsed node, so hide it
            return false;
          }
        }
      }
    }
    return true;
  }

}
