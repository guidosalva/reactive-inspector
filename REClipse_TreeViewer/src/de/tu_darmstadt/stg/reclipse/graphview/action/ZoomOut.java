package de.tu_darmstadt.stg.reclipse.graphview.action;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.view.CustomGraphViewer;

import org.eclipse.jface.action.Action;

/**
 * Provides the action to zoom out of the current graph.
 */
public class ZoomOut extends Action {

  private final CustomGraphViewer viewer;

  public ZoomOut(final CustomGraphViewer v) {
    viewer = v;

    setText(Texts.ZoomOut_Text);
    setToolTipText(Texts.ZoomOut_Tooltip);
    setImageDescriptor(Activator.getImageDescriptor(Images.ZOOM_OUT));
  }

  @Override
  public void run() {
    viewer.zoomOut();
  }
}
