/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panels_frames;

import exa2pro.Exa2Pro;
import exa2pro.Project;
import exa2pro.ProjectCredentials;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 *
 * @author Nikos
 */
public class JPanelMore extends javax.swing.JPanel {

    Project project;
    ProjectFrame parentFrame;
    
    /**
     * Creates new form JPanelMore
     */
    public JPanelMore(Project project, ProjectFrame frame) {
        this.project= project;
        this.parentFrame= frame;
        initComponents();
        
        //if there are more than 1 version enable delete last analysis
        if(project.getCredentials().getProjects().size()==1)
            jButtonLastAnalysisDelete.setEnabled(false);
        else
            jButtonLastAnalysisDelete.setEnabled(true);
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
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButtonLastAnalysisDelete = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButtonDeleteProject = new javax.swing.JButton();

        jLabelRefactorings.setText("Options");

        jPanel1.setLayout(new java.awt.GridLayout(0, 2, 10, 5));

        jLabel1.setText("New Version Ready to be Analyzed");
        jPanel1.add(jLabel1);

        jButton1.setText("Analyze");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);

        jLabel2.setText("Delete Last Analysis");
        jPanel1.add(jLabel2);

        jButtonLastAnalysisDelete.setText("Delete");
        jButtonLastAnalysisDelete.setToolTipText("");
        jButtonLastAnalysisDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLastAnalysisDeleteActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonLastAnalysisDelete);

        jLabel3.setText("Delete Project");
        jPanel1.add(jLabel3);

        jButtonDeleteProject.setText("Delete");
        jButtonDeleteProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteProjectActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonDeleteProject);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelRefactorings))
                .addContainerGap(779, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelRefactorings)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(408, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonLastAnalysisDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLastAnalysisDeleteActionPerformed
        project.getCredentials().removeProject(project);
        project.saveToFile();
    }//GEN-LAST:event_jButtonLastAnalysisDeleteActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Project p= new Project(project.getCredentials(), project.getCredentials().getProjects().size()+1+"");
        p.projectVersionAnalysisFull();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButtonDeleteProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteProjectActionPerformed
        Exa2Pro.projecCredentialstList.remove(project.getCredentials());
        project.saveToFile();
        
        parentFrame.homeFrame.populateProjectList();
        parentFrame.setVisible(false);
        parentFrame.dispose();
    }//GEN-LAST:event_jButtonDeleteProjectActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonDeleteProject;
    private javax.swing.JButton jButtonLastAnalysisDelete;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelRefactorings;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
