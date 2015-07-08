package de.tu_darmstadt.stg.reclipse.graphview.view.graph;

import de.tu_darmstadt.stg.reclipse.graphview.model.SessionContext;
import de.tu_darmstadt.stg.reclipse.graphview.provider.ContentModel;
import de.tu_darmstadt.stg.reclipse.graphview.view.ReactiveVariableLabel;
import de.tu_darmstadt.stg.reclipse.graphview.view.ReactiveVariableTooltip;
import de.tu_darmstadt.stg.reclipse.graphview.view.ReactiveVariableVertex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.swing.SwingConstants;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;

public class TreeViewGraph extends mxGraph {

  private Optional<SessionContext> ctx = Optional.empty();
  private Optional<ContentModel> contentModel = Optional.empty();
  private boolean activeHeatmap = false;

  private final mxGraphLayout graphLayout;

  public TreeViewGraph() {
    super();

    // enable html labels
    setHtmlLabels(true);

    // set custom stylesheet
    setStylesheet(new Stylesheet());

    this.graphLayout = new mxHierarchicalLayout(this, SwingConstants.WEST);
  }

  public void setSessionContext(final SessionContext ctx) {
    this.ctx = Optional.of(ctx);
    this.contentModel = Optional.of(new ContentModel(ctx));
    this.setModel(new mxGraphModel());
  }

  public void removeSessionContext() {
    this.ctx = Optional.empty();
    this.contentModel = Optional.empty();
    this.setModel(new mxGraphModel());
  }

  public void setPointInTime(final int pointInTime, final boolean highlightChange) {
    if (!contentModel.isPresent()) {
      return;
    }

    // set point in time in content model
    contentModel.get().setPointInTime(pointInTime, highlightChange);

    updateGraph();
  }

  /**
   * Redraws the graph by loading the necessary vertices and connecting them
   * appropriately.
   */
  public void updateGraph() {
    if (!contentModel.isPresent()) {
      return;
    }

    getModel().beginUpdate();
    try {
      doUpdateGraph();
    }
    finally {
      getModel().endUpdate();
    }
  }

  private void doUpdateGraph() {
    // remove cells, if any
    removeCells(getChildVertices(getDefaultParent()));

    // load vertices from content model
    List<ReactiveVariableVertex> vertices;

    if (activeHeatmap) {
      vertices = contentModel.get().getHeatmapVertices();
    }
    else {
      vertices = contentModel.get().getVertices();
    }

    // load edges from content model
    final Map<UUID, Set<UUID>> edges = contentModel.get().getEdges();

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

    doLayoutGraph();
  }

  /**
   * Layouts the graph.
   */
  public void layoutGraph() {
    getModel().beginUpdate();
    try {
      doLayoutGraph();
    }
    finally {
      getModel().endUpdate();
    }
  }

  private void doLayoutGraph() {
    // execute layout
    graphLayout.execute(getDefaultParent());

    // center cells
    moveCells(getChildCells(getDefaultParent(), true, true), 50, 50);
  }

  /**
   * Returns all children of a cell.
   *
   * @param cell
   *          A cell in the graph.
   * @return A set of mxCell objects.
   */
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
   * Enables or disables the heatmap mode.
   *
   * @param heatmapMode
   *          The heatmap mode.
   */
  public void setHeatmapEnabled(final boolean heatmapMode) {
    this.activeHeatmap = heatmapMode;
  }

  public boolean isHeatmapEnabled() {
    return activeHeatmap;
  }

  @Override
  public String getToolTipForCell(final Object arg) {
    final mxCell cell = (mxCell) arg;

    final ReactiveVariableLabel reVarLabel = (ReactiveVariableLabel) cell.getValue();
    final ReactiveVariableTooltip tooltip = new ReactiveVariableTooltip(reVarLabel.getVar());
    return tooltip.toString();
  }

  public Optional<SessionContext> getSessionContext() {
    return ctx;
  }
}
