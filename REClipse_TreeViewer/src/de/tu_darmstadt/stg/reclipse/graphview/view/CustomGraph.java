package de.tu_darmstadt.stg.reclipse.graphview.view;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

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

    // create child composite
    final Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.BACKGROUND);

    // set layout data to fill parent composite
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    // create frame inside composite
    final Frame graphFrame = SWT_AWT.new_Frame(composite);

    // initialize graph component and add it to frame
    graphComponent = new mxGraphComponent(this);
    graphFrame.add(graphComponent);

    // add listeners
    addMouseWheelListener();
    addMouseListener();
  }

  private void addMouseWheelListener() {
    graphComponent.addMouseWheelListener(new MouseWheelListener() {

      @Override
      public void mouseWheelMoved(final MouseWheelEvent e) {
        // get rotation of mouse wheel
        final int steps = e.getWheelRotation();

        if (steps < 0) {
          // zoom in if mouse wheel scrolls up
          zoomIn();
        }
        else {
          // zoom out if mouse wheel scrolls down
          zoomOut();
        }
      }
    });
  }

  private void addMouseListener() {
    graphComponent.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(final MouseEvent e) {
        // TODO Implement vertex toggle
      }
    });
  }

  public void zoomIn() {
    graphComponent.zoomIn();
  }

  public void zoomOut() {
    graphComponent.zoomOut();
  }
}
