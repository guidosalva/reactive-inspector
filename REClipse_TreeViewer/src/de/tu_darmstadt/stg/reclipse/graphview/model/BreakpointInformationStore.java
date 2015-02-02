package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.logger.BreakpointInformation;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Sebastian Ruhleder <sebastian.ruhleder@googlemail.com>
 * 
 */
public class BreakpointInformationStore {

  private static BreakpointInformationStore instance = null;

  private final Map<ReactiveVariable, BreakpointInformation> store;

  public BreakpointInformationStore() {
    store = new HashMap<>();
  }

  public static BreakpointInformationStore getInstance() {
    if (instance == null) {
      instance = new BreakpointInformationStore();
    }
    return instance;
  }

  public void put(final ReactiveVariable var, final BreakpointInformation information) {
    store.put(var, information);
  }

  public BreakpointInformation get(final ReactiveVariable var) {
    return store.get(var);
  }
}
