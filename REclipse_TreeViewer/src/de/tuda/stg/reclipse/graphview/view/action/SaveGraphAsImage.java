package de.tuda.stg.reclipse.graphview.view.action;

import de.tuda.stg.reclipse.graphview.Activator;
import de.tuda.stg.reclipse.graphview.Images;
import de.tuda.stg.reclipse.graphview.Texts;
import de.tuda.stg.reclipse.graphview.view.graph.TreeViewGraph;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPartSite;

import com.mxgraph.util.mxCellRenderer;

/**
 * Provides the action to save the current graph as an image.
 */
public class SaveGraphAsImage extends Action {

  private final IWorkbenchPartSite site;
  private final TreeViewGraph graph;

  public SaveGraphAsImage(final IWorkbenchPartSite ps, final TreeViewGraph graph) {
    this.site = ps;
    this.graph = graph;

    setText(Texts.SaveImage_Text);
    setToolTipText(Texts.SaveImage_Tooltip);
    setImageDescriptor(Activator.getImageDescriptor(Images.EXPORT_FILE));
  }

  @Override
  public void run() {
    final FileDialog dialog = new FileDialog(site.getShell(), SWT.SAVE);
    dialog.setFilterExtensions(new String[] {
      "*.png" //$NON-NLS-1$
    });
    final String path = dialog.open();
    final BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);
    try {
      ImageIO.write(image, "PNG", new File(path)); //$NON-NLS-1$
    }
    catch (final IOException e) {
      // display error message
      MessageDialog.openInformation(site.getShell(), "", Texts.SaveImage_Error); //$NON-NLS-1$
    }

    MessageDialog.openInformation(site.getShell(), "", Texts.SaveImage_Result); //$NON-NLS-1$
  }
}
