package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.provider.ContentModel;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

/**
 * 
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 * 
 */
public class CustomGraph extends mxGraph {

  protected mxGraphComponent graphComponent;

  protected ContentModel contentModel;

  private mxGraphLayout graphLayout;

  final GraphCollapser collapser;

  final GraphHighlighter highlighter;

  private boolean activeHeatmap = false;

  public CustomGraph(final Composite parent) {
    super();

    // set custom stylesheet
    setStylesheet(new CustomGraphStylesheet());

    // initialize graph collapser and highlighter
    collapser = new GraphCollapser(this);
    highlighter = new GraphHighlighter(this);

    // create new content model
    contentModel = new ContentModel();

    // set graph layout
    graphLayout = new mxHierarchicalLayout(this, SwingConstants.WEST);

    // create child composite
    final Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.BACKGROUND);

    // set layout data to fill parent composite
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    // create frame inside composite
    final Frame graphFrame = SWT_AWT.new_Frame(composite);

    // initialize graph component and add it to frame
    graphComponent = new mxGraphComponent(this);
    graphComponent.setEnabled(false);

    graphFrame.add(graphComponent);

    // add listeners
    addMouseListener();

    updateGraph();
  }

  /**
   * Layouts the graph.
   */
  public void layoutGraph() {
    getModel().beginUpdate();
    try {
      // execute layout
      graphLayout.execute(getDefaultParent());

      // center cells
      moveCells(getChildCells(getDefaultParent(), true, true), 50, 50);
    }
    finally {
      getModel().endUpdate();
    }
  }

  public void setGraphLayout(final mxGraphLayout l) {
    graphLayout = l;
  }

  /**
   * Updates the point in time the graph should visualize.
   * 
   * @param pointInTime
   *          A point in time.
   */
  public void setPointInTime(final int pointInTime, final boolean highlightChange) {
    // set point in time in content model
    contentModel.setPointInTime(pointInTime, highlightChange);

    updateGraph();
  }

  public void setPointInTime(final int pointInTime) {
    setPointInTime(pointInTime, false);
  }

  /**
   * Redraws the graph by loading the necessary vertices and connecting them
   * appropriately.
   */
  public void updateGraph() {
    // remove cells, if any
    removeCells(getChildVertices(getDefaultParent()));

    // load vertices from content model
    List<ReactiveVariableVertex> vertices;

    if (activeHeatmap) {
      vertices = contentModel.getHeatmapVertices();
    }
    else {
      vertices = contentModel.getVertices();
    }

    // load edges from content model
    final Map<UUID, Set<UUID>> edges = contentModel.getEdges();

    // insert vertices
    final Map<UUID, Object> mapping = new HashMap<>();
    for (final ReactiveVariableVertex vertex : vertices) {
      final Object cell = vertex.draw(this);

      // add cell to mapping
      mapping.put(vertex.getVar().getId(), cell);
    }

    // insert edges
    for (final Object sourceId : edges.keySet()) {
      // get source vertex
      final Object source = mapping.get(sourceId);

      // get destinations
      final Set<UUID> destinations = edges.get(sourceId);

      for (final UUID destinationId : destinations) {
        // get destination vertex
        final Object destination = mapping.get(destinationId);

        insertEdge(defaultParent, null, "", source, destination, "EDGE"); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }

    layoutGraph();
  }

  private void addMouseListener() {
    graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(final MouseEvent e) {
      }

      @Override
      public void mouseReleased(final MouseEvent e) {
        if (!SwingUtilities.isRightMouseButton(e)) {
          return;
        }

        // find clicked cell
        final mxCell cell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());

        // if no cell has been clicked, return
        if (cell == null) {
          return;
        }

        final JPopupMenu popupMenu = new JPopupMenu();

        // add menu items
        popupMenu.add(collapser.createMenuItem(cell));
        popupMenu.addSeparator();
        popupMenu.add(highlighter.createMenuItem(cell));

        // show popup menu on clicked point
        popupMenu.show(graphComponent, e.getX(), e.getY());
      }
    });
  }

  public Set<Object> getChildrenOfCell(final mxCell cell) {
    // collect children of cell
    final Set<Object> children = new HashSet<>();
    traverse(cell, true, new mxICellVisitor() {

      @Override
      public boolean visit(final Object vertex, final Object edge) {
        if (vertex != cell) {
          children.add(vertex);
        }
        return vertex == cell || !isCellCollapsed(vertex);
      }
    });

    return children;
  }

  /**
   * Zooms into the graph.
   */
  public void zoomIn() {
    graphComponent.zoomIn();
  }

  /**
   * Zooms out of the graph.
   */
  public void zoomOut() {
    graphComponent.zoomOut();
  }

  public boolean isHeatmapEnabled() {
    return activeHeatmap;
  }

  public void setHeatmapEnabled(final boolean activeHeatmap) {
    this.activeHeatmap = activeHeatmap;
  }
}
