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
public class DoryTurret extends DoryBase {

    CANJaguar turret;
    LogitechF310 controller;
    double speed;

    public DoryTurret(LogitechF310 shooterController) {
        super(DoryDefinitions.DORY_TURRET_NAME);
        controller = shooterController;
        try {
            turret = new CANJaguar(DoryDefinitions.TURRET_JAG_ID);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        SmartDashboard.putBoolean(DoryDefinitions.TURRET_AT_FORWARD_LIMIT, false);
        SmartDashboard.putBoolean(DoryDefinitions.TURRET_AT_REVERSE_LIMIT, false);
        SmartDashboard.putDouble(DoryDefinitions.TURRET_SPEED, DoryDefinitions.TURRET_SPEED_INITIAL);
        speed = SmartDashboard.getDouble(DoryDefinitions.TURRET_SPEED, DoryDefinitions.TURRET_SPEED_INITIAL);
    }

    public void run() {
        System.out.println("DoryTurret thread started!");
        while (runner.isAlive()) {
            if (isEnabled()) {
                updateLimitStates();
                if (controller.getDPad().x != 0) {
                    try {
                        if ((controller.getDPad().x > 0) && turret.getForwardLimitOK()) {
                            turret.setX(speed);
                        } else if ((controller.getDPad().x < 0) && turret.getReverseLimitOK()) {
                            turret.setX(-1.0 * speed);
                        } else {
                            turret.setX(0.0);
                        }
                    } catch (CANTimeoutException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                try {
                    turret.setX(0.0);
                } catch (CANTimeoutException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }    
    

    private void updateLimitStates() {
        try {
            if (turret.getForwardLimitOK()) {
                SmartDashboard.putBoolean(DoryDefinitions.TURRET_AT_FORWARD_LIMIT, false);
            } else {
                SmartDashboard.putBoolean(DoryDefinitions.TURRET_AT_FORWARD_LIMIT, true);
            }

            if (turret.getReverseLimitOK()) {
                SmartDashboard.putBoolean(DoryDefinitions.TURRET_AT_REVERSE_LIMIT, false);
            } else {
                SmartDashboard.putBoolean(DoryDefinitions.TURRET_AT_REVERSE_LIMIT, true);
            }
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

}
