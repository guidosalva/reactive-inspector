package de.tu_darmstadt.stg.reclipse.graphview.action;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.GraphContainer;

import org.eclipse.jface.action.Action;

/**
 * Provides the action to zoom into the current graph.
 */
public class ZoomIn extends Action {

  private final GraphContainer graphContainer;

  public ZoomIn(final GraphContainer graphContainer) {
    this.graphContainer = graphContainer;

    setText(Texts.ZoomIn_Text);
    setToolTipText(Texts.ZoomIn_Tooltip);
    setImageDescriptor(Activator.getImageDescriptor(Images.ZOOM_IN));
  }

  @Override
  public void run() {
    if (graphContainer.containsGraph()) {
      graphContainer.getGraph().zoomIn();
    }
  }
}
