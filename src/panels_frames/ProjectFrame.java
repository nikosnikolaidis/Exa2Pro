/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panels_frames;

import exa2pro.Exa2Pro;
import exa2pro.Issue;
import exa2pro.Project;
import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import static java.util.stream.Collectors.*;
import javax.swing.DefaultListModel;
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
        populateMethodsLists();
        populateIssueList();
    }
    
    //Populate List with Projects
    private void populateIssueList() {
        if(!Exa2Pro.projecCredentialstList.isEmpty()){
            DefaultListModel<Issue> defaultListModel= new DefaultListModel<>();
            Collections.sort(project.getprojectReport().getIssuesList());
            for(Issue i: project.getprojectReport().getIssuesList()){
                defaultListModel.addElement(i);
            }
            jListCodeSmells.setModel(defaultListModel);
            jListCodeSmells.setCellRenderer(new PanelIssueList());
        }
    }
    
    //Populate all List for Methods and Files
    private void populateMethodsLists(){
        //create the models
        DefaultListModel<String> defaultListModelFanOut = new DefaultListModel<>();
        DefaultListModel<String> defaultListModelCohesion = new DefaultListModel<>();
        DefaultListModel<String> defaultListModelCC = new DefaultListModel<>();
        DefaultListModel<String> defaultListModelLOC = new DefaultListModel<>();
        //create the lists for all the methods and files
        HashMap<String, Double> allFilesCohesion = new HashMap<>();
        HashMap<String, Integer> allFilesFanOut = new HashMap<>();
        HashMap<String, Integer> allMethodsCC = new HashMap<>();
        HashMap<String, Integer> allMethodsLOC = new HashMap<>();
        for(CodeFile cf: project.getprojectFiles()){
            allFilesFanOut.put(cf.file.getName(), cf.fanOut);
            allFilesCohesion.put(cf.file.getName(), Math.round(cf.cohesion * 10.0)/10.0);
            allMethodsCC.putAll(prefixHashMap(cf.methodsCC, cf.file.getName()));
            allMethodsLOC.putAll(prefixHashMap(cf.methodsLOC, cf.file.getName()));
        }
        
        //sort the lists
        HashMap<String, Integer> sortedCC= allMethodsCC.entrySet()
        .stream()
        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
        .collect(
            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
        HashMap<String, Integer> sortedLOC= allMethodsLOC.entrySet()
        .stream()
        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
        .collect(
            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
        HashMap<String, Double> sortedCohecion= allFilesCohesion.entrySet()
        .stream()
        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
        .collect(
            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
        HashMap<String, Integer> sortedFanOut= allFilesFanOut.entrySet()
        .stream()
        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
        .collect(
            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
        
        //add the items to the list
        sortedCC.entrySet().forEach((item) -> {
            defaultListModelCC.addElement(item.getValue()+" "+item.getKey());
        });
        sortedLOC.entrySet().forEach((item) -> {
            defaultListModelLOC.addElement(item.getValue()+" "+item.getKey());
        });
        sortedCohecion.entrySet().forEach((item) -> {
            defaultListModelCohesion.addElement(item.getValue()+" "+item.getKey());
        });
        sortedFanOut.entrySet().forEach((item) -> {
            defaultListModelFanOut.addElement(item.getValue()+" "+item.getKey());
        });
        
        jListFilesFanOut.setModel(defaultListModelFanOut);
        jListFilesIncoherent.setModel(defaultListModelCohesion);
        jListMethodsComplex.setModel(defaultListModelCC);
        jListMethodsLOC.setModel(defaultListModelLOC);
    }
    private HashMap prefixHashMap(HashMap source, String prefix) {
      HashMap result = new HashMap();
      Iterator iter = source.entrySet().iterator();
      while(iter.hasNext()) {
          HashMap.Entry entry = (HashMap.Entry) iter.next();
          Object key = entry.getKey();
          Object value = entry.getValue();
          result.put(prefix + '.' + key.toString(), value);
      }
      return result;
  }
    
    //Populate all JLabels
    private void populateJLabels() {
        jLabelProjectName.setText(project.getCredentials().getProjectName());
        jLabelIssues.setText(project.getprojectReport().getTotalCodeSmells()+" Issues");
        jLabelTotallLines.setText(project.getprojectReport().getTotalLinesOfCode()+"");
        jTextArea1.setText(project.getprojectReport().getLinesOfCodeForAllLanguages());
        jLabelDateAnalysis.setText(project.getprojectReport().getDate()+"");
        jLabelCodeSmells.setText(project.getprojectReport().getTotalCodeSmells()+"");
        jLabelComplexity.setText(project.getprojectReport().getTotalComplexity()+"");
        jLabelTechnicalDebt.setText(project.getprojectReport().getTotalDebt());
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
        jScrollPane2 = new javax.swing.JScrollPane();
        jListFilesFanOut = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        jListMethodsComplex = new javax.swing.JList<>();
        jLabelCodeSmells = new javax.swing.JLabel();
        jLabelTechnicalDebt = new javax.swing.JLabel();
        jLabelComplexity = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jListFilesIncoherent = new javax.swing.JList<>();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jListMethodsLOC = new javax.swing.JList<>();
        jPanelCodeSmells = new javax.swing.JPanel();
        jLabelIssues = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListCodeSmells = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Exa2Pro");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabelProjectName.setFont(new java.awt.Font("Times New Roman", 1, 16)); // NOI18N
        jLabelProjectName.setText("jLabel1");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new java.awt.GridLayout());

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

        jLabel16.setText("Progress");
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

        jPanelButtonMore.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonMore.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonMore.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelButtonMoreMouseClicked(evt);
            }
        });

        jLabel18.setText("More");
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 207, Short.MAX_VALUE)
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
                        .addContainerGap(225, Short.MAX_VALUE))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        jLabel6.setText("Measures");

        jLabel7.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel7.setText("Code Smells");

        jLabel8.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel8.setText("Technical Debt");

        jLabel9.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel9.setText("Complexity");

        jLabel10.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel10.setText("Big Fan-Out Files");

        jLabel11.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel11.setText("Complex Methods");

        jScrollPane2.setViewportView(jListFilesFanOut);

        jScrollPane3.setViewportView(jListMethodsComplex);

        jLabelCodeSmells.setText("jLabel12");

        jLabelTechnicalDebt.setText("jLabel13");

        jLabelComplexity.setText("jLabel14");

        jLabel12.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel12.setText("Incoherent Files");

        jScrollPane4.setViewportView(jListFilesIncoherent);

        jLabel13.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel13.setText("Big Methods");

        jScrollPane5.setViewportView(jListMethodsLOC);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelCodeSmells)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel10))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabelTechnicalDebt)
                            .addComponent(jLabel12)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelComplexity)
                            .addComponent(jLabel9)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(34, 34, 34))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelCodeSmells)
                                .addGap(24, 24, 24)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelTechnicalDebt)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelComplexity)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .addGap(0, 0, 0)
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

        jLabelIssues.setText(" Issues");

        jListCodeSmells.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListCodeSmellsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jListCodeSmells);

        javax.swing.GroupLayout jPanelCodeSmellsLayout = new javax.swing.GroupLayout(jPanelCodeSmells);
        jPanelCodeSmells.setLayout(jPanelCodeSmellsLayout);
        jPanelCodeSmellsLayout.setHorizontalGroup(
            jPanelCodeSmellsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCodeSmellsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCodeSmellsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCodeSmellsLayout.createSequentialGroup()
                        .addComponent(jLabelIssues)
                        .addGap(0, 1081, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanelCodeSmellsLayout.setVerticalGroup(
            jPanelCodeSmellsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCodeSmellsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelIssues)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelParent.add(jPanelCodeSmells, "card3");

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
                .addComponent(jPanelParent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jListCodeSmellsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListCodeSmellsMouseClicked
        Issue selectedIssue= jListCodeSmells.getSelectedValue();
        System.out.println("in "+selectedIssue.getIssueDirectory());
        System.out.println("from "+selectedIssue.getIssueStartLine()+ " till "+selectedIssue.getIssueEndLine());
        System.out.println(selectedIssue.getLinesOfCodeFromSonarQube());
    }//GEN-LAST:event_jListCodeSmellsMouseClicked

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
        jPanelParent.add(jPanelCodeSmells);
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
        jPanelParent.add(new JPanelRefactorings());
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

    private void reverseBorders(){
        jPanelButtonIssues.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonIssues.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonOverview.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonOverview.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonProgress.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonProgress.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonRefactorings.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonRefactorings.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelCodeSmells;
    private javax.swing.JLabel jLabelComplexity;
    private javax.swing.JLabel jLabelDateAnalysis;
    private javax.swing.JLabel jLabelIssues;
    private javax.swing.JLabel jLabelProjectName;
    private javax.swing.JLabel jLabelTechnicalDebt;
    private javax.swing.JLabel jLabelTotallLines;
    private javax.swing.JList<Issue> jListCodeSmells;
    private javax.swing.JList<String> jListFilesFanOut;
    private javax.swing.JList<String> jListFilesIncoherent;
    private javax.swing.JList<String> jListMethodsComplex;
    private javax.swing.JList<String> jListMethodsLOC;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelButtonIssues;
    private javax.swing.JPanel jPanelButtonMore;
    private javax.swing.JPanel jPanelButtonOverview;
    private javax.swing.JPanel jPanelButtonProgress;
    private javax.swing.JPanel jPanelButtonRefactorings;
    private javax.swing.JPanel jPanelCodeSmells;
    private javax.swing.JPanel jPanelOverview;
    private javax.swing.JPanel jPanelParent;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

}
