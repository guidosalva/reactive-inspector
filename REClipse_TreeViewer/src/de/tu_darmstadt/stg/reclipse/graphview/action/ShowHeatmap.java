package de.tu_darmstadt.stg.reclipse.graphview.action;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.TreeViewGraph;

import org.eclipse.jface.action.Action;

public class ShowHeatmap extends Action {

  private final TreeViewGraph graph;

  public ShowHeatmap(final TreeViewGraph graph) {
    this.graph = graph;

    setText(Texts.Show_Heatmap);
    setToolTipText(Texts.Show_Heatmap_Tooltip);
    setImageDescriptor(Activator.getImageDescriptor(Images.HEATMAP_GREY));
  }

  @Override
  public void run() {
    final boolean status = !graph.isHeatmapEnabled();

    if (status) {
      setImageDescriptor(Activator.getImageDescriptor(Images.HEATMAP));
    }
    else {
      setImageDescriptor(Activator.getImageDescriptor(Images.HEATMAP_GREY));
    }

    graph.setHeatmapEnabled(status);
    graph.updateGraph();
  }
}