package de.tu_darmstadt.stg.reclipse.graphview.layouts;

import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;

public class DirectedGraphLayout extends CompositeLayoutAlgorithm {

  private static final int STYLE = LayoutStyles.NO_LAYOUT_NODE_RESIZING;

  public DirectedGraphLayout() {
    super(STYLE, new LayoutAlgorithm[] {
        new DirectedGraphLayoutAlgorithm(STYLE),
        new HorizontalShift(STYLE)
    });
  }
}
