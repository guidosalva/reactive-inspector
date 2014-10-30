package de.tu_darmstadt.stg.reclipse.graphview.provider;

import de.tu_darmstadt.stg.reclipse.graphview.model.CollapsedNodesFilter;
import de.tu_darmstadt.stg.reclipse.graphview.model.RemoteLoggerImpl;
import de.tu_darmstadt.stg.reclipse.graphview.view.ReactiveVariableFigure;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.util.UUID;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.zest.core.viewers.IEntityConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IFigureProvider;

/**
 * Provider which creates instances of {@link ReactiveVariableFigure} in order
 * to show nodes in the graph and which is responsible for the connection
 * colours.
 */
public class ReactiveFigureProvider extends LabelProvider implements IFigureProvider, IEntityConnectionStyleProvider {

  public static final Color CONNECTION_COLOR = ColorConstants.lightGray;
  public static final Color CONNECTION_HIGHLIGHTED_COLOR = ColorConstants.red;

  private final CollapsedNodesFilter collapsedNodesFilter;

  public ReactiveFigureProvider(final CollapsedNodesFilter filter) {
    collapsedNodesFilter = filter;
  }

  @Override
  public String getText(final Object element) {
    if (element instanceof ReactiveVariable) {
      final ReactiveVariable v = (ReactiveVariable) element;
      return v.getName();
    }
    if (element instanceof Dependency) {
      return null;
    }
    return super.getText(element);
  }

  @Override
  public IFigure getFigure(final Object element) {
    if (element instanceof ReactiveVariable) {
      return new ReactiveVariableFigure((ReactiveVariable) element, collapsedNodesFilter);
    }

    return null;
  }

  @Override
  public int getConnectionStyle(final Object src, final Object dest) {
    return 0;
  }

  @Override
  public Color getColor(final Object src, final Object dest) {
    if (src instanceof ReactiveVariable && dest instanceof ReactiveVariable) {
      final UUID srcId = ((ReactiveVariable) src).getId();
      final UUID destId = ((ReactiveVariable) dest).getId();
      if (RemoteLoggerImpl.isNodeConnectionCurrentlyActive(srcId, destId)) {
        return CONNECTION_HIGHLIGHTED_COLOR;
      }
      return CONNECTION_COLOR;
    }

    return null;
  }

  @Override
  public Color getHighlightColor(final Object src, final Object dest) {
    return null;
  }

  @Override
  public int getLineWidth(final Object src, final Object dest) {
    return 3;
  }

  @Override
  public IFigure getTooltip(final Object entity) {
    return null;
  }
}