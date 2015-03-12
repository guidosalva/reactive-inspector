package de.tu_darmstadt.stg.reclipse.graphview.view.graph.actions;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.model.BreakpointInformationStore;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.CustomGraph;
import de.tu_darmstadt.stg.reclipse.logger.BreakpointInformation;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.mxgraph.model.mxCell;

/**
 *
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 *
 */
public class LocateAction {

  /**
   * Currently, the graph instance is not needed.
   */
  @SuppressWarnings("unused")
  private final CustomGraph graph;

  // Reference to the breakpoint information store
  private final BreakpointInformationStore store;

  public LocateAction(final CustomGraph g) {
    super();
    this.graph = g;

    store = BreakpointInformationStore.getInstance();
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
          openSourceCode(cell);
        }
        catch (final PartInitException e) {
          Activator.log(e);
        }
      }
    });

    return item;
  }

  void openSourceCode(final mxCell cell) throws PartInitException {
    // get reactive variable from cell
    final ReactiveVariable reVar = (ReactiveVariable) cell.getValue();

    // get breakpoint information from store
    final BreakpointInformation information = store.get(reVar);

    // create type and file instances
    final IFile file = createFile(information.getSourcePath());

    // open editor with appropriate file
    final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    org.eclipse.ui.ide.IDE.openEditor(page, file, true);
  }

  /**
   * Based on a file name, generates an appropriate file instance.
   *
   * @param fileName
   *          A file name.
   * @return An IFile instance.
   */
  private static IFile createFile(final String fileName) {
    // Get projects in current workspace
    final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

    IFile resource = null;
    for (final IProject project : projects) {
      final IFile file = project.getFile(fileName);

      if (file != null) {
        resource = file;
        break;
      }
    }
    return resource;
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
