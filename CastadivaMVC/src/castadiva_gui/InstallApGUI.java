/*
 * InstallApGUI.java
 *
 * Created on 7 de septiembre de 2006, 11:46
 */

package castadiva_gui;

import castadiva.*;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author  jorge
 */
public class InstallApGUI extends javax.swing.JFrame {
    CastadivaModel m_model;
    /**
     * Creates new form InstallApGUI
     */
     public InstallApGUI(CastadivaModel model) {
        m_model = model;
        initComponents();
        setLocation((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-(int)(this.getWidth()/2), 
                (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-(int)(this.getHeight()/2));
    }
   
    
    public void FillComponents(String EthIp, String WifiIp,
            String ApWorkingDirectory, String SshUser, String SshPwd){
        EthIpTextField.setText(EthIp);
        WifiIpTextField.setText(WifiIp);
        ApNfsTextField.setText(ApWorkingDirectory);
        GatewayTextField.setText(m_model.computer.WhatIP());
        ComputerFolderTextField.setText(m_model.computer.WhatWorkingDirectory());
        SshUserTextField.setText(SshUser);
        SshPasswordField.setText(SshPwd);
    }
     
    public void addCloseButtonListener(ActionListener al){
        CancelButton.addActionListener(al);
    }
    
    public void addInstallButtonListener(ActionListener al){
        InstallButton.addActionListener(al);
    }
   
    public String WhatEthDevice(){
        return EthernetTextField.getText();
    }
    
    public String WhatWifiDevice(){
        return WifiTextField.getText();
    }
    
    public String WhatSwitchDevice(){
        return SwitchTextField.getText();
    }
    
    public String WhatBridgeDevice(){
        return BridgeTextField.getText();
    }
    
    public String WhatEthIp(){
        return EthIpTextField.getText();
    }
    
    public String WhatWifiIp(){
        return WifiIpTextField.getText();
    }
    
    public String WhatGatewayIp(){
        return GatewayTextField.getText();
    }
    
    public String WhatComputerFolder(){
        return ComputerFolderTextField.getText();
    }
    
    public String WhatApNfsFolder(){
        return ApNfsTextField.getText();
    }
    
    public String WhatApScriptFolder(){
        return ScriptFolderTextField.getText();
    }
    
    public String WhatSshUser(){
        return SshUserTextField.getText();
    }
    
    public String WhatSshPwd(){
        return String.valueOf(SshPasswordField.getPassword());
    }
    
    public void ShowEndInstallationMessage(){
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame, "Wait a few minutes to let the installation" +
                " to be finished and reboot the access point.", "Reboot...",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * The current IP is the IP to connect to the AP the first
     * time to install and configure it. Should be the factory default 
     * IP.
     */
    public String WhatCurrentIp(){
        return CurrentIpTextField.getText();
    }
    
    /**
     * Return the user defined SSID.
     */
    public String WhatSSID(){
        return SSIDTextField.getText();
    }
       
  
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        DevicesPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        EthernetTextField = new javax.swing.JTextField();
        WifiTextField = new javax.swing.JTextField();
        SwitchTextField = new javax.swing.JTextField();
        BridgeTextField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        InternetPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        GatewayTextField = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        EthIpTextField = new javax.swing.JTextField();
        WifiIpTextField = new javax.swing.JTextField();
        SSIDTextField = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        FoldersPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        ComputerFolderTextField = new javax.swing.JTextField();
        ApNfsTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        ScriptFolderTextField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        SSHPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        SshPasswordField = new javax.swing.JPasswordField();
        SshUserTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        CurrentIpTextField = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        InstallButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();

        setTitle("Installing an AP");
        setResizable(false);

        DevicesPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Ethernet device:");

        jLabel4.setText("Wifi device:");

        jLabel2.setText("Switch device:");

        jLabel3.setText("Bridge device:");

        EthernetTextField.setText("eth0.0");

        WifiTextField.setText("wl0.0");

        SwitchTextField.setText("eth0");

        BridgeTextField.setText("brlan");

        jLabel13.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel13.setText("AP net devices");

        org.jdesktop.layout.GroupLayout DevicesPanelLayout = new org.jdesktop.layout.GroupLayout(DevicesPanel);
        DevicesPanel.setLayout(DevicesPanelLayout);
        DevicesPanelLayout.setHorizontalGroup(
            DevicesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(DevicesPanelLayout.createSequentialGroup()
                .add(DevicesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(DevicesPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(DevicesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(DevicesPanelLayout.createSequentialGroup()
                                .add(DevicesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(DevicesPanelLayout.createSequentialGroup()
                                        .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(12, 12, 12))
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, DevicesPanelLayout.createSequentialGroup()
                                        .add(DevicesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                            .add(org.jdesktop.layout.GroupLayout.LEADING, SwitchTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                                            .add(EthernetTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                                .add(DevicesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel4)
                                    .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, BridgeTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, WifiTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)))
                            .add(jLabel2)))
                    .add(DevicesPanelLayout.createSequentialGroup()
                        .add(70, 70, 70)
                        .add(jLabel13)))
                .addContainerGap())
        );
        DevicesPanelLayout.setVerticalGroup(
            DevicesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, DevicesPanelLayout.createSequentialGroup()
                .add(jLabel13)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 18, Short.MAX_VALUE)
                .add(DevicesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(DevicesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(EthernetTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(WifiTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(DevicesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(DevicesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(SwitchTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(BridgeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        InternetPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setText("Gateway:");

        jLabel14.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel14.setText("Net addresses");

        jLabel15.setText("Ethernet IP:");

        jLabel16.setText("Wifi IP:");

        SSIDTextField.setText("CASTADIVA");

        jLabel18.setText("SSID:");

        org.jdesktop.layout.GroupLayout InternetPanelLayout = new org.jdesktop.layout.GroupLayout(InternetPanel);
        InternetPanel.setLayout(InternetPanelLayout);
        InternetPanelLayout.setHorizontalGroup(
            InternetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(InternetPanelLayout.createSequentialGroup()
                .add(InternetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(InternetPanelLayout.createSequentialGroup()
                        .add(68, 68, 68)
                        .add(jLabel14))
                    .add(InternetPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(InternetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jLabel6)
                            .add(GatewayTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .add(jLabel15)
                            .add(EthIpTextField))
                        .add(9, 9, 9)
                        .add(InternetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel16)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, WifiIpTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                            .add(jLabel18)
                            .add(SSIDTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))))
                .addContainerGap())
        );
        InternetPanelLayout.setVerticalGroup(
            InternetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(InternetPanelLayout.createSequentialGroup()
                .add(jLabel14)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(InternetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel15)
                    .add(jLabel16))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(InternetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(WifiIpTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(EthIpTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(InternetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(jLabel18))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(InternetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(GatewayTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(SSIDTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        FoldersPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setText("NFS folder in the computer:");

        jLabel7.setText("NFS Folder in the AP:");

        jLabel8.setText("AP scripts destination folder:");

        ScriptFolderTextField.setText("/CASTADIVA");

        jLabel17.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel17.setText("Folders");

        org.jdesktop.layout.GroupLayout FoldersPanelLayout = new org.jdesktop.layout.GroupLayout(FoldersPanel);
        FoldersPanel.setLayout(FoldersPanelLayout);
        FoldersPanelLayout.setHorizontalGroup(
            FoldersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(FoldersPanelLayout.createSequentialGroup()
                .add(FoldersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(FoldersPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(FoldersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(ComputerFolderTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                            .add(jLabel5)
                            .add(jLabel8)
                            .add(jLabel7)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, ApNfsTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                            .add(ScriptFolderTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)))
                    .add(FoldersPanelLayout.createSequentialGroup()
                        .add(88, 88, 88)
                        .add(jLabel17)))
                .addContainerGap())
        );
        FoldersPanelLayout.setVerticalGroup(
            FoldersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, FoldersPanelLayout.createSequentialGroup()
                .add(jLabel17)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 8, Short.MAX_VALUE)
                .add(jLabel5)
                .add(3, 3, 3)
                .add(ComputerFolderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel8)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ScriptFolderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel7)
                .add(3, 3, 3)
                .add(ApNfsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        SSHPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel9.setText("SSH user:");

        jLabel10.setText("SSH pwd:");

        jLabel11.setText("Current IP:");

        CurrentIpTextField.setText("192.168.1.1");
        CurrentIpTextField.setToolTipText("The current IP is the IP to connect to the AP the first time to install and configure it. Should be the factory default IP.");

        jLabel12.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel12.setText("SSH connection");

        org.jdesktop.layout.GroupLayout SSHPanelLayout = new org.jdesktop.layout.GroupLayout(SSHPanel);
        SSHPanel.setLayout(SSHPanelLayout);
        SSHPanelLayout.setHorizontalGroup(
            SSHPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SSHPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(SSHPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(SSHPanelLayout.createSequentialGroup()
                        .add(SSHPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel11)
                            .add(SSHPanelLayout.createSequentialGroup()
                                .add(SSHPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, CurrentIpTextField)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel9)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, SshUserTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 102, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 31, Short.MAX_VALUE)
                                .add(SSHPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel10)
                                    .add(SshPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 102, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, SSHPanelLayout.createSequentialGroup()
                        .add(jLabel12)
                        .add(66, 66, 66))))
        );
        SSHPanelLayout.setVerticalGroup(
            SSHPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, SSHPanelLayout.createSequentialGroup()
                .add(jLabel12)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(SSHPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(SSHPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(SshUserTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(SshPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel11)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(CurrentIpTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        InstallButton.setText("Install");

        CancelButton.setText("Close");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(DevicesPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, InternetPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(InstallButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 43, Short.MAX_VALUE)
                        .add(CancelButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(23, 23, 23))
                    .add(layout.createSequentialGroup()
                        .add(FoldersPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(SSHPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(DevicesPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(InternetPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(FoldersPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(SSHPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(InstallButton)
                    .add(CancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ApNfsTextField;
    private javax.swing.JTextField BridgeTextField;
    private javax.swing.JButton CancelButton;
    private javax.swing.JTextField ComputerFolderTextField;
    private javax.swing.JTextField CurrentIpTextField;
    private javax.swing.JPanel DevicesPanel;
    private javax.swing.JTextField EthIpTextField;
    private javax.swing.JTextField EthernetTextField;
    private javax.swing.JPanel FoldersPanel;
    private javax.swing.JTextField GatewayTextField;
    private javax.swing.JButton InstallButton;
    private javax.swing.JPanel InternetPanel;
    private javax.swing.JPanel SSHPanel;
    private javax.swing.JTextField SSIDTextField;
    private javax.swing.JTextField ScriptFolderTextField;
    private javax.swing.JPasswordField SshPasswordField;
    private javax.swing.JTextField SshUserTextField;
    private javax.swing.JTextField SwitchTextField;
    private javax.swing.JTextField WifiIpTextField;
    private javax.swing.JTextField WifiTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
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