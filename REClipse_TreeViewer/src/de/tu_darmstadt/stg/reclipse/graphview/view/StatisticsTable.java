package de.tu_darmstadt.stg.reclipse.graphview.view;

import javax.swing.JTable;

public class StatisticsTable extends JTable {

  public StatisticsTable() {
    super(new StatisticsTableModel());
  }

}
