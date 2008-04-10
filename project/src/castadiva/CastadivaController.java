package castadiva;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;
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
            HelpWindow helpWindow) {
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

        MainMenuListenersReady();
        SimulationAllListenersReady();
        RandomSimulationAllListenersReady();
        ConfigurationComputerListenersReady();
        NewApListenerReady();
        ModifyApListenerReady();
        InstallAPListenerReady();
        TrafficListenerReady();
        AttachExternalTrafficListenerReady();
        HelpWindowListenerReady();

        checkSimulation = new CheckSimulationThread();
        checkSimulation.start();
    }

    /**
     * Generate a window to search in the file system.
     * @param mode The kind of window. 
     * @see setFileSelectionMode
     */
    String ExplorationWindow(String title, int mode) {
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
        m_view.addInstallButtonListener(new InstallApListener());
        m_view.addComputerConfigurationListener(new ComputerConfigurationListener());
        m_view.addRandomSimulationListener(new RandomSimulationMenuOptionListener());
        m_view.addExternalDeviceListener(new AttachExternalDeviceListener());
        m_view.addHelpWindowListener(new HelpWindowListener());
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

    class AttachExternalDeviceListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_attachTraffic.UpdateWindow();
            m_attachTraffic.setVisible(true);
        }
    }

    /****************************************************************************
     *
     *                              SIMULATION
     *
     ****************************************************************************/
    /**
     *  Inserte the listeners of this window
     */
    private void SimulationAllListenersReady() {
        m_simulationWindow.addSimulateButtonListener(new SimulateListener());
        m_simulationWindow.addReplaySimulationListener(new ReplaySimulationListener());
        m_simulationWindow.addStopSimulationListener(new StopSimulationListener());
        m_simulationWindow.addResetButtonListener(new ResetSimulationListener());
        m_simulationWindow.addCloseButtonListener(new CloseButtonListener());
        m_simulationWindow.addXTextFieldListener(new XFieldListener());
        m_simulationWindow.addYTextFieldListener(new YFieldListener());
        m_simulationWindow.addZTextFieldListener(new ZFieldListener());
        m_simulationWindow.addATextFieldListener(new AreaFieldListener());
        m_simulationWindow.addXBoundListener(new XBoundListener());
        m_simulationWindow.addYBoundListener(new YBoundListener());
        m_simulationWindow.addSimTimeTextFieldListener(new SimulationTimeListener());
        m_simulationWindow.addMaxSpeedTextFieldListener(new MaxSpeedListener());
        m_simulationWindow.addMinSpeedTextFieldListener(new MinSpeedListener());
        m_simulationWindow.addSimPauseTextFieldListener(new SimulationPauseListener());
        m_simulationWindow.addAPComboBoxListener(new SelectAPListener());
        m_simulationWindow.addTrafficButtonListener(new TrafficViewerListener());
        m_simulationWindow.addSizeTextFieldListener(new SizeFieldListener());
        m_simulationWindow.addRandomSceneryButtonListener(new RandomSceneryListener());
        m_simulationWindow.addChangeReplayTimeListener(new ReplayPositionListener());
        m_simulationWindow.addHelpButtonListener(new HelpSimulationListener());
    }

    public void PreviousSimulationSteaps() {
        m_model.SetSimulationTime(m_simulationWindow.ReturnSimulationTime());
        m_model.CalculateRealSimulationTime();
        m_model.SetMobilityMaxSpeed(m_simulationWindow.ReturnMobilityMaxSpeed());
        m_model.SetMobilityMinSpeed(m_simulationWindow.ReturnMobilityMinSpeed());
        m_model.SetBoundX(m_simulationWindow.ReturnXBoundField());
        m_model.SetBoundY(m_simulationWindow.ReturnYBoundField());
        m_model.SetSimulationPause(m_simulationWindow.ReturnMobilityPauseTextField());

        if (m_model.MobiliyActivated()) {
            m_model.ExtendNodePositions();
        }
        m_model.randomSimulating = false;
        m_simulationWindow.EnableReplay(false);
        m_simulationWindow.MarkReplayCheck(false);
        m_model.replay = false;
        m_model.tableModel.UpdateData(m_model.accessPoints.GetTraffic());
        m_trafficGUI.UpdateTable();
        m_trafficGUI.DisableWindow();
        m_randomSimulation.DisableWindow();
        m_model.ChangeRoutingProtocol(m_simulationWindow.ProtocolSelected());
        m_simulationWindow.ChangeSimulationTime(m_model.GetSimulationTime());
        m_randomSimulation.ChangeSimulationTime(m_model.GetSimulationTime());
    }

    class RandomSceneryListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.randomSimulating = false;
            m_model.SetRandomScenery();
            m_simulationWindow.Repaint();
            m_simulationWindow.UpdatePositionPanel();
        }
    }

    class SimulateListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            PreviousSimulationSteaps();
            m_model.isRandomTraffic = false;
            m_model.AllSimulationSteaps();
            m_simulationWindow.RunningSimulationView();
        }
    }

    class ReplaySimulationListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //If the user has change the node position... correct it.
            m_model.PositionateNodesInDeterminedSecond(0);
            m_simulationWindow.ModifyBlackBoard();
            //Make a simulation without calculating a new node movements.
            PreviousSimulationSteaps();
            m_model.ReplaySimulationSteaps();
            m_simulationWindow.ReplaySimulationView();
        }
    }

    class StopSimulationListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.KillSimulation();
        }
    }

    class ResetSimulationListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String instruction = "reboot";
            m_model.InstructionForAll(instruction, false);
            m_model.EndStopwatch();
        }
    }

    class CloseButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.StopSimulation();
            m_view.setVisible(true);
            m_simulationWindow.setVisible(false);
        }
    }

    class XFieldListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.selectionedAP1.x = m_simulationWindow.ReturnXTextField();
            m_model.ChangeAP(m_simulationWindow.WhatSelectedAP(), m_model.selectionedAP1);
            m_model.GenerateStaticVisibilityMatrix();
            m_simulationWindow.ModifyBlackBoard();
        }
    }

    class YFieldListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.selectionedAP1.y = m_simulationWindow.ReturnYTextField();
            m_model.ChangeAP(m_simulationWindow.WhatSelectedAP(), m_model.selectionedAP1);
            m_model.GenerateStaticVisibilityMatrix();
            m_simulationWindow.ModifyBlackBoard();
        }
    }

    class ZFieldListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.selectionedAP1.z = m_simulationWindow.ReturnZTextField();
            m_model.ChangeAP(m_simulationWindow.WhatSelectedAP(), m_model.selectionedAP1);
            m_model.GenerateStaticVisibilityMatrix();
            m_simulationWindow.ModifyBlackBoard();
        }
    }

    class XBoundListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.SetBoundX(m_simulationWindow.ReturnXBoundField());
            m_simulationWindow.ModifyBlackBoard();
        }
    }

    class MaxSpeedListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.SetMobilityMaxSpeed(m_simulationWindow.ReturnMobilityMaxSpeed());
        }
    }

    class MinSpeedListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (m_simulationWindow.ReturnMobilityMinSpeed() >
                    m_simulationWindow.ReturnMobilityMaxSpeed()) {
                m_simulationWindow.SetMobilityMaxSpeed(m_simulationWindow.ReturnMobilityMinSpeed());
            }
            m_model.SetMobilityMinSpeed(m_simulationWindow.ReturnMobilityMinSpeed());
        }
    }

    class YBoundListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.SetBoundY(m_simulationWindow.ReturnYBoundField());
            m_simulationWindow.ModifyBlackBoard();
        }
    }

    class AreaFieldListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.selectionedAP1.range = m_simulationWindow.ReturnATextField();
            m_model.ChangeAP(m_simulationWindow.WhatSelectedAP(), m_model.selectionedAP1);
            m_model.GenerateStaticVisibilityMatrix();
            m_simulationWindow.ModifyBlackBoard();
        }
    }

    class SimulationTimeListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            int st = m_simulationWindow.ReturnSimulationTime();
            m_model.SetSimulationTime(st);
            m_randomSimulation.ChangeSimulationTime(st);
        }
    }

    class SimulationPauseListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.SetSimulationPause(m_simulationWindow.ReturnMobilityPauseTextField());
        }
    }

    class ReplayPositionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (m_simulationWindow.ReturnReplayTime() > m_model.GetSimulationTime()) {
                m_simulationWindow.ChangeReplayTime(m_model.GetSimulationTime());
            }
            if (m_simulationWindow.ReturnReplayTime() < 0) {
                m_simulationWindow.ChangeReplayTime(0);
            }
            if (m_model.replay) {
                m_model.simulationSeconds = m_simulationWindow.ReturnReplayTime();
                checkSimulation.PaintNodeMovement(m_simulationWindow.ReturnReplayTime());
            }
        }
    }

    class TrafficViewerListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.tableModel.UpdateData(m_model.accessPoints.GetTraffic());
            m_trafficGUI.UpdateTable();
            m_trafficGUI.setVisible(true);
        }
    }

    class SelectAPListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (m_model.HowManyAP() > 0) {
                if (m_simulationWindow.WhatSelectedAP() >= 0) {
                    m_model.selectionedAP1 = m_model.GetAP(m_simulationWindow.WhatSelectedAP());
                    m_simulationWindow.UpdatePositionPanel();
                    m_simulationWindow.UpdateShowNodeRange();
                }
            }
        }
    }

    class SizeFieldListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_simulationWindow.GetNewSize();
        }
    }

    class HelpSimulationListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            m_model.selectedHelp = m_model.SCENARIO_INDEX;
            ShowHelp();
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
    private void ShowHelp() {
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
        m_simulationWindow.ConsoleText();
        m_simulationWindow.StopwatchText();
    }

    /**
     * This thread control the simulation time and wath if all routers end 
     * their operations. 
     */
    class CheckSimulationThread extends Thread {

        public CheckSimulationThread() {
        }

        private void PaintNodeMovement(Integer second) {
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
                            EndSimulation();
                        } else {
                            if (m_model.randomNotEnded) {
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
}

