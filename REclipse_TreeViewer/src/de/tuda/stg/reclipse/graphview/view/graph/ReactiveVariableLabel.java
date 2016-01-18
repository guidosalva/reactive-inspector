package de.tuda.stg.reclipse.graphview.view.graph;

import de.tuda.stg.reclipse.logger.BreakpointInformation;
import de.tuda.stg.reclipse.logger.ReactiveVariable;
import de.tuda.stg.reclipse.logger.ReactiveVariableType;

@SuppressWarnings("nls")
public class ReactiveVariableLabel {

  private final ReactiveVariable var;
  private boolean showClassName;
  private final String className;
  private final StyleProperties styleProperties = new StyleProperties();

  private Long evaluationDuration;

  // Setup

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

  // Display

  private interface HTML {

    static String italic(final String html) {
      return wrap("i", html);
    }

    static String wrap(final String tag, final String html) {
      return "<" + tag + ">" + html + "</" + tag + ">";
    }

    static String style(final String style, final String html) {
      return "<div style=\"" + style + "\">" + html + "</div>";
    }

    static String titleStyle = "padding: 0px 0px 4px 0px; margin: 0px; font-size: 10px;";
    static String bodyStyle = "padding: 0px; margin: 0px; font-size: 11px;";
    static String detailStyle = "padding: 0px; margin: 0px;  font-size: 9px;";

  }

  @Override
  public String toString() {
    return HTML.style(HTML.titleStyle, getTitle()) +
            HTML.style(HTML.bodyStyle, getBody()) +
            HTML.style(HTML.detailStyle, getDetailString());
  }

  private String getTitle() {
    if (isNameValid(var)) {
      if (showClassName && className != null) {
        return className + "." + var.getName();
      } else {
        return var.getName();
      }
    } else {
      return HTML.italic("[" + var.getTypeSimple() + "]");
    }
  }

  private String getBody() {
    final ReactiveVariableType varType = var.getReactiveVariableType();

    if (var.isExceptionOccured()) {
      return HTML.italic("EXCEPTION");
    } else if (varType == ReactiveVariableType.VAR || varType == ReactiveVariableType.SIGNAL) {
      return getValueString();
    } else {
      return "";
    }
  }

  private boolean isNameValid(final ReactiveVariable variable) {
    return variable.getName() != null && !variable.getName().equals("?");
  }

  private String getValueString() {
    final String valueString = var.getValueString();
    if (valueString == null) {
      return "null";
    }

    if (valueString.length() > 18) {
      return valueString.substring(0, 17) + '\u2026';
    } else {
      return valueString;
    }
  }

  private String getDetailString() {
    if (evaluationDuration != null) {
      final long evaluationDuration = this.evaluationDuration / 1_000_000L;
      return String.valueOf(evaluationDuration) + " ms";
    }

    return "";
  }

  // Getters and Setters

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
