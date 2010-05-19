/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package castadiva;

import castadiva.CastadivaController.ImportNsListener;
import castadiva.TrafficRecords.ExecutionRecord;
import castadiva_gui.ExecutionPlannerGUI;
import castadiva_gui.ExecutionPropiertiesDialog;
import castadiva_gui.MainMenuGUI;
import castadiva_gui.NewExternalTrafficGUI;
import castadiva_gui.SimulationGUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author alvaro
 */
public class ExecutionPlanner {
    private ExecutionPlannerGUI m_exec;
    private SimulationGUI m_simulationWindow;
    private CastadivaModel m_model;
    private MainMenuGUI m_view;
    private CastadivaController m_control;
    private ExecutionPropiertiesDialog prop;
    private int currentlySimulatingRow;


    ExecutionPlanner(SimulationGUI sim, final ExecutionPlannerGUI exec, CastadivaModel model,
                     NewExternalTrafficGUI attach, final MainMenuGUI main, CastadivaController control) {
        m_exec = exec;
        m_simulationWindow = sim;
        m_model=model;
        m_control = control;
        m_view = main;

        // Prevents the user from closing the whole program when closing the window
        exec.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        // Overrides the windowClosing event and restores the main menu instead.
        exec.addWindowListener(
                new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        main.setVisible(true);
                    }
                }
        );
    }

    void setPropListeners() {
        prop.addCancelButtonListener(new editExecutionPlannerCancel());
        prop.addAcceptButtonListener(new editExecutionPlannerAccept());
        prop.addSourceFolderButton(new editExecutionPlannerSource());
        prop.addResultsFolderButton(new editExecutionPlannerResults());
    }

    void executionPlannerListenersReady() {
        m_exec.addNewSimulationListener(new newSimulationExecutionPlanner());
        m_exec.addEditSimulationListener(new editExecutionPlanner());
        m_exec.addCloseButtonListener(new closeExecutionPlanner());
        m_exec.addDeleteButtonListener(new deleteExecutionPlanner());
        m_exec.addLoadScenarioButtonListener(new LoadScenaryExecutionPlanner());
        m_exec.addImportScenarioButtonListener(m_control.new ImportNsListener());
        m_exec.addGenerateSimulationButtonListener(new generateSimulationsExecutionPlanner());
        m_exec.addStopSimulationsButtonListener(new stopSimulationsExecutionPlanner());
        m_exec.addLoadListButtonListener(new loadExecutionPlannerList());
        m_exec.addSaveListButtonListener(new saveExecutionPlannerList());
    }

    void setVisible(boolean b) {
        m_exec.setVisible(b);
    }

    void newRow(String name) {
        m_exec.newRow(name);
    }

    void addPath(String path) {
        m_exec.paths.add(path);
    }

    /**
     * @author Wannes
     * Starts a new simulation based on the currentllySimulationRow value
     * It also updates the simulation status for the concerned row in the GUI
     * The simulations are stored as ExecutionRecords in the ExecutionPlannerGUI's Table.
     * @see EndExecutionPlannerSimulation
     * @see generateSimulationsExecutionPlanner
     */
    public void StartExecutionPlannerSimulation()
    {
        // Gets the concerned simulation's informations
        ExecutionRecord currentExecutionRecord = m_exec.getRow(currentlySimulatingRow);

        // Do we have to simulate for the current row ?
        if(currentExecutionRecord.getRuns()>0)
        {
            // The activation of this variable tells CastadivaModel what to do when the simulation ends.
            m_model.executionPlannerSimulating = true;
            m_exec.setButtonsForSimulation();

            // Loads the Scenario into Castadiva
            m_model.LoadCastadiva(currentExecutionRecord.getSourceFolder());

            // Starts a common simulation.
            m_model.AllSimulationSteaps();
        }
        else
        {
            // If there are no more runs, status goes to MSG_EXECUTION_PLANNER_DONE
            currentExecutionRecord.setStatus(m_exec.MSG_EXECUTION_PLANNER_DONE);
            m_exec.updateTable();
            
            // Are there more rows to be simulated ?
            if(m_exec.getNumberOfRows() > currentlySimulatingRow+1)
            {
                currentlySimulatingRow++;
                StartExecutionPlannerSimulation();
            }
            else
            {
                m_model.executionPlannerSimulating = false;
                m_model.StatisticsAreShowed();
                m_model.EndStopwatch();

                // Buttons are made available
                m_exec.setButtonsForConfiguration();
            }
        }
    }

    /**
     * @author Nacho Wannes
     * When a simulation ends, the CastadivaModel calls the following function
     * If there are runs left for the current simulation, a new simulation is processed
     * If there are no more runs for the current simulation, the next Scenario is loaded
     * @see generateSimulationsExecutionPlanner
     * @see StartExecutionPlannerSimulation
     */
    public void EndExecutionPlannerSimulation() {
        // Gets the concerned simulation informations
        ExecutionRecord currentExecutionRecord = m_exec.getRow(currentlySimulatingRow);

        // The result folder is created
        File f = new File(currentExecutionRecord.getResultsFolder() + File.separator + "Iterations");
        f.mkdir();

        // The simulation results for the current run are printed into a file in the results folder
        m_model.PrintTraffic(currentExecutionRecord.getResultsFolder() +  File.separator + "Iterations" + File.separator + currentExecutionRecord.getRuns() + "_DefinedTraffic.txt");

        //Copies the files "times_XXXX.txt" to the Iterations folder.
        //It scans all instructionsForNode folders
        //Author - Alvaro Torres
        File times = new File(m_model.computer.WhatWorkingDirectory());
        File destDir = new File(currentExecutionRecord.getResultsFolder() + File.separator + "Iterations");
        File[] linst = times.listFiles(m_control.new BeginWithFilenameFilter("instructionsForNode"));
        for(int j = 0; j < linst.length; j++) {
            if(linst[j].isDirectory()) {
                File[] ltimes = linst[j].listFiles(m_control.new BeginWithFilenameFilter("times"));
                for(int i = 0; i < ltimes.length; i++) {
                   // File aux = new File(currentExecutionRecord.getResultsFolder() +  File.separator + "Iterations" + File.separator + currentExecutionRecord.getRuns()+ "_" + ltimes[i].getName());
                   m_control.copyfile(ltimes[i].getAbsolutePath(), destDir.getAbsolutePath() + File.separator + currentExecutionRecord.getRuns() + "_" + ltimes[i].getName());
                }
            }
        }

        // The runs left for the simulation are decremented
        currentExecutionRecord.setRuns(currentExecutionRecord.getRuns()-1);
        m_exec.updateTable();

        StartExecutionPlannerSimulation();
    }

    public void setStatus(int row)
    {
        m_exec.SetStatus(row);
    }

    public int getCurrentlySimulatingRow(){
        return currentlySimulatingRow;
    }

    private void saveSimulationList(Vector rows, File f) {

        FileWriter fw = null;
        try {
            fw = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(fw);

            out.write("#Source folder\tResults folder\tRuns");
            out.newLine();

            for( int i = 0; i < rows.size(); i++) {
                ExecutionRecord e = (ExecutionRecord) rows.get(i);
                out.write(e.getSourceFolder() +"\t" + e.getResultsFolder() + "\t" + e.getRuns().toString());
                out.newLine();
            }
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(ExecutionPlanner.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ExecutionPlanner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private Vector loadSimulationList(File f) {
        FileReader fr = null;
        Vector rows = new Vector();
        try {
            fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String s;
            while((s = br.readLine()) != null) {
                s = s.trim();
                if(!s.isEmpty() && !s.startsWith("#")) {
                    ExecutionRecord e = new ExecutionRecord();
                    String [] stringRecord = s.split("\t");
                    e.setSourceFolder(stringRecord[0]);
                    e.setResultsFolder(stringRecord[1]);
                    e.setRuns(Integer.parseInt(stringRecord[2]));
                    if(e.getRuns() > 0) {
                        e.setStatus(m_exec.MSG_EXECUTION_PLANNER_READY);
                    }else{
                        e.setStatus(m_exec.MSG_EXECUTION_PLANNER_DONE);
                    }
                    rows.add(e);
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExecutionPlanner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex){
            Logger.getLogger(ExecutionPlanner.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rows;


    }

    /****************************************************************\
     *                         LISTENERS                            *
    \****************************************************************/

    class closeExecutionPlanner implements ActionListener {

        public void actionPerformed(ActionEvent arg0) {
            m_exec.setVisible(false);
            m_view.setVisible(true);
        }
    }

    class LoadScenaryExecutionPlanner implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String file;
            if (!(file = m_control.ExplorationWindow("Load",
                    JFileChooser.DIRECTORIES_ONLY)).equals("")) {

                File f1 = new File(file);

                if (m_control.hasScenarioDir(f1)) {
                    f1 = new File(f1.getAbsolutePath() + File.separator + "Scenario");
                }
                if (m_control.isScenarioDir(f1)) {
                    File APfile = new File(f1.getAbsolutePath() + File.separator + m_model.FILE_APS);
                    m_model.Reset();
                    m_model.LoadAP(APfile.getAbsolutePath());

                    m_simulationWindow.setExecutionPlanner(true);
                    m_simulationWindow.changeSimulateButtonText("Accept");
                    m_simulationWindow.FillFields();
                    m_simulationWindow.FillAPComboBox();
                    m_simulationWindow.ActivateButtons(true);
                    m_exec.setVisible(false);
                    f1 = f1.getParentFile();
                    m_simulationWindow.setLoadPath(f1.getAbsolutePath());
                    m_simulationWindow.setVisible(true);

                }else{
                     JOptionPane.showMessageDialog(m_exec,"This directory does not seem to contain a castadiva simulation.");
                }
            }
        }
    }

    /**
     * @author Wannes
     * When the Generate Simulations button is pressed, the simulation planner starts its work.
     * @see EndExecutionPlannerSimulation
     * @see StartExecutionPlannerSimulation
     */
    class generateSimulationsExecutionPlanner implements ActionListener {

        public void actionPerformed(ActionEvent arg0) {
            if(m_exec.getNumberOfRows() > 0 )
            {
                // The simulations start with the upper row
                currentlySimulatingRow = 0;

                // The first simulation is processed. When that simulation ends, it calls the next one.
                StartExecutionPlannerSimulation();
            } else {
                JOptionPane.showMessageDialog(null, "You must configure a simulation first.");
            }
        }
    }

   /**
    * @author Wannes
    * Allows to cancel the simulation planner's processing
    */
   class stopSimulationsExecutionPlanner implements ActionListener {

        public void actionPerformed(ActionEvent arg0) {
            ExecutionRecord currentExecutionRecord = m_exec.getRow(currentlySimulatingRow);

            // Current simulation is stopped
            m_model.KillSimulation();

            // Status of the current simulation is updated
            currentExecutionRecord.setStatus(m_exec.MSG_EXECUTION_PLANNER_CANCELLED);
            m_exec.updateTable();

            m_model.executionPlannerSimulating = false;
            m_model.StatisticsAreShowed();
            m_model.EndStopwatch();

            // Buttons are set available
            m_exec.setButtonsForConfiguration();
        }
    }


    class newSimulationExecutionPlanner implements ActionListener {

        public void actionPerformed(ActionEvent arg0) {
            m_simulationWindow.setExecutionPlanner(true);
            m_simulationWindow.setVisible(true);
            m_simulationWindow.FillFields();
            m_simulationWindow.FillAPComboBox();
            m_simulationWindow.ActivateButtons(true);
        }
    }

    class editExecutionPlanner implements ActionListener {

        public void actionPerformed(ActionEvent arg0) {
            int selected = m_exec.getNumberSelectedRow();
            if (selected == -1) {
                JOptionPane.showMessageDialog(null, "You must select a row first.");
            } else {
                prop = new ExecutionPropiertiesDialog(m_exec, true, selected);
                setPropListeners();
                prop.setVisible(true);
/*              m_model.LoadCastadiva(m_exec.paths.get(selected));
                if(!m_model.mobilityModel.equals("RANDOM WAY POINT")) {
                    m_simulationWindow.ChangeMobilityModel(m_model.mobilityModel);
                }
                m_simulationWindow.ChangeRoutingProtocol(m_model.routingProtocol);
                Integer size;

                deleteSimulation();

                if (m_model.ExistsOldMobility()) {
                    m_simulationWindow.EnableReplay(true);
                }
                m_simulationWindow.ModifyBlackBoard();
                size = (int) Math.max(m_model.GetBoundX(), m_model.GetBoundY());
                m_simulationWindow.DrawNewSize(size);
                m_attachTraffic.UpdateWindow();
                m_simulationWindow.setVisible(true);*/
            }
        }
    }

    class deleteExecutionPlanner implements ActionListener {

        public void actionPerformed(ActionEvent arg0) {
            if (m_exec.getNumberSelectedRow() != -1) {
                int n = JOptionPane.showConfirmDialog(
                        null,
                        "You are going to delete a simulation, are you sure?", "WARNING",
                        JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    m_exec.deleteRow(m_exec.getNumberSelectedRow());
                }
            } else {
                JOptionPane.showMessageDialog(null, "You must select a row first");
            }
        }
    }

    class editExecutionPlannerCancel implements ActionListener {

        public void actionPerformed(ActionEvent arg0) {
            prop.dispose();
        }
    }

    class editExecutionPlannerAccept implements ActionListener {

        public void actionPerformed(ActionEvent arg0) {
            Integer selected = prop.getSelectedRow();

            ExecutionRecord exe = m_exec.getRow(selected);

            //TODO Error checking
            exe.setSourceFolder(prop.getSourceText());
            exe.setResultsFolder(prop.getResultsText());
            exe.setRuns(prop.getRuns());
            exe.setStatus(m_exec.MSG_EXECUTION_PLANNER_READY);

            m_exec.updateTable();

            prop.dispose();
        }
    }

    class editExecutionPlannerSource implements ActionListener {

        public void actionPerformed(ActionEvent arg0) {
            File f = new File(m_model.pathScenario);
            JFileChooser select = new JFileChooser(f);
            select.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int selection = select.showDialog(prop, "Select");
            if (selection == JFileChooser.APPROVE_OPTION) {
                File file = select.getSelectedFile();
                if(file.getName().equals("")) {
                   JOptionPane.showMessageDialog(prop, "File not valid");
                }else{
                    if(file.getName().equals("Scenario")) {
                        file = file.getParentFile();
                    }
                    prop.setSourceText(file.getAbsolutePath());
                    m_model.pathScenario = file.getAbsolutePath();
                }
            }
        }
    }

    class editExecutionPlannerResults implements ActionListener {

        public void actionPerformed(ActionEvent arg0) {
            File f = new File(m_model.pathScenario);
            JFileChooser select = new JFileChooser(f);
            select.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int selection = select.showDialog(prop, "Select");
            if (selection == JFileChooser.APPROVE_OPTION) {
                File file = select.getSelectedFile();
                if(file.getName().equals("")) {
                   JOptionPane.showMessageDialog(prop, "File not valid");
                }else{
                    if(file.getName().equals("Scenario") || file.getName().equals("Results")) {
                        file = file.getParentFile();
                    }
                    m_model.pathScenario = file.getAbsolutePath();
                    prop.setResultsText(file.getAbsolutePath());
                }
            }
        }
    }

    class saveExecutionPlannerList implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            File f = new File(m_model.pathScenario);
            JFileChooser save = new JFileChooser(f);
            int selected = save.showSaveDialog(save);

            if(selected == JFileChooser.APPROVE_OPTION) {
                File file = save.getSelectedFile();
                Vector list = m_exec.getRowVector();
                saveSimulationList(list, file);
                file = null;
            }

        }

    }

    class loadExecutionPlannerList implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            File f = new File(m_model.pathScenario);
            JFileChooser load = new JFileChooser(f);
            int selected = load.showOpenDialog(load);

            if(selected == JFileChooser.APPROVE_OPTION) {
                File file = load.getSelectedFile();
                Vector list = loadSimulationList(file);
                m_exec.setRowVector(list);
                file = null;
            }
        }

    }
}
