package de.tu_darmstadt.stg.reclipse.graphview.view.graph;

/**
 * This is a container for graphs. It can be used to hold the currently selected
 * graph.
 *
 */
public class GraphContainer {

  private CustomGraph graph;

  public GraphContainer() {
  }

  public GraphContainer(final CustomGraph graph) {
    this.graph = graph;
  }

  public boolean containsGraph() {
    return graph != null;
  }

  public CustomGraph getGraph() {
    return graph;
  }

  public void setGraph(final CustomGraph graph) {
    this.graph = graph;
  }
}