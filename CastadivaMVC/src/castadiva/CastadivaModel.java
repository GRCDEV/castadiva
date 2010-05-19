package castadiva;

//import castadiva.pluginLoader.pluginLoader;
import castadiva.TrafficRecords.RandomTrafficRecord;
import castadiva.TrafficRecords.TrafficRecord;
import castadiva.TableModels.RandomTrafficTableModel;
import castadiva.TableModels.TrafficTableModel;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
//import com.sun.jndi.cosnaming.IiopUrl.Address;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.jar.*;
import java.util.zip.ZipEntry;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import lib.IMobilityPluginCastadiva;
import lib.IPluginCastadiva;

/**
 * Following the Model - View - Controler programming guide, this class is the Model.
 * <p>
 * This class is the kernel of the program. Allow the user to generate a WiFi network
 * and define traffic between nodes like other existing simulators like NS. The difference
 * is Castadiva use the Jcraft tools to really connect to an existing AP and obtain
 * true statistics. This means that:
 * <ul>
 * <li> Interact with another files like NS and MAYA.
 * <li> Control the time of the entire project.
 * <li> Calculate the interaction between nodes according to the position.
 * <li> Connect to the APs ordering what instructions must follow each of them.
 * <li> Generate the orders to the AP in the required language.
 * </ul>
 *
 * @author Jorge Hortelano Otero.
 * @version %I%, %G%
 * @since 1.4
 * @see AP
 * @see APs
 * @see CastadivaController
 */
public class CastadivaModel {

    public final String VERSION = "0.96-QoS";
    public Computer computer;
    public APs accessPoints = new APs();
    //Node connection
    float visibilityMatrix[][];
    Integer gatewayMatrix[][];
    //Default folders variables

    private final String STATISTICS_UDP_DESTINATION_FILE = "CastadivaUdpDestino";
    private final String STATISTICS_TCP_DESTINATION_FILE = "CastadivaTcpDestino";
    private final String INSTRUCTIONS_SERVER_FILE = "serverForNode.sh";
    private final String INSTRUCTIONS_CLIENT_FILE = "clientForNode.sh";
    private final String INSTRUCTIONS_VISIBILITY_FILE = "visibilityForNode.sh";
    private final String INSTRUCTIONS_ROUTING_FILE = "routingForNode.sh";
    private final String DEFAULT_INSTRUCTIONS_FOLDER = "instructionsForNode";
    private final String DEFAULT_STARTING_INSTRUCTION = "startSimulation.sh";
    private final String DEFAULT_EXTERNAL_TRAFFIC_INSTRUCTION = "externalTraffic.sh";
    private final String INSTRUCTIONS_REDIRECT_FILE = "redirectForNode.sh";
    private final String INSTRUCTIONS_CATEGORY_FILE = "categoryForNode.sh";
    private final String DEFAULT_SHELL_LAUNCHER_X86 = "bash";
    private final String DEFAULT_SHELL_LAUNCHER_MIPS = "sh";
    private final String FILE_COMPUTER = "Computer.dat";
    public final String FILE_APS = "APs.dat";
    private final String FILE_SCENARIO = "Scenario.dat";
    private final String FILE_EXTERNAL_TRAFFIC = "ExternalTraffic.dat";
    private String STARTING_FOLDER = "/";
    public String DEFAULT_CONFIG_DIRECTORY = "configuration";
    public final String DEFAULT_APPLICATION_FILE = "applications.txt";
    public static final String CASTADIVA_SOURCE_FOLDER = "src";
    public static final String PLUGIN_WORKFOLDER = "pluginTemporaryFiles"; // The following folder is used to compile plugins
    public static final String PLUGIN_JAR_FOLDER = "src/castadiva/Plugins"; // The compiled plugins are then stored in the following folder
    public static final String PLUGIN_INCLUDE_FOLDER = "src/lib"; // To compile, the abstract IPluginCastadiva.java is needed. It must be located in the following directory
    public static final String MOBILITY_PLUGIN_JAR_FOLDER = "src/castadiva/MobilityPlugins/"; // The compiled mobility plugins are stored in that folder

    //Simulation variables
    public final int TRAFFIC_SERVER_TIME_WAIT = 2;
    public final int TTCP_SERVER_MAX_WAIT = 10;
    public int VISIBILITY_TIME_WAIT = 8;
    public int PROTOCOL_TIME_WAIT = 0;
    private final int ROUTING_EXTRA_TIME = 0;
    private final int TRAFFIC_PORT = 23000;
    private final int DEFAULT_X_BOUND = 1500;
    private final int DEFAULT_Y_BOUND = 1500;
    private final String WIFI_SSID = "Wannes";
    private final String DEFAULT_GW = "192.168.1.2";
    private boolean statisticsShowed = true;
    private int stopwatch;
    private Timer timer;
    private CastadivaMainTimerTask timerTask;
    public AP selectionedAP1;
    public AP selectionedAP2;
    public String selectionedIP1;
    public String selectionedIP2;
    private List<List<String>> categoryInstructions = new ArrayList<List<String>>();
    private List<List<String>> redirectInstructions = new ArrayList<List<String>>();
    private List<List<String>> instructions = new ArrayList<List<String>>();
    private List<List<String>> externalTrafficInstructions = new ArrayList<List<String>>();
    private List<List<String>> clientInstructions = new ArrayList<List<String>>();
    private List<Integer> clientLastTime = new ArrayList<Integer>();
    private List<Integer> serverLastTime = new ArrayList<Integer>();
    private List<Integer> clientEndingUdpTime = new ArrayList<Integer>();
    private List<List<String>> serverInstructions = new ArrayList<List<String>>();
    private List<List<String>> killUdpClientInstructions = new ArrayList<List<String>>();
    private List<String> addInstruction = new ArrayList<String>();
    private List<List<String>> routingInstruction = new ArrayList<List<String>>();
    private List<String> removeInstruction = new ArrayList<String>();
    private List instructionsAnt = new ArrayList();
    private List<SSH> SSHInstructionThread = new ArrayList<SSH>();
    private List<SSH> SSHThread = new ArrayList<SSH>();
    private List<SSH> SSHClientThread = new ArrayList<SSH>();
    private List<SSH> SSHServerThread = new ArrayList<SSH>();
    public SshHost installNode;
    private int totalTime = 10;
    private Integer boardSize = 1000;
    private int numberNodes;
    private float x = DEFAULT_X_BOUND;
    private float y = DEFAULT_Y_BOUND;
    StatisticExchangeBuffer statisticsControl;
    public boolean replay = false;
    public Integer replayTime = 0;
    public String routingProtocol = "NONE";
    public String lastRoutingProtocol = "";
    private String deleteOptimumInstruction;
    public Float udpAverage = new Float(0);
    public Float throughputAverage = new Float(0);
    public boolean RTS = false;
    public Integer simulationSeconds = 0;
    private boolean simulating = false;
    //NS export/import
    private final String DEFAULT_USER = "root";
    private final String DEFAULT_PWD = ".GRC0510";
    private final String DEFAULT_IP_NET = "192.168.1.";
    private final String DEFAULT_WIFI_IP_NET = "192.168.2.";
    private final String DEFAULT_WIFI_MAC = "00:00:00:00:00:00";
    //private final String DEFAULT_WIFI_DEVICE = "eth1";
    private final String DEFAULT_WIFI_DEVICE = "wl0";
    private final String DEFAULT_ID = "router";
    private final float DEFAULT_RANGE = 250;
    private final Integer DEFAULT_CHANNEL = 3;
    private final String DEFAULT_MODE = "Ad-Hoc";
    private final String FILE_PREFIX = ".ctdv";
    private final String DEFAULT_PACKET_LIST = "packets.txt";
    private final String DEFAULT_PROCESSOR = "MIPS";
    public final String DEFAULT_PROCESSOR_FILE = "processors.txt";
    public final String DEFAULT_APS_FILE = "aps.txt";
    public final String DEFAULT_NFS_DIRECTORY = "/castadiva/nfs";
    Random randomGenerator;
    private NodePositionsFromNsMobility nsData;
    //Traffic variables.
    public TrafficTableModel tableModel;
    public RandomTrafficTableModel randomTrafficModel;
    public List<ExternalTraffic> externalTrafficFlow = new ArrayList<ExternalTraffic>();
    //If WRITE_TIME_IN_FILE != 0 write in file the arriving time of each packet.
    //Useful to see the retransmision time of TCP
    private final int WRITE_TIME_IN_FILE = 1;
    //Random simulation variables.
    public Integer minNodes = 0;
    public Integer maxNodes = 0;
    public Integer granularity = 0;
    private Integer iteration = 0;
    public String fileRandomScenaryFormat;
    public boolean randomSimulating = false;
    public List protocolSelectedRandomSimulation = new ArrayList();
    public String savePath = "";
    public Integer loop = 1;
    public Integer loops = 1;
    private Integer routingLoop = 0;
    public boolean randomNotEnded = false;
    public boolean isRandomTraffic = true;
    private List<Integer> listAddressNodes = new ArrayList<Integer>();
    private List<Integer> listSourceNodes = new ArrayList<Integer>();
    //Mobility Variables.
    private MobilityVectors allAddresses;
    private float pause = 0;
    private float maxSpeed = 0;
    private float minSpeed = 0;
    NodeCheckPoint nodePositions[][];
    private Integer[] sleep;
    private final Integer DELETING_INSTRUCTIONS_WAITING_TIME = 1;
    //Help Varibales.
    String Help_Folder = "helpEN";
    int selectedHelp = 0;
    final int ADD_NODE_INDEX = 0;
    final int ATTACH_INDEX = 1;
    final int SCENARIO_INDEX = 2;
    final int MAINMENU_INDEX = 4;
    final String[] helpFiles = {
        "addNode.txt", "attachTraffic.txt", "scenario.txt",
        "about.txt", "mainMenu.txt"
    };

    //If true, show output in console.
    public final boolean debug = true;
    public IPluginCastadiva[] routing_protocols = new IPluginCastadiva[]{};
    public IMobilityPluginCastadiva[] mob_plugins = new IMobilityPluginCastadiva[]{};
    public String mobilityModel = "RANDOM WAY POINT";
    //Ruta por defecto del planificador de ejecucioens
    public String pathScenario = "/Castadiva/Scenarios/";
    public boolean executionPlannerSimulating = false;
    public String pathTarget = "";;
    public PluginDetector detector;

    /** Creates a new instance of CastadivaModel */
    public CastadivaModel() {
            randomGenerator = new Random();
            boardSize = (int) Math.max(WhatBoundX(), WhatBoundY());
            tableModel = new TrafficTableModel(accessPoints.GetTraffic());
            randomTrafficModel = new RandomTrafficTableModel(accessPoints.GetRandomTraffic());
            computer = new Computer();
            SetStopwatch(0);
        
    }

    /****************************************************************************
     *
     *                             INQUERY METHODS
     *
     ****************************************************************************/
    /**
     * Allow to obtain the interface used by the computer. Useful for the GUI windows.
     * @see Computer
     */
    public String WhatComputerInterfaceString() {
        return computer.WhatInterfaceString();
    }

    /**
     * Allow to obtain the directory where all datas are saved. Useful for the GUI windows.
     * @see Computer
     */
    public String WhatComputerWorkingDirectory() {
        return computer.WhatWorkingDirectory();
    }

    /**
     * Change the directory where all data are saved.
     * @see Computer
     */
    public void SetComputerWorkingDirectory(String directory) {
        computer.ChangeWorkingDirectory(directory);
    }

    /**
     * Change the interface used by the class computer.
     * @see Computer
     */
    public void SetComputerInterface(String device) {
        computer.ChangeInterface(device);
        computer.CalculateIpFromInterface();
    }

    /**
     * Return the value of the stopwatch. The stopwatch control the simulation time.
     */
    public Integer WhatStopwatch() {
        return stopwatch;
    }

    /**
     * Set a value into the stopwatch used to control the simulation time.
     * @param value The new stopwatch value.
     */
    public void SetStopwatch(Integer value) {
        stopwatch = value;
    }

    /**
     * return the dimension of te simulation board.
     */
    public Integer WhatBoardSize() {
        return boardSize;
    }

    /**
     * Return the simulation bound X. An AP out of bounds will not be used into
     * the simulation.
     */
    public Float WhatBoundX() {
        return x;
    }

    /**
     * Return the simulation bound Y. An AP out of bounds will not be used into
     * the simulation.
     */
    public Float WhatBoundY() {
        return y;
    }

    /**
     * Return the max time defined to stop the movement of an AP, like NS.
     */
    public Float WhatTimePause() {
        return pause;
    }

    /**
     * Return the maximum speed allowed for the nodes.
     */
    public Float WhatMaxSpeed() {
        return maxSpeed;
    }

    /**
     * Return the minimum speed allowed for the nodes.
     */
    public Float WhatMinSpeed() {
        return minSpeed;
    }

    /**
     * Change the simulation bound X. An AP out of bounds will not be used into
     * the simulation.
     */
    public void SetBoundX(Float boundX) {
        x = boundX;
    }

    /**
     * Change the simulation bound Y. An AP out of bounds will not be used into
     * the simulation.
     */
    public void SetBoundY(float boundY) {
        y = boundY;
    }

    /**
     * Activate or desactivate the wifi RTS/CTS option
     */
    public void SetRTS(boolean value) {
        RTS = value;
    }

    /**
     * Change the max pause time between moves allowed for every AP.
     */
    public void SetSimulationPause(float value) {
        pause = value;
    }

    /**
     * Change the speed value of the mobility simulation option.
     */
    public void SetMobilityMaxSpeed(float value) {
        maxSpeed = value;
    }

    public void SetMobilityMinSpeed(float value) {
        minSpeed = value;
    }

    /**
     * Change the total simulation board.
     */
    public void ChangeBoardSize(Integer size) {
        boardSize = size;
    }

    /**
     * Change a determined AP used in the simulation for another one.
     * @param position The index for the old AP in the APs list.
     * @param node The new AP to replace the old.
     */
    public void ChangeAP(Integer position, AP node) {
        accessPoints.Set(position, node);
    }

    /**
     * Delete an AP used in the simulation.
     * @param position The position of the Ap to delete in the APs list.
     */
    public void RemoveAP(Integer position) {
        accessPoints.RemoveIndex(position);
    }

    /**
     * Return the total of AP used in the simulation.
     */
    public Integer HowManyAP() {
        return accessPoints.Size();
    }

    /**
     * Select into the simulation an AP.
     */
    public void SelectAP(Integer i) {
        selectionedAP1 = GetAP(i);
    }

    /**
     * Return an AP Object located in a determined position of the APs list.
     * @param i The index of the AP desired.
     */
    public AP GetAP(Integer i) {
        return accessPoints.Get(i);
    }

    /**
     * Return the value of the Simulation bound X.
     */
    public Float GetBoundX() {
        return x;
    }

    /**
     * Return the value of the Simulation bound Y.
     */
    public Float GetBoundY() {
        return y;
    }

    /**
     * Return the pause time allowed between movements of the APs.
     */
    public Float GetSimulationPause() {
        return pause;
    }

    /**
     * Return if the simulation is already waiting to show the traffic statistics.
     */
    public boolean IsStatisticsAlreadyShowed() {
        return statisticsShowed;
    }

    /**
     * The traffics statitistics are interesting for no more.
     */
    public void StatisticsAreShowed() {
        statisticsShowed = true;
    }

    /**
     * Return if the simulation has finished.
     */
    public boolean IsSimulationFinished() {
        return !simulating;
    }

    /**
     * Change an AP for another one.
     * @param number The position in the APs list of the AP wanted to replace.
     * @param address The ethernet address to connect the computer with the AP.
     * @param wifiAddress The address for the WiFi card. It would use for the
     * simulation to send a package to another AP.
     * @param user The user defined in the SSH connection to access to the AP.
     * @param pwd The password to connect to the AP by SSH.
     * @param id A name or tag to differenciate the AP from each other.
     * @param x The starting position of the AP in the simulation. Coordinate X.
     * @param y The starting position of the AP in the simulation. Coordinate Y.
     * @param z The starting position of the AP in the simulation. Coordinate Z.
     * @param range The signal range of the WiFi card.
     * @param directory the directory used to store the simulation data (can be a
     * NFS directory).
     * @param channel The 802.11 channel connection.
     * @param mode The connection mode (ad-hoc, managed, auto,... ).
     * @see AP
     * @see APs
     */
    public void SetAP(Integer number, String address, String wifiAddress,
            String wifiMac, String user, String pwd, String id, float x,
            float y, float z, float range, String directory, String processor,
            Integer channel, String mode, String wfDevice, String gw) {
        AP tmp_AP = new AP(address, wifiAddress, wifiMac, user, pwd, id, x,
                y, z, range, directory, processor, channel, mode, wfDevice, gw);
        accessPoints.Set(number, tmp_AP);
        }

    /**
     * Add an AP to the Simulation. This AP now can be used and placed into the simulation
     * @param address The ethernet address to connect the computer with the AP.
     * @param wifiAddress The address for the WiFi card. It would use for the
     * simulation to send a package to another AP.
     * @param user The user defined in the SSH connection to access to the AP.
     * @param pwd The password to connect to the AP by SSH.
     * @param id A name or tag to differenciate the AP from each other.
     * @param x The starting position of the AP in the simulation. Coordinate X.
     * @param y The starting position of the AP in the simulation. Coordinate Y.
     * @param z The starting position of the AP in the simulation. Coordinate Z.
     * @param directory the directory used to store the simulation data (can be
     * a NFS directory).
     * @param channel The 802.11 channel connection.
     * @param mode The connection mode (ad-hoc, managed, auto,... ).
     * @see AP
     * @see APs
     */
    public void AddAP(String address, String wifiAddress, String wifiMac, String user,
            String pwd, String id, float x, float y, float z, String directory,
            String processor, Integer channel, String mode, String wfDevice, String gw) {
        AP tmp_AP = new AP(address, wifiAddress, wifiMac, user, pwd, id, x,
                y, z, DEFAULT_RANGE, directory, processor, channel, mode, wfDevice, gw);
        accessPoints.Add(tmp_AP);
    }

    /**
     * Return a matrix witch contains the information describing what APs see anothers.
     */
    public float[][] WhatVisibilityMatrix() {
        return visibilityMatrix;
    }

    /**
     * Indicate if the mobility is used.
     */
    public boolean MobiliyActivated() {
        if (maxSpeed > 0) {
            return true;
        }
        return false;
    }

    /**
     * Return true si a mobility has defined.
     */
    public boolean ExistsOldMobility() {
        try {
            if (nodePositions[0].length > 0) {
                return true;
            }
        } catch (NullPointerException ne) {
            return false;
        }
        return false;
    }

    /**
     * Return a string with the path to a folder in the system where the user
     * can save, load, import from ns.
     */
    public String GetDefaultExplorationFolder() {
      if(STARTING_FOLDER.equals("/")) {
        return System.getenv("HOME");
      }
        return STARTING_FOLDER;
    }

    /**
     * Change the default folder where the user can save, load or import from ns.
     */
    public void ChangeDefaultExplorationFolder(String path) {
        STARTING_FOLDER = path;
    }

    /****************************************************************************
     *
     *                                    TIME CONTROL
     ****************************************************************************/
    /**
     * Obtain the simulation total time.
     * @see GetSimulationTime
     */
    public Integer GetRealSimulationTime() {
        return GetWaitingSimulationTime() + GetSimulationTime();
    }

    /**
     * Obtain only the time expended in traffic.
     * @ see GetRealSimulationTime
     */
    public Integer GetSimulationTime() {
        return totalTime;
    }

    /**
     * Set only the time expended in traffic.
     * @ see GetSimulationTime
     */
    public void SetSimulationTime(Integer value) {
        totalTime = value;
    }

    /**
     * Return the time used for starting the routing protocols, the mobility calculos, etc.
     */
    public Integer GetWaitingSimulationTime() {
        return GetApTimeWaiting() + ROUTING_EXTRA_TIME;
    }

    /**
     * Return when the routing protocol is loaded.
     */
    public Integer GetProtocolLoadingTimeWaiting() {
        return PROTOCOL_TIME_WAIT;
    }

    /**
     * Obtain the total time used to prepare the APs
     */
    public Integer GetApTimeWaiting() {
        return GetProtocolLoadingTimeWaiting() +
                TRAFFIC_SERVER_TIME_WAIT + VISIBILITY_TIME_WAIT;
    }

    /**
     * The visibility time depends on the number of nodes that are used into the
     * simulation. This function recalculate an optimized pause time.
     */
    public void ObtainNewVisibilityTime() {
        VISIBILITY_TIME_WAIT = 3 + accessPoints.Size() / 2;
    }

    /**
     * Control if the traffic is finished to show the results.
     */
    public boolean IsStatisticsEnded() {
        if (stopwatch > GetRealSimulationTime() &&
                statisticsControl.IsEndOfStatistics()) {
            return true;
        }
        return false;
    }

    /**
     * Prepare the Castadiva time control for a new simulation.
     * This means that the stopwatch return to the value zero.
     */
    private void NewStopwatch() {
        stopwatch = 0;
        timer = new Timer();
        timerTask = new CastadivaMainTimerTask();
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    /**
     * Stops the Castadiva time control.
     */
    void EndStopwatch() {
        try {
            timer.cancel();
        } catch (NullPointerException ex) {
        }
    }

    /**
     * If the simulation needs more time that users specificated time, change the simulation time.
     */
    public void CalculateRealSimulationTime() {
        int finalRealTime = 0;
        TrafficRecord record = null;

        for (int i = 0; i < accessPoints.GetTrafficSize(); i++) {
            record = (TrafficRecord) accessPoints.GetTraffic().get(i);
            if (record.getStop() > finalRealTime) {
                finalRealTime = record.getStop() + 1;
            }
        }
        if (totalTime < finalRealTime) {
            totalTime = finalRealTime;
        }
    }

    /**
     * If the Random simulation needs more time that users specificated time, change the simulation time.
     */
    public void CalculateRandomRealSimulationTime() {
        int finalRealTime = 0;
        RandomTrafficRecord record = null;

        for (int i = 0; i < accessPoints.GetRandomTrafficSize(); i++) {
            record = (RandomTrafficRecord) accessPoints.GetRandomTraffic().get(i);
            // int trafficLineTime = record.getStart() + (record.getMaxPackets() / record.getPacketsSeconds());
            //if(trafficLineTime > finalRealTime) finalRealTime = trafficLineTime;
            if (record.getStop() > finalRealTime) {
                finalRealTime = record.getStop() + 1;
            }
        }
        if (totalTime < finalRealTime) {
            totalTime = finalRealTime;
        }
    }

    /****************************************************************************
     *
     *                              NODE CONNECTION
     *
     ****************************************************************************/
    /**
     * Call to all methods to obtain the node visibility between APs.
     */
    private void CalculateNewNodeVisibility() {
        DeleteOldVisibilityInstructions();
        GenerateStaticVisibilityMatrix();
        AddNodeVisibility(true);
    }

    /**
     * Return the distance in metres between two nodes.
     * @param node1 The firt node to calculate the distance.
     * @param node2 The second node.
     */
    private float CalculateNodeDistance(AP node1, AP node2) {
        return (float) Math.sqrt(Math.pow((node1.x - node2.x), 2) +
                Math.pow((node1.y - node2.y), 2) + Math.pow((node1.z - node2.z), 2));
    }

    /**
     * Calculate what nodes are in range from other nodes.
     * Depends on the node signal range.
     * @param second The second where the visibility will be generated.
     */
    public float[][] GenerateMobilityVisibilityMatrix(Integer second) {
        AP node1, node2;
        float distance, distance1, distance2 = 0;
        float[][] matrix;
        NodeCheckPoint checkPointNode1, checkPointNode2, checkPointNode1b, checkPointNode2b;

        matrix = new float[accessPoints.Size()][accessPoints.Size()];

        for (int i = 0; i < accessPoints.Size(); i++) {
            for (int j = i; j < accessPoints.Size(); j++) {
                try {
                    checkPointNode1 = nodePositions[i][second];
                    checkPointNode2 = nodePositions[j][second];

                    //If the two nodes are too close to be out of range, we considere it out of range.
                    if (second < GetSimulationTime() - 1) {
                        checkPointNode1b = nodePositions[i][second + 1];
                        checkPointNode2b = nodePositions[j][second + 1];
                        distance2 = CalculateCheckPointDistance(checkPointNode1b, checkPointNode2b);
                    }
                    node1 = accessPoints.Get(i);
                    node2 = accessPoints.Get(j);
                    if (j == i) {
                        distance = 0;
                    } else {
                        distance1 = CalculateCheckPointDistance(checkPointNode1, checkPointNode2);
                        if (second < GetSimulationTime() - 1) {
                            if ((distance1 + distance2) / 2 > node1.range) {
                                distance = -1;
                            } else {
                                distance = (distance1 + distance2) / 2;
                            }
                        } else {
                            distance = distance1;
                        }
                    }
                    if (distance < node1.range) {
                        matrix[i][j] = distance;
                    } else {
                        matrix[i][j] = -1;
                    }
                    if (distance < node2.range) {
                        matrix[j][i] = distance;
                    } else {
                        matrix[j][i] = -1;
                    }
                } catch (NullPointerException npe) {
                    break;
                }
            }
        }
        return matrix;
    }

    /**
     * Generate a visibility matrix for static simulations (nodes without movement).
     */
    public void GenerateStaticVisibilityMatrix() {
        visibilityMatrix = GenerateVisibilityMatrix();
    }

    /**
     * Calculate what nodes are in range from other nodes.
     * Depends on the node signal range.
     * @return a matrix with values 0 if is the same node, -1 if is out of range or
     * positive integer that represent the distance in range.
     */
    private float[][] GenerateVisibilityMatrix() {
        AP node1, node2;
        float distance;
        float[][] matrix;

        matrix = new float[accessPoints.Size()][accessPoints.Size()];
        for (int i = 0; i < accessPoints.Size(); i++) {
            for (int j = i; j < accessPoints.Size(); j++) {
                node1 = accessPoints.Get(i);
                node2 = accessPoints.Get(j);
                if (node1.equals(node2)) {
                    distance = 0;
                } else {
                    distance = CalculateNodeDistance(node1, node2);
                }
                if (distance < node1.range) {
                    matrix[i][j] = distance;
                } else {
                    matrix[i][j] = -1;
                }
                if (distance < node2.range) {
                    matrix[j][i] = distance;
                } else {
                    matrix[j][i] = -1;
                }
            }
        }
        return matrix;
    }

    /**
     * Generate the rules to prevent an AP to see another according to the simulation.
     * @param deleteInstructions If true delete all genered instructions at the end of time.
     */
    private void AddNodeVisibility(boolean deleteInstructions) {
        String nodeInstructions;
        String delNodeInstructions;

        for (int i = 0; i < accessPoints.Size(); i++) {
            //Create the instruction list for one node.
            nodeInstructions = "#Starting visibility instructions.\n";
            delNodeInstructions = "#Deleting visibility instructions.\n";

            //Adding the visibility rules.
            for (int j = 0; j < accessPoints.Size(); j++) {
                AP nodo = accessPoints.Get(j);
                if (visibilityMatrix[i][j] < 0) {
                    nodeInstructions = nodeInstructions + LocateIptables(i) +
                            " -I INPUT -m mac --mac-source " + nodo.WhatWifiMac() +
                            " -j DROP\n" +
                            LocateIptables(i) +
                            " -I FORWARD -m mac --mac-source " + nodo.WhatWifiMac() +
                            " -j DROP\n";
                    delNodeInstructions = delNodeInstructions + LocateIptables(i) +
                            " -D INPUT -m mac --mac-source " + nodo.WhatWifiMac() +
                            " -j DROP\n" +
                            LocateIptables(i) +
                            " -D FORWARD -m mac --mac-source " + nodo.WhatWifiMac() +
                            " -j DROP\n";
                }
            }
            //Add both instructions path.
            if (deleteInstructions) {
                nodeInstructions = nodeInstructions + "sleep " +
                        (GetRealSimulationTime() + VISIBILITY_TIME_WAIT) + "\n" + delNodeInstructions;
            }
            //Enqueue the new created instructions.
            addInstruction.add(i, nodeInstructions);
        }
    }

    /**
     * Delete the old rules that prevent one AP to see another.
     */
    private void DeleteOldVisibilityInstructions() {
        String oldInstruction;
        String[] oldInstructions;
        String deleteInstruction = "";

        for (int i = 0; i < instructionsAnt.size(); i++) {
            //Empty the old rules.
            oldInstruction = (String) instructionsAnt.get(i);
            oldInstructions = oldInstruction.split("\n");
            deleteInstruction = "";
            for (int j = 0; j < oldInstructions.length; j++) {
                if (oldInstructions[j].contains("-I")) {
                    deleteInstruction = deleteInstruction + oldInstructions[j].replace("-I", "-D") + "\n";
                }
            }
            //Adding the delete instruccions to each node.
            removeInstruction.add(i, deleteInstruction);
        }
    }

    /****************************************************************************
     *
     *                             STATIC SIMULATION FUNCTIONS
     *
     ****************************************************************************/
    /**
     * Send one instruction for all nodes connected by SSH.
     * @param instruction The instruction to send (in bash syntax).
     * @param withExit If must be showed the return of the instruction.
     */
    void InstructionForAll(String instruction, boolean withExit) {
        String action;
        List<List<String>> nodeInstructions = new ArrayList<List<String>>();
        SshNode ssh;
        SSHInstructionThread = new ArrayList<SSH>();
        List<String> aux = new ArrayList<String>();
        String nodeSelfDestructionInstruction = "kill -9 $(ps -e |" +
                " grep \"/usr/sbin/dropbear\" | grep -v grep | grep -v \"400 S\" " +
                "| tail -1 | cut -n -d \"r\" -f1 )";

            aux.add(instruction);
        //Kill the SSH session (sometimes, not end).
            aux.add(nodeSelfDestructionInstruction);
        for(int i =0; i < accessPoints.Size(); i++){
            nodeInstructions.add(i, aux);
        }

        RunSimulation(SSHInstructionThread, nodeInstructions, withExit);
        //Close the session.
        try {
            for (int i = 0; i < SSHInstructionThread.size(); i++) {
                ssh = (SshNode) SSHInstructionThread.get(i);
                ssh.Disconnect();
            }
            SSHInstructionThread = null;
        } catch (NullPointerException npe) {
            System.out.println("SSH session already closed :"+npe);
        }
    }

    /**
     * LaunchStatisticRecovery generate a Thread for every declared line in
     * the traffic table.
     * This Threads check if the traffic is finished. When all threads are finished
     * means that the simulation is over.
     * @param statisticsControl is a vector that store a flag for every thread.
     * Allow to control when all the threads ends.
     *
     */
    void LaunchStatisticRecovery(StatisticExchangeBuffer statisticsControl) {
        List<ObtainStatisticsThread> VectorStatisticThread = new ArrayList<ObtainStatisticsThread>();

        for (int i = 0; i < accessPoints.GetTrafficSize(); i++) {
            TrafficRecord trafficLine = (TrafficRecord) accessPoints.GetTraffic().get(i);
            //Generate a thread to control the end of the traffic deppending of the kind of traffic.
            if (trafficLine.getTCPUDP().equals("UDP")) {
                ObtainStatisticsThread StatisticsUDP = new ObtainStatisticsThread(
                        computer.WhatWorkingDirectory() + File.separatorChar +
                        STATISTICS_UDP_DESTINATION_FILE + (i + 1), 20, accessPoints.GetTraffic(),
                        i, statisticsControl);
                VectorStatisticThread.add(i, StatisticsUDP);
                StatisticsUDP.start();
            } else {
                ObtainStatisticsThread StatisticsTCP = new ObtainStatisticsThread(
                        computer.WhatWorkingDirectory() + File.separatorChar +
                        STATISTICS_TCP_DESTINATION_FILE + (i + 1), 20, accessPoints.GetTraffic(),
                        i, statisticsControl);
                VectorStatisticThread.add(i, StatisticsTCP);
                StatisticsTCP.start();
                }
            }
        }

    /**
     * This function stop the simulation when all the traffic data has been calculate.
     *
     * @param statisticsControl is a vector that store a flag for every thread.
     * Allow to control when all the threads ends.
     * @see LaunchStatisticRecovery
     * @see IsStatisticsEnded
     */
    private void EndStatisticsSeeker() {
        if (statisticsControl.IsEndOfStatistics()) {
            EndStopwatch();
        }
    }

    /**
     * Prepare the simulation working directory for a new simulation.
     */
    private void PrepareComputerDirectory() {
        DeleteDirectory(computer.WhatWorkingDirectory());
    }

    /**
     * Clear all data for an old simulation and generate the new one to start another.
     */
    private void PrepareSimulation() {
        EndStopwatch();
        NodeDisconnect();
        ObtainNewVisibilityTime();
        PrepareComputerDirectory();
        PrepareTraffic();
        PrepareRoutingProtocol();
        EmptyInstructionsList();
    }

    /**
     * To start a new simulation, first the program must be sure that all datas are ready.
     */
    void AllSimulationSteaps() {
        PrepareSimulation();
        StartFileSimulation();
    }

    /**
     * Replay the last simulation.
     */
    void ReplaySimulationSteaps() {
        PrepareSimulation();
        ReplayFileSimulation();
    }

    /**
     * Prepare the simulation without mobility
     */
    private void StartStaticSimulation() {
        CalculateNewNodeVisibility();
        instructions = MergeInstructionsList(removeInstruction, addInstruction);
        CreateTraffic();
        externalTrafficInstructions = GenerateExternalTrafficInstructions();
        GenerateRoutingInstructions();
    }

    /**
     * Start a new simulation, storing first the instructions in a file.
     */
    private void StartFileSimulation() {
        List<List<String>> startingInstructions;

        statisticsShowed = false;
        simulating = true;
        //statisticsControl mark if all traffic lines are ended.
        statisticsControl = new StatisticExchangeBuffer(accessPoints.GetTrafficSize());
        if (MobiliyActivated()) {
            StartMobility();
        } else {
            StartStaticSimulation();
        }
        GenerateFileInstructionsForAllNodes();
        startingInstructions = GenerateStartingInstructionsForSSH();
        NewStopwatch();
        RunSimulation(SSHThread, startingInstructions, false);
    }

    /**
     * Replay the last simulation, storing first the instructions in a file.
     */
    private void ReplayFileSimulation() {
        List startingInstructions;


        statisticsShowed = false;
        simulating = true;
        //statisticsControl mark if all traffic lines are ended.
        statisticsControl = new StatisticExchangeBuffer(accessPoints.GetTrafficSize());

        if (MobiliyActivated()) {
            ReplayMobility();
        } else {
            StartStaticSimulation();
        }
        GenerateFileInstructionsForAllNodes();
        startingInstructions = GenerateStartingInstructionsForSSH();
        RunSimulation(SSHThread, startingInstructions, false);
    }

    /**
     * Generate one list ready to be sended by SSH to exectue the starting script
     * of the simulation.
     */
    public List GenerateStartingInstructionsForSSH() {
        List<List> startingInstructions = new ArrayList<List>();
        String data;
        for (int i = 0; i < accessPoints.Size(); i++) {
            List<String> nodeStartingInstruction = new ArrayList<String>();
            AP node = accessPoints.Get(i);
            if(node.WhatProcessor().equals("MIPS")) {
                data = DEFAULT_SHELL_LAUNCHER_MIPS + " " + node.WhatWorkingDirectory() + File.separator +
                        DEFAULT_INSTRUCTIONS_FOLDER + (i + 1) +
                        File.separator + DEFAULT_STARTING_INSTRUCTION;
            }else{
                data = DEFAULT_SHELL_LAUNCHER_X86 + " " + node.WhatWorkingDirectory() + File.separator +
                        DEFAULT_INSTRUCTIONS_FOLDER + (i + 1) +
                        File.separator + DEFAULT_STARTING_INSTRUCTION;
            }
            nodeStartingInstruction.add(data);
            startingInstructions.add(nodeStartingInstruction);
        }

        return startingInstructions;
    }

    /**
     * Generate one file for each node witch contain all instructions for
     * this node.
     */
    public void GenerateFileInstructionsForAllNodes() {
        for (int i = 0; i < accessPoints.Size(); i++) {
            StoreAllInstructionsForOneNode(i);
        }
    }

    /**
     * Select all instructions for one node and store it in a default file.
     * @param node The number of the node.
     */
    public void StoreAllInstructionsForOneNode(Integer node) {
        String folder = computer.WhatWorkingDirectory() + File.separator +
                DEFAULT_INSTRUCTIONS_FOLDER + (node + 1);
        List<String> startingInstructions = new ArrayList<String>();

        GenerateFolder(folder);

        String visibilityFile = folder + File.separator + INSTRUCTIONS_VISIBILITY_FILE;
        String clientFile = folder + File.separator + INSTRUCTIONS_CLIENT_FILE;
        String serverFile = folder + File.separator + INSTRUCTIONS_SERVER_FILE;
        String routingFile = folder + File.separator + INSTRUCTIONS_ROUTING_FILE;
        String startingFile = folder + File.separator + DEFAULT_STARTING_INSTRUCTION;
        String externalTrafficFile = folder + File.separator + DEFAULT_EXTERNAL_TRAFFIC_INSTRUCTION;
        String redirectFile = folder + File.separator + INSTRUCTIONS_REDIRECT_FILE;
        String categoryFile = folder + File.separator + INSTRUCTIONS_CATEGORY_FILE;
        //String deletingUdpFile = folder + file.separator + FINISH_UDP_INSTRUCTIONS_FILE;

        startingInstructions.addAll(KillAllOldInstructions());
        if(accessPoints.Get(node).WhatProcessor().equals("X86")) {
            startingInstructions.addAll(SynchronizeTime());
        }
        //startingInstructions.add("ifconfig wl0 down");
        //startingInstructions.add(SetWifiConfiguration(node));
        //startingInstructions.add("ifconfig wl0 up");
        startingInstructions.addAll(GenerateRunAllFilesOfSimulation(node));
        SaveInFile(startingInstructions, startingFile);
        StoreInFileInstructionForOneNode(instructions, node, visibilityFile);
        StoreInFileInstructionForOneNode(routingInstruction, node, routingFile);
        StoreInFileInstructionForOneNode(serverInstructions, node, serverFile);
        StoreInFileInstructionForOneNode(clientInstructions, node, clientFile);
        StoreInFileInstructionForOneNode(externalTrafficInstructions, node, externalTrafficFile);
        StoreInFileInstructionForOneNode(redirectInstructions, node, redirectFile);
        StoreInFileInstructionForOneNode(categoryInstructions, node,  categoryFile);
    //StoreInFileInstructionForOneNode(killUdpClientInstructions, node, deletingUdpFile);
    }

    /**
     * kill all instructions of a previous simulation.
     */
    public List<String> KillAllOldInstructions() {
        List<String> killInstructions = new ArrayList<String>();
        // Killing the sleep instructions will force every function to be processed imediatly
        // Only client and Server will remain as they have an internal timeout.
        
        killInstructions.add("/usr/bin/killall sleep 2>/dev/null;");
        return killInstructions;
    }

    /**
     * 
     * @return List of instructions to sinchronize time
     */
    public List<String> SynchronizeTime() {
        List<String> timeInstructions = new ArrayList<String>();

        timeInstructions.add("ntpdate ntp.upv.es");

        return timeInstructions;
    }

    /**
     * Configure the wifi of an access points to be in the same network of Castadiva.
     */
    public String SetWifiConfiguration(Integer node) {
        AP ap = accessPoints.Get(node);
        String encryption = " key restricted [1] 0012-3498-76";
        String wifi;
        if (RTS) {
            wifi = LocateIwconfig(node) + ap.WhatWifiDevice() +
                    " essid " + WIFI_SSID + " channel " +
                    ap.WhatChannel() + " mode " + ap.WhatMode() + " rts 0 " +
                    encryption;
        } else {
            wifi = LocateIwconfig(node) + ap.WhatWifiDevice() +
                    " essid " + WIFI_SSID + " channel " +
                    ap.WhatChannel() + " mode " + ap.WhatMode() +
                    " rts 2347" +
                    encryption;
        }
        return wifi;
    }

    /**
     * Generate one script to run all the needed scripts in the simulation.
     * @param node The node number witch is generated this script.
     */
    public List<String> GenerateRunAllFilesOfSimulation(Integer node) {
        List<String> AllFilesInstructions = new ArrayList<String>();
        AP ap = accessPoints.Get(node);
        File file = null;
        String DEFAULT_SHELL_LAUNCHER = (ap.WhatProcessor().equals("MIPS"))?DEFAULT_SHELL_LAUNCHER_MIPS:DEFAULT_SHELL_LAUNCHER_X86;
        AllFilesInstructions.add("cd " + ap.WhatWorkingDirectory() + File.separator +
                DEFAULT_INSTRUCTIONS_FOLDER + (node + 1));
        AllFilesInstructions.add(DEFAULT_SHELL_LAUNCHER + " ./" + INSTRUCTIONS_CATEGORY_FILE + " &");
        AllFilesInstructions.add(DEFAULT_SHELL_LAUNCHER + " ./" + INSTRUCTIONS_REDIRECT_FILE + " &");
        AllFilesInstructions.add(DEFAULT_SHELL_LAUNCHER + " ./" + DEFAULT_EXTERNAL_TRAFFIC_INSTRUCTION + " & ");
        AllFilesInstructions.add(DEFAULT_SHELL_LAUNCHER + " ./" + INSTRUCTIONS_ROUTING_FILE + " & ");
        AllFilesInstructions.add("sleep " + PROTOCOL_TIME_WAIT);
        AllFilesInstructions.add(DEFAULT_SHELL_LAUNCHER + " ./" + INSTRUCTIONS_SERVER_FILE + " & ");
        AllFilesInstructions.add("sleep " + TRAFFIC_SERVER_TIME_WAIT);
        AllFilesInstructions.add("sleep " + VISIBILITY_TIME_WAIT);
        // Wannes : With static simulations, it was possible to set visibility at any time before the simulation start.
        // With mobility, visibility rules become dynamic. If visibility instructions are not started at the same time,
        // visibility and traffic will not coincide and results will be wrong
        // Here, I left the "Visibility time wait" and moved the instruction. This is because I dont know exactly how
        // statistics are collected afterwards. This might have en influence. //TODO
        AllFilesInstructions.add(DEFAULT_SHELL_LAUNCHER + " ./" + INSTRUCTIONS_VISIBILITY_FILE + " & ");
        AllFilesInstructions.add(DEFAULT_SHELL_LAUNCHER + " ./" + INSTRUCTIONS_CLIENT_FILE + " & ");
        AllFilesInstructions.add("sleep " + GetSimulationTime());
        //AllFilesInstructions.add(DEFAULT_SHELL_LAUNCHER + "./" + FINISH_UDP_INSTRUCTIONS_FILE + " & ");
        return AllFilesInstructions;
    }

    /**
     * Store in a file only the instructions for one node in a file.
     * @param instructionListForAllNodes a group of instructions for all nodes.
     * @param node The node number.
     */
    public void StoreInFileInstructionForOneNode(List<List<String>> instructionListForAllNodes,
            Integer node, String file) {
        List<String> instructionsOneNode = new ArrayList<String>();
        instructionsOneNode.add("#!/bin/" + DEFAULT_SHELL_LAUNCHER_MIPS);
        if (node < instructionListForAllNodes.size()) {
            instructionsOneNode.addAll((List<String>) instructionListForAllNodes.get(node));
        }
        SaveInFile(instructionsOneNode, file);
    }

    private List<List<String>> StopVisibilityInstructions() {
        List<List<String>> stopVisibility = new ArrayList<List<String>>();

        String instructionStop = "/usr/bin/killall sleep";
        for (int i = 0; i < accessPoints.Size(); i++) {
            List<String> aux = new ArrayList<String>();
            stopVisibility.add(aux);
        }
        return stopVisibility;
    }

    /**
     * Abort a simulation in course.
     */
    public void StopSimulation() {
        List<List<String>> stopVisibility = StopVisibilityInstructions();
        RunSimulation(SSHThread, stopVisibility, false);
        //Desconectamos las sesiones SSH
        NodeDisconnect();
    }

    /**
     * Delete all instruction sended by CASTADIVA.
     */
    private void EmptyInstructionsList() {
        instructionsAnt = addInstruction;
        addInstruction = new ArrayList<String>();
        removeInstruction = new ArrayList<String>();
    }

    /**
     * Combine two instructions list in one.
     */
    private List<List<String>> MergeInstructionsList(List<String> instructionsList1, List<String> instructionsList2) {
        List<List<String>> instructionsCombinadas = new ArrayList<List<String>>();
        List<String> nodeInstructionsFinal;
        String cadena;

        for (int i = 0; i < accessPoints.Size(); i++) {
            cadena = "";
            nodeInstructionsFinal = new ArrayList<String>();
            try {
                cadena = instructionsList1.get(i).toString();
                nodeInstructionsFinal.add(cadena);
            } catch (IndexOutOfBoundsException ioe) {
            }
            try {
                cadena = instructionsList2.get(i).toString();
                nodeInstructionsFinal.add(cadena);
            } catch (IndexOutOfBoundsException ioe) {
            }
            instructionsCombinadas.add(nodeInstructionsFinal);
        }
        return instructionsCombinadas;
    }

    /**
     * Connet to the APs and send the instructions generated.
     * @param sshThreadList The list of Threads to control the SSH connection.
     * @param instructionsList The list of instructions to send.
     * @param exit Show or not the exit message of the SSH connection.
     * @see AddNodeVisibility
     * @see CreateTraffic
     * @see StartSimulation
     */
    private void RunSimulation(List<SSH> sshThreadList, List<List<String>> instructionsList, boolean exit) {
        //Generamos los hilos pertienentes.
        List emptyList = new ArrayList();
        for (int i = 0; i < accessPoints.Size(); i++) {
            if (instructionsList.size() > 0) {
                try {
                    SshNode ssh = new SshNode(accessPoints.Get(i), (List) instructionsList.get(i),
                            exit, emptyList, computer.WhatWorkingDirectory());
                    sshThreadList.add(i, ssh);
                    ssh.start();
                } catch (Exception ce) {
                    if (debug){
                        System.out.println("An error occured while initializing the ssh connection with AP"+i+" :"+ce);
                    }
                }
            }
        }
    }

    /**
     * End the SSH connection to all APs.
     */
    private void NodeDisconnect() {
        for (int i = 0; i < SSHThread.size(); i++) {
            SshNode ssh = (SshNode) SSHThread.get(i);
            ssh.Disconnect();
        }
        for (int i = 0; i < SSHClientThread.size(); i++) {
            SshNode ssh = (SshNode) SSHClientThread.get(i);
            ssh.Disconnect();
        }
        for (int i = 0; i < SSHServerThread.size(); i++) {
            SshNode ssh = (SshNode) SSHServerThread.get(i);
            ssh.Disconnect();
        }

    }

    /**
     * Kill a started simulation.
     */
    void KillSimulation() {
        String stopAll = KillAllOldInstructions() +
                StopTraffic();
        InstructionForAll(stopAll, false);
        EndStopwatch();
        StopSimulation();
    }

    /****************************************************************************
     *
     *                              TRAFFIC SIMULATION
     *
     ****************************************************************************/
    /**
     * Get ready the packet traffic store data.
     */
    private void PrepareTraffic() {
        Vector<TrafficRecord> tmp_traffic;
        //Preparing Traffic Table.
        for (int i = 0; i < accessPoints.GetTrafficSize(); i++) {
            TrafficRecord traffic = (TrafficRecord) accessPoints.GetTraffic().get(i);
            traffic.setLastSpeed(Float.parseFloat("0"));
            traffic.setPacketsReceived(0);

            tmp_traffic = accessPoints.GetTraffic();
            tmp_traffic.set(i, traffic);
        }

        //Preparing traffic instruction list.
        for (int i = 0; i < accessPoints.Size(); i++) {
            List trafficInstructionsTemporal = new ArrayList();
        }

        serverInstructions = new ArrayList<List<String>>();
        clientInstructions = new ArrayList<List<String>>();
        clientLastTime = new ArrayList<Integer>();
        serverLastTime = new ArrayList<Integer>();
        killUdpClientInstructions = new ArrayList<List<String>>();
        clientEndingUdpTime = new ArrayList<Integer>();
        throughputAverage = new Float(0);
        udpAverage = new Float(0);
    }

    /**
     * Create a pause time before starting clients.
     * @param record The traffic record from the program read the instruction.
     * @param node The node to set the sleep time.
     * @return The instruction in one string.
     */
    private String GenerateStartingTimeOfClientInstructions(TrafficRecord record, Integer node) {
        Integer startingTime;
        String sleepInstruction;

        startingTime = record.getStart();
        sleepInstruction = "sleep " + startingTime + " && ";
        return sleepInstruction;
    }

    /**
     * Create a pause time before starting servers.
     * @param record The traffic record from the program read the instruction.
     * @param node The node to set the sleep time.
     * @return The instruction in one string.
     */
    private String GenerateStartingTimeOfServersInstructions(TrafficRecord record, Integer node) {
        Integer startingTime;
        String sleepInstruction;

        startingTime = record.getStart();
        sleepInstruction = "sleep " + 0 + " && ";
        return sleepInstruction;
    }

    /**
     * Create a pause time before killing clients.
     * @param record The traffic record from the program read the instruction.
     * @param node The node to set the sleep time.
     * @return The instruction in one string.
     */
    private String GenerateEndingTimeOfClientUdpInstructions(TrafficRecord record, Integer node) {
        Integer endingTime;
        String sleepInstruction;

        endingTime = record.getStop();
        sleepInstruction = "sleep " + endingTime + " && ";
        return sleepInstruction;
    }

    /**
     * Create an UDP instructions to stop the AP when the simulation is finished.
     */
    private String CreateDeletingUdpInstructions(TrafficRecord record, Integer number) {
        /*String InstructionForDeleteUdpClients = " kill -9 `ps -e | grep \"ttcp -t -p "+
        (TRAFFIC_PORT + number) + "\" | awk '{print $1}'` 2> /dev/null ";   */
        String InstructionForDeleteUdpClients = "\n";
        return InstructionForDeleteUdpClients;
    }

    /**
     * Create a TCP instructions to stop the AP when the simulation is finished.
     */
    private String CreateDeletingTcpInstructions(TrafficRecord record, Integer number) {
        String InstructionForDeleteUdpClients = "\n";
        return InstructionForDeleteUdpClients;
    }

    /**
     * Cretate one UDP instruction for a client.
     * @param record The traffic record from the program read the instruction.
     * @param number The number of the instruction.
     * @return The instruction in one string.
     */
    private String GenerateClientTrafficUdpInstuction(TrafficRecord record, int number) {
        String sourceInstruction;
        Integer startNode, endNode;
        AP sourceNode, destinationNode;

        startNode = accessPoints.SearchAP(record.getSource());
        endNode = accessPoints.SearchAP(record.getAddress());
        sourceNode = accessPoints.Get(startNode);
        destinationNode = accessPoints.Get(endNode);

        String aux;

        if(record.getDacme()) {
            aux = "1";
        }else{
            aux = "0";
        }
        sourceInstruction = sourceNode.WhatWorkingDirectory() + File.separator +
                "bin" + File.separator +
                "UdpFlowClient" + sourceNode.WhatProcessor() + " " +
                destinationNode.WhatWifiIP() + " " + (TRAFFIC_PORT + number) + " " +
                record.getSize() + " " + record.getPacketsSeconds() + " " +
                record.getDelay() + " " + record.getIntAccessCategory() + " " +
                (record.getStop() - record.getStart()) + " " +
                aux + " > " + sourceNode.WhatWorkingDirectory() + File.separator
                + "SalidaUdp" + (TRAFFIC_PORT + number) + " ";
        return sourceInstruction;
    }

    /**
     * Cretate one TCP instruction for a client.
     * @param record The traffic record from the program read the instruction.
     * @param number The number of the instruction.
     * @return The instruction in one string.
     */
    private String GenerateClientTrafficTcpInstuction(TrafficRecord record, int number) {
        String sourceInstruction;
        Integer startNode, endNode;
        AP sourceNode, destinationNode;

        startNode = accessPoints.SearchAP(record.getSource());
        endNode = accessPoints.SearchAP(record.getAddress());
        sourceNode = accessPoints.Get(startNode);
        destinationNode = accessPoints.Get(endNode);

        sourceInstruction = sourceNode.WhatWorkingDirectory() + File.separator +
                "bin" + File.separator +
                "TcpFlowClient" + sourceNode.WhatProcessor() + " " +
                destinationNode.WhatWifiIP() + " " + (TRAFFIC_PORT + number) + " " +
                record.getTransferSize() + " " + (record.getStop() - record.getStart()) + 
                " > " + sourceNode.WhatWorkingDirectory() + File.separator
                + "SalidaTcp" + (TRAFFIC_PORT + number) + " ";
        return sourceInstruction;
    }

    private String GenerateCategoryInstruction(TrafficRecord record, int number, String type) {
        String instruction = "";

      //  if(record.getIntAccessCategory() != 1) {
            Integer sourceNode = accessPoints.SearchAP(record.getSource());
            instruction = LocateIptables(sourceNode) + " -t mangle -" + type + " OUTPUT -p udp --dport " +
                    (TRAFFIC_PORT + number) + " -j DSCP --set-dscp-class " + record.getCSAccessCategory();
       // }

        return instruction;
    }

    private void GenerateCategoryInstructions() {
      /* COMMENTED AS UDP Generator supports native QoS but left
       * here intentionally.
       *
        TrafficRecord record = null;
        String categoryInstruction;

        categoryInstructions = new ArrayList<List<String>>();

        for (int i = 0; i < accessPoints.Size(); i++) {
            AddInstructionToNode(categoryInstructions, "#Category File", i);
        }

        for (int i = 0; i < accessPoints.GetTrafficSize(); i++) {
            record = (TrafficRecord) accessPoints.GetTraffic().get(i);

            if(record.getTCPUDP().equals("UDP") && !record.getDacme()) {
                int sourceNode  = accessPoints.SearchAP(record.getSource());
                categoryInstruction = GenerateCategoryInstruction(record, i, "A");
                AddInstructionToNodeRedirect(categoryInstructions, categoryInstruction, sourceNode);
            }
        }
        
        for (int i = 0; i < accessPoints.Size(); i++) {
                categoryInstruction =  "sleep " + GetSimulationTime();
                AddInstructionToNodeRedirect(categoryInstructions, categoryInstruction, i);
        }

        for (int i = 0; i < accessPoints.GetTrafficSize(); i++) {
            record = (TrafficRecord) accessPoints.GetTraffic().get(i);

            if(record.getTCPUDP().equals("UDP") && !record.getDacme()) {
                int sourceNode  = accessPoints.SearchAP(record.getSource());
                categoryInstruction = GenerateCategoryInstruction(record, i, "D");
                AddInstructionToNodeRedirect(categoryInstructions, categoryInstruction, sourceNode);
            }
       }*/
    }

    private String GenerateRedirectInstructionSource(TrafficRecord record, int number, String type) {
        String instruction = "";
        if(record.getRedirect()) {
            //TODO Modify APs to select control network (Ex. eth0, eth1...)
            //TODO Does only works whith one "ethX"
            String sourceNode =  accessPoints.Get(accessPoints.SearchAP(record.getAddress())).WhatEthIP();
            instruction = LocateIptables(number) + " -t nat -"+ type + " POSTROUTING -p udp --dport " +
                    (TRAFFIC_PORT + number) + " -j SNAT --to-source " + 
                    "$(/sbin/ifconfig $(/sbin/ifconfig | grep eth | awk '{print $1}') | grep inet: | awk '{print $2}'| cut -d\":\" -f2) ";
        }

        return instruction;
    }

    private String GenerateRedirectInstruction(TrafficRecord record, int number, String type) {
        String instruction = "";
        if(record.getRedirect()) {
            Integer destNode =  accessPoints.SearchAP(record.getAddress());
            String sourceNode =  accessPoints.Get(accessPoints.SearchAP(record.getSource())).WhatEthIP();
            instruction = LocateIptables(destNode) + " -t nat -"+ type + " PREROUTING -p udp --dport " +
                    (TRAFFIC_PORT + number) + " -j DNAT --to-destination " + sourceNode;
        }

        return instruction;
    }

    private void GenerateRedirectTrafficInstructions() {
        TrafficRecord record = null;
        String redirectInstruction;
        Vector<Boolean> redirect = new Vector<Boolean>();

        redirectInstructions = new ArrayList<List<String>>();
        
        for (int i = 0; i < accessPoints.Size(); i++) {
            redirect.add(false);
            AddInstructionToNode(redirectInstructions, "#Redirect File", i);
        }

        for (int i = 0; i < accessPoints.GetTrafficSize(); i++) {
            record = (TrafficRecord) accessPoints.GetTraffic().get(i);

            if(record.getRedirect() && record.getTCPUDP().equals("UDP")) {
                Integer destNode = accessPoints.SearchAP(record.getAddress());
                redirect.set(destNode, true);
                redirectInstruction = GenerateRedirectInstruction(record, i, "A");
                AddInstructionToNodeRedirect(redirectInstructions, redirectInstruction, destNode);
                redirectInstruction = GenerateRedirectInstructionSource(record, i, "A");
                AddInstructionToNodeRedirect(redirectInstructions, redirectInstruction, destNode);
            }
        }

     /*TODO Delete this code
      for (int i = 0; i < accessPoints.Size(); i++) {
            if(redirect.elementAt(i)) {
                redirectInstruction =  LocateIptables(i) + " -t nat -A POSTROUTING -p udp -j SNAT --to-source " +
                        accessPoints.Get(i).WhatEthIP();
                AddInstructionToNodeRedirect(redirectInstructions, redirectInstruction, i);
            }
        }*/

        for (int i = 0; i < accessPoints.Size(); i++) {
            redirect.set(i, false);
            redirectInstruction = "echo \"1\" > /proc/sys/net/ipv4/ip_forward";
            AddInstructionToNodeRedirect(redirectInstructions, redirectInstruction, i);
            redirectInstruction =  "sleep " + GetSimulationTime();
            AddInstructionToNodeRedirect(redirectInstructions, redirectInstruction, i);
        }

        for (int i = 0; i < accessPoints.GetTrafficSize(); i++) {
            record = (TrafficRecord) accessPoints.GetTraffic().get(i);

            if(record.getRedirect() && record.getTCPUDP().equals("UDP")) {
                Integer destNode = accessPoints.SearchAP(record.getAddress());
                redirect.set(destNode, true);
                redirectInstruction = GenerateRedirectInstruction(record, i, "D");
                AddInstructionToNodeRedirect(redirectInstructions, redirectInstruction, destNode);
                redirectInstruction = GenerateRedirectInstructionSource(record, i, "D");
                AddInstructionToNodeRedirect(redirectInstructions, redirectInstruction, destNode);
            }
        }

        /*TODO Delete this code
        for (int i = 0; i < accessPoints.Size(); i++) {
            if(redirect.elementAt(i)) {
                redirectInstruction =  LocateIptables(i) + " -t nat -D POSTROUTING -p udp -j SNAT --to-source " +
                        accessPoints.Get(i).WhatEthIP();
                AddInstructionToNodeRedirect(redirectInstructions, redirectInstruction, i);
            }
        }
*/

    }
    /**
     * Preparte the TTCP tool to behaviour like a client sending packets.
     * The traffic UDP is generated with the ttcp tool. This tool need one server
     * and one client. This method send the client instructions.
     */
    private void GenerateClientTrafficInstructions() {
        TrafficRecord record = null;
        Integer startNode;
        String sourceInstruction;
        String deleteInstruction;
        List<String> nodeInstructions;
        List<String> nodeDeleteInstructions;
        String trueInstruction;
        String trueDeleteInstruction;

        //By default, the instructions are empty.
        nodeInstructions = new ArrayList<String>();
        nodeDeleteInstructions = new ArrayList<String>();
        for (int i = 0; i < accessPoints.Size(); i++) {
            //nodeInstructions.add("\nsleep " + (TRAFFIC_SERVER_TIME_WAIT + PROTOCOL_TIME_WAIT) + " &");
            //nodeDeleteInstructions.add("\nsleep " + (TRAFFIC_SERVER_TIME_WAIT + PROTOCOL_TIME_WAIT) + " &");
            nodeInstructions.add("#Adding new traffic instructions.");
            nodeDeleteInstructions.add("#Deleting old traffic instructions.");
            clientLastTime.add(0);
            clientEndingUdpTime.add(0);
        }

        for (int i = 0; i < accessPoints.GetTrafficSize(); i++) {
            record = (TrafficRecord) accessPoints.GetTraffic().get(i);
            startNode = accessPoints.SearchAP(record.getSource());
            sourceInstruction = GenerateStartingTimeOfClientInstructions(record, startNode);
            deleteInstruction = GenerateEndingTimeOfClientUdpInstructions(record, startNode);

            //Dependind if the traffic is TCP or UDP
            if (record.getTCPUDP().equals("UDP")) {
                sourceInstruction = sourceInstruction + GenerateClientTrafficUdpInstuction(record, i);
                deleteInstruction = deleteInstruction + CreateDeletingUdpInstructions(record, i);
            } else {
                // Is defined the traffic like TCP.
                sourceInstruction = sourceInstruction + GenerateClientTrafficTcpInstuction(record, i);
                deleteInstruction = deleteInstruction + CreateDeletingTcpInstructions(record, i);
            }
            //Write the instrucciont to the client instructions list
            AP router = accessPoints.Get(startNode);
            sourceInstruction = nodeInstructions.get(startNode) + "& \n" + sourceInstruction;
            deleteInstruction = nodeDeleteInstructions.get(startNode) + "& \n" + deleteInstruction;
            nodeInstructions.set(startNode, sourceInstruction);
            nodeDeleteInstructions.set(startNode, deleteInstruction);

            //Save the last instruction starting/ending time. This time is used to calculate when the
            //next instruction will start/end.
            clientLastTime.add(startNode, record.getStart());
            clientEndingUdpTime.add(startNode, record.getStop());
        }

        //Adding the instructions to each node.
        for (int i = 0; i < accessPoints.Size(); i++) {
            trueInstruction = nodeInstructions.get(i).toString();
            trueDeleteInstruction = nodeDeleteInstructions.get(i).toString();
            if (!trueInstruction.equals("")) {
                AddInstructionToNode(clientInstructions, trueInstruction, i);
            }
            if (!trueDeleteInstruction.equals("")) {
                AddInstructionToNode(killUdpClientInstructions, trueDeleteInstruction, i);
            }
        }
    }

    /**
     * Cretate one UDP instruction for a server.
     * @param router The AP that act as a server.
     * @param number The number of the instruction.
     * @return The instruction in one string.
     */
    private String GenerateServerTrafficUDPInstruction(AP router, int number, int seconds) {
        String addressInstruction;
        int segundosEsperaServidor = GetRealSimulationTime() + TRAFFIC_SERVER_TIME_WAIT;
        addressInstruction = router.WhatWorkingDirectory() + File.separator +
                "bin" + File.separator +
                "UdpFlowServer" + router.WhatProcessor() + " " +
                (TRAFFIC_PORT + number) + " " + segundosEsperaServidor + " " +
                (seconds+1) + " " + WRITE_TIME_IN_FILE + " > " +
                router.WhatWorkingDirectory() + File.separator +
                STATISTICS_UDP_DESTINATION_FILE + (number + 1);
        return addressInstruction;
    }

    /**
     * Cretate one TCP instruction for a server.
     * @param router The AP that act as a server.
     * @param number The number of the instruction.
     * @return The instruction in one string.
     */
    private String GeneranteServerTrafficTCPInstruction(AP router, int number) {
        String addressInstruction;
        int segundosEsperaServidor = GetRealSimulationTime() + TRAFFIC_SERVER_TIME_WAIT;
        addressInstruction = router.WhatWorkingDirectory() + File.separator +
                "bin" + File.separator +
                "TcpFlowServer" + router.WhatProcessor() + " " +
                (TRAFFIC_PORT + number) + " " + segundosEsperaServidor + " " + WRITE_TIME_IN_FILE + " > " + router.WhatWorkingDirectory() +
                File.separator + STATISTICS_TCP_DESTINATION_FILE + (number + 1);
        return addressInstruction;
    }

    /**
     * Activate the program tcpdump in the desired node.
     * @param node
     */
    private String GenerateInstructionForReplay(Integer node) {
        String instructionReplay;
        instructionReplay = "NET_DEVICE=`NET=\\`ifconfig | grep " + accessPoints.Get(node).WhatWifiIP() +
                " -n | cut -d\":\" -f1 | head  -1\\` && " + "let NET=$NET-1 && ifconfig |  " +
                "head -$NET | tail -1 | cut -d\" \" -f1`" + " && /usr/sbin/tcpdump -w " +
                accessPoints.Get(node).WhatWorkingDirectory() + "tcpdump" + node +
                ".dump -i $NET_DEVICE & ";
        return instructionReplay;
    }

    /**
     * Preparte the TTCP tool to behaviour like a server.
     * The traffic UDP is generated with the ttcp tool. This tool need one server
     * and one client. This method send the server instructions to prepare an Ap
     * to receive packets.
     */
    private void GenerateServerTrafficInstructions() {
        TrafficRecord record = null;
        Integer nodo;
        AP router;
        String addressInstruction;
        List<String> nodeInstructions;
        String trueInstruction;
        String instructionReplay;

        //Empty instructions and add the default one.
        nodeInstructions = new ArrayList<String>();
        for (int i = 0; i < accessPoints.Size(); i++) {
            //No borrar el espacio!!!
            nodeInstructions.add(" ");
            serverLastTime.add(0);
        }

        //Generating the instruction.
        for (int i = 0; i < accessPoints.GetTrafficSize(); i++) {
            record = (TrafficRecord) accessPoints.GetTraffic().get(i);
            nodo = accessPoints.SearchAP(record.getAddress());
            router = accessPoints.Get(nodo);

            addressInstruction = GenerateStartingTimeOfServersInstructions(record, nodo);

            //Dependind if the traffic is TCP or UDP
            if (record.getTCPUDP().equals("UDP")) {
                if(record.getRedirect()) {
                    nodo = accessPoints.SearchAP(record.getSource());
                    router = accessPoints.Get(nodo);
                }
                addressInstruction = addressInstruction + GenerateServerTrafficUDPInstruction(router, i,
                            record.getStop() - record.getStart());
            } else {
                // Is defined the traffic like TCP.
                addressInstruction = addressInstruction + GeneranteServerTrafficTCPInstruction(router, i);
            }

            //Adding the new instruction.
            addressInstruction = nodeInstructions.get(nodo) + addressInstruction + " & \n";
            nodeInstructions.set(nodo, addressInstruction);

            //Store the last instruction starting time.
            serverLastTime.add(nodo, record.getStart());
        }

        //Adding all instruction to the node.
        for (int i = 0; i < accessPoints.Size(); i++) {
            trueInstruction = nodeInstructions.get(i).toString();
            if (replay) {
                //Adding net scan instruction only for wifi device.
                instructionReplay = GenerateInstructionForReplay(i);
                trueInstruction = instructionReplay + trueInstruction;
            }
            if (!trueInstruction.equals("")) {
                AddInstructionToNode(serverInstructions, trueInstruction, i);
            }
        }
    }

    /**
     * Generate all the traffic instructions needed and prepare it to send to the APs.
     * @see GenerateClientTrafficInstructions
     * @see GenerateServerTrafficInstructions
     */
    private void CreateTraffic() {
        GenerateCategoryInstructions();
        GenerateRedirectTrafficInstructions();
        GenerateClientTrafficInstructions();
        GenerateServerTrafficInstructions();
    }

    /**
     * Order the traffic vector in order of starting time of each instruction.
     */
    void OrderTrafficVector(Vector<TrafficRecord> traffic) {
        Vector<TrafficRecord> aux = new Vector<TrafficRecord>();
        TrafficRecord record;
        TrafficRecord recordAux = null;
        Integer firstRowValue;
        int firstRow = 0;

        Vector<TrafficRecord> newVector = new Vector<TrafficRecord>();
        VectorCopyTo(traffic, newVector, traffic.size() - 1);

        while (newVector.size() > 0) {
            firstRowValue = 10000000;
            for (int i = 0; i < newVector.size(); i++) {
                record = (TrafficRecord) newVector.get(i);
                if (record.getStart() < firstRowValue) {
                    firstRow = i;
                    firstRowValue = record.getStart();
                }
            }
            aux.add(newVector.get(firstRow));
            record = (TrafficRecord) newVector.get(firstRow);
            newVector.remove(firstRow);
        }
        UpdateTraffic(aux);
    }

    /**
     * Duplicate one row of traffic clonning other line.
     */
    void DuplicateTrafficRow(List<TrafficRecord> traffic, Integer row) {
        TrafficRecord recordAux;

        if (row > -1 && row < traffic.size() - 1) {
            traffic.remove(traffic.size() - 1);
            recordAux = (TrafficRecord) traffic.get(row);
            traffic.add(new TrafficRecord(
                    recordAux.getTCPUDP(), recordAux.getSource(),
                    recordAux.getAddress(), recordAux.getSize(),
                    recordAux.getStart(), recordAux.getStop(),
                    recordAux.getTransferSize(), recordAux.getPacketsSeconds(),
                    recordAux.getMaxPackets(), recordAux.getLastSpeed(),
                    recordAux.getPacketsReceived(), recordAux.getAccessCategory(),
                    recordAux.getDacme(), recordAux.getDelay(),
                    recordAux.getRedirect(), recordAux.getMeanDelay()
                    ));
        }
    }

    /**
     * Cancel all traffic sended to the APs.
     */
    private String StopTraffic() {
        String instruction = "/usr/bin/killall TcpFlowServerMIPS 1>&2; " +
                "/usr/bin/killall UdpFlowServerMIPS 1>&2; " +
                "/usr/bin/killall UdpFlowClientMIPS 1>&2; " +
                "/usr/bin/killall TcpFlowClientMIPS 1>&2; " +
                "/usr/bin/killall TcpFlowServerX86 1>&2; " +
                "/usr/bin/killall UdpFlowServerX86 1>&2; " +
                "/usr/bin/killall UdpFlowClientX86 1>&2; " +
                "/usr/bin/killall TcpFlowClientX86 1>&2; ";
        return instruction;
    }

    /**
     * Check if one traffic instruction has identical source and destination.
     * @return The line number where it happen.
     * @see TrafficRecord
     */
    int TrafficIdenticalSourceDestination() {
        TrafficRecord Linea;
        for (int i = 0; i < accessPoints.GetTrafficSize(); i++) {
            Linea = (TrafficRecord) accessPoints.GetTraffic().get(i);
            if (Linea.getAddress().equals(Linea.getSource())) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * Check the traffic table to controlate all the lines are written
     * in order.
     * @return The line number where it happen.
     * @see TrafficRecord
     */
    int TrafficCheckTimeOrder() {
        List<Integer> lastTime = new ArrayList<Integer>();
        TrafficRecord record = null;
        int startNode;

        for (int i = 0; i < accessPoints.Size(); i++) {
            lastTime.add(0);
        }
        for (int i = 0; i < accessPoints.GetTrafficSize(); i++) {
            record = (TrafficRecord) accessPoints.GetTraffic().get(i);
            startNode = accessPoints.SearchAP(record.getSource());
            if ((Integer) lastTime.get(startNode) > record.getStart()) {
                return i + 1;
            } else {
                lastTime.set(startNode, (Integer) record.getStart());
            }
        }
        return 0;
    }

    /**
     * Generate a list with all traffic in plain text.
     * @return A list with has every line of the traffic data.
     */
    private List ExportTrafficToText() {
        TrafficRecord record = null;
        String line = null;
        List<String> trafficPlainText = new ArrayList<String>();

        trafficPlainText.add("Line\tStrt\tStop\tSrce\tAddr\tTraff\t" +
                "Transf.\tSize\tPkt/sec\tPackt\t" +
                "Thrghpt\tReceived\tMean Delay\tAC\tDACME\tDelay\tRedirect");
        trafficPlainText.add("----------------------------------------" +
                "-----------------------------------------------------" +
                "-----------------------------------------------------");
        for (int i = 0; i < accessPoints.GetTrafficSize(); i++) {
            record = (TrafficRecord) accessPoints.GetTraffic().get(i);
            line = "";
            if (record.getTCPUDP().equals("UDP")) {
                trafficPlainText.add((i + 1) + "\t" + record.getStart() + "\t" + record.getStop() +
                        "\t" + record.getSource() + "\t" + record.getAddress() + "\t" +
                        record.getTCPUDP() + "\t \t" + record.getSize() + "\t" + record.getPacketsSeconds() + "\t" + record.getMaxPackets() + "\t" +
                        +record.getLastSpeed() + "\t" + record.getPacketsPerCentReceived()
                        + "\t" + record.getMeanDelay()
                        + "\t" + record.getAccessCategory() + "\t" + record.getDacme()
                        + "\t" + record.getDelay() + "\t" +record.getRedirect());
            } else {
                trafficPlainText.add((i + 1) + "\t" + record.getStart() + "\t" + record.getStop() +
                        "\t" + record.getSource() + "\t" + record.getAddress() +
                        "\t" + record.getTCPUDP() + "\t" + record.getTransferSize() + "\t \t \t" +
                        "\t" + record.getLastSpeed());
            }
        }
        trafficPlainText.add("\n\nTotal UDP packets received: " + udpAverage);
        trafficPlainText.add("Average throughput: " + throughputAverage);
        return trafficPlainText;
    }

    /**
     * The following can be used to generate gnuplot freindly text files.
     */
    private List customTraficToTextExport()
    {
        TrafficRecord record = null;
        String line = null;
        List<String> trafficPlainText = new ArrayList<String>();
        if (record.getTCPUDP().equals("UDP")) {
             trafficPlainText.add("# packets/sec throughput %received");
        }
        else
        {
             trafficPlainText.add("# transfer_size throughput");
        }
        for (int i = 0; i < accessPoints.GetTrafficSize(); i++) {
            record = (TrafficRecord) accessPoints.GetTraffic().get(i);
            line = "";
            if (record.getTCPUDP().equals("UDP")) {
                // We are going to display UDP packets/sec, throughput and received packets
                trafficPlainText.add(record.getPacketsSeconds()+" "+record.getLastSpeed()+" "+record.getPacketsPerCentReceived());
            } else {
                // We are going to display TCP transfer size and throughput
                trafficPlainText.add(record.getTransferSize()+" "+record.getLastSpeed());
            }
        }
        return trafficPlainText;
    }

    /**
     * Generate a file with all the traffic in plain text. Useful for printing.
     * @see ExportTrafficToText
     */
    void PrintTraffic(String file) {
        List trafficPlainText;
        //trafficPlainText = ExportTrafficToText();
        trafficPlainText = customTraficToTextExport();
        //ShowTraffic(trafficPlainText);
        SaveInFile(trafficPlainText, file);
    }

    /**
     * Update all data structures involved in the traffic.
     * @param vector The new traffic value.
     */
    void UpdateTraffic(Vector<TrafficRecord> vector) {
        accessPoints.SetTraffic(vector);
        tableModel.UpdateData(vector);
    }

    /**
     * Calculate the average TCP Throughput and the average UDP packet lost.
     */
    void ObtainAverageTraffic() {
        TrafficRecord record = null;
        Float totalPacketsLost = new Float(0);
        Integer totalUdp = 0;
        Float totalThroughput = new Float(0);

        for (int i = 0; i < accessPoints.GetTrafficSize(); i++) {
            record = (TrafficRecord) accessPoints.GetTraffic().get(i);
            totalThroughput += record.getLastSpeed();
            if (record.getTCPUDP().equals("UDP")) {
                totalPacketsLost += record.getPacketsPerCentReceived();
                totalUdp++;
            }
        }
        udpAverage = totalPacketsLost / totalUdp;
        throughputAverage = totalThroughput / accessPoints.GetTrafficSize();
        if (udpAverage.isNaN()) {
            udpAverage = new Float(0);
        }
        if (throughputAverage.isNaN()) {
            throughputAverage = new Float(0);
        }
    }

    /**
     * Return the path of the iptables binary of a determined node.
     */
    String LocateIptables(int node) {
        AP ap = accessPoints.Get(node);
        if (ap.WhatProcessor().equals("MIPS")) {
            return "/usr/sbin/iptables ";
        }
        if (ap.WhatProcessor().equals("X86")) {
            return "/sbin/iptables ";
        }
        return "";
    }

    /**
     * Return the path of the iwconfig binary of a determined node.
     */
    String LocateIwconfig(int node) {
        AP ap = accessPoints.Get(node);
        if (ap.WhatProcessor().equals("MIPS")) {
            return "/usr/sbin/iwconfig ";
        }
        if (ap.WhatProcessor().equals("X86")) {
            return "/sbin/iwconfig ";
        }
        return "";
    }

    
    /****************************************************************************
     *
     *                              EXTERNAL TRAFFIC
     *
     ****************************************************************************/
    /**
     * Check if exists an external traffic with this id.
     * @return the index of the equal traffic id.
     */
    private int ExistExternalTrafficWithThisName(String name) {
        ExternalTraffic externalTraffic;
        for (int i = 0; i < externalTrafficFlow.size(); i++) {
            externalTraffic = (ExternalTraffic) externalTrafficFlow.get(i);
            if (externalTraffic.name.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Add an external traffic flow to Castadiva.
     */
    public int AttachExternalTraffic(String tmp_startPort, String tmp_endPort,
            String tmp_fromIp, String tmp_toIp, int tmp_fromAp, String tmp_fromNet,
            int tmp_toAp, String tmp_toNet, String tmp_protocol, String tmp_id) {
        ExternalTraffic externalTraffic;


        try {
            externalTraffic = new ExternalTraffic(Integer.parseInt(tmp_startPort),
                    Integer.parseInt(tmp_endPort), tmp_fromIp, tmp_toIp,
                    tmp_fromAp, tmp_fromNet, tmp_toAp, tmp_toNet, tmp_protocol, tmp_id);
            int existIndex = ExistExternalTrafficWithThisName(tmp_id);
            if (existIndex < 0) {
                externalTrafficFlow.add(externalTraffic);
            } else {
                externalTrafficFlow.set(existIndex, externalTraffic);
            }
            return 0;
        } catch (NumberFormatException nfe) {
            if (debug) {
                System.out.println("Error inserting external traffic!");
            }
            return -1;
        }
    }

    /**
     * Return all instructions for one determined node to adapt it to external traffic.
     */
    private List<String> GenerateExternalTrafficInstructionsForOneNode(int node) {
        List<String> externalTrafficInstructionsForNode = new ArrayList<String>();
        if (debug) {
            System.out.println(" ** External Traffic: \n");
        }
        for (int i = 0; i < externalTrafficFlow.size(); i++) {
            ExternalTraffic externalTraffic = externalTrafficFlow.get(i);
            //Select the instructions of the desired node.

            /* Laptop 192.168.1.32 (in AP1) to laptop 192.168.1.30 (in AP5) */
            if (node == externalTraffic.fromAp) {
                /* /usr/sbin/iptables -t nat -A PREROUTING -p udp -s 192.168.1.32 
                --dport 5000:5099 -j DNAT --to-destination 192.168.2.5 & */
                String instruction1 = LocateIptables(externalTraffic.fromAp) +
                        " -t nat -A PREROUTING -p " + externalTraffic.protocol +
                        " -s " + externalTraffic.fromIp + " --dport " +
                        externalTraffic.startRangePort + ":" + externalTraffic.endRangePort +
                        " -j DNAT --to-destination " + accessPoints.Get(externalTraffic.toAp).WhatWifiIP() + " && ";
                externalTrafficInstructionsForNode.add(instruction1);

                /* /usr/sbin/iptables -t nat -A POSTROUTING -p udp -s 192.168.1.32 
                --dport 5000:5099 -j SNAT --to 192.168.2.1 */
                String instruction2 = LocateIptables(externalTraffic.fromAp) +
                        " -t nat -A POSTROUTING -p " + externalTraffic.protocol +
                        " -s " + externalTraffic.fromIp + " --dport " +
                        externalTraffic.startRangePort + ":" + externalTraffic.endRangePort +
                        " -j SNAT --to " + accessPoints.Get(externalTraffic.fromAp).WhatWifiIP() + " && ";
                externalTrafficInstructionsForNode.add(instruction2);

                /* /usr/sbin/iptables -t nat -A PREROUTING -p udp -s 192.168.2.5 
                --dport 5000:5099 -j DNAT --to-destination 192.168.1.32 && */
                String instruction3 = LocateIptables(externalTraffic.fromAp) +
                        " -t nat -A PREROUTING -p " + externalTraffic.protocol +
                        " -s " + accessPoints.Get(externalTraffic.toAp).WhatWifiIP() + " --dport " +
                        externalTraffic.startRangePort + ":" + externalTraffic.endRangePort +
                        " -j DNAT --to-destination " + externalTraffic.fromIp + " && ";
                externalTrafficInstructionsForNode.add(instruction3);

                /* /usr/sbin/iptables -t nat -A POSTROUTING -p udp -s 192.168.2.5 
                --dport 5000:5099 -j SNAT --to 192.168.1.30 */
                String instruction4 = LocateIptables(externalTraffic.fromAp) +
                        " -t nat -A POSTROUTING -p " + externalTraffic.protocol +
                        " -s " + accessPoints.Get(externalTraffic.toAp).WhatWifiIP() + " --dport " +
                        externalTraffic.startRangePort + ":" + externalTraffic.endRangePort +
                        " -j SNAT --to " + externalTraffic.toIp + " && ";
                externalTrafficInstructionsForNode.add(instruction4);

                if (debug) {
                    System.out.println(instruction1 + "\n" + instruction2 + "\n" + instruction3 + "\n" + instruction4);
                }
            }

            if (node == externalTraffic.toAp) {
                /* /usr/sbin/iptables -t nat -A PREROUTING -p udp -s 192.168.1.30 
                --dport 5000:5099 -j DNAT --to-destination 192.168.2.1 & */
                String instruction1 = LocateIptables(externalTraffic.toAp) +
                        " -t nat -A PREROUTING -p " + externalTraffic.protocol +
                        " -s " + externalTraffic.toIp + " --dport " +
                        externalTraffic.startRangePort + ":" + externalTraffic.endRangePort +
                        " -j DNAT --to-destination " + accessPoints.Get(externalTraffic.fromAp).WhatWifiIP() + " && ";
                externalTrafficInstructionsForNode.add(instruction1);

                /* /usr/sbin/iptables -t nat -A POSTROUTING -p udp -s 192.168.1.30 
                --dport 5000:5099 -j SNAT --to 192.168.2.5 & */
                String instruction2 = LocateIptables(externalTraffic.toAp) +
                        " -t nat -A POSTROUTING -p " + externalTraffic.protocol +
                        " -s " + externalTraffic.toIp + " --dport " +
                        externalTraffic.startRangePort + ":" + externalTraffic.endRangePort +
                        " -j SNAT --to " + accessPoints.Get(externalTraffic.toAp).WhatWifiIP() + " && ";
                externalTrafficInstructionsForNode.add(instruction2);

                /* /usr/sbin/iptables -t nat -A PREROUTING -p udp -s 192.168.2.1 
                --dport 5000:5099 -j DNAT --to-destination 192.168.1.30 & && */
                String instruction3 = LocateIptables(externalTraffic.toAp) +
                        " -t nat -A PREROUTING -p " + externalTraffic.protocol +
                        " -s " + accessPoints.Get(externalTraffic.fromAp).WhatWifiIP() + " --dport " +
                        externalTraffic.startRangePort + ":" + externalTraffic.endRangePort +
                        " -j DNAT --to-destination " + externalTraffic.toIp + " && ";
                externalTrafficInstructionsForNode.add(instruction3);

                /* /usr/sbin/iptables -t nat -A POSTROUTING -p udp -s 192.168.2.1 
                --dport 5000:5099 -j SNAT --to 192.168.1.32 */
                String instruction4 = LocateIptables(externalTraffic.toAp) +
                        " -t nat -A POSTROUTING -p " + externalTraffic.protocol +
                        " -s " + accessPoints.Get(externalTraffic.fromAp).WhatWifiIP() + " --dport " +
                        externalTraffic.startRangePort + ":" + externalTraffic.endRangePort +
                        " -j SNAT --to " + externalTraffic.fromIp + " && ";
                externalTrafficInstructionsForNode.add(instruction4);

                if (debug) {
                    System.out.println(instruction1 + "\n" + instruction2 + "\n" + instruction3 + "\n" + instruction4);
                }
            }
        }
        return externalTrafficInstructionsForNode;
    }

    /**
     * Obtain all instructions for all nodes to adapt them to external traffic.
     */
    private List<List<String>> GenerateExternalTrafficInstructions() {
        List<String> oneNodeInstructionsList;
        List<String> deleteoneNodeInstructionsList;
        List<List<String>> totalAllNodeInstructionsList = new ArrayList<List<String>>();
        for (int i = 0; i < accessPoints.Size(); i++) {
            oneNodeInstructionsList = GenerateExternalTrafficInstructionsForOneNode(i);
            deleteoneNodeInstructionsList = new ArrayList<String>();
            //Deleting all instructions when the simulation finish.
            for (int j = 0; j < oneNodeInstructionsList.size(); j++) {
                String oneNodeInstruction = oneNodeInstructionsList.get(j);
                //When the simulation is finished, delete the instructions.
                String deleteoneNodeInstruction = oneNodeInstruction.replace("-A", "-D");
                deleteoneNodeInstructionsList.add(deleteoneNodeInstruction);
            }
            //Waiting the time of the simulation.             
            oneNodeInstructionsList.add("\nsleep " + GetRealSimulationTime() +
                    "\n");
            oneNodeInstructionsList.addAll(deleteoneNodeInstructionsList);
            totalAllNodeInstructionsList.add(oneNodeInstructionsList);
        }

        return totalAllNodeInstructionsList;
    }

    void DeleteExternalTrafficInstruction(int index) {
        if (index < externalTrafficFlow.size() && index >= 0) {
            externalTrafficFlow.remove(index);
        }
    }

    /****************************************************************************
     *
     *                              ROUTING PROTOCOLS
     *
     ****************************************************************************/
    /**
     * Clean all Routing variables.
     */
    private void PrepareRoutingProtocol() {
        routingInstruction.removeAll(routingInstruction);
    }

    /**
     * Change the routing protocol used in the simulation.
     * @param protocolName A string containing the name of the protocol.
     */
    void ChangeRoutingProtocol(String protocolName) {
        routingProtocol = protocolName;
    }

    /**
     * Prepare Castadiva wtih all needed instructions to select a routing protocol.
     */
    private void GenerateRoutingInstructions() {
        DeactivateRoutingProtocol();
        ActivateRoutingProtocol();
    }

    /**
     * If you kill the olsrd in the Openwrt or stop the Optimum routing protocol,
     * some route entries persist into the device.
     * This function clean all route entries for the wifi device and generate
     * the default one.
     */
    private String CleanRoutingRules(AP node) {
        String script =
                "#Clean all Castadiva route entries.\n" +
                "i=$((`route -n| wc -l`+1));\n" +
                "while [ \"$i\" -gt \"3\" ] \n" +
                "do \n" +
                "i=$(($i-1));\n" +
                "net=`route -n | head -$i | tail -n 1 | awk '{print $1}'`\n" +
                "gw=`route -n | head -$i | tail -n 1 | awk '{print $2}'`\n" +
                "mask=`route -n | head -$i | tail -n 1 | awk '{print $3}'`\n" +
                "device=`route -n | head -$i | tail -n 1 | awk '{print $8}'`\n" +
                "if [ \"$device\" = \"" + node.WhatWifiDevice() + "\" ];then\n" +
                "route del -net $net netmask $mask\n" +
                "fi\n" +
                "done\n\n" +
                "#Add default entries.\n" +
                "route add -net " + ObtainNetTypeC(accessPoints.Get(1).WhatWifiIP()) +
                " netmask 255.255.255.0 dev " + node.WhatWifiDevice() + ";\n" +
                " route add default gw " + node.WhatGW() + ";\n";
        return script;
    }

    /**
     * Kill the old routing Protocol (if exists) running in the APs.
     */
    private void DeactivateRoutingProtocol() {
        String killAodv = "kill -9 `pidof aodv` 2>/dev/null";
        //String killOlsr ="kill -9 `pidof unik-olsr` 2>/dev/null";
        //String killOlsr ="killall olsrd 2>/dev/null";
        String killOptimum = deleteOptimumInstruction;
        String deleteInstruction = "";
        PROTOCOL_TIME_WAIT = 0;

        if (!routingProtocol.equals(lastRoutingProtocol)) {
            for (int i = 0; i < accessPoints.Size(); i++) {
                if (lastRoutingProtocol.equals("AODV")) {
                    deleteInstruction = killAodv;
                }
                if (lastRoutingProtocol.equals("Optimum")) {
                    deleteInstruction = killOptimum;
                }
                if (lastRoutingProtocol.equals("none")) {
                    deleteInstruction = "";
                }
                AddInstructionToNode(routingInstruction, deleteInstruction, i);
            }
        }
    }

    /**
     * Generate the instructions to send to the AP for activating the Selectioned protocol.
     * @return boolean value depending if the system must to change the protocol.
     */
    private void ActivateRoutingProtocol() {

        if (routingProtocol.equals("none")) {
            PROTOCOL_TIME_WAIT = 0;
        } else if (routingProtocol.equals("Optimum")) {
            GenerateOptimumInstructions();
        } else {
            GeneratePluginProtocolInstructions();
        }
    }

    /**
     * When a custom routing plugin is used, the following finds out among every
     * custom routing protocol which one must be used.
     * Then, it processes the requested actions for a custom routing plugin,
     * generating instructions and storing them in the routingInstructions
     * global variable.
     * Later on in the simulation, the routingInstructions variables is written
     * in the routingForNodes.sh file (see StoreAllInstructionsForOneNode).
     * @author nacho wannes
     * @see createRoutingPluginListener StoreAllInstructionsForOneNode pluginDetector
     */
    private void GeneratePluginProtocolInstructions() {

        // When the program is started, a plugin detection system checks for
        // custom plugin files (.jar). The following allors to get the result of
        // the plugin detection
        routing_protocols = detector.getProtocolPlugins();
        String protocolInstructions;

        if (routing_protocols.length > 0) {
            for (IPluginCastadiva a : routing_protocols) {
                if (a.getClass().getSimpleName().equals(routingProtocol)) {
                    PROTOCOL_TIME_WAIT = 2;

                    // The configuration file for the protocol is copied to the
                    // local nfs folder with the correct filename
                    try {
                        // Locate and open the jar file
                        JarFile jar = new JarFile(CastadivaModel.PLUGIN_JAR_FOLDER+"/"+a.getClass().getSimpleName()+".jar");
                        ZipEntry entry = jar.getEntry(a.getConf());

                        // Get the configuration file's BufferedReader
                        BufferedReader confFileReader = new BufferedReader(new InputStreamReader(jar.getInputStream(entry)));
                        // Get the destination file's buffered writer
                        BufferedWriter confFileWriter = new BufferedWriter(new FileWriter(computer.WhatWorkingDirectory()+"/"+a.getConf()));

                        // Transfer the content of the first one into the second one
                        String confFileLine;
                        while((confFileLine = confFileReader.readLine()) != null){
                              confFileWriter.write(confFileLine+"\n");
                        }
                        // Close the two files
                        confFileWriter.close();
                        confFileReader.close();
                    } catch (Exception e) {
                        System.out.println("Unable to copy the configuration file from "+CastadivaModel.PLUGIN_JAR_FOLDER + a.getClass().getSimpleName() + ".conf to " + computer.WhatWorkingDirectory() + "/" + a.getClass().getSimpleName() + ".conf  : "+e);
                    }

                    // On every router, the previously copied configuration file
                    // is now copied from the nfs folder to its final location
                    for (int i = 0; i < accessPoints.Size(); i++) {
                        
                        protocolInstructions = "";
                        // The configuration path is copied through NFS to the remote location
                        protocolInstructions ="cp "+accessPoints.Get(i).WhatWorkingDirectory()+"/"+a.getConf()+" "+a.getPathConf()+"\n";
                        
                        // The routing instructions can now be set.
                        // After a sleep time, the script automatically reverses
                        // its changes.
                        protocolInstructions+=
                                "#Start routing protocol.\n" +
                                a.getBin() +" "+a.getFlags()+"\n\n"+
                                "#Wait for the end of simulation.\n" +
                                "sleep " + GetRealSimulationTime() + "\n\n" +
                                "#Kill the protocol\n" +
                                a.getKillInstruction()+ "\n\n" +
                                "#Clean protocol configuration file\n"+
                                "rm "+a.getPathConf()+"\n";

                        SetInstructionToNode(routingInstruction, protocolInstructions, i);
                     }
                }
            }
        } else {
            System.out.println("No plugin found");
        }
    }
    

    /**
     * Generate all specific instructions for activating the AODV protocol.
     */
    private void GenerateAodvInstructions() {
        PROTOCOL_TIME_WAIT = 30;
        String protocolInstruction = "";

        for (int i = 0; i < accessPoints.Size(); i++) {
            protocolInstruction = accessPoints.Get(i).WhatWorkingDirectory() +
                    File.separator + "aodvd.sh " + accessPoints.Get(i).WhatProcessor() + " " + accessPoints.Get(i).WhatWorkingDirectory() + " \n";
            protocolInstruction = protocolInstruction + "; sleep " + GetRealSimulationTime() + ";\n" + "/sur/bin/kill -9 `pidof aodv` 2>/dev/null";
            SetInstructionToNode(routingInstruction, protocolInstruction, i);
        }
    }

    /**
     * Obtain all instructions to use the Optimum protocol without movement.
     */
    private void GenerateStaticOptimumInstructions() {
        String protocolInstruction = "";
        List<String> delList;

        PROTOCOL_TIME_WAIT = 2;
        Gateways();
        for (int i = 0; i < accessPoints.Size(); i++) {
            delList = new ArrayList<String>();
            for (int j = 0; j < accessPoints.Size(); j++) {
                //I.E. route add -host 192.168.1.2 gw 192.168.1.1 eth1
                if(i!=j && gatewayMatrix[i][j] != -1) {
                protocolInstruction = "route add -host " +
                        accessPoints.Get(j).WhatWifiIP() + " gw " +
                        accessPoints.Get(gatewayMatrix[i][j]).WhatWifiIP() + " " +
                        accessPoints.Get(i).WhatWifiDevice();
                SetInstructionToNode(routingInstruction, protocolInstruction, i);
                delList.add(protocolInstruction);
                }
            }
            SetInstructionToNode(routingInstruction, "sleep " + GetSimulationTime(), i);
            //Generating the deletion of the routing
            deleteOptimumInstruction = "";
            for (int j = delList.size(); j > 0; j--) {
                protocolInstruction = (String) delList.get(j - 1);
                protocolInstruction = Replace(protocolInstruction, "add", "del");
                deleteOptimumInstruction = deleteOptimumInstruction + protocolInstruction + " & ";
                SetInstructionToNode(routingInstruction, protocolInstruction, i);
            }
        }
    }

    /**
     * Obtain all instructions to use the Optimum protocol with mobility.
     */
    private void GenerateMobilityOptimumInstrucions() {
        String protocolInstruction = "", deleteInstruction = "", totalInstruction = "";
        Integer oldGatewayMatrix[][];
        Integer sleepTime[], elapsedTime[];

        PROTOCOL_TIME_WAIT = 2;
        oldGatewayMatrix = GenerateMatrix(accessPoints.Size(), accessPoints.Size(), 0);
        sleepTime = GenerateVector(accessPoints.Size(), 0);
        elapsedTime = GenerateVector(accessPoints.Size(), 0);
        for (int k = 0; k < GetSimulationTime(); k++) {
            if (k > 0) {
                oldGatewayMatrix = gatewayMatrix.clone();
            }
            Gateways(k);
            for (int i = 0; i < accessPoints.Size(); i++) {
                totalInstruction = "";
                for (int j = 0; j < accessPoints.Size(); j++) {
                    if (oldGatewayMatrix[i][j] != gatewayMatrix[i][j]) {
                        protocolInstruction = "";
                        deleteInstruction = "";
                        //If there is a new gateway.
                        if (gatewayMatrix[i][j] != 0) {
                            protocolInstruction = "route add -host " +
                                    accessPoints.Get(j).WhatWifiIP() + " gw " +
                                    accessPoints.Get(gatewayMatrix[i][j]).WhatWifiIP() + " " +
                                    accessPoints.Get(i).WhatWifiDevice() + "\n";
                        }
                        //If is necessary, delete the old one.
                        if (oldGatewayMatrix[i][j] != 0) {
                            deleteInstruction = "route del -host " +
                                    accessPoints.Get(j).WhatWifiIP() + " gw " +
                                    accessPoints.Get(oldGatewayMatrix[i][j]).WhatWifiIP() + " " +
                                    accessPoints.Get(i).WhatWifiDevice() + "\n";
                        }
                        if (sleepTime[i] != 0) {
                            totalInstruction = "sleep " + sleepTime[i] + "\n";
                        }
                        totalInstruction = totalInstruction + deleteInstruction +
                                protocolInstruction;
                        //If an instruction is added, reset the waiting time.
                        if (oldGatewayMatrix[i][j] != 0 || gatewayMatrix[i][j] != 0) {
                            elapsedTime[i] += sleepTime[i];
                            sleepTime[i] = 0;
                        }
                    }
                    //We prepare the sleep for the next second.
                    if (j == accessPoints.Size() - 1) {
                        sleepTime[i]++;
                    }
                }
                if (!totalInstruction.equals("")) {
                    SetInstructionToNode(routingInstruction, totalInstruction, i);
                }
            }
        }
        //Deleting all remaining routing instructions.
        for (int i = 0; i < accessPoints.Size(); i++) {
            SetInstructionToNode(routingInstruction, "sleep " + (GetRealSimulationTime() - elapsedTime[i]), i);
            SetInstructionToNode(routingInstruction, CleanRoutingRules(accessPoints.Get(i)), i);
        }

    }

    /**
     * Generate all instructions to define the routing in the network without
     * routing protocol.
     */
    private void GenerateOptimumInstructions() {
        if (MobiliyActivated()) {
            GenerateMobilityOptimumInstrucions();
        } else {
            GenerateStaticOptimumInstructions();
        }
    }

    /**
     * Show a matrix with the nearest node to send a packet to reach anotherone.
     * Columns and rows are the nodes and the value is the gateway. I.E: To reach
     * Y node from X node, see the position X,Y and see the gateway of X.
     */
    public void Gateways() {
        gatewayMatrix = new Integer[accessPoints.Size()][accessPoints.Size()];
        Integer tree[] = new Integer[accessPoints.Size()];
        Integer gateways[] = new Integer[accessPoints.Size()];

        //Set gateway matrix to 0
        for (int i = 0; i < accessPoints.Size(); i++) {
            for (int j = 0; j < accessPoints.Size(); j++) {
                gatewayMatrix[i][j] = 0;
            }
        }

        for (int i = 0; i < accessPoints.Size(); i++) {
            //Calculate the tree of this node.
            tree = TreeGenerator(i);
            gateways = GatewayOfOneNode(tree, i);

            for (int j = 0; j < accessPoints.Size(); j++) {
                gatewayMatrix[i][j] = gateways[j];
            }
        }
    }

    /**
     * Show a matrix with the nearest node to send a packet to reach anotherone.
     * Columns and rows are the nodes and the value is the gateway. I.E: To reach
     * Y node from X node, see the position X,Y and see the gateway of X.
     */
    public void Gateways(Integer second) {
        gatewayMatrix = new Integer[accessPoints.Size()][accessPoints.Size()];
        Integer tree[] = new Integer[accessPoints.Size()];
        Integer gateways[] = new Integer[accessPoints.Size()];

        //Set gateway matrix to 0
        for (int j = 0; j < accessPoints.Size(); j++) {
            for (int i = 0; i < accessPoints.Size(); i++) {
                gatewayMatrix[i][j] = 0;
            }
        }

        for (int i = 0; i < accessPoints.Size(); i++) {
            //Calculate the tree of this node.
            tree = TreeGenerator(i, second);
            gateways = GatewayOfOneNode(tree, i);

            for (int j = 0; j < accessPoints.Size(); j++) {
                gatewayMatrix[i][j] = gateways[j];
            }
        }
    }

    /**
     * Obtain the gateways of one node.
     */
    public Integer[] GatewayOfOneNode(Integer[] tree, Integer node) {
        Integer gateways[] = new Integer[accessPoints.Size()];
        Integer prevNode;
        Integer nextNode;

        for (int i = 0; i < accessPoints.Size(); i++) {
            prevNode = i;
            nextNode = tree[i];

            /* OLD_CODE Think does not work properly
             if (tree[i] == node) {
                gateways[i] = i;
            } else {
                //Searching the next hop in the routing.
                while (nextNode != node) {
                    if (tree[prevNode] == 0) {
                        break;
                    }
                    prevNode = tree[prevNode];
                    nextNode = tree[prevNode];
                }
                gateways[i] = prevNode;
            }*/

            //BEGIN NEW CODE
            while(nextNode != node) {
                if(tree[prevNode] == 0) {
                    break;
                }
                prevNode = nextNode;
                nextNode = tree[prevNode];
            }
            gateways[i] = prevNode;
            //END NEW CODE
            
        }
        System.out.println("Gateways " + accessPoints.Get(node).WhatAP());
        printIntegerVector(gateways);
        return gateways;
    }


    public void printIntegerVector(Object v[]) {
        for(int i = 0; i < v.length; i++) {
            System.out.print(v[i].toString() + " ");
        }
    }
    /**
     * Create a tree of a graph with a determinated root.
     */
    public Integer[] TreeGenerator(Integer node) {
        Integer nodeUsed;
        Integer visited[] = new Integer[accessPoints.Size()];
        List<Integer> next = new ArrayList<Integer>();
        Integer tree[] = new Integer[accessPoints.Size()];

        for (int i = 0; i < accessPoints.Size(); i++) {
            tree[i] = 0;
            visited[i] = 0;
        }

        visited[node] = 1;
        tree[node] = node;
        next.add(node);

        while (next.size() > 0) {
            nodeUsed = (Integer) next.get(0);
            next.remove(0);
            //System.out.println("Node Used " + accessPoints.Get(nodeUsed).WhatAP());
            for (int i = 0; i < accessPoints.Size(); i++) {
                //This node reach other node.
                if (visited[i] == 0 && visibilityMatrix[nodeUsed][i] > 0) //It is not reached yet
                {
                        //System.out.println("Views " + accessPoints.Get(i).WhatAP());
                        //Add to the list.
                        next.add(i);
                        visited[i] = 1;
                        tree[i] = nodeUsed;
                }
            }
        }
        printIntegerVector(tree);
        /*System.out.println("Visibility Matrix");
        for(int i = 0; i < visibilityMatrix.length; i++){
            for(int j = 0; j < visibilityMatrix[i].length; j++){
                System.out.print(visibilityMatrix[i][j] + " ");
            }
            System.out.println();
        }*/
        return tree;
    }

    /**
     * Create a tree of a graph with a determinated root.
     */
    public Integer[] TreeGenerator(Integer node, Integer second) {
        Integer nodeUsed;
        Integer visited[] = new Integer[accessPoints.Size()];
        List<Integer> next = new ArrayList<Integer>();
        Integer tree[] = new Integer[accessPoints.Size()];

        for (int i = 0; i < accessPoints.Size(); i++) {
            tree[i] = 0;
            visited[i] = 0;
        }

        visited[node] = 1;
        tree[node] = node;
        next.add(node);

        visibilityMatrix = GenerateMobilityVisibilityMatrix(second);
        while (next.size() > 0) {
            nodeUsed = (Integer) next.get(0);
            next.remove(0);
            for (int i = 0; i < accessPoints.Size(); i++) {
                //This node reach other node.
                if (visited[i] == 0 && visibilityMatrix[nodeUsed][i] > 0) //It is not reached yet
                {
                        //Add to the list.
                        next.add(i);
                        visited[i] = 1;
                        tree[i] = nodeUsed;
                }
            }
        }
        return tree;
    }

    /****************************************************************************
     *
     *                        MOBILITY SIMULATION FUNCTIONS
     *
     ****************************************************************************/
    /**
     * Return the distance in metres between two nodes.
     * @param node1 The firt node to calculate the distance.
     * @param node2 The second node.
     */
    private float CalculateCheckPointDistance(NodeCheckPoint node1, NodeCheckPoint node2) {
        return (float) Math.sqrt(Math.pow((node1.xCoordinate - node2.xCoordinate), 2) +
                Math.pow((node1.yCoordinate - node2.yCoordinate), 2) +
                Math.pow((node1.zCoordinate - node2.zCoordinate), 2));
    }

    /**
     * Generate the starting visibility instructions for each node at the start of the simulaton.
     * @param markInsertedInstructions Store if exists an iptable rule for a later elimination.
     */
    private float[][] StartingVisibilityInstructionsForMobility(boolean markInsertedInstructions[][]) {
        //float startingVisibilityMatrix[][];

        //Create starting instructions. These instructions will be stored in addIntructions list.
        visibilityMatrix = GenerateVisibilityMatrix();
        if (debug) {
            System.out.println("Starting nodes distance:");
            PrintMatrix(visibilityMatrix);
        }
        AddNodeVisibility(false);

        //Mark all starting instructions to be deleted at the end of simulation.
        for (int i = 0; i < accessPoints.Size(); i++) {
            for (int j = 0; j < accessPoints.Size(); j++) {
                if (visibilityMatrix[i][j] == -1) {
                    markInsertedInstructions[i][j] = true;
                } else {
                    markInsertedInstructions[i][j] = false;
                }
            }
        }
        return visibilityMatrix;
    }

    /**
     * Generate the rules to prevent an AP to see another according to the simulation.
     */
    private void AddNodeVisibilityWithMobility() {
        String nodeInstructions;
        String delNodeInstructions;
        float oldVisibilityMatrix[][];
        boolean deleteIptable[][];
        List<String> temporalInstructions = new ArrayList<String>();

        //Delete all visibility instructions when the simulation ends.
        deleteIptable = GenerateMatrix(accessPoints.Size(), accessPoints.Size(), false);
        oldVisibilityMatrix = StartingVisibilityInstructionsForMobility(deleteIptable);

        sleep = GenerateVector(accessPoints.Size(), 0);
        //Inicialize instructions.
        for (int p = 0; p < accessPoints.Size(); p++) {
            temporalInstructions.add("#Changes into the visibility on simulation time.\n");
        }

        //Create future instructions.
        for (int k = 0; k < GetSimulationTime(); k++) {
            if (k > 0) {
                oldVisibilityMatrix = visibilityMatrix.clone();
            }
            visibilityMatrix = GenerateMobilityVisibilityMatrix(k);

            for (int i = 0; i < accessPoints.Size(); i++) {
                nodeInstructions = (String) temporalInstructions.get(i);

                //Adding the visibility rules.
                for (int j = 0; j < accessPoints.Size(); j++) {
                    AP nodo2 = accessPoints.Get(j);
                    //The nodes not change the visibility.
                    if (!((oldVisibilityMatrix[i][j] >= 0 && visibilityMatrix[i][j] >= 0) || (oldVisibilityMatrix[i][j] < 0 && visibilityMatrix[i][j] < 0))) {
                        //If one node gets out of range.
                        if (visibilityMatrix[i][j] < 0) {
                            nodeInstructions = nodeInstructions + "sleep " + sleep[i] +
                                    "\n" + LocateIptables(i) +
                                    " -I INPUT -m mac --mac-source " + nodo2.WhatWifiMac() +
                                    " -j DROP\n" +
                                    LocateIptables(i) +
                                    " -I FORWARD -m mac --mac-source " + nodo2.WhatWifiMac() +
                                    " -j DROP\n";
                            deleteIptable[i][j] = true;
                        }
                        //If one node gets in range.
                        if (visibilityMatrix[i][j] > 0) {
                            nodeInstructions = nodeInstructions + "sleep " + sleep[i] +
                                    "\n" + LocateIptables(i) +
                                    " -D INPUT -m mac --mac-source " + nodo2.WhatWifiMac() +
                                    " -j DROP\n" +
                                    LocateIptables(i) +
                                    " -D FORWARD -m mac --mac-source " + nodo2.WhatWifiMac() +
                                    " -j DROP\n";
                            deleteIptable[i][j] = false;
                        }
                        if (visibilityMatrix[i][j] != 0) {
                            sleep[i] = 0;
                        }
                    }
                    //Only increment sleep if Castadiva compruebes the last node.
                    if (j == accessPoints.Size() - 1) {
                        sleep[i]++;
                    }
                }
                temporalInstructions.set(i, nodeInstructions);
            }
        }

        //Delete at the end of the simulation all rules.
        for (int z = 0; z < accessPoints.Size(); z++) {
            String allInstructions = (String) temporalInstructions.get(z);
            //Ensure that all iptables are deleted.
            delNodeInstructions = "";
            for (int l = 0; l < accessPoints.Size(); l++) {
                if (deleteIptable[z][l]) {
                    AP nodo = accessPoints.Get(l);
                    delNodeInstructions = delNodeInstructions + LocateIptables(z) +
                            " -D INPUT -m mac --mac-source " + nodo.WhatWifiMac() +
                            " -j DROP\n" +
                            LocateIptables(z) +
                            " -D FORWARD -m mac --mac-source " + nodo.WhatWifiMac() +
                            " -j DROP\n";
                }
            }
            allInstructions = allInstructions + "\n#Delete remaining visibility " +
                    "instructions at the end of the simulation.\nsleep " +
                    (GetRealSimulationTime() + VISIBILITY_TIME_WAIT) + "\n" + delNodeInstructions;
            String aux = (String) addInstruction.get(z);
            aux = aux + "\n" + allInstructions;
            addInstruction.set(z, aux);
        }
    }

    /**
     * Call to all methods to obtain the node visibility between APs.
     */
    private void CalculateNewNodeVisibilityWithMobility() {
        sleep = new Integer[accessPoints.Size()];
        DeleteOldVisibilityInstructions();
        AddNodeVisibilityWithMobility();
    }

    /**
     * Start a new simulation.
     */
    private void StartMobilitySimulation() {
        CalculateNewNodeVisibilityWithMobility();
        //Delete the old simulation IPtables instructions.
        instructions = MergeInstructionsList(removeInstruction, addInstruction);
        CreateTraffic();
        externalTrafficInstructions = GenerateExternalTrafficInstructions();
        GenerateRoutingInstructions();
    }

    /**
     * Start the mobility of all simulation.
     */
    public void StartMobility() {
        if (MobiliyActivated()) {
            ObtainNodePositionsForEntireSimulation();
            StartMobilitySimulation();
        }
    }

    /**
     * Start the mobility of all simulation.
     */
    public void ReplayMobility() {
        PositionateNodesInDeterminedSecond(0);
        if (MobiliyActivated()) {
            if (debug) {
                System.out.println("\nReplaing simulation with mobility...");
            }
            StartMobilitySimulation();
        }
    }

      
    /**
     * When mobility is used, the following function is called. First, it will
     * try to find out which mobility model was choses. If it was not RANDOM WAY
     * POINT or CITYMOB, it will search among the available mobility plugins
     * As a resut this function, the global variable nodePositions will be set.
     * @author Nacho Wannes
     * @see CastadivaController.generatePluginListener() PluginDetector
     *
     */
    private void ObtainNodePositionsForEntireSimulation() {
        // Check if the RANDOM WAY POINT model was asked
        if (mobilityModel.equals("RANDOM WAY POINT")) {
            nodePositions = new NodeCheckPoint[accessPoints.Size()][GetSimulationTime() + 1];
            allAddresses = new MobilityVectors(this, accessPoints, minSpeed, maxSpeed, pause);
            allAddresses.ObtainNodePositionsForEntireSimulation(accessPoints);
        }
        // Check if CITIMOB was asked
        else if (mobilityModel.equals("CITYMOB")) {
            //With citymob, nodePositions is is already set
        }
        // If the two previous options failed, it looks for a plugin
        else {
            // Recuperation of the mobility plugins
            mob_plugins = detector.getMobilityPlugins();
            for (IMobilityPluginCastadiva plugin : mob_plugins) {
                // Each mobility plugin's name is compared to the selected plugin
                if (plugin.getClass().getSimpleName().equals(mobilityModel)) {
                    // Initialization of the nodePositions variable
                    nodePositions = new NodeCheckPoint[accessPoints.Size()][GetSimulationTime() + 1];
                    allAddresses = new MobilityVectors(this, accessPoints, minSpeed, maxSpeed, pause);
                    if(debug)
                    {
                        System.out.println("Mobility plugin "+plugin.getClass().getSimpleName()+" is being used");
                    }
                    // The following calls the user specified function
                    plugin.ObtainNodePositionsForEntireSimulation(nodePositions, accessPoints, minSpeed, maxSpeed, pause, totalTime, x, y);
                    break;
                }
            }
        }
    }

    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Extends NodeMovent to use more simulation time.
     */
    public void ExtendNodePositions() {
        try {
            if (nodePositions[0].length < GetSimulationTime() + 1) {
                NodeCheckPoint[][] extendedNodePositions = new NodeCheckPoint[accessPoints.Size()][GetSimulationTime() + 1];
                //Copy the old movemnts.
                for (int i = 0; i < nodePositions.length; i++) {
                    for (int j = 0; j < nodePositions[0].length; j++) {
                        extendedNodePositions[i][j] = nodePositions[i][j];
                    }
                }
                //Calculate new movemnts with the old movements and an inertia.
                if (nodePositions[0].length > 1) {
                    for (int i = 0; i < nodePositions.length; i++) {
                        NodeCheckPoint pos1, pos2;

                        for (int j = nodePositions[i].length; j < extendedNodePositions[i].length; j++) {
                            pos2 = extendedNodePositions[i][j - 1];
                            pos1 = extendedNodePositions[i][j - 2];

                            Float xInertia = pos2.xCoordinate - pos1.xCoordinate;
                            Float yInertia = pos2.yCoordinate - pos1.yCoordinate;
                            Float zInertia = pos2.zCoordinate - pos1.zCoordinate;
                            Float newX = pos2.xCoordinate + xInertia;
                            if (newX < 0) {
                                newX = new Float(0);
                            }
                            if (newX > WhatBoundX()) {
                                newX = WhatBoundX();
                            }
                            Float newY = pos2.yCoordinate + yInertia;
                            if (newY < 0) {
                                newY = new Float(0);
                            }
                            if (newY > WhatBoundY()) {
                                newY = WhatBoundY();
                            }
                            Float newZ = pos2.zCoordinate + zInertia;
                            if (newZ < 0) {
                                newZ = new Float(0);
                            }

                            NodeCheckPoint newPosition = new NodeCheckPoint(newX, newY, newZ);
                            extendedNodePositions[i][j] = newPosition;
                        }
                    }
                    //Copy the new movement.
                    nodePositions = extendedNodePositions;
                }
            }
        } catch (NullPointerException npe) {
        }
    }

    /**
     * Positionate all nodes in a determined second with the calculated checkpoints.
     */
    public void PositionateNodesInDeterminedSecond(Integer second) {
        for (int i = 0; i < accessPoints.Size(); i++) {
            AP node = accessPoints.Get(i);
            try {
                NodeCheckPoint point = nodePositions[i][second];
                node.x = point.xCoordinate;
                node.y = point.yCoordinate;
                node.z = point.zCoordinate;
            } catch (NullPointerException e) {
                System.out.println("ERROR : Position is not defined for node "+i+" at simulation time "+second);
            }
            if(this.debug){
                // In debug mode, position of each node (Or one node) is printed out along with the corresponding simulation time
                // This was introduced by Wannes in order to check the position processing. It can be removed at any time.
                if(true)// Put i = x to only display one node
                {
                    System.out.format("Mobility : At second %d node %d should be at position : (%.1f, %.1f, %.1f)%n",second,i, node.x, node.y, node.z);
                }
            }
        }
        GenerateMobilityVisibilityMatrix(second);
    }

    /**
     * Store the position of one node in determined second.
     */
    public void StoreNodePosition(Integer nodeNumber, Integer second, NodeCheckPoint checkPoint) {
        nodePositions[nodeNumber][second] = checkPoint;
    }

    /****************************************************************************
     *
     *                          RANDOM SIMULATION FUNCTIONS
     *
     ****************************************************************************/
    /**
     * Generate a randomly position for all defined nodes.
     */
    public void SetRandomScenery() {
        AP nodo;
        Double aux;

        for (int i = 0; i < accessPoints.Size(); i++) {
            nodo = accessPoints.Get(i);
            if (randomSimulating &&
                    i >= minNodes + iteration * granularity) {
                //Send far of the network the nodes not used
                //into the simulation.
                nodo.x = -1000000;
                nodo.y = -1000000;
            } else {
                aux = (randomGenerator.nextDouble() * x);
                nodo.x = aux.floatValue();
                aux = (randomGenerator.nextDouble() * y);
                nodo.y = aux.floatValue();
            }
            accessPoints.Set(i, nodo);
        }
    }

    /**
     * Update all data structures involved in the traffic.
     * @param vector The new traffic value.
     */
    void UpdateRandomTraffic(Vector<RandomTrafficRecord> vector) {
        accessPoints.SetRandomTraffic(vector);
        randomTrafficModel.UpdateData(vector);
    }

    /**
     * Save all data files (scenary/traffic) of a random simulation.
     * @see ExportNsScenario
     * @see SaveCastadivaAps
     * @see PrintTraffic
     */
    void SaveRandomSimulation(String path) {
        String scenary;
        String directory;

        scenary = (minNodes + granularity * iteration) + "nodes_" + loop + "loop" + "-" + routingProtocol;
        directory = "Nodes:" + minNodes + "-" + maxNodes + "in" +
                +x + "x" + y;
        File newDirectory = new java.io.File(path + File.separator + directory);
        newDirectory.mkdir();
        if (fileRandomScenaryFormat.equals("NS-2")) {
            ExportNsScenario(path + File.separator + directory +
                    File.separator + scenary + ".ns", maxNodes);
            ExportNsTraffic(path + File.separator + directory +
                    File.separator + scenary + "_Traffic.ns");
            PrintTraffic(path + File.separator + directory +
                    File.separator + scenary + "_Traffic.txt");
        }
        if (fileRandomScenaryFormat.equals("Castadiva")) {
            SaveCastadivaAps(path + File.separator + directory, scenary + ".dat");
            PrintTraffic(path + File.separator + directory + File.separator + scenary + "Traffic.txt");
        }
    }

    /**
     * Duplicate one row of the random traffic clonning other line.
     */
    void DuplicateRandomTrafficRow(List<RandomTrafficRecord> traffic, Integer row) {
        RandomTrafficRecord recordAux;

        if (row > -1 && row < traffic.size() - 1) {
            traffic.remove(traffic.size() - 1);
            recordAux = (RandomTrafficRecord) traffic.get(row);
            traffic.add(new RandomTrafficRecord(
                    recordAux.getTCPUDP(), recordAux.getSize(),
                    recordAux.getStart(), recordAux.getStop(),
                    recordAux.getTransferSize(), recordAux.getPacketsSeconds(),
                    recordAux.getFlows()));
        }
    }

    /**
     * Order the random traffic vector in order of starting time of each instruction.
     */
    public void OrderRandomTrafficVector(Vector traffic) {
        Vector<RandomTrafficRecord> aux = new Vector<RandomTrafficRecord>();
        RandomTrafficRecord record;
        RandomTrafficRecord recordAux = null;
        Integer firstRowValue;
        int firstRow = 0;

        Vector<RandomTrafficRecord> newVector = new Vector<RandomTrafficRecord>();
        VectorCopyTo(traffic, newVector, traffic.size() - 1);

        while (newVector.size() > 0) {
            firstRowValue = 10000000;
            for (int i = 0; i < newVector.size(); i++) {
                record = (RandomTrafficRecord) newVector.get(i);
                if (record.getStart() < firstRowValue) {
                    firstRow = i;
                    firstRowValue = record.getStart();
                }
            }
            aux.add(newVector.get(firstRow));
            record = (RandomTrafficRecord) newVector.get(firstRow);
            newVector.remove(firstRow);
        }
        UpdateRandomTraffic(aux);
    }

    /**
     * Share traffic randomly among nodes.
     */
    private TrafficRecord ShareTrafficRandomly(Integer totalAP, RandomTrafficRecord randomRecord) {
        Double aux1, aux2;
        String source, address;
        TrafficRecord record;

        aux1 = (randomGenerator.nextDouble() * totalAP);
        source = accessPoints.Get(aux1.intValue()).WhatAP();
        do {
            aux2 = (randomGenerator.nextDouble() * totalAP);
        } while (aux1.intValue() == aux2.intValue());
        address = accessPoints.Get(aux2.intValue()).WhatAP();
        record = new TrafficRecord(randomRecord.getTCPUDP(), source,
                address, randomRecord.getSize(), randomRecord.getStart(),
                randomRecord.getStop(), randomRecord.getTransferSize(),
                randomRecord.getPacketsSeconds(), randomRecord.getMaxPackets(),
                Float.parseFloat("0"), (Integer) 0, (String)"AC_BE",
                Boolean.parseBoolean("false"), Integer.parseInt("0"),
                Boolean.parseBoolean("false"), Float.parseFloat("0.0")
                );
        return record;
    }

    /**
     * Generate a list with all node numbers shuffled.
     */
    private List<Integer> GenerateRandomNodeOrderList() {
        List<Integer> originalList = new ArrayList<Integer>();
        List<Integer> suffledList = new ArrayList<Integer>();
        Double aux;
        String list = "";

        for (int i = 0; i < (minNodes + (iteration) * granularity); i++) {
            originalList.add(i);
        }

        do {
            aux = (randomGenerator.nextDouble() * originalList.size());
            suffledList.add(originalList.get(aux.intValue()));
            originalList.remove(aux.intValue());
        } while (originalList.size() > 0);

        return suffledList;
    }

    /**
     * Calculate a node address sharing traffic balanced.
     */
    private Integer ObtainAnUnrepeatedAddress(Integer node1) {
        Integer node;

        if (listAddressNodes.size() == 0) {
            listAddressNodes = GenerateRandomNodeOrderList();
        }
        node = (Integer) listAddressNodes.get(0);
        listAddressNodes.remove(0);
        if (node1 == node) {
            //If there are no other node, generate a new list.
            if (listAddressNodes.size() == 0) {
                listAddressNodes = GenerateRandomNodeOrderList();
                listAddressNodes.add(node);
                return ObtainAnUnrepeatedAddress(node1);
            } else {
                listAddressNodes.add(node);
                return ObtainAnUnrepeatedAddress(node1);
            }
        }
        return node;
    }

    /**
     * Calculate a node source sharing traffic balanced.
     */
    private Integer ObtainAnUnrepeatedSource() {
        Integer node;

        if (listSourceNodes.size() == 0) {
            listSourceNodes = GenerateRandomNodeOrderList();
        }
        node = (Integer) listSourceNodes.get(0);
        listSourceNodes.remove(node);
        return node;
    }

    /**
     * Share traffic balanced among nodes.
     */
    private TrafficRecord ShareTrafficBalanced(Integer totalAP,
            RandomTrafficRecord randomRecord) {
        Double aux1, aux2;
        String source, address;
        TrafficRecord record;
        Integer node1, node2;
        Integer lastElement;

        node1 = ObtainAnUnrepeatedSource();
        node2 = ObtainAnUnrepeatedAddress(node1);

        source = accessPoints.Get(node1).WhatAP();
        address = accessPoints.Get(node2).WhatAP();

        record = new TrafficRecord(randomRecord.getTCPUDP(), source,
                address, randomRecord.getSize(), randomRecord.getStart(),
                randomRecord.getStop(), randomRecord.getTransferSize(),
                randomRecord.getPacketsSeconds(), randomRecord.getMaxPackets(),
                Float.parseFloat("0"), (Integer) 0, (String)"AC_BE",
                Boolean.parseBoolean("false"), Integer.parseInt("0"),
                Boolean.parseBoolean("false"), Float.parseFloat("0.0"));
        return record;
    }

    /**
     * With de traffic data defined by the user, generate the complete traffic.
     * @return True if the translation was successful.
     */
    private boolean TranslateRandomTrafficToTraffic() {
        RandomTrafficRecord randomRecord;
        TrafficRecord record;
        Vector<TrafficRecord> newTraffic = new Vector<TrafficRecord>();
        Integer totalAP;


        if (accessPoints.Size() < 2) {
            return false;
        }

        //Calculating the AP used in this iteration.
        if (minNodes + iteration * granularity < accessPoints.Size()) {
            totalAP = minNodes + iteration * granularity;
        } else {
            totalAP = accessPoints.Size();
        }

        for (int i = 0; i < accessPoints.GetRandomTrafficSize(); i++) {
            randomRecord = (RandomTrafficRecord) accessPoints.GetRandomTraffic().get(i);
            //If Castadiva must share all traffic completly random.
            for (int j = 0; j < randomRecord.getFlows(); j++) {
                if (isRandomTraffic) {
                    record = ShareTrafficRandomly(totalAP, randomRecord);
                } else {
                    //Castadiva share traffic balanced.
                    record = ShareTrafficBalanced(totalAP, randomRecord);
                }
                if (debug) {
                    System.out.println(record.getSource() + " " + record.getAddress() + " " + record.getTCPUDP() + " " + record.getMaxPackets());
                }
                newTraffic.add(record);
            }
        }
        //Add a dummy record for the last line.
        record = new TrafficRecord();
        newTraffic.add(record);
        accessPoints.SetTraffic(newTraffic);
        return true;
    }

    /**
     * Start a new Random Simulation.
     */
    public void GenerateRandomSimulation(String saveFolder, Integer loops) {
        savePath = saveFolder;
        routingProtocol = protocolSelectedRandomSimulation.get(routingLoop).toString();
        if (debug) {
            System.out.println("---------------------------------");
            System.out.println("          NEW SIMULATION         ");
            System.out.println("---------------------------------");
            System.out.println("Nodes: " + (minNodes + (iteration) * granularity) + " Loop: " + loop);
            System.out.println("Protocol: " + routingProtocol);
            System.out.println("---------------------------------");
        }
        PrepareSimulation();
        SetRandomScenery();
        this.loops = loops;
        CalculateNewNodeVisibility();
        if (TranslateRandomTrafficToTraffic()) {
            StartFileSimulation();
        }
    }
  

    /**
     * This function tells if CASTADIVA must to do a next
     * iteration or not.
     * @return true if the last simulation round is
     * started
     */
    public boolean IsEndOfRandomSimulation() {
        if ((minNodes + (iteration) * granularity) > maxNodes) {
            return true;
        }
        return false;
    }

    /**
     * Reset the iteration count to begin a new one.
     */
    public void StartIterationCount() {
        iteration = 0;
        loop = 0;
        routingLoop = 0;
    }

    /**
     * Create a new Iteration of the Random Simulation.
     */
    public void NextIteration(Integer loops) {
        routingLoop++;
        if (routingLoop == protocolSelectedRandomSimulation.size()) {
            loop++;
            routingLoop = 0;
            if (loop.equals(loops)) {
                loop = 0;
                iteration++;
                simulationSeconds = 0;
            }
        }
    }
        
    /****************************************************************************
     *
     *                              NODE INSTALATION
     *
     ****************************************************************************/
    /**
     * Obtain the OpenWRT packet list stored in a external file.
     */
    private List WhatPackets() {
        File file = new File(DEFAULT_CONFIG_DIRECTORY + File.separator + DEFAULT_PACKET_LIST);

        if(!file.exists()){
            System.err.println("Error opening file " + file.getAbsolutePath());
        }

        return ReadTextFileInLines(file);
    }

    /**
     * Generate the instructions needed to install the OpenWRT packets.
     */
    private String InstallOpenwrtPackets(List packets) {
        String installInstruction;
        String packetInstallation = "";
        for (int i = 0; i < packets.size(); i++) {
            installInstruction = "opkg install " + packets.get(i);
            packetInstallation = packetInstallation + installInstruction + "; ";
        }
        packetInstallation = "opkg update; " + packetInstallation;
        return packetInstallation;
    }

    /**
     * Generate the text of a script that allow a OpenWRT node to generate the deseared
     * the net configuration every time that it starts.
     * @param ethDevice The linux device name of the ethernet card.
     * @param wifiDevice The linux device name of the wifi card.
     * @param switchDevice The linux device name of the switch.
     * @param bridgeDevice The linux device name of the bridge.
     * @param newEthIp The IP desired in to connecto to the computer network.
     * @param newWifiIp The IP desired to connect to the wifi network.
     * @param gateway The IP to connect to Internet.
     * @param scriptsApFolder Where the scripts will be stored.
     * @param SSID The identificator of the WiFi network.
     * @param channel The WiFi network channel.
     * @return The entire script.
     */
    private String GenerateNetScriptText(String ethDevice, String wifiDevice, String switchDevice,
            String bridgeDevice, String newEthIp, String newWifiIp, String gateway) {
        String script = null;
        script = "#!/bin/"+DEFAULT_SHELL_LAUNCHER_MIPS + " \n\n" +
                "IPT=/usr/sbin/iptables\n" +
                "SWITCH_IP=\\\"" + newEthIp + "\\\"\n" +
                "SWITCH_NET=\\\"" + ObtainNetTypeC(newEthIp) + "/24\\\"\n" +
                "WIFI_IP=\\\"" + newWifiIp + "\\\"\n" +
                "WIFI_NET=\\\"" + ObtainNetTypeC(newWifiIp) + "/24\\\"\n" +
                "ETH_DEV=" + ethDevice + "\n" +
                "SWITCH_DEV=" + switchDevice + "\n" +
                "WIFI_DEV=" + wifiDevice + "\n" +
                "BRIDGE=" + bridgeDevice + "\n" +
                "GW=\\\"" + gateway + "\\\"\n\n" +
                "insmod /lib/modules/2.4.30/ipt_mac.o\n\n" +
                "ifconfig \\$BRIDGE down\n" +
                "brctl delif \\$BRIDGE \\$SWITCH_DEV\n" +
                "brctl delif \\$BRIDGE \\$WIFI_DEV\n" +
                "brctl delbr \\$BRIDGE\n\n" +
                "ifconfig \\$SWITCH_DEV  \\$SWITCH_IP netmask 255.255.255.0\n" +
                "ifconfig \\$ETH_DEV \\$SWITCH_IP netmask 255.255.255.0\n" +
                "ifconfig \\$WIFI_DEV  \\$WIFI_IP netmask 255.255.255.0\n" +
                "\\$IPT -t nat -F\n" +
                "\\$IPT -P FORWARD ACCEPT\n" +
                "echo \\\"1\\\" > /proc/sys/net/ipv4/ip_forward\n" +
                "echo \\\"1\\\" > /proc/sys/net/ipv4/conf/all/rp_filter\n\n" +
                "route add default gw \\$GW dev \\$SWITCH_DEV\n";
        return script;
    }

    /**
     * The instruction to generate the script in the AP.
     * @param folder Where the script will be saved.
     * @param scriptName The name of the file where the script will be stored.
     * @param text The text of the script.
     */
    private String GenerateScript(String folder, String scriptName, String text) {
        String file = folder + File.separator + scriptName;
        return "echo \"" + text + "\" > " + file + "\n";
    }

    /**
     * Change the nvram openwrt variables for consistence purpose.
     */
    private String SetNvramVariables(String script, String eth, String vlan) {
        return "echo \"" +
                "/usr/sbin/nvram set lan_ifname=" + eth + "\n" +
                "/usr/sbin/nvram set vlan_iface=" + vlan + "\n" +
                "\" >> " + script;
    }

    /**
     * Generate the instruction to create the folder where all Castadiva scripts
     * will be saved. Change the folder owner to nobody.
     * @param folder. The folder to be created.
     */
    private String GenerateScriptsFolders(String folder) {
        String folderInstallation = "";
        folderInstallation = "/bin/mkdir -p " + folder + "; chown nobody " + folder + "; ";
        return folderInstallation;
    }

    /**
     * Generate the simbolic link in the AP needed to autoboot the network script.
     * @param file The name of the file to be linked.
     */
    private String GenerateAutoBootInstruction(String file) {
        // return "ln -s " + file + " /etc/init.d/S91Castadiva ";
        return "ln -s " + file + " /etc/rc.d/S91Castadiva ";
    }

    /**
     * Change the configuration file olsrd to prepare the protocol to use the wifi card.
     */
    private List<String> ConfigureOLSRInstruction(String wifiDevice, String net, String mask) {
        List<String> addOlsr = new ArrayList<String>();
        String changeNetwork = "sed 's/@@HNA_IP@@/\"" + net + "\"/g' /etc/olsrd.conf > /tmp/tmpCasta && mv /tmp/tmpCasta /etc/olsrd.conf";
        String changeMask = "sed 's/@@HNA_MASK@@/\"" + mask + "\"/g' /etc/olsrd.conf > /tmp/tmpCasta && mv /tmp/tmpCasta /etc/olsrd.conf";
        String changeInterface = "sed 's/@@INTERFACE@@/\"" + wifiDevice + "\"/g' /etc/olsrd.conf > /tmp/tmpCasta && mv /tmp/tmpCasta /etc/olsrd.conf";
        String noBootWithOlsrd = "rm /etc/init.d/S60olsrd";
        addOlsr.add(changeNetwork);
        addOlsr.add(changeMask);
        addOlsr.add(changeInterface);
        addOlsr.add(noBootWithOlsrd);
        return addOlsr;
    }

    /**
     * Add a instruction to generate a NFS folder to one scrpit.
     * @param script The script where the instruction would be added.
     * @param nfsComputerFolder The folder where in the computer that will be shared by NFS.
     * @param nfsApFolder The folder in the AP where NFS will export the computer NFS folder.
     * @param time The time to wait before installing the nfs foler.
     * @return The instruction.
     */
    String LoadNfsFolder(String script, String nfsComputerFolder, String nfsApFolder) {
        return "echo \"sleep " + randomGenerator.nextInt(30) + " && mount -t nfs \\$GW:" + nfsComputerFolder + " " + nfsApFolder + " -o nolock,wsize=8192,rsize=8192,timeo=14 \n\" " +
                " >> " + script;
    }

    /**
     * Send the group of instructions to one AP not installed in CASTADIVA by SSH.
     * @param ip The ip of the AP.
     * @param user The SSH user to connect.
     * @param pwd The password for SSH.
     * @param instructions The instructions to be sended.
     */
    void SendInstruction(String ip, String user, String pwd, List instructions) {
        List<Integer> numInst = new ArrayList<Integer>();
        for (int i = 0; i < instructions.size(); i++) {
            numInst.add(i);
        }
        installNode = new SshHost(ip, user, pwd, instructions,
                false, numInst, "/tmp");
        installNode.start();
        }

    /**
     * Generate all the instructions to prepare an AP to run with CASTADIVA. The ap
     * must have installed an OpenWRT operative system. This function change the
     * network configuration to adapt it to CASTADIVA network.
     * @param ethDevice The linux device name of the ethernet card.
     * @param wifiDevice The linux device name of the wifi card.
     * @param switchDevice The linux device name of the switch.
     * @param bridgeDevice The linux device name of the bridge.
     * @param newEthIp The IP desired in to connecto to the computer network.
     * @param newWifiIp The IP desired to connect to the wifi network.
     * @param gateway The IP to connect to Internet.
     * @param nfsComputerFolder The folder where in the computer that will be shared by NFS.
     * @param nfsApFolder The folder in the AP where NFS will export the computer NFS folder.
     * @param scriptsApFolder Where the scripts will be stored.
     * @param user The user to connect by SSH with the node.
     * @param pwd The password for the SSH session.
     * @param currentIp The actual IP of the node.
     * @param SSID The identificator of the WiFi network.
     * @param channel The WiFi network channel.
     */
    void InstallAp(String ethDevice, String wifiDevice, String switchDevice,
            String bridgeDevice, String newEthIp, String newWifiIp, String gateway,
            String nfsComputerFolder, String nfsApFolder, String scriptsApFolder,
            String user, String pwd, String currentIp) {

        List<String> instructionList = new ArrayList<String>();
        List packets;
        List<String> changeOlsrConf;
        String packetInstallation, folderScriptInstallation, createFolders, folderNfs, scriptText, autoBoot, scriptInstruction, chmodScript;
        String nvram;
        String scriptName = "Castadiva.sh";
        String creatingNfsFolder = "";

        packets = WhatPackets();
        packetInstallation = InstallOpenwrtPackets(packets);
        scriptText = GenerateNetScriptText(ethDevice, wifiDevice, switchDevice,
                bridgeDevice, newEthIp, newWifiIp, gateway);
        folderScriptInstallation = GenerateScriptsFolders(scriptsApFolder);
        folderNfs = GenerateScriptsFolders(nfsApFolder);
        scriptInstruction = GenerateScript(scriptsApFolder, scriptName, scriptText);
        creatingNfsFolder = LoadNfsFolder(scriptsApFolder + File.separator + scriptName,
                nfsComputerFolder, nfsApFolder);
        autoBoot = GenerateAutoBootInstruction(scriptsApFolder + File.separator + scriptName);
        chmodScript = "chown nobody " + scriptsApFolder + File.separator + scriptName +
                " && chmod u+x " + scriptsApFolder + File.separator + scriptName +
                " && chown nobody " + nfsApFolder + " && chmod 777 " + nfsApFolder;
        changeOlsrConf = ConfigureOLSRInstruction(wifiDevice, ObtainNetTypeC(newWifiIp), "255.255.255.0");
        nvram = SetNvramVariables(scriptsApFolder + File.separator + scriptName,
                ethDevice, switchDevice);

        createFolders = folderScriptInstallation + " " + folderNfs;
        instructionList.add(packetInstallation);
        instructionList.add(createFolders);
        instructionList.add(scriptInstruction);
        instructionList.add(creatingNfsFolder);
        instructionList.add(chmodScript);
        instructionList.add(autoBoot);
        instructionList.addAll(changeOlsrConf);
        instructionList.add(nvram);
        //ShowList(instructionList);
        SendInstruction(currentIp, user, pwd, instructionList);
    }

    /****************************************************************************
     *
     *                              MAYA EXPORT
     *
     ****************************************************************************/
    /**
     * Generate the head of the file to export the data to Maya.
     * @param MayaList A empty list of data.
     */
    private void CreateMayaHead(List<String> MayaList) {
        MayaList.add(x + " " + y);
    }

    /**
     * Translate every Castadiva node to Maya node.
     * @param MayaList A list with the head of Maya.
     */
    private void CreateMayaNodes(List<String> MayaList) {
        AP node = null;
        for (int i = 0; i < accessPoints.Size(); i++) {
            node = accessPoints.Get(i);
            MayaList.add(node.WhatWifiIP() + " " + node.x + " " + node.y + " " +
                    node.z + " " + node.range + " " + node.WhatUser() + " " +
                    node.WhatPwd());
        }
    }

    /**
     * Create a list of data ready to export to Maya.
     * @param MayaList A empty list of data.
     * @see CreateMayaHead
     * @see CreateMayaNodes
     */
    private void CreateMaya(List<String> MayaList) {
        CreateMayaHead(MayaList);
        CreateMayaNodes(MayaList);
    }

    /**
     * Generate a file with all the Castadiva information ready for the Maya system.
     * @param file The path to save the file.
     * @see CreateMaya
     */
    void ExportMaya(String file) {
        List<String> MayaList;
        MayaList = new LinkedList<String>();
        CreateMaya(MayaList);
        SaveInFile(MayaList, file);
    }

    /****************************************************************************
     *
     *                              NS IMPORT/EXPORT
     *
     ****************************************************************************/
    /**
     * Generate a file with a structure that can be readed by the simulator NS.
     * @param file The path to save the file.
     * @param nodes The number of real nodes used.
     * @see CreateNS
     */
    void ExportNsScenario(String file, Integer nodes) {
        List<String> NsScenario, NsNodes, NsMobility;
        NsScenario = CreateNSHead(nodes);
        NsNodes = CreateNSNodes(nodes);
        NsScenario.addAll(NsNodes);
        if (MobiliyActivated()) {
            NsMobility = GenerateNsMobility();
            NsScenario.addAll(NsMobility);
        }
        SaveInFile(NsScenario, file);
    }

    /**
     * Generate a file with a structure that can be readed by the simulator NS.
     * @param file The path to save the file.
     * @see CreateNS
     */
    void ExportNsTraffic(String file) {
        List NsTraffic;
        NsTraffic = GenerateNsTraffic();
        SaveInFile(NsTraffic, file);
    }

    /**
     * Write into a file all data to generate a scenario with mobility.
     * @param file The path to save the file.
     * @see CreateNS
     */
    void ExportNSMobility(String file) {
        List NsMobility;
        NsMobility = GenerateNsMobility();
        SaveInFile(NsMobility, file);
    }

    /**
     * Obtain the distance between tho NodeCheckPoints.
     */
    private float CalculateNodeCheckPointDistance(NodeCheckPoint node1,
            NodeCheckPoint node2) {
        return (float) Math.sqrt(Math.pow((node1.xCoordinate - node2.xCoordinate), 2) +
                Math.pow((node1.yCoordinate - node2.yCoordinate), 2) +
                Math.pow((node1.zCoordinate - node2.zCoordinate), 2));
    }

    /**
     * Translate the data read to make easier to convert to NS.
     */
    private List<NodeImportedData> ObtainNodeDestinations(List<NodeImportedData> nodeMovements) {
        List<NodeImportedData> tidiedNodeMovements = new ArrayList<NodeImportedData>();

        if (debug) {
            System.out.println("Obtained:");
        }
        for (int i = 0; i < nodeMovements.size() - 1; i++) {
            NodeImportedData box1 = nodeMovements.get(i);
            NodeImportedData box2 = nodeMovements.get(i + 1);

            NodeImportedData movement = new NodeImportedData(box1.nodeNumber,
                    box2.xCoordinate, box2.yCoordinate, box1.speed, box1.second);
            tidiedNodeMovements.add(movement);
        }
        return tidiedNodeMovements;
    }

    /**
     *
     */
    List<NodeImportedData> ReadNodeDestinations(int node) {
        List<NodeImportedData> nodeMovements = new ArrayList<NodeImportedData>();
        NodeCheckPoint point2 = null;
        float xIncrement, yIncrement, zIncrement,
                prevXIncrement = 0, prevYIncrement = 0, prevZIncrement = 0,
                errorIncrement = new Float(0.0005), speed = 0;

        if (debug) {
            System.out.println("Node -> " + (node + 1));
        }
        for (int i = 1; i < nodePositions[node].length; i++) {
            NodeCheckPoint point1 = nodePositions[node][i - 1];
            point2 = nodePositions[node][i];

            xIncrement = point2.xCoordinate - point1.xCoordinate;
            yIncrement = point2.yCoordinate - point1.yCoordinate;
            zIncrement = point2.zCoordinate - point1.zCoordinate;

            if (Math.abs(xIncrement - prevXIncrement) > errorIncrement ||
                    Math.abs(yIncrement - prevYIncrement) > errorIncrement ||
                    Math.abs(zIncrement - prevZIncrement) > errorIncrement) {

                speed = CalculateNodeCheckPointDistance(point1, point2);
                NodeImportedData movement = new NodeImportedData(node,
                        point1.xCoordinate, point1.yCoordinate, speed, i - 1);
                if (debug) {
                    System.out.println("   Speed: " + movement.speed + " at second " + movement.second + " x:" + movement.xCoordinate + " y:" + movement.yCoordinate);
                }
                nodeMovements.add(movement);
            }

            prevXIncrement = xIncrement;
            prevYIncrement = yIncrement;
            prevZIncrement = zIncrement;
        }

        NodeImportedData movement = new NodeImportedData(node,
                point2.xCoordinate, point2.yCoordinate, (float) 0, nodePositions[node].length);
        if (debug) {
            System.out.println("   Speed: " + movement.speed + " at second " + movement.second + " x:" + movement.xCoordinate + " y:" + movement.yCoordinate);
        }
        nodeMovements.add(movement);

        return nodeMovements;
    }

    /**
     * Order by time a group of NodeImportedData in one vector.
     */
    private NodeImportedData[] SortNodeDestinations(List<NodeImportedData> allMovements[], int total) {
        NodeImportedData[] sequencedMovements = new NodeImportedData[total];
        int lastUsed[] = new int[nodePositions.length];
        int min, inserted = 0, selected = 0;

        for (int j = 0; j < nodePositions.length; j++) {
            lastUsed[j] = 0;
        }

        if (debug) {
            for (int i = 0; i < allMovements.length; i++) {
                for (int j = 0; j < allMovements[i].size(); j++) {
                    System.out.println("t:" + allMovements[i].get(j).second +
                            " node: " + allMovements[i].get(j).nodeNumber +
                            " x:" + allMovements[i].get(j).xCoordinate +
                            " y:" + allMovements[i].get(j).yCoordinate +
                            " s: " + allMovements[i].get(j).speed);
                }
            }
        }

        while (inserted < total) {
            min = 10000;
            for (int i = 0; i < nodePositions.length; i++) {
                if (lastUsed[i] < allMovements[i].size() && allMovements[i].get(lastUsed[i]).second < min) {
                    min = allMovements[i].get(lastUsed[i]).second;
                    selected = i;
                }
            }
            sequencedMovements[inserted] = allMovements[selected].get(lastUsed[selected]);
            inserted++;
            lastUsed[selected]++;
        }
        return sequencedMovements;
    }

    /**
     * Generate a list with all instructions to represent the mobility for the NS simulator.
     */
    private List<String> GenerateNsMobility() {
        List<String> nsMobility = new ArrayList<String>();
        List<NodeImportedData> nodeMovements;
        List<NodeImportedData> allMovements[] = new ArrayList[nodePositions.length];
        NodeImportedData[] sequencedMovements;
        NodeImportedData node;

        int sum = 0;
        if (debug) {
            System.out.println("Calculate Export to NS-2");
        }
        for (int j = 0; j < nodePositions.length; j++) {
            nodeMovements = ReadNodeDestinations(j);
            nodeMovements = ObtainNodeDestinations(nodeMovements);
            allMovements[j] = nodeMovements;
            sum += nodeMovements.size();
        }
        if (debug) {
            System.out.println("Total movements detected: " + sum);
        }
        sequencedMovements = SortNodeDestinations(allMovements, sum);
        for (int i = 0; i < sequencedMovements.length; i++) {
            node = sequencedMovements[i];
            nsMobility.add("$ns_ at " + node.second + " \"$node_(" + node.nodeNumber + ") setdest " + node.xCoordinate + " " + node.yCoordinate + " " + node.speed + "\"");
        }
        return nsMobility;
    }

    /**
     * Generate the three first lines of a NS file. It contains the general data
     * of the file.
     * @return The firsts lines of a NS file.
     */
    private List<String> CreateNSHead(Integer nodes) {
        List<String> NSList = new ArrayList<String>();
        NSList.add("#");
        NSList.add("# nodes: " + nodes + ", pause: " + pause + ", max speed: " +
                maxSpeed + ", max x = " + x + ", max y: " + y);
        NSList.add("#");
        return NSList;
    }

    /**
     * Translate every castadiva node to NS node.
     * @return A list containing every position node in NS format.
     */
    private List<String> CreateNSNodes(Integer nodes) {
        AP node = null;
        List<String> NSList = new ArrayList<String>();

        for (int i = 0; i < nodes; i++) {
            node = accessPoints.Get(i);
            NSList.add("$node_(" + i + ") set X_ " + node.x);
            NSList.add("$node_(" + i + ") set Y_ " + node.y);
            NSList.add("$node_(" + i + ") set Z_ " + node.z);
        }
        return NSList;
    }

    /**
     * Translate Castadiva Traffic to a file ready to be
     * importen to NS.
     * @return A list with all the traffic lines in NS format.
     */
    private List<String> GenerateNsTraffic() {
        String head = "global ns_ udp_ cbr_ null_ node_ ";
        List<String> resultList = new ArrayList<String>();
        String line;

        resultList.add(head);
        for (int i = 0; i < accessPoints.GetTrafficSize(); i++) {
            TrafficRecord record = (TrafficRecord) accessPoints.GetTraffic().get(i);
            float stop = record.getStart() + (record.getMaxPackets() /
                    record.getPacketsSeconds());
            float ratio = 1 / Float.parseFloat(record.getPacketsSeconds() + "");
            line = "set udp_(" + i + ") [new Agent/UDP]\n" +
                    "$ns_ attach-agent $node_(" + accessPoints.SearchAP(record.getSource()) + ") $udp_(" + i + ")\n" +
                    "set null_(" + i + ") [new Agent/Null]\n" +
                    "$ns_ attach-agent $node_(" + accessPoints.SearchAP(record.getAddress()) + ") $null_(" + i + ")\n" +
                    "set cbr_(" + i + ") [new Application/Traffic/CBR]\n" +
                    "$cbr_(" + i + ") set packetSize_ " + record.getSize() + "\n" +
                    "$cbr_(" + i + ") set interval_ " + ratio + "\n" +
                    "$cbr_(" + i + ") set random_ 0\n" +
                    "$cbr_(" + i + ") attach-agent $udp_(" + i + ")\n" +
                    "$ns_ connect $udp_(" + i + ") $null_(" + i + ")\n" +
                    "$ns_ at " + (float) record.getStart() + " \"$cbr_(" + i + ") start\"\n" +
                    "$ns_ at " + stop + " \"$cbr_(" + i + ") stop\"\n";
            resultList.add(line);
        }
        return resultList;
    }

    /**
     * Import a file written in NS format to datas for Castadiva.
     * @param file The path to load the file.
     * @return <code>true</code> it the file has been readed.
     */
    boolean ImportNs(String file) {
        String rawText;
        int lastReadedLine;
        String nsText[];

        rawText = NSRead(file);
        nsText = rawText.split("\n");

        //Se transforman los datos
        try {
            if (ReadNSHead(nsText)) {
                nsText = DeleteNsBodyComments(3, nsText);
                lastReadedLine = ReadNsNode(3, nsText);
                 // lastReadedLine = ReadNsNode(14, nsText); PRuEBA PARA CITYMOB
                if (maxSpeed > 0) {
                    ReadNsMobility(lastReadedLine, nsText);
                    //DeduceNsSimulationTime();
                    TranslateNsDataToMobilityMatrix();
                }
            } else {
                return false;
            }

        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame, "It is not a NS file!", "Load Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return true;
    }

    /**
     * Open a file written in NS format.
     * @param NSPath The path on where is stored the file.
     * @see ImportNs
     */
    private String NSRead(String NSPath) {
        File fileNS = new File(NSPath);
        String text = "Error opening the file.";
        try {
            FileInputStream inputData = new FileInputStream(fileNS);
            byte bt[] = new byte[(int) fileNS.length()];
            int numBytes = inputData.read(bt);
            text = new String(bt);
            inputData.close();
        } catch (FileNotFoundException fne) {
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return text;
    }

    /**
     * Read the three first lines of a NS file. In this lines are vital information
     * about the rest of the file (the size and the number of nodes).
     * @param nsText Is a String containing all the data readed of a NS file.
     * @see ImportNs
     */
    
    private boolean ReadNSHead(String nsText[]) throws ArrayIndexOutOfBoundsException {
        String[] intro = null;

        try {
            intro = nsText[1].split(" ");
            try {
                numberNodes = Integer.parseInt(intro[2].split(",")[0]);
                pause = Float.parseFloat(intro[4].split(",")[0]);
                maxSpeed = Float.parseFloat(intro[7].split(",")[0]);
                x = Float.parseFloat(intro[10].split(",")[0]);
                y = Float.parseFloat(intro[13].split(",")[0]);
            } catch (NumberFormatException ne) {
                //ne.printStackTrace();
                return false;
            }
        } catch (ArrayIndexOutOfBoundsException oe) {
            return false;
        }
        return true;
    }

    /**
     * Obtain data for a node from a file or set a default one.
     */
    public String[] ObtainStoredApData(Integer node, File file) {
        List lines;
        String line;
        Integer help_lines = 1;

        lines = ReadTextFileInLines(file);
        if (node + 1 > lines.size() - help_lines) {
            String descomposed_line[] = {DEFAULT_WIFI_MAC, DEFAULT_IP_NET + (node + 1),
                DEFAULT_WIFI_IP_NET + (node + 1), DEFAULT_CHANNEL + "", DEFAULT_WIFI_DEVICE,
                DEFAULT_RANGE + "", DEFAULT_USER, DEFAULT_PWD, DEFAULT_PROCESSOR,
                DEFAULT_NFS_DIRECTORY, DEFAULT_GW
            };
            return descomposed_line;
        } else {
            line = (String) lines.get(node + help_lines);
            String[] descomposed_line = line.split(" ");
            return descomposed_line;
        }
    }

    public String[] ObtainStoredApData(Integer node) {
        File file = new File(DEFAULT_CONFIG_DIRECTORY + File.separator + DEFAULT_APS_FILE);

        if(!file.exists()){
            System.err.println("Error opening file " + file.getAbsolutePath());
        }
        
        return ObtainStoredApData(node, file);
    }

    /**
     * Obtain data for a node from a file or set a default one.
     */
    public void LoadApsFromFile(File file) {
        List lines = ReadTextFileInLines(file);
        String line;
        String[] ap;
        Integer help_lines = 1;
        Integer nparams = 16;
        accessPoints = new APs();

        for(int l = 0; l < lines.size() - help_lines; l++) {
            line = (String) lines.get(l+help_lines);
            ap = line.split(" ");
            if(ap.length == nparams) {
                System.out.println("Ap is :"+line);
               AP node = new AP(ap[0], ap[1], ap[2], ap[3], ap[4], ap[5],
                        Float.parseFloat(ap[6]), Float.parseFloat(ap[7]),
                        Float.parseFloat(ap[8]), Float.parseFloat(ap[9]),
                        ap[10], ap[11], Integer.parseInt(ap[12]),
                        ap[13], ap[14], ap[15]);
         //   public AP(String address, String wifiAddress, String wifiMac, String user, String pwd, String id,
         //   float x, float y, float z, float range, String directory, String processor, Integer channel,
         //   String mode, String wfDevice, String tmp_gw) {

                accessPoints.Add(node);
            }
        }
    }


    public void WriteAPToFile(BufferedWriter out, AP ap) throws IOException  {

        out.write(ap.WhatEthIP() +" "+ ap.WhatWifiIP() + " " + ap.WhatWifiMac() + " ");
        out.write(ap.WhatUser() +" "+ ap.WhatPwd() + " " + ap.WhatAP() + " ");
        out.write(new Float(ap.WhatX()).toString() +" "+new Float(ap.WhatY()).toString() + " " + new Float(ap.WhatZ()).toString() + " ");
        out.write(new Float(ap.range).toString() +" "+ ap.WhatWorkingDirectory() + " " + ap.WhatProcessor() + " ");
        out.write(Integer.toString(ap.WhatChannel()) +" "+ ap.WhatMode() + " " + ap.WhatWifiDevice() + " ");
        out.write(ap.WhatGW()+"\n");
 

        
    }

     /**
     * Obtain data for a node from a file or set a default one.
     */
    public void SaveApsToFile(File file) {
        try {
            //Integer help_lines = 1;
            //Integer nparams = 16;
            FileWriter fw = new FileWriter(file.getCanonicalPath());
            
            BufferedWriter out = new BufferedWriter(fw);
            out.write( "#String address, String wifiAddress, String wifiMac, String user, String pwd, String id, float x, float y, float z, float range, String directory, String processor, Integer channel, String mode, String wfDevice, String tmp_gw\n");
            

            for(int i = 0; i < accessPoints.Size(); i++) {
                WriteAPToFile(out, accessPoints.Get(i));
            }

            out.close();

        } catch (IOException ex) {
            Logger.getLogger(CastadivaModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Delte all useless lines in the NS text.
     */
    private String[] DeleteNsBodyComments(int startingLine, String nsText[]) {
        List<String> array = new ArrayList<String>();
        for (int j = 0; j < startingLine; j++) {
            array.add(nsText[j]);
        }
        for (int i = startingLine; i < nsText.length; i++) {
            String line = nsText[i];
            if (!(line.startsWith("#") || line.startsWith("\n") || line.equals(""))) {
                array.add(line);
            }
        }
        String newText[] = new String[array.size()];
        for (int k = 0; k < array.size(); k++) {
            newText[k] = array.get(k);
        }

        return newText;
    }

    /**
     * Read every node in a NS file and translate it to Castadiva AP.
     * @param lineaInicio Which line is used to start the conversion from the file.
     * @param nsText All the ns text.
     * @return How many nodes have been readed.
     * @see ImportNs
     */
    private int ReadNsNode(int startingLine, String nsText[]) {
        int i, id;
        float positionX, positionY, positionZ;
        accessPoints = new APs();
        for (i = startingLine; i < numberNodes * 3 + startingLine; i += 3) {
            positionX = Float.parseFloat(nsText[i].split(" ")[3]);
            positionY = Float.parseFloat(nsText[i + 1].split(" ")[3]);
            positionZ = Float.parseFloat(nsText[i + 2].split(" ")[3]);
            id = Integer.parseInt(nsText[i].split("\\)")[0].split("\\(")[1]);
            String[] ap = ObtainStoredApData((i / 3) - 1);
            String name = "";
            try {
                name = ap[10];
            } catch (Exception e) {
                name = DEFAULT_ID + id;
            }
            AP node = new AP(ap[1], ap[2], ap[0], ap[6], ap[7], name, positionX, positionY, positionZ,
                    Float.parseFloat(ap[5]), ap[9], ap[8], Integer.parseInt(ap[3]), DEFAULT_MODE,
                    ap[4], DEFAULT_GW);
            accessPoints.Add(node);
        }
        return i;
    }

    /**
     * Read the node mobility in a NS file and translate it to Castadiva AP.
     * @param startingLine Which line is used to start the conversion from the file.
     * @param nsText All the ns text.
     * @return How many nodes have been readed.
     * @see ImportNs
     */
    private int ReadNsMobility(int startingLine, String nsText[]) {
        int i;
        Integer node;
        Float xDestCoordinate, yDestCoordinate, speed, second;
        String node_str, node_substr, speed_str, node_id, node_name;
        String[] words;


        // Generation, for each node, of a mobility vecotr
        // Max speed is set to -1 to that mobility vector
        allAddresses = new MobilityVectors(this, accessPoints, pause);

        // We initialise nsData, which will contain the position and speed of each node
        // at any time in the simulation.
        nsData = new NodePositionsFromNsMobility(accessPoints.Size());

        if (debug) {
            System.out.println("\nImporting NS Mobility:");
        }
        for (i = startingLine; i < nsText.length; i++) {
            // Example line :
            // $ns_ at 27.373013805488327 "$node_(1) setdest 800.0 400.0 68.42146302464042"
            words = nsText[i].split(" ");
            // If we have a correct line
            if (words[0].endsWith("$ns_") && words[1].endsWith("at") &&
                    words[4].endsWith("setdest") && !words[0].startsWith("#")) {

                second = Float.parseFloat(words[2]);
                xDestCoordinate = Float.parseFloat(words[5]);
                yDestCoordinate = Float.parseFloat(words[6]);

                // Parse the speed
                speed_str = words[7];
                speed = Float.parseFloat(speed_str.split("\"")[0]); // Remove the final "

                // Get the node id
                node_str = words[3];
                node_substr = node_str.split("\\(")[1];
                node_id = node_substr.split("\\)")[0];

                // Compose the node name using the node id and the default prefix
                // This means that nodes should be named as String0, String1, ...
                // node_name = DEFAULT_ID + new Integer(new Integer(node_id)+1);
                node = new Integer(node_id);

                allAddresses.ChangeMobilityVector(xDestCoordinate, yDestCoordinate, speed, node);
                
                // We add the collected information to nsData. It now contains the movement instructions as in the CityMob file.
                nsData.AddNodeInformation(node, xDestCoordinate, yDestCoordinate, speed, second);
                if (GetSimulationTime() < Float.parseFloat(words[2])) {
                    SetSimulationTime((int) Float.parseFloat(words[2]) + 1);
                }
            } else {
                // If we have a god_ line
                if (words[0].endsWith("$ns_") && words[1].endsWith("at") && words[3].endsWith("god_")) {
                    if (GetSimulationTime() < Float.parseFloat(words[2])) {
                        SetSimulationTime((int) Float.parseFloat(words[2]) + 1);
                    }
                }
            }
        }
        return i;
    }

    /**
     * Store all waypoints of a node to simulate it later.
     */
    private void StoreNewNodePosition(Float x, Float y, Integer nodeNumber, Integer second) {
        NodeCheckPoint checkPoint = new NodeCheckPoint(x, y, new Float(0));
        nodePositions[nodeNumber][second] = checkPoint;
        if (debug) {
            System.out.println("Node: " + (nodeNumber + 1) + " second: " + second + " x/y: " + checkPoint.xCoordinate + "/" + checkPoint.yCoordinate);
        }
    }

    /**
     * Obtain the intermediate movement between two positions of one node.
     */
    private void ObtainNodePositionBetweenTwoMovements(NodeImportedData start, NodeImportedData end,
            Integer nodeNumber, Integer usedSeconds) {
        //Integer usedSeconds;
        Float xIncrease, yIncrease;

        if (usedSeconds < 1) {
            usedSeconds = 1;
        }
        xIncrease = (end.xCoordinate - start.xCoordinate) / usedSeconds;
        yIncrease = (end.yCoordinate - start.yCoordinate) / usedSeconds;
        if (debug) {
            System.out.println("Node: " + (nodeNumber + 1) + " starting second: " + end.second + " Increasex/y: " + xIncrease + "/" + yIncrease + " startx/y: " + start.xCoordinate + "/" + start.yCoordinate + " endx/y: " + end.xCoordinate + "/" + end.yCoordinate + " Used Sec:" + usedSeconds + " Speed: " + end.speed);
        }
        for (int i = 0; i <= usedSeconds; i++) {
            StoreNewNodePosition(start.xCoordinate + xIncrease * i,
                    start.yCoordinate + yIncrease * i, nodeNumber, end.second + i);
        }
    }

    /**
     * Calculate the last movement of a node if this movement not end in the simulation.
     */
    private void ObtainLastNodePositionMovement(NodeImportedData start, NodeImportedData end,
            Integer nodeNumber) {
        Float xIncrease, yIncrease;
        Float usedSeconds;

        usedSeconds = ObtainArrivingTime(start.xCoordinate, start.yCoordinate,
                end.xCoordinate, end.yCoordinate, end.speed);
        xIncrease = (end.xCoordinate - start.xCoordinate) / usedSeconds;
        yIncrease = (end.yCoordinate - start.yCoordinate) / usedSeconds;
        if (debug) {
            System.out.println("Node: " + (nodeNumber + 1) + " starting second: " + end.second + " Increasex/y: " + xIncrease + "/" + yIncrease + " startx/y: " + start.xCoordinate + "/" + start.yCoordinate + " endx/y: " + end.xCoordinate + "/" + end.yCoordinate + " Used Sec:" + usedSeconds + " Speed: " + end.speed);
        }
        for (int i = end.second; i <= GetSimulationTime(); i++) {
            StoreNewNodePosition(start.xCoordinate + xIncrease * (i - end.second),
                    start.yCoordinate + yIncrease * (i - end.second), nodeNumber, i);
        }
    }

    /**
     * Obtain the arriving time from one point ot another with a determined speed.
     */
    private Float ObtainArrivingTime(Float x1, Float y1, Float x2, Float y2, Float speed) {
        Double cathetus1, cathetus2, hypotenuse;
        Float time;
        if (speed == 0) {
            return Float.POSITIVE_INFINITY;
        }
        cathetus1 = (double) Math.abs(y2 - y1);
        cathetus2 = (double) Math.abs(x2 - x1);
        hypotenuse = Math.sqrt(Math.pow(cathetus1, 2) + Math.pow(cathetus2, 2));
        time = new Float(hypotenuse / speed);
        return time;
    }

    /**
     * Transforms the mobility instructions readen form a CityMob file to the
     * mobility matrix used in Castadiva.
     * The data was previously readen from the file using the ImportNsCITYMOB function.
     * Warning : In castadiva, an aproximation is made, the time granularity is set to one second
     * For example, an instruction that would be : at 28.654488 set .... in citymob is interpreted as : at 29 set ..... in castadiva
     * @author Nacho Wannes
     * @see ImportNsCITYMOB
     */
    private void TranslateNsDataToMobilityMatrix() {
        List<NodeImportedData> nodeMovements;
        NodeImportedData nodeVector1, nodeVector2, nodeVector3;

        // Creation of the matrix that will contain node's position at any time of the simulation
        nodePositions = new NodeCheckPoint[accessPoints.Size()][GetSimulationTime() + 1];

        // We first place each node at it's initial position for the whole simulation
        // Later on, we will browse the mobility instructions and make the apropriate changes at the apropriate time
        // Any node that is not affected by any mobility instruction will remain as it was set here
        for (int node = 0; node < accessPoints.Size(); node++) {
            // recovery of the node
            AP ap = accessPoints.Get(node);

            // Set nodes position a it's initial position for the whole simulation
            for(int j=0;j <= GetSimulationTime(); j++ )
            {
                StoreNewNodePosition(ap.x, ap.y, node, j);
            }
        }

        // Now that each node is safely placed, we need to introduces movement instructions for each node
        for (int node = 0; node < accessPoints.Size(); node++) {
            
            // Recovery of the mobility instructions for the node
            nodeMovements = nsData.GetNodeInformation(node);

            // Each mobility instruction influences the next one.
            // We need to keep a trace of the position of the node after its move.
            float xPos;
            float yPos;

            // For each instruction we have for the current node
            for (int i = 0; i < nodeMovements.size(); i++)
            {
                // Recovery of the specific mobility instruction
                NodeImportedData movementInstruction = nodeMovements.get(i);

                // Recovery of the position of the node when the instruction occurs
                xPos = nodePositions[node][movementInstruction.second].xCoordinate;
                yPos = nodePositions[node][movementInstruction.second].yCoordinate;


                // We now calculate the different parameters of the move
                float deltaX = movementInstruction.xCoordinate - xPos;
                float deltaY = movementInstruction.yCoordinate - yPos;

                // Delta is the distance of the move
                float delta = (float) Math.sqrt(Math.pow(deltaX, 2)+Math.pow(deltaY, 2));

                int aproximateTimeMoving = 0;
                float exactTimeMoving = 0;

                // How do the x and y coordinates change for a one second movement
                float oneSecXIncrease = 0;
                float oneSecYIncrease = 0;

                // This is necessary to avoid division by 0. Speed = 0 is used in citymob as a STOP instruction.
                if(movementInstruction.speed > 0)
                {
                    exactTimeMoving = delta/movementInstruction.speed;
                    aproximateTimeMoving = (int) Math.round(exactTimeMoving);

                    // We can now know how a node move
                    oneSecXIncrease = deltaX/exactTimeMoving;
                    oneSecYIncrease = deltaY/exactTimeMoving;
                }


                // As we calculated the time of the movement, we now write the new positions in the matrix
                // if the movement instruction is given at time t, the node will have moved at time t+1
                for(int second=movementInstruction.second+1; second<= movementInstruction.second + aproximateTimeMoving && second < GetSimulationTime(); second++)
                {
                    // If we are making our last move, we apply a correction.
                    // That correction allows to obtain better results,
                    // it avoid aproximation errors to sum after each mobility instruction
                    if(second == movementInstruction.second+aproximateTimeMoving){
                        xPos=movementInstruction.xCoordinate;
                        yPos=movementInstruction.yCoordinate;
                    }else{
                        xPos+=oneSecXIncrease;
                        yPos+=oneSecYIncrease;
                    }
                    StoreNewNodePosition( xPos,  yPos, node, second);
                }

                // The position of the node after it's move must be updated in the matrix
                // Next movement instructions might erase this, or not
                for(int second = movementInstruction.second+aproximateTimeMoving+1;second<=GetSimulationTime();second++){
                    StoreNewNodePosition(xPos, yPos, node, second);
                }
            }
        }
    }
    
    /****************************************************************************
     *
     *                          IMPORT CITYMOB SCENARIO
     *
     ****************************************************************************/
    
    /* Imports a CityMob mobility scenario from a file into castadiva*/
    boolean ImportNsCITYMOB(String file) {
        String rawText;
        int lastReadedLine;
        String nsText[];

        // Reading the file
        rawText = NSRead(file);

        // Seperation each line
        nsText = rawText.split("\n");

        //Se transforman los datos
        try {
            // We try to get the simulation information from the header :
            // max speed, number of nodes, pause time, width, height
            if (ReadNSHeadCITYMOB(nsText)) {
                // If the lecture was sucessfull, the comments are cleaned out of the
                // text file. The header (14 first lines) is kept.
                nsText = DeleteNsBodyComments(14, nsText);

                // We now read the initial position information for each node and
                // add the nodes to Castadiva using information stroed in configuration/aps.txt
                lastReadedLine = ReadNsNodeCITYMOB(14, nsText);

                // If maxSpeed is = 0, there is no need to import any more mobility information
               if (maxSpeed > 0) {
                   // Analysis of the cityMob information 
                   ReadNsMobility(lastReadedLine, nsText);
                   // Conversion of the cityMob information into a mobility matrix in Castadiva
                   TranslateNsDataToMobilityMatrix();
                }
            } else {
                // If we were unable to read the header...
                return false;
            }

        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame, "It is not a NS file!", "Load Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return true;
    }

    private boolean ReadNSHeadCITYMOB(String nsText[]) throws ArrayIndexOutOfBoundsException {
        String[] intro = null;

        try {
            intro = nsText[7].split(" ");

            try {
                numberNodes = Integer.parseInt(intro[3].split(",")[0]);
                pause = Float.parseFloat(intro[6].split(",")[0]);
                maxSpeed = Float.parseFloat(intro[8].split(",")[0]);
                x = Float.parseFloat(intro[11].split(",")[0]);
                y = Float.parseFloat(intro[14].split(",")[0]);
            } catch (NumberFormatException ne) {
                //ne.printStackTrace();
                return false;
            }
        } catch (ArrayIndexOutOfBoundsException oe) {
            return false;
        }
        return true;
    }

    /* This function optains Access Point information from the aps.txt file
     * that is stored in the configuration folder
     * It is mandatory for CityMob Importation to set aps.txt correctly
     */
    public String[] ObtainStoredApDataCITYMOB(Integer node) {
        List lines;
        String line;
        Integer help_lines = 1;
        File file = new File(DEFAULT_CONFIG_DIRECTORY + File.separator + DEFAULT_APS_FILE);

        if(!file.exists()){
            System.err.println("Error opening file " + file.getAbsolutePath());
        }

        lines = ReadTextFileInLines(file);
        // If there are no suficient lines in aps.txt, we generate a default node
        // configuration.
        if (node + 1 > lines.size() - help_lines) {
            String descomposed_line[] = {DEFAULT_WIFI_MAC, DEFAULT_IP_NET + (node + 1),
                DEFAULT_WIFI_IP_NET + (node + 1), DEFAULT_CHANNEL + "", DEFAULT_WIFI_DEVICE,
                DEFAULT_RANGE + "", DEFAULT_USER, DEFAULT_PWD, DEFAULT_PROCESSOR,
                DEFAULT_NFS_DIRECTORY, DEFAULT_GW
            };
            return descomposed_line;
        } else {
            line = (String) lines.get(node + help_lines);
            String[] descomposed_line = line.split(" ");
            return descomposed_line;
        }
    }
    /**
     * The following function allows to read every node's position from the cityMob
     * file and to create the nodes in castadiva according to the information stored
     * in configuration/aps.txt
     * @param startingLine is the line in the cityMob file where the header ends and
     * node's initial position starts.
     * @parem nsText contains the cityMob file.
     */
    private int ReadNsNodeCITYMOB(int startingLine, String nsText[]) {
        int i, id;
        float positionX, positionY, positionZ;
        
        int apnum = 0; //Access point counter

        accessPoints = new APs();
        // Reading information from all AP. Information is structured as follows
        // $node_(0) set X_ 200
        for (i = startingLine; i < numberNodes * 3 + startingLine; i += 3) {
            positionX = Float.parseFloat(nsText[i].split(" ")[3]);
            positionY = Float.parseFloat(nsText[i + 1].split(" ")[3]);
            positionZ = Float.parseFloat(nsText[i + 2].split(" ")[3]);

            id = Integer.parseInt(nsText[i].split("\\)")[0].split("\\(")[1]);

            // We get AP configuration from the configuration/aps.txt file
            // If aps.txt contains no sufficient information, default values are returned.
            String[] ap = ObtainStoredApDataCITYMOB(apnum);
            apnum++;

            // We now try to set the name, from aps.txt or by default.
            String name = "";
            try {
                name = ap[11]; // The twleveth field is the name of the AP
            } catch (Exception e) {
                name = DEFAULT_ID + id; // This is a default name for the AP.
            }

            // Creation of a node from the previoulsy gathered information
            AP node = new AP(ap[1], ap[2], ap[0], ap[6], ap[7], name, positionX, positionY, positionZ,
                    Float.parseFloat(ap[5]), ap[9], ap[8], Integer.parseInt(ap[3]), DEFAULT_MODE,
                    ap[4], DEFAULT_GW);
            accessPoints.Add(node);
        }
        return i;
    }

    /****************************************************************************
     *
     *                          CASTADIVA RESET/LOAD/SAVE
     *
     ****************************************************************************/
    /**
     * Store in a file all Castadiva objects. Allow a user to load them later.
     * This function generate a file with the user given name and a folder with
     * all the objects of Castadiva saved in differents files.
     * @param folder The basic class path.
     */
    boolean SaveCastadiva(String folder) {
        JFrame frame = null;

        if (GenerateFolder(folder) && SaveCastadivaComputer(folder, FILE_COMPUTER)) {
            if (SaveCastadivaAps(folder, FILE_APS)) {
                if (SaveCastadivaData(folder, FILE_SCENARIO)) {
                    if (SaveExternalTraffic(folder, FILE_EXTERNAL_TRAFFIC)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Store the computer object.
     * @param folder The path where to save the file.
     * @param file The name of the file.
     */
    private boolean SaveCastadivaComputer(String folder, String file) {
        try {
            new SerialComputerStream(folder + File.separator +
                    file).save(computer);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Store the APs object.
     * @param folder The path where to save the file.
     * @param file The name of the file.
     */
    private boolean SaveCastadivaAps(String folder, String file) {
        try {
            new SerialAPStream(folder + File.separator +
                    file).save(accessPoints);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Save all data from the scenario definition in a special class.
     * @param folder The path where to save the file.
     * @param file The name of the file.
     * @see StoreData
     */
    private boolean SaveCastadivaData(String folder, String file) {
        StoreData info = new StoreData(minSpeed, maxSpeed, pause, x, y, RTS, GetSimulationTime(),
                allAddresses, nodePositions, mobilityModel, routingProtocol);
        try {
            new SerialScenarioDataStream(folder + File.separator +
                    file).save(info);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Save all external traffic in a special class.
     * @param folder The path where to save the file.
     * @param file The name of the file.
     * @see StoreData
     */
    private boolean SaveExternalTraffic(String folder, String file) {
        try {
            new SerialExternalTrafficDataStream(folder + File.separator +
                    file).save(externalTrafficFlow);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Load a file in Castadiva format. Allow the user to contiuate with an
     * experiment.
     * @see LoadAP
     * @see LoadComputer
     */
    void LoadCastadiva(String folder) {
        //Stop all previous simulations.
        File f = new File(folder);
        if(!f.getName().equals("Scenario")) {
            folder = folder + File.separator + "Scenario";
        }
        Reset();
        //Load files.
        LoadComputer(folder);
        //indicar fichero, si se llama desde el menu ppal viene especificado,
        //desde aqui solo conocemos la carpeta
        LoadAP(folder + File.separator + FILE_APS);
        LoadData(folder);
        LoadExternalTraffic(folder);
        if (debug) {
            System.out.println("    Load complete!");
        }
    }

    /** Load the file containing all the information about AP objects. This file must
     * be stored in Castadiva format.
     * @param folder The path where to save the file.
     * @see AP
     * @see TrafficRecord
     */
    void LoadAP(String folder) {
        List l;

        try {
            l = new SerialAPStream(folder).load();
            accessPoints = (APs) l.get(0);
            try {
                TrafficRecord tr = (TrafficRecord) accessPoints.GetTraffic().get(0);
            } catch (ArrayIndexOutOfBoundsException ex) {
                TrafficRecord tr = new TrafficRecord();
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            if (debug) {
                System.out.println("File Format not valid or problem loading the AP " +
                        "configuration.");
            }
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame, "File Format not valid or problem " +
                    "loading the AP configuration.", "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Load the file containing all the information about the computer. This file must
     * be stored in Castadiva format.
     * @param folder The path where to save the file.
     */
    private void LoadComputer(String folder) {
        List l;

        try {
            l = new SerialComputerStream(folder + File.separator + FILE_COMPUTER).load();
            computer = (Computer) l.get(0);
            computer.ChangeInterface(computer.card);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            //ex.printStackTrace();
            if (debug) {
                System.out.println("File format not valid or problem loading the " +
                        "computer configuration.");
            }
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame, "File format not valid or " +
                    "problem loading the computer configuration.", "Load Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * Load the file containing all the information about the scenario. This file must
     * be stored in Castadiva format.
     * @param folder The path where to save the file.
     * @see StoreData
     */
    private void LoadData(String folder) {
        List l;

        try {
            l = new SerialScenarioDataStream(folder + File.separator + FILE_SCENARIO).load();
            StoreData data = (StoreData) l.get(0);
            if (debug) {
                System.out.println("Reading data...");
            }
            ReadData(data);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            //ex.printStackTrace();
            if (debug) {
                System.out.println("File format not valid or problem loading the " +
                        "scenario configuration.");
            }
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame, "File format not valid or " +
                    "problem loading the scenario configuration.", "Load Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Load the file containing all the external traffic. This file must
     * be stored in Castadiva format.
     * @param folder The path where to save the file.
     * @see StoreData
     */
    private void LoadExternalTraffic(String folder) {
        List l;

        try {
            l = new SerialScenarioDataStream(folder + File.separator +
                    FILE_EXTERNAL_TRAFFIC).load();
            externalTrafficFlow = (List<ExternalTraffic>) l.get(0);
            if (debug) {
                System.out.println("Reading data...");
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            //ex.printStackTrace();
            if (debug) {
                System.out.println("File format not valid or problem loading the " +
                        "external traffic flow.");
            }
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame, "File format not valid or " +
                    "problem loading the external traffic flow.", "Load Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Read data from class StoreData and configure the scenario and mobility.
     */
    private void ReadData(StoreData infoToBeRead) {
        SetSimulationPause(infoToBeRead.pauseValue);
        SetMobilityMaxSpeed(infoToBeRead.maxSpeed);
        SetMobilityMinSpeed(infoToBeRead.minSpeed);
        SetRTS(infoToBeRead.rtsValue);
        SetBoundX(infoToBeRead.xBoundValue);
        SetBoundY(infoToBeRead.yBoundValue);
        SetSimulationTime(infoToBeRead.simulationTime);
        EndStopwatch();
        allAddresses = infoToBeRead.allAddressesDataValue;
        nodePositions = infoToBeRead.nodePositionsValue;
        mobilityModel = infoToBeRead.MobilityModel;
        routingProtocol = infoToBeRead.RoutingProtocol;

        if (MobiliyActivated()) {
            PositionateNodesInDeterminedSecond(0);
        }
    }

    /**
     * Obtain the folder where the Castadiva files would be saved.
     * @param file The path to the dummy file.
     * @see SaveCastadiva
     */
    private String CalculateSaveFolder(String file) {
        String[] name = null;
        name = file.split(FILE_PREFIX);
        return name[0];
    }

    /**
     * Start a new Castadiva simulation. Delete all objects and empty the simulation
     * window.
     */
    void Reset() {

        computer = new Computer();
        accessPoints = new APs();
        tableModel = new TrafficTableModel(accessPoints.GetTraffic());
        randomTrafficModel = new RandomTrafficTableModel(accessPoints.GetRandomTraffic());
        externalTrafficFlow = new ArrayList<ExternalTraffic>();
        SetStopwatch(0);
        EndStopwatch();
        x = DEFAULT_X_BOUND;
        y = DEFAULT_Y_BOUND;
        pause = 0;
    }

    /***************************************************************************
     *
     *                               SHOW FUNCTIONS
     *
     ****************************************************************************/
    /**
     * Print in console the components of a list.
     * @param list The list to be printed.
     */
    public void ShowList(List list) {
        for (int i = 0; i < list.size(); i++) {
            String show = list.get(i).toString();
        }
    }

    /**
     * Show all the instructions of a simulation.
     */
    private void ShowInstructions(List showedInstructions) {
        List nodo;
        System.out.println("");
        System.out.println("");
        System.out.println("****************************");
        System.out.println("        SIMULACION          ");
        System.out.println("****************************");
        for (int i = 0; i < showedInstructions.size(); i++) {
            System.out.println("");
            System.out.println("Node: " + (i + 1));
            System.out.println("--------------");
            nodo = (List) showedInstructions.get(i);
            for (int j = 0; j < nodo.size(); j++) {
                System.out.println("Node :"+nodo.get(j));
            }
        }
        System.out.println("****************************");
    }

    /**
     * Show in the System.out all traffic instructions created.
     * @see TrafficRecord
     */
    void ShowTrafficVector(Vector vector) {
        for (int i = 0; i < vector.size() - 1; i++) {
            TrafficRecord line = (TrafficRecord) vector.get(i);
            System.out.println(line.getTCPUDP());
        }
        System.out.println(" VECTOR END ");
    }

    /**
     * Show in the command console the traffic obtained.
     * @param trafficData. The traffic formatted in plain text.
     */
    public void ShowTraffic(List trafficData) {
        for (int i = 0; i < trafficData.size(); i++) {
            System.out.println(trafficData.get(i));
        }
    }

    /**
     * Show by console a determined matrix.
     */
    public void ShowFloatMatrix(float[][] matrix) {
        String line;
        for (int i = 0; i < matrix.length; i++) {
            line = "";
            for (int j = 0; j < matrix.length; j++) {
                line = line + " " + matrix[i][j];
            }
            //System.out.println(line);
        }
        System.out.println(" ");
    }

    /***************************************************************************
     *
     *                              OTHERS
     *
     ****************************************************************************/
    /**
     * Generate a NET of type C from a IP. It means that change
     * the last value of the IP by one 0.
     * @param ip The ip to obtain the C net.
     * @return The net where the IP belong to.
     */
    private String ObtainNetTypeC(String ip) {
        String tmp[];
        tmp = ip.split("\\.");
        return tmp[0] + "." + tmp[1] + "." + tmp[2] + ".0";
    }

    /**
     * Check if is a valid IP.
     * @return true if is a real ip.
     */
    public boolean CheckNetStructure(String ip) {
        String tmp[];
        tmp = ip.split("\\.");
        try {
            if (Integer.parseInt(tmp[0]) < 256 && Integer.parseInt(tmp[1]) < 256 &&
                    Integer.parseInt(tmp[2]) < 256 && Integer.parseInt(tmp[3]) < 256 &&
                    Integer.parseInt(tmp[0]) >= 0 && Integer.parseInt(tmp[1]) >= 0 &&
                    Integer.parseInt(tmp[2]) >= 0 && Integer.parseInt(tmp[3]) >= 0) {
                return true;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
        return false;
    }

    /**
     * Copy 'n' elements of one vector in other.
     * @param source The vector witch must be cloned.
     * @param destination The vector to add all elements.
     * @param n The number of elements to copy.
     */
    void VectorCopyTo(Vector source, Vector destination, Integer n) {
        destination.removeAllElements();
        for (int i = 0; i < n; i++) {
            destination.add(source.get(i));
        }
    }

    /**
     * Store text in a file. The text must be written in a String list.
     * @param dataList The text to be written
     * @file the path to the file.
     */
    private void SaveInFile(List dataList, String file) {
        File outputFile;
        byte b[];
        //Se guarda en el fichero
        outputFile = new File(file);
        try {
            FileOutputStream outputChannel = new FileOutputStream(outputFile);
            for (int i = 0; i < dataList.size(); i++) {
                b = (dataList.get(i).toString() + "\n").getBytes();
                try {
                    outputChannel.write(b);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            try {
                outputChannel.close();
                outputFile.setExecutable(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (FileNotFoundException ex) {
            String text = "Impossible to generate file:\n\t" + file +
                    ". \nIs the working directory created properly?\n" +
                    "Check into \"Configuration -> Configurate the Computer\"";
            String title = "No such file or directory";
            ShowSaveErrorMessage(text, title);
        }
    }

    /**
     * Read the text stored in a file.
     */
    String ReadTextFile(String file) {
        File licence = new File(file);
        String text = "Error opening the file.";
        try {
            FileInputStream inputData = new FileInputStream(licence);
            byte bt[] = new byte[(int) licence.length()];
            text = new String(bt);
            inputData.close();
        } catch (IOException ex) {
             System.out.println("Error while reading the text "+text+" in the file "+file+" :"+ex);
        }
        return text;
    }

    /**
     * Fetch the entire contents of a text file, and return it in a List.
     * The returned list has a line of the file in each position.
     *
     * @param file is a file which already exists and can be read.
     */
    public List<String> ReadTextFileInLines(File file) {
        List<String> contents = new ArrayList<String>();

        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = input.readLine()) != null) {
                contents.add(line);
            }
        } catch (FileNotFoundException ex) {
            //ex.printStackTrace();
        } catch (IOException ex) {
            //ex.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        return contents;
    }

    /**
     * Set a instruction to a node, without deleting the old node instructions.
     * @param nodesInstructionList A list that contain all nodes instructions.
     * @param instruction The instruction to add.
     * @param node The number of the node in the list.
     */
    private void SetInstructionToNode(List<List<String>> nodesInstructionList, String instruction, int node) {
        List<String> auxList;
        try {
            auxList = nodesInstructionList.get(node);
        } catch (IndexOutOfBoundsException ex) {
            auxList = new ArrayList<String>();
        }
        auxList.add(instruction);
        nodesInstructionList.set(node, auxList);
    }

    /**
     * Add a instruction to a node, without deleting the old node instructions.
     * @param nodesInstructionList A list that contain all nodes instructions.
     * @param instruction The instruction to add.
     * @param node The namber of the node in the list.
     */
    private void AddInstructionToNode(List<List<String>> nodesInstructionList, String instruction, int node) {
        List<String> auxList;
        try {
            auxList = nodesInstructionList.get(node);
        } catch (IndexOutOfBoundsException ex) {
            auxList = new ArrayList<String>();
        }
        auxList.add(instruction);
        nodesInstructionList.add(node, auxList);
    }

    private void AddInstructionToNodeRedirect(List<List<String>> nodesInstructionList, String instruction, int node) {
        List<String> auxList;
        try {
            auxList = nodesInstructionList.get(node);
        } catch (IndexOutOfBoundsException ex) {
            auxList = new ArrayList<String>();
        }
        auxList.add(instruction);
    }

    /**
     * Create in the file system the desired folder.
     * @param folder The path to the desired folder.
     */
    public boolean GenerateFolder(String folder) {
        //Save the objects of Castadiva.
        File dirFileSave = new java.io.File(folder);
        if (!dirFileSave.exists()) {
            try {
                dirFileSave.mkdirs();
            } catch (SecurityException se) {
                return false;
            }
        }
        return true;
    }

    /**
     * Replace one pattern with other in String.
     * @param str The string.
     * @param pattern The substring to replace.
     * @param replace The new substring.
     */
    static String Replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e + pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }

    /**
     * Delete a directory in a filesystem.
     * @return <code>true</code> if the directory is deleted.
     */
    private boolean DeleteDirectory(String directory) {
        File path = new File(directory);
        return DeletePath(path);
    }

    /**
     * Delete all files in a Path.
     * @return <code>true</code> if all files are deleted.
     */
    private boolean DeletePath(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    if (!(files[i].toString().contains("notDelete") ||
                            files[i].toString().contains("bin"))) {
                        DeletePath(files[i]);
                    }
                } else {
                    //Don't delete files with string "Flow". This files are
                    //used to generate the traffic TCP
                    if (!(files[i].toString().contains("Flow") ||
                            files[i].toString().contains("aodv") ||
                            files[i].toString().contains("olsrd") ||
                            files[i].toString().contains(".dump"))) {
                        files[i].delete();
                    }
                }
            }
        }
        return (path.delete());
    }

    /**
     * Generate a n x m matrix with default value.
     */
    private float[][] GenerateMatrix(Integer n, Integer m, Float value) {
        float[][] matrix = new float[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = value;
            }
        }
        return matrix;
    }

    /**
     * Generate a n x m matrix with default value.
     */
    private Integer[][] GenerateMatrix(Integer n, Integer m, Integer value) {
        Integer[][] matrix = new Integer[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = value;
            }
        }
        return matrix;
    }

    /**
     * Generate a n x m matrix with default value.
     */
    private boolean[][] GenerateMatrix(Integer n, Integer m, boolean value) {
        boolean[][] matrix = new boolean[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = value;
            }
        }
        return matrix;
    }

    /**
     * Generate a n vector with default value.
     */
    private Integer[] GenerateVector(Integer n, Integer value) {
        Integer[] vector = new Integer[n];
        for (int i = 0; i < n; i++) {
            vector[i] = value;
        }
        return vector;
    }

    /**
     * Show a matrix into the standar output.
     */
    private void PrintMatrix(Object[][] matrix) {
        String line = "";
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                line = line + matrix[i][j] + "\t";
            }
            System.out.println(line);
            line = "";
        }
    }

    /**
     * Show a matrix into the standar output.
     */
    private void PrintMatrix(float[][] matrix) {
        String line = "";
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                line = line + matrix[i][j] + "\t";
            }
            System.out.println(line);
            line = "";
        }
    }

    /**
     * Show a list in one line on the standar output.
     */
    private void PrintList(List list) {
        String print = "l: ";
        for (int i = 0; i < list.size(); i++) {
            print = print + " " + list.get(i);
        }
        System.out.println(print + " s: " + list.size());
    }

    /**
     * Show a vector on the standar output.
     */
    private void PrintStringVector(String[] vector) {
        for (int i = 0; i < vector.length; i++) {
            System.out.println(vector[i]);
        }
    }

    /**
     * Show an error.
     */
    void ShowSaveErrorMessage(String text, String title) {
        JFrame frame = null;
        if (debug) {
            System.out.println("Error message :"+text);
        }
        JOptionPane.showMessageDialog(frame, text, title, JOptionPane.ERROR_MESSAGE);
    }


    /****************************************************************************
     *
     *                              SECONDARY CLASSES
     *
     ****************************************************************************/
    /**
     * This abstract class generate the standar methods to connect by SSH.
     * Based on the JCraft tools.
     */
    private abstract class SSH extends Thread {

        Channel channel;
        Session session;
        String answer;
        boolean print;
        List commands;
        List number;
        String sourceDirectory;

        @Override
        public void run() {
            if (Connect()) {
                for (int i = 0; i < commands.size(); i++) {
                    if (i < number.size()) {
                        RunCommand((String) commands.get(i), sourceDirectory,
                                "CastadivaTTCP" + number.get(i));
                    } else {
                        RunCommand((String) commands.get(i), sourceDirectory,
                                "CastadivaTTCPnone");
                    }
                }
            }
        }

        private void SaveExitInDirectory(String text, String directory, String file) {
            File exitDirectory;

            byte b[] = text.getBytes();
            exitDirectory = new File(directory + File.separator + file);
            FileOutputStream exitChannel = null;
            try {
                exitChannel = new FileOutputStream(exitDirectory);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            try {
                exitChannel.write(b);
                exitChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void RunCommand(String command, String directory, String file) {
            try {
                channel = session.openChannel("exec");
                ((ChannelExec) channel).setCommand(command);
                channel.setXForwarding(true);
                channel.setInputStream(null);
                ((ChannelExec) channel).setErrStream(System.err);
                InputStream in = channel.getInputStream();
                channel.connect();

                byte[] tmp = new byte[1024];
                while (true) {
                    while (in.available() > 0) {
                        int i = in.read(tmp, 0, 1024);
                        if (i < 0) {
                            break;
                        }
                        if (print) {
                            answer = (new String(tmp, 0, i));
                            SaveExitInDirectory(answer, directory, file);
                        }
                    }
                    if (channel.isClosed()) {
                        if (debug) {
                            System.out.println("exit-status: " + channel.getExitStatus());
                        }
                        break;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (Exception ee) {
                        if(debug){
                            System.out.println("Error while puttin thread to sleep :"+ee);
                        }
                    }
                }
            //Disconnect();
            } catch (Exception e) {
                if (debug) {
                    System.out.println("Error while running command :"+e);
                }
            }
        }

        public void Disconnect() {
            try {
                channel.disconnect();
            } catch (NullPointerException npe) {
                if (debug) {
                    System.out.println("Channel already disconnected:"+npe);
                }
            }
            try {
                session.disconnect();
            } catch (NullPointerException npe) {
                if (debug) {
                    System.out.println("Session already disconnected:"+npe);
                }
            }
        }

        abstract boolean Connect();

        abstract class MyUserInfo implements UserInfo {
        }
    }

    /**
     * This class allow the connection to one AP by SSH. Make a connection
     * and send an instruction to the AP.
     * @author Jorge Hortelano Otero.
     * @version %I%, %G%
     * @since 1.4
     */
    private class SshNode extends SSH {

        AP node;

        /**
         * Creates a new instance of SshNode.
         * @param accessPoint The AP  where the instructions must be exectued.
         * @param instructins A list of bash instructions.
         * @param printExit A <code>boolean</code> value that indicate if
         * the exit of the instruction is needed or not.
         * @param numInst The number/name of The AP. Its used to write the name
         * of the file where the output will be written.
         * @param directoryAddress The directory where the output will be written.
         * Can be a local directory in the AP or a NFS directory.
         */
        public SshNode(AP accessPoint, List instructions, boolean printExit,
                List numInst, String directoryAddress) {
            node = accessPoint;
            commands = instructions;
            print = printExit;
            number = numInst;
            sourceDirectory = directoryAddress;
        }

        public boolean Connect() {
            boolean ok = true;
            try {
                JSch jsch = new JSch();
                String host = node.WhatEthIP();
                String user = node.WhatUser();
                session = jsch.getSession(user, host, 22);
                UserInfo ui = new MyUserInfo(node);
                session.setUserInfo(ui);
                session.connect();
            } catch (JSchException e) {
                if(debug)
                {
                    System.out.println("Error while connecting :"+e);
                }
                String text = e.getMessage() + " in AP " + node.WhatAP();
                String title = "Conexion status";
                ShowSaveErrorMessage(text, title);
                ok = false;
            }
            return ok;
        }

        public class MyUserInfo implements UserInfo {

            AP node;

            public MyUserInfo(AP accessPoint) {
                node = accessPoint;
            }

            public String getPassword() {
                return node.WhatPwd();
            }

            public boolean promptYesNo(String str) {
                return true;
            }

            public String getPassphrase() {
                return null;
            }

            public boolean promptPassphrase(String message) {
                return true;
            }

            public boolean promptPassword(String message) {
                return true;
            }

            public void showMessage(String message) {
            }
        }
    }

    /**
     * This class allow the connection to an IP by SSH.
     * Useful to connect to the AP when are not installed in CASTADIVA.
     * @author Jorge Hortelano Otero.
     * @version %I%, %G%
     * @since 1.4
     */
    private class SshHost extends SSH {

        String sshIp;
        String sshUser;
        String sshPwd;

        /**
         * Creates a new instance of SshNode.
         * @param ip The IP where do you want to connect.
         * @param user The ssh user to login.
         * @param pwd The password to login.
         * @param instructins A list of bash instructions.
         * @param printExit A <code>boolean</code> value that indicate if
         * the exit of the instruction is needed or not.
         * @param numInst The number/name of The AP. Its used to write the name
         * of the file where the output will be written.
         * @param directoryAddress The directory where the output will be written.
         * Can be a local directory in the AP or a NFS directory.
         */
        public SshHost(String ip, String user, String pwd, List instructions,
                boolean printExit, List numInst, String directoryAddress) {
            sshIp = ip;
            sshUser = user;
            sshPwd = pwd;
            commands = instructions;
            print = printExit;
            number = numInst;
            sourceDirectory = directoryAddress;
        }

        public boolean Connect() {
            boolean ok = true;
            try {
                JSch jsch = new JSch();
                session = jsch.getSession(sshUser, sshIp, 22);
                UserInfo ui = new MyUserInfo(sshPwd);
                session.setUserInfo(ui);
                session.connect();
            } catch (Exception e) {
                if (debug) {
                    System.out.println(e.getMessage() + " in connection to " + sshIp);
                }
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame, e.getMessage() + " in " +
                        sshIp, "Connection status", JOptionPane.ERROR_MESSAGE);
                ok = false;
            }
            return ok;
        }

        public class MyUserInfo implements UserInfo {

            String password;

            public MyUserInfo(String password) {
                this.password = password;
            }

            public String getPassword() {
                return password;
            }

            public boolean promptYesNo(String str) {
                return true;
            }

            public String getPassphrase() {
                return null;
            }

            public boolean promptPassphrase(String message) {
                return true;
            }

            public boolean promptPassword(String message) {
                return true;
            }

            public void showMessage(String message) {
            }
        }
    }

    /**
     * This is the abstract class where all Serializable objects inherit the Load
     * Write functions. Allow to save the simulation in Castadiva format.
     * @author Jorge Hortelano Otero.
     * @version %I%, %G%
     * @since 1.4
     */
    private class SerialParent {

        protected String FILENAME;

        public void write(Object objectToSave) throws IOException {
            ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(
                    new FileOutputStream(FILENAME)));
            os.writeObject(objectToSave);
            os.close();
        }

        public void save(Object o) throws IOException {
            List<Object> l = new ArrayList<Object>();
            l.add(o);
            write(l);
        }

        public List load() throws IOException, ClassNotFoundException,
                FileNotFoundException {
            List l;
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(FILENAME));
            l = (List) is.readObject();
            is.close();
            return l;
        }

        public void dump() throws IOException, ClassNotFoundException {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(FILENAME));
            System.out.println("Dump :"+is.readObject());
            is.close();
        }
    }

    /**
     * Inherit from SerialParent this class allow to save the AP structure in a
     * file.
     * @see SerialParent
     * @see SerialScenarioData
     * @see SerialComputerStream
     */
    private class SerialAPStream extends SerialParent {

        public SerialAPStream(String file) {
            FILENAME = file;
        }

        public APs loadAP() throws IOException, ClassNotFoundException {
            APs tmp_accessPoints;
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(FILENAME));
            tmp_accessPoints = (APs) is.readObject();
            is.close();
            return tmp_accessPoints;
        }
    }

    /**
     * Inherit from SerialParent this class allow to save the Computer structure in a
     * file.
     * @see SerialParent
     * @see SerialAPStream
     * @see SerialScenarioData
     */
    private class SerialComputerStream extends SerialParent {

        public SerialComputerStream(String file) {
            FILENAME = file;
        }
    }

    /**
     * Inherit from SerialParent this class allow to save the scenario structure in a
     * file.
     * @see SerialParent
     * @see SerialAPStream
     * @see SerialComputerStream
     */
    private class SerialScenarioDataStream extends SerialParent {

        public SerialScenarioDataStream(String file) {
            FILENAME = file;
        }

        public StoreData loadScenario() throws IOException, ClassNotFoundException {
            StoreData info;
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(FILENAME));
            info = (StoreData) is.readObject();
            is.close();
            return info;
        }
    }

    /**
     * Inherit from SerialParent this class allow to save the external traffic in a
     * file.
     * @see SerialParent
     * @see SerialAPStream
     * @see SerialComputerStream
     */
    private class SerialExternalTrafficDataStream extends SerialParent {

        public SerialExternalTrafficDataStream(String file) {
            FILENAME = file;
        }

        public List<ExternalTraffic> loadExternalTraffic() throws IOException, ClassNotFoundException {
            List<ExternalTraffic> extTraffic;
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(FILENAME));
            extTraffic = (List<ExternalTraffic>) is.readObject();
            is.close();
            return extTraffic;
        }
    }

    /**
     * This class generate a time counter to control the simulation steps and
     * wait for the AP response when a Simulation begin.
     */
    private class CastadivaMainTimerTask extends TimerTask {

        public void run() {
            stopwatch++;

            //When all is ended...
            if (stopwatch == GetRealSimulationTime()) {
                LaunchStatisticRecovery(statisticsControl);
            }

            //Seek for the statistics when the time is over.
            if (stopwatch > GetRealSimulationTime()) {
                simulating = false;
                EndStatisticsSeeker();
            }
        }
    }

    /**
     * This class is useful to control when all traffic instructions are ended.
     * @see ObtainStatisticsThread
     */
    class StatisticExchangeBuffer {

        private int buffer[];
        private int size;

        StatisticExchangeBuffer(int length) {
            if (length > 0) {
                buffer = new int[length];
                size = length;
            } else {
                buffer = new int[0];
                size = 0;
            }
        }

        public synchronized void ShowBufferValue() {
            for (int i = 0; i < size; i++) {
                System.out.print(buffer[i]);
            }
            System.out.println("");
        }

        public synchronized String ReturnBufferValue() {
            String value = "";
            for (int i = 0; i < size; i++) {
                value = value + buffer[i];
            }
            return value;
        }

        public synchronized void ChangeBufferValue(int position, int value) {
            buffer[position] = value;
        }

        private synchronized boolean IsEndOfStatistics() {
            for (int i = 0; i < size; i++) {
                if (buffer[i] == 0) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * One thread for each traffic instruction to check when the TTCP tool has ended.
     * @see TrafficRecord
     */
    private class ObtainStatisticsThread extends Thread {

        private String file;
        private Vector<TrafficRecord> trafficData;
        private StatisticExchangeBuffer statisticVector;
        private int fileSize;
        private int line;

        /** Creates a new instance of ObtainStatisticsThread */
        public ObtainStatisticsThread(String inputFile, int inputFileSize,
                Vector<TrafficRecord> traffic, int where,
                StatisticExchangeBuffer statisticExchange) {
            file = inputFile;
            trafficData = traffic;
            fileSize = inputFileSize;
            line = where;
            statisticVector = statisticExchange;
        }

        @Override
        public void run() {
            ReadTrafficStatistic();
        }

        private void ReadUdpTrafficLine(String text, int line, TrafficRecord traffic) {
            String tmp;
            Integer numPackets;
            Float speed;
            Float delay;

            //Calculating transmission speed.
            try {
                speed = Float.parseFloat(text.substring(text.indexOf("Throughput: ") + 12, text.indexOf(" Kb/s")));
                traffic.setLastSpeed(speed);
            } catch (StringIndexOutOfBoundsException sob) {
                traffic.setLastSpeed(Float.parseFloat("0"));
            }
            //Calculating received data.
            try {
                numPackets = Integer.parseInt(text.substring(text.indexOf("Received: ") + 10, text.indexOf(" packets.")));
                traffic.setPacketsReceived(numPackets);
            } catch (StringIndexOutOfBoundsException sob) {
                traffic.setPacketsReceived(Integer.parseInt("0"));
            }

            try{
                delay = Float.parseFloat(text.substring(text.indexOf("Delay: ") + 7, text.indexOf(" ms")));
                traffic.setMeanDelay(delay);
            } catch(StringIndexOutOfBoundsException sob){
                System.err.println("Traffic not detected.");
                traffic.setMeanDelay(Float.parseFloat("0"));
            } catch(Exception e) {
                e.printStackTrace();
                traffic.setMeanDelay(Float.parseFloat("0"));
            }

            trafficData.setElementAt(traffic, line);
        }

        private void ReadTcpTrafficLine(String text, int line, TrafficRecord traffic) {
            String tmp;
            Integer numPackets;
            Float speed;

            //Calculating transmission speed.
            try {
                speed = Float.parseFloat(text.substring(text.indexOf("Throughput: ") + 12, text.indexOf(" Kb/s")));
                traffic.setLastSpeed(speed);
            } catch (StringIndexOutOfBoundsException sob) {
                traffic.setLastSpeed(Float.parseFloat("0"));
            }
        }

        public void ReadTrafficStatistic() {
            File statistics = new File(file);
            String text = "Error opening the file.";
            List<String> contents = new ArrayList<String>();
            BufferedReader input = null;

            //When the file is too short to be an ended file...
            try {
                while (statistics.length() < fileSize) {
                    statistics = new File(file);
                    try {
                        sleep(2000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                /*FileInputStream inputData = new FileInputStream(statistics);
                byte bt[] = new byte[(int) statistics.length()];
                text = new String(bt);
                System.out.println("1;"+text);
                inputData.close();
                inputData = null;*/
                input = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));
                while ((text = input.readLine()) != null) {
                    contents.add(text);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
            //Search the desired data.
            text = contents.get(0);
            if (!text.trim().equals("")) {
                //Mark this traffic line as is ended.
                statisticVector.ChangeBufferValue(line, 1);
                TrafficRecord traffic = (TrafficRecord) trafficData.get(line);
                if (traffic.getTCPUDP().equals("UDP")) {
                    ReadUdpTrafficLine(text, line, traffic);
                }
                if (traffic.getTCPUDP().equals("TCP")) {
                    ReadTcpTrafficLine(text, line, traffic);
                }
            }
        }
    }





    /**
     * Store the characteristics of one node imported from NS in a determined second.
     */
    private class NodeImportedData {

        public Float xCoordinate;
        public Float yCoordinate;
        public Float zCoordinate = (float) 0;
        public Integer second;
        public Float speed;
        public int nodeNumber;

        NodeImportedData(int node, Float newX, Float newY, Float determinedSpeed, Integer time) {
            xCoordinate = newX;
            yCoordinate = newY;
            speed = determinedSpeed;
            second = time;
            nodeNumber = node;
        }
    }

    /**
     * Store the entire information of the mobility from NS.
     */
    private class NodePositionsFromNsMobility {

        List<NodeImportedData>[] PositionOfEachNode;

        NodePositionsFromNsMobility(Integer nodes) {
            PositionOfEachNode = new List[nodes];
            for (int i = 0; i < nodes; i++) {
                List<NodeImportedData> l = new ArrayList<NodeImportedData>();
                PositionOfEachNode[i] = l;
            }
        }

        public void AddNodeInformation(Integer node, Float xPosition, Float yPosition,
                Float speed, Float second) {
            List<NodeImportedData> l;
            NodeImportedData nodeData = new NodeImportedData(node, xPosition, yPosition,
                    speed, Math.round(second));
            l = PositionOfEachNode[node];
            l.add(nodeData);
            PositionOfEachNode[node] = l;
        }

        public List<NodeImportedData> GetNodeInformation(Integer node) {
            return PositionOfEachNode[node];
        }

        public Integer Size() {
            return PositionOfEachNode.length;
        }
    }

    /****************************************************************************
     *
     *                       NS IMPORT/EXPORT PARA CITYMOB
     *
     ****************************************************************************/

}
