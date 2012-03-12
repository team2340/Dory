/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package team2340;

import java.lang.*;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.networktables.NetworkTableKeyNotDefined;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Team 2340
 */
public class DoryArm extends DoryBase {

    CANJaguar armJag;
    LogitechF310 controller;
    double armspeed;

    public DoryArm(LogitechF310 controller) {
        super(DoryDefinitions.DORY_ARM);
        this.controller = controller;
        try {
            armJag = new CANJaguar(DoryDefinitions.ARM_JAG_ID);

        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        SmartDashboard.putDouble(DoryDefinitions.ARM_SPEED, DoryDefinitions.ARM_SPEED_INITIAL);
        armspeed = SmartDashboard.getDouble(DoryDefinitions.ARM_SPEED, DoryDefinitions.ARM_SPEED_INITIAL);

        SmartDashboard.putBoolean(DoryDefinitions.ARM_AT_FORWARD_LIMIT, false);
        SmartDashboard.putBoolean(DoryDefinitions.ARM_AT_REVERSE_LIMIT, false);
    }

    public void run() {
        System.out.println("DoryArm thread started!");
        while (runner.isAlive()) {
            if (isEnabled()) {
                try {
                    updateLimitStates();
                    
                    if (controller.getDPad().y > 0.0 && armJag.getForwardLimitOK()) { // Check directions
                        liftRobot();
                    } else if (controller.getDPad().y < 0.0 && armJag.getReverseLimitOK()) {
                        dropRobot();
                    } else {
                        stopArm();
                    }
                } catch (CANTimeoutException ex) {
                    ex.printStackTrace();
                }
            } else {
                stopArm();
            }
        }
    }

    private void liftRobot() {
        try {
            armJag.setX(armspeed);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    private void dropRobot() {
        try {
            armJag.setX(-1 * armspeed);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    private void stopArm() {
        try {
            armJag.setX(0.0);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    private void updateLimitStates() {
        try {
            if (armJag.getForwardLimitOK()) {
                SmartDashboard.putBoolean(DoryDefinitions.ARM_AT_FORWARD_LIMIT, false);
            } else {
                SmartDashboard.putBoolean(DoryDefinitions.ARM_AT_FORWARD_LIMIT, true);
            }

            if (armJag.getReverseLimitOK()) {
                SmartDashboard.putBoolean(DoryDefinitions.ARM_AT_REVERSE_LIMIT, false);
            } else {
                SmartDashboard.putBoolean(DoryDefinitions.ARM_AT_REVERSE_LIMIT, true);
            }
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }
}