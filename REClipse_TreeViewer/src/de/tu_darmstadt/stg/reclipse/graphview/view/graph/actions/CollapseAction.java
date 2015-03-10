package de.tu_darmstadt.stg.reclipse.graphview.view.graph.actions;

import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.CustomGraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import com.mxgraph.model.mxCell;

/**
 * 
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 * 
 */
public class CollapseAction {

  private final CustomGraph graph;

  private final Map<mxCell, Set<Object>> collapsed;

  public CollapseAction(final CustomGraph g) {
    super();
    this.graph = g;

    collapsed = new HashMap<>();
  }

  /**
   * Creates a menu item for the cell.
   * 
   * @param cell
   *          A cell in the graph.
   * @return JMenuItem instance
   */
  public JMenuItem createMenuItem(final mxCell cell) {
    final JMenuItem item = new JMenuItem();

    // set text to label
    item.setText(createLabelForCell(cell));

    // load icon
    final ImageIcon icon = new ImageIcon(getClass().getResource(Images.COLLAPSE.getPath()));
    item.setIcon(icon);

    item.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        collapseCell(cell);
      }
    });

    return item;
  }

  /**
   * Checks if a cell is currently collapsed.
   * 
   * @param cell
   *          A cell in the graph.
   * @return True, if the cell is collapsed. False, otherwise.
   */
  private boolean isCollapsed(final mxCell cell) {
    return collapsed.containsKey(cell);
  }

  /**
   * Gets the amount of children of the cell in the graph.
   * 
   * @param cell
   *          A cell in the graph.
   * @return Amount of the cell's children.
   */
  private int getAmountOfChildren(final mxCell cell) {
    return collapsed.containsKey(cell) ? collapsed.get(cell).size() : 0;
  }

  /**
   * Builds a label based on whether a cell is collapsed.
   * 
   * @param cell
   *          A cell in the graph.
   * @return A label String.
   */
  private String createLabelForCell(final mxCell cell) {
    String label;

    if (isCollapsed(cell)) {
      label = Texts.MenuItem_Collapse_Unfold;

      final int amountOfChildren = getAmountOfChildren(cell);

      if (amountOfChildren > 0) {
        label += " (" + amountOfChildren; //$NON-NLS-1$

        if (amountOfChildren == 1) {
          label += " cell"; //$NON-NLS-1$
        }
        else {
          label += " cells"; //$NON-NLS-1$
        }

        label += ")"; //$NON-NLS-1$
      }
    }
    else {
      label = Texts.MenuItem_Collapse_Fold;
    }

    return label;
  }

  /**
   * Collapses a cell in the graph.
   * 
   * @param cell
   *          A cell in the graph.
   */
  public void collapseCell(final mxCell cell) {
    graph.getModel().beginUpdate();
    try {
      // cell collapsed?
      if (collapsed.containsKey(cell)) {
        // show cells
        graph.toggleCells(true, collapsed.get(cell).toArray(), true);

        // remove cell from collapsed map
        collapsed.remove(cell);
      }
      else {
        final Set<Object> children = graph.getChildrenOfCell(cell);

        // add to collapsed map
        collapsed.put(cell, children);

        // hide cells
        graph.toggleCells(false, collapsed.get(cell).toArray(), true);
      }
    }
    finally {
      graph.getModel().endUpdate();
    }
  }

}
