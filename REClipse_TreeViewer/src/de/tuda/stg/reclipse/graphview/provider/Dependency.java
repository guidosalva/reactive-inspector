package de.tuda.stg.reclipse.graphview.provider;

import de.tuda.stg.reclipse.logger.ReactiveVariable;

/**
 * Models a simple dependency / connection / directed edge in the graph.
 */
public class Dependency {

  protected final ReactiveVariable source;
  protected final ReactiveVariable destination;

  protected Dependency(final ReactiveVariable s, final ReactiveVariable dest) {
    source = s;
    destination = dest;
  }

  public ReactiveVariable getSource() {
    return source;
  }

  public ReactiveVariable getDestination() {
    return destination;
  }
}
