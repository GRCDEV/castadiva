package castadiva.TrafficRecords;

import castadiva.*;


/**
 * This class is a data structure to store the information needed to define a
 * traffic flux between two AP. The traffic is defined by:
 * <ul>
 * <li> Source: The source AP of the traffic.
 * <li> Adress: The destination AP of the traffic.
 * <li> Traffic class: TCP/UDP
 * <li> Time: Starting time and ending ti.
 * <li> Packets: Size, packets per second and total number.
 * </ul>
 *
 * @author jorge
 * @version %I%, %G%
 * @since 1.4
 */
public class TrafficRecord extends TrafficSchema {
    private String source;
    private String address;
    private Float lastSpeed;
    private Integer packetsReceived;
    //QoS
    private String accessCategory;
    private Boolean dacme;
    private Integer delay;
    private Boolean redirect;
    private Float mean_delay;

    /** Creates a new instance of TrafficRecord */
    public TrafficRecord() {
        trafficKind = "UDP";
        source = "";
        address = "";
        size = 512;
        start = 10;
        stop = 110;
        transferSize = 1000000;
        packetPerSeconds = 4;
        totalPackets = 400;
        lastSpeed = (float)0;
        packetsReceived = 0;
        accessCategory = "AC_BE";
        dacme = false;
        delay = 0;
        redirect = false;
        mean_delay = (float)0.0;

    }
    
    public TrafficRecord(String traffic, String source, String address, Integer size, 
            Integer start, Integer stop, Integer transferSize, Integer packetPerSeconds,
            Integer totalPackets, Float lastSpeed, Integer packetsReceived, String accessCategory,
            Boolean dacme, Integer delay, Boolean redirect, Float mean_delay){
        trafficKind = traffic;
        this.source = source;
        this.address = address;
        this.size = size;
        this.start = start;
        this.stop = stop;
        this.transferSize = transferSize;
        this.packetPerSeconds = packetPerSeconds;
        this.totalPackets = totalPackets;
        this.lastSpeed = lastSpeed;
        this.packetsReceived = packetsReceived;
        this.accessCategory = accessCategory;
        this.dacme = dacme;
        this.delay = delay;
        this.redirect = redirect;
        this.mean_delay = mean_delay;

    }



    /**
     * Return the source of a traffic declaration.
     */
    public String getSource(){
        return source;
    }
    
    /**
     * Return the destination of a traffic declaration.
     */
    public String getAddress(){
        return address;
    }
    
    /**
     * Return the KB/seconds obtained in the last simulation.
     */
    public Float getLastSpeed(){
        return lastSpeed;
    }
    
    /**
     * Return the total of packets received in the last simulation.
     */
    public Integer getPacketsReceived(){
        return packetsReceived;
    }
    
    /**
     * Return the % of the packets received in the last simulation.
     */
    public Float getPacketsPerCentReceived(){
        Float value = (new Float(packetsReceived)/ new Float(totalPackets))*100;
        if(value <= 0) value = new Float(0);
        return value;
    }

    public String getAccessCategory() {
        return accessCategory;
    }

    public int getIntAccessCategory() {
        if(accessCategory.equals("AC_BK"))
            return 0;
        if(accessCategory.equals("AC_BE"))
            return 1;
        if(accessCategory.equals("AC_VI"))
            return 2;
        if(accessCategory.equals("AC_VO"))
            return 3;
        return 1;
    }

    public String getCSAccessCategory() {
        if(accessCategory.equals("AC_BK"))
            return "CS1";
        if(accessCategory.equals("AC_BE"))
            return "BE";
        if(accessCategory.equals("AC_VI"))
            return "CS5";
        if(accessCategory.equals("AC_VO"))
            return "CS6";
        return "BE";
    }

    public Boolean getDacme() {
        return dacme;
    }

    public Integer getDelay() {
        return delay;
    }

    public Boolean getRedirect() {
        return redirect;
    }

    public Float getMeanDelay() {
        return mean_delay;
    }
    /**
     * Change the AP source of a traffic flux.
     * @param direccionOrigen Is the name of the AP.
     */
    public void setSource(String direccionOrigen){
        source = direccionOrigen;
    }
    
    /**
     * Change the AP adress of a traffic flux.
     * @param direccionDestino Is the name of the AP.
     */
    public void setAddress(String direccionDestino){
        address = direccionDestino;
    }
    
    /**
     * Change the last speed obtained in a previous simulation.
     * @param speed The simulation must change this value one time is obtained.
     */
    public void setLastSpeed(Float speed){
        lastSpeed = speed;
    }
    
    /**
     * Change the value of packets received in the previous simulation.
     * @param pkt The simulation must change this value one time is obtained.
     */
    public void setPacketsReceived(Integer pkt){
        packetsReceived = pkt;
    }

    public void setAccessCategory(String ac) {
        this.accessCategory = ac;
    }

    public void setDacme(Boolean dacme) {
        this.dacme = dacme;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public void setRedirect(Boolean redirect) {
        this.redirect = redirect;
    }
    public void setMeanDelay(Float mean_delay) {
        this.mean_delay = mean_delay;
    }
}
