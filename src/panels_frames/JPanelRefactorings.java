/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panels_frames;

import exa2pro.Project;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import static java.util.stream.Collectors.toMap;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import parsers.CodeFile;

/**
 *
 * @author Nikos
 */
public class JPanelRefactorings extends javax.swing.JPanel {

    Project project;
    
    /**
     * Creates new form JPanelRefactorings
     */
    public JPanelRefactorings(Project project) {
        this.project=project;
        initComponents();
        jPanel6.setVisible(false);
        
        populateMethodsLists();
    }

    //Populate all List for Methods and Files
    private void populateMethodsLists(){
        HashMap<String, Double> thresholds= exa2pro.PieChart.calculateThresholds();
        //create the models
        DefaultListModel<String> defaultListModelFanOut = new DefaultListModel<>();
        DefaultListModel<String> defaultListModelCohesion = new DefaultListModel<>();
        DefaultListModel<String> defaultListModelLCOP = new DefaultListModel<>();
        DefaultListModel<String> defaultListModelCC = new DefaultListModel<>();
        DefaultListModel<String> defaultListModelLOC = new DefaultListModel<>();
        //create the lists for all the methods and files
        HashMap<String, Double> allFilesCohesion = new HashMap<>();
        HashMap<String, Integer> allFilesLCOP = new HashMap<>();
        HashMap<String, Integer> allFilesFanOut = new HashMap<>();
        HashMap<String, Integer> allMethodsCC = new HashMap<>();
        HashMap<String, Integer> allMethodsLOC = new HashMap<>();
        for(CodeFile cf: project.getprojectFiles()){
            if(cf.fanOut >= thresholds.get("FanOut"))
                allFilesFanOut.put(cf.file.getName(), cf.fanOut);
            if(Math.round(cf.cohesion * 10.0)/10.0 >= thresholds.get("LCOL"))
                allFilesCohesion.put(cf.file.getName(), Math.round(cf.cohesion * 10.0)/10.0);
            if(cf.lcop!=-1 && Math.round(cf.lcop * 10.0)/10.0 >= thresholds.get("LCOP"))
                allFilesLCOP.put(cf.file.getName(), cf.lcop);
            allMethodsCC.putAll(prefixHashMap(cf.methodsCC, cf.file.getName(), thresholds, "CC"));
            allMethodsLOC.putAll(prefixHashMap(cf.methodsLOC, cf.file.getName(), thresholds, "LOC"));
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
        HashMap<String, Integer> sortedLCOP= allFilesLCOP.entrySet()
        .stream()
        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
        .collect(
            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
        
        //add the items to the list
        System.out.println("CC");
        sortedCC.entrySet().forEach((item) -> {
            System.out.println(item.getValue()+" "+item.getKey());
            defaultListModelCC.addElement(item.getValue()+" "+item.getKey());
        });
        System.out.println("LOC");
        sortedLOC.entrySet().forEach((item) -> {
            System.out.println(item.getValue()+" "+item.getKey());
            defaultListModelLOC.addElement(item.getValue()+" "+item.getKey());
        });
        System.out.println("LCOM");
        sortedCohecion.entrySet().forEach((item) -> {
            System.out.println(item.getValue()+" "+item.getKey());
            defaultListModelCohesion.addElement(item.getValue()+" "+item.getKey());
        });
        System.out.println("FO");
        sortedFanOut.entrySet().forEach((item) -> {
            System.out.println(item.getValue()+" "+item.getKey());
            defaultListModelFanOut.addElement(item.getValue()+" "+item.getKey());
        });
        System.out.println("LCOP");
        sortedLCOP.entrySet().forEach((item) -> {
            System.out.println(item.getValue()+" "+item.getKey());
            defaultListModelLCOP.addElement(item.getValue()+" "+item.getKey());
        });
        
        jListFilesFanOut.setModel(defaultListModelFanOut);
        jListFilesIncoherent.setModel(defaultListModelCohesion);
        jListMethodsComplex.setModel(defaultListModelCC);
        jListMethodsLOC.setModel(defaultListModelLOC);
        jListFilesLCOP.setModel(defaultListModelLCOP);
    }
    private HashMap prefixHashMap(HashMap source, String prefix, HashMap<String, Double> thresholds, String metric) {
        HashMap result = new HashMap();
        Iterator iter = source.entrySet().iterator();
        while(iter.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) iter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if((Integer)value >= thresholds.get(metric))
                result.put(prefix + '.' + key.toString(), value);
        }
        return result;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabelRefactorings = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListFilesFanOut = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jListFilesIncoherent = new javax.swing.JList<>();
        jPanel7 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jListFilesLCOP = new javax.swing.JList<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jListMethodsComplex = new javax.swing.JList<>();
        jPanel4 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jListMethodsLOC = new javax.swing.JList<>();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListOpportunities = new javax.swing.JList<>();

        jLabel2.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
        jLabel2.setText("Select a file and wait while calculating possible refactorings");

        jLabelRefactorings.setText("Refactorings");

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        jLabel10.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel10.setText("Big Fan-Out Files");

        jScrollPane2.setViewportView(jListFilesFanOut);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.add(jPanel1);

        jLabel12.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel12.setText("(LCOL) Incoherent Files");

        jListFilesIncoherent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jListFilesIncoherentMousePressed(evt);
            }
        });
        jScrollPane4.setViewportView(jListFilesIncoherent);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.add(jPanel2);

        jLabel14.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel14.setText("(LCOP) Incoherent Files");

        jScrollPane6.setViewportView(jListFilesLCOP);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.add(jPanel7);

        jLabel11.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel11.setText("Complex Methods");

        jScrollPane3.setViewportView(jListMethodsComplex);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.add(jPanel3);

        jLabel13.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel13.setText("Long Methods");

        jScrollPane5.setViewportView(jListMethodsLOC);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.add(jPanel4);

        jLabel1.setText("Opportunity refactorings");

        jListOpportunities.setLayoutOrientation(javax.swing.JList.VERTICAL_WRAP);
        jScrollPane1.setViewportView(jListOpportunities);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                .addGap(6, 6, 6))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelRefactorings)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelRefactorings)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jListFilesIncoherentMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListFilesIncoherentMousePressed
        DefaultListModel<String> defaultListModelOpp = new DefaultListModel<>();
        
        boolean fast;
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(this,
                    "Would you like to use the Faster Opportunity extractor, but in accuracy drawback?",
                    "Algorithm Option", dialogButton);
        if(dialogResult == 0) {
            fast=true;
        } else {
            fast=false;
        } 
        
        String fileName= jListFilesIncoherent.getSelectedValue().split(" ")[1];
        for(CodeFile cf:project.getprojectFiles()){
           if(fileName.equals(cf.file.getName())){
               cf.parse();
               //cf.calculateCohesion();
               cf.calculateOpportunities(fast);
               
               cf.opportunities.forEach((opp) -> {
                   defaultListModelOpp.addElement(opp.split(" ", 2)[0]+"-"+opp.split(" ", 2)[1].replace("()", "():"));
               });
               //if(!defaultListModelOpp.isEmpty())
                   jPanel6.setVisible(true);
               //jListOpportunities.setFixedCellWidth(100);
               jListOpportunities.setModel(defaultListModelOpp);
           }
       }
    }//GEN-LAST:event_jListFilesIncoherentMousePressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelRefactorings;
    private javax.swing.JList<String> jListFilesFanOut;
    private javax.swing.JList<String> jListFilesIncoherent;
    private javax.swing.JList<String> jListFilesLCOP;
    private javax.swing.JList<String> jListMethodsComplex;
    private javax.swing.JList<String> jListMethodsLOC;
    private javax.swing.JList<String> jListOpportunities;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    // End of variables declaration//GEN-END:variables
}
