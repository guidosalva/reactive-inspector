package de.tu_darmstadt.stg.reclipse.graphview.view;

import java.util.Hashtable;

import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;

/**
 * 
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 * 
 */
public class CustomGraphStylesheet extends mxStylesheet {

  public CustomGraphStylesheet() {
    super();

    addCustomStyles();
  }

  private void addCustomStyles() {
    // set base style
    final Hashtable<String, Object> baseStyle = new Hashtable<>();

    // set style for VAR vertices
    final Hashtable<String, Object> varStyle = new Hashtable<>(baseStyle);
    varStyle.put(mxConstants.STYLE_FILLCOLOR, "#FF1919"); //$NON-NLS-1$
    varStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#FF6666"); //$NON-NLS-1$
    varStyle.put(mxConstants.STYLE_STROKECOLOR, "#FF1919"); //$NON-NLS-1$
    varStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000"); //$NON-NLS-1$
    varStyle.put(mxConstants.STYLE_ROUNDED, "1"); //$NON-NLS-1$
    putCellStyle("VAR", varStyle); //$NON-NLS-1$

    // set style for SIGNAL vertices
    final Hashtable<String, Object> signalStyle = new Hashtable<>(baseStyle);
    signalStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFA347"); //$NON-NLS-1$
    signalStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#FFB870"); //$NON-NLS-1$
    signalStyle.put(mxConstants.STYLE_STROKECOLOR, "#FFA347"); //$NON-NLS-1$
    signalStyle.put(mxConstants.STYLE_ROUNDED, "1"); //$NON-NLS-1$
    signalStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000"); //$NON-NLS-1$
    putCellStyle("SIGNAL", signalStyle); //$NON-NLS-1$

    // set style for EVENT vertices
    final Hashtable<String, Object> eventStyle = new Hashtable<>(baseStyle);
    eventStyle.put(mxConstants.STYLE_FILLCOLOR, "#85FF5C"); //$NON-NLS-1$
    eventStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#A3FF85"); //$NON-NLS-1$
    eventStyle.put(mxConstants.STYLE_STROKECOLOR, "#66FF33"); //$NON-NLS-1$
    eventStyle.put(mxConstants.STYLE_ROUNDED, "1"); //$NON-NLS-1$
    eventStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000"); //$NON-NLS-1$
    putCellStyle("EVENT", eventStyle); //$NON-NLS-1$

    // set style for EVENT_HANDLER vertices
    final Hashtable<String, Object> eventHandlerStyle = new Hashtable<>(baseStyle);
    eventHandlerStyle.put(mxConstants.STYLE_FILLCOLOR, "#85FF5C"); //$NON-NLS-1$
    eventHandlerStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#A3FF85"); //$NON-NLS-1$
    eventHandlerStyle.put(mxConstants.STYLE_STROKECOLOR, "#66FF33"); //$NON-NLS-1$
    eventHandlerStyle.put(mxConstants.STYLE_ROUNDED, "1"); //$NON-NLS-1$
    eventStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000"); //$NON-NLS-1$
    putCellStyle("EVENT_HANDLER", eventHandlerStyle); //$NON-NLS-1$

    // set edge style
    final Hashtable<String, Object> edgeStyle = new Hashtable<>();
    edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000"); //$NON-NLS-1$
    edgeStyle.put(mxConstants.STYLE_STROKEWIDTH, "2"); //$NON-NLS-1$
    putCellStyle("EDGE", edgeStyle); //$NON-NLS-1$
  }
}
