package de.tu_darmstadt.stg.reclipse.graphview.view.graph;

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

  public static Map<String, Integer> calculateChangeMap(final int lastPointInTime) {
    final Map<String, Integer> changes = new HashMap<>();

    final Map<String, String> values = new HashMap<>();

    for (int pointInTime = 0; pointInTime < lastPointInTime; pointInTime++) {
      final List<ReactiveVariable> currentReVars = DatabaseHelper.getReVars(pointInTime);

      for (final ReactiveVariable reVar : currentReVars) {
        if (reVar == null) {
          continue;
        }

        final String name = reVar.getName();

        String value = reVar.getValueString();
        if (value == null) {
          value = new String();
        }

        if (values.containsKey(name)) {
          if (!values.get(name).equals(value)) {
            changes.put(name, changes.get(name) + 1);
          }
        }
        else {
          changes.put(name, 0);
        }

        values.put(name, value);
      }
    }

    return changes;
  }

  private static String calculateColor(final int value, final int max) {
    final double norm = (value * 1.0f) / max;

    final int rh = 255;
    final int rl = 240;
    final int gh = 237;
    final int gl = 59;
    final int bh = 160;
    final int bl = 32;

    final int r = (int) (rl + (rh - rl) * norm);
    final int g = (int) (gl + (gh - gl) * norm);
    final int b = (int) (bl + (bh - bl) * norm);

    return String.format("#%02x%02x%02x", r, g, b); //$NON-NLS-1$
  }
}
