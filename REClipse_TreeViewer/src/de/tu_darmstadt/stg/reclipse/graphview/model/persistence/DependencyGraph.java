package de.tu_darmstadt.stg.reclipse.graphview.model.persistence;

import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DependencyGraph {

  private final Set<Vertex> vertices;

  public DependencyGraph(final Collection<? extends Vertex> vertices) {
    this.vertices = new HashSet<>(vertices);
  }

  public static DependencyGraph emptyGraph() {
    return new DependencyGraph(Collections.<Vertex> emptySet());
  }

  public Set<Vertex> getVertices() {
    return Collections.unmodifiableSet(vertices);
  }

  public static class Vertex {

    private final UUID id;
    private final ReactiveVariable variable;
    private final Set<Vertex> connected = new HashSet<>();

    public Vertex(final UUID id, final ReactiveVariable variable) {
      this.id = id;
      this.variable = variable;
    }

    public void addConnectedVertex(final Vertex v) {
      connected.add(v);
    }

    public Set<Vertex> getConnectedVertices() {
      return Collections.unmodifiableSet(connected);
    }

    public UUID getId() {
      return id;
    }

    public ReactiveVariable getVariable() {
      return variable;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Vertex other = (Vertex) obj;
      if (id == null) {
        if (other.id != null) {
          return false;
        }
      }
      else if (!id.equals(other.id)) {
        return false;
      }
      return true;
    }
  }
}
