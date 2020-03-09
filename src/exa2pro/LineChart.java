package exa2pro;

import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;
import parsers.CodeFile;


public class LineChart extends ApplicationFrame {

    ProjectCredentials projectC;
    public ChartPanel chartPanel;
    private String metric;

    public LineChart(ProjectCredentials pC, String metric, String applicationTitle, String chartTitle) {
        super(applicationTitle);
        this.projectC = pC;
        this.metric=metric;
        JFreeChart lineChart = ChartFactory.createLineChart(
                chartTitle,
                "Version", "Value",
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);

        chartPanel = new ChartPanel( lineChart );
        //chartPanel.setPreferredSize( new java.awt.Dimension( 600, 500) );
        setContentPane( chartPanel );
    }

    private DefaultCategoryDataset createDataset() {
        ArrayList<Project> allProject = projectC.getProjects();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        int version = 1;
        for (Project p : allProject) {
            double num=0;
            switch (metric) {
                case "Issues":
                    num= p.getprojectReport().getTotalCodeSmells();
                    break;
                case "Fan-Out":
                    {
                        int sum=0;
                        sum = p.getprojectFiles().stream().map((cf) -> cf.fanOut).reduce(sum, Integer::sum);
                        num= sum/p.getprojectFiles().size();
                        break;
                    }
                case "LCOM2":
                    {
                        double sum=0;
                        sum = p.getprojectFiles().stream().map((cf) -> cf.cohesion).reduce(sum, Double::sum);
                        num= sum/p.getprojectFiles().size();
                        break;
                    }
                case "CC":
                    {
                        int sum=0;
                        for(CodeFile cf: p.getprojectFiles()){
                            for (HashMap.Entry pair : cf.methodsCC.entrySet()) {
                                sum+= (Integer)pair.getValue();
                            }
                        }
                        num= sum/p.getprojectFiles().size();
                        break;
                    }
                case "LOC":
                    {
                        int sum=0;
                        for(CodeFile cf: p.getprojectFiles()){
                            for (HashMap.Entry pair : cf.methodsLOC.entrySet()) {
                                sum+= (Integer)pair.getValue();
                            }
                        }
                        num= sum/p.getprojectFiles().size();
                        break;
                    }
                default:
                    break;
            }
            
            dataset.setValue(num, "Rate", "V." + version);
            version++;
        }

        return dataset;
    }

}
