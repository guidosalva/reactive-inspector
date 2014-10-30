package de.tu_darmstadt.stg.reclipse.graphview.action;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;

import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.Graph;

/**
 * Provides the action to save the current graph as an image.
 */
public class SaveGraphAsImage extends Action {

  private final IWorkbenchPartSite site;
  private final GraphViewer viewer;

  public SaveGraphAsImage(final IWorkbenchPartSite ps, final GraphViewer v) {
    site = ps;
    viewer = v;

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
    if (path != null) {
      final Graph g = viewer.getGraphControl();
      final Rectangle bounds = g.getContents().getBounds();
      final Point size = new Point(g.getContents().getSize().width, g.getContents().getSize().height);
      final org.eclipse.draw2d.geometry.Point viewLocation = g.getViewport().getViewLocation();
      final Image image = new Image(null, size.x, size.y);
      final GC gc = new GC(image);
      final SWTGraphics swtGraphics = new SWTGraphics(gc);
      swtGraphics.translate(-1 * bounds.x + viewLocation.x, -1 * bounds.y + viewLocation.y);
      g.getViewport().paint(swtGraphics);
      gc.copyArea(image, 0, 0);
      gc.dispose();
      final ImageLoader loader = new ImageLoader();
      loader.data = new ImageData[] {
              image.getImageData()
      };
      loader.save(path, SWT.IMAGE_PNG);

      MessageDialog.openInformation(site.getShell(), "", Texts.SaveImage_Result); //$NON-NLS-1$
    }
  }
}
