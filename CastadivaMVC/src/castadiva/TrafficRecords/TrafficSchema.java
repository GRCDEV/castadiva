/*
 * TrafficSchema.java
 *
 * Created on 6 de noviembre de 2006, 12:05
 *
 *
 */

package castadiva.TrafficRecords;

import java.io.Serializable;

/**
 *
 *    @author jorge
 *
 */

public abstract class TrafficSchema implements Serializable{
    String trafficKind;
    Integer size;
    Integer start;
    Integer stop;
    Integer packetPerSeconds;
    Integer transferSize;
    Integer totalPackets;
    
    /**
     * Return if the traffic declared is UDP or TCP.
     */
    public String getTCPUDP(){
        return trafficKind;
    }
    
    /**
     * Return the size of the packet.
     */
    public Integer getSize(){
        return size;
    }
    
    /**
     * Return the second when the traffic start to be sended.
     */
    public Integer getStart(){
        return start;
    }
    
    /**
     * Return the second when the traffic ends.
     */
    public Integer getStop(){
        return stop;
    }
    
    /**
     * Return how many packets must be sended by second.
     */
    public Integer getPacketsSeconds(){
        return packetPerSeconds;
    }
    
    /**
     * Return the total traffic por TCP traffic.
     */
    public Integer getTransferSize(){
        return transferSize;
    }
    
    /**
     * Return the total packets sended in the traffic flux.
     */
    public Integer getMaxPackets(){
        return totalPackets;
    }
    
    /**
     * Set the traffic to a specified protocol (UDP or TCP).
     * @param tcpudp Is a string containing the protocol.
     */
    public void setTCPUDP(String tcpudp){
        trafficKind = tcpudp;
    }
    
    /**
     * Change the packet size.
     * @param packetSize The number of the packet size.
     */
    public void setSize(Integer packetSize){
        size = packetSize;
    }
    
    /**
     * Change the number of packets sended by second.
     * @param packetNumber Define the packet burst.
     */
    public void setPacketsSeconds(Integer packetNumber){
        packetPerSeconds = packetNumber;
    }
    
    /**
     * Obtain the max packets that can be sended in a determined time;
     */    
    public void CalculateMaxPackets(){
        totalPackets = (start - stop)*packetPerSeconds;
    }
    
    /**
     * Change the maxim number of packets sended.
     * @param max Define the total packets desired.
     */
    public void setMaxPackets(Integer max){
        totalPackets = max;
    }
    
    /**
     * Change the starting simulation second for this traffic flux.
     * @param start Is the starting second.
     */
    public void setStart(Integer start){
        this.start = start;
    }
    
    /**
     * Change the end simulation second for this traffic flow.
     * @param stop Is the second when will end.
     */
    public void setStop(Integer stop){
        this.stop = stop;
    }
    
    /**
     * Change the total traffic for TCP.
     * @param transferSize The new TCP traffic between two selected nodes.
     */
    public void setTransferSize(Integer transferSize){
        this.transferSize = transferSize;
    }
}
