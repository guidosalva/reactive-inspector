package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.model.DatabaseHelper;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 * 
 */
public class Heatmap {

  public static Map<String, String> generateHeatmap(final int lastPointInTime) {
    final Map<String, String> heatmap = new HashMap<>();

    final Map<String, Integer> changes = calculateChangeMap(lastPointInTime);

    int maximum = 0;
    for (final String name : changes.keySet()) {
      final Integer value = changes.get(name);

      if (value > maximum) {
        maximum = value;
      }
    }

    for (final String name : changes.keySet()) {
      final Integer value = changes.get(name);

      final String color = calculateColor(value, maximum);

      heatmap.put(name, color);
    }

    return heatmap;
  }

  private static Map<String, Integer> calculateChangeMap(final int lastPointInTime) {
    final Map<String, Integer> changes = new HashMap<>();

    final Map<String, String> values = new HashMap<>();

    try {
      for (int pointInTime = 0; pointInTime < lastPointInTime; pointInTime++) {
        final List<ReactiveVariable> currentReVars = DatabaseHelper.getReVars(pointInTime);

        for (final ReactiveVariable reVar : currentReVars) {
          if (reVar == null) {
            continue;
          }

          final String name = reVar.getName();

          if (values.containsKey(name)) {
            if (!values.get(name).equals(reVar.getValueString())) {
              changes.put(name, changes.get(name) + 1);
            }
          }
          else {
            changes.put(name, 0);
          }

          values.put(name, reVar.getValueString());
        }
      }
    }
    catch (final Exception e) {
      System.out.println(e);
    }

    return changes;
  }

  private static String calculateColor(final int value, final int max) {
    // set minimum and maximum value
    final float minimum = 0.0f;
    final float maximum = max * 1.0f;

    // calculate ratio
    final float ratio = 2 * (value - minimum) / (maximum - minimum);

    // calculate color
    final int r = (int) Math.max(0, 255 * (1 - ratio));
    final int b = (int) Math.max(0, 255 * (ratio - 1));
    final int g = 255 - b - r;

    return String.format("#%02x%02x%02x", r, g, b); //$NON-NLS-1$
  }
}
