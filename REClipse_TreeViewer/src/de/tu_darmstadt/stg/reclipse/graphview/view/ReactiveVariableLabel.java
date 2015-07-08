package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariableType;

public class ReactiveVariableLabel {

  private final ReactiveVariable var;

  private boolean isHighlighted;

  public ReactiveVariableLabel(final ReactiveVariable v) {
    super();
    this.var = v;

    isHighlighted = false;
  }

  public boolean isHighlighted() {
    return isHighlighted;
  }

  public void setHighlighted(final boolean h) {
    this.isHighlighted = h;
  }

  @Override
  public String toString() {
    String label = ""; //$NON-NLS-1$

    if (var.getName() != null && !var.getName().equals("?")) { //$NON-NLS-1$
      label += "<h4>" + var.getName() + "</h4>"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    else {
      label += "<h4><i>[" + var.getTypeSimple() + "]</i></h4>"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    final ReactiveVariableType varType = var.getReactiveVariableType();
    if (varType == ReactiveVariableType.VAR || varType == ReactiveVariableType.SIGNAL) {
      if (isHighlighted) {
        label += "<h3>CHANGE</h3>"; //$NON-NLS-1$
      }
      else {
        label += "<h3>" + getValueString() + "</h3>"; //$NON-NLS-1$ //$NON-NLS-2$
      }
    }

    return label;
  }

  private String getValueString() {
    if (var.getValueString() == null) {
      return "null"; //$NON-NLS-1$
    }

    if (var.getValueString().length() > 18) {
      return var.getValueString().substring(0, 17) + '\u2026';
    }
    else {
      return var.getValueString();
    }
  }

  public ReactiveVariable getVar() {
    return var;
  }
}
