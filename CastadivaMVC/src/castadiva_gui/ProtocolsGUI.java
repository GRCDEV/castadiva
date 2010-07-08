/*
 * ProtocolsGUI.java
 *
 * Created on 20 de noviembre de 2008, 11:33
 */
package castadiva_gui;

import castadiva.*;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

/**
 *
 * @author  nacho
 */
public class ProtocolsGUI extends javax.swing.JFrame {

    private CastadivaModel model;

    /** Creates new form ProtocolsGUI */
    public ProtocolsGUI(CastadivaModel m) {
        this.model = m;
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        JPanelDatos = new javax.swing.JPanel();
        JTjar = new javax.swing.JTextField();
        jLjar = new javax.swing.JLabel();
        JTruta = new javax.swing.JTextField();
        jLruta = new javax.swing.JLabel();
        jLflags = new javax.swing.JLabel();
        jTflags = new javax.swing.JTextField();
        jPanelConf = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTAconf = new javax.swing.JTextArea();
        jLabelContenidoConf = new javax.swing.JLabel();
        jLconf = new javax.swing.JLabel();
        JTpath = new javax.swing.JTextField();
        jLabelTitulo = new javax.swing.JLabel();
        JBGuardar = new javax.swing.JButton();
        JBcancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Configure Protocols");

        JPanelDatos.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        JPanelDatos.setFocusable(false);

        jLjar.setText("Name:");
        jLjar.setFocusable(false);

        JTruta.setText("/usr/sbin/");

        jLruta.setText("Bin:");
        jLruta.setFocusable(false);

        jLflags.setText("Flags:");
        jLflags.setFocusable(false);

        jTflags.setText("-d 0");

        javax.swing.GroupLayout JPanelDatosLayout = new javax.swing.GroupLayout(JPanelDatos);
        JPanelDatos.setLayout(JPanelDatosLayout);
        JPanelDatosLayout.setHorizontalGroup(
            JPanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JPanelDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(JPanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLjar)
                    .addGroup(JPanelDatosLayout.createSequentialGroup()
                        .addGroup(JPanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLruta)
                            .addComponent(jLflags))
                        .addGap(35, 35, 35)
                        .addGroup(JPanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTflags, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                            .addComponent(JTruta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                            .addComponent(JTjar, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE))))
                .addContainerGap())
        );
        JPanelDatosLayout.setVerticalGroup(
            JPanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JPanelDatosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(JPanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(JTjar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLjar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(JPanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(JTruta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLruta))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(JPanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jTflags, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLflags))
                .addContainerGap())
        );

        jPanelConf.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTAconf.setColumns(20);
        jTAconf.setLineWrap(true);
        jTAconf.setRows(5);
        jScrollPane1.setViewportView(jTAconf);

        jLabelContenidoConf.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        jLabelContenidoConf.setText("Configuration File Content:");

        jLconf.setText("Path protocol.conf:");
        jLconf.setFocusable(false);

        JTpath.setText("/etc/");

        javax.swing.GroupLayout jPanelConfLayout = new javax.swing.GroupLayout(jPanelConf);
        jPanelConf.setLayout(jPanelConfLayout);
        jPanelConfLayout.setHorizontalGroup(
            jPanelConfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelConfLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelConfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelConfLayout.createSequentialGroup()
                        .addComponent(jLconf)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JTpath, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE))
                    .addComponent(jLabelContenidoConf, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        jPanelConfLayout.setVerticalGroup(
            jPanelConfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelConfLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelContenidoConf)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanelConfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLconf)
                    .addComponent(JTpath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabelTitulo.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabelTitulo.setText("New Protocol Configuration");
        jLabelTitulo.setBorder(null);

        JBGuardar.setText("Create plugin");

        JBcancelar.setText("Cancel");
        JBcancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JBcancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(JPanelDatos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(432, 432, 432)
                        .addComponent(JBGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(JBcancelar))
                    .addComponent(jPanelConf, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelTitulo))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {JBGuardar, JBcancelar});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitulo)
                .addGap(8, 8, 8)
                .addComponent(JPanelDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelConf, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JBGuardar)
                    .addComponent(JBcancelar))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void JBcancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBcancelarActionPerformed
    dispose();
}//GEN-LAST:event_JBcancelarActionPerformed


    public void addCreatePluginListener(ActionListener a1) {
        JBGuardar.addActionListener(a1);
    }

    public String getBinaryFilePath() {
        return JTruta.getText();
    }
    public String getConfigurationFilePath() {
        return  JTpath.getText().trim();
    }

    public String getJarFileName() {
        return JTjar.getText();
    }

    public String getProtocolConfiguration() {
        return jTAconf.getText();
    }

    public String getProtocolFlags() {
        return jTflags.getText();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton JBGuardar;
    private javax.swing.JButton JBcancelar;
    private javax.swing.JPanel JPanelDatos;
    private javax.swing.JTextField JTjar;
    private javax.swing.JTextField JTpath;
    private javax.swing.JTextField JTruta;
    private javax.swing.JLabel jLabelContenidoConf;
    private javax.swing.JLabel jLabelTitulo;
    private javax.swing.JLabel jLconf;
    private javax.swing.JLabel jLflags;
    private javax.swing.JLabel jLjar;
    private javax.swing.JLabel jLruta;
    private javax.swing.JPanel jPanelConf;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTAconf;
    private javax.swing.JTextField jTflags;
    // End of variables declaration//GEN-END:variables
}
