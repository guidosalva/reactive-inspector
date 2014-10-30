package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.model.CollapsedNodesFilter;
import de.tu_darmstadt.stg.reclipse.graphview.model.RemoteLoggerImpl;
import de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariableType;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * View class providing and containing all the information which is necessary in
 * order to show a reactive variable as a node in the dependency graph.
 */
public class ReactiveVariableFigure extends Figure {

  private static Font FONT_TYPE_NORMAL = new Font(null, "", 6, SWT.NONE); //$NON-NLS-1$
  private static Font FONT_TYPE_BOLD = new Font(null, "", 6, SWT.BOLD); //$NON-NLS-1$
  private static Font FONT_TYPE_ITALIC = new Font(null, "", 6, SWT.ITALIC); //$NON-NLS-1$
  private static Font FONT_TYPE_BOLD_ITALIC = new Font(null, "", 6, SWT.BOLD | SWT.ITALIC); //$NON-NLS-1$
  private static Font FONT_ATTRIBUTE_NAME = FONT_TYPE_NORMAL;
  private static Font FONT_ATTRIBUTE_VALUE = FONT_TYPE_ITALIC;

  private static final Color NODE_SIGNAL_COLOR = ColorConstants.orange;
  private static final Color NODE_VAR_COLOR = ColorConstants.yellow;
  private static final Color NODE_EVENT_COLOR = new Color(null, 37, 218, 255);
  private static final Color NODE_EVENT_HANDLER_COLOR = new Color(null, 37, 161, 255);
  private static final Color NODE_HIGHLIGHTED_COLOR = ColorConstants.lightGreen;
  private static final Color NODE_BORDER_STANDARD_COLOR = ColorConstants.black;
  private static final Color NODE_BORDER_HIGHLIGHT_COLOR = ColorConstants.red;

  protected final ReactiveVariable var;
  private final CollapsedNodesFilter collapsedNodesFilter;
  private final LineBorder varBorder = new LineBorder();
  private final LineBorder tooltipVarBorder = new LineBorder();
  private final Label valueLabel = new Label();
  private final Label tooltipValueLabel = new Label();
  private final Color nodeColor;
  private final Color nodeBorderColor;

  public ReactiveVariableFigure(final ReactiveVariable v, final CollapsedNodesFilter filter) {
    var = v;
    collapsedNodesFilter = filter;
    varBorder.setWidth(computeBorderWidth());
    setValueText();
    nodeColor = computeNodeColor();
    nodeBorderColor = computeNodeBorderColor();

    fillFigure(this, false);
    resize();
  }

  private int computeBorderWidth() {
    if (shouldHighlightBorder()) {
      return 4;
    }
    return 1;
  }

  private Color computeNodeColor() {
    if (var.isActive()) {
      return NODE_HIGHLIGHTED_COLOR;
    }
    else if (var.getReactiveVariableType() == ReactiveVariableType.VAR) {
      return NODE_VAR_COLOR;
    }
    else if (var.getReactiveVariableType() == ReactiveVariableType.SIGNAL) {
      return NODE_SIGNAL_COLOR;
    }
    else if (var.getReactiveVariableType() == ReactiveVariableType.EVENT) {
      return NODE_EVENT_COLOR;
    }
    return NODE_EVENT_HANDLER_COLOR;
  }

  private Color computeNodeBorderColor() {
    if (shouldHighlightBorder()) {
      return NODE_BORDER_HIGHLIGHT_COLOR;
    }
    return NODE_BORDER_STANDARD_COLOR;
  }

  private boolean shouldHighlightBorder() {
    return RemoteLoggerImpl.getCurrentDependencyGraphHistoryType() == DependencyGraphHistoryType.NODE_EVALUATION_STARTED && var.isActive();
  }

  protected void setValueText() {
    final String val = var.getValueString();
    valueLabel.setText(val);
    tooltipValueLabel.setText(val);
    resize();
  }

  private void resize() {
    final Dimension d = getPreferredSize();
    setSize(d.width + 4, d.height);
  }

  protected static void setAdditionalKeyLabel(final Label l, final Object o) {
    if (l != null) {
      l.setText(o != null ? o.toString() : ""); //$NON-NLS-1$
    }
  }

  private void fillFigure(final IFigure f, final boolean tooltip) {
    // paint the header
    if (collapsedNodesFilter.isNodeCollapsed(var.getId())) {
      final Label collapsedLabel = new Label(Texts.VariableNode_Collapsed);
      collapsedLabel.setFont(FONT_TYPE_BOLD_ITALIC);
      f.add(collapsedLabel);
    }
    final Label header = new Label(var.getTypeSimple());
    header.setFont(FONT_TYPE_BOLD);
    header.setBorder(new MarginBorder(3, 2, 3, 2));
    header.setForegroundColor(ColorConstants.black);
    f.add(header);

    // paint the attributes
    f.add(getAttributesFigure(tooltip));

    varBorder.setColor(nodeBorderColor);
    f.setBorder(tooltip ? tooltipVarBorder : varBorder);
    f.setLayoutManager(new ToolbarLayout());
    f.setOpaque(true);
    f.setBackgroundColor(nodeColor);

    if (!tooltip) {
      final IFigure tooltipFigure = new Figure();
      fillFigure(tooltipFigure, true);
      f.setToolTip(tooltipFigure);
    }
  }

  private IFigure getAttributesFigure(final boolean tooltip) {

    final Figure attributes = new Figure();
    attributes.setForegroundColor(ColorConstants.black);
    {
      // set the layout
      final GridLayout layout = new GridLayout();
      layout.numColumns = 2;
      attributes.setLayoutManager(layout);
    }

    if (var.getName() != null) {
      Label l = new Label(Texts.VariableNode_Name);
      l.setLabelAlignment(PositionConstants.LEFT);
      l.setFont(FONT_ATTRIBUTE_NAME);
      attributes.add(l);
      l = new Label(var.getName());
      l.setLabelAlignment(PositionConstants.LEFT);
      l.setFont(FONT_ATTRIBUTE_VALUE);
      attributes.add(l);
    }
    {
      // value label is always visible
      Label l = new Label(Texts.VariableNode_Value);
      l.setLabelAlignment(PositionConstants.LEFT);
      l.setFont(FONT_ATTRIBUTE_NAME);
      attributes.add(l);
      l = (tooltip ? tooltipValueLabel : valueLabel);
      l.setLabelAlignment(PositionConstants.LEFT);
      l.setFont(FONT_ATTRIBUTE_VALUE);
      attributes.add(l);
    }

    if (tooltip) {
      {
        Label l = new Label(Texts.VariableNode_Id);
        l.setLabelAlignment(PositionConstants.LEFT);
        l.setFont(FONT_ATTRIBUTE_NAME);
        attributes.add(l);
        l = new Label(var.getId().toString());
        l.setLabelAlignment(PositionConstants.LEFT);
        l.setFont(FONT_ATTRIBUTE_VALUE);
        attributes.add(l);
      }

      if (var.getTypeFull() != null) {
        Label l = new Label(Texts.VariableNode_Fulltype);
        l.setLabelAlignment(PositionConstants.LEFT);
        l.setFont(FONT_ATTRIBUTE_NAME);
        attributes.add(l);
        l = new Label(var.getTypeFull());
        l.setLabelAlignment(PositionConstants.LEFT);
        l.setFont(FONT_ATTRIBUTE_VALUE);
        attributes.add(l);
      }

      {
        // fill the additional keys
        final Figure additionals = new Figure();
        {
          // set the layout
          final GridLayout layout = new GridLayout();
          layout.numColumns = 2;
          additionals.setLayoutManager(layout);
        }

        for (final String key : var.getAdditionalKeysKeySet()) {
          Label l = new Label(key + Texts.VariableNode_AdditionKeyPostfix);
          l.setLabelAlignment(PositionConstants.LEFT);
          l.setFont(FONT_ATTRIBUTE_NAME);
          additionals.add(l);
          l = new Label();
          setAdditionalKeyLabel(l, var.getAdditionalKeyValue(key));
          l.setLabelAlignment(PositionConstants.LEFT);
          l.setFont(FONT_ATTRIBUTE_VALUE);
          additionals.add(l);
        }

        additionals.setBorder(new TopLineBorder(0));
        // paint the attributes
        final GridData gd = new GridData();
        gd.horizontalSpan = 2;
        attributes.add(additionals, gd);
      }
    }

    attributes.setBorder(new TopLineBorder(0));

    return attributes;
  }

  public ReactiveVariable getVar() {
    return var;
  }
}
