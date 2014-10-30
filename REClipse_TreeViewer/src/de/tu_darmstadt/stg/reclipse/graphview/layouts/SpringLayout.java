package de.tu_darmstadt.stg.reclipse.graphview.layouts;

import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

public class SpringLayout extends CompositeLayoutAlgorithm {

  private static final int STYLE = LayoutStyles.NO_LAYOUT_NODE_RESIZING;

  public SpringLayout() {
    super(STYLE, new LayoutAlgorithm[] {
        new SpringLayoutAlgorithm(STYLE),
        new HorizontalShift(STYLE)
    });
  }
}
