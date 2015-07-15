package de.tu_darmstadt.stg.reclipse.graphview.view.graph;

import de.tu_darmstadt.stg.reclipse.logger.BreakpointInformation;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariableType;

public class ReactiveVariableLabel {

  private final ReactiveVariable var;
  private boolean showClassName;
  private final String className;

  public ReactiveVariableLabel(final ReactiveVariable v, final BreakpointInformation br, final boolean showClassName) {
    super();
    this.var = v;
    this.showClassName = showClassName;
    this.className = parseClassName(br);
  }

  private String parseClassName(final BreakpointInformation br) {
    if (br != null && br.getClassName() != null && !br.getClassName().trim().isEmpty()) {
      final int start = br.getClassName().lastIndexOf('.');

      if (start >= 0) {
        return br.getClassName().substring(start + 1);
      }
      else {
        return br.getClassName();
      }
    }
    else {
      return null;
    }
  }

  @Override
  public String toString() {
    String label = ""; //$NON-NLS-1$

    if (var.getName() != null && !var.getName().equals("?")) { //$NON-NLS-1$
      if (showClassName && className != null) {
        label += "<h4>" + className + "." + var.getName() + "</h4>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }
      else {
        label += "<h4>" + var.getName() + "</h4>"; //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
    else {
      label += "<h4><i>[" + var.getTypeSimple() + "]</i></h4>"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    final ReactiveVariableType varType = var.getReactiveVariableType();

    if (var.isExceptionOccured()) {
      label += "<h3><i>EXCEPTION</i></h3>"; //$NON-NLS-1$
    }
    else if (varType == ReactiveVariableType.VAR || varType == ReactiveVariableType.SIGNAL) {
      label += "<h3>" + getValueString() + "</h3>"; //$NON-NLS-1$ //$NON-NLS-2$
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

  public boolean isShowClassName() {
    return showClassName;
  }

  public void setShowClassName(final boolean showClassName) {
    this.showClassName = showClassName;
  }
}
