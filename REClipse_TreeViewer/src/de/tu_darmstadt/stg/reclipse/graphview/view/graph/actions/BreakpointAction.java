package de.tu_darmstadt.stg.reclipse.graphview.view.graph.actions;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.model.SessionContext;
import de.tu_darmstadt.stg.reclipse.graphview.util.BreakpointUtils;
import de.tu_darmstadt.stg.reclipse.graphview.view.ReactiveVariableLabel;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.CustomGraph;
import de.tu_darmstadt.stg.reclipse.logger.BreakpointInformation;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.debug.core.IJavaWatchpoint;

import com.mxgraph.model.mxCell;

/**
 *
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 *
 */
public class BreakpointAction {

  /**
   * Currently, the graph instance is not needed.
   */
  @SuppressWarnings("unused")
  private final CustomGraph graph;

  // Statuses of breakpoints
  private final Map<mxCell, Boolean> breakpointStatus;

  // Tracked watchpoints
  private final Map<mxCell, IJavaWatchpoint> watchpoints;

  private final SessionContext ctx;

  public BreakpointAction(final CustomGraph g, final SessionContext ctx) {
    super();
    this.graph = g;
    this.ctx = ctx;

    breakpointStatus = new HashMap<>();
    watchpoints = new HashMap<>();
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
        triggerBreakpoint(cell);
      }
    });

    return item;
  }

  /**
   * Activates (or deactivates) a breakpoint on a reactive variable given its
   * cell representation in the graph.
   *
   * @param cell
   *          A cell in the graph.
   */
  void triggerBreakpoint(final mxCell cell) {
    if (!breakpointStatus.containsKey(cell)) {
      breakpointStatus.put(cell, false);
    }

    final boolean isEnabled = breakpointStatus.get(cell);

    if (isEnabled) {
      final IJavaWatchpoint watchpoint = watchpoints.get(cell);

      try {
        watchpoint.delete();
      }
      catch (final CoreException e) {
        Activator.log(e);
      }
    }
    else {
      // get reactive variable from cell
      final ReactiveVariableLabel reVarLabel = (ReactiveVariableLabel) cell.getValue();
      final ReactiveVariable reVar = reVarLabel.getVar();

      // get breakpoint information from store
      final BreakpointInformation information = ctx.getBreakpointInformation(reVar);

      // create watchpoint
      final IJavaWatchpoint watchpoint = BreakpointUtils.createWatchpoint(information, reVar.getName());

      // save watchpoint
      if (watchpoint != null) {
        watchpoints.put(cell, watchpoint);
      }
    }

    breakpointStatus.put(cell, !isEnabled);
  }

  /**
   * Builds a label based on whether a cell has an active breakpoint.
   *
   * @param cell
   *          A cell in the graph.
   * @return A label String.
   */
  private String createLabelForCell(final mxCell cell) {
    return isActiveBreakpoint(cell) ? Texts.MenuItem_Breakpoint_Disable : Texts.MenuItem_Breakpoint_Enable;
  }

  /**
   * Checks whether a breakpoint has been set on a cell.
   *
   * @param cell
   *          A cell in the graph.
   * @return True iff a breakpoint has been set on the cell's reactive variable.
   */
  public boolean isActiveBreakpoint(final mxCell cell) {
    if (!breakpointStatus.containsKey(cell)) {
      return false;
    }

    return breakpointStatus.get(cell);
  }
}
