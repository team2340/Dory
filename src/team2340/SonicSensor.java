/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package team2340;

import edu.wpi.first.wpilibj.AnalogChannel;
import java.util.Date;

/**
 *
 * @author Team 2340
 */
public class SonicSensor extends DoryBase {

    AnalogChannel analogChannel;
    double lastDistance;
    long lastReadAt;
    
    public SonicSensor(int channel) {
        super(DoryDefinitions.DORY_SONIC_SENSOR);
        analogChannel = new AnalogChannel(channel);
        lastDistance = 0.0;
        lastReadAt = 0;
    }
    
    // returns value in inches
    synchronized public double getDistance() {
        return lastDistance;
    }

    public void run() {
        while(runner.isAlive()) {
            try {
                if(isEnabled()) {
                    synchronized(this) {
                        lastDistance = analogChannel.getVoltage() * 98.0;
                    }
                }
                runner.sleep(120);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}