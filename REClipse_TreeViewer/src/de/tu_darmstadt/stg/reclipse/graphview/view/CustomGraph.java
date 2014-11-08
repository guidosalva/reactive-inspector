package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.provider.ContentModel;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

/**
 * 
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 * 
 */
public class CustomGraph extends mxGraph {

  protected mxGraphComponent graphComponent;

  protected ContentModel contentModel;

  private final mxGraphLayout graphLayout;

  private double xTranslate;

  public CustomGraph(final Composite parent) {
    super();

    // create stylesheets
    createStylesheets();

    // create new content model
    contentModel = new ContentModel();

    // set graph layout
    graphLayout = new mxHierarchicalLayout(this, SwingConstants.WEST);

    // create child composite
    final Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.BACKGROUND);

    // set layout data to fill parent composite
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    // create frame inside composite
    final Frame graphFrame = SWT_AWT.new_Frame(composite);

    // initialize graph component and add it to frame
    graphComponent = new mxGraphComponent(this);
    graphComponent.setEnabled(false);

    graphFrame.add(graphComponent);

    // add listeners
    addMouseWheelListener();
    addMouseListener();

    updateGraph();
  }

  private void createStylesheets() {
    final mxStylesheet stylesheet = getStylesheet();

    // set base style
    final Hashtable<String, Object> baseStyle = new Hashtable<>();

    // set VAR style
    final Hashtable<String, Object> varStyle = new Hashtable<>(baseStyle);
    varStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFD633");
    varStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#FFE680");
    varStyle.put(mxConstants.STYLE_STROKECOLOR, "#FFCC00");
    varStyle.put(mxConstants.STYLE_ROUNDED, "1");
    stylesheet.putCellStyle("VAR", varStyle);

    // set SIGNAL style
    final Hashtable<String, Object> signalStyle = new Hashtable<>(baseStyle);
    signalStyle.put(mxConstants.STYLE_FILLCOLOR, "#FF8533");
    signalStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#FFA366");
    signalStyle.put(mxConstants.STYLE_STROKECOLOR, "#FF6600");
    signalStyle.put(mxConstants.STYLE_ROUNDED, "1");
    stylesheet.putCellStyle("SIGNAL", signalStyle);

    // set EVENT style
    final Hashtable<String, Object> eventStyle = new Hashtable<>(baseStyle);
    eventStyle.put(mxConstants.STYLE_FILLCOLOR, "#85FF5C");
    eventStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#A3FF85");
    eventStyle.put(mxConstants.STYLE_STROKECOLOR, "#66FF33");
    eventStyle.put(mxConstants.STYLE_ROUNDED, "1");
    stylesheet.putCellStyle("EVENT", eventStyle);

    // set EVENT_HANDLER style
    final Hashtable<String, Object> eventHandlerStyle = new Hashtable<>(baseStyle);
    eventHandlerStyle.put(mxConstants.STYLE_FILLCOLOR, "#85FF5C");
    eventHandlerStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#A3FF85");
    eventHandlerStyle.put(mxConstants.STYLE_STROKECOLOR, "#66FF33");
    eventHandlerStyle.put(mxConstants.STYLE_ROUNDED, "1");
    stylesheet.putCellStyle("EVENT_HANDLER", eventHandlerStyle);

    // set edge style
    final Hashtable<String, Object> edgeStyle = new Hashtable<>();
    edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
    edgeStyle.put(mxConstants.STYLE_STROKEWIDTH, "2");
    stylesheet.putCellStyle("EDGE", edgeStyle);
  }

  public void setPointInTime(final int pointInTime) {
    // set point in time in content model
    contentModel.setPointInTime(pointInTime);

    updateGraph();
  }

  private String determineStyle(final ReactiveVariable reVar) {
    switch (reVar.getReactiveVariableType()) {
      case VAR:
        return "VAR";
      case SIGNAL:
        return "SIGNAL";
      case EVENT:
        return "EVENT";
      case EVENT_HANDLER:
        return "EVENT_HANDLER";
      default:
        return "";
    }
  }

  public void updateGraph() {
    // remove cells, if any
    removeCells(getChildVertices(getDefaultParent()));

    // load vertices from content model
    final Map<Object, Object> vertices = contentModel.getVertices();

    // load edges from content model
    final Map<Object, Set<Object>> edges = contentModel.getEdges();

    // set default parent
    final Object defaultParent = getDefaultParent();

    // insert vertices
    final Map<Object, Object> mapping = new HashMap<>();
    for (final Object id : vertices.keySet()) {
      // get reactive variable
      final ReactiveVariable reVar = (ReactiveVariable) vertices.get(id);

      // create label
      final String label = reVar.getName() + "\n\n" + "Value: " + reVar.getValueString() + "\n" + "Type: " + reVar.getTypeSimple();

      // create vertex
      final Object vertex = insertVertex(defaultParent, id.toString(), label, 0, 0, 160, 80, determineStyle(reVar));

      // add vertex to mapping
      mapping.put(id, vertex);
    }

    // insert edges
    for (final Object id : edges.keySet()) {
      // get source vertex
      final Object sourceVertex = mapping.get(id);

      // get destinations
      final Set<Object> destinations = edges.get(id);

      for (final Object connectedId : destinations) {
        // get destination vertex
        final Object destinationVertex = mapping.get(connectedId);

        insertEdge(defaultParent, null, "", sourceVertex, destinationVertex, "EDGE"); //$NON-NLS-1$
      }
    }

    // calculate bounds for vertices (and edges)
    final mxRectangle cellBounds = getBoundsForCells(mapping.values().toArray(), true, true, true);

    // calculate x translation
    xTranslate = graphComponent.getWidth() / 2.0;
    if (cellBounds != null) {
      xTranslate -= (cellBounds.getWidth() / 2.0);
    }

    // execute layout
    getModel().beginUpdate();
    try {
      // execute layout
      graphLayout.execute(getDefaultParent());

      // center cells
      // moveCells(getChildCells(getDefaultParent(), true, true), xTranslate,
      // 20);
    }
    finally {
      getModel().endUpdate();
    }
  }

  private void addMouseWheelListener() {
    graphComponent.addMouseWheelListener(new MouseWheelListener() {

      @Override
      public void mouseWheelMoved(final MouseWheelEvent e) {
        // get rotation of mouse wheel
        final int steps = e.getWheelRotation();

        if (steps < 0) {
          // zoom in if mouse wheel scrolls up
          zoomIn();
        }
        else {
          // zoom out if mouse wheel scrolls down
          zoomOut();
        }
      }
    });
  }

  private void addMouseListener() {
    graphComponent.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(final MouseEvent e) {
        // TODO Implement vertex toggle
      }
    });
  }

  public void zoomIn() {
    graphComponent.zoomIn();
  }

  public void zoomOut() {
    graphComponent.zoomOut();
  }
}
