package de.tu_darmstadt.stg.reclipse.graphview.view.graph.actions;

import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.CustomGraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import com.mxgraph.model.mxCell;

public class Breakpointer {

  private final CustomGraph graph;

  private final Map<mxCell, Boolean> breakpointMap;

  public Breakpointer(final CustomGraph g) {
    super();
    this.graph = g;

    breakpointMap = new HashMap<>();
  }

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
        triggerBreakpoint(cell);
      }
    });

    return item;
  }

  void triggerBreakpoint(final mxCell cell) {
    if (!breakpointMap.containsKey(cell)) {
      breakpointMap.put(cell, false);
    }

    final boolean isEnabled = breakpointMap.get(cell);

    if (isEnabled) {

    }
    else {

    }

    breakpointMap.put(cell, !isEnabled);
  }

  /**
   * Builds a label based on whether a cell is highlighted.
   * 
   * @param cell
   *          A cell in the graph.
   * @return A label String.
   */
  private String createLabelForCell(final mxCell cell) {
    return isActiveBreakpoint(cell) ? Texts.MenuItem_Breakpoint_Disable : Texts.MenuItem_Breakpoint_Enable;
  }

  public boolean isActiveBreakpoint(final mxCell cell) {
    if (!breakpointMap.containsKey(cell)) {
      return false;
    }

    return breakpointMap.get(cell);
  }
}
