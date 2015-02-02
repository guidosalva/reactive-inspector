package de.tu_darmstadt.stg.reclipse.graphview.view.statistics;

import de.tu_darmstadt.stg.reclipse.graphview.model.DatabaseHelper;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.Heatmap;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * 
 * @author Sebastian Ruhleder <sebastian.ruhleder@gmail.com>
 * 
 */
public class StatisticsCharts {

  private final JFreeChart typeChart;

  private final JFreeChart changeChart;

  private final DefaultPieDataset typeDataset;

  private final DefaultCategoryDataset changeDataset;

  private static String TYPE_TITLE = "Reactive Variable Types"; //$NON-NLS-1$

  private static String CHANGE_TITLE = "Amount of Updates"; //$NON-NLS-1$

  public StatisticsCharts() {
    typeDataset = new DefaultPieDataset();
    changeDataset = new DefaultCategoryDataset();

    typeChart = ChartFactory.createPieChart3D(TYPE_TITLE, typeDataset, true, true, false);
    changeChart = ChartFactory.createBarChart3D(CHANGE_TITLE, "Variables", "Changes", changeDataset, PlotOrientation.VERTICAL, false, true, false);

    final NumberAxis rangeAxis = (NumberAxis) changeChart.getCategoryPlot().getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    populateTypeDataset();
  }

  /**
   * 
   * @return Pie chart instance of JFreeChart.
   */
  public JFreeChart getTypeChart() {
    return typeChart;
  }

  public JFreeChart getChangeChart() {
    return changeChart;
  }

  /**
   * Refreshses the dataset of the pie chart.
   */
  public void refresh() {
    populateTypeDataset();
    populateChangeDataset();

    typeChart.fireChartChanged();
    changeChart.fireChartChanged();
  }

  /**
   * Calculates the distribution of reactive variable types and populates the
   * dataset accordingly.
   */
  private void populateTypeDataset() {
    final Map<String, Integer> types = new HashMap<>();

    final int lastPointInTime = DatabaseHelper.getLastPointInTime();
    final List<ReactiveVariable> reVars = DatabaseHelper.getReVars(lastPointInTime);

    for (final ReactiveVariable reVar : reVars) {
      final String type = reVar.getTypeSimple();

      if (types.containsKey(type)) {
        types.put(type, types.get(type) + 1);
      }
      else {
        types.put(type, 1);
      }
    }

    for (final String type : types.keySet()) {
      typeDataset.setValue(type, types.get(type));
    }
  }

  private void populateChangeDataset() {
    final int lastPointInTime = DatabaseHelper.getLastPointInTime();

    final Map<String, Integer> changemap = Heatmap.calculateChangeMap(lastPointInTime);

    for (final String name : changemap.keySet()) {
      final Integer value = changemap.get(name);

      if (value == 0) {
        continue;
      }

      changeDataset.setValue(value, "", name);
    }
  }
}
