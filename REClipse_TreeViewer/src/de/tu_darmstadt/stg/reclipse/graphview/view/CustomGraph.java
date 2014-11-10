package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.provider.ContentModel;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.SwingConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

/**
 * 
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 * 
 */
public class CustomGraph extends mxGraph {

  protected mxGraphComponent graphComponent;

  protected ContentModel contentModel;

  final mxGraphLayout graphLayout;

  private final Map<mxCell, Set<Object>> collapsedVertices;

  public CustomGraph(final Composite parent) {
    super();

    // set custom stylesheet
    setStylesheet(new CustomGraphStylesheet());

    // initialize collapsed vertices map
    collapsedVertices = new HashMap<>();

    // create new content model
    contentModel = new ContentModel();

    // set graph layout
    graphLayout = new mxHierarchicalLayout(this, SwingConstants.WEST);

    // create child composite
    final Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.BACKGROUND);

    // set layout data to fill parent composite
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    // create frame inside composite
    final Frame graphFrame = SWT_AWT.new_Frame(composite);

    // initialize graph component and add it to frame
    graphComponent = new mxGraphComponent(this);
    graphComponent.setEnabled(false);

    graphFrame.add(graphComponent);

    // add listeners
    addMouseWheelListener();
    addMouseListener();

    updateGraph();
  }

  public void setPointInTime(final int pointInTime) {
    // set point in time in content model
    contentModel.setPointInTime(pointInTime);

    updateGraph();
  }

  public void updateGraph() {
    // remove cells, if any
    removeCells(getChildVertices(getDefaultParent()));

    // clear collapsed map
    collapsedVertices.clear();

    // load vertices from content model
    final Set<ReactiveVariableVertex> vertices = contentModel.getVertices();

    // load edges from content model
    final Map<UUID, Set<UUID>> edges = contentModel.getEdges();

    // insert vertices
    final Map<UUID, Object> mapping = new HashMap<>();
    for (final ReactiveVariableVertex vertex : vertices) {
      final Object cell = vertex.draw(this);

      // add cell to mapping
      mapping.put(vertex.getVar().getId(), cell);
    }

    // insert edges
    for (final Object sourceId : edges.keySet()) {
      // get source vertex
      final Object source = mapping.get(sourceId);

      // get destinations
      final Set<UUID> destinations = edges.get(sourceId);

      for (final UUID destinationId : destinations) {
        // get destination vertex
        final Object destination = mapping.get(destinationId);

        insertEdge(defaultParent, null, "", source, destination, "EDGE"); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }

    // execute layout
    getModel().beginUpdate();
    try {
      // execute layout
      graphLayout.execute(getDefaultParent());

      // center cells
      moveCells(getChildCells(getDefaultParent(), true, true), 50, 50);
    }
    finally {
      getModel().endUpdate();
    }
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
    graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(final MouseEvent e) {
        // get clicked cell
        final mxCell cell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());

        getModel().beginUpdate();
        try {
          // cell collapsed?
          if (collapsedVertices.containsKey(cell)) {
            // show cells
            toggleCells(true, collapsedVertices.get(cell).toArray(), true);

            // remove cell from collapsed map
            collapsedVertices.remove(cell);
          }
          else {
            // collect children of cell
            final Set<Object> children = new HashSet<>();
            traverse(cell, true, new mxICellVisitor() {

              @Override
              public boolean visit(final Object vertex, final Object edge) {
                if (vertex != cell) {
                  children.add(vertex);
                }
                return vertex == cell || !isCellCollapsed(vertex);
              }
            });

            // add to collapsed map
            collapsedVertices.put(cell, children);

            // hide cells
            toggleCells(false, collapsedVertices.get(cell).toArray(), true);
          }
        }
        finally {
          getModel().endUpdate();
        }
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
