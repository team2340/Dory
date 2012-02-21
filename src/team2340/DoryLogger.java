/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package team2340;

import com.sun.squawk.microedition.io.FileConnection;
import edu.wpi.first.wpilibj.Timer;
import java.io.*;
import javax.microedition.io.Connector;

/**
 *
 * @author NAZareX Robotics
 */
public class DoryLogger {
    private final FileConnection file;
    private final OutputStream os;
    private final PrintStream p;

    public DoryLogger() throws IOException {
       file = (FileConnection)Connector.open("file://logs.txt");
       if (!file.exists()) {
           System.out.println("No File");
           file.create();
       }
       if (file.exists()) {
           System.out.println("Found a file delete old");
           file.delete();
           file.create();
       }
       os = file.openOutputStream();
       p = new PrintStream(os);
       p.println("Team 2340 Data Log: StartTime:" + Timer.getFPGATimestamp());
      
    }
    
    public void close() {
        if (file.isOpen())
        {
         try {
            p.println(Timer.getFPGATimestamp() + ": Close");
            file.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }   
        }
    }
    private void finalize() {
        try {
            p.println(Timer.getFPGATimestamp() + ": Close");
            file.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
