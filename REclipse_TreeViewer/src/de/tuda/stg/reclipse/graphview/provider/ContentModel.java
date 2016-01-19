package de.tuda.stg.reclipse.graphview.provider;

import de.tuda.stg.reclipse.graphview.Activator;
import de.tuda.stg.reclipse.graphview.model.SessionContext;
import de.tuda.stg.reclipse.graphview.model.persistence.DependencyGraph;
import de.tuda.stg.reclipse.graphview.model.persistence.DependencyGraph.Vertex;
import de.tuda.stg.reclipse.graphview.view.graph.Heatmap;
import de.tuda.stg.reclipse.graphview.view.graph.ReactiveVariableVertex;
import de.tuda.stg.reclipse.graphview.view.graph.Stylesheet;
import de.tuda.stg.reclipse.logger.BreakpointInformation;
import de.tuda.stg.reclipse.logger.ReactiveVariable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 */
public class ContentModel {

  private final SessionContext ctx;

  private int pointInTime = 0;
  private boolean highlightChange;

  private Map<UUID, String> library = new HashMap<>();
  private DependencyGraph dependencyGraph = DependencyGraph.emptyGraph();

  public ContentModel(final SessionContext ctx) {
    this.ctx = ctx;
    this.highlightChange = false;
  }

  /**
   * Updates the point in time.
   * @param newPointInTime  The new point in time.
   */
  public void setPointInTime(final int newPointInTime) {
    setPointInTime(newPointInTime, false);
  }

  public void setPointInTime(final int newPointInTime, final boolean propagateChange) {
    this.pointInTime = newPointInTime;
    this.highlightChange = propagateChange;

    updateLibary();

    this.dependencyGraph = ctx.getPersistence().getDependencyGraph(newPointInTime);
  }

  private void updateLibary() {
    library = new HashMap<>();

    if (dependencyGraph != null) {
      for (final Vertex vertex : dependencyGraph.getVertices()) {
        final ReactiveVariable variable = vertex.getVariable();
        library.put(variable.getId(), variable.getValueString());
      }
    }
  }

  /**
   *
   * @return A set of reactive variable vertices.
   */
  public List<ReactiveVariableVertex> getVertices() {
    final List<ReactiveVariableVertex> vertices = new ArrayList<>();

    for (final Vertex vertex : dependencyGraph.getVertices()) {
      final ReactiveVariable variable = vertex.getVariable();
      final boolean variableChanged = hasVariableChanged(variable);

      // create reactive variable vertex
      final boolean isHighlighted = variableChanged && highlightChange;
      final BreakpointInformation breakpointInformation = ctx.getVariableLocation(variable.getId());
      final ReactiveVariableVertex variableVertext = new ReactiveVariableVertex(vertex, breakpointInformation, isHighlighted);

      vertices.add(variableVertext);
    }

    Collections.sort(vertices);

    return vertices;
  }

  @SuppressWarnings("nls")
  public List<ReactiveVariableVertex> getAbsolutePerformanceLatestVertices() {
    final Map<String, Long> values = new HashMap<>();

    for (final Vertex vertex : dependencyGraph.getVertices()) {
      final ReactiveVariable variable = vertex.getVariable();
      final Long evaluationDuration = (Long)variable.getAdditionalKeys().getOrDefault("evaluationDuration", Long.valueOf(0));
      final String name = variable.getName();
      values.put(name, evaluationDuration);
    }

    final Map<String, String> heatmap = Heatmap.generateHeatmap(values);

    final List<ReactiveVariableVertex> vertices = new ArrayList<>();

    for (final Vertex vertex : dependencyGraph.getVertices()) {
      final ReactiveVariable variable = vertex.getVariable();

      // create reactive variable vertex
      final BreakpointInformation breakpointInformation = ctx.getVariableLocation(variable.getId());

      final String color = heatmap.get(variable.getName());
      final String style = Stylesheet.calculateStyleFromColor(color);

      final Long evaluationDuration = (Long)variable.getAdditionalKeys().getOrDefault("evaluationDuration", Long.valueOf(0));

      final ReactiveVariableVertex variableVertex = new ReactiveVariableVertex(vertex, breakpointInformation, style, evaluationDuration);

      vertices.add(variableVertex);
    }
    Collections.sort(vertices);

    return vertices;
  }

  @SuppressWarnings("nls")
  public List<ReactiveVariableVertex> getAbsolutePerformanceSumVertices() {
    final Map<String, Long> values = new HashMap<>();

    for (final Vertex vertex : dependencyGraph.getVertices()) {
      final ReactiveVariable variable = vertex.getVariable();
      final Long evaluationDuration = (Long)variable.getAdditionalKeys().getOrDefault("sumOfEvaluationDurations", Long.valueOf(0));
      final String name = variable.getName();
      values.put(name, evaluationDuration);
    }

    final Map<String, String> heatmap = Heatmap.generateHeatmap(values);

    final List<ReactiveVariableVertex> vertices = new ArrayList<>();
    for (final Vertex vertex : dependencyGraph.getVertices()) {
      final ReactiveVariable variable = vertex.getVariable();
      // create reactive variable vertex
      final BreakpointInformation breakpointInformation = ctx.getVariableLocation(variable.getId());

      final String color = heatmap.get(variable.getName());
      final String style = Stylesheet.calculateStyleFromColor(color);

      final Long evaluationDuration = (Long)variable.getAdditionalKeys().getOrDefault("sumOfEvaluationDurations", Long.valueOf(0));

      final ReactiveVariableVertex variableVertex = new ReactiveVariableVertex(vertex, breakpointInformation, style, evaluationDuration);

      vertices.add(variableVertex);
    }

    Collections.sort(vertices);

    return vertices;
  }

  private boolean hasVariableChanged(final ReactiveVariable variable) {
    if (!library.containsKey(variable.getId())) {
      return !library.isEmpty();
    }

    final String oldValue = library.get(variable.getId());
    return !Objects.equals(oldValue, variable.getValueString());
  }

  public List<ReactiveVariableVertex> getHeatmapVertices() {
    final List<ReactiveVariableVertex> vertices = new ArrayList<>();

    final Map<String, Long> values = Heatmap.calculateChangeMap(pointInTime, ctx);
    final Map<String, String> heatmap = Heatmap.generateHeatmap(values);

    for (final Vertex vertex : dependencyGraph.getVertices()) {
      final ReactiveVariable variable = vertex.getVariable();
      final String color = heatmap.get(variable.getName());
      final String style = Stylesheet.calculateStyleFromColor(color);
      final BreakpointInformation breakpointInformation = ctx.getVariableLocation(variable.getId());
      final ReactiveVariableVertex variableVertext = new ReactiveVariableVertex(vertex, breakpointInformation, style);

      Activator.logInfo("Name: " + variable.getName() + "  Evaluations: " + values.getOrDefault(variable.getName(), Long.valueOf(0L)));

      vertices.add(variableVertext);
    }

    Collections.sort(vertices);

    return vertices;
  }

  /**
   *
   * @return A map containing information about the edges between vertices.
   */
  public Map<UUID, Set<UUID>> getEdges() {
    final Map<UUID, Set<UUID>> edges = new HashMap<>();

    for (final Vertex vertex : dependencyGraph.getVertices()) {
      final Set<UUID> connectedWith = new HashSet<>();

      for (final Vertex connectedVertex : vertex.getConnectedVertices()) {
        connectedWith.add(connectedVertex.getVariable().getId());
      }

      edges.put(vertex.getVariable().getId(), connectedWith);
    }

    return edges;
  }
}
