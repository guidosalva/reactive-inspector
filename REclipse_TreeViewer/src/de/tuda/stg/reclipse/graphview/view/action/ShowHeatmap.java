package de.tuda.stg.reclipse.graphview.view.action;

import de.tuda.stg.reclipse.graphview.Activator;
import de.tuda.stg.reclipse.graphview.Images;
import de.tuda.stg.reclipse.graphview.Texts;
import de.tuda.stg.reclipse.graphview.util.ViewMode;
import de.tuda.stg.reclipse.graphview.view.graph.TreeViewGraph;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class ShowHeatmap extends Action implements IMenuCreator {

  private final TreeViewGraph graph;
  private Menu menu;

  public ShowHeatmap(final TreeViewGraph graph) {
    super("", IAction.AS_DROP_DOWN_MENU);
    this.graph = graph;
    setToolTipText(Texts.Show_Heatmap_Tooltip);
    setImageDescriptor(Activator.getImageDescriptor(Images.HEATMAP));
    setMenuCreator(this);
    /*
    setText(Texts.Show_Heatmap);
     */
  }

  /*
  @Override
  public void run() {
    final boolean status = !graph.isHeatmapEnabled();

    if (status) {
      setImageDescriptor(Activator.getImageDescriptor(Images.HEATMAP));
    }
    else {
      setImageDescriptor(Activator.getImageDescriptor(Images.HEATMAP_GREY));
    }

    graph.setHeatmapEnabled(status);
    graph.updateGraph();
  }
   */

  private void setupMenu() {
    if (menu != null) {

      final MenuItem defaultMenuItem = new MenuItem(menu, SWT.RADIO);
      defaultMenuItem.setText("Default Settings");
      defaultMenuItem.addSelectionListener(new SelectionListener() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          graph.setViewMode(ViewMode.DEFAULT);
          graph.updateGraph();
        }

        @Override
        public void widgetDefaultSelected(final SelectionEvent event) {
          widgetSelected(event);
        }
      });

      //   final MenuItem separator = new MenuItem(menu, SWT.SEPARATOR);

      final MenuItem relativePerformanceMenuItem = new MenuItem(menu, SWT.RADIO);
      relativePerformanceMenuItem.setText("Show relative Performance");
      relativePerformanceMenuItem.addSelectionListener(new SelectionListener() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          graph.setViewMode(ViewMode.RELATIVE);
          graph.updateGraph();
        }

        @Override
        public void widgetDefaultSelected(final SelectionEvent event) {
          widgetSelected(event);
        }
      });

      final MenuItem absolutePerformanceMenuItem = new MenuItem(menu, SWT.RADIO);
      absolutePerformanceMenuItem.setText("Show absolute Performance");
      absolutePerformanceMenuItem.addSelectionListener(new SelectionListener() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          graph.setViewMode(ViewMode.ABSOLUTE);
          graph.updateGraph();
        }

        @Override
        public void widgetDefaultSelected(final SelectionEvent event) {
          widgetSelected(event);
        }
      });
    }
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
    throw new UnsupportedOperationException("the menu is designed to be opened by a button"); //$NON-NLS-1$
  }
}