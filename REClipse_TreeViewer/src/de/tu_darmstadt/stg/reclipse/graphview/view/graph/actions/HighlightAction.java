package de.tu_darmstadt.stg.reclipse.graphview.view.graph.actions;

import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.IGraphListener;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.TreeViewGraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.ImageIcon;
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
    final JMenuItem item = new JMenuItem();

    // set text to label
    item.setText(createLabelForCell(cell));

    // load icon
    final ImageIcon icon = new ImageIcon(getClass().getResource(Images.HIGHLIGHT.getPath()));
    item.setIcon(icon);

    item.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        highlightCell(cell);
      }
    });

    return item;
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
   * Builds a label based on whether a cell is highlighted.
   *
   * @param cell
   *          A cell in the graph.
   * @return A label String.
   */
  private String createLabelForCell(final mxCell cell) {
    return isHighlighted(cell) ? Texts.MenuItem_Highlighter_RemoveHighlight : Texts.MenuItem_Highlighter_Highlight;
  }

  /**
   * Highlights a cell in the graph.
   *
   * @param cell
   *          A cell in the graph.
   */
  public void highlightCell(final mxCell cell) {
    graph.getModel().beginUpdate();
    try {
      if (isHighlighted(cell)) {
        graph.resetNodes();

        highlightedCell = null;
      }
      else {
        final Set<Object> nodes = graph.getChildrenOfCell(cell);
        nodes.add(cell);
        graph.foregoundNodes(nodes);

        highlightedCell = cell;
      }
    }
    finally {
      graph.getModel().endUpdate();
    }

    graph.refresh();
  }
}
