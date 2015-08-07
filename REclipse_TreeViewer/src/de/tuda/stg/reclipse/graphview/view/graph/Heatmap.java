package de.tuda.stg.reclipse.graphview.view.graph;

import de.tuda.stg.reclipse.logger.ReactiveVariable;

import de.tuda.stg.reclipse.graphview.model.SessionContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 *
 */
public class Heatmap {

  /**
   * Generates a heatmap for the specified point in time. The heatmap maps from
   * names of reactive variables to HEX color codes.
   *
   * @param lastPointInTime
   *          Point in time for which the heatmap should be generated.
   * @return A map of names and color codes.
   */
  public static Map<String, String> generateHeatmap(final int lastPointInTime, final SessionContext ctx) {
    final Map<String, String> heatmap = new HashMap<>();

    // calculate change map
    final Map<String, Integer> changes = calculateChangeMap(lastPointInTime, ctx);

    // find the maxmimum
    int maximum = 0;
    for (final String name : changes.keySet()) {
      final Integer value = changes.get(name);

      if (value > maximum) {
        maximum = value;
      }
    }

    // generate heatmap
    for (final String name : changes.keySet()) {
      final Integer value = changes.get(name);

      final String color = calculateColor(value, maximum);

      heatmap.put(name, color);
    }

    return heatmap;
  }

  /**
   * Calculates a map of names of reactive variables mapping to the amount of
   * changes until the specified point in time.
   *
   * @param lastPointInTime
   *          Point in time for which the change map should be calculated.
   * @return A map of names and change counters.
   */
  public static Map<String, Integer> calculateChangeMap(final int lastPointInTime, final SessionContext ctx) {
    final Map<String, Integer> changes = new HashMap<>();

    final Map<String, String> values = new HashMap<>();

    // iterate through points in time
    for (int pointInTime = 0; pointInTime < lastPointInTime; pointInTime++) {
      // get reactive variables for current point in time
      final List<ReactiveVariable> currentReVars = ctx.getPersistence().getReVars(pointInTime);

      for (final ReactiveVariable reVar : currentReVars) {
        if (reVar == null) {
          continue;
        }

        final String name = reVar.getName();

        // create non-empty string from value
        String value = reVar.getValueString();
        if (value == null) {
          value = new String();
        }

        // update change map
        if (values.containsKey(name)) {
          if (!values.get(name).equals(value)) {
            changes.put(name, changes.get(name) + 1);
          }
        }
        else {
          changes.put(name, 0);
        }

        // save count
        values.put(name, value);
      }
    }

    return changes;
  }

  /**
   * Calculates a "heat" color for a given value and a maximum value.
   *
   * @param value
   *          A value.
   * @param max
   *          The maximum value in the range.
   * @return A HEX color code string.
   */
  private static String calculateColor(final int value, final int max) {
    // calculate normalized value
    final double norm = (value * 1.0f) / max;

    // upper limit color
    final int rh = 255;
    final int rl = 240;
    final int gh = 237;

    // lower limit color
    final int gl = 59;
    final int bh = 160;
    final int bl = 32;

    // calculate color
    final int r = (int) (rl + (rh - rl) * norm);
    final int g = (int) (gl + (gh - gl) * norm);
    final int b = (int) (bl + (bh - bl) * norm);

    return String.format("#%02x%02x%02x", r, g, b); //$NON-NLS-1$
  }
}
