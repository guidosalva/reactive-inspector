package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import com.mxgraph.model.mxCell;

public class GraphHighlighter {

  private final CustomGraph graph;

  private final Map<mxCell, Set<Object>> highlighted;

  public GraphHighlighter(final CustomGraph g) {
    super();
    this.graph = g;

    highlighted = new HashMap<>();
  }

  public JMenuItem createMenuItem(final mxCell cell) {
    final JMenuItem item = new JMenuItem();

    item.setText(createLabelForCell(cell));

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

  private boolean isHighlighted(final mxCell cell) {
    return highlighted.containsKey(cell);
  }

  private String createLabelForCell(final mxCell cell) {
    String label;

    if (isHighlighted(cell)) {
      label = Texts.MenuItem_Highlighter_RemoveHighlight;
    }
    else {
      label = Texts.MenuItem_Highlighter_Highlight;
    }

    return label;
  }

  public void highlightCell(final mxCell cell) {
    graph.getModel().beginUpdate();
    try {
      // cell highlighted?
      if (highlighted.containsKey(cell)) {
        for (final Object child : highlighted.get(cell)) {
          final mxCell childCell = (mxCell) child;

          childCell.setStyle(childCell.getStyle().replace("HIGHLIGHTED", "")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // remove cell from highlighted map
        highlighted.remove(cell);
      }
      else {
        final Set<Object> children = graph.getChildrenOfCell(cell);

        // add to highlighted mpa
        highlighted.put(cell, children);

        // remove highlight from cells
        for (final Object child : highlighted.get(cell)) {
          final mxCell childCell = (mxCell) child;

          childCell.setStyle(childCell.getStyle() + "HIGHLIGHTED"); //$NON-NLS-1$
        }
      }
    }
    finally {
      graph.getModel().endUpdate();
    }

    graph.refresh();
  }
}
