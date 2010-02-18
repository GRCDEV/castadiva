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
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author alvaro
 */
public class ExecutionPlanner {
    private ExecutionPlannerGUI m_exec;
    private SimulationGUI m_simulationWindow;
    private CastadivaModel m_model;
    private NewExternalTrafficGUI m_attachTraffic;
    private MainMenuGUI m_view;
    private CastadivaController m_control;
    private ExecutionPropiertiesDialog prop;

    ExecutionPlanner(SimulationGUI sim, ExecutionPlannerGUI exec, CastadivaModel model,
                     NewExternalTrafficGUI attach, MainMenuGUI main, CastadivaController control) {
        m_exec = exec;
        m_simulationWindow = sim;
        m_model=model;
        m_attachTraffic = attach;
        m_control = control;
        m_view = main;
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

    public void EndExecutionPlannerSimulation() {

        File f = new File(m_model.pathTarget + m_exec.getSimulationName(m_exec.scenario) + File.separator + "Iterations");
        f.mkdir();
        m_model.PrintTraffic(m_model.pathTarget + m_exec.getSimulationName(m_exec.scenario) + File.separator + "Iterations" + File.separator + m_model.plannerLoops + "_DefinedTraffic.txt");
        m_model.NextPlannerIteration(m_exec.getRuns(m_exec.scenario));
        if (!m_model.IsEndOfPlannerSimulation()) {
            m_model.GenerateExecutionPlannerSimulation(m_model.pathTarget, m_exec.getRuns(m_exec.scenario));
            m_simulationWindow.ModifyBlackBoard();
        } else {
            if (m_exec.scenario != (m_exec.getTotalScenarios() - 1)) {
                m_exec.scenario++;
                m_model.LoadCastadiva(m_exec.paths.get(m_exec.scenario).toString());

                m_model.pathTarget = m_exec.getTargetFolder(m_exec.scenario);
                m_model.GenerateExecutionPlannerSimulation(m_exec.getTargetFolder(m_exec.scenario), m_exec.getRuns(m_exec.scenario));
                m_simulationWindow.ModifyBlackBoard();
            } else {
                m_model.executionPlannerSimulating = false;
                m_model.plannerNotEnded = false;
                m_exec.scenario = 0;
                m_model.StatisticsAreShowed();
                m_simulationWindow.ChangeReplayTime(m_model.GetSimulationTime());
                m_simulationWindow.EnableReplay(true);
                m_model.EndStopwatch();
            }
        }
    }

    private void deleteSimulation() {
        int selected = m_exec.getNumberSelectedRow();
        //delete the files from the HD
        File f1 = new File(m_exec.paths.get(selected) + "/APs.dat");
        f1.delete();
        f1 = new File(m_exec.paths.get(selected) + "/Computer.dat");
        f1.delete();
        f1 = new File(m_exec.paths.get(selected) + "/ExternalTraffic.dat");
        f1.delete();
        f1 = new File(m_exec.paths.get(selected) + "/Scenario.dat");
        f1.delete();
        f1 = new File(m_exec.paths.get(selected));
        f1.delete();
        //delete file path
        m_exec.paths.remove(selected);
        //delete row in JTable
        m_exec.deleteRow(selected);
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
                /*TODO Cargar correctamente escenario
                 */
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
                    m_simulationWindow.setLoadPath(f1.getAbsolutePath());
                    m_simulationWindow.FillFields();
                    m_simulationWindow.FillAPComboBox();
                    m_simulationWindow.ActivateButtons(true);
                    m_exec.setVisible(false);
                    m_simulationWindow.setVisible(true);

                }else{
                     JOptionPane.showMessageDialog(m_exec,"This directory does not seem to contain a castadiva simulation.");
                }
            }
        }
    }

    class generateSimulationsExecutionPlanner implements ActionListener {

        public void actionPerformed(ActionEvent arg0) {
            if (m_exec.getNumberOfRows() != -1) {
                m_model.LoadCastadiva(m_exec.getTargetFolder(0));
                if(!m_model.mobilityModel.equals("RANDOM WAY POINT")) {
                    m_simulationWindow.ChangeMobilityModel(m_model.mobilityModel);
                }
                m_simulationWindow.ChangeRoutingProtocol(m_model.routingProtocol);
                m_simulationWindow.ModifyBlackBoard();
                m_model.executionPlannerSimulating = true;
                m_model.plannerNotEnded = true;
                m_model.pathTarget = m_model.pathScenario;
                m_model.GenerateExecutionPlannerSimulation(m_exec.getTargetFolder(0), m_exec.getRuns(0));

            } else {
                JOptionPane.showMessageDialog(null, "You must configure a simulation first.");
            }
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
}
