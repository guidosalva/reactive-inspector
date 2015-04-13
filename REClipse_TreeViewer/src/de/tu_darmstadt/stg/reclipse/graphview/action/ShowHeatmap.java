package de.tu_darmstadt.stg.reclipse.graphview.action;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.GraphContainer;

import org.eclipse.jface.action.Action;

public class ShowHeatmap extends Action {

  private final GraphContainer graphContainer;

  private boolean status;

  public ShowHeatmap(final GraphContainer graphContainer) {
    this.graphContainer = graphContainer;
    this.status = false;

    setText(Texts.Show_Heatmap);
    setToolTipText(Texts.Show_Heatmap_Tooltip);
    setImageDescriptor(Activator.getImageDescriptor(Images.HEATMAP_GREY));
  }

  @Override
  public void run() {
    if (graphContainer.containsGraph()) {
      status = !status;

      if (status) {
        setImageDescriptor(Activator.getImageDescriptor(Images.HEATMAP));
      }
      else {
        setImageDescriptor(Activator.getImageDescriptor(Images.HEATMAP_GREY));
      }

      graphContainer.getGraph().setHeatmapEnabled(status);
      graphContainer.getGraph().updateGraph();
    }
  }
}