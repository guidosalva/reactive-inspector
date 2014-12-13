package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.util.Comparator;

/**
 * 
 * @author Sebastian Ruhleder <sebastian.ruhleder@googlemail.com>
 * 
 */
public class ReactiveVariableNameComparator implements Comparator<ReactiveVariable> {

  @Override
  public int compare(final ReactiveVariable o1, final ReactiveVariable o2) {
    final String name1 = o1.getName();
    final String name2 = o2.getName();

    if (name1 == null) {
      return 0;
    }

    return name1.compareTo(name2);
  }
}
