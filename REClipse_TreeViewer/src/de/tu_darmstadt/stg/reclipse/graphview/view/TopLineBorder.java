package de.tu_darmstadt.stg.reclipse.graphview.view;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Border that has just a line at the top
 */
public class TopLineBorder extends MarginBorder {

  public TopLineBorder(final Insets ins) {
    super(ins);
  }

  public TopLineBorder(final int t, final int l, final int b, final int r) {
    super(t, l, b, r);
  }

  public TopLineBorder(final int allsides) {
    super(allsides);
  }

  @Override
  public void paint(final IFigure figure, final Graphics graphics, final Insets in) {
    final Rectangle rec = getPaintRectangle(figure, insets);
    graphics.drawLine(rec.getTopLeft(), rec.getTopRight());
  }
}
