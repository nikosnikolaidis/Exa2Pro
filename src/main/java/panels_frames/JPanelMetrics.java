/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panels_frames;

import exa2pro.Project;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import parsers.CodeFile;

/**
 *
 * @author Nikos
 */
public class JPanelMetrics extends javax.swing.JPanel {

    Project project;
    
    /**
     * Creates new form JPanelMetrics
     */
    public JPanelMetrics(Project project) {
        this.project= project;
        initComponents();
        
        addRowsInTable();
    }

    private void addRowsInTable(){
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {}, new String [] {
                "File", "CBF", "LOC", "LCOP"
            }){
                Class[] types = { String.class, Integer.class, Integer.class, String.class};
                @Override
                public Class getColumnClass(int columnIndex) {
                    return this.types[columnIndex];
                }
                boolean[] canEdit = { false, false, false, false};
                public boolean isCellEditable(int columnIndex) {
                    return this.canEdit[columnIndex];
                }
            });
        
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        for(CodeFile cf: project.getprojectFiles()){
            if(cf.lcop==-1)
                model.addRow(new Object[]{cf.file.getName(), cf.fanOut, Math.round(cf.totalLines * 10.0)/10.0, "NonDefined"});
            else
                model.addRow(new Object[]{cf.file.getName(), cf.fanOut, Math.round(cf.totalLines * 10.0)/10.0, ""+cf.lcop});
        }
        
        //right aligment in lcop column
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        jTable1.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        
//        print the files with number of methods
//        HashMap<String,Integer> temp=new HashMap<>();
//        for(CodeFile cf: project.getprojectFiles()){
//            temp.put(cf.file.getName(), cf.methodsLOC.size());
//        }
//        
//        final Map<String, Integer> sortedByCount = temp.entrySet()
//                .stream()
//                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
//        sortedByCount.forEach((key, value) -> System.out.println(key + ":" + value));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "File", "Fan-Out", "LCOL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("Files");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Methods");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2)
                        .addGap(0, 971, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {}, new String [] {
                "Method", "CC", "LCOL"
            }){
                Class[] types = { String.class, Integer.class, Integer.class};
                @Override
                public Class getColumnClass(int columnIndex) {
                    return this.types[columnIndex];
                }
                boolean[] canEdit = { false, false, false};
                public boolean isCellEditable(int columnIndex) {
                    return this.canEdit[columnIndex];
                }
            });
        
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        for(CodeFile cf: project.getprojectFiles()){
            for(String key :cf.methodsLOC.keySet()){
                if(cf.methodsLCOL.containsKey(key.split(" ")[key.split(" ").length-1]))
                    model.addRow(new Object[]{cf.file.getName()+": "+key, cf.methodsCC.get(key),
                        cf.methodsLCOL.get(key.split(" ")[key.split(" ").length-1])});
                else
                    model.addRow(new Object[]{cf.file.getName()+": "+key, cf.methodsCC.get(key),"-"});
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {}, new String [] {
                "File", "Fan-Out", "LCOL", "LCOP"
            }));
        
        addRowsInTable();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
