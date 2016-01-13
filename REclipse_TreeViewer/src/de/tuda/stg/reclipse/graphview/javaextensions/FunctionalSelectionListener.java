package de.tuda.stg.reclipse.graphview.javaextensions;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

@FunctionalInterface
public interface FunctionalSelectionListener extends SelectionListener {

  void onSelected(SelectionEvent event);

  @Override
  default void widgetSelected(final SelectionEvent event) {
    onSelected(event);
  }

  @Override
  default void widgetDefaultSelected(final SelectionEvent event) {}

}
