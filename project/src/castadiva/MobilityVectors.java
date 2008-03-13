/*
 * MobilityVectors.java
 *
 * Created on 9 de mayo de 2007, 11:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package castadiva;

import java.io.Serializable;
import java.util.Random;

/**
 *
 * @author jorge
 */

/**
 * The manipulation of the mobility vector of each node.
 */
public class MobilityVectors implements Serializable{
    private MobilityVector[] allMobilityVector;
    private Float maxMobilitySpeed;
    private Float minMobilitySpeed;
    private Float timePause;
    Random randomGenerator;
    transient private CastadivaModel m_model;
    
    /**
     * Constructor.
     */
    MobilityVectors(CastadivaModel modelParam, APs aps, Float minSpeed, Float maxSpeed, Float simulationPause){
        allMobilityVector = new MobilityVector[aps.Size()];
        timePause=simulationPause;
        randomGenerator = new Random();
        
        m_model = modelParam;
        for(int i=0; i< aps.Size();i++){
            maxMobilitySpeed = maxSpeed;
            minMobilitySpeed = minSpeed;
            allMobilityVector[i] = new MobilityVector(maxMobilitySpeed);
        }
    }
    
    /**
     * Generate a empty mobilityVector.
     */
    MobilityVectors(CastadivaModel modelParam, APs aps, Float simulationPause){
        m_model = modelParam;
        randomGenerator = new Random();
        allMobilityVector = new MobilityVector[aps.Size()];
        maxMobilitySpeed = (float)-1;
        timePause=simulationPause;
    }
    
    /**
     * Update a mobility vector with new data.
     */
    public void ChangeMobilityVector(Float x, Float y, Float speed, Integer node){
        allMobilityVector[node] = new MobilityVector(x, y, speed);
    }
    
    /**
     * Calculate the angle between the starting position of one node and the ending position.
     */
    private double ObtainAngle(Float x1, Float y1, Float x2, Float y2){
        Double angle;
        Double cathetus1, cathetus2, hypotenuse;
        cathetus1 = (double)Math.abs(y2-y1);
        cathetus2 = (double)Math.abs(x2-x1);
        hypotenuse = Math.sqrt(Math.pow(cathetus1,2) + Math.pow(cathetus2,2));
        angle = Math.asin(cathetus1 / hypotenuse);
        return angle;
    }
    
    /**
     * Store one position of a node in the matrix of positions.
     * @param node The node used to calculate the new position.
     * @param nodeNumber The node position in the list of nodes.
     * @param second The second that is calculated.
     */
    private void StoreNewNodePosition(AP node, Integer nodeNumber, Integer second){
        NodeCheckPoint checkPoint = new NodeCheckPoint(node.x, node.y, new Float(0));
        m_model.StoreNodePosition(nodeNumber, second, checkPoint);
        //nodePositions[nodeNumber][second] = checkPoint;
    }
    
    /**
     * Calculate the travel distance of a node in one second.
     * @param node The node used to calculate the new position.
     * @param nodeNumber The node position in the list of nodes.
     * @param second The second that is calculated.
     */
    private AP ObtainNewNodePositionInOneSecond(AP node, Integer nodeNumber, Integer second){
        Double angle;
        Double newX, newY;
        
        MobilityVector direction = allMobilityVector[nodeNumber];
        
        //If is not Making a pause..
        if(direction.waitingTime == 0){
            angle = ObtainAngle(node.x, node.y, direction.X(), direction.Y());
            newX = Math.cos(angle) * direction.Speed();
            newY = Math.sin(angle) * direction.Speed();
            //Move coordinate X.
            if(Math.abs(direction.X() - node.x) < Math.abs(newX)) {
                newX = (double)0;
                node.x = direction.X();
            }else{
                if(direction.X() < node.x){
                    newX = -newX;
                }
                node.x = node.x + newX.floatValue();
            }
            //Move coordinate Y.
            if(Math.abs(direction.Y() - node.y) < Math.abs(newY)) {
                newY = (double)0;
                node.y = direction.Y();
            }else{
                if(direction.Y() < node.y){
                    newY = -newY;
                }
                node.y = node.y + newY.floatValue();
            }
            //If arrives to the destiny...
            if(node.x == direction.X() && node.y == direction.Y()){
                //Generate a new address but wait timePause.
                direction = new MobilityVector(maxMobilitySpeed);
                direction.waitingTime = timePause;
                allMobilityVector[nodeNumber] = direction;
            }
        }else{
            //Decrease one second the pause and do nothing.
            direction.waitingTime --;
        }
        //Store the checkpoint of this node in the matrix.
        StoreNewNodePosition(node, nodeNumber, second);
        return node;
    }
    
    /**
     * Obtain a  new nodePositions matrix with the values of the new simulation.
     * @param stimatedAps A list with all APs of the simulation. This list will be
     * modified!
     */
    private void CalculateNodePositionMatrix(APs stimatedAps){
        //Copy the starting node position.
        for(int k=0; k<stimatedAps.Size();k++){
            AP node = stimatedAps.Get(k);
            StoreNewNodePosition(node, k, 0);
        }
        //For each seconds of the simulation.
        for(int j=1; j<=m_model.GetSimulationTime(); j++){
            //Change the position of all nodes in one second.
            for(int i=0; i<stimatedAps.Size(); i++){
                stimatedAps.Set(i,ObtainNewNodePositionInOneSecond(stimatedAps.Get(i), i, j));
            }
        }
    }
    
    /**
     * Move all simulation nodes the covered space in one second.
     * @param stimatedAps A list with all APs of the simulation. This list will be
     * modified!
     */
    public void ObtainNodePositionsForEntireSimulation(APs aps){
        APs stimatedAps = new APs();
        
        //Security copy.
        for(int i=0; i<aps.Size(); i++){
            AP node = aps.Get(i);
            AP nodeAux = new AP(node.WhatEthIP(), node.WhatWifiIP(), node.WhatWifiMac(),
                    node.WhatUser(), node.WhatPwd(), node.WhatAP(), node.x, node.y,
                    node.z, node.range, node.WhatWorkingDirectory(), node.WhatProcessor(),
                    node.WhatChannel(), node.WhatMode(), node.WhatWifiDevice());
            stimatedAps.Add(nodeAux);
        }
        CalculateNodePositionMatrix(stimatedAps);
    }
    
    /**
     * Represent one mobility vector of one node. Used in the mobility functions.
     */
    class MobilityVector implements Serializable{
        private Float xCoordinate;
        private Float yCoordinate;
        private Float speed;
        public Float waitingTime;
        
        MobilityVector(Float maxMobilitySpeed){
            ObtainRandomAddress();
            ObtainRandomSpeed(maxMobilitySpeed);
            waitingTime = (float)0;
        }
        
        MobilityVector(Float x, Float y, Float definedSpeed){
            xCoordinate = x;
            yCoordinate = y;
            speed = definedSpeed;
            waitingTime = (float)0;
        }
        
        public Float X(){return xCoordinate;}
        public Float Y(){return yCoordinate;}
        public Float Speed(){return speed;}
        public Float Waiting(){return waitingTime;}
        
        /**
         * Obtain one random address point.
         */
        private void ObtainRandomAddress(){
            Double aux;
            aux = (randomGenerator.nextDouble()*m_model.GetBoundX());
            xCoordinate = aux.floatValue();
            aux = (randomGenerator.nextDouble()*m_model.GetBoundY());
            yCoordinate= aux.floatValue();
        }
        
        /**
         * Obtain one random speed().
         */
        private void ObtainRandomSpeed(Float maxMobilitySpeed){
            Double aux;
            aux = randomGenerator.nextDouble() * (maxMobilitySpeed-minMobilitySpeed) + minMobilitySpeed;
            speed = aux.floatValue();
        }
    }
}

