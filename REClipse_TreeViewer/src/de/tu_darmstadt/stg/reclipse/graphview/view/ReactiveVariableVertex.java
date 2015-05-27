package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.model.persistence.DependencyGraph.Vertex;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.Stylesheet;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import com.mxgraph.view.mxGraph;

/**
 *
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 *
 */
public class ReactiveVariableVertex implements Comparable<ReactiveVariableVertex> {

  private final Vertex vertex;
  private final ReactiveVariable var;

  private boolean isHighlighted;

  private String customStyle;

  public ReactiveVariableVertex(final Vertex v) {
    this.vertex = v;
    this.var = v.getVariable();
    this.isHighlighted = false;
    this.customStyle = null;
  }

  public ReactiveVariableVertex(final Vertex v, final String cs) {
    this(v);

    this.customStyle = cs;
  }

  public ReactiveVariableVertex(final Vertex v, final boolean h) {
    this(v);

    this.isHighlighted = h;
  }

  @Override
  public int compareTo(final ReactiveVariableVertex other) {
    return Integer.compare(vertex.getCreated(), other.vertex.getCreated());
  }

  /**
   *
   * @return The style to be used when displaying this vertex in the graph.
   */
  public String getStyle() {
    Stylesheet.Styles style;

    // determine style
    switch (var.getReactiveVariableType()) {
      case SIGNAL:
        style = Stylesheet.Styles.SIGNAL;
        break;
      case EVENT:
        style = Stylesheet.Styles.EVENT;
        break;
      case EVENT_HANDLER:
        style = Stylesheet.Styles.EVENTHANDLER;
        break;
      default:
        style = Stylesheet.Styles.VAR;
    }

    // set highlight, if enabled
    if (isHighlighted) {
      style = Stylesheet.Styles.getHighlight(style);
    }

    return style.name();
  }

  /**
   * Inserts the vertex into the graph.
   *
   * @param graph
   *          A graph.
   * @return The cell representing the vertex in the graph.
   */
  public Object draw(final mxGraph graph) {
    // use default parent of graph
    final Object parent = graph.getDefaultParent();

    // create label
    final ReactiveVariableLabel label = new ReactiveVariableLabel(var);

    // set style
    final String style = (customStyle != null) ? customStyle : getStyle();

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
}
