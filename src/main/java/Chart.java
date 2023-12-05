import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Chart {
    public static void drawHistograms(Table table) {
        for(int i = 0; i < table.header.size(); ++i) {
            String columnName = table.header.get(i);
            ArrayList<Double> columnData = new ArrayList<>();

            for(int j = 0; j < table.data.size(); ++j) {
                columnData.add(table.data.get(j).get(i));
            }

            Collections.sort(columnData);
            double step = (table.getMaxData().get(i) - table.getMinData().get(i)) / 10.0;
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            int count = 0;
            double cur = table.getMinData().get(i) + step;

            for (Double value : columnData) {
                if (value < cur) {
                    ++count;
                } else {
                    dataset.addValue(count, cur - step + "-" + cur, "");
                    count = 0;
                    cur += step;
                }
            }

            dataset.addValue(count, cur - step + "-" + cur, "");
            JFreeChart chart = ChartFactory.createBarChart("Histogram - " + columnName, "Columns", "Values", dataset, PlotOrientation.VERTICAL, true, true, false);
            CategoryPlot plot = (CategoryPlot)chart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);
            CategoryAxis xAxis = plot.getDomainAxis();
            xAxis.setTickLabelFont(xAxis.getTickLabelFont().deriveFont(14.0F));
            NumberAxis yAxis = (NumberAxis)plot.getRangeAxis();
            yAxis.setTickLabelFont(yAxis.getTickLabelFont().deriveFont(14.0F));
            BarRenderer renderer = (BarRenderer)plot.getRenderer();
            renderer.setDrawBarOutline(false);
            renderer.setItemMargin(0.1);
            ChartFrame frame = new ChartFrame("Histogram - " + columnName, chart);
            frame.pack();
            frame.setVisible(true);
        }

    }

    public static void drawAUC_ROC(ArrayList<Double> x, ArrayList<Double> y) {
        XYSeries series = new XYSeries("График");
        XYSeries series1 = new XYSeries("График1");

        for (int i = 0; i < x.size(); i++) {
            if (!y.get(i).isNaN() && !x.get(i).isNaN()) {
                series.add(x.get(i), y.get(i));
            }
        }

        series1.add(0, 0);
        series1.add(1, 1);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(series1);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "AUC-ROC",
                "FPR",
                "TPR",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartFrame frame = new ChartFrame("График", chart);
        frame.pack();
        frame.setVisible(true);
    }

    public static void drawAUC_PR(ArrayList<Double> x, ArrayList<Double> y) {
        XYSeries series = new XYSeries("График");
        XYSeries series1 = new XYSeries("График1");

        for (int i = 0; i < x.size(); i++) {
            if ((!y.get(i).isNaN() && !x.get(i).isNaN()) && (Math.abs(x.get(i) - 1.0) >= 0.01 || Math.abs(y.get(i) - 1.0) >= 0.01)) {
                series.add(x.get(i), y.get(i));
            }
        }

        series1.add(0, 1);
        series1.add(1, 0);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(series1);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "AUC-PR",
                "precision",
                "recall",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartFrame frame = new ChartFrame("График", chart);
        frame.pack();
        frame.setVisible(true);
    }
}
