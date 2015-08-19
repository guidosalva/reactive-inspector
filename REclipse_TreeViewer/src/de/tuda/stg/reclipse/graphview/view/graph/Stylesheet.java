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

  private static final String VAR = "VAR"; //$NON-NLS-1$
  private static final String SIGNAL = "SIGNAL"; //$NON-NLS-1$
  private static final String EVENT = "EVENT"; //$NON-NLS-1$
  private static final String EVENT_HANDLER = "EVENT_HANDLER"; //$NON-NLS-1$
  private static final String GRAYED_OUT_POSTFIX = "_GRAY"; //$NON-NLS-1$
  private static final String HIGHLIGHT_VALUE_CHANGE = "HIGHLIGHT_VALUE_CHANGE"; //$NON-NLS-1$
  private static final String HIGHLIGHT_SEARCH_RESULT = "HIGHLIGHT_SEARCH_RESULT"; //$NON-NLS-1$

  private final Hashtable<String, Object> baseStyle;

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
    addAdditionalStyles();
  }

  private void addCustomStyles() {
    // set style for VAR vertices
    final Hashtable<String, Object> varStyle = new Hashtable<>(baseStyle);
    varStyle.put(mxConstants.STYLE_FILLCOLOR, "#00DAF5"); //$NON-NLS-1$
    varStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#00C3DB"); //$NON-NLS-1$
    varStyle.put(mxConstants.STYLE_STROKECOLOR, "#00A1B5"); //$NON-NLS-1$
    putCellStyle(VAR, varStyle);
    putCellStyle(VAR + GRAYED_OUT_POSTFIX, createGrayStyle(varStyle));

    // set style for SIGNAL vertices
    final Hashtable<String, Object> signalStyle = new Hashtable<>(baseStyle);
    signalStyle.put(mxConstants.STYLE_FILLCOLOR, "#DBFFB8"); //$NON-NLS-1$
    signalStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#CCFF99"); //$NON-NLS-1$
    signalStyle.put(mxConstants.STYLE_STROKECOLOR, "#B8E68A"); //$NON-NLS-1$
    putCellStyle(SIGNAL, signalStyle);
    putCellStyle(SIGNAL + GRAYED_OUT_POSTFIX, createGrayStyle(signalStyle));

    // set style for EVENT vertices
    final Hashtable<String, Object> eventStyle = new Hashtable<>(baseStyle);
    eventStyle.put(mxConstants.STYLE_FILLCOLOR, "#FCFF9A"); //$NON-NLS-1$
    eventStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#E3E58B"); //$NON-NLS-1$
    eventStyle.put(mxConstants.STYLE_STROKECOLOR, "#BDBF73"); //$NON-NLS-1$
    putCellStyle(EVENT, eventStyle);
    putCellStyle(EVENT + GRAYED_OUT_POSTFIX, createGrayStyle(eventStyle));

    // set style for EVENT_HANDLER vertices
    final Hashtable<String, Object> eventHandlerStyle = new Hashtable<>(baseStyle);
    eventHandlerStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFC4A4"); //$NON-NLS-1$
    eventHandlerStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#E5B194"); //$NON-NLS-1$
    eventHandlerStyle.put(mxConstants.STYLE_STROKECOLOR, "#BF937B"); //$NON-NLS-1$
    putCellStyle(EVENT_HANDLER, eventHandlerStyle);
    putCellStyle(EVENT_HANDLER + GRAYED_OUT_POSTFIX, createGrayStyle(eventHandlerStyle));
  }

  private void addAdditionalStyles() {
    final Hashtable<String, Object> highlightChangedStyle = new Hashtable<>();
    highlightChangedStyle.put(mxConstants.STYLE_FONTCOLOR, "#FF0000"); //$NON-NLS-1$
    highlightChangedStyle.put(mxConstants.STYLE_STROKECOLOR, "#FF0000"); //$NON-NLS-1$
    highlightChangedStyle.put(mxConstants.STYLE_STROKEWIDTH, 4);
    putCellStyle(HIGHLIGHT_VALUE_CHANGE, highlightChangedStyle);

    final Hashtable<String, Object> highlightSearchStyle = new Hashtable<>();
    highlightSearchStyle.put(mxConstants.STYLE_STROKECOLOR, "#0000FF"); //$NON-NLS-1$
    highlightSearchStyle.put(mxConstants.STYLE_STROKEWIDTH, 4);
    putCellStyle(HIGHLIGHT_SEARCH_RESULT, highlightSearchStyle);
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

  public static String getStyle(final ReactiveVariableLabel label) {
    String baseStyle = getBaseStyle(label);

    if (label.getStyleProperties().isGrayedOut()) {
      baseStyle += GRAYED_OUT_POSTFIX;
    }

    String style = baseStyle;

    if (label.getStyleProperties().isValueChanged()) {
      style += ";" + HIGHLIGHT_VALUE_CHANGE; //$NON-NLS-1$
    }

    if (label.getStyleProperties().isSearchResult()) {
      style += ";" + HIGHLIGHT_SEARCH_RESULT; //$NON-NLS-1$
    }

    return style;
  }

  private static String getBaseStyle(final ReactiveVariableLabel label) {
    switch (label.getVar().getReactiveVariableType()) {
      case EVENT:
        return EVENT;
      case EVENT_HANDLER:
        return EVENT_HANDLER;
      case SIGNAL:
        return SIGNAL;
      case VAR:
        return VAR;
      default:
        return ""; //$NON-NLS-1$
    }
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
