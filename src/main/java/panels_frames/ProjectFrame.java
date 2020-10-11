/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panels_frames;

import exa2pro.Exa2Pro;
import exa2pro.PieChart;
import exa2pro.Project;
import exa2pro.Report;
import java.awt.Color;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import parsers.CodeFile;

/**
 *
 * @author Nikos
 */
public class ProjectFrame extends javax.swing.JFrame {
    
    Project project;

    /**
     * Creates new form ProjectFrame
     */
    public ProjectFrame() {
        initComponents();
    }
    /**
     * Creates new form Home
     * @param p the project that was selected to view
     */
    public ProjectFrame(Project p){
        this.project=p;
        initComponents();
        
        populateJLabels();
        addPieChart();
        
        
            ///calculate Interest///
            /*double totalInterest=0.0;
            HashMap<String, Integer> tdOfFiles=project.getprojectReport().getTdOfEachFile();
            //for each file
            if(!project.getprojectReport().getTdOfEachFile().isEmpty())
            for (CodeFile file : project.getprojectFiles()) {
                HashMap<String,Double> similarityOfFiles=new HashMap<>();
                int sumCC=0;
                for(String key:file.methodsLOC.keySet()){
                    sumCC+= file.methodsCC.get(key);
                }
                int size=file.methodsLOC.size();
                if(size==0)
                    size=1;
                double avCC= (sumCC*1.0)/size;
                System.out.println("--");
//                System.out.println("   LOC:"+file.totalLines+ " CC:"+avCC+
//                		" FO:"+file.fanOut+" LCOL:"+file.cohesion+ " NOP:"+file.methodsLOC.size()
//                		+" TD:"+tdOfFiles.get(file.file.getName()));
                //calculate the similarity with all the rest
//                System.out.println("=======");
                for (CodeFile file2 : project.getprojectFiles()) {
                    if( !file.file.getAbsolutePath().equals(file2.file.getAbsolutePath()) ){
//                    	if(file.file.getName().equals("prep_annealing.f") 
//                    					&& file2.file.getName().equals("x2p_copy.hpp")) {
//                    		System.out.println("!**");
//                    	}
                        int sumCC2=0;
                        for(String key2:file2.methodsLOC.keySet()){
                            sumCC2+= file2.methodsCC.get(key2);
                        }
                        int size2=file2.methodsLOC.size();
                        if(size2==0)
                            size2=1;
                        double avCC2= (sumCC2*1.0)/size2;
                        
                        
                        double tempCC= avCC;
                        if(tempCC==0)
                        	tempCC=1;
                        double tempLCOL= file.cohesion;
                        if(tempLCOL==0)
                        	tempLCOL=1;
                        int tempNOP= file.methodsLOC.size();
                        if(tempNOP==0)
                        	tempNOP=1;
                        int tempTD= tdOfFiles.get(file.file.getName());
                        if(tempTD==0)
                        		tempTD=1;
                        
                        double similarity= ( Math.abs(file.totalLines-file2.totalLines)*1.0/file.totalLines +
                                Math.abs(avCC-avCC2)*1.0/tempCC +
                                //Math.abs(file.cohesion-file2.cohesion)*1.0/tempLCOL+
                                //Math.abs(file.methodsLOC.size()-file2.methodsLOC.size())*1.0/tempNOP +
                                Math.abs(tdOfFiles.get(file.file.getName())-tdOfFiles.get(file2.file.getName()))*1.0/tempTD
                                )/3;
                        similarityOfFiles.put(file2.file.getAbsolutePath(), 1-similarity);

//                        System.out.println("LOC:"+file2.totalLines+ " CC:"+avCC2+
//                        		" FO:"+file2.fanOut+" LCOL:"+file2.cohesion+ " NOP:"+file2.methodsLOC.size()
//                        		+ " TD:"+tdOfFiles.get(file2.file.getName()));
//                        System.out.println(1-similarity);
                    }
                }
                
                //keep top 3
                Map<String, Double> sortedSimilarity= similarityOfFiles.entrySet().stream()
                    .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
                    .collect(
                        toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
                            LinkedHashMap::new));
                List<Entry<String, Double>> list = 
                            new ArrayList<Entry<String, Double>>(sortedSimilarity.entrySet());
                
                ArrayList<CodeFile> filesForCompare= new ArrayList<>();
                for (CodeFile fileTemp : project.getprojectFiles()) {
                    if(list.get(0).getKey().equals(fileTemp.file.getAbsolutePath()) && list.get(0).getValue()>=0.5) {
                        filesForCompare.add(fileTemp);
                        break;
                    }
                }
                for(CodeFile fileTemp: project.getprojectFiles()) {
                	if( (list.get(1).getKey().equals(fileTemp.file.getAbsolutePath()) ||
                            list.get(2).getKey().equals(fileTemp.file.getAbsolutePath()) ) && list.get(0).getValue()>=0.5 )
                		filesForCompare.add(fileTemp);
                }
                if(filesForCompare.isEmpty()) {
                	for (CodeFile fileTemp : project.getprojectFiles()) {
                        if(list.get(0).getKey().equals(fileTemp.file.getAbsolutePath()) ) {
                            filesForCompare.add(fileTemp);
                            break;
                        }
                    }
                }
                	
                
                //print this file
                System.out.println("   LOC:"+file.totalLines+ " CC:"+avCC+
                		" FO:"+file.fanOut+" LCOL:"+file.cohesion+ " NOP:"+file.methodsLOC.size()
                		+" TD:"+tdOfFiles.get(file.file.getName()));
                // and print top 3
                for(int i=0; i<filesForCompare.size(); i++) {
	                for (CodeFile file2 : project.getprojectFiles()) {
	                	if(file2.file.getAbsoluteFile().toString().equals( list.get(i).getKey().toString() )) {
	                		int sumCC2=0;
	                        for(String key2:file2.methodsLOC.keySet()){
	                            sumCC2+= file2.methodsCC.get(key2);
	                        }
	                        int size2=file2.methodsLOC.size();
	                        if(size2==0)
	                            size2=1;
	                        double avCC2= (sumCC2*1.0)/size2;
	                        
	                		System.out.println("LOC:"+file2.totalLines+ " CC:"+avCC2+
	                        		" FO:"+file2.fanOut+" LCOL:"+file2.cohesion+ " NOP:"+file2.methodsLOC.size()
	                        		+ " TD:"+tdOfFiles.get(file2.file.getName()));
	                		System.out.println(list.get(i).getValue());
	                		break;
	                	}
	                }
                }
                
                //get optimal metrics
                int sumCCopt=0;
                for(String key:filesForCompare.get(0).methodsLOC.keySet()){
                    sumCCopt+= filesForCompare.get(0).methodsCC.get(key);
                }
                int sizeOpt=filesForCompare.get(0).methodsLOC.size();
                if(sizeOpt==0)
                    sizeOpt=1;
                double avCCopt= sumCCopt*1.0/sizeOpt;
                
                double optimalLOC= filesForCompare.get(0).totalLines;
                double optimalCC= avCCopt;
                int optimalFO= filesForCompare.get(0).fanOut;
                double optimalLCOL= filesForCompare.get(0).cohesion;
                double optimalLCOP= filesForCompare.get(0).lcop;
//                System.out.println("LOC:"+filesForCompare.get(0).totalLines+ " CC:"+avCCopt+
//                		" FO:"+filesForCompare.get(0).fanOut+" LCOL:"+filesForCompare.get(0).cohesion);
                
                for (int i=1; i<filesForCompare.size(); i++) {
                    if(filesForCompare.get(i).fanOut < optimalFO)
                        optimalFO=filesForCompare.get(i).fanOut;
                    if( (filesForCompare.get(i).cohesion!=0 && filesForCompare.get(i).cohesion < optimalLCOL)
                    		|| optimalLCOL==0)
                        optimalLCOL=filesForCompare.get(i).cohesion;
                    if(filesForCompare.get(i).totalLines!=0 && filesForCompare.get(i).totalLines<optimalLOC)
                    	optimalLOC=filesForCompare.get(i).totalLines;
                    sumCCopt=0;
                    for(String key:filesForCompare.get(i).methodsLOC.keySet()){
                        sumCCopt+= filesForCompare.get(i).methodsCC.get(key);
                    }
                    sizeOpt=filesForCompare.get(i).methodsLOC.size();
                    if(sizeOpt==0)
                        sizeOpt=1;
                    avCCopt= sumCCopt*1.0/sizeOpt;
                    if(avCCopt < optimalCC)
                        optimalCC=avCCopt;
                    if( (filesForCompare.get(i).lcop!=-1 && filesForCompare.get(i).lcop < optimalLCOP)
                    		|| optimalLCOP==-1)
                    	optimalLCOP= filesForCompare.get(i).lcop;
//                    System.out.println("LOC:"+filesForCompare.get(i).totalLines+ " CC:"+avCCopt+
//                    		" FO:"+filesForCompare.get(i).fanOut+" LCOL:"+filesForCompare.get(i).cohesion);
                }
                
                int investFO=0;
                if(file.fanOut==0 && optimalFO==0)
                	investFO=1;
                int investCC=0;
                if(avCC==0 && optimalCC==0)
                	investCC=1;
                int investLCOP=0;
                if(file.lcop==0 && optimalLCOP==0)
                	investLCOP=1;
                
                //normalize
                if(optimalLOC==0)
                	optimalLOC=1.0;
                if(optimalCC==0)
                	optimalCC=1.0;
                if(optimalFO==0)
                	optimalFO=1;
                if(optimalLCOL==0)
                	optimalLCOL=file.cohesion;
                if(optimalLCOP==0)
                	optimalLCOP=1;
                
                //get new lines average
                int sumNewLines= 0;
                int sumFiles=0;
                for(Project proj: project.getCredentials().getProjects()){
                    sumNewLines+= proj.getprojectReport().getNewLinesOfCode();
                    sumFiles+= proj.getprojectFiles().size();
                }
                if(project.getCredentials().getProjects().size()>1){
                    int avgNewLines= sumNewLines/(project.getCredentials().getProjects().size()-1);
                    int avgFiles= sumFiles/(project.getCredentials().getProjects().size()-1);
                    avgNewLines= avgNewLines/avgFiles;

                        //calculate the interest per LOC
                        double sumInterestPerLOC=0;
//	                    double interestLCOL= (file.cohesion-optimalLCOL)*1.0/optimalLCOL;
//	                    if(file.cohesion!=0)
//	                    	sumInterestPerLOC+= interestLCOL;
                        if(file.lcop!=-1 && optimalLCOP!=-1) {
                        	if(investLCOP==1)
                        		sumInterestPerLOC+= (investLCOP-optimalLCOP)*1.0/optimalLCOP;
                        	else
                        		sumInterestPerLOC+= (file.lcop-optimalLCOP)*1.0/optimalLCOP;
                        }
                        
	                    if(investFO==1)
	                    	sumInterestPerLOC+= (investFO-optimalFO)*1.0/optimalFO;
	                    else
	                    	sumInterestPerLOC+= (file.fanOut-optimalFO)*1.0/optimalFO;
	                    
	                    sumInterestPerLOC+= (file.totalLines-optimalLOC)*1.0/optimalLOC;
	                    
                        if(investCC==1)
                        	sumInterestPerLOC+= (investCC-optimalCC)*1.0/optimalCC;
                        else
                        	sumInterestPerLOC+=(avCC-optimalCC)*1.0/optimalCC;
	                    
	                    double avgInterestPerLOC= (sumInterestPerLOC)/3;
	                    if(file.lcop!=-1 && optimalLCOP!=-1) {
	                    	avgInterestPerLOC= (sumInterestPerLOC)/4;
	                    }

                        //calculate the interest in AVG LOC
                        double interestInAvgLOC= avgInterestPerLOC*avgNewLines;
                        interestInAvgLOC= avgInterestPerLOC*5.77;//40.72;//41.67;//82.4;
                        
                        //calculate the interest in hours
                        double interestInHours= interestInAvgLOC/25;
                        //calculate the interest in dollars
                        double interestInEuros= interestInHours*39.44;

                        totalInterest+= interestInEuros;
                        System.out.println(interestInEuros);
                }
                
                
            }
            
            
            
            
            System.out.println("Total Interest: " +totalInterest);*/
    }
    
    private void addPieChart(){
        HashMap<String, Double> temp= PieChart.calculateThresholds();
        jPanel6.removeAll();
        PieChart chart = new PieChart(project,"Pie","FanOut"," of Files", temp.get("FanOut"));
        javax.swing.GroupLayout jPanelChartLayout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanelChartLayout);
        jPanelChartLayout.setHorizontalGroup(
            jPanelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanelChartLayout.setVerticalGroup(
            jPanelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        
        jPanel7.removeAll();
        PieChart chart1 = new PieChart(project,"Pie","LCOL"," of Files", temp.get("LCOL"));
        javax.swing.GroupLayout jPanelChartLayout1 = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanelChartLayout1);
        jPanelChartLayout1.setHorizontalGroup(
            jPanelChartLayout1.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout1.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart1.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanelChartLayout1.setVerticalGroup(
            jPanelChartLayout1.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout1.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart1.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        
        jPanel11.removeAll();
        PieChart chart5 = new PieChart(project,"Pie","LCOP"," of Files", temp.get("LCOP"));
        javax.swing.GroupLayout jPanelChartLayout5 = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanelChartLayout5);
        jPanelChartLayout5.setHorizontalGroup(
            jPanelChartLayout5.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout5.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart5.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanelChartLayout5.setVerticalGroup(
            jPanelChartLayout5.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout5.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart5.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        
        jPanel8.removeAll();
        PieChart chart2 = new PieChart(project,"Pie","CC"," of Methods", temp.get("CC"));
        javax.swing.GroupLayout jPanelChartLayout2 = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanelChartLayout2);
        jPanelChartLayout2.setHorizontalGroup(
            jPanelChartLayout2.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout2.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart2.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanelChartLayout2.setVerticalGroup(
            jPanelChartLayout2.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout2.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart2.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        
        jPanel9.removeAll();
        PieChart chart3 = new PieChart(project,"Pie","LOC"," of Methods", temp.get("LOC"));
        javax.swing.GroupLayout jPanelChartLayout3 = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanelChartLayout3);
        jPanelChartLayout3.setHorizontalGroup(
            jPanelChartLayout3.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout3.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart3.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanelChartLayout3.setVerticalGroup(
            jPanelChartLayout3.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout3.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart3.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }
    
    
    //Populate all JLabels
    private void populateJLabels() {
        jLabelProjectName.setText(project.getCredentials().getProjectName());
        jLabelTotallLines.setText(project.getprojectReport().getTotalLinesOfCode()+"");
        jTextArea1.setText(project.getprojectReport().getLinesOfCodeForAllLanguages());
        jLabelDateAnalysis.setText(project.getprojectReport().getDate()+"");
        jLabelCodeSmells.setText(project.getprojectReport().getTotalCodeSmells()+"");
        jLabelTechnicalDebt.setText(project.getprojectReport().getTotalDebt());
        
        //print the metrics of system
        double sumLOC=0;
        int sumLCOP=0;
        int countNonUndif=0;
        int sumCC=0;
        int sumLCOL=0;
        int sumFO=0;
        int c=0;
        for(CodeFile cf: project.getprojectFiles()){
            sumLOC+= cf.totalLines;
            sumFO+= cf.fanOut;
            if(cf.lcop!=-1){
                sumLCOP+= cf.lcop;
                countNonUndif++;
            }
            for (Map.Entry<String, Integer> entry : cf.methodsCC.entrySet()) {
                sumCC+= entry.getValue();
                c++;
            }
            for (Map.Entry<String, Double> entry : cf.methodsLCOL.entrySet()) {
                sumLCOL+= entry.getValue();
            }
        }
        
        DecimalFormat df = new DecimalFormat("#.#");
        jLabelCC.setText( df.format(sumCC*1.0/c) +"");
        jLabelLCOL.setText( df.format(sumLCOL*1.0/c) +"");
        jLabelFO.setText( df.format(sumFO*1.0/project.getprojectFiles().size()) +"");
        jLabelLOC.setText( df.format(sumLOC/project.getprojectFiles().size()) +"");
        if(countNonUndif==0)
            jLabelLCOP.setText("-");
        else
            jLabelLCOP.setText(df.format(sumLCOP/countNonUndif) +"");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabelProjectName = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanelButtonOverview = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jPanelButtonIssues = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jPanelButtonProgress = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jPanelButtonRefactorings = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jPanelButtonMetrics = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jPanelButtonForecasting = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jPanelButtonMore = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jPanelParent = new javax.swing.JPanel();
        jPanelOverview = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabelTotallLines = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabelDateAnalysis = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabelCodeSmells = new javax.swing.JLabel();
        jLabelTechnicalDebt = new javax.swing.JLabel();
        jLabelCC = new javax.swing.JLabel();
        jLabelLOC = new javax.swing.JLabel();
        jLabelFO = new javax.swing.JLabel();
        jLabelLCOL = new javax.swing.JLabel();
        jLabelLCOP = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Exa2Pro");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabelProjectName.setFont(new java.awt.Font("Times New Roman", 1, 16)); // NOI18N
        jLabelProjectName.setText("jLabel1");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new java.awt.GridLayout(1, 0));

        jPanelButtonOverview.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanelButtonOverview.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelButtonOverviewMouseClicked(evt);
            }
        });

        jLabel15.setText("Overview");
        jPanelButtonOverview.add(jLabel15);

        jPanel3.add(jPanelButtonOverview);

        jPanelButtonIssues.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonIssues.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonIssues.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelButtonIssuesMouseClicked(evt);
            }
        });

        jLabel14.setText("Issues");
        jPanelButtonIssues.add(jLabel14);

        jPanel3.add(jPanelButtonIssues);

        jPanelButtonProgress.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonProgress.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonProgress.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelButtonProgressMouseClicked(evt);
            }
        });

        jLabel16.setText("Evolution");
        jPanelButtonProgress.add(jLabel16);

        jPanel3.add(jPanelButtonProgress);

        jPanelButtonRefactorings.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonRefactorings.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonRefactorings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelButtonRefactoringsMouseClicked(evt);
            }
        });

        jLabel17.setText("Refactorings");
        jPanelButtonRefactorings.add(jLabel17);

        jPanel3.add(jPanelButtonRefactorings);

        jPanelButtonMetrics.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonMetrics.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonMetrics.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelButtonMetricsMouseClicked(evt);
            }
        });

        jLabel22.setText("Metrics");
        jPanelButtonMetrics.add(jLabel22);

        jPanel3.add(jPanelButtonMetrics);

        jPanelButtonForecasting.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonForecasting.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonForecasting.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelButtonForecastingMouseClicked(evt);
            }
        });

        jLabel19.setText("Forecasting");
        jPanelButtonForecasting.add(jLabel19);

        jPanel3.add(jPanelButtonForecasting);

        jPanelButtonMore.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonMore.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonMore.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelButtonMoreMouseClicked(evt);
            }
        });

        jLabel18.setText("Manage Project");
        jPanelButtonMore.add(jLabel18);

        jPanel3.add(jPanelButtonMore);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelProjectName)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabelProjectName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelParent.setLayout(new java.awt.CardLayout());

        jLabel2.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabel2.setText("Overview");

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        jLabel1.setText("About");

        jLabelTotallLines.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabelTotallLines.setText("jLabel3");

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel3.setText("Total Lines of Code");

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel4.setText("Lines of Code per Language");

        jLabel5.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel5.setText("Last analysis");

        jLabelDateAnalysis.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabelDateAnalysis.setText("jLabel6");

        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(240, 240, 240));
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane7.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTotallLines)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabelDateAnalysis)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTotallLines)
                        .addGap(22, 22, 22)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelDateAnalysis)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        jLabel6.setText("System Measures");

        jLabel7.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel7.setText("Code Smells");

        jLabel8.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel8.setText("Technical Debt");

        jLabel9.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel9.setText("CC");

        jLabel10.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel10.setText("LOC");

        jLabel11.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel11.setText("CBF");

        jLabel12.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel12.setText("LCOL");

        jLabel13.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel13.setText("LCOP");

        jLabelCodeSmells.setText("jLabel12");

        jLabelTechnicalDebt.setText("jLabel13");

        jLabelCC.setText("jLabel14");

        jLabelLOC.setText("jLabel11");

        jLabelFO.setText("jLabel12");

        jLabelLCOL.setText("jLabel13");

        jLabelLCOP.setText("jLabel14");

        jPanel10.setLayout(new java.awt.GridLayout(2, 2, 10, 10));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 286, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel10.add(jPanel6);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 286, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 205, Short.MAX_VALUE)
        );

        jPanel10.add(jPanel7);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 286, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 205, Short.MAX_VALUE)
        );

        jPanel10.add(jPanel8);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 205, Short.MAX_VALUE)
        );

        jPanel10.add(jPanel9);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 286, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 205, Short.MAX_VALUE)
        );

        jPanel10.add(jPanel11);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelCodeSmells, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabelTechnicalDebt))
                        .addGap(60, 60, 60)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabelCC))
                        .addGap(55, 55, 55)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabelLOC))
                        .addGap(55, 55, 55)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabelFO))
                        .addGap(55, 55, 55)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabelLCOL))
                        .addGap(55, 55, 55)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelLCOP)
                            .addComponent(jLabel13))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 880, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabelCodeSmells))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabelTechnicalDebt)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelCC)
                            .addComponent(jLabelLOC)
                            .addComponent(jLabelFO)
                            .addComponent(jLabelLCOL)
                            .addComponent(jLabelLCOP))))
                .addGap(18, 18, 18)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelOverviewLayout = new javax.swing.GroupLayout(jPanelOverview);
        jPanelOverview.setLayout(jPanelOverviewLayout);
        jPanelOverviewLayout.setHorizontalGroup(
            jPanelOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOverviewLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelOverviewLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelOverviewLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelOverviewLayout.setVerticalGroup(
            jPanelOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOverviewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanelParent.add(jPanelOverview, "card2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelParent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanelParent, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPanelButtonOverviewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelButtonOverviewMouseClicked
        reverseBorders();
        jPanelButtonOverview.setBackground(new Color(240, 240, 240));
        jPanelButtonOverview.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        
        jPanelParent.removeAll();
        jPanelParent.add(jPanelOverview);
        jPanelParent.repaint();
        jPanelParent.revalidate();

    }//GEN-LAST:event_jPanelButtonOverviewMouseClicked

    private void jPanelButtonIssuesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelButtonIssuesMouseClicked
        reverseBorders();
        jPanelButtonIssues.setBackground(new Color(240, 240, 240));
        jPanelButtonIssues.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        
        jPanelParent.removeAll();
        jPanelParent.add(new JPanelIssues(project));
        jPanelParent.repaint();
        jPanelParent.revalidate();
    }//GEN-LAST:event_jPanelButtonIssuesMouseClicked

    private void jPanelButtonProgressMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelButtonProgressMouseClicked
        reverseBorders();
        jPanelButtonProgress.setBackground(new Color(240, 240, 240));
        jPanelButtonProgress.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        
        jPanelParent.removeAll();
        jPanelParent.add(new JPanelProgress(project));
        jPanelParent.repaint();
        jPanelParent.revalidate();
    }//GEN-LAST:event_jPanelButtonProgressMouseClicked

    private void jPanelButtonRefactoringsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelButtonRefactoringsMouseClicked
        reverseBorders();
        jPanelButtonRefactorings.setBackground(new Color(240, 240, 240));
        jPanelButtonRefactorings.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        
        jPanelParent.removeAll();
        jPanelParent.add(new JPanelRefactorings(project));
        jPanelParent.repaint();
        jPanelParent.revalidate();
    }//GEN-LAST:event_jPanelButtonRefactoringsMouseClicked

    private void jPanelButtonMoreMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelButtonMoreMouseClicked
        reverseBorders();
        jPanelButtonMore.setBackground(new Color(240, 240, 240));
        jPanelButtonMore.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        
        jPanelParent.removeAll();
        jPanelParent.add(new JPanelMore(project));
        jPanelParent.repaint();
        jPanelParent.revalidate();
    }//GEN-LAST:event_jPanelButtonMoreMouseClicked

    private void jPanelButtonMetricsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelButtonMetricsMouseClicked
        reverseBorders();
        jPanelButtonMetrics.setBackground(new Color(240, 240, 240));
        jPanelButtonMetrics.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        
        jPanelParent.removeAll();
        jPanelParent.add(new JPanelMetrics(project));
        jPanelParent.repaint();
        jPanelParent.revalidate();
    }//GEN-LAST:event_jPanelButtonMetricsMouseClicked

    private void jPanelButtonForecastingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelButtonForecastingMouseClicked
        reverseBorders();
        jPanelButtonForecasting.setBackground(new Color(240, 240, 240));
        jPanelButtonForecasting.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        
        jPanelParent.removeAll();
        jPanelParent.add(new JPanelForecasting(project));
        jPanelParent.repaint();
        jPanelParent.revalidate();
    }//GEN-LAST:event_jPanelButtonForecastingMouseClicked

    private void reverseBorders(){
        jPanelButtonIssues.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonIssues.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonOverview.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonOverview.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonProgress.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonProgress.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonRefactorings.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonRefactorings.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonMetrics.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonMetrics.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonForecasting.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonForecasting.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonMore.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonMore.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProjectFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProjectFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProjectFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProjectFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProjectFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelCC;
    private javax.swing.JLabel jLabelCodeSmells;
    private javax.swing.JLabel jLabelDateAnalysis;
    private javax.swing.JLabel jLabelFO;
    private javax.swing.JLabel jLabelLCOL;
    private javax.swing.JLabel jLabelLCOP;
    private javax.swing.JLabel jLabelLOC;
    private javax.swing.JLabel jLabelProjectName;
    private javax.swing.JLabel jLabelTechnicalDebt;
    private javax.swing.JLabel jLabelTotallLines;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelButtonForecasting;
    private javax.swing.JPanel jPanelButtonIssues;
    private javax.swing.JPanel jPanelButtonMetrics;
    private javax.swing.JPanel jPanelButtonMore;
    private javax.swing.JPanel jPanelButtonOverview;
    private javax.swing.JPanel jPanelButtonProgress;
    private javax.swing.JPanel jPanelButtonRefactorings;
    private javax.swing.JPanel jPanelOverview;
    private javax.swing.JPanel jPanelParent;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
    
}
