package de.tu_darmstadt.stg.reclipse.graphview.view.graph.actions;

import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.TreeViewGraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
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
public class HighlightAction {

  private final TreeViewGraph graph;

  private final Map<mxCell, Set<Object>> highlighted;

  public HighlightAction(final TreeViewGraph g) {
    super();
    this.graph = g;

    highlighted = new HashMap<>();
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
    return highlighted.containsKey(cell);
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
      // cell highlighted?
      if (highlighted.containsKey(cell)) {
        // remove cell from highlighted map
        highlighted.remove(cell);
      }
      else {
        final Set<Object> children = graph.getChildrenOfCell(cell);
        // add to highlighted map
        highlighted.put(cell, children);
      }

      final Set<Object> allHighlighted = new HashSet<>();

      for (final Set<Object> children : highlighted.values()) {
        allHighlighted.addAll(children);
      }

      graph.highlightNodes(allHighlighted);
    }
    finally {
      graph.getModel().endUpdate();
    }

    graph.refresh();
  }
}
