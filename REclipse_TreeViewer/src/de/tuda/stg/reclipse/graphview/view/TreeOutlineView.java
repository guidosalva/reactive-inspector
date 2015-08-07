package de.tuda.stg.reclipse.graphview.view;

import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;

public class TreeOutlineView extends ViewPart implements IPartListener {

  protected mxGraphOutline outline;

  @Override
  public void createPartControl(final Composite parent) {
    parent.setLayout(new GridLayout(1, true));

    final Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.BACKGROUND);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    final mxGraphComponent graphComponent = findGraphComponent();
    outline = new mxGraphOutline(graphComponent);

    final Frame graphFrame = SWT_AWT.new_Frame(composite);
    graphFrame.add(outline);

    getSite().getPage().addPartListener(this);
  }

  private mxGraphComponent findGraphComponent() {
    final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    final IViewPart view = page.findView(ReactiveTreeView.ID);
    if (!(view instanceof ReactiveTreeView)) {
      return null;
    }

    return ((ReactiveTreeView) view).graphComponent;
  }

  @Override
  public void setFocus() {
  }

  @Override
  public void partOpened(final IWorkbenchPart part) {
    if (part.getSite().getId().equals(ReactiveTreeView.ID)) {
      final mxGraphComponent graphComponent = ((ReactiveTreeView) part).graphComponent;
      outline.setGraphComponent(graphComponent);
    }
  }

  @Override
  public void partClosed(final IWorkbenchPart part) {
    if (part.getSite().getId().equals(ReactiveTreeView.ID)) {
      outline.setGraphComponent(null);
    }
  }

  @Override
  public void partActivated(final IWorkbenchPart arg0) {
  }

  @Override
  public void partBroughtToTop(final IWorkbenchPart arg0) {
  }

  @Override
  public void partDeactivated(final IWorkbenchPart arg0) {
  }
}
