package de.tuda.stg.reclipse.graphview.view.statistics;

import de.tuda.stg.reclipse.graphview.model.SessionContext;
import de.tuda.stg.reclipse.graphview.view.graph.Heatmap;
import de.tuda.stg.reclipse.logger.ReactiveVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
public class Charts {

  private final SessionContext ctx;

  private final JFreeChart typeChart;

  private final JFreeChart changeChart;

  private final DefaultPieDataset typeDataset;

  private final DefaultCategoryDataset changeDataset;

  private static String TYPE_TITLE = "Reactive Variable Types"; //$NON-NLS-1$

  private static String CHANGE_TITLE = "Amount of Updates"; //$NON-NLS-1$

  public Charts(final SessionContext ctx) {
    this.ctx = ctx;

    typeDataset = new DefaultPieDataset();
    changeDataset = new DefaultCategoryDataset();

    typeChart = ChartFactory.createPieChart3D(TYPE_TITLE, typeDataset, true, true, false);
    changeChart = ChartFactory.createBarChart3D(CHANGE_TITLE, "Variables", "Changes", changeDataset, PlotOrientation.VERTICAL, false, true, false); //$NON-NLS-1$ //$NON-NLS-2$

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

    final int lastPointInTime = ctx.getPersistence().getLastPointInTime();
    final List<ReactiveVariable> reVars = ctx.getPersistence().getReVars(lastPointInTime);

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
    final int lastPointInTime = ctx.getPersistence().getLastPointInTime();

    final Map<UUID, Long> changemap = Heatmap.calculateChangeMap(lastPointInTime, ctx);

    for (final UUID id : changemap.keySet()) {
      final Long value = changemap.get(id);

      if (value == 0) {
        continue;
      }

      changeDataset.setValue(value, "", id); //$NON-NLS-1$
    }
  }
}
