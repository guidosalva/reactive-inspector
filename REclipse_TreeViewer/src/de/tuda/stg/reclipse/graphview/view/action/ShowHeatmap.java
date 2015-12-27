package de.tuda.stg.reclipse.graphview.view.action;

import de.tuda.stg.reclipse.graphview.Activator;
import de.tuda.stg.reclipse.graphview.Images;
import de.tuda.stg.reclipse.graphview.Texts;
import de.tuda.stg.reclipse.graphview.view.graph.TreeViewGraph;

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