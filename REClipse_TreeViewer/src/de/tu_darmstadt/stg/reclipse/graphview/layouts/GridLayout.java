package de.tu_darmstadt.stg.reclipse.graphview.layouts;

import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;

public class GridLayout extends CompositeLayoutAlgorithm {

  private static final int STYLE = LayoutStyles.NO_LAYOUT_NODE_RESIZING;

  public GridLayout() {
    super(STYLE, new LayoutAlgorithm[] {
        new GridLayoutAlgorithm(STYLE),
        new HorizontalShift(STYLE)
    });
  }
}
