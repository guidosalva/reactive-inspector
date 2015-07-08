package de.tu_darmstadt.stg.reclipse.graphview.view.graph;

import java.util.Hashtable;

import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;

/**
 *
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 *
 */
public class Stylesheet extends mxStylesheet {

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

  public Stylesheet() {
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
    varStyle.put(mxConstants.STYLE_FILLCOLOR, "#00DAF5"); //$NON-NLS-1$
    varStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#00C3DB"); //$NON-NLS-1$
    varStyle.put(mxConstants.STYLE_STROKECOLOR, "#00A1B5"); //$NON-NLS-1$
    putCellStyle(Styles.VAR.name(), varStyle);

    // set style for SIGNAL vertices
    final Hashtable<String, Object> signalStyle = new Hashtable<>(baseStyle);
    signalStyle.put(mxConstants.STYLE_FILLCOLOR, "#DBFFB8"); //$NON-NLS-1$
    signalStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#CCFF99"); //$NON-NLS-1$
    signalStyle.put(mxConstants.STYLE_STROKECOLOR, "#B8E68A"); //$NON-NLS-1$
    putCellStyle(Styles.SIGNAL.name(), signalStyle);

    // set style for EVENT vertices
    final Hashtable<String, Object> eventStyle = new Hashtable<>(baseStyle);
    eventStyle.put(mxConstants.STYLE_FILLCOLOR, "#FCFF9A"); //$NON-NLS-1$
    eventStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#E3E58B"); //$NON-NLS-1$
    eventStyle.put(mxConstants.STYLE_STROKECOLOR, "#BDBF73"); //$NON-NLS-1$
    putCellStyle(Styles.EVENT.name(), eventStyle);

    // set style for EVENT_HANDLER vertices
    final Hashtable<String, Object> eventHandlerStyle = new Hashtable<>(baseStyle);
    eventHandlerStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFC4A4"); //$NON-NLS-1$
    eventHandlerStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#E5B194"); //$NON-NLS-1$
    eventHandlerStyle.put(mxConstants.STYLE_STROKECOLOR, "#BF937B"); //$NON-NLS-1$
    putCellStyle(Styles.EVENTHANDLER.name(), eventHandlerStyle);
  }

  private void addCustomHighlightStyles() {
    // set style for highlighted SIGNAL vertices
    final Hashtable<String, Object> highlightedSignalStyle = new Hashtable<>(baseStyle);
    highlightedSignalStyle.put(mxConstants.STYLE_FILLCOLOR, "#FF704D"); //$NON-NLS-1$
    highlightedSignalStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#FF3300"); //$NON-NLS-1$
    highlightedSignalStyle.put(mxConstants.STYLE_STROKECOLOR, "#E62E00"); //$NON-NLS-1$

    putCellStyle(Styles.SIGNAL_HIGHLIGHT.name(), highlightedSignalStyle);
    putCellStyle(Styles.VAR_HIGHLIGHT.name(), highlightedSignalStyle);
    putCellStyle(Styles.EVENT_HIGHLIGHT.name(), highlightedSignalStyle);
    putCellStyle(Styles.EVENTHANDLER_HIGHLIGHT.name(), highlightedSignalStyle);
  }

  /**
   * Darkens a color string by a given factor.
   *
   * @param colorStr
   *          A color string.
   * @param factor
   *          The factor by which to darken it.
   * @return A darkened color string.
   */
  public static String darkenColor(final String colorStr, final float factor) {
    final int r = Integer.valueOf(colorStr.substring(1, 3), 16);
    final int b = Integer.valueOf(colorStr.substring(3, 5), 16);
    final int g = Integer.valueOf(colorStr.substring(5, 7), 16);

    int rs = (int) (r * (1 + factor));
    int gs = (int) (g * (1 + factor));
    int bs = (int) (b * (1 + factor));

    rs = rs > 255 ? 255 : rs;
    gs = gs > 255 ? 255 : gs;
    bs = bs > 255 ? 255 : bs;

    return String.format("#%02x%02x%02x", rs, gs, bs); //$NON-NLS-1$
  }

  /**
   * Calculates an appropriate font color for a given background color.
   *
   * @param colorStr
   *          Background color.
   * @return A font color string.
   */
  public static String calculateFontColor(final String colorStr) {
    final int r = Integer.valueOf(colorStr.substring(1, 3), 16);
    final int b = Integer.valueOf(colorStr.substring(3, 5), 16);
    final int g = Integer.valueOf(colorStr.substring(5, 7), 16);

    /*
     * Uses perceptive luminance to decide whether to use black or white
     * font.
     *
     * Source for formula: http://stackoverflow.com/a/1855903/1080221
     */

    final double a = 1 - (0.299f * r + 0.587f * g + 0.114 * b) / 255;

    int d = 0;

    if (a >= 0.5f) {
      d = 255;
    }

    return String.format("#%02x%02x%02x", d, d, d); //$NON-NLS-1$
  }

  /**
   * Generates a style string from a HEX color string.
   *
   * @param color
   *          HEX color string.
   * @return A style string for JXGraph components.
   */
  public static String calculateStyleFromColor(final String color) {
    String style = mxConstants.STYLE_ROUNDED + "=1;"; //$NON-NLS-1$

    style += mxConstants.STYLE_FONTCOLOR + "=" + calculateFontColor(color) + ";"; //$NON-NLS-1$ //$NON-NLS-2$
    style += mxConstants.STYLE_FILLCOLOR + "=" + color + ";"; //$NON-NLS-1$ //$NON-NLS-2$
    style += mxConstants.STYLE_GRADIENTCOLOR + "=" + darkenColor(color, 0.85f) + ";"; //$NON-NLS-1$ //$NON-NLS-2$
    style += mxConstants.STYLE_STROKECOLOR + "=" + darkenColor(color, 0.75f); //$NON-NLS-1$

    return style;
  }
}
