/*
 * NodeCheckPoint.java
 *
 * Created on 9 de mayo de 2007, 11:21
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

/**
 * Store the position of one node in a determined second.
 */
public class NodeCheckPoint implements Serializable{
    public Float xCoordinate;
    public Float yCoordinate;
    public Float zCoordinate;
    
    NodeCheckPoint(Float newX, Float newY, Float newZ){
        xCoordinate = newX;
        yCoordinate = newY;
        zCoordinate = newZ;
    }
}
