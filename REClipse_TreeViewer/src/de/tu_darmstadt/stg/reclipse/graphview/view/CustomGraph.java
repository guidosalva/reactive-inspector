package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.provider.ContentModel;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

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

  private mxGraphLayout graphLayout;

  final Map<mxCell, Set<Object>> collapsedVertices;

  final Map<mxCell, Set<Object>> highlightedVertices;

  public CustomGraph(final Composite parent) {
    super();

    // set custom stylesheet
    setStylesheet(new CustomGraphStylesheet());

    // initialize collapsed vertices map
    collapsedVertices = new HashMap<>();

    // initialize highlighted vertices map
    highlightedVertices = new HashMap<>();

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

  /**
   * Layouts the graph.
   */
  public void layoutGraph() {
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

  public void setGraphLayout(final mxGraphLayout l) {
    graphLayout = l;
  }

  /**
   * Updates the point in time the graph should visualize.
   * 
   * @param pointInTime
   *          A point in time.
   */
  public void setPointInTime(final int pointInTime) {
    // set point in time in content model
    contentModel.setPointInTime(pointInTime);

    updateGraph();
  }

  /**
   * Redraws the graph by loading the necessary vertices and connecting them
   * appropriately.
   */
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

    layoutGraph();
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
      }

      @Override
      public void mouseReleased(final MouseEvent e) {
        if (!SwingUtilities.isRightMouseButton(e)) {
          return;
        }

        // find clicked cell
        final mxCell cell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());

        // if no cell has been clicked, return
        if (cell == null) {
          return;
        }

        final JPopupMenu popupMenu = new JPopupMenu();

        // collapse menu item
        final JMenuItem collapseItem = new JMenuItem();
        collapseItem.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(final ActionEvent e) {
            collapseCell(cell);
          }
        });

        if (isCollapsed(cell)) {
          collapseItem.setText("Unfold (" + getAmountOfCollapsedCells(cell) + " cells)");
        }
        else {
          collapseItem.setText("Fold");
        }

        // highlight menu item
        final JMenuItem highlightItem = new JMenuItem();
        highlightItem.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(final ActionEvent e) {
            highlightCell(cell);
          }
        });

        if (isHighlighted(cell)) {
          highlightItem.setText("Remove Highlight");
        }
        else {
          highlightItem.setText("Highlight Change Propagation");
        }

        // add menu items
        popupMenu.add(collapseItem);
        popupMenu.addSeparator();
        popupMenu.add(highlightItem);

        // show popup menu on clicked point
        popupMenu.show(graphComponent, e.getX(), e.getY());
      }
    });
  }

  public boolean isCollapsed(final mxCell cell) {
    return collapsedVertices.containsKey(cell);
  }

  public int getAmountOfCollapsedCells(final mxCell cell) {
    if (!collapsedVertices.containsKey(cell)) {
      return 0;
    }

    return collapsedVertices.get(cell).size();
  }

  public void collapseCell(final mxCell cell) {
    if (cell == null) {
      return;
    }

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
        final Set<Object> children = getChildrenOfCell(cell);

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

  public boolean isHighlighted(final mxCell cell) {
    return highlightedVertices.containsKey(cell);
  }

  public void highlightCell(final mxCell cell) {
    if (cell == null) {
      return;
    }

    getModel().beginUpdate();
    try {
      // cell highlighted?
      if (highlightedVertices.containsKey(cell)) {
        for (final Object child : highlightedVertices.get(cell)) {
          final mxCell childCell = (mxCell) child;

          childCell.setStyle(childCell.getStyle().replace("HIGHLIGHTED", ""));
        }

        // remove cell from highlighted map
        highlightedVertices.remove(cell);
      }
      else {
        final Set<Object> children = getChildrenOfCell(cell);

        // add to highlighted mpa
        highlightedVertices.put(cell, children);

        // remove highlight from cells
        for (final Object child : highlightedVertices.get(cell)) {
          final mxCell childCell = (mxCell) child;

          childCell.setStyle(childCell.getStyle() + "HIGHLIGHTED");
        }
      }
    }
    finally {
      getModel().endUpdate();
    }

    this.refresh();
  }

  public Set<Object> getChildrenOfCell(final mxCell cell) {
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

    return children;
  }

  /**
   * Zooms into the graph.
   */
  public void zoomIn() {
    graphComponent.zoomIn();
  }

  /**
   * Zooms out of the graph.
   */
  public void zoomOut() {
    graphComponent.zoomOut();
  }
}
