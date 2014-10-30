package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.action.ChooseLayoutAlgorithm;
import de.tu_darmstadt.stg.reclipse.graphview.model.CollapsedNodesFilter;
import de.tu_darmstadt.stg.reclipse.graphview.provider.ContentProvider;
import de.tu_darmstadt.stg.reclipse.graphview.provider.ReactiveFigureProvider;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.ZestStyles;

/**
 * A custom {@link GraphViewer} implementation in order to provide zoom
 * functionality and to add various listeners.
 */
public class CustomGraphViewer extends GraphViewer {

  /**
   * Filters the nodes when user collapses certain nodes.
   */
  protected final CollapsedNodesFilter collapsedNodesFilter = new CollapsedNodesFilter();

  public CustomGraphViewer(final Composite composite, final int style) {
    super(composite, style);
    addMouseWheelListener();
    addMouseListener();
    setContentProvider(new ContentProvider());
    setLabelProvider(new ReactiveFigureProvider(collapsedNodesFilter));
    setLayoutAlgorithm(ChooseLayoutAlgorithm.getInitialLayoutAlgorithm());
    setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED | ZestStyles.CONNECTIONS_SOLID);
    setNodeStyle(ZestStyles.NODES_NO_LAYOUT_ANIMATION | ZestStyles.NODES_NO_ANIMATION);
  }

  private void addMouseWheelListener() {
    getGraphControl().addMouseWheelListener(new MouseWheelListener() {

      @Override
      public void mouseScrolled(final MouseEvent e) {
        if ((e.stateMask & SWT.CTRL) != 0) {
          if (e.count > 0) {
            zoomIn();
          }
          else if (e.count < 0) {
            zoomOut();
          }
        }
      }
    });
  }

  private void addMouseListener() {
    getGraphControl().addMouseListener(new MouseAdapter() {

      @Override
      public void mouseDoubleClick(final MouseEvent e) {
        final Point p = new Point(e.x, e.y);
        getGraphControl().getRootLayer().translateToRelative(p);
        final IFigure fig = getGraphControl().getFigureAt(p.x, p.y);
        if (fig instanceof ReactiveVariableFigure) {
          final ReactiveVariableFigure reFig = (ReactiveVariableFigure) fig;
          final ReactiveVariable var = reFig.getVar();
          collapsedNodesFilter.toggleNode(var.getId());
          setFilters(new ViewerFilter[] {
            collapsedNodesFilter
          });
          applyLayout();
        }
      }
    });
  }

  @Override
  public void resetFilters() {
    collapsedNodesFilter.clear();
    super.resetFilters();
  }

  @SuppressWarnings("restriction")
  public void zoomIn() {
    getZoomManager().zoomIn();
  }

  @SuppressWarnings("restriction")
  public void zoomOut() {
    getZoomManager().zoomOut();
  }

  public CollapsedNodesFilter getCollapsedNodesFilter() {
    return collapsedNodesFilter;
  }
}
