package de.tuda.stg.reclipse.graphview.view.graph;

import de.tuda.stg.reclipse.graphview.view.graph.actions.BreakpointAction;
import de.tuda.stg.reclipse.graphview.view.graph.actions.CollapseAction;
import de.tuda.stg.reclipse.graphview.view.graph.actions.HighlightAction;
import de.tuda.stg.reclipse.graphview.view.graph.actions.LocateAction;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Set;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.eclipse.core.runtime.Platform;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;

public class GraphComponent extends mxGraphComponent {

  private static final long serialVersionUID = 1L;

  private final TreeViewGraph graph;

  protected final CollapseAction collapser;
  protected final HighlightAction highlighter;
  protected BreakpointAction breakpointer;
  protected LocateAction locater;

  private String searchQuery;
  private Set<mxCell> searchResults;

  public GraphComponent(final TreeViewGraph graph) {
    super(graph);

    this.graph = graph;

    setToolTips(true);
    setDoubleBuffered(true);
    // prevent auto scroll if cursor is dragged out of bounds
    setAutoScroll(false);
    setWheelScrollingEnabled(false);

    collapser = new CollapseAction(graph);
    highlighter = new HighlightAction(graph);
    breakpointer = new BreakpointAction(graph);
    locater = new LocateAction(graph);

    addMouseListener();
  }

  private void addMouseListener() {
    getGraphControl().addMouseListener(new PopupMouseAdapter());
    getGraphControl().addMouseListener(new ClickMouseAdapter());

    final ScrollMouseAdapter scrollAdapter = new ScrollMouseAdapter();
    getGraphControl().addMouseListener(scrollAdapter);
    getGraphControl().addMouseMotionListener(scrollAdapter);

    addMouseWheelListener(new ZoomMouseAdapter());
  }

  public void searchNodes(final String query) {
    searchQuery = query;
    searchResults = graph.searchNodes(query);

    highlightSearchResult();
  }

  private void highlightSearchResult() {
    if (searchResults == null) {
      return;
    }

    graph.getModel().beginUpdate();
    try {
      graph.highlightSearchResults(searchResults);
    }
    finally {
      graph.getModel().endUpdate();
    }

    graph.refresh();
  }

  public boolean clearSearch() {
    if (!hasSearchResult()) {
      return false;
    }

    searchQuery = null;
    searchResults = null;
    resetSearchHighlighting();

    return true;
  }

  private void resetSearchHighlighting() {
    graph.getModel().beginUpdate();
    try {
      graph.resetSearchHighlighting();
    }
    finally {
      graph.getModel().endUpdate();
    }

    graph.refresh();
  }

  public boolean hasSearchResult() {
    return searchResults != null;
  }

  public int getSearchResultCount() {
    if (!hasSearchResult()) {
      return 0;
    }

    return searchResults.size();
  }

  protected class PopupMouseAdapter extends MouseAdapter {

    @Override
    public void mouseClicked(final MouseEvent e) {
      if (!SwingUtilities.isRightMouseButton(e)) {
        return;
      }

      // find clicked cell
      final mxCell cell = (mxCell) getCellAt(e.getX(), e.getY());

      // if no cell has been clicked, return
      if (cell == null) {
        return;
      }

      final JPopupMenu popupMenu = new JPopupMenu();

      // add menu items
      popupMenu.add(collapser.createMenuItem(cell));
      popupMenu.add(highlighter.createMenuItem(cell));
      popupMenu.addSeparator();
      popupMenu.add(breakpointer.createMenuItem(cell));
      popupMenu.add(locater.createMenuItem(cell));

      // take scrolling offset into account
      final Point pos = getViewport().getViewPosition();
      final int x = e.getX() - (int) pos.getX();
      final int y = e.getY() - (int) pos.getY();

      // show popup menu on clicked point
      popupMenu.show(GraphComponent.this, x, y);
    }
  }

  protected class ScrollMouseAdapter extends MouseAdapter {

    private int lastX;
    private int lastY;

    @Override
    public void mousePressed(final MouseEvent e) {
      lastX = e.getXOnScreen();
      lastY = e.getYOnScreen();
      getParent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
      getParent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
      // calculate dragging
      final int dx = e.getXOnScreen() - lastX;
      final int dy = e.getYOnScreen() - lastY;

      // scroll to new position
      final Point pos = getViewport().getViewPosition();
      pos.translate(-dx, -dy);
      final Rectangle rec = new Rectangle(pos, getViewport().getSize());
      getGraphControl().scrollRectToVisible(rec);

      lastX = e.getXOnScreen();
      lastY = e.getYOnScreen();
    }
  }

  protected class ZoomMouseAdapter extends MouseAdapter {

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
      if (e.isShiftDown()) {
        // do nothing on vertical scroll
        return;
      }

      if (e.getWheelRotation() < 0) {
        zoomIn();
      }
      else if (e.getWheelRotation() > 0) {
        zoomOut();
      }
    }
  }

  protected class ClickMouseAdapter extends MouseAdapter {

    @Override
    public void mouseClicked(final MouseEvent e) {
      if (!SwingUtilities.isLeftMouseButton(e)) {
        return;
      }

      if (isLocateShortcut(e)) {
        final mxCell cell = (mxCell) getCellAt(e.getX(), e.getY());

        if (cell != null) {
          locater.locate(cell);
        }
      }
    }

    private boolean isLocateShortcut(final MouseEvent e) {
      final int keyMask = Platform.getOS().equals(Platform.OS_MACOSX) ? InputEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;

      return (e.getModifiersEx() & keyMask) == keyMask;
    }
  }

}
