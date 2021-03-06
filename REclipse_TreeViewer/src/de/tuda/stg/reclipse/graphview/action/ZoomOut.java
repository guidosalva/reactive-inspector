package de.tuda.stg.reclipse.graphview.action;

import de.tuda.stg.reclipse.graphview.Activator;
import de.tuda.stg.reclipse.graphview.Images;
import de.tuda.stg.reclipse.graphview.Texts;
import de.tuda.stg.reclipse.graphview.view.graph.GraphComponent;

import org.eclipse.jface.action.Action;

/**
 * Provides the action to zoom out of the current graph.
 */
public class ZoomOut extends Action {

  private final GraphComponent graphComponent;

  public ZoomOut(final GraphComponent graphComponent) {
    this.graphComponent = graphComponent;

    setText(Texts.ZoomOut_Text);
    setToolTipText(Texts.ZoomOut_Tooltip);
    setImageDescriptor(Activator.getImageDescriptor(Images.ZOOM_OUT));
  }

  @Override
  public void run() {
    graphComponent.zoomOut();
  }
}
