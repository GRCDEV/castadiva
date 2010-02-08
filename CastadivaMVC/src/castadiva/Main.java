package castadiva;

import castadiva_gui.AboutBox;
import castadiva_gui.RandomSimulationGUI;
import castadiva_gui.TrafficGUI;
import castadiva_gui.HelpWindow;
import castadiva_gui.APNewGUI;
import castadiva_gui.ComputerGUI;
import castadiva_gui.NewExternalTrafficGUI;
import castadiva_gui.ProtocolsGUI;
import castadiva_gui.APModifyGUI;
import castadiva_gui.MainMenuGUI;
import castadiva_gui.SimulationGUI;
import castadiva_gui.MobilityDesignerGUI;
import castadiva_gui.InstallApGUI;
import castadiva_gui.ExecutionPlannerGUI;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * This class start all the program and generate all the classes needed.
 *
 * @author Jorge Hortelano Otero.
 * @version %I%, %G%
 * @since 1.4
 */
public class Main {

    /** Creates a new instance of Main */
    public Main() {
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
        // handle exception
        } catch (ClassNotFoundException e) {
        // handle exception
        } catch (InstantiationException e) {
        // handle exception
        } catch (IllegalAccessException e) {
        // handle exception
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here        

        CastadivaModel model = new CastadivaModel();
        MainMenuGUI view = new MainMenuGUI(model);
        AboutBox about = new AboutBox(model);
        SimulationGUI simulationWindow = new SimulationGUI(model);
        TrafficGUI traffic = new TrafficGUI(model);
        APNewGUI newAPWindow = new APNewGUI(model);
        APModifyGUI modifyAPWindow = new APModifyGUI(model);
        ComputerGUI computerGUI = new ComputerGUI(model);
        InstallApGUI installAP = new InstallApGUI(model);
        RandomSimulationGUI randomSimulation = new RandomSimulationGUI(model);
        NewExternalTrafficGUI attachApplication = new NewExternalTrafficGUI(model);
        HelpWindow helpWindow = new HelpWindow(model);
        ProtocolsGUI protocolGui = new ProtocolsGUI(model);
        ExecutionPlannerGUI exec = new ExecutionPlannerGUI(model);
        MobilityDesignerGUI mobDes = new MobilityDesignerGUI(model);
        PluginDetector pl = new PluginDetector();

        CastadivaController controller = new CastadivaController(model, view, about,
                simulationWindow, traffic, newAPWindow, modifyAPWindow, computerGUI,
                installAP, randomSimulation, attachApplication, helpWindow, protocolGui, exec, mobDes, pl);

        simulationWindow.setVisible(false);
        about.setVisible(false);
        view.setVisible(true);
        traffic.setVisible(false);
        newAPWindow.setVisible(false);
        modifyAPWindow.setVisible(false);
        computerGUI.setVisible(false);
        installAP.setVisible(false);
        randomSimulation.setVisible(false);
        attachApplication.setVisible(false);
        protocolGui.setVisible(false);
        exec.setVisible(false);
        mobDes.setVisible(false);
    }
}
