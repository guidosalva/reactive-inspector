package de.tuda.stg.reclipse.graphview.view.graph;

import de.tuda.stg.reclipse.graphview.Texts;
import de.tuda.stg.reclipse.logger.BreakpointInformation;
import de.tuda.stg.reclipse.logger.ReactiveVariable;

@SuppressWarnings("nls")
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
    builder.append("<html><table>");

    appendName(builder);
    appendType(builder);
    appendValue(builder);
    appendClass(builder);
    appendSource(builder);
    appendLineNumber(builder);

    builder.append("</table></html>");

    return builder.toString();
  }

  private void appendName(final StringBuilder builder) {
    builder.append("<tr>");
    builder.append("<td><b>").append(Texts.Graph_Tooltip_Name).append("<b></td>");
    builder.append("<td>").append(var.getName()).append("</td>");
    builder.append("</tr>");
  }

  private void appendType(final StringBuilder builder) {
    builder.append("<tr>");
    builder.append("<td><b>").append(Texts.Graph_Tooltip_Type).append("<b></td>");
    builder.append("<td>").append(var.getTypeSimple()).append("</td>");
    builder.append("</tr>");
  }

  private void appendValue(final StringBuilder builder) {
    final String name = var.isExceptionOccured() ? Texts.Graph_Tooltip_Exception : Texts.Graph_Tooltip_Value;

    builder.append("<tr>");
    builder.append("<td><b>").append(name).append("<b></td>");
    builder.append("<td>").append(var.getValueString()).append("</td>");
    builder.append("</tr>");
  }

  private void appendClass(final StringBuilder builder) {
    builder.append("<tr>");
    builder.append("<td><b>").append(Texts.Graph_Tooltip_Class).append("<b></td>");
    builder.append("<td>").append(getClassName()).append("</td>");
    builder.append("</tr>");
  }

  private void appendSource(final StringBuilder builder) {
    builder.append("<tr>");
    builder.append("<td><b>").append(Texts.Graph_Tooltip_Source).append("<b></td>");
    builder.append("<td>").append(getSource()).append("</td>");
    builder.append("</tr>");
  }

  private void appendLineNumber(final StringBuilder builder) {
    builder.append("<tr>");
    builder.append("<td><b>").append(Texts.Graph_Tooltip_Line).append("<b></td>");
    builder.append("<td>").append(getLineNumber()).append("</td>");
    builder.append("</tr>");
  }

  private String getClassName() {
    return breakpointInformation != null ? breakpointInformation.getClassName() : "?";
  }

  private String getSource() {
    return breakpointInformation != null ? breakpointInformation.getSourcePath() : "?";
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
