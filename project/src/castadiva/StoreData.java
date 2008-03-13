/*
 * StoreData.java
 *
 * Created on 9 de mayo de 2007, 11:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package castadiva;

import java.io.Serializable;

/**
 *
 * @author jorge
 */
public class StoreData implements Serializable {
    public Float maxSpeed;
    public Float minSpeed;
    public Float pauseValue;
    public Float xBoundValue;
    public Float yBoundValue;
    public boolean rtsValue;
    public Integer simulationTime;
    public MobilityVectors allAddressesDataValue;
    public NodeCheckPoint nodePositionsValue[][];
    
    StoreData(Float minSpeedParameter, Float maxSpeedParameter, Float pauseParameter, Float xBoundParameter,
            Float yBoundParameter, boolean rts, Integer totalTime,
            MobilityVectors allAddressesData, NodeCheckPoint nodePositionsData[][]){
        //Scenario data.
        maxSpeed = maxSpeedParameter;
        minSpeed = minSpeedParameter;
        pauseValue = pauseParameter;
        xBoundValue = xBoundParameter;
        yBoundValue = yBoundParameter;
        rtsValue = rts;
        simulationTime = totalTime;
        allAddressesDataValue = allAddressesData;
        nodePositionsValue = nodePositionsData;
    }
}