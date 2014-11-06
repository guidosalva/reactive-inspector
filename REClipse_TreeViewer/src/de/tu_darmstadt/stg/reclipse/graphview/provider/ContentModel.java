package de.tu_darmstadt.stg.reclipse.graphview.provider;

import de.tu_darmstadt.stg.reclipse.graphview.model.DatabaseHelper;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 * 
 */
public class ContentModel {

  private int pointInTime = 0;

  public void setPointInTime(final int newPointInTime) {
    this.pointInTime = newPointInTime;
  }

  public Map<Object, Object> getVertices() {
    final Map<Object, Object> vertices = new HashMap<>();

    // make sure that point in tim eis in a valid range
    if (pointInTime < 1 || pointInTime > DatabaseHelper.getLastPointInTime()) {
      return vertices;
    }

    // get reactive variables
    final ArrayList<ReactiveVariable> reVars = DatabaseHelper.getReVars(pointInTime);

    for (final ReactiveVariable reVar : reVars) {
      // return empty map if not all reactive variables are created yet
      if (reVar == null) {
        return new HashMap<>();
      }

      vertices.put(reVar.getId(), reVar);
    }

    return vertices;
  }

  public Map<Object, Set<Object>> getEdges() {
    final Map<Object, Set<Object>> edges = new HashMap<>();

    // make sure that point in tim eis in a valid range
    if (pointInTime < 1 || pointInTime > DatabaseHelper.getLastPointInTime()) {
      return edges;
    }

    // get reactive variables
    final ArrayList<ReactiveVariable> reVars = DatabaseHelper.getReVars(pointInTime);

    for (final ReactiveVariable reVar : reVars) {
      // return empty map if not all reactive variables are created yet
      if (reVar == null) {
        return new HashMap<>();
      }

      // collect connected variables
      final Set<Object> connectedWith = new HashSet<>();
      for (final UUID connectedId : reVar.getConnectedWith()) {
        connectedWith.add(connectedId);
      }

      edges.put(reVar.getId(), connectedWith);
    }

    return edges;
  }
}
