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
    private static DoryLogger logger = null;

    private DoryLogger() throws IOException {
        file = (FileConnection) Connector.open("file://logs_" + Timer.getFPGATimestamp() + ".txt");
        if (!file.exists()) {
            System.out.println("No File");
            file.create();
        } 
        os = file.openOutputStream();
        p = new PrintStream(os);
        p.println("Team 2340 Data Log: StartTime:" + Timer.getFPGATimestamp());

    }

    public static DoryLogger getInstance() {
        if (logger == null) {
            try {
                logger = new DoryLogger();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return logger;
    }

    public void close() {
        if (file.isOpen()) {
            try {
                p.println(Timer.getFPGATimestamp() + ": Close");
                file.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void log(String info, double data) {
        p.println(Timer.getFPGATimestamp() + ": " + info + ":" + data);
    }

    public void log(String info, int data) {
        p.println(Timer.getFPGATimestamp() + ": " + info + ":" + data);
    }

    public void log(String info) {
        p.println(Timer.getFPGATimestamp() + ": " + info);
    }

    public void log(String info, float data) {
        p.println(Timer.getFPGATimestamp() + ": " + info + ":" + data);
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
