package de.tuda.stg.reclipse.graphview.action;

import de.tuda.stg.reclipse.graphview.Activator;
import de.tuda.stg.reclipse.graphview.Images;
import de.tuda.stg.reclipse.graphview.Properties;
import de.tuda.stg.reclipse.graphview.Texts;
import de.tuda.stg.reclipse.graphview.view.graph.TreeViewGraph;

import org.eclipse.jface.action.Action;

public class ShowClassName extends Action {

  private final TreeViewGraph graph;

  public ShowClassName(final TreeViewGraph graph) {
    super(Texts.ShowClassName_Text, AS_CHECK_BOX);

    this.graph = graph;

    setToolTipText(Texts.ShowClassName_Tooltip);
    setImageDescriptor(Activator.getImageDescriptor(Images.SHOW_CLASS_NAME));
    setChecked(Properties.getBoolean(Properties.SHOW_CLASS_NAME));
  }

  @Override
  public void run() {
    Properties.setBoolean(Properties.SHOW_CLASS_NAME, this.isChecked());

    graph.setShowClassName(this.isChecked());
  }

}
