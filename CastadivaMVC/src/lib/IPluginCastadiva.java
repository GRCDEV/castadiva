/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lib;

/**
 *
 * @author nacho wannes
 */
public interface IPluginCastadiva {
        public String getBin();
        public String getFlags();
        public String getPathConf();
        /*
         The following function is no longer used as the configuration file is
         now stored directly into the .jar. 
        */
        public String getConfContent();
        public String getConf();
        public String getKillInstruction();
}
