package de.tu_darmstadt.stg.reclipse.graphview.action;

import de.tu_darmstadt.stg.reclipse.graphview.Texts;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.GraphViewer;

/**
 * Provides the relayout action, which applies the currently selected layout
 * algorithm again, so that the nodes are positioned again according to the
 * layout algorithm.
 */
public class Relayout extends Action {

  private final GraphViewer viewer;

  public Relayout(final GraphViewer v) {
    viewer = v;

    setText(Texts.Relayout_Text);
    setToolTipText(Texts.Relayout_Tooltip);
    setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
  }

  @Override
  public void run() {
    viewer.applyLayout();
  }
}
