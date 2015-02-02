package de.tu_darmstadt.stg.reclipse.graphview.view.statistics;

import javax.swing.table.DefaultTableModel;

public class StatisticsTableModel extends DefaultTableModel {

  /**
   * 
   */
  private static final long serialVersionUID = -7592080310599273380L;

  private static String[] COLUMN_NAMES = {
      "Name", //$NON-NLS-1$
      "Type", //$NON-NLS-1$
      "Value", //$NON-NLS-1$
      "# Connected Variables" //$NON-NLS-1$
  };

  public StatisticsTableModel() {
    super();

    setColumnIdentifiers(COLUMN_NAMES);
  }

  public void loadData() {
    // for (int i = 0; i < getRowCount(); i++) {
    // removeRow(i);
    // }
    // final int lastPointInTime = DatabaseHelper.getLastPointInTime();
    //
    // final List<ReactiveVariable> reVars =
    // DatabaseHelper.getReVars(lastPointInTime);
    //
    // for (final ReactiveVariable reVar : reVars) {
    // final List<Object> entry = new ArrayList<>();
    //
    // entry.add(reVar.getName());
    // entry.add(reVar.getTypeSimple());
    // entry.add(reVar.getValueString());
    // entry.add(reVar.getConnectedWith().size());
    //
    // this.addRow(entry.toArray());
    // }
  }
}
