/*
 * HelpWindow.java
 *
 * Created on 16 de julio de 2007, 9:55
 */

package castadiva_gui;

import castadiva.*;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

/**
 *
 * @author  jorge
 */
public class HelpWindow extends javax.swing.JFrame {
    CastadivaModel m_model;
    
    /** Creates new form HelpWindow */
    public HelpWindow(CastadivaModel model) {
        m_model = model;
        initComponents();
        setLocation((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-(int)(this.getWidth()/2),
                (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-(int)(this.getHeight()/2));
    }
    
    public void UpdateText(String text){      
        Blackboard.setText(text);
    }
    
    public int GetLanguageIndex(){
        return LanguageComboBox.getSelectedIndex();
    }
    
    
    
     /*********************************************************************
     *
     *                             LISTENERS
     *
     *********************************************************************/
    
    
    public void addBeforeButtonListener(ActionListener al){
        BeforeButton.addActionListener(al);
    }
    
    public void addAfterButtonListener(ActionListener al){
        AfterButton.addActionListener(al);
    }
    
    public void addLanguageComboBoxListener(ActionListener al){
        LanguageComboBox.addActionListener(al);
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        Blackboard = new javax.swing.JTextPane();
        CloseButton = new javax.swing.JButton();
        AfterButton = new javax.swing.JButton();
        BeforeButton = new javax.swing.JButton();
        LanguageComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

        setTitle("HELP");
        setResizable(false);

        Blackboard.setEditable(false);
        jScrollPane1.setViewportView(Blackboard);

        CloseButton.setText("Accept");
        CloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseButtonActionPerformed(evt);
            }
        });

        AfterButton.setText(">>");

        BeforeButton.setText("<<");

        LanguageComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "English", "Español" }));

        jLabel1.setText("Language:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 646, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CloseButton, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(BeforeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(AfterButton))
                    .addComponent(LanguageComboBox, 0, 126, Short.MAX_VALUE)
                    .addComponent(jLabel1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LanguageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 162, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(AfterButton)
                            .addComponent(BeforeButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CloseButton)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_CloseButtonActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AfterButton;
    private javax.swing.JButton BeforeButton;
    private javax.swing.JTextPane Blackboard;
    private javax.swing.JButton CloseButton;
    private javax.swing.JComboBox LanguageComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
}