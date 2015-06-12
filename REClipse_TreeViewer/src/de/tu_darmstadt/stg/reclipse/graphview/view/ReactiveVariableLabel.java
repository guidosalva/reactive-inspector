package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

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
    String label = "<h4>" + var.getName() + "</h4>"; //$NON-NLS-1$ //$NON-NLS-2$

    if (isHighlighted) {
      label += "Value: CHANGE\n"; //$NON-NLS-1$
    }
    else {
      label += "Value: " + getValueString() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    label += "Type: " + var.getTypeSimple(); //$NON-NLS-1$

    return label;
  }

  private String getValueString() {
    if (var.getValueString().length() > 20) {
      return var.getValueString().substring(0, 19) + '\u2026';
    }
    else {
      return var.getValueString();
    }
  }
}
