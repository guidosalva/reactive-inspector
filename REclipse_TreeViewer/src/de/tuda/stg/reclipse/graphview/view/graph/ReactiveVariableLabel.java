package de.tuda.stg.reclipse.graphview.view.graph;

import de.tuda.stg.reclipse.logger.BreakpointInformation;
import de.tuda.stg.reclipse.logger.ReactiveVariable;
import de.tuda.stg.reclipse.logger.ReactiveVariableType;

public class ReactiveVariableLabel {

  private final ReactiveVariable var;
  private boolean showClassName;
  private final String className;
  private final StyleProperties styleProperties = new StyleProperties();

  private Long evaluationDuration;

  public ReactiveVariableLabel(final ReactiveVariable v, final BreakpointInformation br, final boolean showClassName) {
    this.var = v;
    this.showClassName = showClassName;
    this.className = parseClassName(br);
    this.evaluationDuration = null;
  }

  public ReactiveVariableLabel(final ReactiveVariable v, final BreakpointInformation br, final Long evaluationDuration) {
    this(v, br, false);
    this.evaluationDuration = evaluationDuration;
  }

  private String parseClassName(final BreakpointInformation br) {
    if (br == null || br.getClassName() == null || br.getClassName().trim().isEmpty()) {
      return null;
    }

    String className = br.getClassName();
    final int start = className.lastIndexOf('.');
    if (start >= 0) {
      className = className.substring(start + 1);
    }

    return className;
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
    if (evaluationDuration != null) {
      final long evaluationDuration = this.evaluationDuration;
      return String.valueOf(evaluationDuration / 1_000_000.0);
    }

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

  public StyleProperties getStyleProperties() {
    return styleProperties;
  }
}
