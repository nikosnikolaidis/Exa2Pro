/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panels_frames;

import admit.ModelParameter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nikos
 */
public class PanelAdmitModelValueInput extends javax.swing.JPanel {

    public List<PanelAdmitModelEachVariable> variablesPanels= new ArrayList<>();
    private ModelParameter model;
    
    /**
     * Creates new form PanelAdmitModelValueInput
     */
    public PanelAdmitModelValueInput(ModelParameter model) {
        initComponents();
        this.model = model;
        jLabelName.setText(model.getName());
        
        for(String st: model.getEquation().getParameters().keySet()){
            PanelAdmitModelEachVariable p= new PanelAdmitModelEachVariable(st, model.getEquation().getParameters().get(st));
            variablesPanels.add(p);
            jPanel.add(p);
        }
    }
    
    public String getName(){
        return jLabelName.getText();
    }
    
    public ModelParameter getModelParameter(){
        return model;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelName = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel = new javax.swing.JPanel();

        jLabelName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelName.setText("jLabel1");

        jPanel.setLayout(new javax.swing.BoxLayout(jPanel, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(jPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelName)
                        .addGap(0, 271, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelName;
    private javax.swing.JPanel jPanel;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
