package de.tu_darmstadt.stg.reclipse.graphview.view;

import javax.swing.table.DefaultTableModel;

public class StatisticsTableModel extends DefaultTableModel {

  private static String[] COLUMN_NAMES = {
      "Name",
      "Type",
      "Value",
      "# Connected Variables"
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
