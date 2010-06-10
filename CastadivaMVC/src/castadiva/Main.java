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
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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


   private static Boolean checkArgs(String[] args, CastadivaModel model) {
        //TODO - Mejorar detección de argumentos
       String recognizedArgs[] = {"--config-dir","-cd","--no-reset","-nr", "--computer-working-dir", "-cwd"};
       Integer option[] = {1,1,2,2,3,3};

       Boolean used[] = new Boolean[args.length];
       for(int i = 0; i < used.length; i++)
           used[i] = false;

       Boolean error = false;

       System.out.println("Checking args:");
  
       File f;
       for(int i = 0; i < args.length; i++) {
           Integer SelectedOption = 0;
           if(args[i].startsWith("-") && !used[i]) {
               for(int j = 0; j < recognizedArgs.length; j++) {
                   if(args[i].equals(recognizedArgs[j])) {
                       SelectedOption = option[j];
                       break;
                   }
               }
               switch(SelectedOption) {
                   case 0:
                       error = true;
                       System.err.println(args[i] + " : Command not recognized.");
                       break;
                   case 1:
                       System.out.println("\tConfig dir: " + args[i+1]);

                       f = new File(args[i+1]);
                       if(!f.exists() || !f.isDirectory()) {
                            System.err.println("---- Configuration directory not valid ----");
                            error = true;
                       } else {
                            model.DEFAULT_CONFIG_DIRECTORY = args[i+1];
                            used[i] = true;
                            used[i+1] = true;
                       }
                       break;
                   case 2:
                       System.out.println("Not rebooting APS in Execution Planner.");
                       model.RESET_APS = false;
                       used[i] = true;
                       break;
                   case 3:
                       System.out.println("Computer Working directory: " + args[i+1]);
                       f = new File(args[i+1]);
                       if(!f.exists() || !f.isDirectory()) {
                            System.err.println("---- Working directory not valid ----");
                            error = true;
                       } else {
                            model.setComputerWorkingDirectory(args[i+1]);
                            used[i] = true;
                            used[i+1] = true;
                       }
                       break;
               }
           }else{
                if(used[i] == false) {
                    System.err.println(args[i] +  " is not a valid option");
                }
           }
       }

       return error;
    }

    /** Creates a new instance of Main */
    public Main() {
        /*try {
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
        }*/
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            } catch (ClassNotFoundException ex1) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (InstantiationException ex1) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (IllegalAccessException ex1) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (UnsupportedLookAndFeelException ex1) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        
        CastadivaModel model = new CastadivaModel();
        System.out.println("Welcome to Castadiva " + model.VERSION);
        
        if(!checkArgs(args, model)) {
       
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


        }else{
            System.err.println("Error parsing args. Exiting...");
        }
    }
}
