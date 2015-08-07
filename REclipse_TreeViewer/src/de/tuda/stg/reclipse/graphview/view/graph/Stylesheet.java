package de.tuda.stg.reclipse.graphview.view.graph;

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
    VAR, SIGNAL, EVENT, EVENTHANDLER, VAR_HIGHLIGHT, SIGNAL_HIGHLIGHT, EVENT_HIGHLIGHT, EVENTHANDLER_HIGHLIGHT, VAR_GRAY, SIGNAL_GRAY, EVENT_GRAY, EVENTHANDLER_GRAY;

    public static Styles getHighlight(final Styles style) {
      switch (style) {
        case VAR:
        case VAR_GRAY:
          return Styles.VAR_HIGHLIGHT;
        case SIGNAL:
        case SIGNAL_GRAY:
          return Styles.SIGNAL_HIGHLIGHT;
        case EVENT:
        case EVENT_GRAY:
          return Styles.EVENT_HIGHLIGHT;
        case EVENTHANDLER:
        case EVENTHANDLER_GRAY:
          return Styles.EVENTHANDLER_HIGHLIGHT;
        default:
          return style;
      }
    }

    public static Styles getHighlight(final String styleName) {
      return getHighlight(Styles.valueOf(styleName));
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

    public static Styles removeHighlight(final String styleName) {
      return removeHighlight(Styles.valueOf(styleName));
    }

    public static Styles getDisabled(final Styles style) {
      switch (style) {
        case VAR:
        case VAR_HIGHLIGHT:
          return Styles.VAR_GRAY;
        case SIGNAL:
        case SIGNAL_HIGHLIGHT:
          return Styles.SIGNAL_GRAY;
        case EVENT:
        case EVENT_HIGHLIGHT:
          return Styles.EVENT_GRAY;
        case EVENTHANDLER:
        case EVENTHANDLER_HIGHLIGHT:
          return Styles.EVENTHANDLER_GRAY;
        default:
          return style;
      }
    }

    public static Styles getDisabled(final String styleName) {
      return getDisabled(Styles.valueOf(styleName));
    }

    public static Styles getEnabled(final Styles style) {
      switch (style) {
        case VAR_GRAY:
          return Styles.VAR;
        case SIGNAL_GRAY:
          return Styles.SIGNAL;
        case EVENT_GRAY:
          return Styles.EVENT;
        case EVENTHANDLER_GRAY:
          return Styles.EVENTHANDLER;
        default:
          return style;
      }
    }

    public static Styles getEnabled(final String styleName) {
      return getEnabled(Styles.valueOf(styleName));
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
    // addCustomHighlightStyles();
  }

  private void addCustomStyles() {
    // set style for VAR vertices
    final Hashtable<String, Object> varStyle = new Hashtable<>(baseStyle);
    varStyle.put(mxConstants.STYLE_FILLCOLOR, "#00DAF5"); //$NON-NLS-1$
    varStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#00C3DB"); //$NON-NLS-1$
    varStyle.put(mxConstants.STYLE_STROKECOLOR, "#00A1B5"); //$NON-NLS-1$
    putCellStyle(Styles.VAR.name(), varStyle);
    putCellStyle(Styles.VAR_HIGHLIGHT.name(), createHighlightedStyle(varStyle));
    putCellStyle(Styles.VAR_GRAY.name(), createGrayStyle(varStyle));

    // set style for SIGNAL vertices
    final Hashtable<String, Object> signalStyle = new Hashtable<>(baseStyle);
    signalStyle.put(mxConstants.STYLE_FILLCOLOR, "#DBFFB8"); //$NON-NLS-1$
    signalStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#CCFF99"); //$NON-NLS-1$
    signalStyle.put(mxConstants.STYLE_STROKECOLOR, "#B8E68A"); //$NON-NLS-1$
    putCellStyle(Styles.SIGNAL.name(), signalStyle);
    putCellStyle(Styles.SIGNAL_HIGHLIGHT.name(), createHighlightedStyle(signalStyle));
    putCellStyle(Styles.SIGNAL_GRAY.name(), createGrayStyle(signalStyle));

    // set style for EVENT vertices
    final Hashtable<String, Object> eventStyle = new Hashtable<>(baseStyle);
    eventStyle.put(mxConstants.STYLE_FILLCOLOR, "#FCFF9A"); //$NON-NLS-1$
    eventStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#E3E58B"); //$NON-NLS-1$
    eventStyle.put(mxConstants.STYLE_STROKECOLOR, "#BDBF73"); //$NON-NLS-1$
    putCellStyle(Styles.EVENT.name(), eventStyle);
    putCellStyle(Styles.EVENT_HIGHLIGHT.name(), createHighlightedStyle(eventStyle));
    putCellStyle(Styles.EVENT_GRAY.name(), createGrayStyle(eventStyle));

    // set style for EVENT_HANDLER vertices
    final Hashtable<String, Object> eventHandlerStyle = new Hashtable<>(baseStyle);
    eventHandlerStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFC4A4"); //$NON-NLS-1$
    eventHandlerStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#E5B194"); //$NON-NLS-1$
    eventHandlerStyle.put(mxConstants.STYLE_STROKECOLOR, "#BF937B"); //$NON-NLS-1$
    putCellStyle(Styles.EVENTHANDLER.name(), eventHandlerStyle);
    putCellStyle(Styles.EVENTHANDLER_HIGHLIGHT.name(), createHighlightedStyle(eventHandlerStyle));
    putCellStyle(Styles.EVENTHANDLER_GRAY.name(), createGrayStyle(eventHandlerStyle));
  }

  private Hashtable<String, Object> createHighlightedStyle(final Hashtable<String, Object> style) {
    final Hashtable<String, Object> highlightedStyle = new Hashtable<>(style);
    highlightedStyle.put(mxConstants.STYLE_STROKECOLOR, "#FF0000"); //$NON-NLS-1$
    highlightedStyle.put(mxConstants.STYLE_STROKEWIDTH, 4);
    return highlightedStyle;
  }

  private Hashtable<String, Object> createGrayStyle(final Hashtable<String, Object> style) {
    final Hashtable<String, Object> grayStyle = new Hashtable<>(style);
    lightenColorProperty(grayStyle, mxConstants.STYLE_FILLCOLOR);
    lightenColorProperty(grayStyle, mxConstants.STYLE_GRADIENTCOLOR);
    lightenColorProperty(grayStyle, mxConstants.STYLE_STROKECOLOR);
    lightenColorProperty(grayStyle, mxConstants.STYLE_FONTCOLOR);
    return grayStyle;
  }

  private void lightenColorProperty(final Hashtable<String, Object> style, final String property) {
    final String original = (String) style.get(property);
    final String brightened = lightenColor(original, 0.75f);
    style.put(property, brightened);
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
    final int g = Integer.valueOf(colorStr.substring(3, 5), 16);
    final int b = Integer.valueOf(colorStr.substring(5, 7), 16);

    final int rs = (int) (r * (1 - factor));
    final int gs = (int) (g * (1 - factor));
    final int bs = (int) (b * (1 - factor));

    return String.format("#%02x%02x%02x", rs, gs, bs); //$NON-NLS-1$
  }

  /**
   * Brightens a color string by a given factor.
   *
   * @param colorStr
   *          A color string.
   * @param factor
   *          The factor by which to darken it.
   * @return A darkened color string.
   */
  public static String lightenColor(final String colorStr, final float factor) {
    final int r = Integer.valueOf(colorStr.substring(1, 3), 16);
    final int g = Integer.valueOf(colorStr.substring(3, 5), 16);
    final int b = Integer.valueOf(colorStr.substring(5, 7), 16);

    final int rs = (int) (r + (factor * (255 - r)));
    final int gs = (int) (g + (factor * (255 - g)));
    final int bs = (int) (b + (factor * (255 - b)));

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
    final int g = Integer.valueOf(colorStr.substring(3, 5), 16);
    final int b = Integer.valueOf(colorStr.substring(5, 7), 16);

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
    style += mxConstants.STYLE_GRADIENTCOLOR + "=" + darkenColor(color, 0.15f) + ";"; //$NON-NLS-1$ //$NON-NLS-2$
    style += mxConstants.STYLE_STROKECOLOR + "=" + darkenColor(color, 0.25f); //$NON-NLS-1$

    return style;
  }
}
