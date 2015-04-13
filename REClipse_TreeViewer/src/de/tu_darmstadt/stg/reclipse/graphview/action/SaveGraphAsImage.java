package de.tu_darmstadt.stg.reclipse.graphview.action;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.GraphContainer;

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
  private final GraphContainer graphContainer;

  public SaveGraphAsImage(final IWorkbenchPartSite ps, final GraphContainer graphContainer) {
    this.site = ps;
    this.graphContainer = graphContainer;

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
    if (graphContainer.containsGraph()) {
      final BufferedImage image = mxCellRenderer.createBufferedImage(graphContainer.getGraph(), null, 1, Color.WHITE, true, null);
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
}
