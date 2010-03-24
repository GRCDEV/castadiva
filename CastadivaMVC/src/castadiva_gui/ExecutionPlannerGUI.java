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
import java.util.Vector;
import javax.swing.JViewport;
import javax.swing.table.DefaultTableModel;
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
        exe.setRuns(1);
        exe.setStatus("Ready");
        
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
            record.setStatus("Loading the protocol...");
        }
        if (model.WhatStopwatch() > 0 && model.WhatStopwatch() == model.GetProtocolLoadingTimeWaiting()) {
            record.setStatus("Waiting for the AP...");
        }
        if (model.WhatStopwatch() == model.GetApTimeWaiting()) {
            record.setStatus("Starting simulation...");
        }
        if (model.WhatStopwatch() == (model.GetApTimeWaiting() + 1)) {
            record.setStatus("Simulation in process...  ");
        }
        if (model.WhatStopwatch() > model.GetRealSimulationTime() && !model.IsStatisticsEnded()) {
            record.setStatus("Retrieving data from AP. ");
        }
        this.updateTable();
    }

    public void setButtonsForSimulation()
    {
        this.JBclose.setEnabled(false);
        this.JBdelete.setEnabled(false);
        this.JBedit.setEnabled(false);
        this.JBgenerate.setEnabled(false);
        this.JBimportcity.setEnabled(false);
        this.JBimportsc.setEnabled(false);
        this.JBloadsim.setEnabled(false);
        this.JBnewsim.setEnabled(false);
        this.JBStopSimulations.setEnabled(true);
    }

    public void setButtonsForConfiguration()
    {
        this.JBclose.setEnabled(true);
        this.JBdelete.setEnabled(true);
        this.JBedit.setEnabled(true);
        this.JBgenerate.setEnabled(true);
        this.JBimportcity.setEnabled(true);
        this.JBimportsc.setEnabled(true);
        this.JBloadsim.setEnabled(true);
        this.JBnewsim.setEnabled(true);
        this.JBStopSimulations.setEnabled(false);
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
        JBimportcity = new javax.swing.JButton();
        JBimportsc = new javax.swing.JButton();
        JBloadsim = new javax.swing.JButton();
        JBnewsim = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        JBStopSimulations = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        JBLoadList = new javax.swing.JButton();
        JBSaveList = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();

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

        JBimportcity.setText("Import CityMob Scenario");

        JBimportsc.setText("Import NS-2 Scenario");

        JBloadsim.setText("Load Scenario");

        JBnewsim.setText("New Simulation");

        JBStopSimulations.setText("Stop Simulations");

        JBLoadList.setText("Load list");

        JBSaveList.setText("Save list");

        javax.swing.GroupLayout ButtonsPanelLayout = new javax.swing.GroupLayout(ButtonsPanel);
        ButtonsPanel.setLayout(ButtonsPanelLayout);
        ButtonsPanelLayout.setHorizontalGroup(
            ButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ButtonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(jSeparator7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(JBLoadList, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(JBnewsim, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(JBloadsim, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(JBimportsc, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(JBimportcity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(JBclose, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(JBStopSimulations, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(JBgenerate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(JBedit, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(JBdelete, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(JBSaveList, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE))
                .addContainerGap())
        );
        ButtonsPanelLayout.setVerticalGroup(
            ButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ButtonsPanelLayout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(JBnewsim)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBloadsim)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBimportsc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBimportcity, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBedit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBdelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.DEFAULT_SIZE, 10, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBLoadList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBSaveList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(74, 74, 74)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(JBgenerate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JBStopSimulations)
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 743, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE)
                    .addComponent(ButtonsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    public void addImportScenarioButtonListener(ActionListener a1) {
        JBimportsc.addActionListener(a1);
    }

    public void addImportCityMobScenarioButtonListener(ActionListener a1) {
        JBimportcity.addActionListener(a1);
    }

    public void addGenerateSimulationButtonListener(ActionListener a1) {
        JBgenerate.addActionListener(a1);
    }

    public void addStopSimulationsButtonListener(ActionListener a1) {
        JBStopSimulations.addActionListener(a1);
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
    private javax.swing.JButton JBSaveList;
    private javax.swing.JButton JBStopSimulations;
    private javax.swing.JButton JBclose;
    private javax.swing.JButton JBdelete;
    private javax.swing.JButton JBedit;
    private javax.swing.JButton JBgenerate;
    private javax.swing.JButton JBimportcity;
    private javax.swing.JButton JBimportsc;
    private javax.swing.JButton JBloadsim;
    private javax.swing.JButton JBnewsim;
    private javax.swing.JTable JTplanner;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    // End of variables declaration//GEN-END:variables


}