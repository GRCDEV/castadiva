/*
 * ExternalDevice.java
 *
 * Created on 13 de julio de 2007, 10:02
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
public class ExternalTraffic implements Serializable{
    public int startRangePort;
    public int endRangePort;
    public String fromIp, toIp;
    public int fromAp, toAp;
    public String fromNet, toNet;
    public String protocol;
    public String name;
    
    /** Creates a new instance of ExternalDevice */
    public ExternalTraffic(int tmp_startPort, int tmp_endPort,  String tmp_fromIp,
            String tmp_toIp, int tmp_fromAp, String tmp_fromNet, int to_Ap,
            String tmp_toNet, String tmp_protocol, String tmp_id) {
        startRangePort = tmp_startPort;
        endRangePort = tmp_endPort;
        fromIp = tmp_fromIp;
        toIp = tmp_toIp;
        fromAp = tmp_fromAp;
        fromNet = tmp_fromNet;
        toAp = to_Ap;
        toNet = tmp_toNet;
        protocol = tmp_protocol;
        name = tmp_id;
    }
    
}
