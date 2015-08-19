package de.tuda.stg.reclipse.graphview.view.graph.actions;

import de.tuda.stg.reclipse.graphview.Activator;
import de.tuda.stg.reclipse.graphview.Images;
import de.tuda.stg.reclipse.graphview.Texts;
import de.tuda.stg.reclipse.graphview.model.SessionContext;
import de.tuda.stg.reclipse.graphview.util.BreakpointUtils;
import de.tuda.stg.reclipse.graphview.view.graph.IGraphListener;
import de.tuda.stg.reclipse.graphview.view.graph.ReactiveVariableLabel;
import de.tuda.stg.reclipse.graphview.view.graph.TreeViewGraph;
import de.tuda.stg.reclipse.logger.BreakpointInformation;
import de.tuda.stg.reclipse.logger.ReactiveVariable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

  // Statuses of breakpoints
  protected final Map<mxCell, Boolean> breakpointStatus;

  // Tracked watchpoints
  protected final Map<mxCell, IJavaWatchpoint> watchpoints;

  protected final TreeViewGraph graph;

  public BreakpointAction(final TreeViewGraph graph) {
    super();
    this.graph = graph;

    breakpointStatus = new HashMap<>();
    watchpoints = new HashMap<>();

    graph.addGraphListener(new IGraphListener() {

      @Override
      public void onGraphChanged() {
        breakpointStatus.clear();
        watchpoints.clear();
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
    final ImageIcon icon = new ImageIcon(getClass().getResource('/' + Images.HIGHLIGHT.getPath()));
    item.setIcon(icon);

    item.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        final Optional<SessionContext> ctx = graph.getSessionContext();

        if (ctx.isPresent()) {
          triggerBreakpoint(cell, ctx.get());
        }
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
  void triggerBreakpoint(final mxCell cell, final SessionContext ctx) {
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
