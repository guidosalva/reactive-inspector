package de.tuda.stg.reclipse.graphview.view.action;

import de.tuda.stg.reclipse.graphview.Activator;
import de.tuda.stg.reclipse.graphview.Images;
import de.tuda.stg.reclipse.graphview.Texts;
import de.tuda.stg.reclipse.graphview.javaextensions.FunctionalSelectionListener;
import de.tuda.stg.reclipse.graphview.util.ViewMode;
import de.tuda.stg.reclipse.graphview.view.graph.TreeViewGraph;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class ViewModeAction extends Action implements IMenuCreator {

  private final TreeViewGraph graph;
  private Menu menu;

  public ViewModeAction(final TreeViewGraph graph) {
    super("", IAction.AS_DROP_DOWN_MENU); //$NON-NLS-1$
    this.graph = graph;
    setToolTipText(Texts.ViewMode_ToolTip);
    setImageDescriptor(Activator.getImageDescriptor(Images.HEATMAP));
    setMenuCreator(this);
  }

  @FunctionalInterface
  interface Runnable {
    void run();
  }

  private void setupMenu() {
    final MenuItem defaultMenuItem = new MenuItem(menu, SWT.RADIO);
    defaultMenuItem.setText(Texts.DefaultViewMode);
    defaultMenuItem.setSelection(true);
    defaultMenuItem.addSelectionListener((FunctionalSelectionListener) (event) -> {
      graph.setViewMode(ViewMode.DEFAULT);
      graph.updateGraph();
    });

    final MenuItem relativePerformanceMenuItem = new MenuItem(menu, SWT.RADIO);
    relativePerformanceMenuItem.setText(Texts.RelativePerformanceViewMode);
    relativePerformanceMenuItem.addSelectionListener((FunctionalSelectionListener) (event) -> {
      graph.setViewMode(ViewMode.RELATIVE);
      graph.updateGraph();
    });

    final MenuItem absolutePerformanceLatestMenuItem = new MenuItem(menu, SWT.RADIO);
    absolutePerformanceLatestMenuItem.setText(Texts.AbsolutePerformanceLatestViewMode);
    absolutePerformanceLatestMenuItem.addSelectionListener((FunctionalSelectionListener) (event) -> {
      graph.setViewMode(ViewMode.ABSOLUTE_LATEST);
      graph.updateGraph();
    });

    final MenuItem absolutePerformanceSumMenuItem = new MenuItem(menu, SWT.RADIO);
    absolutePerformanceSumMenuItem.setText(Texts.AbsolutePerformanceSumViewMode);
    absolutePerformanceSumMenuItem.addSelectionListener((FunctionalSelectionListener) (event) -> {
      graph.setViewMode(ViewMode.ABSOLUTE_SUM);
      graph.updateGraph();
    });
  }

  @Override
  public void dispose() {
    if (menu != null) {
      menu.dispose();
      menu = null;
    }
  }

  @Override
  public Menu getMenu(final Control parent) {
    if (menu == null) {
      menu = new Menu(parent);
      setupMenu();
    }
    return menu;
  }

  @Override
  public Menu getMenu(final Menu parent) {
    throw new UnsupportedOperationException("The menu is designed to be opened by a button."); //$NON-NLS-1$
  }
}