package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

public class ReactiveVariableTooltip {

  private final ReactiveVariable var;
  private final String tooltipContent;

  public ReactiveVariableTooltip(final ReactiveVariable var) {
    super();
    this.var = var;
    this.tooltipContent = createContent();
  }

  private String createContent() {
    final StringBuilder builder = new StringBuilder();
    builder.append("<html><table>"); //$NON-NLS-1$

    appendName(builder);
    appendType(builder);
    appendValue(builder);

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
    builder.append("<tr>"); //$NON-NLS-1$
    builder.append("<td><b>").append(Texts.Graph_Tooltip_Value).append("<b></td>"); //$NON-NLS-1$ //$NON-NLS-2$
    builder.append("<td>").append(var.getValueString()).append("</td>"); //$NON-NLS-1$//$NON-NLS-2$
    builder.append("</tr>"); //$NON-NLS-1$
  }

  public ReactiveVariable getVar() {
    return var;
  }

  @Override
  public String toString() {
    return tooltipContent;
  }
}
