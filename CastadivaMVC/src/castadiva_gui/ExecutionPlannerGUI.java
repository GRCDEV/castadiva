/*
 * executionPlannerGUI.java
 *
 * Created on 13 de marzo de 2009, 11:10
 */
package castadiva_gui;

import castadiva.*;
import castadiva.TableModels.ExecutionPlannerTableModel;
import castadiva.TrafficRecords.ExecutionRecord;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.JViewport;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


/**
 *
 * @author  nacho
 */
public class ExecutionPlannerGUI extends javax.swing.JFrame {
    CastadivaModel model;
    Vector<String> simulaciones = new Vector<String>();
    public Vector<String>paths = new Vector<String>();
    int numSim = 0;
    ExecutionPlannerTableModel tableModel = new ExecutionPlannerTableModel();

    public final String MSG_EXECUTION_PLANNER_DONE = "Done";
    public final String MSG_EXECUTION_PLANNER_READY = "Ready";
    public final String MSG_EXECUTION_PLANNER_CANCELLED = "Cancelled";
    public final String MSG_EXECUTION_PLANNER_LOAD = "Loading protocol...";
    public final String MSG_EXECUTION_PLANNER_WAITING = "Waiting for the AP...";
    public final String MSG_EXECUTION_PLANNER_STARTING = "Starting simulation...";
    public final String MSG_EXECUTION_PLANNER_SIMULATING = "Simulation in process...  ";
    public final String MSG_EXECUTION_PLANNER_RETRIEVING = "Retrieving data from AP. ";
    /** Creates new form executionPlannerGUI */
    public ExecutionPlannerGUI(CastadivaModel model) {
        initComponents();

        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        this.model = model;
        
        JTplanner.setModel(tableModel);


        JViewport scroll = (JViewport) JTplanner.getParent();
        int width = scroll.getWidth();
        int columnWidth = 0;
        TableColumnModel modeloColumna = JTplanner.getColumnModel();
        TableColumn columnaTabla;
        for (int i = 0; i < JTplanner.getColumnCount(); i++) {
            columnaTabla = modeloColumna.getColumn(i);
            switch (i) {
                case 0:
                    columnWidth = (30 * width) / 100;
                    break;
                case 1:
                    columnWidth = (30 * width) / 100;
                    break;
                case 2:
                    columnWidth = (7 * width) / 100;
                    break;
                case 3:
                    columnWidth = (33 * width) / 100;
                    break;
            }
            columnaTabla.setPreferredWidth(columnWidth);
        }

        // Activates and desactivates buttons
        setButtonsForConfiguration();
    }

    public void newRow(String name) {                
        ExecutionRecord exe = new ExecutionRecord();

        exe.setSourceFolder(name);
        exe.setResultsFolder(name);
        exe.setRuns(10);
        exe.setStatus(MSG_EXECUTION_PLANNER_READY);
        
        tableModel.addRow(exe);
        updateTable();
    }
    
    public int getNumberSelectedRow() {
            return JTplanner.getSelectedRow();
    }

    public int getNumberOfRows() {
         return JTplanner.getRowCount();
    }
    public int getTotalScenarios() {
         return JTplanner.getRowCount();
    }
    
    public int getRuns(int n) {
         Integer i = getRow(n).getRuns();
         return i;
    }
    
    public String getSimulationName() {
            if(this.getNumberSelectedRow() != -1) {
                return getRow(getNumberSelectedRow()).getSourceFolder();
            }
            else return "";
    }
    
    public String getSimulationName(int n) {
         String temp = getRow(n).getSourceFolder();
         return temp;
    }
    
    public String getTargetFolder(int n) {
            String target = "";
            target = getRow(n).getResultsFolder();
            return target;
    }
    
    public ExecutionRecord getRow(int row) {
        return tableModel.getRow(row);
    }

    public Vector getRowVector() {
        return tableModel.getRowVector();
    }

    public void setRowVector(Vector list) {
        tableModel.setRowVector(list);
        updateTable();
    }

    public void deleteRow(int row) {
        tableModel.delRow(row);
        updateTable();
    }

     public void updateTable() {
        tableModel.fireTableDataChanged();
    }

    public Boolean getWaitChecked() {
        return StartSimulationCheckBox.isSelected();
    }

    public Integer getWaitingTime() {
        Integer MAXTime = 24 * 60; //A complete DAY in minutes.
        
        if(getWaitChecked()) {
            Integer hora = (Integer)StartSimulationHoursSpinner.getValue();
            Integer minutos = (Integer)StartSimulationMinutesSpinner.getValue();

            Calendar rightNow = Calendar.getInstance();
            Integer hour = rightNow.get(Calendar.HOUR_OF_DAY);
            Integer minutes = rightNow.get(Calendar.MINUTE);




            Integer actualTime = hour * 60 + minutes;
            Integer desiredTime = hora * 60 + minutos;

            Integer WaitingTime;

            if(actualTime > desiredTime) {
                WaitingTime = desiredTime + (MAXTime - actualTime);
            }else{
                WaitingTime = desiredTime - actualTime;
            }

            System.out.println("Spinner-> Hora " + hora + " Minutos " + minutos);
            System.out.println("Local-> Hora " + hour + " Minutos " + minutes);
            System.out.println("Operaciones-> actual " + actualTime + " deseado " + desiredTime);

            return WaitingTime;
        }else{
            return 0;
        }
    }

    /**
     * @author Wannes
     * This function returns the current status depending on the simulation time
     * Note : the status information should probably not be stored in the GUI
     * but it is also how it is done in the SimulationGUI class
     * @param row
     * @see SimulationGUI.ConsoleText()
     */
    public void SetStatus(int row) {
        ExecutionRecord record = getRow(row);
        if (model.WhatStopwatch() > 0 && model.WhatStopwatch() < model.GetProtocolLoadingTimeWaiting()) {
            record.setStatus(MSG_EXECUTION_PLANNER_LOAD);
        }
        if (model.WhatStopwatch() > 0 && model.WhatStopwatch() == model.GetProtocolLoadingTimeWaiting()) {
            record.setStatus(MSG_EXECUTION_PLANNER_WAITING);
        }
        if (model.WhatStopwatch() == model.GetApTimeWaiting()) {
            record.setStatus(MSG_EXECUTION_PLANNER_STARTING);
        }
        if (model.WhatStopwatch() == (model.GetApTimeWaiting() + 1)) {
            record.setStatus(MSG_EXECUTION_PLANNER_SIMULATING);
        }
        if (model.WhatStopwatch() > model.GetRealSimulationTime() && !model.IsStatisticsEnded()) {
            record.setStatus(MSG_EXECUTION_PLANNER_RETRIEVING);
        }
        this.updateTable();
    }

    public void setButtonsForSimulation()
    {
        this.JBclose.setEnabled(false);
        this.JBdelete.setEnabled(false);
        this.JBedit.setEnabled(false);
        this.JBgenerate.setEnabled(false);
        this.JBloadsim.setEnabled(false);
        this.JBnewsim.setEnabled(false);
        this.JBResetAPs.setEnabled(true);
        this.JBSaveList.setEnabled(false);
        this.JBLoadList.setEnabled(false);
        this.StartSimulationCheckBox.setEnabled(false);
        this.StartSimulationHoursSpinner.setEnabled(false);
        this.StartSimulationMinutesSpinner.setEnabled(false);
    }

    public void setButtonsForConfiguration()
    {
        this.JBclose.setEnabled(true);
        this.JBdelete.setEnabled(true);
        this.JBedit.setEnabled(true);
        this.JBgenerate.setEnabled(true);
        this.JBloadsim.setEnabled(true);
        this.JBnewsim.setEnabled(true);
        this.JBResetAPs.setEnabled(false);
        this.JBSaveList.setEnabled(true);
        this.JBLoadList.setEnabled(true);
        this.StartSimulationCheckBox.setEnabled(true);
        this.StartSimulationHoursSpinner.setEnabled(true);
        this.StartSimulationMinutesSpinner.setEnabled(true);
    }

    /**
     * Allows to change the text value of the Reset APS button and set it to cancel
     * When a simulation is plannified, it should be cancelable
     * @param cancel
     */
    public void setResetButtonToCancel(boolean cancel)
    {
        if(cancel){
            this.JBResetAPs.setText("Cancel");
        }
        else{
            this.JBResetAPs.setText("Reset Access Points");
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        JTplanner = new javax.swing.JTable();
        ButtonsPanel = new javax.swing.JPanel();
        JBedit = new javax.swing.JButton();
        JBdelete = new javax.swing.JButton();
        JBgenerate = new javax.swing.JButton();
        JBclose = new javax.swing.JButton();
        JBloadsim = new javax.swing.JButton();
        JBnewsim = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        JBResetAPs = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        JBLoadList = new javax.swing.JButton();
        JBSaveList = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JSeparator();
        StartSimulationHoursSpinner = new javax.swing.JSpinner();
        StartSimulationMinutesSpinner = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        StartSimulationCheckBox = new javax.swing.JCheckBox();
        jSeparator3 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CASTADIVA - Execution Planner");

        JTplanner.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Target Folder", "Runs", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        JTplanner.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        JTplanner.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(JTplanner);

        JBedit.setText("Edit Simulation");

        JBdelete.setText("Delete Simulation");

        JBgenerate.setText("Generate Simulations");

        JBclose.setText("Close");

        JBloadsim.setText("Load Scenario");

        JBnewsim.setText("New Simulation");

        JBResetAPs.setText("Reset Access Points");

        JBLoadList.setText("Load list");

        JBSaveList.setText("Save list");

        StartSimulationHoursSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                StartSimulationHoursSpinnerStateChanged(evt);
            }
        });

        StartSimulationMinutesSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                StartSimulationMinutesSpinnerStateChanged(evt);
            }
        });

        jLabel2.setText("Hours");

        jLabel3.setText("Minutes");

        StartSimulationCheckBox.setText("Start simulation at");

        javax.swing.GroupLayout ButtonsPanelLayout = new javax.swing.GroupLayout(ButtonsPanel);
        ButtonsPanel.setLayout(ButtonsPanelLayout);
        ButtonsPanelLayout.setHorizontalGroup(
            ButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ButtonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(JBclose, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(JBResetAPs, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(JBgenerate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(JBnewsim, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(JBloadsim, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(JBedit, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(JBdelete, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(jSeparator7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(JBLoadList, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(JBSaveList, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addGroup(ButtonsPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(StartSimulationHoursSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(StartSimulationMinutesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(StartSimulationCheckBox)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))
                .addContainerGap())
        );
        ButtonsPanelLayout.setVerticalGroup(
            ButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ButtonsPanelLayout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(JBnewsim)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBloadsim)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBedit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBdelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBLoadList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBSaveList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(StartSimulationCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(StartSimulationMinutesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(StartSimulationHoursSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(137, 137, 137)
                .addComponent(JBgenerate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBResetAPs)
                .addGap(5, 5, 5)
                .addComponent(JBclose)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
                    .addComponent(ButtonsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void StartSimulationMinutesSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_StartSimulationMinutesSpinnerStateChanged
        // TODO add your handling code here:
        if((Integer)StartSimulationMinutesSpinner.getValue() < 0) {
            StartSimulationMinutesSpinner.setValue(new Integer(0));
        }
        if((Integer)StartSimulationMinutesSpinner.getValue() > 59) {
            StartSimulationMinutesSpinner.setValue(new Integer(59));
        }
    }//GEN-LAST:event_StartSimulationMinutesSpinnerStateChanged

    private void StartSimulationHoursSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_StartSimulationHoursSpinnerStateChanged
        // TODO add your handling code here:
        if((Integer)StartSimulationHoursSpinner.getValue() < 0) {
            StartSimulationHoursSpinner.setValue(new Integer(0));
        }
        if((Integer)StartSimulationHoursSpinner.getValue() > 23) {
            StartSimulationHoursSpinner.setValue(new Integer(23));
        }
    }//GEN-LAST:event_StartSimulationHoursSpinnerStateChanged

    public void addNewSimulationListener(ActionListener a1) {
        JBnewsim.addActionListener(a1);
    }
    
    public void addEditSimulationListener(ActionListener a1) {
        JBedit.addActionListener(a1);
    }
        
    public void addCloseButtonListener(ActionListener al) {
        JBclose.addActionListener(al);
    }
    
    public void addDeleteButtonListener(ActionListener a1) {
        JBdelete.addActionListener(a1);
    }

    public void addLoadScenarioButtonListener(ActionListener a1) {
        JBloadsim.addActionListener(a1);
    }

    public void addGenerateSimulationButtonListener(ActionListener a1) {
        JBgenerate.addActionListener(a1);
    }

    public void addResetAPsButtonListener(ActionListener a1) {
        JBResetAPs.addActionListener(a1);
    }

    public void addLoadListButtonListener(ActionListener a) {
        JBLoadList.addActionListener(a);
    }

    public void addSaveListButtonListener(ActionListener a) {
        JBSaveList.addActionListener(a);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ButtonsPanel;
    private javax.swing.JButton JBLoadList;
    private javax.swing.JButton JBResetAPs;
    private javax.swing.JButton JBSaveList;
    private javax.swing.JButton JBclose;
    private javax.swing.JButton JBdelete;
    private javax.swing.JButton JBedit;
    private javax.swing.JButton JBgenerate;
    private javax.swing.JButton JBloadsim;
    private javax.swing.JButton JBnewsim;
    private javax.swing.JTable JTplanner;
    private javax.swing.JCheckBox StartSimulationCheckBox;
    private javax.swing.JSpinner StartSimulationHoursSpinner;
    private javax.swing.JSpinner StartSimulationMinutesSpinner;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator7;
    // End of variables declaration//GEN-END:variables


}