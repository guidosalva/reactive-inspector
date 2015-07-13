package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.logger.BreakpointInformation;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

public class ReactiveVariableTooltip {

  private final ReactiveVariable var;
  private final BreakpointInformation breakpointInformation;
  private final String tooltipContent;

  public ReactiveVariableTooltip(final ReactiveVariable var, final BreakpointInformation breakpointInformation) {
    super();
    this.var = var;
    this.breakpointInformation = breakpointInformation;
    this.tooltipContent = createContent();
  }

  private String createContent() {
    final StringBuilder builder = new StringBuilder();
    builder.append("<html><table>"); //$NON-NLS-1$

    appendName(builder);
    appendType(builder);
    appendValue(builder);
    appendClass(builder);
    appendSource(builder);
    appendLineNumber(builder);

    builder.append("</table></html>"); //$NON-NLS-1$

    return builder.toString();
  }

  private void appendName(final StringBuilder builder) {
    builder.append("<tr>"); //$NON-NLS-1$
    builder.append("<td><b>").append(Texts.Graph_Tooltip_Name).append("<b></td>"); //$NON-NLS-1$ //$NON-NLS-2$
    builder.append("<td>").append(var.getName()).append("</td>"); //$NON-NLS-1$//$NON-NLS-2$
    builder.append("</tr>"); //$NON-NLS-1$
  }

  private void appendType(final StringBuilder builder) {
    builder.append("<tr>"); //$NON-NLS-1$
    builder.append("<td><b>").append(Texts.Graph_Tooltip_Type).append("<b></td>"); //$NON-NLS-1$ //$NON-NLS-2$
    builder.append("<td>").append(var.getTypeSimple()).append("</td>"); //$NON-NLS-1$//$NON-NLS-2$
    builder.append("</tr>"); //$NON-NLS-1$
  }

  private void appendValue(final StringBuilder builder) {
    final String name = var.isExceptionOccured() ? Texts.Graph_Tooltip_Exception : Texts.Graph_Tooltip_Value;

    builder.append("<tr>"); //$NON-NLS-1$
    builder.append("<td><b>").append(name).append("<b></td>"); //$NON-NLS-1$ //$NON-NLS-2$
    builder.append("<td>").append(var.getValueString()).append("</td>"); //$NON-NLS-1$//$NON-NLS-2$
    builder.append("</tr>"); //$NON-NLS-1$
  }

  private void appendClass(final StringBuilder builder) {
    builder.append("<tr>"); //$NON-NLS-1$
    builder.append("<td><b>").append(Texts.Graph_Tooltip_Class).append("<b></td>"); //$NON-NLS-1$ //$NON-NLS-2$
    builder.append("<td>").append(getClassName()).append("</td>"); //$NON-NLS-1$//$NON-NLS-2$
    builder.append("</tr>"); //$NON-NLS-1$
  }

  private void appendSource(final StringBuilder builder) {
    builder.append("<tr>"); //$NON-NLS-1$
    builder.append("<td><b>").append(Texts.Graph_Tooltip_Source).append("<b></td>"); //$NON-NLS-1$ //$NON-NLS-2$
    builder.append("<td>").append(getSource()).append("</td>"); //$NON-NLS-1$//$NON-NLS-2$
    builder.append("</tr>"); //$NON-NLS-1$
  }

  private void appendLineNumber(final StringBuilder builder) {
    builder.append("<tr>"); //$NON-NLS-1$
    builder.append("<td><b>").append(Texts.Graph_Tooltip_Line).append("<b></td>"); //$NON-NLS-1$ //$NON-NLS-2$
    builder.append("<td>").append(getLineNumber()).append("</td>"); //$NON-NLS-1$//$NON-NLS-2$
    builder.append("</tr>"); //$NON-NLS-1$
  }

  private String getClassName() {
    return breakpointInformation != null ? breakpointInformation.getClassName() : "?"; //$NON-NLS-1$
  }

  private String getSource() {
    return breakpointInformation != null ? breakpointInformation.getSourcePath() : "?"; //$NON-NLS-1$
  }

  private int getLineNumber() {
    return breakpointInformation != null ? breakpointInformation.getLineNumber() : -1;
  }

  public ReactiveVariable getVar() {
    return var;
  }

  @Override
  public String toString() {
    return tooltipContent;
  }
}
