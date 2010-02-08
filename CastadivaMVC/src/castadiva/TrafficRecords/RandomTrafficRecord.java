package castadiva.TrafficRecords;


/**
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
public class RandomTrafficRecord extends TrafficSchema {
    Integer flows;

    /** 
     * Creates a new instance of TrafficRecord 
     */
    public RandomTrafficRecord() {
        trafficKind = "UDP";
        size = 1400;
        start = 0;
        stop = 0;
        transferSize = 10000;
        packetPerSeconds = 10;
        totalPackets = 100;
        flows = 1;
    }
 
      public RandomTrafficRecord(String traffic, Integer size, 
            Integer start, Integer stop, Integer transferSize, 
              Integer packetsPerSeconds, Integer flows){
        trafficKind = traffic;
        this.size = size;
        this.start = start;
        this.stop = stop;
        this.transferSize = transferSize;
        this.packetPerSeconds = packetsPerSeconds;
        this.totalPackets = (start - stop) * packetPerSeconds;        
        this.flows = flows;
    }
      
      /**
       * Set a value of flows.
       */
     public void setFlows(Integer i){
         if (i < 0) flows = 0;
         else flows = i;
     }
      
      /**
       * obtain the number of flows that represent this record.
       */
     public Integer getFlows(){
         return flows;
     }
}

