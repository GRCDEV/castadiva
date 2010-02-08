/*
 * PingThread.java
 *
 * Created on 11 de mayo de 2006, 11:30
 *
 *
 */

package castadiva;
import java.util.*;
import java.io.*;

/**
 *
 *    @author jorge
 *
 */

public class PingThread extends Thread{
    javax.swing.JTextArea blackBoard;
    String address;
    
    /**
     * Creates a new instance of PingThread
     */
    public PingThread(javax.swing.JTextArea PingText, String addr) {
        blackBoard = PingText;
        address = addr;
    }
    
    @Override
    public void run(){
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("ping -c 4 " + address);
            InputStream stdin = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(stdin);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            String linetot = "Starting Ping command...";
            while ( (line = br.readLine()) != null){
                linetot = linetot + "\n  " + line;
                blackBoard.setText(linetot);
                //System.out.println(line);
            }
           
            //int exitVal = proc.waitFor();
  
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
}
