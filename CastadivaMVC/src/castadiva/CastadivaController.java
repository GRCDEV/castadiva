package castadiva;

import castadiva.SimulationController.ExecutionPlannerListener;
import castadiva.SimulationController.MobilityDesignerListener;
import castadiva.SimulationController.ProtocolsListener;
import castadiva_gui.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * This class interact with the model and the different GUI to follow the MVC
 * (Model - View - Controller) programming style.
 * This class is the controller, that allow to asociate the logic with an GUI.
 *
 * @author Jorge Hortelano.
 * @since 1.4
 * @version %I%, %G%
 * @see CastadivaModel
 * @see MainMenuGUI
 * @see AboutBox
 * @see SimulationGUI
 * @see TrafficGUI
 * @see APNewGUI
 * @see APModifyGUI
 * @see ComputerGUI
 */
public class CastadivaController {

    private CastadivaModel m_model;
    private MainMenuGUI m_view;
    private AboutBox m_about;
    private SimulationGUI m_simulationWindow;
    private TrafficGUI m_trafficGUI;
    private APNewGUI m_newAP;
    private APModifyGUI m_modifyAP;
    private ComputerGUI m_computerGUI;
    private InstallApGUI m_installAP;
    private CheckSimulationThread checkSimulation;
    private RandomSimulationGUI m_randomSimulation;
    private NewExternalTrafficGUI m_attachTraffic;
    private HelpWindow m_helpWindow;
    private ProtocolsGUI m_protocol;
    private MobilityDesignerGUI mobDes;
    private PluginDetector pluginDet;
    private ExecutionPlanner executionPlanner;
    private SimulationController simulationControl;
    /**
     * Creates a new instance of the controler.
     *
     * @param model The modeler witch the logic of the program.
     * @param view The main window associated (GUI). Contains the menu to access to the
     * others windows.
     * @param about The aboutbox with the author and program information.
     * @param simulationWindow the GUI that allow the user to generate a simulation
     * with the APs.
     * @param traffic This GUI define the packet interaction between APs.
     * @param newAPWindow Another GUI to allow an user to add a new AP.
     * @param modifyAPWindow This GUI allows the user to modify an existing AP.
     * @param computerGUI The viewer to configurate the computer.
     * @param randomSimulation The GUI that allow to generate randomly a simulation.
     * @param attachTraffic The GUI to allow an external traffic to be in Castadiva.
     * @param helpWindow A window to help the user to configurate Castadiva.
     */
    public CastadivaController(CastadivaModel model, MainMenuGUI view, AboutBox about,
            SimulationGUI simulationWindow, TrafficGUI traffic, APNewGUI newAPWindow,
            APModifyGUI modifyAPWindow, ComputerGUI computerGUI, InstallApGUI installAP,
            RandomSimulationGUI randomSimulation, NewExternalTrafficGUI attachTraffic,
            HelpWindow helpWindow, ProtocolsGUI protocolGUI, ExecutionPlannerGUI exec,
            MobilityDesignerGUI mobDesigner, PluginDetector pl) {
        m_model = model;
        m_view = view;
        m_about = about;
        m_simulationWindow = simulationWindow;
        m_trafficGUI = traffic;
        m_newAP = newAPWindow;
        m_modifyAP = modifyAPWindow;
        m_computerGUI = computerGUI;
        m_installAP = installAP;
        m_randomSimulation = randomSimulation;
        m_attachTraffic = attachTraffic;
        m_helpWindow = helpWindow;
        m_protocol = protocolGUI;
        mobDes = mobDesigner;
        pluginDet = pl;
        checkSimulation = new CheckSimulationThread();
        executionPlanner = new ExecutionPlanner(simulationWindow, exec, model, attachTraffic, view, this);
        simulationControl = new SimulationController(simulationWindow, executionPlanner, model, traffic, randomSimulation, checkSimulation, this, protocolGUI, mobDesigner, m_view);

        pluginDet.registerObservers(m_simulationWindow);
        pluginDet.notifyInitialObservers();
        pluginDet.notifyInitialObserversRout();

        m_model.detector = pluginDet;

        MainMenuListenersReady();
        simulationControl.SimulationAllListenersReady();
        RandomSimulationAllListenersReady();
        ConfigurationComputerListenersReady();
        NewApListenerReady();
        ModifyApListenerReady();
        InstallAPListenerReady();
        TrafficListenerReady();
        AttachExternalTrafficListenerReady();
        HelpWindowListenerReady();
        executionPlanner.executionPlannerListenersReady();
        mobilityDesignerListenersReady();
        routingProtocolListenersReady();

        
        checkSimulation.start();
    }

    /**
     * Generate a window to search in the file system.
     * @param mode The kind of window. 
     * @see setFileSelectionMode
     */
    public String ExplorationWindow(String title, int mode) {
        JFrame frame = null;
        JFileChooser fc;


        fc = new JFileChooser(new File(m_model.GetDefaultExplorationFolder()));
        fc.setFileSelectionMode(mode);
        int fcReturn = fc.showDialog(frame, title);
        if (fcReturn == JFileChooser.APPROVE_OPTION) {
            m_model.ChangeDefaultExplorationFolder(fc.getSelectedFile().toString());
            return fc.getSelectedFile().toString();
        }
        return "";
    }

    /**
     * Create a window to show an error message.
     */
    void ShowSaveErrorMessage(String text, String title) {
        JFrame frame = null;
        if (m_model.debug) {
            System.out.println(text);
        }
        JOptionPane.showMessageDialog(frame, text, title, JOptionPane.ERROR_MESSAGE);
    }

    /****************************************************************************
     *
     *                               MAIN MENU
     *
     ****************************************************************************/
    /**
     *  Inserte the listeners of this window
     */
    private void MainMenuListenersReady() {
        m_view.addImportNsListener(new ImportNsListener());
        m_view.addExportNsListener(new ExportNsListener());
        m_view.addExportMayaListener(new ExportMayaListener());
        m_view.addNewListener(new NewListener());
        m_view.addLoadListener(new LoadListener());
        m_view.addLoadScenaryListener(new LoadScenaryListener());
        m_view.addSaveListener(new SaveListener());
        m_view.addSimulationMenuOption(new SimulationMenuOptionListener());
        m_view.addAboutBoxListener(new AboutBoxListener());
        m_view.addNewApOption(new NewApListener());
        m_view.addModifyApOption(new ModifyApListener());
        m_view.addLoadApOption(new LoadApsListener());
        m_view.addSaveApOption(new SaveApsListener());
        m_view.addInstallButtonListener(new InstallApListener());
        m_view.addComputerConfigurationListener(new ComputerConfigurationListener());
        m_view.addRandomSimulationListener(new RandomSimulationMenuOptionListener());
        m_view.addExternalDeviceListener(new AttachExternalDeviceListener());
        m_view.addHelpWindowListener(new HelpWindowListener());
        m_view.addProtocolsListener(simulationControl.new ProtocolsListener());
        m_view.addExecutionPlannerListener(simulationControl.new ExecutionPlannerListener());
        m_view.addMobilityDesignerListener(simulationControl.new MobilityDesignerListener());
        //citymob
        m_view.addImportNsCitymob(new ImportCitymobListener());
    }

    void setDefaultSimulationControllers() {
        simulationControl.SimulationAllListenersReady();
    }

    class ImportNsListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String file;
            Integer size;

            if (!(file = ExplorationWindow("Import", JFileChooser.FILES_AND_DIRECTORIES)).equals("")) {
                m_model.Reset();
                if (!m_model.ImportNs(file)) {
                    ShowSaveErrorMessage("File format error, its is not a NS file!",
                            "NS Import error...");
                }
            }
            m_simulationWindow.EnableReplay(true);
            m_simulationWindow.FillFields();
            m_simulationWindow.ModifyBlackBoard();
            size = (int) Math.max(m_model.GetBoundX(), m_model.GetBoundY());
            m_simulationWindow.DrawNewSize(size);
        }
    }

    class ExportNsListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String file;
            if (!(file = ExplorationWindow("Export scenario",
                    JFileChooser.FILES_AND_DIRECTORIES)).equals("")) {
                m_model.ExportNsScenario(file, m_model.HowManyAP());
                if (m_model.accessPoints.GetTrafficSize() > 0) {
                    if (!(file = ExplorationWindow("Export traffic",
                            JFileChooser.FILES_AND_DIRECTORIES)).equals("")) {
                        m_model.ExportNsTraffic(file);
                    }
                }
            }
        }
    }

    class ExportMayaListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String file;
            if (!(file = ExplorationWindow("Export",
                    JFileChooser.FILES_AND_DIRECTORIES)).equals("")) {
                m_model.ExportMaya(file);
            }
        }
    }

    class NewListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.Reset();
            m_simulationWindow.SetSimulationTime(0);
            m_simulationWindow.EnableReplay(false);
            m_simulationWindow.DisableSimulation();
            m_simulationWindow.ModifyBlackBoard();
        }
    }

    class LoadListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String file;
            Integer size;
            if (!(file = ExplorationWindow("Load",
                    JFileChooser.DIRECTORIES_ONLY)).equals("")) {
                m_model.LoadCastadiva(file);
                if(!m_model.mobilityModel.equals("RANDOM WAY POINT")) {
                    m_simulationWindow.ChangeMobilityModel(m_model.mobilityModel);
                }
                m_simulationWindow.ChangeRoutingProtocol(m_model.routingProtocol);
            }
            if (m_model.ExistsOldMobility()) {
                m_simulationWindow.EnableReplay(true);
            }
            m_simulationWindow.ModifyBlackBoard();
            size = (int) Math.max(m_model.GetBoundX(), m_model.GetBoundY());
            m_simulationWindow.DrawNewSize(size);
            m_attachTraffic.UpdateWindow();
        }
    }

    class LoadScenaryListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String file;
            if (!(file = ExplorationWindow("Load",
                    JFileChooser.FILES_AND_DIRECTORIES)).equals("")) {
                m_model.Reset();
                m_model.LoadAP(file);
            }
        }
    }

    class SaveListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String file;
            if (!(file = ExplorationWindow("Save",
                    JFileChooser.DIRECTORIES_ONLY)).equals("")) {
                if (!m_model.SaveCastadiva(file)) {
                    ShowSaveErrorMessage(
                                "Directory not created! check your user read/write permission",
                            "Save error...");
                }
            }
        }
    }

    class RandomSimulationMenuOptionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_view.setVisible(false);
            m_model.randomTrafficModel.UpdateData(m_model.accessPoints.GetRandomTraffic());
            m_randomSimulation.UpdateTable();
            m_randomSimulation.setVisible(true);
        }
    }

    class SimulationMenuOptionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_simulationWindow.FillAPComboBox();
            m_simulationWindow.ActivateButtons(true);
            m_view.setVisible(false);
            m_simulationWindow.setVisible(true);
            m_simulationWindow.FillFields();
        }
    }

    class AboutBoxListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_about.UpdateText(m_model.ReadTextFile(m_model.Help_Folder + File.separator + "about.txt"));
            m_about.setVisible(true);
        }
    }

    class HelpWindowListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.selectedHelp = m_model.MAINMENU_INDEX;
            ShowHelp();
        }
    }

    class ComputerConfigurationListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_computerGUI.GUIReady();
            m_computerGUI.setVisible(true);
        }
    }

    class NewApListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_newAP.DefaultWindow();
            m_newAP.setVisible(true);
        }
    }

    class ModifyApListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_modifyAP.GUIReady();
            m_modifyAP.setVisible(true);
        }
    }

    class LoadApsListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JFileChooser load = new JFileChooser();
            int selection = load.showOpenDialog(load);

            if(selection == JFileChooser.APPROVE_OPTION) {
                File file = load.getSelectedFile();
                m_model.LoadApsFromFile(file);
                file = null;
            }
        }
    }

    class SaveApsListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JFileChooser save = new JFileChooser();
            int selection = save.showSaveDialog(save);

            if(selection == JFileChooser.APPROVE_OPTION) {
                File file = save.getSelectedFile();
                m_model.SaveApsToFile(file);
                file = null;
            }
        }
    }

    class AttachExternalDeviceListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_attachTraffic.UpdateWindow();
            m_attachTraffic.setVisible(true);
        }
    }

    class ImportCitymobListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String file;
            Integer size;

            if (!(file = ExplorationWindow("Import", JFileChooser.FILES_AND_DIRECTORIES)).equals("")) {
                m_model.Reset();
                if (!m_model.ImportNsCITYMOB(file)) {
                    ShowSaveErrorMessage("File format error, its is not a NS file!",
                            "NS Import error...");
                }
            }
            m_simulationWindow.EnableReplay(true);
            m_simulationWindow.FillFields();
            m_simulationWindow.ModifyBlackBoard();
            size = (int) Math.max(m_model.GetBoundX(), m_model.GetBoundY());
            m_simulationWindow.DrawNewSize(size);
            m_simulationWindow.activarCityMob();
        }
    }

    /****************************************************************************
     *
     *                            RANDOM SIMULATION
     *
     ****************************************************************************/
    /**
     *  Inserte the listeners of this window
     */
    private void RandomSimulationAllListenersReady() {
        m_randomSimulation.addCloseButtonListener(new RandomSimulationCloseButtonListener());
        m_randomSimulation.addSaveFolderActionListener(new SaveRandomSimulation());
        m_randomSimulation.addGenerateActionListener(new GenerateRandomSimulation());
        m_randomSimulation.addDelRowButtonListener(new RandomTrafficDeleteElement());
        m_randomSimulation.addClearButtonListener(new RandomTrafficClear());
        m_randomSimulation.addDuplicateActionListener(new DuplicateRandomTrafficRow());
        m_randomSimulation.addOrderActionListener(new OrderRandomTraffic());
        m_randomSimulation.addXBoundListener(new RandomXBoundListener());
        m_randomSimulation.addYBoundListener(new RandomYBoundListener());
        m_randomSimulation.addSimulationTimeTextFieldListener(new RandomSimulationTimeListener());
    }

    class SaveRandomSimulation implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String file;
            if (!(file = ExplorationWindow("Save",
                    JFileChooser.DIRECTORIES_ONLY)).equals("")) {
                m_randomSimulation.ChangeSaveFolderText(file);
            }
        }
    }

    private void GetRandomData() {
        m_model.SetBoundX(m_randomSimulation.ReturnXSize());
        m_model.SetBoundY(m_randomSimulation.ReturnYSize());
        m_model.protocolSelectedRandomSimulation = m_randomSimulation.GetProtocolsSelected();
        m_model.SetSimulationPause(m_randomSimulation.ReturnPauseTime());
        m_model.SetMobilityMaxSpeed(m_randomSimulation.ReturnSpeed());
        m_model.SetMobilityMinSpeed(0);
        m_model.SetSimulationTime(m_randomSimulation.ReturnSimulationTime());
        m_simulationWindow.SetSimulationTime(m_randomSimulation.ReturnSimulationTime());
        m_model.isRandomTraffic = m_randomSimulation.IsTrafficRandom();
    }

    class GenerateRandomSimulation implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            GetRandomData();
            m_randomSimulation.StartSimulation();
            m_model.randomSimulating = true;
            m_model.randomNotEnded = true;
            m_model.simulationSeconds = 0;
            m_model.CalculateRandomRealSimulationTime();
            Float max = Math.max(m_randomSimulation.ReturnXSize(),
                    m_randomSimulation.ReturnYSize());
            m_model.ChangeBoardSize(max.intValue());

            m_model.OrderRandomTrafficVector(m_model.accessPoints.GetRandomTraffic());
            m_randomSimulation.UpdateTable();
            m_model.fileRandomScenaryFormat = m_randomSimulation.GetFormat();
            m_randomSimulation.ModifyBlackBoard();
            m_model.StartIterationCount();
            m_model.GenerateRandomSimulation(m_randomSimulation.GetSaveFolderText(),
                    m_randomSimulation.GetRuns());
        }
    }

    class RandomSimulationCloseButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_view.setVisible(true);
            m_randomSimulation.setVisible(false);
        }
    }

    class RandomXBoundListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.SetBoundX(m_randomSimulation.ReturnXSize());
            m_randomSimulation.ModifyBlackBoard();
        }
    }

    class RandomYBoundListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.SetBoundY(m_randomSimulation.ReturnYSize());
            m_randomSimulation.ModifyBlackBoard();
        }
    }

    class RandomSimulationTimeListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            int st = m_randomSimulation.ReturnSimulationTime();
            m_model.SetSimulationTime(st);
            m_simulationWindow.SetSimulationTime(st);
        }
    }

    class RandomTrafficDeleteElement implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            Vector newVector = new Vector();
            int row = m_randomSimulation.GetSelectedRow();
            if (row > -1) {
                m_model.VectorCopyTo(m_model.accessPoints.GetRandomTraffic(),
                        newVector, m_model.accessPoints.GetRandomTrafficSize());
                try {
                    newVector.remove(row);
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                //m_model.accessPoints.DelRandomTrafficRow(row);
                m_model.UpdateRandomTraffic(newVector);
                m_randomSimulation.UpdateTable();
            }
        }
    }

    class RandomTrafficClear implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.UpdateRandomTraffic(new Vector());
            m_randomSimulation.GenerateTable();
        }
    }

    class DuplicateRandomTrafficRow implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.DuplicateRandomTrafficRow(m_model.accessPoints.GetRandomTraffic(),
                    m_randomSimulation.GetSelectedRow());
            m_randomSimulation.UpdateTable();
        }
    }

    class OrderRandomTraffic implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.OrderRandomTrafficVector(m_model.accessPoints.GetRandomTraffic());
            m_randomSimulation.UpdateTable();
        }
    }

    /****************************************************************************
     *
     *                            INSTALL AP
     *
     ****************************************************************************/
    /**
     *  Inserte the listeners of this window
     */
    private void InstallAPListenerReady() {
        m_installAP.addCloseButtonListener(new InstallCloseButton());
        m_installAP.addInstallButtonListener(new InstallApButton());
    }

    class InstallCloseButton implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_installAP.setVisible(false);
        }
    }

    class InstallApButton implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.InstallAp(m_installAP.WhatEthDevice(),
                    m_installAP.WhatWifiDevice(),
                    m_installAP.WhatSwitchDevice(), m_installAP.WhatBridgeDevice(),
                    m_installAP.WhatEthIp(), m_installAP.WhatWifiIp(),
                    m_installAP.WhatGatewayIp(), m_installAP.WhatComputerFolder(),
                    m_installAP.WhatApNfsFolder(), m_installAP.WhatApScriptFolder(),
                    m_installAP.WhatSshUser(), m_installAP.WhatSshPwd(),
                    m_installAP.WhatCurrentIp());
            m_installAP.ShowEndInstallationMessage();
        }
    }

    /****************************************************************************
     *
     *                                NEW AP
     *
     ****************************************************************************/
    /**
     *  Inserte the listeners of this window
     */
    private void NewApListenerReady() {
        m_newAP.addPingButtonListener(new NewPingListener());
        m_newAP.addOkButtonListener(new CreateApListener());
        m_newAP.addInstallButtonListener(new InstallApListener());
        m_newAP.addHelpButtonListener(new HelpNewApListener());
    }

    class NewPingListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            new PingGUI(m_newAP.GiveMeTheIp()).setVisible(true);
        }
    }

    class CreateApListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.AddAP(m_newAP.GiveMeTheIp(), m_newAP.GiveMeTheWifiIp(),
                    m_newAP.GiveMeTheWifiMac(), m_newAP.GiveMeTheSshUser(),
                    m_newAP.GiveMeTheSshPwd(), m_newAP.GiveMeTheId(),
                    200, 200, 0, m_newAP.GiveMeTheWorkingDirectory(),
                    m_newAP.GiveMeTheProcessor(), m_newAP.GiveMeTheChannel(),
                    m_newAP.GiveMeTheMode(), m_newAP.GiveMeWifiDevice(),
                    m_newAP.GiveMeGW());
            m_newAP.dispose();
        }
    }

    class InstallApListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_installAP.FillComponents(m_newAP.GiveMeTheIp(),
                    m_newAP.GiveMeTheWifiIp(), m_newAP.GiveMeTheWorkingDirectory(),
                    m_newAP.GiveMeTheSshUser(), m_newAP.GiveMeTheSshPwd());
            m_newAP.setVisible(false);
            m_installAP.setVisible(true);
        }
    }

    class HelpNewApListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.selectedHelp = m_model.ADD_NODE_INDEX;
            ShowHelp();
        }
    }

    /****************************************************************************
     *
     *                              MODIFY AP
     *
     ****************************************************************************/
    /**
     *  Inserte the listeners of this window
     */
    private void ModifyApListenerReady() {
        m_modifyAP.addPingButtonListener(new ModifyPingListener());
        m_modifyAP.addOkButtonListener(new ChangeApListener());
        m_modifyAP.addDeleteButtonListener(new DeleteApListener());
    }

    class ModifyPingListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            new PingGUI(m_modifyAP.GiveMeTheIp()).setVisible(true);
        }
    }

    class ChangeApListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (m_modifyAP.IsWindowEditable()) {
                m_model.SetAP(m_modifyAP.GiveMeTheSelectedIndex(), m_modifyAP.GiveMeTheIp(),
                        m_modifyAP.GiveMeTheWifiIp(), m_modifyAP.GiveMeTheWifiMac(),
                        m_modifyAP.GiveMeTheSshUser(), m_modifyAP.GiveMeTheSshPwd(),
                        m_modifyAP.GiveMeTheId(), m_model.selectionedAP1.x,
                        m_model.selectionedAP1.y, m_model.selectionedAP1.z,
                        m_model.selectionedAP1.range, m_modifyAP.GiveMeTheWorkingDirectory(),
                        m_modifyAP.GiveMeTheProcessor(), m_modifyAP.GiveMeTheChannel(),
                        m_modifyAP.GiveMeTheMode(), m_modifyAP.GiveMeWifiDevice(), m_modifyAP.GiveMeGW());
                m_modifyAP.ShowOkDialog();
            }
        }
    }

    class DeleteApListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.RemoveAP(m_modifyAP.GiveMeTheSelectedIndex());
            m_modifyAP.GUIReady();
        }
    }

    /****************************************************************************
     *
     *                             MODIFY COMPUTER
     *
     ****************************************************************************/
    /**
     *  Inserte the listeners of this window
     */
    private void ConfigurationComputerListenersReady() {
        m_computerGUI.addCloseButtonListener(new ModifyComputerListener());
    }

    class ModifyComputerListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.SetComputerInterface(m_computerGUI.ReturnSelectedInterface());
            m_model.SetComputerWorkingDirectory(m_computerGUI.ReturnTheWorkingDirectory());
            m_computerGUI.setVisible(false);
        }
    }

    /****************************************************************************
     *
     *                             TRAFFIC
     *
     ****************************************************************************/
    /**
     *  Inserte the listeners of this window
     */
    private void TrafficListenerReady() {
        m_trafficGUI.addDelRowButtonListener(new TrafficDeleteElement());
        m_trafficGUI.addClearButtonListener(new TrafficClear());
        m_trafficGUI.addAcceptButtonListener(new TrafficAccept());
        m_trafficGUI.addSaveInTextFileButtonListener(new SaveTrafficInTextFile());
        m_trafficGUI.addDuplicateActionListener(new DuplicateTrafficRow());
        m_trafficGUI.addOrderButtonListener(new OrderTraffic());
    }

    class TrafficDeleteElement implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            Vector newVector = new Vector();
            int row = m_trafficGUI.GetSelectedRow();
            if (row > -1) {
                m_model.VectorCopyTo(m_model.accessPoints.GetTraffic(),
                        newVector, m_model.accessPoints.GetTrafficSize());
                try {
                    newVector.remove(row);
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                //m_model.accessPoints.DelTrafficRow(row);
                m_model.UpdateTraffic(newVector);
                m_trafficGUI.UpdateTable();
            }
        }
    }

    class DuplicateTrafficRow implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.DuplicateTrafficRow(m_model.accessPoints.GetTraffic(), m_trafficGUI.GetSelectedRow());
            m_trafficGUI.UpdateTable();
        }
    }

    class OrderTraffic implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.OrderTrafficVector(m_model.accessPoints.GetTraffic());
            m_trafficGUI.UpdateTable();
        }
    }

    class SaveTrafficInTextFile implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String file;
            if (!(file = ExplorationWindow("Save text file",
                    JFileChooser.FILES_AND_DIRECTORIES)).equals("")) {
                m_model.PrintTraffic(file);
            }
        }
    }

    class TrafficAccept implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            int row;
            if ((row = m_model.TrafficIdenticalSourceDestination()) > 0) {
                m_trafficGUI.ShowError("You have defined a identical source and adress" +
                        " at row " + row, "Traffic problem");
            } else {
                m_model.OrderTrafficVector(m_model.accessPoints.GetTraffic());
                m_trafficGUI.UpdateTable();
                m_model.CalculateRealSimulationTime();
                m_trafficGUI.setVisible(false);
            }
        }
    }

    class TrafficClear implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.UpdateTraffic(new Vector());
            m_trafficGUI.GenerateTable(m_model.accessPoints.GetTraffic());
        }
    }

    /****************************************************************************
     *
     *                              EXTERNAL TRAFFIC
     *
     ****************************************************************************/
    /**
     *  Inserte the listeners of this window
     */
    private void AttachExternalTrafficListenerReady() {
        m_attachTraffic.addAttachButtonListener(new AcceptAttach());
        m_attachTraffic.addHelpButtonListener(new HelpAttach());
        m_attachTraffic.addCloseButtonListener(new CloseAttach());
        m_attachTraffic.addDeleteButtonListener(new DeleteAttach());
    }

    class AcceptAttach implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_attachTraffic.AttachTraffic();
            m_attachTraffic.ClearInputText();
        }
    }

    class HelpAttach implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.selectedHelp = m_model.ATTACH_INDEX;
            ShowHelp();
        }
    }

    class CloseAttach implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_attachTraffic.setVisible(false);
        }
    }

    class DeleteAttach implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.DeleteExternalTrafficInstruction(m_attachTraffic.ReturnSelectedExternalTrafficFlow());
            m_attachTraffic.UpdateAfterDelete();
        }
    }

    /****************************************************************************
     *
     *                              HELP WINDOW
     *
     ****************************************************************************/
    /**
     *  Inserte the listeners of this window
     */
    private void HelpWindowListenerReady() {
        m_helpWindow.addAfterButtonListener(new AfterHelp());
        m_helpWindow.addBeforeButtonListener(new BeforeHelp());
        m_helpWindow.addLanguageComboBoxListener(new LanguageHelp());
    }

    /**
     * Shows the help window eith the selected index that represents a help file.
     */
    public void ShowHelp() {
        m_helpWindow.UpdateText(m_model.ReadTextFile(m_model.Help_Folder + File.separator + m_model.helpFiles[m_model.selectedHelp]));
        m_helpWindow.setVisible(true);
    }

    /**
     * Select next help page.
     */
    class AfterHelp implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.selectedHelp++;
            if (m_model.selectedHelp > m_model.helpFiles.length - 1) {
                m_model.selectedHelp = 0;
            }
            ShowHelp();
        }
    }

    /**
     * Select previous help page.
     */
    class BeforeHelp implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.selectedHelp--;
            if (m_model.selectedHelp < 0) {
                m_model.selectedHelp = m_model.helpFiles.length - 1;
            }
            ShowHelp();
        }
    }

    /**
     * Change the text language.
     */
    class LanguageHelp implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            switch (m_helpWindow.GetLanguageIndex()) {
                case 0:
                    m_model.Help_Folder = "helpEN";
                    break;
                case 1:
                    m_model.Help_Folder = "helpES";
                    break;
            }
            m_helpWindow.UpdateText(m_model.ReadTextFile(m_model.Help_Folder + File.separator + m_model.helpFiles[m_model.selectedHelp]));
        }
    }

     /****************************************************************************
     *  *                 PROTOCOL PLUG-INS
     *
     ****************************************************************************/

    /**
     * Action performed when the "Create plugin" button is pushed in the "configure
     * protocols" window.
     */
    void routingProtocolListenersReady() {
        m_protocol.addCreatePluginListener(new createRoutingPluginListener());
    }

    /**
     * According to the information given in the "configure protocols" interface,
     * the following class allows to create and compile a new plugin. Plugins are
     * compiled as .jar files. Later on, they can be loaded using the
     * ServiceLoader class.
     * @author nacho wannes
     * @see ServiceLoader
     */
    class createRoutingPluginListener implements ActionListener {
        String binaryFilePath = m_protocol.getBinaryFilePath();
       
        String configurationFilePath; 
        String jarFileName;
        String protocolConfiguration;
        String protocolFlags;
        
        String[] configurationFileName;
        String[] binaryFileName;

        public void actionPerformed(ActionEvent arg0) {
            // Getting information from the GUI 
            binaryFilePath = m_protocol.getBinaryFilePath();
            configurationFilePath = m_protocol.getConfigurationFilePath();
            jarFileName = m_protocol.getJarFileName();
            protocolConfiguration = m_protocol.getProtocolConfiguration();
            protocolFlags = m_protocol.getProtocolFlags();

            // The last part of binaryFilePath is considered as the configuration filename
            configurationFileName = binaryFilePath.split("[" + File.separatorChar + "]");
            // The last part of configurationFilePath is considered as the bin filename
            binaryFileName = configurationFilePath.split("[" + File.separatorChar + "]");

            // Checking if all requested information has been provided
            if (m_protocol.getJarFileName().length() < 1) {
                JOptionPane.showMessageDialog(new JFrame(), "The plugin needs a name");
            }
            else if (m_protocol.getProtocolConfiguration().length() <= 1) {
                JOptionPane.showMessageDialog(new JFrame(), "Empty configuration protocol content");
            }
            else if (m_protocol.getConfigurationFilePath().length() < 1 || binaryFileName.length < 1) {
                JOptionPane.showMessageDialog(new JFrame(), "Empty configuration protocol path");
            }
            // The configuration file name must contain at least one File.separatorChar character
            else if (configurationFileName.length >= 2) {
                int answer = JOptionPane.showConfirmDialog(new JFrame(), "Do you want to save your changes?", "Save Changes?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
                if (answer == JOptionPane.OK_OPTION) {
                    compileJarFile();
                    createJars();
                    // The plugin detector must be warned about the creation of
                    // a new plugin
                    pluginDet.notifyObserversRout(jarFileName);
                }
            }
        }

        /**
         * The following creates a .java file with the gathered protocol informations.
         * The .java file is a class that implements IPluginsCastadiva
         * The .java file is placed into a work folder and will later on be used
         * by the createJars fuction.
         * @author nacho wannes
         * @see createJars
         */
        private void compileJarFile() {

            String javaFileName = jarFileName + ".java";

            try {
                // A temporary folder is created to generate the .java file
                File pluginWorkDirectory = new File(CastadivaModel.PLUGIN_WORKFOLDER);
                pluginWorkDirectory.mkdirs();

                /* Wannes : It is not possible to write conf in the .java
                 * file when it is written over several lines and/or with special characters.
                 * Therefore, the conf file must be written as a file
                 * and later on, included in the jar. The getConfContent can
                 * then return the content of the file
                 */

                File pluginsDir = new File(pluginWorkDirectory.getPath()+"/castadiva/Plugins");
                pluginsDir.mkdirs();

                BufferedWriter bout = new BufferedWriter(new FileWriter(pluginWorkDirectory.getPath()+"/castadiva/Plugins/"+ javaFileName));
                bout.write("package castadiva.Plugins;\n");
                bout.write("import java.io.BufferedReader;\n");
                bout.write("import java.io.FileReader;\n");
                bout.write("import lib.IPluginCastadiva;\n");
                bout.write("public class " + jarFileName + " implements IPluginCastadiva {\n");
                //metodos
                bout.write("    public String getBin() {\n");
                bout.write("        return \"" + binaryFilePath + "\"; \n    }\n");
                bout.write("    public String getFlags() {\n");
                bout.write("        return \"" + protocolFlags + "\"; \n    }\n");
                bout.write("    public String getPathConf() {\n");
                bout.write("        return \"" + configurationFilePath + "\"; \n    }\n");
                bout.write("    public String getConfContent(){\n");
                bout.write("        BufferedReader confFileReader;\n");
                bout.write("        try {\n");
                bout.write("            confFileReader = new BufferedReader(new FileReader(\"src/castadiva/Plugins/"+jarFileName+".conf\"));\n");
                bout.write("            String confFile = \"\";\n");
                bout.write("            String confFileLine;\n");
                bout.write("            while((confFileLine = confFileReader.readLine()) != null){\n");
                bout.write("                 confFile+=\"\\n\"+confFileLine;\n");
                bout.write("            }\n");
                bout.write("            return(confFile);\n");
                bout.write("        } catch (Exception ex) {\n");
                bout.write("            System.out.println(ex);\n");
                bout.write("        }\n");
                bout.write("        return(null);\n");
                bout.write("    }\n");
                bout.write("    public String getKillInstruction() {\n");
                bout.write("        return  \"killall " + binaryFileName[binaryFileName.length - 1] + " 2>/dev/null\"" + ";\n    }\n}");
                bout.close();

                // The configuration is saved in a .conf file directly in the final jar directory
                BufferedWriter confFileWriter = new BufferedWriter(new FileWriter(CastadivaModel.PLUGIN_JAR_FOLDER+"/"+jarFileName+".conf"));
                confFileWriter.write(protocolConfiguration);
                confFileWriter.close();
                                
            } catch (IOException ex) {
                Logger.getLogger(ProtocolsGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /**
         * CreateJars() first compiles the .java class that was previously
         * created in compileJarFile. It then packages it as a .jar file which
         * can later on be used by pluginLoader with the ServiceLoader system
         * @author nacho wannes
         * @see compileJarFile, ServiceLoader
         */
        private void createJars() {
            try {
                // We first have to make a few folders to organise the jar file.
                File libDir = new File(CastadivaModel.PLUGIN_WORKFOLDER+"/lib/");
                libDir.mkdirs();
                File metaDir = new File(CastadivaModel.PLUGIN_WORKFOLDER+"/META-INF/");
                metaDir.mkdirs();
                File servicesDir = new File(CastadivaModel.PLUGIN_WORKFOLDER+"/META-INF/services/");
                servicesDir.mkdirs();
                File pluginsDir = new File(CastadivaModel.PLUGIN_WORKFOLDER+"/castadiva/Plugins");

                BufferedWriter bout2 = new BufferedWriter(new FileWriter(servicesDir.getPath()+"/lib.IPluginCastadiva"));
                bout2.write("castadiva.Plugins." + jarFileName);
                bout2.close();
                Runtime rt = null;
                try {
                    rt = Runtime.getRuntime();

                    Process p = rt.exec("javac "+pluginsDir.getPath()+"/" + jarFileName + ".java "+CastadivaModel.PLUGIN_INCLUDE_FOLDER+"/IPluginCastadiva.java");
                    p.waitFor(); //esperamos a que termine el proceso externo

                    // The file is moved to be inserted into the package
                    Runtime copyLib = Runtime.getRuntime();
                    Process copyLibProcess = copyLib.exec("mv "+CastadivaModel.PLUGIN_INCLUDE_FOLDER+"/IPluginCastadiva.class "+libDir.getPath()+"/IPluginCastadiva.class");
                    copyLibProcess.waitFor();
                } catch (Exception e) {
                    System.out.println(e);
                }
                try {
                    // Finaly the jar file is packaged in the final plugin directory
                    Process p2 = rt.exec("jar cf "+CastadivaModel.PLUGIN_JAR_FOLDER+"/" + jarFileName + ".jar "+pluginsDir.getPath()+"/"+jarFileName+".class "+libDir.getPath()+" "+metaDir.getPath()+")");
                    p2.waitFor();
                } catch (Exception e1) {
                }
                JOptionPane.showMessageDialog(new JFrame(), "Plugin sucessfully created");
                
                // The temporary folder is cleaned as well as the temporary files
                File workFolder = new File(CastadivaModel.PLUGIN_WORKFOLDER);
                deleteDirectory(workFolder);

                // The protocol configuration window is closed
                m_protocol.dispose();
            } catch (IOException ex) {
                Logger.getLogger(ProtocolsGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        /*
         * @author Wannes
         * Allows to recursively delete a directory with its content
         * @param path points to the folder to be deleted
         * @return true if the deletion was sucessfull
         * @see File
         */
        public void deleteDirectory(File path) {
            if( path.exists() ) {
                File[] files = path.listFiles();
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
    }

  
   

    /****************************************************************************
     *  *                 MOBILITY DESIGNER
     *
     ****************************************************************************/
    void mobilityDesignerListenersReady() {
        mobDes.addGeneratePluginListener(new generatePluginListener());

    }

    class generatePluginListener implements ActionListener {

        public void actionPerformed(ActionEvent arg0) {
            //First we create Movement.java, the file that includes the new code
            Vector<String> movement = new Vector<String>();
            String name = "";

            name = mobDes.getMobName();

            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader("castadiva/Movement2.java"));
                String line;

                while ((line = br.readLine()) != null) {

                    if (line.trim().equals("#")) {
                       movement.add(mobDes.getCode());
                    } else {
                        movement.add(line);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(CastadivaController.class.getName()).log(Level.SEVERE, null, ex);
            }


            BufferedWriter bout = null;
            try {
                bout = new BufferedWriter(new FileWriter("castadiva/Movement.java"));

                for(int i = 0; i < movement.size(); ++i)
                    bout.write(movement.get(i) + "\n");

                bout.close();

            } catch (IOException ex) {
                Logger.getLogger(CastadivaController.class.getName()).log(Level.SEVERE, null, ex);
            }

            Vector<String> newMain = loadNewMainForThePLugin(name);

            //Before compiling we have to create the main .java for the plugin and modify METAINF file
            BufferedWriter bufout = null;
            try {
                bufout = new BufferedWriter(new FileWriter("castadiva/Plugins/Mobility/" + name + ".java"));

                for(int i = 0; i < newMain.size(); ++i)
                    bufout.write(newMain.get(i) + "\n");

                bufout.close();
                bufout = new BufferedWriter(new FileWriter("META-INF/services/lib.IMobilityPluginCastadiva"));
                bufout.write("castadiva.Plugins.Mobility." + name);

                bufout.close();

            } catch (IOException ex) {
                Logger.getLogger(CastadivaController.class.getName()).log(Level.SEVERE, null, ex);
            }



            //Now we need to compile and to create the jar file
            Runtime rt = null;
            try {
                //a continuacion lanzamos los procesos correspondientes a la compilacion y a la encapsulacion del JAR
                rt = Runtime.getRuntime();

                //Process p = rt.exec("javac mobilityDesigns/castadiva/Plugins/Mobility/" + mobDes.getName() + ".java lib/IPluginCastadiva.java");
                Process p = rt.exec("javac lib/IMobilityPluginCastadiva.java castadiva/AP.java " +
                        "castadiva/APs.java castadiva/Movement.java " +
                        "castadiva/NodeCheckPoint.java castadiva/Plugins/Mobility/" + name  + ".java");

                p.waitFor(); //esperamos a que termine el proceso externo

                InputStream is = p.getErrorStream();
                BufferedReader br2 = new BufferedReader(new InputStreamReader(is));
                if (br2.readLine() != null) {  //if it contains errors
                    ErrorsGUI e = new ErrorsGUI();
                    e.showErrors(br2);
                } else {
                    try {
                        Process p2 = rt.exec("jar cf src/castadiva/MobilityPlugins/" + name + ".jar " +
                                "castadiva lib META-INF");
                        p2.waitFor();//"mobilityDesigns/


                        InputStream ins = p.getErrorStream();
                        BufferedReader br3 = new BufferedReader(new InputStreamReader(ins));
                        if (br3.readLine() != null) {
                            ErrorsGUI e = new ErrorsGUI();
                            e.showErrors(new BufferedReader(new InputStreamReader(p2.getErrorStream())));
                        } else {
                            JOptionPane.showMessageDialog(new JFrame(), "Plugin created correctly");
                            pluginDet.notifyObserversMob(name);
                            mobDes.setVisible(false);
                            m_view.setVisible(true);

                        }

                    } catch (Exception e1) {
                        System.out.println(e1);
                    }
                //JOptionPane.showMessageDialog(this, "Plugin created correctly");
                }

            } catch (Exception e) {
            }/*
        try {
        Process p2 = rt.exec("jar cf src/castadiva/Plugins/" + nombreJar + ".jar castadiva/Plugins/" + nombreJar + ".class lib META-INF");
            p2.waitFor();
            }catch(Exception e1) {}
            JOptionPane.showMessageDialog(this, "Plugin created correctly");*/

        }
    }

    private Vector<String> loadNewMainForThePLugin(String name) {
        Vector<String> file = new Vector<String>();

        file.add("package castadiva.Plugins.Mobility; \n");
        file.add("import castadiva.APs; \n");
        file.add("import castadiva.Movement; \n");
        file.add("import castadiva.NodeCheckPoint; \n");
        file.add("import lib.IMobilityPluginCastadiva;    \n");
        file.add("/**    \n");
        file.add(" * @author nacho    \n");
        file.add(" */    \n");
        file.add("public class " + name + "  implements IMobilityPluginCastadiva {    \n");
        file.add("public void ObtainNodePositionsForEntireSimulation(NodeCheckPoint[][] nodes, APs accessPoints, Float minSpeed, Float maxSpeed, Float simulationPause, int totalTime,  float X, float Y) {    \n");
        file.add("Movement P = new Movement(minSpeed, maxSpeed, simulationPause, totalTime, X, Y);    \n");
        file.add("P.ObtainNodePositionsForEntireSimulation(accessPoints, nodes); \n");
        file.add(" }   \n  }  \n");


        return file;
    }

  

    /****************************************************************************
     *
     *                             TIMES
     *
     ****************************************************************************/
    /**
     *  Inserte the listeners of this window
     */
    public void EndRandomSimulation() {
        //If is a random simulation, save the statistics and make
        //a new one.        
        m_model.SaveRandomSimulation(m_model.savePath);
        m_model.NextIteration(m_randomSimulation.GetRuns());
        if (!m_model.IsEndOfRandomSimulation()) {
            m_model.GenerateRandomSimulation(m_randomSimulation.GetSaveFolderText(),
                    m_randomSimulation.GetRuns());
            m_randomSimulation.ModifyBlackBoard();
        } else {
            if (m_model.debug) {
                System.out.println("---------------------------------");
                System.out.println("       END RANDOM SIMULATION     ");
                System.out.println("---------------------------------");
            }
            m_model.StatisticsAreShowed();
            m_randomSimulation.EndSimulation();
            m_model.randomSimulating = false;
            m_simulationWindow.ChangeReplayTime(m_model.GetSimulationTime());
            m_simulationWindow.EnableReplay(true);
            m_model.EndStopwatch();
            m_model.randomNotEnded = false;
        }
    }

    public void EndSimulation() {
        m_simulationWindow.ShowEndSimulation();
        m_trafficGUI.FillAveragePanel(m_model.udpAverage, m_model.throughputAverage);
        if (!m_model.IsStatisticsAlreadyShowed()) {
            m_trafficGUI.ActivateWindow();
            m_randomSimulation.ActivateWindow();
            m_model.StatisticsAreShowed();
        }
    }

    public void EachInstructionForEachSimulationSteap() {
        if(!m_model.executionPlannerSimulating){
        m_simulationWindow.ConsoleText();
        m_simulationWindow.StopwatchText();
        }
        else
        // The following code allows to update the execution planner's status
        {
            executionPlanner.setStatus(executionPlanner.getCurrentlySimulatingRow());
        }
    }

    /**
     * This thread control the simulation time and wath if all routers end 
     * their operations. 
     */
    class CheckSimulationThread extends Thread {

        public CheckSimulationThread() {
        }

        public void PaintNodeMovement(Integer second) {
            if (m_model.MobiliyActivated()) {
                m_model.PositionateNodesInDeterminedSecond(second);
                if (!m_model.randomSimulating) {
                    m_simulationWindow.ChangeReplayTime(m_model.simulationSeconds);
                    m_simulationWindow.UpdatePositionPanel();
                    m_simulationWindow.Repaint();
                } else {
                    m_randomSimulation.ChangeCurrentTime(m_model.simulationSeconds);
                    m_randomSimulation.Repaint();
                }
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    EachInstructionForEachSimulationSteap();
                    if (m_model.IsStatisticsEnded()) {
                        m_model.ObtainAverageTraffic();
                        //If is a defined simulation, show the statistics.
                        m_model.simulationSeconds = 0;
                        if (!m_model.randomSimulating) {
                            if (!m_model.executionPlannerSimulating) {
                                EndSimulation();
                            } else {
                                executionPlanner.EndExecutionPlannerSimulation();
                            }
                        } else {
                            if (m_model.randomNotEnded) {
                                //if(m_model.executionPlannerSimulating)
                                //EndExecutionPlannerSimulation();
                                //else 
                                EndRandomSimulation();
                            }
                        }
                    }
                    sleep(1000);
                    if (!m_model.replay) {
                        m_randomSimulation.ChangeCurrentTime(m_model.simulationSeconds);
                        if (m_model.WhatStopwatch() >= m_model.GetWaitingSimulationTime() &&
                                m_model.simulationSeconds < m_model.GetSimulationTime() &&
                                m_model.MobiliyActivated() && !m_model.IsSimulationFinished()) {
                            m_model.simulationSeconds++;
                            PaintNodeMovement(m_model.simulationSeconds);
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
    
    public class ScenarioFilenameFilter implements FilenameFilter{

        public boolean accept(File dir, String name) {
            return name.equals("Scenario");
        }

    }

    public Boolean isScenarioDir(File f) {
        if(f.exists()) {
            if(f.getName().equals("Scenario")){
                return true;
            }
        }
        return false;
    }

    public Boolean isScenarioDir(String s) {
        File f = new File(s);
        return isScenarioDir(f);
    }

    public Boolean hasScenarioDir(File f) {
        if(f.exists()) {
            if(f.listFiles(new ScenarioFilenameFilter()).length > 0) {
                return true;
            }
        }
        return false;
    }

    public Boolean hasScenarioDir(String s){
        File f = new File(s);
        return hasScenarioDir(f);
    }
}

