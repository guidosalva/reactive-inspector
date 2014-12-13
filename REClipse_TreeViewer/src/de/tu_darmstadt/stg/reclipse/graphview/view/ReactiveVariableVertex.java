package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import com.mxgraph.view.mxGraph;

/**
 * 
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 * 
 */
public class ReactiveVariableVertex {

  protected ReactiveVariable var;

  private boolean isHighlighted;

  private String customStyle;

  public ReactiveVariableVertex(final ReactiveVariable v) {
    this.var = v;
    this.isHighlighted = false;
    this.customStyle = null;
  }

  public ReactiveVariableVertex(final ReactiveVariable v, final String cs) {
    this(v);

    this.customStyle = cs;
  }

  public ReactiveVariableVertex(final ReactiveVariable v, final boolean h) {
    this(v);

    this.isHighlighted = h;
  }

  /**
   * 
   * @return The style to be used when displaying this vertex in the graph.
   */
  public String getStyle() {
    CustomGraphStylesheet.Styles style;

    // determine style
    switch (var.getReactiveVariableType()) {
      case SIGNAL:
        style = CustomGraphStylesheet.Styles.SIGNAL;
        break;
      case EVENT:
        style = CustomGraphStylesheet.Styles.EVENT;
        break;
      case EVENT_HANDLER:
        style = CustomGraphStylesheet.Styles.EVENTHANDLER;
        break;
      default:
        style = CustomGraphStylesheet.Styles.VAR;
    }

    // set highlight, if enabled
    if (isHighlighted) {
      style = CustomGraphStylesheet.Styles.getHighlight(style);
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
