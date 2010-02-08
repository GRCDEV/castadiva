/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lib;

import castadiva.APs;
import castadiva.NodeCheckPoint;




public interface IMobilityPluginCastadiva {

     void ObtainNodePositionsForEntireSimulation(NodeCheckPoint[][] nodes, APs accessPoints, Float minSpeed, Float maxSpeed, Float pause, int totaltime, float X, float Y);
}