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
        
        populateMethodsLists();
    }

    //Populate all List for Methods and Files
    private void populateMethodsLists(){
        HashMap<String, Double> thresholds= exa2pro.PieChart.calculateThresholds();
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
            if(cf.fanOut > thresholds.get("FanOut"))
                allFilesFanOut.put(cf.file.getName(), cf.fanOut);
            if(Math.round(cf.cohesion * 10.0)/10.0 > thresholds.get("LCOM2"))
                allFilesCohesion.put(cf.file.getName(), Math.round(cf.cohesion * 10.0)/10.0);
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
    private HashMap prefixHashMap(HashMap source, String prefix, HashMap<String, Double> thresholds, String metric) {
        HashMap result = new HashMap();
        Iterator iter = source.entrySet().iterator();
        while(iter.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) iter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if((Integer)value > thresholds.get(metric))
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

        jLabelRefactorings = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jListMethodsLOC = new javax.swing.JList<>();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jListMethodsComplex = new javax.swing.JList<>();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jListFilesIncoherent = new javax.swing.JList<>();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListFilesFanOut = new javax.swing.JList<>();
        jLabel10 = new javax.swing.JLabel();

        jLabelRefactorings.setText("Refactorings");

        jScrollPane5.setViewportView(jListMethodsLOC);

        jLabel13.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel13.setText("Long Methods");

        jScrollPane3.setViewportView(jListMethodsComplex);

        jLabel11.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel11.setText("Complex Methods");

        jScrollPane4.setViewportView(jListFilesIncoherent);

        jLabel12.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel12.setText("Incoherent Files");

        jScrollPane2.setViewportView(jListFilesFanOut);

        jLabel10.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel10.setText("Big Fan-Out Files");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelRefactorings)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addComponent(jLabel10)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel11)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(111, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelRefactorings)
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane4))))
                .addGap(192, 192, 192))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabelRefactorings;
    private javax.swing.JList<String> jListFilesFanOut;
    private javax.swing.JList<String> jListFilesIncoherent;
    private javax.swing.JList<String> jListMethodsComplex;
    private javax.swing.JList<String> jListMethodsLOC;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    // End of variables declaration//GEN-END:variables
}