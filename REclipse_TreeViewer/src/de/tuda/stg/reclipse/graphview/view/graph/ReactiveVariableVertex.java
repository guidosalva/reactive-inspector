package de.tuda.stg.reclipse.graphview.view.graph;

import de.tuda.stg.reclipse.graphview.model.persistence.DependencyGraph.Vertex;
import de.tuda.stg.reclipse.logger.BreakpointInformation;
import de.tuda.stg.reclipse.logger.ReactiveVariable;

import com.mxgraph.view.mxGraph;

/**
 *
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 *
 */
public class ReactiveVariableVertex implements Comparable<ReactiveVariableVertex> {

  private final Vertex vertex;
  private final ReactiveVariable var;
  private final BreakpointInformation breakpointInformation;

  private boolean highlighted;

  private String customStyle;

  public ReactiveVariableVertex(final Vertex v, final BreakpointInformation br) {
    this.vertex = v;
    this.breakpointInformation = br;
    this.var = v.getVariable();
    this.highlighted = false;
    this.customStyle = null;
  }

  public ReactiveVariableVertex(final Vertex v, final BreakpointInformation br, final String cs) {
    this(v, br);

    this.customStyle = cs;
  }

  public ReactiveVariableVertex(final Vertex v, final BreakpointInformation br, final boolean h) {
    this(v, br);

    this.highlighted = h;
  }

  @Override
  public int compareTo(final ReactiveVariableVertex other) {
    return Integer.compare(vertex.getCreated(), other.vertex.getCreated());
  }

  /**
   * Inserts the vertex into the graph.
   *
   * @param graph
   *          A graph.
   * @return The cell representing the vertex in the graph.
   */
  public Object draw(final mxGraph graph, final boolean showClassName) {
    // use default parent of graph
    final Object parent = graph.getDefaultParent();

    // create label
    final ReactiveVariableLabel label = new ReactiveVariableLabel(var, breakpointInformation, showClassName);
    label.getStyleProperties().setValueChanged(highlighted);

    // set style
    final String style = (customStyle != null) ? customStyle : Stylesheet.getStyle(label);

    // insert vertex and return it
    return graph.insertVertex(parent, var.getId().toString(), label, 0, 0, 160, 80, style);
  }

  /**
   *
   * @return A reactive variable.
   */
  public ReactiveVariable getVar() {
    return var;
  }

  public BreakpointInformation getBreakpointInformation() {
    return breakpointInformation;
  }
}
