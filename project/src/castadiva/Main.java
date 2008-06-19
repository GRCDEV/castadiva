package castadiva;

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
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        CastadivaModel model = new CastadivaModel();
        MainMenuGUI view = new MainMenuGUI(model);
        AboutBox about = new AboutBox(model);
        SimulationGUI  simulationWindow = new SimulationGUI(model);
        TrafficGUI traffic = new TrafficGUI(model);
        APNewGUI newAPWindow = new APNewGUI(model);
        APModifyGUI modifyAPWindow = new APModifyGUI(model);
        ComputerGUI computerGUI = new ComputerGUI(model);
        InstallApGUI installAP = new InstallApGUI(model);
        RandomSimulationGUI randomSimulation = new RandomSimulationGUI(model);
        NewExternalTrafficGUI attachApplication = new NewExternalTrafficGUI(model);
        HelpWindow helpWindow = new HelpWindow(model);
        
        
        CastadivaController controller = new CastadivaController(model, view, about, 
                simulationWindow, traffic, newAPWindow, modifyAPWindow, computerGUI, 
                installAP, randomSimulation, attachApplication, helpWindow);
        
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
    }

}
