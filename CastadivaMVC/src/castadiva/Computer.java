package castadiva;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * This class generate an object that represents the computer. This computer is
 * the one that is connected to the APs by a net and run the CASTADIVA project.
 *
 * @author Jorge Hortelano Otero
 * @version %I%, %G%
 * @since 1.4
 */
public class Computer implements Serializable {
    private InetAddress ipIntAdrs;
    private String ip;
    private String workingDirectory = "/CASTADIVA";
    protected String card;
    transient private NetworkInterface computerInterface = null;
    
    /**
     * The class constructor.
     */
    public Computer() {
        Enumeration dispositivos = null;
        try {
            dispositivos = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        computerInterface = (NetworkInterface) dispositivos.nextElement();
        card = TranslateInterfaceToString(computerInterface.toString());
        CalculateIpFromInterface();
    }
    
    /**
     * Return the name of a Computer Interface.
     */
    public String TranslateInterfaceToString(String computerInterface){
        String[] show, piece;
        
        piece = computerInterface.split(":");
        show = piece[1].split(" ");
        return show[0];
    }
    
    /**
     * Return the name of the interface that the computer use to connect
     * to the APs.
     */
    public String WhatInterfaceString(){
        String[] show, piece;
        
        piece = computerInterface.toString().split(":");
        show = piece[1].split(" ");
        return show[0];
    }
    
    /**
     * Returns the computer interface used to connect.
     */
    public NetworkInterface WhatInterface(){
        return computerInterface;
    }
    
    /**
     * Change the computer interface by other.
     * @param eth the new interface.
     */
    public void ChangeInterface(String eth){
        card = eth;
        try {
            computerInterface = NetworkInterface.getByName(eth);
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Fill the IP from an Interface choosed before.
     */
    void CalculateIpFromInterface(){
        String address = null;
        Enumeration addresses = computerInterface.getInetAddresses();
        
        while (addresses.hasMoreElements()) {
            ipIntAdrs = (InetAddress) addresses.nextElement();
            
            // the last available ip address will be returned
            ip = ipIntAdrs.getHostAddress();
        }
    }
    
    /**
     * Return the IP of the computer.
     */
    public String WhatIP(){
        return ip;
    }
    
    /**
     * Return the directory where the data will be saved.
     */
    public String WhatWorkingDirectory(){
        return workingDirectory;
    }

    public void setWorkingDirectory(String dir){
        workingDirectory = dir;
    }
    
    /**
     * Change the directory where all simulation files would be stored.
     */
    public void ChangeWorkingDirectory(String directory){
        workingDirectory = directory;
    }
}
