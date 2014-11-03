package de.tu_darmstadt.stg.reclipse.graphview.view;

import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

/**
 * 
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 * 
 */
public class CustomGraph extends mxGraph {

  protected mxGraphComponent graphComponent;

  public CustomGraph(final Composite parent) {
    super();

    final Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.BACKGROUND);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    final Frame graphFrame = SWT_AWT.new_Frame(composite);

    graphComponent = new mxGraphComponent(this);
    graphFrame.add(graphComponent);
  }
}
