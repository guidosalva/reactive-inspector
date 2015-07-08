package de.tu_darmstadt.stg.reclipse.graphview.action;

import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.TreeViewGraph;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Provides the relayout action, which applies the currently selected layout
 * algorithm again, so that the nodes are positioned again according to the
 * layout algorithm.
 */
public class Relayout extends Action {

  private final TreeViewGraph graph;

  public Relayout(final TreeViewGraph graph) {
    this.graph = graph;

    setText(Texts.Relayout_Text);
    setToolTipText(Texts.Relayout_Tooltip);
    setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
  }

  @Override
  public void run() {
    graph.layoutGraph();
  }
}
