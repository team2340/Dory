/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package team2340;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/**
 *
 * @author NAZareX Robotics
 */
public class DoryAutoBalancer extends DoryBase {

    private final DoryDrive driveSystem;
    private final GyroSensor gyro;
    private final LogitechF310 controller;

    public DoryAutoBalancer(DoryDrive driveSystem, GyroSensor gyro, LogitechF310 controller) {
        super(DoryDefinitions.DORY_AUTO_BALANCER);
        this.driveSystem = driveSystem;
        this.gyro = gyro;
        this.controller = controller;
    }

    public void run() {
        System.out.println("DoryAutoBalancer thread started!");
        while (runner.isAlive()) {
            if (isEnabled()) {
                if (controller.getLT())
                {
                    AutoBalance();
                }
                else if (controller.getLB())
                {
                    DisableAutoBalance();
                }
                
            }
        }

    }
    
    public void init() {
        
    }

    private void AutoBalance() {
        int angle = gyro.get360Angle();
        logger.log("Starting Angle", angle);
        while (gyro.get360Angle() == 0) {
            if (angle > 0) {
            goForward();
            }
            else if (angle < 0) {
            goBackward();       
            }
        }
        stop();
    }

    private void DisableAutoBalance() {
    }

    private void goForward() {
        logger.log("Go Forward");
        driveSystem.drive(new Direction(0, .25));
        Timer.delay(.2);
        
        
    }

    private void goBackward() {
        logger.log("Go Backwards");
        driveSystem.drive(new Direction(0, -.25));
        Timer.delay(.2);
                
    }

    private void stop() {
        logger.log("Stop" );
        driveSystem.stop();
    }
}
