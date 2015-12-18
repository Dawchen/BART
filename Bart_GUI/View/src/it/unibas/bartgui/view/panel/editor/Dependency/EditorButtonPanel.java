/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.view.panel.editor.Dependency;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class EditorButtonPanel extends javax.swing.JPanel {

    /**
     * Creates new form editorButtonPanel
     */
    public EditorButtonPanel() {
        initComponents();
        resetButton.setEnabled(false);
        resetButton.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        saveRButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 204)));

        org.openide.awt.Mnemonics.setLocalizedText(saveRButton, org.openide.util.NbBundle.getMessage(EditorButtonPanel.class, "EditorButtonPanel.saveRButton.text")); // NOI18N
        saveRButton.setToolTipText(org.openide.util.NbBundle.getMessage(EditorButtonPanel.class, "EditorButtonPanel.saveRButton.toolTipText")); // NOI18N
        saveRButton.setActionCommand(org.openide.util.NbBundle.getMessage(EditorButtonPanel.class, "EditorButtonPanel.saveRButton.actionCommand")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(resetButton, org.openide.util.NbBundle.getMessage(EditorButtonPanel.class, "EditorButtonPanel.resetButton.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(saveRButton)
                .addGap(18, 18, 18)
                .addComponent(resetButton)
                .addContainerGap(345, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveRButton)
                    .addComponent(resetButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton resetButton;
    private javax.swing.JButton saveRButton;
    // End of variables declaration//GEN-END:variables


    /**
     * @return the resetButton
     */
    public javax.swing.JButton getResetButton() {
        return resetButton;
    }

    /**
     * @return the saveRButton
     */
    public javax.swing.JButton getSaveRButton() {
        return saveRButton;
    }
}