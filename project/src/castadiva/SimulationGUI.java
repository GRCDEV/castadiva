/*
 * SimulationWindow.java
 *
 * Created on 17 de mayo de 2006, 14:17
 */

package castadiva;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

/**
 *
 * @author  jorge
 */
public class SimulationGUI extends javax.swing.JFrame {
    CastadivaModel m_model;
    Map blackBoard;
    private Graphics g;
    private boolean tags = true;
    private boolean grid = true;
    private boolean speedLine = false;
    
    
    public SimulationGUI(CastadivaModel model){
        m_model = model;
        initComponents();
        EnableReplay(false);
        setLocation((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-(int)(this.getWidth()/2), (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-(int)(this.getHeight()/2));
        FillFields();
        ActivateButtons(true);
        CreateBlackBoard();
    }
    
    
    /*********************************************************************
     *
     *                         INQUERY METHODS
     *
     *********************************************************************/
    
    
    void GetNewSize(){
        DrawNewSize(Integer.parseInt(SizeTextField.getText()));
    }
    
    void GetNewSizeAreaSlider(){
        DrawNewSize(AreaSlider.getValue());
    }
    
    Float ReturnXTextField(){
        try{
            return Float.parseFloat(XTextField.getText());
        }catch(NumberFormatException nfe){
            return (float)200;
        }
    }
    
    Float ReturnYTextField(){
         try{
            return Float.parseFloat(YTextField.getText());
        }catch(NumberFormatException nfe){
            return (float)200;
        }
    }
    
    Float ReturnZTextField(){
        try{
            return Float.parseFloat(ZTextField.getText());
        }catch(NumberFormatException nfe){
            return (float)200;
        }
    }
    
    Float ReturnXBoundField(){
        try{
            return Float.parseFloat(SimXTextField.getText());
        }catch(NumberFormatException nfe){
            return (float)200;
        }
    }        
    
    Float ReturnYBoundField(){
         try{
            return Float.parseFloat(SimYTextField.getText());
        }catch(NumberFormatException nfe){
            return (float)200;
        }
    }
    
    Float ReturnATextField(){
        try{
            return Float.parseFloat(ATextField.getText());
        }catch(NumberFormatException nfe){
            return (float)250;
        }
    }
    
    Integer ReturnSimulationTime(){
         int st;
            try{
                st =Integer.parseInt(SimTimeTextField.getText());
            }catch(NumberFormatException nfe){
                st = 0;
        }
        
        return st;
    }
    
    void SetSimulationTime(Object value){
        SimTimeTextField.setText(value.toString());
    }
    
    Integer ReturnMobilityPauseTextField(){
            try{
               return Integer.parseInt(SimPauseTextField.getText());
            }catch(NumberFormatException nfe){
                return 0;
            }
    }
    
    Float ReturnMobilityMaxSpeed(){
        return Float.parseFloat(MaxSpeedTextField.getText());
    }
    
    Float ReturnMobilityMinSpeed(){
        return Float.parseFloat(MinSpeedTextField.getText());
    }
    
    Integer WhatSelectedAP(){
        return APComboBox.getSelectedIndex();
    }
    
    float ReturnBorderX(){
        return m_model.WhatBoundX();
    }
    
    float ReturnBorderY(){
        return m_model.WhatBoundY();
    }
    
    Integer ReturnReplayTime(){
        Integer value;
        try{
            value = Integer.parseInt(ReplayTimeTextField.getText());
        }catch(NumberFormatException nfe){
            value = 0;
        }
        return value;
    }
    
    void ChangeReplayTime(Integer value){
        ReplayTimeTextField.setText(value.toString());
    }
    
    
    /*********************************************************************
     *
     *                         GUI CONTROL
     *
     *********************************************************************/
    
    /**
     * Update the frame with all node represented.
     */
    void Repaint(){
        Frame.repaint();
    }
    
    private void CreateBlackBoard(){
        //Adding the drawing area.
        blackBoard = new Map(m_model, RangeRadioButton.isSelected(), grid, 
                m_model.WhatBoundX(), m_model.WhatBoundY());
        blackBoard.setPreferredSize(new Dimension(Frame.getWidth(),Frame.getHeight()));
        Frame.add(blackBoard,0);
    }
    
    void ModifyBlackBoard(){
        //Change the map.
        blackBoard.MapChange(RangeRadioButton.isSelected(), tags, grid, m_model.WhatBoundX(), 
                m_model.WhatBoundY(), speedLine);
        Frame.repaint();
    }
    
    void ActivateButtons(boolean activation){
        if (m_model.HowManyAP() == 0) activation = false;
        XTextField.setEnabled(activation);
        YTextField.setEnabled(activation);
        ZTextField.setEnabled(activation);
        ATextField.setEnabled(activation);
        APComboBox.setEnabled(activation);
        Frame.setEnabled(activation);
        StopSimulationButton.setEnabled(activation);
        ResetButton.setEnabled(activation);
        TrafficButton.setEnabled(activation);
        SimulateButton.setEnabled(activation);
        RandomSceneryButton.setEnabled(activation);
    }
    
    void FillFields(){
        SimXTextField.setText(m_model.WhatBoundX().toString());
        SimYTextField.setText(m_model.WhatBoundY().toString());
        SimPauseTextField.setText(m_model.WhatTimePause().toString());
        MaxSpeedTextField.setText(m_model.WhatMaxSpeed().toString());
        MinSpeedTextField.setText(m_model.WhatMinSpeed().toString());
        SimTimeTextField.setText(m_model.GetSimulationTime().toString());
    }
    
    void FillAPComboBox(){
        APComboBox.removeAllItems();
        //Preconfiguration of the AP elements.
        for(int i=0;i<m_model.HowManyAP();i++){
            APComboBox.addItem(m_model.GetAP(i).WhatAP());
        }
    }
    
    public String ProtocolSelected(){
        if(AodvRadioButton.isSelected()) return "AODV";
        if(OptimumRadioButton.isSelected()) return "Optimum";
        if(OlsrRadioButton.isSelected()) return "OLSR";
        return "none";
    }
    
    void ConsoleText(){
        if (m_model.WhatStopwatch() == 0 )
            if(APComboBox.getItemCount() == 0)ConsoleTextField.setText("No access points defined.");
            else ConsoleTextField.setText("Ready.");
        if (m_model.WhatStopwatch() > 0 &&  m_model.WhatStopwatch() < m_model.GetProtocolLoadingTimeWaiting()) 
            ConsoleTextField.setText("Loading the protocol...");
        if (m_model.WhatStopwatch() > 0 &&  m_model.WhatStopwatch() == m_model.GetProtocolLoadingTimeWaiting() ) 
            ConsoleTextField.setText("Waiting for the AP...");
        if (m_model.WhatStopwatch() == m_model.GetApTimeWaiting()) 
            ConsoleTextField.setText("Starting simulation...");        
        if (m_model.WhatStopwatch() == (m_model.GetApTimeWaiting() + 1)) 
            ConsoleTextField.setText("Simulation in process...  ");
        if (m_model.WhatStopwatch() > m_model.GetRealSimulationTime() && !m_model.IsStatisticsEnded()) 
            ConsoleTextField.setText("Retrieving data from AP. Traffic flow Finished: " + 
                    m_model.statisticsControl.ReturnBufferValue());
    }
    
    void StopwatchText(){
        if (m_model.WhatStopwatch() >= m_model.GetWaitingSimulationTime()  &&
                m_model.WhatStopwatch() < m_model.GetRealSimulationTime()){
            CurrentTimeTextField.setText((m_model.WhatStopwatch() - m_model.GetWaitingSimulationTime() + 1)+"");
        }else{
            if (m_model.WhatStopwatch() <= m_model.GetWaitingSimulationTime()){
                CurrentTimeTextField.setText("Waiting.");
            }else{
                CurrentTimeTextField.setText(m_model.GetSimulationTime().toString());
            }
        }
    }
    
    void ShowEndSimulation(){
        ConsoleTextField.setText("Simulation finished!");
        SimulateButton.setEnabled(true);
        StopSimulationButton.setEnabled(false);
        RandomSceneryButton.setEnabled(true);
        EnableReplay(true);
        if(m_model.WhatMaxSpeed() > 0){
            ReplaySimulationButton.setEnabled(true);
        }
        SimulateButton.setText("New Simulation");
        ReplaySimulationButton.setText("Replay Simulation");
    }
    
    void UpdatePositionPanel(){
        XTextField.setText(m_model.selectionedAP1.x+"");
        YTextField.setText(m_model.selectionedAP1.y+"");
        ZTextField.setText(m_model.selectionedAP1.z+"");
        ATextField.setText(m_model.selectionedAP1.range+"");
    }
    
    void DisableSimulation(){
        SimulateButton.setEnabled(false);
        ReplaySimulationButton.setEnabled(false);
        StopSimulationButton.setEnabled(false);
    }
    
    void ReplaySimulationView(){
        SimulateButton.setEnabled(false);
        ReplaySimulationButton.setEnabled(false);
        StopSimulationButton.setEnabled(true);
        ReplaySimulationButton.setText("Replaying!!");
    }
    
    void RunningSimulationView(){
        SimulateButton.setEnabled(false);
        StopSimulationButton.setEnabled(true);
        ReplaySimulationButton.setEnabled(false);
        SimulateButton.setText("Simulating!!");
        RandomSceneryButton.setEnabled(false);
    }
    
    void DrawNewSize(Integer boardSize){
        if (boardSize < 500) boardSize = 500;
        if (boardSize > 10000) boardSize = 10000;
        AreaSlider.setValue(boardSize);
        SizeTextField.setText(boardSize.toString());
        m_model.ChangeBoardSize(boardSize);
        blackBoard.MapChange(RangeRadioButton.isSelected(), tags, grid, m_model.WhatBoundX(),
                m_model.WhatBoundY(), speedLine);
        Frame.repaint();
    }
    
    public void ChangeSimulationTime(Integer value){
        SimTimeTextField.setText(value.toString());
    }
    
    /**
     * habilite the replay option in the GUI
     */
    public void EnableReplay(boolean value){
        ReplayCheckBox.setEnabled(value);
        ReplayTimeTextField.setEnabled(value);
        if(m_model.WhatMaxSpeed() > 0){
            ReplaySimulationButton.setEnabled(value);
        }
    }
    
    public void MarkReplayCheck(boolean value){
        ReplayCheckBox.setSelected(value);
    }
    
    /*********************************************************************
     *
     *                             LISTENERS
     *
     *********************************************************************/
    
    
    void addStopSimulationListener(ActionListener al){
        StopSimulationButton.addActionListener(al);
    }
    
    void addSimulateButtonListener(ActionListener al){
        SimulateButton.addActionListener(al);
    }
    
    void addResetButtonListener(ActionListener al){
        ResetButton.addActionListener(al);
    }
    
    void addCloseButtonListener(ActionListener al){
        CloseButton.addActionListener(al);
    }
    
    void addXTextFieldListener(ActionListener al){
        XTextField.addActionListener(al);
    }
    
    void addYTextFieldListener(ActionListener al){
        YTextField.addActionListener(al);
    }
    
    void addZTextFieldListener(ActionListener al){
        ZTextField.addActionListener(al);
    }
    
    void addXBoundListener(ActionListener al){
        SimXTextField.addActionListener(al);
    }
    
    void addYBoundListener(ActionListener al){
        SimYTextField.addActionListener(al);
    }
    
    void addATextFieldListener(ActionListener al){
        ATextField.addActionListener(al);
    }
    
    void addSimTimeTextFieldListener(ActionListener al){
        SimTimeTextField.addActionListener(al);
    }
    
    void addSimPauseTextFieldListener(ActionListener al){
        SimPauseTextField.addActionListener(al);
    }
    
    void addMaxSpeedTextFieldListener(ActionListener al){
        MaxSpeedTextField.addActionListener(al);
    }
    
    void addMinSpeedTextFieldListener(ActionListener al){
        MinSpeedTextField.addActionListener(al);
    }            
    
    void addAPComboBoxListener(ActionListener al){
        APComboBox.addActionListener(al);
    }
    
    void addTrafficButtonListener(ActionListener al){
        TrafficButton.addActionListener(al);
    }
    
    void addRandomSceneryButtonListener(ActionListener al){
        RandomSceneryButton.addActionListener(al);
    }
    
    void addSizeTextFieldListener(ActionListener al){
        SizeTextField.addActionListener(al);
    }
    
    void addChangeReplayTimeListener(ActionListener al){
        ReplayTimeTextField.addActionListener(al);
    }
    
    void addReplaySimulationListener(ActionListener al){
        ReplaySimulationButton.addActionListener(al);
    }
    
    void addHelpButtonListener(ActionListener al){
        HelpButton.addActionListener(al);
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

        RangeGraph = new javax.swing.ButtonGroup();
        RoutingProtocolbuttonGroup = new javax.swing.ButtonGroup();
        SimPanel = new javax.swing.JPanel();
        BoundPanel = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        SimYTextField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        SimXTextField = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        TimePanel = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        SimTimeTextField = new javax.swing.JTextField();
        CurrentTimeTextField = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        SpeedPanel = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        MinSpeedTextField = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        SimPauseTextField = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        MaxSpeedTextField = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        SimLabel = new javax.swing.JLabel();
        TrafficPanel = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        TrafficButton = new javax.swing.JButton();
        ProtocolPanel = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        NoneRadioButton = new javax.swing.JRadioButton();
        AodvRadioButton = new javax.swing.JRadioButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        OptimumRadioButton = new javax.swing.JRadioButton();
        OlsrRadioButton = new javax.swing.JRadioButton();
        ShowPanel = new javax.swing.JPanel();
        ReplayCheckBox = new javax.swing.JCheckBox();
        ReplayTimeTextField = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        CenterPanel = new javax.swing.JPanel();
        ControlPanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        SizeTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        AreaSlider = new javax.swing.JSlider();
        Frame = new javax.swing.JPanel();
        StatusPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        ConsoleTextField = new javax.swing.JTextField();
        RightPanel = new javax.swing.JPanel();
        APPanel = new javax.swing.JPanel();
        PositionPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        ZTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        YTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        XTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        APComboBox = new javax.swing.JComboBox();
        APLabel = new javax.swing.JLabel();
        SignalPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        ATextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        RtsCheckBox = new javax.swing.JCheckBox();
        ButtonPanel = new javax.swing.JPanel();
        CloseButton = new javax.swing.JButton();
        ResetButton = new javax.swing.JButton();
        SimulateButton = new javax.swing.JButton();
        RandomSceneryButton = new javax.swing.JButton();
        ReplaySimulationButton = new javax.swing.JButton();
        StopSimulationButton = new javax.swing.JButton();
        HelpButton = new javax.swing.JButton();
        GraphPanel = new javax.swing.JPanel();
        RepresentationPanel = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        GraphRadioButton = new javax.swing.JRadioButton();
        RangeRadioButton = new javax.swing.JRadioButton();
        LabelCheckBox = new javax.swing.JCheckBox();
        ShowMovementCheckBox = new javax.swing.JCheckBox();
        GridCheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CASTADIVA - SIMULATION");
        setResizable(false);

        SimPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        BoundPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel16.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel16.setText("Bounds");

        jLabel18.setText("Y:");

        jLabel17.setText("X:");

        jLabel20.setText("m");

        jLabel19.setText("m");

        org.jdesktop.layout.GroupLayout BoundPanelLayout = new org.jdesktop.layout.GroupLayout(BoundPanel);
        BoundPanel.setLayout(BoundPanelLayout);
        BoundPanelLayout.setHorizontalGroup(
            BoundPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(BoundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(BoundPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(BoundPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, BoundPanelLayout.createSequentialGroup()
                            .add(jLabel18)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(SimYTextField))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, BoundPanelLayout.createSequentialGroup()
                            .add(jLabel17)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(SimXTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 81, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jLabel16))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(BoundPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel20)
                    .add(jLabel19))
                .addContainerGap(77, Short.MAX_VALUE))
        );
        BoundPanelLayout.setVerticalGroup(
            BoundPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(BoundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel16)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(BoundPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel17)
                    .add(jLabel19)
                    .add(SimXTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(BoundPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel18)
                    .add(SimYTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel20))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TimePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel21.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel21.setText("Time");

        jLabel22.setText("Total:");

        jLabel35.setText("Simulation:");

        SimTimeTextField.setText(m_model.GetSimulationTime()+"");

        CurrentTimeTextField.setEditable(false);
        CurrentTimeTextField.setText("0");

        jLabel36.setText("s");

        jLabel24.setText("s");

        org.jdesktop.layout.GroupLayout TimePanelLayout = new org.jdesktop.layout.GroupLayout(TimePanel);
        TimePanel.setLayout(TimePanelLayout);
        TimePanelLayout.setHorizontalGroup(
            TimePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(TimePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(TimePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel21)
                    .add(TimePanelLayout.createSequentialGroup()
                        .add(TimePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel22)
                            .add(jLabel35))
                        .add(17, 17, 17)
                        .add(TimePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(SimTimeTextField, 0, 0, Short.MAX_VALUE)
                            .add(CurrentTimeTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(TimePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel36)
                            .add(jLabel24))))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        TimePanelLayout.setVerticalGroup(
            TimePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(TimePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel21)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(TimePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel35)
                    .add(CurrentTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel24))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(TimePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel22)
                    .add(SimTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel36))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        SpeedPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel26.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel26.setText("Node Mobility");

        jLabel27.setText("Max speed:");

        MinSpeedTextField.setText("0");

        jLabel28.setText("m/s");

        jLabel23.setText("Pause:");

        jLabel25.setText("s");

        MaxSpeedTextField.setText("0");

        jLabel32.setText("-");

        org.jdesktop.layout.GroupLayout SpeedPanelLayout = new org.jdesktop.layout.GroupLayout(SpeedPanel);
        SpeedPanel.setLayout(SpeedPanelLayout);
        SpeedPanelLayout.setHorizontalGroup(
            SpeedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SpeedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(SpeedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(SpeedPanelLayout.createSequentialGroup()
                        .add(SpeedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel23)
                            .add(jLabel27))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(SpeedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(SpeedPanelLayout.createSequentialGroup()
                                .add(MinSpeedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(6, 6, 6)
                                .add(jLabel32)
                                .add(6, 6, 6)
                                .add(MaxSpeedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(SimPauseTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(SpeedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel28)
                            .add(jLabel25)))
                    .add(jLabel26))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        SpeedPanelLayout.setVerticalGroup(
            SpeedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SpeedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel26)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(SpeedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(MinSpeedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel32)
                    .add(MaxSpeedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel28)
                    .add(jLabel27))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(SpeedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel23)
                    .add(jLabel25)
                    .add(SimPauseTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        SimLabel.setFont(new java.awt.Font("Dialog", 1, 24));
        SimLabel.setText("Simulation");

        TrafficPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel29.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel29.setText("Traffic");

        TrafficButton.setText("Declare traffic");

        org.jdesktop.layout.GroupLayout TrafficPanelLayout = new org.jdesktop.layout.GroupLayout(TrafficPanel);
        TrafficPanel.setLayout(TrafficPanelLayout);
        TrafficPanelLayout.setHorizontalGroup(
            TrafficPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(TrafficPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(TrafficPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(TrafficButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                    .add(jLabel29))
                .addContainerGap())
        );
        TrafficPanelLayout.setVerticalGroup(
            TrafficPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, TrafficPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel29)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(TrafficButton)
                .addContainerGap())
        );

        ProtocolPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel15.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel15.setText("Routing Protocol:");

        RoutingProtocolbuttonGroup.add(NoneRadioButton);
        NoneRadioButton.setSelected(true);
        NoneRadioButton.setText("None");
        NoneRadioButton.setToolTipText("Standar Behaviour.");
        NoneRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        RoutingProtocolbuttonGroup.add(AodvRadioButton);
        AodvRadioButton.setText("AODV");
        AodvRadioButton.setToolTipText("Use The Castadiva AODV Compilation.");
        AodvRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        RoutingProtocolbuttonGroup.add(jRadioButton1);
        jRadioButton1.setText("DYMO");
        jRadioButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton1.setEnabled(false);

        RoutingProtocolbuttonGroup.add(jRadioButton2);
        jRadioButton2.setText("DSR");
        jRadioButton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton2.setEnabled(false);

        RoutingProtocolbuttonGroup.add(OptimumRadioButton);
        OptimumRadioButton.setText("Optimum");
        OptimumRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        RoutingProtocolbuttonGroup.add(OlsrRadioButton);
        OlsrRadioButton.setText("OLSR");
        OlsrRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout ProtocolPanelLayout = new org.jdesktop.layout.GroupLayout(ProtocolPanel);
        ProtocolPanel.setLayout(ProtocolPanelLayout);
        ProtocolPanelLayout.setHorizontalGroup(
            ProtocolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ProtocolPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(ProtocolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel15)
                    .add(ProtocolPanelLayout.createSequentialGroup()
                        .add(ProtocolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(NoneRadioButton)
                            .add(OptimumRadioButton)
                            .add(AodvRadioButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ProtocolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jRadioButton2)
                            .add(OlsrRadioButton)
                            .add(jRadioButton1))))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        ProtocolPanelLayout.setVerticalGroup(
            ProtocolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ProtocolPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel15)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ProtocolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(NoneRadioButton)
                    .add(OlsrRadioButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ProtocolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(OptimumRadioButton)
                    .add(jRadioButton1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ProtocolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(AodvRadioButton)
                    .add(jRadioButton2))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ShowPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        ReplayCheckBox.setText("Node Positions");
        ReplayCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ReplayCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ReplayCheckBoxItemStateChanged(evt);
            }
        });

        ReplayTimeTextField.setText("0");

        jLabel31.setText("s");

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel7.setText("Show");

        org.jdesktop.layout.GroupLayout ShowPanelLayout = new org.jdesktop.layout.GroupLayout(ShowPanel);
        ShowPanel.setLayout(ShowPanelLayout);
        ShowPanelLayout.setHorizontalGroup(
            ShowPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ShowPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(ShowPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ShowPanelLayout.createSequentialGroup()
                        .add(ReplayCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ReplayTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel31)
                        .add(10, 10, 10))
                    .add(jLabel7)))
        );
        ShowPanelLayout.setVerticalGroup(
            ShowPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, ShowPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(ShowPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel31)
                    .add(ReplayCheckBox)
                    .add(ReplayTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 219, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 53, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout SimPanelLayout = new org.jdesktop.layout.GroupLayout(SimPanel);
        SimPanel.setLayout(SimPanelLayout);
        SimPanelLayout.setHorizontalGroup(
            SimPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SimPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(SimPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, SimPanelLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 44, Short.MAX_VALUE)
                        .add(SimLabel)
                        .add(59, 59, 59))
                    .add(SimPanelLayout.createSequentialGroup()
                        .add(SimPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, TrafficPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, ShowPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, ProtocolPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, TimePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, SpeedPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, BoundPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        SimPanelLayout.setVerticalGroup(
            SimPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SimPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(SimLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(BoundPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(TimePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(SpeedPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ProtocolPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ShowPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(TrafficPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        CenterPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        ControlPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setText("Drawing area:");

        SizeTextField.setText(m_model.WhatBoardSize()+"");

        jLabel11.setText("m");

        AreaSlider.setMajorTickSpacing(1000);
        AreaSlider.setMaximum(10000);
        AreaSlider.setMinimum(500);
        AreaSlider.setMinorTickSpacing(250);
        AreaSlider.setPaintLabels(true);
        AreaSlider.setPaintTicks(true);
        AreaSlider.setToolTipText("Tamaño del cuadrado de dibujo.");
        AreaSlider.setValue(m_model.WhatBoardSize());
        AreaSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                AreaSliderStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout ControlPanelLayout = new org.jdesktop.layout.GroupLayout(ControlPanel);
        ControlPanel.setLayout(ControlPanelLayout);
        ControlPanelLayout.setHorizontalGroup(
            ControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, ControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(ControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(SizeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel10))
                .add(13, 13, 13)
                .add(AreaSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 448, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel11)
                .add(754, 754, 754))
        );
        ControlPanelLayout.setVerticalGroup(
            ControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(ControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(ControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(AreaSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel11))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, ControlPanelLayout.createSequentialGroup()
                        .add(jLabel10)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(SizeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        Frame.setBackground(new java.awt.Color(255, 255, 255));
        Frame.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Frame.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                FrameMouseClicked(evt);
            }
        });
        Frame.setLayout(new java.awt.BorderLayout());

        StatusPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel1.setText("Status:");

        ConsoleTextField.setEditable(false);

        org.jdesktop.layout.GroupLayout StatusPanelLayout = new org.jdesktop.layout.GroupLayout(StatusPanel);
        StatusPanel.setLayout(StatusPanelLayout);
        StatusPanelLayout.setHorizontalGroup(
            StatusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(StatusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ConsoleTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
                .addContainerGap())
        );
        StatusPanelLayout.setVerticalGroup(
            StatusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(StatusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(StatusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(ConsoleTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout CenterPanelLayout = new org.jdesktop.layout.GroupLayout(CenterPanel);
        CenterPanel.setLayout(CenterPanelLayout);
        CenterPanelLayout.setHorizontalGroup(
            CenterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(CenterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(CenterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(CenterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, ControlPanel, 0, 613, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, StatusPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(Frame, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE))
                .addContainerGap())
        );
        CenterPanelLayout.setVerticalGroup(
            CenterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, CenterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(Frame, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ControlPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(StatusPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        RightPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        APPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        PositionPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel6.setText("Position");

        jLabel12.setText("Z:");

        jLabel3.setText("Y:");

        jLabel2.setText("X:");

        jLabel5.setText("m");

        jLabel8.setText("m");

        jLabel13.setText("m");

        org.jdesktop.layout.GroupLayout PositionPanelLayout = new org.jdesktop.layout.GroupLayout(PositionPanel);
        PositionPanel.setLayout(PositionPanelLayout);
        PositionPanelLayout.setHorizontalGroup(
            PositionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PositionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(PositionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(PositionPanelLayout.createSequentialGroup()
                        .add(PositionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, PositionPanelLayout.createSequentialGroup()
                                .add(jLabel12)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(ZTextField))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, PositionPanelLayout.createSequentialGroup()
                                .add(jLabel3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(YTextField))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, PositionPanelLayout.createSequentialGroup()
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(XTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 107, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(PositionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel5)
                            .add(jLabel8)
                            .add(jLabel13)))
                    .add(jLabel6))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        PositionPanelLayout.setVerticalGroup(
            PositionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PositionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(PositionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(XTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(PositionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(YTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(PositionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(ZTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel13))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        APLabel.setFont(new java.awt.Font("Dialog", 1, 24));
        APLabel.setText("Access Point");

        SignalPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setText("Range:");

        jLabel9.setText("m");

        jLabel30.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel30.setText("WiFi Connection:");

        RtsCheckBox.setText("RTS/CTS");
        RtsCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        RtsCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                RtsCheckBoxStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout SignalPanelLayout = new org.jdesktop.layout.GroupLayout(SignalPanel);
        SignalPanel.setLayout(SignalPanelLayout);
        SignalPanelLayout.setHorizontalGroup(
            SignalPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SignalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(SignalPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(SignalPanelLayout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ATextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel9))
                    .add(jLabel30)
                    .add(RtsCheckBox))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        SignalPanelLayout.setVerticalGroup(
            SignalPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SignalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel30)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(SignalPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(jLabel9)
                    .add(ATextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 11, Short.MAX_VALUE)
                .add(RtsCheckBox)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout APPanelLayout = new org.jdesktop.layout.GroupLayout(APPanel);
        APPanel.setLayout(APPanelLayout);
        APPanelLayout.setHorizontalGroup(
            APPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, APPanelLayout.createSequentialGroup()
                .add(APPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, APPanelLayout.createSequentialGroup()
                        .add(33, 33, 33)
                        .add(APLabel))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, APPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(APComboBox, 0, 211, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, APPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(APPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(SignalPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(PositionPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        APPanelLayout.setVerticalGroup(
            APPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(APPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(APLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(APComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(PositionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(SignalPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ButtonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        CloseButton.setText("Close");

        ResetButton.setText(" Reset all AP");

        SimulateButton.setText("New Simulation");

        RandomSceneryButton.setText("Random Scenario");

        ReplaySimulationButton.setText("Replay Simulation");

        StopSimulationButton.setText("Stop Simulation");

        HelpButton.setText("Help");

        org.jdesktop.layout.GroupLayout ButtonPanelLayout = new org.jdesktop.layout.GroupLayout(ButtonPanel);
        ButtonPanel.setLayout(ButtonPanelLayout);
        ButtonPanelLayout.setHorizontalGroup(
            ButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(ButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(CloseButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                    .add(ResetButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                    .add(RandomSceneryButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                    .add(StopSimulationButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                    .add(ReplaySimulationButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                    .add(SimulateButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                    .add(HelpButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
                .addContainerGap())
        );
        ButtonPanelLayout.setVerticalGroup(
            ButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, ButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(SimulateButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ReplaySimulationButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(StopSimulationButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(RandomSceneryButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ResetButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(HelpButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(CloseButton)
                .addContainerGap())
        );

        GraphPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        RepresentationPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel14.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel14.setText("View");

        RangeGraph.add(GraphRadioButton);
        GraphRadioButton.setText("Graph");
        GraphRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        RangeGraph.add(RangeRadioButton);
        RangeRadioButton.setSelected(true);
        RangeRadioButton.setText("Range");
        RangeRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        RangeRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                RangeRadioButtonItemStateChanged(evt);
            }
        });

        LabelCheckBox.setSelected(true);
        LabelCheckBox.setText("Labels");
        LabelCheckBox.setToolTipText("Show the name of the APs");
        LabelCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        LabelCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LabelCheckBoxItemStateChanged(evt);
            }
        });

        ShowMovementCheckBox.setText("Movement");
        ShowMovementCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ShowMovementCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ShowMovementCheckBoxItemStateChanged(evt);
            }
        });

        GridCheckBox.setSelected(true);
        GridCheckBox.setText("Grid");
        GridCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        GridCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                GridCheckBoxItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout RepresentationPanelLayout = new org.jdesktop.layout.GroupLayout(RepresentationPanel);
        RepresentationPanel.setLayout(RepresentationPanelLayout);
        RepresentationPanelLayout.setHorizontalGroup(
            RepresentationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(RepresentationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(RepresentationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(GraphRadioButton)
                    .add(RangeRadioButton)
                    .add(jLabel14))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 30, Short.MAX_VALUE)
                .add(RepresentationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(LabelCheckBox)
                    .add(ShowMovementCheckBox)
                    .add(GridCheckBox))
                .addContainerGap())
        );
        RepresentationPanelLayout.setVerticalGroup(
            RepresentationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(RepresentationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(RepresentationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel14)
                    .add(GridCheckBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(RepresentationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(RangeRadioButton)
                    .add(LabelCheckBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(RepresentationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(GraphRadioButton)
                    .add(ShowMovementCheckBox))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout GraphPanelLayout = new org.jdesktop.layout.GroupLayout(GraphPanel);
        GraphPanel.setLayout(GraphPanelLayout);
        GraphPanelLayout.setHorizontalGroup(
            GraphPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(GraphPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(RepresentationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );
        GraphPanelLayout.setVerticalGroup(
            GraphPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, GraphPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(RepresentationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout RightPanelLayout = new org.jdesktop.layout.GroupLayout(RightPanel);
        RightPanel.setLayout(RightPanelLayout);
        RightPanelLayout.setHorizontalGroup(
            RightPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, RightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(RightPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, APPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(GraphPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(ButtonPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        RightPanelLayout.setVerticalGroup(
            RightPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(RightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(APPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GraphPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(ButtonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(SimPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(CenterPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(RightPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(RightPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, CenterPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(SimPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void GridCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_GridCheckBoxItemStateChanged
        grid = !grid;
        ModifyBlackBoard();
    }//GEN-LAST:event_GridCheckBoxItemStateChanged

    private void ShowMovementCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ShowMovementCheckBoxItemStateChanged
        speedLine=!speedLine;
        m_model.simulationSeconds = ReturnReplayTime();
        ModifyBlackBoard();
    }//GEN-LAST:event_ShowMovementCheckBoxItemStateChanged
    
    private void ReplayCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ReplayCheckBoxItemStateChanged
        m_model.replay = !m_model.replay;
        RandomSceneryButton.setEnabled(!m_model.replay);
        ResetButton.setEnabled(!m_model.replay);
        if(m_model.replay){
            if(m_model.WhatMaxSpeed() > 0){
                m_model.PositionateNodesInDeterminedSecond(ReturnReplayTime());
                m_model.simulationSeconds = ReturnReplayTime();
                ModifyBlackBoard();
            }
        }
    }//GEN-LAST:event_ReplayCheckBoxItemStateChanged
    
    private void RtsCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_RtsCheckBoxStateChanged
        m_model.RTS = !m_model.RTS;
    }//GEN-LAST:event_RtsCheckBoxStateChanged
    
    private void LabelCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LabelCheckBoxItemStateChanged
        tags=!tags;
        ModifyBlackBoard();
    }//GEN-LAST:event_LabelCheckBoxItemStateChanged
    
    private void RangeRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_RangeRadioButtonItemStateChanged
        ModifyBlackBoard();
    }//GEN-LAST:event_RangeRadioButtonItemStateChanged
    
    private void FrameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FrameMouseClicked
        int x = evt.getX();
        int y = evt.getY();
        float xm = x * m_model.WhatBoardSize() / Frame.getWidth();
        float ym = y * m_model.WhatBoardSize() / Frame.getHeight();
        
        if(APComboBox.getItemCount() > 0 && !m_model.replay && m_model.IsSimulationFinished()) {
            m_model.SetAP(APComboBox.getSelectedIndex(), m_model.selectionedAP1.WhatEthIP(),
                    m_model.selectionedAP1.WhatWifiIP(), m_model.selectionedAP1.WhatWifiMac(),
                    m_model.selectionedAP1.WhatUser(), m_model.selectionedAP1.WhatPwd(),
                    APComboBox.getSelectedItem().toString(), xm, ym, 0,
                    m_model.selectionedAP1.range, m_model.selectionedAP1.WhatWorkingDirectory(),
                    m_model.selectionedAP1.WhatProcessor(), m_model.selectionedAP1.WhatChannel(),
                    m_model.selectionedAP1.WhatMode(), m_model.selectionedAP1.WhatWifiDevice()
                    );
            m_model.selectionedAP1 = m_model.GetAP(APComboBox.getSelectedIndex());
            
            //Actualizamos el texto del simulador.
            UpdatePositionPanel();
            Frame.repaint();
        }
    }//GEN-LAST:event_FrameMouseClicked
    
    private void AreaSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_AreaSliderStateChanged
        DrawNewSize(AreaSlider.getValue());
    }//GEN-LAST:event_AreaSliderStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox APComboBox;
    private javax.swing.JLabel APLabel;
    private javax.swing.JPanel APPanel;
    private javax.swing.JTextField ATextField;
    private javax.swing.JRadioButton AodvRadioButton;
    private javax.swing.JSlider AreaSlider;
    private javax.swing.JPanel BoundPanel;
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JPanel CenterPanel;
    private javax.swing.JButton CloseButton;
    private javax.swing.JTextField ConsoleTextField;
    private javax.swing.JPanel ControlPanel;
    private javax.swing.JTextField CurrentTimeTextField;
    private javax.swing.JPanel Frame;
    private javax.swing.JPanel GraphPanel;
    private javax.swing.JRadioButton GraphRadioButton;
    private javax.swing.JCheckBox GridCheckBox;
    private javax.swing.JButton HelpButton;
    private javax.swing.JCheckBox LabelCheckBox;
    private javax.swing.JTextField MaxSpeedTextField;
    private javax.swing.JTextField MinSpeedTextField;
    private javax.swing.JRadioButton NoneRadioButton;
    private javax.swing.JRadioButton OlsrRadioButton;
    private javax.swing.JRadioButton OptimumRadioButton;
    private javax.swing.JPanel PositionPanel;
    private javax.swing.JPanel ProtocolPanel;
    private javax.swing.JButton RandomSceneryButton;
    private javax.swing.ButtonGroup RangeGraph;
    private javax.swing.JRadioButton RangeRadioButton;
    private javax.swing.JCheckBox ReplayCheckBox;
    private javax.swing.JButton ReplaySimulationButton;
    private javax.swing.JTextField ReplayTimeTextField;
    private javax.swing.JPanel RepresentationPanel;
    private javax.swing.JButton ResetButton;
    private javax.swing.JPanel RightPanel;
    private javax.swing.ButtonGroup RoutingProtocolbuttonGroup;
    private javax.swing.JCheckBox RtsCheckBox;
    private javax.swing.JCheckBox ShowMovementCheckBox;
    private javax.swing.JPanel ShowPanel;
    private javax.swing.JPanel SignalPanel;
    private javax.swing.JLabel SimLabel;
    private javax.swing.JPanel SimPanel;
    private javax.swing.JTextField SimPauseTextField;
    private javax.swing.JTextField SimTimeTextField;
    private javax.swing.JTextField SimXTextField;
    private javax.swing.JTextField SimYTextField;
    private javax.swing.JButton SimulateButton;
    private javax.swing.JTextField SizeTextField;
    private javax.swing.JPanel SpeedPanel;
    private javax.swing.JPanel StatusPanel;
    private javax.swing.JButton StopSimulationButton;
    private javax.swing.JPanel TimePanel;
    private javax.swing.JButton TrafficButton;
    private javax.swing.JPanel TrafficPanel;
    private javax.swing.JTextField XTextField;
    private javax.swing.JTextField YTextField;
    private javax.swing.JTextField ZTextField;
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
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    // End of variables declaration//GEN-END:variables
    
    
}

