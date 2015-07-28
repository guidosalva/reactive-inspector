package de.tu_darmstadt.stg.reclipse.graphview.view.graph.actions;

import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.IGraphListener;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.TreeViewGraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.mxgraph.model.mxCell;

/**
 *
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 *
 */
public class HighlightAction {

  private final TreeViewGraph graph;

  protected mxCell highlightedCell;

  public HighlightAction(final TreeViewGraph g) {
    super();
    this.graph = g;

    graph.addGraphListener(new IGraphListener() {

      @Override
      public void onGraphChanged() {
        highlightedCell = null;
      }
    });
  }

  /**
   * Creates a menu item for the cell.
   *
   * @param cell
   *          A cell in the graph.
   * @return JMenuItem instance
   */
  public JMenuItem createMenuItem(final mxCell cell) {
    return isHighlighted(cell) ? createRemoveMenuItem() : createHighlightMenuItem(cell);
  }

  private JMenuItem createRemoveMenuItem() {
    final JMenuItem item = new JMenuItem();

    // set text to label
    item.setText(Texts.MenuItem_Highlighter_RemoveHighlight);

    // load icon
    final ImageIcon icon = new ImageIcon(getClass().getResource(Images.HIGHLIGHT.getPath()));
    item.setIcon(icon);

    item.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        removeHighlighting();
      }
    });

    return item;
  }

  private JMenuItem createHighlightMenuItem(final mxCell cell) {
    final JMenu menu = new JMenu();

    // set text to label
    menu.setText(Texts.MenuItem_Highlighter_Highlight);

    // load icon
    final ImageIcon icon = new ImageIcon(getClass().getResource(Images.HIGHLIGHT.getPath()));
    menu.setIcon(icon);

    final JMenuItem ancestorsItem = new JMenuItem();
    ancestorsItem.setText(Texts.MenuItem_Highlighter_Ancestors);
    ancestorsItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        highlightAncestorCells(cell);
      }
    });

    final JMenuItem childrenItem = new JMenuItem();
    childrenItem.setText(Texts.MenuItem_Highlighter_Children);
    childrenItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        highlightChildCells(cell);
      }
    });

    menu.add(ancestorsItem);
    menu.add(childrenItem);

    return menu;
  }

  /**
   * Checks if a cell is currently highlighted.
   *
   * @param cell
   *          A cell in the graph.
   * @return True, if the cell is highlighted. False, otherwise.
   */
  private boolean isHighlighted(final mxCell cell) {
    if (highlightedCell == null) {
      return false;
    }

    return cell.equals(highlightedCell);
  }

  /**
   * Remove the current highlighting.
   */
  public void removeHighlighting() {
    graph.getModel().beginUpdate();
    try {
      graph.resetNodes();

      highlightedCell = null;
    }
    finally {
      graph.getModel().endUpdate();
    }

    graph.refresh();
  }

  /**
   * Highlights all ancestor cells in the graph.
   *
   * @param cell
   *          A cell in the graph.
   */
  public void highlightAncestorCells(final mxCell cell) {
    graph.getModel().beginUpdate();
    try {
      final Set<Object> nodes = graph.getAncestorsOfCell(cell);
      nodes.add(cell);
      graph.foregoundNodes(nodes);

      highlightedCell = cell;
    }
    finally {
      graph.getModel().endUpdate();
    }

    graph.refresh();
  }

  /**
   * Highlights all child cells in the graph.
   *
   * @param cell
   *          A cell in the graph.
   */
  public void highlightChildCells(final mxCell cell) {
    graph.getModel().beginUpdate();
    try {
      final Set<Object> nodes = graph.getChildrenOfCell(cell);
      nodes.add(cell);
      graph.foregoundNodes(nodes);

      highlightedCell = cell;
    }
    finally {
      graph.getModel().endUpdate();
    }

    graph.refresh();
  }
}
