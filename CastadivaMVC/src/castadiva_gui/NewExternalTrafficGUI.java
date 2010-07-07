/*
 * NewExternalApplication.java
 *
 * Created on 12 de julio de 2007, 12:33
 */
package castadiva_gui;

import castadiva.*;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author  jorge
 */
public class NewExternalTrafficGUI extends javax.swing.JFrame {

    CastadivaModel m_model;
    List<String[]> applications;

    /** Creates new form NewExternalApplication */
    public NewExternalTrafficGUI(CastadivaModel model) {
        m_model = model;
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        GUIReady();
    }

    /**
     * Start the window with default data.
     */
    private void GUIReady() {
        applications = ObtainStoredApplicationData();
        FillStandarAppComboBox();
        UpdateWindow();
    }

    /**
     * Make visual changes to the window.
     */
    public void UpdateWindow() {
        FillApComboBox1();
        FillApComboBox2();
        FillExternalTrafficComboBox();
        if (ApComboBox1.getItemCount() > 0) {
            AttachButton.setEnabled(true);
        } else {
            AttachButton.setEnabled(false);
        }
        if (ExternalTrafficComboBox.getItemCount() > 0) {
            SelectExternalTrafficFlow(ExternalTrafficComboBox.getSelectedIndex());
        }

    }

    /**
     * Prepare windows after a successful delete option.
     */
    public void UpdateAfterDelete() {
        ClearInputText();
        FillExternalTrafficComboBox();
    }

    /**
     * Fill the AP combobox with all ap that exists in the system.
     */
    private boolean FillApComboBox1() {
        ApComboBox1.removeAllItems();
        if (m_model.HowManyAP() == 0) {
            return false;
        }
        //Preparamos los elementos relativos a los Puntos de acceso.
        for (int i = 0; i < m_model.HowManyAP(); i++) {
            ApComboBox1.addItem(m_model.GetAP(i).WhatAP());
        }
        ApComboBox1.setSelectedIndex(0);
        return true;
    }

    /**
     * Fill the AP combobox with all ap that exists in the system.
     */
    private boolean FillApComboBox2() {
        ApComboBox2.removeAllItems();
        if (m_model.HowManyAP() == 0) {
            return false;
        }
        //Preparamos los elementos relativos a los Puntos de acceso.
        for (int i = 0; i < m_model.HowManyAP(); i++) {
            ApComboBox2.addItem(m_model.GetAP(i).WhatAP());
        }
        ApComboBox2.setSelectedIndex(0);
        return true;
    }

    /**
     * Obtain the networks of the selected ap.
     */
    private boolean FillDeviceComboBox1() {
        DeviceComboBox1.removeAllItems();
        if (ApComboBox1.getItemCount() > 0) {
            DeviceComboBox1.addItem(m_model.selectionedAP1.WhatEthIP());
            DeviceComboBox1.addItem(m_model.selectionedAP1.WhatWifiIP());
            return true;
        }
        return false;
    }

    /**
     * Obtain the networks of the selected ap.
     */
    private boolean FillDeviceComboBox2() {
        DeviceComboBox2.removeAllItems();
        if (ApComboBox2.getItemCount() > 0) {
            DeviceComboBox2.addItem(m_model.selectionedAP2.WhatEthIP());
            DeviceComboBox2.addItem(m_model.selectionedAP2.WhatWifiIP());
            return true;
        }
        return false;
    }

    /**
     * Fill the application combobox with the datas contained in the list applications.
     */
    private void FillStandarAppComboBox() {
        this.StandarAppComboBox.removeAllItems();
        String[] application;
        for (int i = 0; i < applications.size(); i++) {
            application = applications.get(i);
            StandarAppComboBox.addItem(application[0]);
        }
    }

    /**
     * Fill the External Traffic ComboBox with all inserted ExternalTraffic data.
     */
    private void FillExternalTrafficComboBox() {
        ExternalTraffic externalTraffic;
        ExternalTrafficComboBox.removeAllItems();
        for (int i = 0; i < m_model.externalTrafficFlow.size(); i++) {
            externalTraffic = (ExternalTraffic) m_model.externalTrafficFlow.get(i);
            ExternalTrafficComboBox.addItem(externalTraffic.name);
        }
    }

    /**
     * Obtain data for an application from a file or set a default one.
     */
    private List ObtainStoredApplicationData() {
        List lines;
        String line;
        Integer help_lines = 1;
        List data = new ArrayList();
        File file = new File(m_model.DEFAULT_CONFIG_DIRECTORY + File.separator
                + m_model.DEFAULT_APPLICATION_FILE);
        if (!file.exists()) {
            System.err.println("Error opening file " + file.getAbsolutePath());
        }

        lines = m_model.ReadTextFileInLines(file);
        for (int i = help_lines; i < lines.size(); i++) {
            line = (String) lines.get(i);
            String[] descomposed_line = line.split(" ");
            data.add(descomposed_line);
        }
        return data;
    }

    /**
     * Attach the generated traffic to the selected ap.
     */
    public void AttachTraffic() {
        int returnValue = -1;
        if (m_model.CheckNetStructure(FromIpTextField.getText())
                && m_model.CheckNetStructure(ToIpTextField.getText())) {
            returnValue = m_model.AttachExternalTraffic(StartingPortTextField.getText(),
                    EndingPortTextField.getText(), FromIpTextField.getText(),
                    ToIpTextField.getText(),
                    ApComboBox1.getSelectedIndex(), (String) DeviceComboBox1.getSelectedItem(),
                    ApComboBox2.getSelectedIndex(), (String) DeviceComboBox2.getSelectedItem(),
                    ProtocolTextField.getText(), NameTextField.getText());
        }
        if (returnValue < 0) {
            ShowErrorMessage("Values inserted are not valid!", "Input error");
        } else {
            ClearInputText();
            FillExternalTrafficComboBox();
        }
    }

    /**
     * Delete all inserted text.
     */
    public void ClearInputText() {
        NameTextField.setText("");
        FromIpTextField.setText("");
        ToIpTextField.setText("");
        ProtocolTextField.setText("");
        StartingPortTextField.setText("1024");
        EndingPortTextField.setText("1025");
        try {
            ApComboBox1.setSelectedIndex(0);
            ApComboBox2.setSelectedIndex(ApComboBox2.getItemCount() - 1);
        } catch (IllegalArgumentException iae) {
        }
    }

    /**
     * Complete the window with the data obtained on the list applications.
     */
    private void SelectStandarApplication(Integer application) {
        int startAp, endAp;

        String appData[] = applications.get(application);
        NameTextField.setText(appData[1]);
        ProtocolTextField.setText(appData[2]);
        StartingPortTextField.setText(appData[3]);
        EndingPortTextField.setText(appData[4]);
        FromIpTextField.setText(appData[5]);
        ToIpTextField.setText(appData[6]);
        startAp = Integer.parseInt(appData[7]);
        endAp = Integer.parseInt(appData[8]);
        if (ApComboBox1.getItemCount() > 0) {
            if (startAp > ApComboBox1.getItemCount()) {
                startAp = 1;
            }
            ApComboBox1.setSelectedIndex(startAp - 1);
            if (endAp > ApComboBox2.getItemCount()) {
                endAp = ApComboBox2.getItemCount();
            }
            ApComboBox2.setSelectedIndex(endAp - 1);
        }
    }

    /**
     * Fill all fields with the data of the selected traffic flow.
     */
    private void SelectExternalTrafficFlow(int index) {
        int startAp, endAp;

        ExternalTraffic extTraffic = m_model.externalTrafficFlow.get(index);
        NameTextField.setText(extTraffic.name);
        ProtocolTextField.setText(extTraffic.protocol);
        StartingPortTextField.setText(extTraffic.startRangePort + "");
        EndingPortTextField.setText(extTraffic.endRangePort + "");
        FromIpTextField.setText(extTraffic.fromIp);
        ToIpTextField.setText(extTraffic.toIp);
        startAp = extTraffic.fromAp;
        endAp = extTraffic.toAp;
        if (ApComboBox1.getItemCount() > 0) {
            if (startAp > ApComboBox1.getItemCount()) {
                startAp = 0;
            }
            ApComboBox1.setSelectedIndex(startAp);
            if (endAp > ApComboBox2.getItemCount()) {
                endAp = ApComboBox2.getItemCount();
            }
            ApComboBox2.setSelectedIndex(endAp);
        }
    }

    /**
     * Create a window to show an error message.
     */
    void ShowErrorMessage(String text, String title) {
        JFrame frame = null;
        if (m_model.debug) {
            System.out.println("Error message :" + text);
        }
        JOptionPane.showMessageDialog(frame, text, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Return the number of the traffic flow selected.
     */
    public int ReturnSelectedExternalTrafficFlow() {
        return ExternalTrafficComboBox.getSelectedIndex();
    }

    /*********************************************************************
     *
     *                             LISTENERS
     *
     *********************************************************************/
    public void addAttachButtonListener(ActionListener al) {
        AttachButton.addActionListener(al);
    }

    public void addHelpButtonListener(ActionListener al) {
        HelpButton.addActionListener(al);
    }

    public void addCloseButtonListener(ActionListener al) {
        CancelButton.addActionListener(al);
    }

    public void addDeleteButtonListener(ActionListener al) {
        DeleteButton.addActionListener(al);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TitlePanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        NameTextField = new javax.swing.JTextField();
        ExternalTrafficComboBox = new javax.swing.JComboBox();
        AttachButton = new javax.swing.JButton();
        DeleteButton = new javax.swing.JButton();
        AppPanel = new javax.swing.JPanel();
        StandarAppComboBox = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ProtocolTextField = new javax.swing.JTextField();
        StartingPortTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        EndingPortTextField = new javax.swing.JTextField();
        ButtonPanel = new javax.swing.JPanel();
        HelpButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();
        FromPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        FromIpTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        ApComboBox1 = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        DeviceComboBox1 = new javax.swing.JComboBox();
        ToPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        ToIpTextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        ApComboBox2 = new javax.swing.JComboBox();
        DeviceComboBox2 = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Attach external application");
        setResizable(false);

        TitlePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel8.setText("External Traffic:");

        ExternalTrafficComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ExternalTrafficComboBoxItemStateChanged(evt);
            }
        });

        AttachButton.setText("Attach");

        DeleteButton.setText("Delete");

        javax.swing.GroupLayout TitlePanelLayout = new javax.swing.GroupLayout(TitlePanel);
        TitlePanel.setLayout(TitlePanelLayout);
        TitlePanelLayout.setHorizontalGroup(
            TitlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TitlePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TitlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(TitlePanelLayout.createSequentialGroup()
                        .addComponent(NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ExternalTrafficComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AttachButton, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel8))
                .addContainerGap())
        );

        TitlePanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AttachButton, DeleteButton});

        TitlePanelLayout.setVerticalGroup(
            TitlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TitlePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(TitlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ExternalTrafficComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DeleteButton)
                    .addComponent(AttachButton))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        AppPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        StandarAppComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ekiga" }));
        StandarAppComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                StandarAppComboBoxItemStateChanged(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel9.setText("App:");

        jLabel2.setText("Protocol:");

        jLabel4.setText("Port Range:");

        jLabel3.setText(":");

        javax.swing.GroupLayout AppPanelLayout = new javax.swing.GroupLayout(AppPanel);
        AppPanel.setLayout(AppPanelLayout);
        AppPanelLayout.setHorizontalGroup(
            AppPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AppPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AppPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(StandarAppComboBox, 0, 139, Short.MAX_VALUE)
                    .addComponent(jLabel4)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AppPanelLayout.createSequentialGroup()
                        .addComponent(StartingPortTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EndingPortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9)
                    .addGroup(AppPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(27, 27, 27)
                        .addComponent(ProtocolTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)))
                .addContainerGap())
        );
        AppPanelLayout.setVerticalGroup(
            AppPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AppPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(StandarAppComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(AppPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ProtocolTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AppPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EndingPortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(StartingPortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        ButtonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        HelpButton.setText("Help");

        CancelButton.setText("Close");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ButtonPanelLayout = new javax.swing.GroupLayout(ButtonPanel);
        ButtonPanel.setLayout(ButtonPanelLayout);
        ButtonPanelLayout.setHorizontalGroup(
            ButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .addComponent(HelpButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
                .addContainerGap())
        );
        ButtonPanelLayout.setVerticalGroup(
            ButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(HelpButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CancelButton)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        FromPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setText("From IP:");

        jLabel1.setText("From AP:");

        ApComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ApComboBox1ActionPerformed(evt);
            }
        });

        jLabel6.setText("Net:");

        DeviceComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeviceComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout FromPanelLayout = new javax.swing.GroupLayout(FromPanel);
        FromPanel.setLayout(FromPanelLayout);
        FromPanelLayout.setHorizontalGroup(
            FromPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FromPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(FromPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(FromIpTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FromPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(ApComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FromPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DeviceComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap())
        );
        FromPanelLayout.setVerticalGroup(
            FromPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FromPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(FromPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FromPanelLayout.createSequentialGroup()
                        .addGroup(FromPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(FromPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(FromIpTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ApComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(FromPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(DeviceComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(FromPanelLayout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addGap(30, 30, 30))))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        ToPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setText("To IP:");

        jLabel10.setText("To AP:");

        ApComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ApComboBox2ActionPerformed(evt);
            }
        });

        DeviceComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeviceComboBox2ActionPerformed(evt);
            }
        });

        jLabel11.setText("Net:");

        javax.swing.GroupLayout ToPanelLayout = new javax.swing.GroupLayout(ToPanel);
        ToPanel.setLayout(ToPanelLayout);
        ToPanelLayout.setHorizontalGroup(
            ToPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ToPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ToPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ToIpTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(15, 15, 15)
                .addGroup(ToPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(ApComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(ToPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(ToPanelLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(126, 126, 126))
                    .addComponent(DeviceComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ToPanelLayout.setVerticalGroup(
            ToPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ToPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ToPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ToPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ApComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ToIpTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DeviceComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(ToPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(FromPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                    .addComponent(TitlePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(AppPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {FromPanel, TitlePanel, ToPanel});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AppPanel, ButtonPanel});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(TitlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(FromPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(AppPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ButtonPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(ToPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {FromPanel, TitlePanel, ToPanel});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void StandarAppComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_StandarAppComboBoxItemStateChanged
        if (StandarAppComboBox.getItemCount() > 0) {
            SelectStandarApplication(StandarAppComboBox.getSelectedIndex());
        }
    }//GEN-LAST:event_StandarAppComboBoxItemStateChanged

    private void ExternalTrafficComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ExternalTrafficComboBoxItemStateChanged
        if (ExternalTrafficComboBox.getItemCount() > 0) {
            SelectExternalTrafficFlow(ExternalTrafficComboBox.getSelectedIndex());
        }
    }//GEN-LAST:event_ExternalTrafficComboBoxItemStateChanged

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_CancelButtonActionPerformed

    private void ApComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ApComboBox2ActionPerformed
        if (ApComboBox2.getItemCount() > 0) {
            m_model.selectionedAP2 = m_model.GetAP(ApComboBox2.getSelectedIndex());
            FillDeviceComboBox2();
        }
    }//GEN-LAST:event_ApComboBox2ActionPerformed

    private void DeviceComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeviceComboBox2ActionPerformed
        if (DeviceComboBox2.getItemCount() > 0) {
            m_model.selectionedIP2 = (String) DeviceComboBox2.getSelectedItem();
        }
    }//GEN-LAST:event_DeviceComboBox2ActionPerformed

    private void DeviceComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeviceComboBox1ActionPerformed
        if (DeviceComboBox1.getItemCount() > 0) {
            m_model.selectionedIP1 = (String) DeviceComboBox1.getSelectedItem();
        }
    }//GEN-LAST:event_DeviceComboBox1ActionPerformed

    private void ApComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ApComboBox1ActionPerformed
        if (ApComboBox1.getItemCount() > 0) {
            m_model.selectionedAP1 = m_model.GetAP(ApComboBox1.getSelectedIndex());
            FillDeviceComboBox1();
        }
    }//GEN-LAST:event_ApComboBox1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox ApComboBox1;
    private javax.swing.JComboBox ApComboBox2;
    private javax.swing.JPanel AppPanel;
    private javax.swing.JButton AttachButton;
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JButton CancelButton;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JComboBox DeviceComboBox1;
    private javax.swing.JComboBox DeviceComboBox2;
    private javax.swing.JTextField EndingPortTextField;
    private javax.swing.JComboBox ExternalTrafficComboBox;
    private javax.swing.JTextField FromIpTextField;
    private javax.swing.JPanel FromPanel;
    private javax.swing.JButton HelpButton;
    private javax.swing.JTextField NameTextField;
    private javax.swing.JTextField ProtocolTextField;
    private javax.swing.JComboBox StandarAppComboBox;
    private javax.swing.JTextField StartingPortTextField;
    private javax.swing.JPanel TitlePanel;
    private javax.swing.JTextField ToIpTextField;
    private javax.swing.JPanel ToPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
