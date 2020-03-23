/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panels_frames;

import exa2pro.Analysis;
import exa2pro.Exa2Pro;
import exa2pro.Project;
import exa2pro.ProjectCredentials;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Nikos
 */
public class HomeFrame extends javax.swing.JFrame {

    /**
     * Creates new form Home
     */
    public HomeFrame() {
        initComponents();
        
        populateProjectList();
    }

    //Populate List with Projects
    private void populateProjectList() {
        if(!Exa2Pro.projecCredentialstList.isEmpty()){
            DefaultListModel<ProjectCredentials> defaultListModel= new DefaultListModel<>();
            for(ProjectCredentials p: Exa2Pro.projecCredentialstList){
                defaultListModel.addElement(p);
            }
            jListProjects.setModel(defaultListModel);
            jListProjects.setCellRenderer(new PanelProjectList());
        }
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jButtonProjects = new javax.swing.JButton();
        jButtonAddNew = new javax.swing.JButton();
        jButtonSettings = new javax.swing.JButton();
        parentPanel = new javax.swing.JPanel();
        jPanelProjects = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListProjects = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        jPanelSettings = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldSonarURL = new javax.swing.JTextField();
        jTextFieldICodePath = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jButtonChangeRuleEffort = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jTextFieldSonarPath = new javax.swing.JTextField();
        jPanelAddNew = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldProjectName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldProjectDirectory = new javax.swing.JTextField();
        jButtonDirectory = new javax.swing.JButton();
        jButtonSaveProject = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(950, 635));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exa2pro/exa2pro.trans.png"))); // NOI18N
        jPanel2.add(jLabel1);

        jButtonProjects.setText("Projects");
        jButtonProjects.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProjectsActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonProjects);

        jButtonAddNew.setText("Add New");
        jButtonAddNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddNewActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonAddNew);

        jButtonSettings.setText("Settings");
        jButtonSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSettingsActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonSettings);

        parentPanel.setLayout(new java.awt.CardLayout());

        jListProjects.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jListProjectsMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jListProjects);

        jLabel2.setText("Projects");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(554, 554, 554))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 606, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelProjects.add(jPanel6);

        parentPanel.add(jPanelProjects, "card2");

        jLabel4.setText("Settings");

        jLabel7.setText("Sonar Qube URL:");

        jLabel8.setText("iCode Path:");

        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel9.setText("Change rule effort time:");

        jButtonChangeRuleEffort.setText("Change");
        jButtonChangeRuleEffort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChangeRuleEffortActionPerformed(evt);
            }
        });

        jLabel10.setText("Sonar Qube Path:");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8))
                                .addGap(46, 46, 46))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(43, 43, 43)))
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButtonChangeRuleEffort)
                            .addComponent(jTextFieldSonarPath)
                            .addComponent(jTextFieldICodePath)
                            .addComponent(jTextFieldSonarURL, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel9))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextFieldSonarURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldICodePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextFieldSonarPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jButtonChangeRuleEffort))
                .addGap(34, 34, 34)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jPanelSettings.add(jPanel7);

        parentPanel.add(jPanelSettings, "card3");

        jPanelAddNew.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 30));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Add new project");

        jLabel5.setText("Project Name:");

        jTextFieldProjectName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldProjectNameActionPerformed(evt);
            }
        });

        jLabel6.setText("Projet Directory:");

        jTextFieldProjectDirectory.setEditable(false);
        jTextFieldProjectDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldProjectDirectoryActionPerformed(evt);
            }
        });

        jButtonDirectory.setText("...");
        jButtonDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDirectoryActionPerformed(evt);
            }
        });

        jButtonSaveProject.setText("Save");
        jButtonSaveProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveProjectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonSaveProject)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel6)
                                .addComponent(jLabel5))
                            .addGap(10, 10, 10)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextFieldProjectDirectory)
                                .addComponent(jTextFieldProjectName, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButtonDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addGap(160, 160, 160)
                            .addComponent(jLabel3))))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextFieldProjectName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextFieldProjectDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDirectory))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addComponent(jButtonSaveProject))
        );

        jPanelAddNew.add(jPanel5);

        parentPanel.add(jPanelAddNew, "card4");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(parentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(parentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonProjectsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProjectsActionPerformed
        parentPanel.removeAll();
        parentPanel.add(jPanelProjects);
        parentPanel.repaint();
        parentPanel.revalidate();
    }//GEN-LAST:event_jButtonProjectsActionPerformed

    private void jButtonSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSettingsActionPerformed
        parentPanel.removeAll();
        parentPanel.add(jPanelSettings);
        parentPanel.repaint();
        parentPanel.revalidate();
        jTextFieldSonarURL.setText(Exa2Pro.sonarURL);
        jTextFieldICodePath.setText(Exa2Pro.iCodePath);
        jTextFieldSonarPath.setText(Exa2Pro.sonarPath);
    }//GEN-LAST:event_jButtonSettingsActionPerformed

    private void jButtonAddNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddNewActionPerformed
        parentPanel.removeAll();
        parentPanel.add(jPanelAddNew);
        parentPanel.repaint();
        parentPanel.revalidate();
    }//GEN-LAST:event_jButtonAddNewActionPerformed

    private void jTextFieldProjectNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldProjectNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldProjectNameActionPerformed

    private void jButtonDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDirectoryActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select Folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
            System.out.println("getCurrentDirectory(): " +  chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : " +  chooser.getSelectedFile());
            jTextFieldProjectDirectory.setText(""+chooser.getSelectedFile());
        }
    }//GEN-LAST:event_jButtonDirectoryActionPerformed

    private void jTextFieldProjectDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldProjectDirectoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldProjectDirectoryActionPerformed

    private void jButtonSaveProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveProjectActionPerformed
        // If name and directory is provided
        if( !jTextFieldProjectName.getText().equals("") && !jTextFieldProjectDirectory.getText().equals("")){
            // Search if there is a project with this name
            boolean found= false;
            for(ProjectCredentials projectsCr:Exa2Pro.projecCredentialstList){
                if(jTextFieldProjectName.getText().equals(projectsCr.getProjectName())){
                    found=true;
                    break;
                }
            }
            // Create the new project and populate the List
            if(!found){
                ProjectCredentials pC=new ProjectCredentials(jTextFieldProjectName.getText(), jTextFieldProjectName.getText(),
                        jTextFieldProjectDirectory.getText());
                Project p= new Project(pC, pC.getProjects().size()+1+"");
                Exa2Pro.projecCredentialstList.add(pC);
                
                populateProjectList();
                
                p.projectVersionAnalysis();
                // Project saved and being transfered to Projects Panel
                jTextFieldProjectName.setText("");
                jTextFieldProjectDirectory.setText("");
                parentPanel.removeAll();
                parentPanel.add(jPanelProjects);
                parentPanel.repaint();
                parentPanel.revalidate();
            }
            // show a message to put a different name
            else{
                JOptionPane.showMessageDialog(new JFrame(), "Provide a different name.");
            }
        }
    }//GEN-LAST:event_jButtonSaveProjectActionPerformed

    private void jListProjectsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListProjectsMousePressed
        ProjectCredentials selectedProject= jListProjects.getSelectedValue();
        ProjectFrame projectF= new ProjectFrame(selectedProject.getProjects().get(selectedProject.getProjects().size()-1));
        projectF.setVisible(true);
    }//GEN-LAST:event_jListProjectsMousePressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Exa2Pro.saveSettingsToFile(jTextFieldSonarURL.getText(),jTextFieldICodePath.getText(),jTextFieldSonarPath.getText());
        Exa2Pro.getSettingFromFile();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButtonChangeRuleEffortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChangeRuleEffortActionPerformed
        parentPanel.removeAll();
        parentPanel.add(new HomeJPanelRuleEffort(this));
        parentPanel.repaint();
        parentPanel.revalidate();
    }//GEN-LAST:event_jButtonChangeRuleEffortActionPerformed

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
            java.util.logging.Logger.getLogger(HomeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomeFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonAddNew;
    private javax.swing.JButton jButtonChangeRuleEffort;
    private javax.swing.JButton jButtonDirectory;
    private javax.swing.JButton jButtonProjects;
    private javax.swing.JButton jButtonSaveProject;
    private javax.swing.JButton jButtonSettings;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<ProjectCredentials> jListProjects;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanelAddNew;
    private javax.swing.JPanel jPanelProjects;
    private javax.swing.JPanel jPanelSettings;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldICodePath;
    private javax.swing.JTextField jTextFieldProjectDirectory;
    private javax.swing.JTextField jTextFieldProjectName;
    private javax.swing.JTextField jTextFieldSonarPath;
    private javax.swing.JTextField jTextFieldSonarURL;
    private javax.swing.JPanel parentPanel;
    // End of variables declaration//GEN-END:variables

    public void changeParentPanelSettings(){
        parentPanel.removeAll();
        parentPanel.add(jPanelSettings);
        parentPanel.repaint();
        parentPanel.revalidate();
        jTextFieldSonarURL.setText(Exa2Pro.sonarURL);
        jTextFieldICodePath.setText(Exa2Pro.iCodePath);
        jTextFieldSonarPath.setText(Exa2Pro.sonarPath);
    }
}
