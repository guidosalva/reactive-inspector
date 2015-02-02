package de.tu_darmstadt.stg.reclipse.graphview.provider;

import de.tu_darmstadt.stg.reclipse.graphview.model.DatabaseHelper;
import de.tu_darmstadt.stg.reclipse.graphview.model.ReactiveVariableNameComparator;
import de.tu_darmstadt.stg.reclipse.graphview.view.ReactiveVariableVertex;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.Stylesheet;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.Heatmap;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

  private boolean highlightChange;

  private final Map<String, String> library;

  private final Map<String, Boolean> state;

  public ContentModel() {
    this.library = new HashMap<>();
    this.state = new HashMap<>();
    this.highlightChange = false;
  }

  /**
   * Updates the point in time.
   * 
   * @param newPointInTime
   *          The new point in time.
   */
  public void setPointInTime(final int newPointInTime) {
    setPointInTime(newPointInTime, false);
  }

  public void setPointInTime(final int newPointInTime, final boolean propagateChange) {
    this.pointInTime = newPointInTime;
    this.highlightChange = propagateChange;
  }

  /**
   * 
   * @return A set of reactive variable vertices.
   */
  public List<ReactiveVariableVertex> getVertices() {
    final List<ReactiveVariableVertex> vertices = new ArrayList<>();

    // make sure that point in time is in a valid range
    if (pointInTime < 1 || pointInTime > DatabaseHelper.getLastPointInTime()) {
      return vertices;
    }

    // get reactive variables
    final ArrayList<ReactiveVariable> reVars = DatabaseHelper.getReVars(pointInTime);

    // sort by name
    Collections.sort(reVars, new ReactiveVariableNameComparator());

    // set flag if library is empty
    final boolean emptyLibrary = library.size() == 0;

    for (final ReactiveVariable reVar : reVars) {
      // return empty map if not all reactive variables are created yet
      if (reVar == null) {
        return new ArrayList<>();
      }

      // extract name and value
      final String name = reVar.getName();
      final String value = reVar.getValueString();

      // only update highlight status if a new variable has been added
      if (library.size() < reVars.size()) {
        if (library.containsKey(name)) {
          state.put(name, !library.get(name).equals(value));
        }
        else {
          state.put(name, !emptyLibrary);
        }
      }

      // update value in library
      library.put(name, value == null ? "null" : value); //$NON-NLS-1$

      // create reactive variable vertex
      final boolean isHighlighted = state.get(name) && highlightChange;
      final ReactiveVariableVertex vertex = new ReactiveVariableVertex(reVar, isHighlighted);

      vertices.add(vertex);
    }

    return vertices;
  }

  public List<ReactiveVariableVertex> getHeatmapVertices() {
    final List<ReactiveVariableVertex> vertices = new ArrayList<>();

    // make sure that point in time is in a valid range
    if (pointInTime < 1 || pointInTime > DatabaseHelper.getLastPointInTime()) {
      return vertices;
    }

    // get reactive variables
    final ArrayList<ReactiveVariable> reVars = DatabaseHelper.getReVars(pointInTime);

    // generate heatmap based on point in time
    final Map<String, String> heatmap = Heatmap.generateHeatmap(pointInTime);

    for (final ReactiveVariable reVar : reVars) {
      // get color for reactive variable
      final String color = heatmap.get(reVar.getName());

      // generate style from color
      final String style = Stylesheet.calculateStyleFromColor(color);

      // create vertex instance
      final ReactiveVariableVertex vertex = new ReactiveVariableVertex(reVar, style);

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
