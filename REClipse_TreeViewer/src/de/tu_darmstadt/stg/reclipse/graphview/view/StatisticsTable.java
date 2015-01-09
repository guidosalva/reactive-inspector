package de.tu_darmstadt.stg.reclipse.graphview.view;

import javax.swing.JTable;

public class StatisticsTable extends JTable {

  private final StatisticsTableModel model;

  public StatisticsTable() {
    super();

    model = new StatisticsTableModel();

    setModel(model);
  }

  public void refresh() {
    model.loadData();
  }
}
