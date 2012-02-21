/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package team2340;

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
        System.out.println("DoryArm thread started!");
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
        while (gyro.get360Angle() == 0) {
            goForward();
            goBackward();       
        }
        stop();
    }

    private void DisableAutoBalance() {
    }

    private void goForward() {
        driveSystem.drive(new Direction(0, .25));
    }

    private void goBackward() {
        driveSystem.drive(new Direction(0, -.25));
                
    }

    private void stop() {
        driveSystem.stop();
    }
}
