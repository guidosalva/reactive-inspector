package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import com.mxgraph.view.mxGraph;

public class ReactiveVariableVertex {

  protected ReactiveVariable var;

  public ReactiveVariableVertex(final ReactiveVariable v) {
    var = v;
  }

  public String getLabel() {
    return var.getName() + "\n\n" + "Value: " + var.getValueString() + "\n" + "Type: " + var.getTypeSimple();
  }

  public String getStyle() {
    switch (var.getReactiveVariableType()) {
      case VAR:
        return "VAR";
      case SIGNAL:
        return "SIGNAL";
      case EVENT:
        return "EVENT";
      case EVENT_HANDLER:
        return "EVENT_HANDLER";
      default:
        return "";
    }
  }

  public Object draw(final mxGraph graph) {
    // use default parent of graph
    final Object parent = graph.getDefaultParent();

    // insert vertex and return it
    return graph.insertVertex(parent, var.getId().toString(), getLabel(), 0, 0, 160, 80, getStyle());
  }

  public ReactiveVariable getVar() {
    return var;
  }
}
