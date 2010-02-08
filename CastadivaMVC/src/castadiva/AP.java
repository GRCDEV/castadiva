package castadiva;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class store the information about the Access Point device such as routers. 
 * Each object represents an Access Point and the different data tha differenciate it from others.
 *
 * @author Jorge Hortelano.
 * @since 1.4
 * @version %I%, %G%
 */
public class AP implements Serializable {

    private InetAddress ip;
    private InetAddress wifiIP;
    private InetAddress gateway;
    private String wifiMac;
    private String user;
    private String id;
    private String processor;
    private String password;
    private String workingDirectory;
    private Integer channel;
    private String mode;
    private String wifiDevice;
    public float x;
    public float y;
    public float z;
    public float range;
    float inputTraffic;
    float outputTraffic;
    public boolean showRange = true;  //Shows the range circle in the simulation window. Used to see only the range of some nodes.

   /**
     * The constructor. Generate a new AP ready to be used in the simulation.
     *
     * @param address The ethernet address to connect the computer with the AP.
     * @param wifiAddress The address for the WiFi card. It would use for the 
     * simulation to send a package to another AP.
     * @param user The user defined in the SSH connection to access to the AP.
     * @param pwd The password to connect to the AP by SSH.
     * @param id A name or tag to differenciate the AP from each other.
     * @param x The starting position of the AP in the simulation. Coordinate X.
     * @param y The starting position of the AP in the simulation. Coordinate Y.
     * @param z The starting position of the AP in the simulation. Coordinate Z.
     * @param range The range of the signal of the WiFi device.
     * @param directory the directory used to store the simulation data (can be a 
     * NFS directory).
     */
    public AP(String address, String wifiAddress, String wifiMac, String user, String pwd, String id,
            float x, float y, float z, float range, String directory, String processor, Integer channel,
            String mode, String wfDevice, String tmp_gw) {
        this.user = user;
        this.id = id;
        password = pwd;
        this.x = x;
        this.y = y;
        this.z = z;
        this.range = range;
        this.channel = channel;
        this.mode = mode;
        inputTraffic = 0;
        outputTraffic = 0;
        workingDirectory = directory;
        this.wifiMac = wifiMac;
        this.wifiDevice = wfDevice;
        this.processor = processor;

        try {
            ip = InetAddress.getByName(address);
            wifiIP = InetAddress.getByName(wifiAddress);
            gateway = InetAddress.getByName(tmp_gw);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This function return the name or tag that differenciate the AP.
     */
    public String WhatAP() {
        return id;
    }

    /**
     * Return the user stored to connect by SSH.
     */
    public String WhatUser() {
        return user;
    }

    /**
     * Return the password stored to connect by SSH.
     */
    public String WhatPwd() {
        return password;
    }

    /**
     * Return the IP of the ethernet card used to connecto to the computer.
     */
    public String WhatEthIP() {
        return ip.getHostAddress();
    }

    /**
     * Return the IP of the WiFi card used to connecto to the others AP.
     */
    public String WhatWifiIP() {
        return wifiIP.getHostAddress();
    }
    
     /**
     * Return the IP of the GW to reach other networks.
     */
    public String WhatGW() {
        return gateway.getHostAddress();
    }

    /**
     * Return the MAC of the WiFi card used to connecto to the others AP.
     */
    public String WhatWifiMac() {
        return wifiMac;
    }

    /**
     * Return the directory where the AP store all the data generated in the simulation. 
     * Can be a NFS directory.
     */
    public String WhatWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * Return the processor type of the router.
     */
    public String WhatProcessor() {
        return processor;
    }

    public Integer WhatChannel() {
        return channel;
    }

    public String WhatMode() {
        return mode;
    }

    /**
     * Return the device used by the system "eth1, eth2..."
     */
    public String WhatWifiDevice() {
        return wifiDevice;
    }
    
    public float WhatX() {
        return x;
    }
    public float WhatY() {
        return y;
    }
    public float WhatZ() {
        return z;
    }
    
}
