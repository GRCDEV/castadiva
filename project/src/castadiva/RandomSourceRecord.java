package castadiva;


import java.io.Serializable;
/*
 * RandomTrafficRecord.java
 *
 * Created on 6 de noviembre de 2006, 11:34
 *
 *
 */

/**
 *
 *    @author jorge
 *
 */
public class RandomSourceRecord extends TrafficSchema {
    Integer packetPerSecondsStart;
    Integer packetPerSecondsEnd;
    Integer packetsPerSecondsGranularity;

    /** 
     * Creates a new instance of TrafficRecord 
     */
    public RandomSourceRecord() {
        trafficKind = "UDP";
        size = 1400;
        start = 0;
        stop = 0;
        transferSize = 10000;
        packetPerSecondsStart = 10;
        packetPerSecondsEnd = 100;
        packetsPerSecondsGranularity = 10;
        totalPackets = 1000;
        
    }
 
      public RandomSourceRecord(String traffic, Integer size, 
            Integer start, Integer stop, Integer packetPerSecondsStart, 
              Integer packetPerSecondsEnd, Integer packetsPerSecondsGranularity,
              Integer packetPerSeconds, Integer totalPackets){
        trafficKind = traffic;
        this.size = size;
        this.start = start;
        this.stop = stop;
        this.packetPerSecondsStart = packetPerSecondsStart;
        this.packetPerSecondsEnd = packetPerSecondsEnd;
        this.packetsPerSecondsGranularity = packetsPerSecondsGranularity;
        this.totalPackets = totalPackets;        
    }
}


