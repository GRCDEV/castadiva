/*
 * RandomSimulation.java
 *
 * Created on 26 de octubre de 2006, 11:52
 */

package castadiva_gui;

import castadiva.*;

import castadiva.TableModels.RandomTrafficTableModel;
import castadiva.TableModels.TrafficTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author  jorge
 */
public class RandomSimulation extends javax.swing.JFrame {
    CastadivaModel m_model;
    Map blackBoard;
    RandomTrafficForm randomTrafficTable;
    
    /** Creates new form RandomSimulation */
    public RandomSimulation(CastadivaModel model) {
        initComponents();
        setLocation((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-(int)(this.getWidth()/2),
                (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-(int)(this.getHeight()/2));
        m_model = model;
        CreateBlackBoard();
    }
    
    
    /*********************************************************************
     *
     *                         INQUERY METHODS
     *
     *********************************************************************/
    
    
    /**
     * Where the test would be saved.
     */
    String GetSaveFolderText(){
        return SaveFolderTextField.getText();
    }
    
    String GetFormat(){
        if(CastadivaRadioButton.isSelected()) return "Castadiva";
        return "NS-2";
    }
    
    List GetProtocolsSelected(){
        List protocolList = new ArrayList();
        if(NoneCheckBox.isSelected()) protocolList.add("none");
        if(OptimumCheckBox.isSelected()) protocolList.add("optimum");
        if(DsrCheckBox.isSelected()) protocolList.add("dsr");
        if(AodvCheckBox.isSelected()) protocolList.add("aodv");
        return protocolList;
    }
    
    public Integer GetRuns(){
        return (Integer)RunsSpinner.getValue();
    }
    
    public String SaveFormatSelected(){
        if(CastadivaRadioButton.isSelected()) return "Castadiva";
        return "NS-2";
    }
    
    /**
     * Obtain the X size of the simulation.
     */
    public Float ReturnXSize(){
        return Float.parseFloat(SizeXTextField.getText());
    }
    
    /**
     * Obtain the Y bound of the simulation.
     */
    public Float ReturnYSize(){
        return Float.parseFloat(SizeYTextField.getText());
    }
    
    /**
     * Obtain the desired time of the simulation.
     */
    String ReturnSimulationTimeTextField(){
        return SimulationTimeTextField.getText();
    }
    
    /**
     * Return true if the flow is shared randomly or is balanced.
     */
    public boolean IsTrafficRandom(){
        return RandomFlowRadioButton.isSelected();
    }
    
    /**
     * Return the pause time written by the user.
     */
    public Float ReturnPauseTime(){
       return Float.parseFloat(PauseTimeTextField.getText());
    }
    
    /**
     * Return the node max speed written by the user.
     */
    public Float ReturnSpeed(){
       return Float.parseFloat(SpeedTextField.getText()); 
    }
    
    public void ChangeCurrentTime(Integer value){
        CurrentTimeTextField.setText(value.toString());
    }
    
    public void EndSimulation(){
        ShowMessageSimulationEnded();
        GenerateButton.setEnabled(true);
    }
    
    public void StartSimulation(){
        GenerateButton.setEnabled(false);
    }
    
    
    /*********************************************************************
     *
     *                         GUI CONTROL
     *
     *********************************************************************/
    
    
    /**
     * Paint the scenario.
     */    
    private void CreateBlackBoard(){
        //Adding the drawing area.
        blackBoard = new Map(m_model, m_model.WhatBoundX(), m_model.WhatBoundY());
        blackBoard.setPreferredSize(new Dimension(Frame.getWidth(),Frame.getHeight()));
        Frame.add(blackBoard,0);
    }
    
    void ModifyBlackBoard(){
        //Change the map.
        blackBoard.MapChange(false, false, false, m_model.WhatBoundX(), m_model.WhatBoundY(), false);
        Frame.repaint();
    }
      
    void ActivateScenario(boolean state){
        JTextField textField = new JTextField();
        Color c;
        MaxNodeNumberSpinner.setEnabled(state);
        GranularitySpinner.setEnabled(state);
        if(!state){
            c = new Color(220,220,220);
        }else{
            c = new Color(0,0,0);
        }
        toLabel.setForeground(c);
        GranularityLabel.setForeground(c);
    }
    
    void ActivateTraffic(boolean state){
        UpdateTable();
    }
     
    void ChangeSaveFolderText(String text){
        SaveFolderTextField.setText(text);
    }
    
    /**
     * Must be at least 1 loop
     */
    private void RunsInRange(){
        if((Integer)RunsSpinner.getValue() < 1) {
            RunsSpinner.setValue(1);
        }
    }
    
    /**
     * Allow to push the generation button only when the simulation can start.
     */
    private void ActivateGenerationButton(){
        if(!m_model.randomNotEnded){
        if((Integer)MaxNodeNumberSpinner.getValue() > 1 &&
                m_model.randomTrafficModel.getRowCount() > 1){
                GenerateButton.setEnabled(true);
        }else{
            GenerateButton.setEnabled(false);
        }
        }
    }
    
    /**
     * Avoid the granularity to hava impossible values.
     */
    private void GranularityInRange(){
        if((Integer)MaxNodeNumberSpinner.getValue() > 0 &&
                (Integer)GranularitySpinner.getValue() == 0)
            GranularitySpinner.setValue(1);
        if((Integer)GranularitySpinner.getValue() >
                ((Integer)MaxNodeNumberSpinner.getValue() -
                (Integer)MinNodeNumberSpinner.getValue())){
            GranularitySpinner.setValue(((Integer)MaxNodeNumberSpinner.getValue() -
                    (Integer)MinNodeNumberSpinner.getValue())+1);
        }
        if((Integer)GranularitySpinner.getValue() < 0){
            GranularitySpinner.setValue(0);
        }
        m_model.granularity = (Integer) GranularitySpinner.getValue();
    }
    
    /**
     * Set the MinNode spinner in to correct values.
     */
    private void MinNodeNumberInRange(){
        if((Integer)MinNodeNumberSpinner.getValue() < 0){
            MinNodeNumberSpinner.setValue(0);
        }
        if((Integer)MinNodeNumberSpinner.getValue() < 2 && (Integer)MaxNodeNumberSpinner.getValue() > 1){
            MinNodeNumberSpinner.setValue(2);
        }
        if((Integer)MinNodeNumberSpinner.getValue() > m_model.HowManyAP()){
            MinNodeNumberSpinner.setValue(m_model.HowManyAP());
        }
        if((Integer)MinNodeNumberSpinner.getValue() > (Integer)MaxNodeNumberSpinner.getValue()){
            MaxNodeNumberSpinner.setValue((Integer)MinNodeNumberSpinner.getValue());
        }
        m_model.minNodes = (Integer) MinNodeNumberSpinner.getValue();
    }
    
    /**
     * Set the MaxNode spinner in to correct values.
     */
    private void MaxNodeNumberInRange(){
        if((Integer)MaxNodeNumberSpinner.getValue() < 0){
            MaxNodeNumberSpinner.setValue(0);
        }
        if((Integer)MaxNodeNumberSpinner.getValue() > m_model.HowManyAP()){
            MaxNodeNumberSpinner.setValue(m_model.HowManyAP());
        }
        if((Integer)MinNodeNumberSpinner.getValue() < 2 && (Integer)MaxNodeNumberSpinner.getValue() > 1){
            MinNodeNumberSpinner.setValue(2);
        }
        if((Integer)MaxNodeNumberSpinner.getValue() < (Integer)MinNodeNumberSpinner.getValue()){
            MaxNodeNumberSpinner.setValue((Integer)MinNodeNumberSpinner.getValue());
        }
        if((Integer)MaxNodeNumberSpinner.getValue() < (Integer)MinNodeNumberSpinner.getValue()){
            MinNodeNumberSpinner.setValue((Integer)MaxNodeNumberSpinner.getValue());
        }
        m_model.maxNodes = (Integer) MaxNodeNumberSpinner.getValue();
        ActivateGenerationButton();
    }
    
    /**
     * Show a message to tell the user the simulation is finished.
     */
    void ShowMessageSimulationEnded(){
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame, "The simulation has finished.", "End of simulation.",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Update the frame with all node represented.
     */
     void Repaint(){
        Frame.repaint();
    }
    
      
     
    /*********************************************************************
     *
     *                     TABLE MODIFICATION
     *
     *********************************************************************/
    
     
    /**
     * Obtain the selected row of the table.
     */
    public Integer GetSelectedRow(){
        return randomTrafficTable.table.getSelectedRow();
    }
    
    public void UpdateTable(){
        ActivateGenerationButton();

            TableTitleLabel.setText("Sources:");
            GenerateTrafficTable(m_model.accessPoints.GetRandomTraffic());
    }
    
    void GenerateTable(){
        
            TableTitleLabel.setText("Sources:");
            GenerateTrafficTable(m_model.accessPoints.GetRandomTraffic());
    }
    
    /**
     * Create a table where the user can select all random simulation specifications,
     * defined by the traffic.
     * @param data The records to fill the table.
     */
    void GenerateTrafficTable(Vector data){
        randomTrafficTable = new RandomTrafficForm(data);
        RandomTrafficScrollPanel.setViewportView(randomTrafficTable);
        
        JComboBox TCPUDPComboBox = new JComboBox();
        TCPUDPComboBox.addItem("UDP");
        TCPUDPComboBox.addItem("TCP");
        randomTrafficTable.table.getColumnModel().getColumn(RandomTrafficTableModel.TCPUDP_INDEX).
                setCellEditor(new DefaultCellEditor(TCPUDPComboBox));
        
        randomTrafficTable.setVisible(false);
        randomTrafficTable.setVisible(true);
    }
    
    
    
    /*********************************************************************
     *
     *                             LISTENERS
     *
     *********************************************************************/
    
    
    /**
     * Listener activated when the user want to close the window.
     */
    void addCloseButtonListener(ActionListener al){
        CloseButton.addActionListener(al);
    }
    
    void addSaveFolderActionListener(ActionListener al){
        SearchDirectoryButton.addActionListener(al);
    }
    
    void addDelRowButtonListener(ActionListener al){
        DeleteRowButton.addActionListener(al);
    }
    
    void addDuplicateActionListener(ActionListener al){
        DuplicateButton.addActionListener(al);
    }
    
    void addClearButtonListener(ActionListener al){
        DeleteAllButton.addActionListener(al);
    }
    
    void addGenerateActionListener(ActionListener al){
        GenerateButton.addActionListener(al);
    }
    
    void addOrderActionListener(ActionListener al){
        OrderButton.addActionListener(al);
    }
    
    void addXBoundListener(ActionListener al){
        SizeXTextField.addActionListener(al);
    }
    
    void addYBoundListener(ActionListener al){
        SizeYTextField.addActionListener(al);
    }
    
    void addSimulationTimeTextFieldListener(ActionListener al){
        SimulationTimeTextField.addActionListener(al);
    }
    
    void addPauseSimulationTimeTextFieldListener(ActionListener al){
        PauseTimeTextField.addActionListener(al);
    }
    
    void addSpeedSimulationTextFieldListener(ActionListener al){
        SpeedTextField.addActionListener(al);
    }
    
    
    
    /*********************************************************************
     *
     *                           AUTOGENERATED
     *
     *********************************************************************/
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SaveFormatButtonGroup = new javax.swing.ButtonGroup();
        FlowButtonGroup = new javax.swing.ButtonGroup();
        StoreDataPanel = new javax.swing.JPanel();
        SaveFolderTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        SearchDirectoryButton = new javax.swing.JButton();
        SourcePanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        MinNodeNumberSpinner = new javax.swing.JSpinner();
        MaxNodeNumberSpinner = new javax.swing.JSpinner();
        toLabel = new javax.swing.JLabel();
        GranularityLabel = new javax.swing.JLabel();
        GranularitySpinner = new javax.swing.JSpinner();
        RunsSpinner = new javax.swing.JSpinner();
        jLabel15 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        PauseTimeTextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        SpeedTextField = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        TrafficPanel = new javax.swing.JPanel();
        RandomTrafficScrollPanel = new javax.swing.JScrollPane();
        DeleteRowButton = new javax.swing.JButton();
        DeleteAllButton = new javax.swing.JButton();
        DuplicateButton = new javax.swing.JButton();
        OrderButton = new javax.swing.JButton();
        TableTitleLabel = new javax.swing.JLabel();
        ScenarioPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        SizeXTextField = new javax.swing.JTextField();
        SizeYTextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        SimulationTimeTextField = new javax.swing.JTextField();
        CurrentTimeTextField = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        ButtonPanel = new javax.swing.JPanel();
        GenerateButton = new javax.swing.JButton();
        CloseButton = new javax.swing.JButton();
        OutputPanel = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        CastadivaRadioButton = new javax.swing.JRadioButton();
        NSRadioButton = new javax.swing.JRadioButton();
        ChoosePanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        RandomFlowRadioButton = new javax.swing.JRadioButton();
        BalancedFlowRadioButton = new javax.swing.JRadioButton();
        ProtocolPanel = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        OptimumCheckBox = new javax.swing.JCheckBox();
        NoneCheckBox = new javax.swing.JCheckBox();
        DsrCheckBox = new javax.swing.JCheckBox();
        AodvCheckBox = new javax.swing.JCheckBox();
        Frame = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CASTADIVA - Random simulation");
        setResizable(false);

        StoreDataPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        SaveFolderTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        SaveFolderTextField.setText("/tmp");

        jLabel1.setText("Save in Folder:");

        SearchDirectoryButton.setText("Select");

        org.jdesktop.layout.GroupLayout StoreDataPanelLayout = new org.jdesktop.layout.GroupLayout(StoreDataPanel);
        StoreDataPanel.setLayout(StoreDataPanelLayout);
        StoreDataPanelLayout.setHorizontalGroup(
            StoreDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(StoreDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(StoreDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(StoreDataPanelLayout.createSequentialGroup()
                        .add(SaveFolderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 137, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(SearchDirectoryButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        StoreDataPanelLayout.setVerticalGroup(
            StoreDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(StoreDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(StoreDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(SaveFolderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(SearchDirectoryButton))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        SourcePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel3.setText("Nodes:");

        MinNodeNumberSpinner.setRequestFocusEnabled(false);
        MinNodeNumberSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                MinNodeNumberSpinnerStateChanged(evt);
            }
        });

        MaxNodeNumberSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                MaxNodeNumberSpinnerStateChanged(evt);
            }
        });

        toLabel.setText("to");

        GranularityLabel.setText("Granularity:");

        GranularitySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                GranularitySpinnerStateChanged(evt);
            }
        });

        RunsSpinner.setValue(1);
        RunsSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                RunsSpinnerStateChanged(evt);
            }
        });

        jLabel15.setText("Runs:");

        jLabel13.setText("Speed:");

        jLabel8.setText("Pause:");

        PauseTimeTextField.setText("0");

        jLabel10.setText("s");

        SpeedTextField.setText("0");

        jLabel28.setText("m/s");

        org.jdesktop.layout.GroupLayout SourcePanelLayout = new org.jdesktop.layout.GroupLayout(SourcePanel);
        SourcePanel.setLayout(SourcePanelLayout);
        SourcePanelLayout.setHorizontalGroup(
            SourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SourcePanelLayout.createSequentialGroup()
                .add(SourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(SourcePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(SourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(SourcePanelLayout.createSequentialGroup()
                                .add(MinNodeNumberSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 52, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(toLabel))
                            .add(SourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(jLabel28)
                                .add(GranularityLabel))
                            .add(GranularitySpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 51, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(SourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, SpeedTextField)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(SourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel15)
                            .add(SourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(RunsSpinner)
                                .add(MaxNodeNumberSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(SourcePanelLayout.createSequentialGroup()
                                .add(SourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(PauseTimeTextField)
                                    .add(jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .add(4, 4, 4)
                                .add(jLabel10))))
                    .add(SourcePanelLayout.createSequentialGroup()
                        .add(51, 51, 51)
                        .add(jLabel3)))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        SourcePanelLayout.setVerticalGroup(
            SourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SourcePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(SourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(SourcePanelLayout.createSequentialGroup()
                        .add(SourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(MinNodeNumberSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(toLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(GranularityLabel)
                        .add(1, 1, 1)
                        .add(GranularitySpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(SourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel13)
                            .add(SourcePanelLayout.createSequentialGroup()
                                .add(21, 21, 21)
                                .add(SourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel28)
                                    .add(SpeedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                    .add(SourcePanelLayout.createSequentialGroup()
                        .add(MaxNodeNumberSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel15)
                        .add(1, 1, 1)
                        .add(RunsSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(SourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel10)
                            .add(PauseTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        TrafficPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        DeleteRowButton.setText("Delete row");

        DeleteAllButton.setText("Delete all");

        DuplicateButton.setText("Duplicate row");

        OrderButton.setText("Order traffic");

        TableTitleLabel.setFont(new java.awt.Font("Dialog", 1, 16));
        TableTitleLabel.setText("Traffic:");

        org.jdesktop.layout.GroupLayout TrafficPanelLayout = new org.jdesktop.layout.GroupLayout(TrafficPanel);
        TrafficPanel.setLayout(TrafficPanelLayout);
        TrafficPanelLayout.setHorizontalGroup(
            TrafficPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(TrafficPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(TrafficPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, RandomTrafficScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 886, Short.MAX_VALUE)
                    .add(TableTitleLabel)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, TrafficPanelLayout.createSequentialGroup()
                        .add(OrderButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(DuplicateButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(DeleteRowButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(DeleteAllButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        TrafficPanelLayout.setVerticalGroup(
            TrafficPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, TrafficPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(TableTitleLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(RandomTrafficScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(TrafficPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(DeleteAllButton)
                    .add(DeleteRowButton)
                    .add(DuplicateButton)
                    .add(OrderButton))
                .addContainerGap())
        );

        ScenarioPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel11.setText("Simulation:");

        SizeXTextField.setText("1000");

        SizeYTextField.setText("1000");

        jLabel9.setText("s");

        jLabel6.setText("Y");

        jLabel4.setText("Size:");

        jLabel5.setText("X");

        jLabel7.setText("Time:");

        SimulationTimeTextField.setText("10");

        CurrentTimeTextField.setText("0");
        CurrentTimeTextField.setEnabled(false);

        jLabel16.setText("s");

        org.jdesktop.layout.GroupLayout ScenarioPanelLayout = new org.jdesktop.layout.GroupLayout(ScenarioPanel);
        ScenarioPanel.setLayout(ScenarioPanelLayout);
        ScenarioPanelLayout.setHorizontalGroup(
            ScenarioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ScenarioPanelLayout.createSequentialGroup()
                .add(ScenarioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ScenarioPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(ScenarioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel4)
                            .add(ScenarioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, ScenarioPanelLayout.createSequentialGroup()
                                    .add(jLabel7)
                                    .add(36, 36, 36))
                                .add(ScenarioPanelLayout.createSequentialGroup()
                                    .add(ScenarioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(ScenarioPanelLayout.createSequentialGroup()
                                            .add(SimulationTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                            .add(3, 3, 3)
                                            .add(jLabel9))
                                        .add(ScenarioPanelLayout.createSequentialGroup()
                                            .add(SizeXTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                            .add(3, 3, 3)
                                            .add(jLabel5)))
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(ScenarioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(CurrentTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(SizeYTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(ScenarioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jLabel6)
                                        .add(jLabel16))
                                    .add(16, 16, 16)))))
                    .add(ScenarioPanelLayout.createSequentialGroup()
                        .add(35, 35, 35)
                        .add(jLabel11)))
                .add(11, 11, 11))
        );
        ScenarioPanelLayout.setVerticalGroup(
            ScenarioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ScenarioPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel11)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ScenarioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(SizeXTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5)
                    .add(SizeYTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ScenarioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(SimulationTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9)
                    .add(CurrentTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel16))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ButtonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        GenerateButton.setText("Generate");
        GenerateButton.setEnabled(false);

        CloseButton.setText("Close");

        org.jdesktop.layout.GroupLayout ButtonPanelLayout = new org.jdesktop.layout.GroupLayout(ButtonPanel);
        ButtonPanel.setLayout(ButtonPanelLayout);
        ButtonPanelLayout.setHorizontalGroup(
            ButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(ButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, GenerateButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .add(CloseButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
                .addContainerGap())
        );
        ButtonPanelLayout.setVerticalGroup(
            ButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(GenerateButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 7, Short.MAX_VALUE)
                .add(CloseButton)
                .addContainerGap())
        );

        OutputPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel14.setText("Export format:");

        SaveFormatButtonGroup.add(CastadivaRadioButton);
        CastadivaRadioButton.setText("Castadiva");
        CastadivaRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        SaveFormatButtonGroup.add(NSRadioButton);
        NSRadioButton.setSelected(true);
        NSRadioButton.setText("NS-2");
        NSRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout OutputPanelLayout = new org.jdesktop.layout.GroupLayout(OutputPanel);
        OutputPanel.setLayout(OutputPanelLayout);
        OutputPanelLayout.setHorizontalGroup(
            OutputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(OutputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(OutputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel14)
                    .add(CastadivaRadioButton)
                    .add(NSRadioButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        OutputPanelLayout.setVerticalGroup(
            OutputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(OutputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel14)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(CastadivaRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(NSRadioButton)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        ChoosePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Traffic Flow:");

        FlowButtonGroup.add(RandomFlowRadioButton);
        RandomFlowRadioButton.setText("Random");
        RandomFlowRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        FlowButtonGroup.add(BalancedFlowRadioButton);
        BalancedFlowRadioButton.setSelected(true);
        BalancedFlowRadioButton.setText("Balanced");
        BalancedFlowRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout ChoosePanelLayout = new org.jdesktop.layout.GroupLayout(ChoosePanel);
        ChoosePanel.setLayout(ChoosePanelLayout);
        ChoosePanelLayout.setHorizontalGroup(
            ChoosePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ChoosePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(ChoosePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(RandomFlowRadioButton)
                    .add(BalancedFlowRadioButton))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        ChoosePanelLayout.setVerticalGroup(
            ChoosePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ChoosePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(BalancedFlowRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(RandomFlowRadioButton)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        ProtocolPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel12.setText("Protocol Used:");

        OptimumCheckBox.setText("Optimum");
        OptimumCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        NoneCheckBox.setSelected(true);
        NoneCheckBox.setText("None");
        NoneCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        DsrCheckBox.setText("DSR");
        DsrCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        DsrCheckBox.setEnabled(false);

        AodvCheckBox.setText("AODV");
        AodvCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout ProtocolPanelLayout = new org.jdesktop.layout.GroupLayout(ProtocolPanel);
        ProtocolPanel.setLayout(ProtocolPanelLayout);
        ProtocolPanelLayout.setHorizontalGroup(
            ProtocolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ProtocolPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(ProtocolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel12)
                    .add(ProtocolPanelLayout.createSequentialGroup()
                        .add(ProtocolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(OptimumCheckBox)
                            .add(NoneCheckBox))
                        .add(21, 21, 21)
                        .add(ProtocolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(AodvCheckBox)
                            .add(DsrCheckBox))))
                .addContainerGap(62, Short.MAX_VALUE))
        );
        ProtocolPanelLayout.setVerticalGroup(
            ProtocolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ProtocolPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel12)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ProtocolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(NoneCheckBox)
                    .add(AodvCheckBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 26, Short.MAX_VALUE)
                .add(ProtocolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(OptimumCheckBox)
                    .add(DsrCheckBox))
                .addContainerGap())
        );

        Frame.setBackground(new java.awt.Color(255, 255, 255));
        Frame.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Frame.setLayout(new java.awt.BorderLayout());

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(Frame, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(SourcePanel, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(ScenarioPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(TrafficPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(ChoosePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ProtocolPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(StoreDataPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(OutputPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ButtonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(TrafficPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(ButtonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(OutputPanel, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(StoreDataPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(ProtocolPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, ChoosePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(Frame, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 109, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(SourcePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ScenarioPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * Alerts Castadiva when the loop spinner change.
     */
    private void RunsSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_RunsSpinnerStateChanged
        RunsInRange();
    }//GEN-LAST:event_RunsSpinnerStateChanged
       
    /**
     * Make the spinner that shows granularity to be in range.
     */
    private void GranularitySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_GranularitySpinnerStateChanged
        GranularityInRange();
    }//GEN-LAST:event_GranularitySpinnerStateChanged
    
    /**
     * Prevent the min value to be greater than 0 and greater than the
     * max value.
     */
    private void MinNodeNumberSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_MinNodeNumberSpinnerStateChanged
        MinNodeNumberInRange();
        GranularityInRange();
    }//GEN-LAST:event_MinNodeNumberSpinnerStateChanged
    
    /**
     * Prevent the max value to be greater than the total node number and smaller than the
     * min value.
     */
    private void MaxNodeNumberSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_MaxNodeNumberSpinnerStateChanged
        MaxNodeNumberInRange();
        GranularityInRange();
    }//GEN-LAST:event_MaxNodeNumberSpinnerStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox AodvCheckBox;
    private javax.swing.JRadioButton BalancedFlowRadioButton;
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JRadioButton CastadivaRadioButton;
    private javax.swing.JPanel ChoosePanel;
    private javax.swing.JButton CloseButton;
    private javax.swing.JTextField CurrentTimeTextField;
    private javax.swing.JButton DeleteAllButton;
    private javax.swing.JButton DeleteRowButton;
    private javax.swing.JCheckBox DsrCheckBox;
    private javax.swing.JButton DuplicateButton;
    private javax.swing.ButtonGroup FlowButtonGroup;
    private javax.swing.JPanel Frame;
    private javax.swing.JButton GenerateButton;
    private javax.swing.JLabel GranularityLabel;
    private javax.swing.JSpinner GranularitySpinner;
    private javax.swing.JSpinner MaxNodeNumberSpinner;
    private javax.swing.JSpinner MinNodeNumberSpinner;
    private javax.swing.JRadioButton NSRadioButton;
    private javax.swing.JCheckBox NoneCheckBox;
    private javax.swing.JCheckBox OptimumCheckBox;
    private javax.swing.JButton OrderButton;
    private javax.swing.JPanel OutputPanel;
    private javax.swing.JTextField PauseTimeTextField;
    private javax.swing.JPanel ProtocolPanel;
    private javax.swing.JRadioButton RandomFlowRadioButton;
    private javax.swing.JScrollPane RandomTrafficScrollPanel;
    private javax.swing.JSpinner RunsSpinner;
    private javax.swing.JTextField SaveFolderTextField;
    private javax.swing.ButtonGroup SaveFormatButtonGroup;
    private javax.swing.JPanel ScenarioPanel;
    private javax.swing.JButton SearchDirectoryButton;
    private javax.swing.JTextField SimulationTimeTextField;
    private javax.swing.JTextField SizeXTextField;
    private javax.swing.JTextField SizeYTextField;
    private javax.swing.JPanel SourcePanel;
    private javax.swing.JTextField SpeedTextField;
    private javax.swing.JPanel StoreDataPanel;
    private javax.swing.JLabel TableTitleLabel;
    private javax.swing.JPanel TrafficPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel toLabel;
    // End of variables declaration//GEN-END:variables
    
    
    /*********************************************************************
     *
     *                           SECONDARY CLASSES
     *
     *********************************************************************/
    
    /**
     *
     */
    public class RandomTrafficForm extends JPanel {
        public JTable table;
        protected JScrollPane scroller;
        public int column;
        public int row;
        
        public RandomTrafficForm(Vector vectorDatos) {
            initComponent();
        }
        
        public void initComponent() {
            m_model.randomTrafficModel.addTableModelListener(new RandomTrafficForm.InteractiveTableModelListener());
            table = new JTable();
            //table.setColumnSelectionAllowed(true);
            table.setRowSelectionAllowed(true);
            table.setModel(m_model.randomTrafficModel);
            table.setSurrendersFocusOnKeystroke(true);
            if (!m_model.randomTrafficModel.hasEmptyRow()) {
                m_model.randomTrafficModel.addEmptyRow();
            }
            //Table column size.
            TableColumn number = table.getColumnModel().getColumn(RandomTrafficTableModel.NUMBER_INDEX);
            number.setMinWidth(20);
            number.setPreferredWidth(30);
            number.setMaxWidth(30);
            
            TableColumn transferSize = table.getColumnModel().getColumn(RandomTrafficTableModel.TRANSFERSIZE_INDEX);
            transferSize.setMinWidth(70);
            transferSize.setPreferredWidth(110);
            transferSize.setMaxWidth(140);
            transferSize.setCellRenderer(new NotEditableTableCellRenderer());
            
            TableColumn start = table.getColumnModel().getColumn(RandomTrafficTableModel.START_INDEX);
            start.setMinWidth(20);
            start.setPreferredWidth(50);
            start.setCellRenderer(new NotEditableTableCellRenderer());
            
            TableColumn stop = table.getColumnModel().getColumn(RandomTrafficTableModel.STOP_INDEX);
            stop.setMinWidth(20);
            stop.setPreferredWidth(50);
            stop.setCellRenderer(new NotEditableTableCellRenderer());
            
            TableColumn size = table.getColumnModel().getColumn(RandomTrafficTableModel.SIZE_INDEX);
            size.setCellRenderer(new NotEditableTableCellRenderer());
            
            TableColumn duration = table.getColumnModel().getColumn(RandomTrafficTableModel.SEC_INDEX);
            duration.setCellRenderer(new NotEditableTableCellRenderer());
            
            /*TableColumn total = table.getColumnModel().getColumn(m_model.randomTrafficModel.MAX_INDEX);
            total.setCellRenderer(new NotEditableTableCellRenderer());*/
            
            TableColumn flows = table.getColumnModel().getColumn(RandomTrafficTableModel.FLOWS_INDEX);
            flows.setCellRenderer(new NotEditableTableCellRenderer());
            
            scroller = new javax.swing.JScrollPane(table);
            table.setPreferredScrollableViewportSize(new java.awt.Dimension(50, 30));
            TableColumn hidden = table.getColumnModel().getColumn(RandomTrafficTableModel.HIDDEN_INDEX);
            hidden.setMinWidth(1);
            hidden.setPreferredWidth(1);
            hidden.setMaxWidth(1);
            hidden.setCellRenderer(new InteractiveRenderer(RandomTrafficTableModel.HIDDEN_INDEX));
            
            setLayout(new BorderLayout());
            add(scroller, BorderLayout.CENTER);
        }
        
        public void highlightLastRow(int row) {
            int lastrow = m_model.randomTrafficModel.getRowCount();
            if (row == lastrow - 1) {
                table.setRowSelectionInterval(lastrow - 1, lastrow - 1);
            } else {
                table.setRowSelectionInterval(row + 1, row + 1);
            }
            table.setColumnSelectionInterval(0, 0);
        }
        
        class InteractiveRenderer extends DefaultTableCellRenderer {
            protected int interactiveColumn;
            
            public InteractiveRenderer(int interactiveColumn) {
                this.interactiveColumn = interactiveColumn;
            }
            
            @Override
            public Component getTableCellRendererComponent
                    (JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row,int column) {
                Component c = super.getTableCellRendererComponent
                        (table, value, isSelected, hasFocus, row, column);
                if (column == interactiveColumn && hasFocus) {
                    if ((m_model.randomTrafficModel.getRowCount() - 1) == row &&
                            !m_model.randomTrafficModel.hasEmptyRow()) {
                        m_model.randomTrafficModel.addEmptyRow();
                    }
                    highlightLastRow(row);
                }
                return c;
            }
        }
        
        public class NotEditableTableCellRenderer extends DefaultTableCellRenderer {
            @Override
            public Component getTableCellRendererComponent
                    (JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent
                        (table, value, isSelected, hasFocus, row, column);
                if(!table.isCellEditable(row,column)) {
                    cell.setForeground(Color.lightGray);
                }else{
                    cell.setForeground(Color.black);
                }
                if((column == TrafficTableModel.START_INDEX ||
                        column == TrafficTableModel.STOP_INDEX) &&
                        (Integer)table.getValueAt(row,TrafficTableModel.START_INDEX) >=
                        (Integer)table.getValueAt(row,TrafficTableModel.STOP_INDEX)){
                    cell.setForeground(Color.RED);
                }
                
                //Max Packets are equal a time per packets/second.
                /*if(((((Integer)table.getValueAt(row,m_model.tableModel.STOP_INDEX)) -
                        ((Integer)table.getValueAt(row,m_model.tableModel.START_INDEX))) *
                        ((Integer)table.getValueAt(row,m_model.tableModel.SEC_INDEX)) !=
                        ((Integer)table.getValueAt(row,m_model.tableModel.MAX_INDEX)))){
                    Integer maxPackets = ((Integer)table.getValueAt(row,m_model.tableModel.STOP_INDEX) -
                            (Integer)table.getValueAt(row,m_model.tableModel.START_INDEX)) *
                            (Integer)table.getValueAt(row,m_model.tableModel.SEC_INDEX);
                    table.setValueAt((int)maxPackets,row,m_model.tableModel.MAX_INDEX);
                }*/
                
                
                table.setColumnSelectionInterval(0,table.getColumnCount()-1);
                return cell;
            }
        }
        
        public class InteractiveTableModelListener implements TableModelListener {
            public void tableChanged(TableModelEvent evt) {
                column = evt.getColumn();
                row = evt.getFirstRow();
                if (evt.getType() == TableModelEvent.UPDATE) {
                    table.setColumnSelectionInterval(column + 1, column + 1);
                    table.setRowSelectionInterval(row, row);
                }
                //Adding a new line where the last one is full.
                if (!m_model.randomTrafficModel.hasEmptyRow()) {
                    m_model.randomTrafficModel.addEmptyRow();
                }
                ActivateGenerationButton();
            }
        }
        
        public void delRow(){
            m_model.randomTrafficModel.delRow(row);
            if (!m_model.randomTrafficModel.hasEmptyRow()) {
                m_model.randomTrafficModel.addEmptyRow();
            }
        }
    }
    
}
