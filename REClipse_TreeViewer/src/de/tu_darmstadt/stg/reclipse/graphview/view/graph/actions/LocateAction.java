package de.tu_darmstadt.stg.reclipse.graphview.view.graph.actions;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.model.SessionContext;
import de.tu_darmstadt.stg.reclipse.graphview.util.BreakpointUtils;
import de.tu_darmstadt.stg.reclipse.graphview.view.ReactiveVariableLabel;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.TreeViewGraph;
import de.tu_darmstadt.stg.reclipse.logger.BreakpointInformation;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.mxgraph.model.mxCell;

/**
 *
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 *
 */
public class LocateAction {

  private final TreeViewGraph graph;

  public LocateAction(final TreeViewGraph graph) {
    super();
    this.graph = graph;
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
      public void actionPerformed(final ActionEvent event) {
        try {
          final Optional<SessionContext> ctx = graph.getSessionContext();

          if (ctx.isPresent()) {
            openSourceCode(cell, ctx.get());
          }
        }
        catch (final CoreException e) {
          Activator.log(e);
        }
      }
    });

    return item;
  }

  void openSourceCode(final mxCell cell, final SessionContext ctx) throws CoreException {
    // get reactive variable from cell
    final ReactiveVariableLabel reVarLabel = (ReactiveVariableLabel) cell.getValue();
    final ReactiveVariable reVar = reVarLabel.getVar();

    // get breakpoint information from store
    final BreakpointInformation information = ctx.getVariableLocation(reVar.getId());
    if (information == null) {
      return;
    }

    // create file instances
    final IFile file = BreakpointUtils.findFile(information.getSourcePath());
    if (file == null) {
      return;
    }

    // breakpoints are 1 line ahead
    final int locationLine = information.getLineNumber() - 1;
    final IMarker marker = createMarker(file, locationLine);

    Display.getDefault().asyncExec(new Runnable() {

      @Override
      public void run() {
        // open editor with appropriate file
        try {
          final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
          IDE.openEditor(page, marker, true);
        }
        catch (final PartInitException e) {
          Activator.log(e);
        }
      }
    });
  }

  /**
   * Creates a text marker in the file on the given line number.
   *
   * @param file
   *          the file
   * @param lineNumber
   *          the line number
   * @return the text marker
   * @throws CoreException
   */
  private IMarker createMarker(final IFile file, final int lineNumber) throws CoreException {
    final IMarker marker = file.createMarker(IMarker.TEXT);
    marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
    return marker;
  }

  /**
   * Builds a label.
   *
   * @param cell
   *          A cell in the graph.
   * @return A label String.
   */
  private String createLabelForCell(final mxCell cell) {
    return Texts.MenuItem_Locate;
  }
}
