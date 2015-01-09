package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.model.DatabaseHelper;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class StatisticsTableModel extends AbstractTableModel {

  private List<List<Object>> data;

  private static String[] COLUMN_NAMES = {
      "Name",
      "Type",
      "Value"
  };

  public StatisticsTableModel() {
    super();

    loadData();
  }

  public void loadData() {
    data = new ArrayList<>();

    final int lastPointInTime = DatabaseHelper.getLastPointInTime();

    final List<ReactiveVariable> reVars = DatabaseHelper.getReVars(lastPointInTime);

    for (final ReactiveVariable reVar : reVars) {
      final List<Object> entry = new ArrayList<>();

      entry.add(reVar.getName());
      entry.add(reVar.getTypeSimple());
      entry.add(reVar.getValueString());

      data.add(entry);
    }

    fireTableDataChanged();
  }

  @Override
  public int getRowCount() {
    return data.size();
  }

  @Override
  public int getColumnCount() {
    return COLUMN_NAMES.length;
  }

  @Override
  public String getColumnName(final int column) {
    return COLUMN_NAMES[column];
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    if (rowIndex > data.size()) {
      return null;
    }

    return data.get(rowIndex).get(columnIndex);
  }
}
