package de.tuda.stg.reclipse.graphview.view.graph;

import de.tuda.stg.reclipse.graphview.Properties;
import de.tuda.stg.reclipse.graphview.model.SessionContext;
import de.tuda.stg.reclipse.graphview.provider.ContentModel;
import de.tuda.stg.reclipse.graphview.util.ViewMode;
import de.tuda.stg.reclipse.logger.BreakpointInformation;
import de.tuda.stg.reclipse.logger.ReactiveVariable;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingConstants;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

public class TreeViewGraph extends mxGraph {

  private Optional<SessionContext> ctx = Optional.empty();
  private Optional<ContentModel> contentModel = Optional.empty();
  private ViewMode viewMode = ViewMode.DEFAULT;
  private boolean showClassName = true;

  private final mxGraphLayout graphLayout;

  private final List<IGraphListener> graphListeners = new CopyOnWriteArrayList<>();

  public TreeViewGraph() {
    super();

    // enable html labels
    setHtmlLabels(true);

    // set custom stylesheet
    setStylesheet(new Stylesheet());

    this.graphLayout = new mxHierarchicalLayout(this, SwingConstants.WEST);

    this.showClassName = Properties.getBoolean(Properties.SHOW_CLASS_NAME);
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

    switch (viewMode) {
      case DEFAULT:
        vertices = contentModel.get().getVertices();
        break;

      case RELATIVE:
        vertices = contentModel.get().getHeatmapVertices();
        break;

      case ABSOLUTE_LATEST:
        vertices = contentModel.get().getAbsolutePerformanceLatestVertices();
        break;

      case ABSOLUTE_SUM:
        vertices = contentModel.get().getAbsolutePerformanceSumVertices();
        break;

      default:
        vertices = contentModel.get().getVertices();
    }

    // load edges from content model
    final Map<UUID, Set<UUID>> edges = contentModel.get().getEdges();

    // insert vertices
    final Map<UUID, Object> mapping = new HashMap<>();
    for (final ReactiveVariableVertex vertex : vertices) {
      final Object cell = vertex.draw(this, showClassName);

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

    fireGraphChanged();
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
   * Returns all ancestors of a cell.
   *
   * @param cell
   *          A cell in the graph.
   * @return A set of mxCell objects.
   */
  public Set<Object> getAncestorsOfCell(final mxCell cell) {
    final Set<Object> ancestors = new HashSet<>();

    final Deque<mxICell> queue = new LinkedList<>();
    queue.addAll(getParents(cell));

    while (!queue.isEmpty()) {
      final mxICell next = queue.poll();
      ancestors.add(next);

      final Set<mxICell> parents = getParents(next);

      for (final mxICell parent : parents) {
        if (!ancestors.contains(parent) && !queue.contains(parent)) {
          queue.add(parent);
        }
      }
    }

    return ancestors;
  }

  private Set<mxICell> getParents(final mxICell cell) {
    final Set<mxICell> parents = new HashSet<>();

    final Object[] edges = getIncomingEdges(cell);

    for (final Object edge : edges) {
      final mxICell source = ((mxCell) edge).getSource();
      parents.add(source);
    }

    return parents;
  }

  public Set<mxCell> searchNodes(final String query) {
    final Set<mxCell> nodes = new HashSet<>();

    final Object[] vertices = getChildVertices(getDefaultParent());

    for (final Object vertex : vertices) {
      final mxCell cell = (mxCell) vertex;
      final ReactiveVariableLabel reVarLabel = (ReactiveVariableLabel) cell.getValue();
      final ReactiveVariable reVar = reVarLabel.getVar();

      final String prefix = query.trim().toLowerCase();

      if (reVar.getName() != null && reVar.getName().toLowerCase().startsWith(prefix)) {
        nodes.add(cell);
      }
    }

    return nodes;
  }

  public void resetSearchHighlighting() {
    final Object[] vertices = getChildVertices(getDefaultParent());

    for (final Object vertex : vertices) {
      final mxCell cell = (mxCell) vertex;
      final ReactiveVariableLabel label = (ReactiveVariableLabel) cell.getValue();

      label.getStyleProperties().setSearchResult(false);

      cell.setStyle(Stylesheet.getStyle(label));
    }
  }

  public void resetDependencyHighlighting() {
    final Object[] vertices = getChildVertices(getDefaultParent());

    for (final Object vertex : vertices) {
      final mxCell cell = (mxCell) vertex;
      final ReactiveVariableLabel label = (ReactiveVariableLabel) cell.getValue();

      label.getStyleProperties().setGrayedOut(false);

      cell.setStyle(Stylesheet.getStyle(label));
    }
  }

  public void highlightDependentNodes(final Set<Object> nodes) {
    final Object[] vertices = getChildVertices(getDefaultParent());

    for (final Object vertex : vertices) {
      final mxCell cell = (mxCell) vertex;
      final boolean highlight = nodes.contains(cell);
      final ReactiveVariableLabel label = (ReactiveVariableLabel) cell.getValue();

      label.getStyleProperties().setGrayedOut(!highlight);

      cell.setStyle(Stylesheet.getStyle(label));
    }
  }

  public void highlightSearchResults(final Set<? extends Object> nodes) {
    final Object[] vertices = getChildVertices(getDefaultParent());

    for (final Object vertex : vertices) {
      final mxCell cell = (mxCell) vertex;
      final boolean highlight = nodes.contains(cell);
      final ReactiveVariableLabel label = (ReactiveVariableLabel) cell.getValue();

      label.getStyleProperties().setSearchResult(highlight);

      cell.setStyle(Stylesheet.getStyle(label));
    }
  }

  public void addGraphListener(final IGraphListener listener) {
    graphListeners.add(listener);
  }

  public void removeGraphListener(final IGraphListener listener) {
    graphListeners.remove(listener);
  }

  private void fireGraphChanged() {
    for (final IGraphListener listener : graphListeners) {
      listener.onGraphChanged();
    }
  }

  /**
   * Enables or disables the heatmap mode.
   *
   * @param heatmapMode
   *          The heatmap mode.
   */
  public void setViewMode(final ViewMode viewMode) {
    this.viewMode = viewMode;
  }

  public ViewMode getViewMode() {
    return viewMode;
  }

  public boolean isShowClassName() {
    return showClassName;
  }

  public void setShowClassName(final boolean showClassName) {
    this.showClassName = showClassName;

    updateShowClassName();
    refresh();
  }

  private void updateShowClassName() {
    final Object[] vertices = getChildVertices(getDefaultParent());

    for (final Object vertex : vertices) {
      final mxCell cell = (mxCell) vertex;
      final ReactiveVariableLabel label = (ReactiveVariableLabel) cell.getValue();
      label.setShowClassName(showClassName);
    }
  }

  @Override
  public String getToolTipForCell(final Object arg) {
    final mxCell cell = (mxCell) arg;

    final ReactiveVariableLabel reVarLabel = (ReactiveVariableLabel) cell.getValue();
    final ReactiveVariable reVar = reVarLabel.getVar();
    final BreakpointInformation breakpointInformation = ctx.isPresent() ? ctx.get().getVariableLocation(reVar.getId()) : null;

    final ReactiveVariableTooltip tooltip = new ReactiveVariableTooltip(reVarLabel.getVar(), breakpointInformation);
    return tooltip.toString();
  }

  public Optional<SessionContext> getSessionContext() {
    return ctx;
  }

  public static class SearchResult {

    private final List<mxCell> results;
    private int index;

    protected SearchResult(final List<mxCell> results) {
      this.results = results;
      this.index = 0;
    }

    public int getResultCount() {
      return results.size();
    }

    public int getCurrentIndex() {
      return index;
    }

    public mxCell getCurrent() {
      return results.get(index);
    }

    public void nextResult() {
      if (results.isEmpty()) {
        return;
      }

      index = Math.floorMod(index + 1, results.size());
    }

    public void prevResult() {
      if (results.isEmpty()) {
        return;
      }

      index = Math.floorMod(index - 1, results.size());
    }
  }
}
