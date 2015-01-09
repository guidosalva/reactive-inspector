package de.tu_darmstadt.stg.reclipse.graphview.action;

import de.tu_darmstadt.stg.reclipse.graphview.view.StatisticsView;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

public class ShowStatistics implements IObjectActionDelegate {

  private IWorkbenchPartSite partSite;

  @Override
  public void run(final IAction action) {
    final IWorkbenchPage page = partSite.getPage();
    IViewPart part = page.findView(StatisticsView.ID);
    if (part == null) {
      try {
        part = page.showView(StatisticsView.ID);
      }
      catch (final PartInitException e) {
      }
    }
    else {
      page.bringToTop(part);
    }

    if (part != null) {
      final StatisticsView rtv = (StatisticsView) part;
    }
  }

  @Override
  public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
    partSite = targetPart.getSite();
  }

  @Override
  public void selectionChanged(final IAction action, final ISelection selection) {
    // do nothing for the time being
  }
}
