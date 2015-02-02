package de.tu_darmstadt.stg.reclipse.graphview.view.statistics;

import javax.swing.JTable;

public class StatisticsTable extends JTable {

  /**
   * 
   */
  private static final long serialVersionUID = -8614962359378125590L;

  public StatisticsTable() {
    super(new StatisticsTableModel());
  }

}
