/*
 * New_AP.java
 *
 * Created on 15 de mayo de 2006, 9:37
 */
package castadiva_gui;

import castadiva.*;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

/**
 *
 * @author  jorge
 */
public class APModifyGUI extends javax.swing.JFrame {

    CastadivaModel m_model;

    /**
     * Creates new form New_AP
     */
    public APModifyGUI(CastadivaModel model) {
        m_model = model;
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        GUIReady();
    }

    public void GUIReady() {
        boolean value;
        value = FillAPComboBox();
        GetReadyTheButtons(value);
    }

    void FillProcessorComboBox() {
        List<String> processor;

        ProcessorComboBox.removeAllItems();
        File file = new File(m_model.DEFAULT_CONFIG_DIRECTORY + File.separator +
                m_model.DEFAULT_PROCESSOR_FILE);
        System.out.println(m_model.DEFAULT_CONFIG_DIRECTORY + File.separator +
                m_model.DEFAULT_PROCESSOR_FILE);
        processor = m_model.ReadTextFileInLines(file);
        if (processor.size() == 0) {
            processor.add("none");
        }
        for (int i = 0; i < processor.size(); i++) {
            ProcessorComboBox.addItem(processor.get(i));
        }

        //Get the selected processor.
        if (m_model.HowManyAP() > 0) {
            ProcessorComboBox.setSelectedIndex(processor.indexOf(m_model.GetAP(APComboBox.getSelectedIndex()).WhatProcessor()));
        }
    }

    boolean FillAPComboBox() {
        APComboBox.removeAllItems();
        if (m_model.HowManyAP() == 0) {
            return false;
        }
        //Preparamos los elementos relativos a los Puntos de acceso.
        for (int i = 0; i < m_model.HowManyAP(); i++) {
            APComboBox.addItem(m_model.GetAP(i).WhatAP());
        }
        APComboBox.setSelectedIndex(0);
        return true;
    }

    public void addPingButtonListener(ActionListener al) {
        PingButton.addActionListener(al);
    }

    public void addOkButtonListener(ActionListener al) {
        ModifyButton.addActionListener(al);
    }

    public void addDeleteButtonListener(ActionListener al) {
        DeleteButton.addActionListener(al);
    }

    public String GiveMeTheIp() {
        return IPtext.getText();
    }

    public String GiveMeTheWifiIp() {
        return WifiIPtext.getText();
    }

    public String GiveMeTheId() {
        return APComboBox.getSelectedItem().toString();
    }

    public String GiveMeTheWifiMac() {
        return WifiMacTextField.getText();
    }

    public String GiveMeTheSshUser() {
        return UsuarioText.getText();
    }

    public String GiveMeTheSshPwd() {
        return String.valueOf(PasswordField.getPassword());
    }

    public String GiveMeTheWorkingDirectory() {
        return WorkingDirectoryTextField.getText();
    }

    public String GiveMeTheProcessor() {
        return ProcessorComboBox.getSelectedItem().toString();
    }

    public Integer GiveMeTheSelectedIndex() {
        return APComboBox.getSelectedIndex();
    }

    public Integer GiveMeTheChannel() {
        return (Integer) ChannelSpinner.getValue();
    }

    public String GiveMeTheMode() {
        return ModeComboBox.getSelectedItem().toString();
    }

    public String GiveMeWifiDevice() {
        return WifiDeviceTextField.getText();
    }

    public String GiveMeGW() {
        return GWTextField.getText();
    }

    public void ChannelInRange() {
        if ((Integer) ChannelSpinner.getValue() < 1) {
            ChannelSpinner.setValue(1);
        }
        if ((Integer) ChannelSpinner.getValue() > 14) {
            ChannelSpinner.setValue(14);
        }
    }

    public boolean IsWindowEditable() {
        return (APComboBox.getItemCount() > 0);
    }

    public void ShowOkDialog() {
        OKDialog.setBounds(0, 0, 300, 180);
        OKDialog.setVisible(true);
        OKDialog.setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (OKDialog.getWidth() / 2), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (OKDialog.getHeight() / 2));
    }

    void SelectAdecuatedMode() {
        List<String> objects = new ArrayList<String>();
        for (int i = 0; i < ModeComboBox.getItemCount(); i++) {
            objects.add(ModeComboBox.getItemAt(i).toString());
        }
        ModeComboBox.setSelectedIndex(objects.indexOf(m_model.selectionedAP1.WhatMode()));
    }

    private void GetReadyTheButtons(boolean value) {
        if (APComboBox.getItemCount() == 0) {
            value = !value;
        }
        ModifyButton.setEnabled(value);
        IPtext.setEnabled(value);
        PingButton.setEnabled(value);
        UsuarioText.setEnabled(value);
        PasswordField.setEnabled(value);
        WifiIPtext.setEnabled(value);
        WifiMacTextField.setEnabled(value);
        DeleteButton.setEnabled(value);
        WorkingDirectoryTextField.setEnabled(value);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        OKDialog = new javax.swing.JDialog();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        AceptarButton = new javax.swing.JButton();
        SSHPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        UsuarioText = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        PasswordField = new javax.swing.JPasswordField();
        ButtonPanel = new javax.swing.JPanel();
        ModifyButton = new javax.swing.JButton();
        CloseButton = new javax.swing.JButton();
        NetPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        IPtext = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        WifiIPtext = new javax.swing.JTextField();
        PingButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        WorkingDirectoryTextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        WifiMacTextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        ProcessorComboBox = new javax.swing.JComboBox();
        ChannelSpinner = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        ModeComboBox = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        WifiDeviceTextField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        GWTextField = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        IDPanel = new javax.swing.JPanel();
        APComboBox = new javax.swing.JComboBox();
        DeleteButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        OKDialog.setTitle("¡Modificado!");

        jLabel3.setText("¡La conexión con el punto de acceso");

        jLabel5.setText("ha sido modificada!");

        AceptarButton.setText("Aceptar");
        AceptarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AceptarButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout OKDialogLayout = new org.jdesktop.layout.GroupLayout(OKDialog.getContentPane());
        OKDialog.getContentPane().setLayout(OKDialogLayout);
        OKDialogLayout.setHorizontalGroup(
            OKDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(OKDialogLayout.createSequentialGroup()
                .add(OKDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(OKDialogLayout.createSequentialGroup()
                        .add(35, 35, 35)
                        .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 234, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(OKDialogLayout.createSequentialGroup()
                        .add(91, 91, 91)
                        .add(jLabel5)))
                .addContainerGap(34, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, OKDialogLayout.createSequentialGroup()
                .addContainerGap(119, Short.MAX_VALUE)
                .add(AceptarButton)
                .add(103, 103, 103))
        );
        OKDialogLayout.setVerticalGroup(
            OKDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(OKDialogLayout.createSequentialGroup()
                .add(31, 31, 31)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 29, Short.MAX_VALUE)
                .add(AceptarButton)
                .addContainerGap())
        );

        setTitle("Change AP");
        setResizable(false);

        SSHPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("User:");

        jLabel6.setText("Password:");

        org.jdesktop.layout.GroupLayout SSHPanelLayout = new org.jdesktop.layout.GroupLayout(SSHPanel);
        SSHPanel.setLayout(SSHPanelLayout);
        SSHPanelLayout.setHorizontalGroup(
            SSHPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SSHPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(SSHPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(UsuarioText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(15, 15, 15)
                .add(SSHPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(PasswordField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                    .add(jLabel6))
                .addContainerGap())
        );
        SSHPanelLayout.setVerticalGroup(
            SSHPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, SSHPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(SSHPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(SSHPanelLayout.createSequentialGroup()
                        .add(SSHPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(jLabel6))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(UsuarioText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(PasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        ButtonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        ModifyButton.setText("Change");

        CloseButton.setText("Close");
        CloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout ButtonPanelLayout = new org.jdesktop.layout.GroupLayout(ButtonPanel);
        ButtonPanel.setLayout(ButtonPanelLayout);
        ButtonPanelLayout.setHorizontalGroup(
            ButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(ModifyButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 59, Short.MAX_VALUE)
                .add(CloseButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        ButtonPanelLayout.setVerticalGroup(
            ButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, ButtonPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(ButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ModifyButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(CloseButton))
                .addContainerGap())
        );

        NetPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setText("Control IP Adress:");

        jLabel7.setText("Wifi IP Address:");

        PingButton.setText("Ping");

        jLabel8.setText("NFS Directory:");

        jLabel9.setText("Wifi Mac address:");

        jLabel10.setText("Processor:");

        ChannelSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ChannelSpinnerStateChanged(evt);
            }
        });

        jLabel11.setText("Channel:");

        ModeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ad-Hoc", "Managed", "Master", "Repeater", "Secondary", "Monitor", "Auto" }));

        jLabel12.setText("Mode:");

        jLabel13.setText("Device:");

        GWTextField.setText("192.168.1.15");

        jLabel14.setText("Gateway:");

        jCheckBox1.setText("Gateway");

        org.jdesktop.layout.GroupLayout NetPanelLayout = new org.jdesktop.layout.GroupLayout(NetPanel);
        NetPanel.setLayout(NetPanelLayout);
        NetPanelLayout.setHorizontalGroup(
            NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(NetPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(NetPanelLayout.createSequentialGroup()
                        .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel4)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, NetPanelLayout.createSequentialGroup()
                                .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel7)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, IPtext, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, WifiIPtext, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                                .add(17, 17, 17)
                                .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(PingButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, WorkingDirectoryTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)))
                            .add(NetPanelLayout.createSequentialGroup()
                                .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(NetPanelLayout.createSequentialGroup()
                                        .add(ModeComboBox, 0, 129, Short.MAX_VALUE)
                                        .add(17, 17, 17))
                                    .add(NetPanelLayout.createSequentialGroup()
                                        .add(jLabel12)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(WifiDeviceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel13))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, ChannelSpinner)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel11)))
                            .add(NetPanelLayout.createSequentialGroup()
                                .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(NetPanelLayout.createSequentialGroup()
                                        .add(WifiMacTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                                        .add(17, 17, 17))
                                    .add(NetPanelLayout.createSequentialGroup()
                                        .add(jLabel9)
                                        .add(37, 37, 37)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel10)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, ProcessorComboBox, 0, 121, Short.MAX_VALUE))))
                        .addContainerGap())
                    .add(NetPanelLayout.createSequentialGroup()
                        .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(NetPanelLayout.createSequentialGroup()
                                .add(GWTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jCheckBox1))
                            .add(jLabel14))
                        .add(52, 52, 52))))
        );
        NetPanelLayout.setVerticalGroup(
            NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(NetPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(PingButton)
                    .add(IPtext, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(WorkingDirectoryTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(WifiIPtext, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(jLabel11)
                    .add(jLabel13))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ModeComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ChannelSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(WifiDeviceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(WifiMacTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ProcessorComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(9, 9, 9)
                .add(jLabel14)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(NetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(GWTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jCheckBox1))
                .addContainerGap())
        );

        IDPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        APComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                APComboBoxActionPerformed(evt);
            }
        });

        DeleteButton.setText("Delete");

        jLabel1.setText("Name:");

        org.jdesktop.layout.GroupLayout IDPanelLayout = new org.jdesktop.layout.GroupLayout(IDPanel);
        IDPanel.setLayout(IDPanelLayout);
        IDPanelLayout.setHorizontalGroup(
            IDPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(IDPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(IDPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, IDPanelLayout.createSequentialGroup()
                        .add(APComboBox, 0, 139, Short.MAX_VALUE)
                        .add(14, 14, 14)
                        .add(DeleteButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 114, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel1))
                .addContainerGap())
        );
        IDPanelLayout.setVerticalGroup(
            IDPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, IDPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(IDPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(APComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(DeleteButton))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, NetPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, ButtonPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(SSHPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, IDPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(IDPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(NetPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(SSHPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ButtonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void ChannelSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ChannelSpinnerStateChanged
        ChannelInRange();
    }//GEN-LAST:event_ChannelSpinnerStateChanged

    private void AceptarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AceptarButtonActionPerformed
        OKDialog.dispose();
        GUIReady();
    }//GEN-LAST:event_AceptarButtonActionPerformed

    private void APComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_APComboBoxActionPerformed
        if (APComboBox.getItemCount() > 0) {
            m_model.selectionedAP1 = m_model.GetAP(APComboBox.getSelectedIndex());
            IPtext.setText(m_model.selectionedAP1.WhatEthIP());
            WifiIPtext.setText(m_model.selectionedAP1.WhatWifiIP());
            WifiMacTextField.setText(m_model.selectionedAP1.WhatWifiMac());
            UsuarioText.setText(m_model.selectionedAP1.WhatUser());
            PasswordField.setText(m_model.selectionedAP1.WhatPwd());
            WorkingDirectoryTextField.setText(m_model.selectionedAP1.WhatWorkingDirectory());
            ChannelSpinner.setValue(m_model.selectionedAP1.WhatChannel());
            WifiDeviceTextField.setText(m_model.selectionedAP1.WhatWifiDevice());
            SelectAdecuatedMode();
            FillProcessorComboBox();
        }
        GetReadyTheButtons(true);
    }//GEN-LAST:event_APComboBoxActionPerformed

    private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_CloseButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox APComboBox;
    private javax.swing.JButton AceptarButton;
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JSpinner ChannelSpinner;
    private javax.swing.JButton CloseButton;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JTextField GWTextField;
    private javax.swing.JPanel IDPanel;
    private javax.swing.JTextField IPtext;
    private javax.swing.JComboBox ModeComboBox;
    private javax.swing.JButton ModifyButton;
    private javax.swing.JPanel NetPanel;
    private javax.swing.JDialog OKDialog;
    private javax.swing.JPasswordField PasswordField;
    private javax.swing.JButton PingButton;
    private javax.swing.JComboBox ProcessorComboBox;
    private javax.swing.JPanel SSHPanel;
    private javax.swing.JTextField UsuarioText;
    private javax.swing.JTextField WifiDeviceTextField;
    private javax.swing.JTextField WifiIPtext;
    private javax.swing.JTextField WifiMacTextField;
    private javax.swing.JTextField WorkingDirectoryTextField;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    // End of variables declaration//GEN-END:variables
}
