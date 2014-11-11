package de.tu_darmstadt.stg.reclipse.graphview.provider;

import de.tu_darmstadt.stg.reclipse.graphview.model.DatabaseHelper;
import de.tu_darmstadt.stg.reclipse.graphview.view.ReactiveVariableVertex;
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

  /**
   * Updates the point in time.
   * 
   * @param newPointInTime
   *          The new point in time.
   */
  public void setPointInTime(final int newPointInTime) {
    this.pointInTime = newPointInTime;
  }

  /**
   * 
   * @return A set of reactive variable vertices.
   */
  public Set<ReactiveVariableVertex> getVertices() {
    final Set<ReactiveVariableVertex> vertices = new HashSet<>();

    // make sure that point in time is in a valid range
    if (pointInTime < 1 || pointInTime > DatabaseHelper.getLastPointInTime()) {
      return vertices;
    }

    // get reactive variables
    final ArrayList<ReactiveVariable> reVars = DatabaseHelper.getReVars(pointInTime);

    for (final ReactiveVariable reVar : reVars) {
      // return empty map if not all reactive variables are created yet
      if (reVar == null) {
        return new HashSet<>();
      }

      // create reactive variable vertex
      final ReactiveVariableVertex vertex = new ReactiveVariableVertex(reVar);

      vertices.add(vertex);
    }

    return vertices;
  }

  /**
   * 
   * @return A map containing information about the edges between vertices.
   */
  public Map<UUID, Set<UUID>> getEdges() {
    final Map<UUID, Set<UUID>> edges = new HashMap<>();

    // make sure that point in time is in a valid range
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

      edges.put(reVar.getId(), reVar.getConnectedWith());
    }

    return edges;
  }
}
