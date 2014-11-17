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

  public ReactiveVariableVertex(final ReactiveVariable v) {
    var = v;
  }

  /**
   * 
   * @return The style to be used when displaying this vertex in the graph.
   */
  public String getStyle() {
    switch (var.getReactiveVariableType()) {
      case VAR:
        return CustomGraphStylesheet.Styles.VAR.name();
      case SIGNAL:
        return CustomGraphStylesheet.Styles.SIGNAL.name();
      case EVENT:
        return CustomGraphStylesheet.Styles.EVENT.name();
      case EVENT_HANDLER:
        return CustomGraphStylesheet.Styles.EVENTHANDLER.name();
      default:
        return ""; //$NON-NLS-1$
    }
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

    // insert vertex and return it
    return graph.insertVertex(parent, var.getId().toString(), label, 0, 0, 160, 80, getStyle());
  }

  /**
   * 
   * @return A reactive variable.
   */
  public ReactiveVariable getVar() {
    return var;
  }
}
