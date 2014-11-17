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

  private final Hashtable<String, Object> baseStyle;

  public enum Styles {
    VAR, SIGNAL, EVENT, EVENTHANDLER, VAR_HIGHLIGHT, SIGNAL_HIGHLIGHT, EVENT_HIGHLIGHT, EVENTHANDLER_HIGHLIGHT;

    public static Styles getHighlight(final Styles style) {
      switch (style) {
        case VAR:
          return Styles.VAR_HIGHLIGHT;
        case SIGNAL:
          return Styles.SIGNAL_HIGHLIGHT;
        case EVENT:
          return Styles.EVENT_HIGHLIGHT;
        case EVENTHANDLER:
          return Styles.EVENTHANDLER_HIGHLIGHT;
        default:
          return style;
      }
    }

    public static Styles getHighlight(final String style) {
      return getHighlight(Styles.valueOf(style));
    }

    public static Styles removeHighlight(final Styles style) {
      switch (style) {
        case VAR_HIGHLIGHT:
          return Styles.VAR;
        case SIGNAL_HIGHLIGHT:
          return Styles.SIGNAL;
        case EVENT_HIGHLIGHT:
          return Styles.EVENT;
        case EVENTHANDLER_HIGHLIGHT:
          return Styles.EVENTHANDLER;
        default:
          return style;
      }
    }

    public static Styles removeHighlight(final String style) {
      return removeHighlight(Styles.valueOf(style));
    }
  }

  public CustomGraphStylesheet() {
    super();

    // set base style
    baseStyle = new Hashtable<>();
    baseStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000"); //$NON-NLS-1$
    baseStyle.put(mxConstants.STYLE_ROUNDED, "1"); //$NON-NLS-1$

    // set edge style
    final Hashtable<String, Object> edgeStyle = new Hashtable<>();
    edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000"); //$NON-NLS-1$
    edgeStyle.put(mxConstants.STYLE_STROKEWIDTH, "2"); //$NON-NLS-1$
    putCellStyle("EDGE", edgeStyle); //$NON-NLS-1$

    addCustomStyles();
    addCustomHighlightStyles();
  }

  private void addCustomStyles() {
    // set style for VAR vertices
    final Hashtable<String, Object> varStyle = new Hashtable<>(baseStyle);
    varStyle.put(mxConstants.STYLE_FILLCOLOR, "#DBFF70"); //$NON-NLS-1$
    varStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#CCFF33"); //$NON-NLS-1$
    varStyle.put(mxConstants.STYLE_STROKECOLOR, "#B8E62E"); //$NON-NLS-1$
    putCellStyle(Styles.VAR.name(), varStyle);

    // set style for SIGNAL vertices
    final Hashtable<String, Object> signalStyle = new Hashtable<>(baseStyle);
    signalStyle.put(mxConstants.STYLE_FILLCOLOR, "#DBFFB8"); //$NON-NLS-1$
    signalStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#CCFF99"); //$NON-NLS-1$
    signalStyle.put(mxConstants.STYLE_STROKECOLOR, "#B8E68A"); //$NON-NLS-1$
    putCellStyle(Styles.SIGNAL.name(), signalStyle);

    // set style for EVENT vertices
    final Hashtable<String, Object> eventStyle = new Hashtable<>(baseStyle);
    eventStyle.put(mxConstants.STYLE_FILLCOLOR, "#FF4D94"); //$NON-NLS-1$
    eventStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#FF0066"); //$NON-NLS-1$
    eventStyle.put(mxConstants.STYLE_STROKECOLOR, "#E6005C"); //$NON-NLS-1$
    putCellStyle(Styles.EVENT.name(), eventStyle);

    // set style for EVENT_HANDLER vertices
    final Hashtable<String, Object> eventHandlerStyle = new Hashtable<>(baseStyle);
    eventHandlerStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFB8DB"); //$NON-NLS-1$
    eventHandlerStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#FF99CC"); //$NON-NLS-1$
    eventHandlerStyle.put(mxConstants.STYLE_STROKECOLOR, "#E68AB8"); //$NON-NLS-1$
    putCellStyle(Styles.EVENTHANDLER.name(), eventHandlerStyle);
  }

  private void addCustomHighlightStyles() {
    // set style for highlighted SIGNAL vertices
    final Hashtable<String, Object> highlightedSignalStyle = new Hashtable<>(baseStyle);
    highlightedSignalStyle.put(mxConstants.STYLE_FILLCOLOR, "#FF704D"); //$NON-NLS-1$
    highlightedSignalStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#FF3300"); //$NON-NLS-1$
    highlightedSignalStyle.put(mxConstants.STYLE_STROKECOLOR, "#E62E00"); //$NON-NLS-1$
    putCellStyle(Styles.SIGNAL_HIGHLIGHT.name(), highlightedSignalStyle);
    // VAR vertices should never be highlighted; added to make sure graph does
    // not break
    putCellStyle(Styles.VAR_HIGHLIGHT.name(), highlightedSignalStyle);
  }
}
