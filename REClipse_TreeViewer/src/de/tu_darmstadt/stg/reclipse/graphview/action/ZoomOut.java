package de.tu_darmstadt.stg.reclipse.graphview.action;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.view.CustomGraph;

import org.eclipse.jface.action.Action;

/**
 * Provides the action to zoom out of the current graph.
 */
public class ZoomOut extends Action {

  private final CustomGraph graph;

  public ZoomOut(final CustomGraph g) {
    graph = g;

    setText(Texts.ZoomOut_Text);
    setToolTipText(Texts.ZoomOut_Tooltip);
    setImageDescriptor(Activator.getImageDescriptor(Images.ZOOM_OUT));
  }

  @Override
  public void run() {
    graph.zoomOut();
  }
}
