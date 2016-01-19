package de.tuda.stg.reclipse.graphview.view.graph;

import de.tuda.stg.reclipse.graphview.model.SessionContext;
import de.tuda.stg.reclipse.logger.ReactiveVariable;

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
   * @param pointInTime
   *          Point in time for which the heatmap should be generated.
   * @return A map of names and color codes.
   */
  public static Map<String, String> generateHeatmap(final Map<String, Long> values) {
    final long maximum = values.values().stream().max(Long::compare).orElse(1L).longValue();

    final Map<String, String> heatmap = new HashMap<>();

    for (final String name : values.keySet()) {
      final Long value = values.get(name);
      heatmap.put(name, color);
      final String color = calculateColor(value, maximum);
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
  public static Map<String, Long> calculateChangeMap(final int lastPointInTime, final SessionContext ctx) {
    final Map<String, Long> changes = new HashMap<>();

    final Map<String, String> values = new HashMap<>();

    // iterate through points in time
    for (int pointInTime = 0; pointInTime < lastPointInTime; pointInTime++) {
      // get reactive variables for current point in time
      final List<ReactiveVariable> currentReVars = ctx.getPersistence().getReVars(pointInTime);

      for (final ReactiveVariable reVar : currentReVars) {
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
          changes.put(name, 0L);
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
  private static String calculateColor(final long value, final long max) {
    // calculate normalized value
    final double norm = (value * 1.0) / max;

    // lower limit color
    final int rl = 240;
    final int gl = 237;
    final int bl = 160;

    // upper limit color
    final int rh = 255;
    final int gh = 59;
    final int bh = 32;

    // calculate color
    final int r = (int) (rl + (rh - rl) * norm);
    final int g = (int) (gl + (gh - gl) * norm);
    final int b = (int) (bl + (bh - bl) * norm);

    return String.format("#%02x%02x%02x", r, g, b); //$NON-NLS-1$
  }
}
