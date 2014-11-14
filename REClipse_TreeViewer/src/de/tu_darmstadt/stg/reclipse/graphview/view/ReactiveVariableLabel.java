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
    String label = var.getName() + "\n\n"; //$NON-NLS-1$

    if (isHighlighted) {
      label += "Value: CHANGE\n"; //$NON-NLS-1$
    }
    else {
      label += "Value: " + var.getValueString() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    label += "Type: " + var.getTypeSimple(); //$NON-NLS-1$

    return label;
  }
}
