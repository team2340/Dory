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
public class DoryBallCollection extends DoryBase {

    CANJaguar topAnemone;
    CANJaguar bottomAnemone;
    LogitechF310 controller;
    double topSpeed;
    double bottomSpeed;

    public DoryBallCollection(LogitechF310 controller) {
        super(DoryDefinitions.DORY_BALL_COLLECTION);
        this.controller = controller;
        try {
            //topAnemone = new CANJaguar(DoryDefinitions.TOP_ANEMONE_JAG_ID);
            bottomAnemone = new CANJaguar(DoryDefinitions.BOTTOM_ANEMONE_JAG_ID);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        SmartDashboard.putDouble(DoryDefinitions.TOP_ANEMONE_SPEED, DoryDefinitions.TOP_ANEMONE_SPEED_INITIAL);
        topSpeed = SmartDashboard.getDouble(DoryDefinitions.TOP_ANEMONE_SPEED, DoryDefinitions.TOP_ANEMONE_SPEED_INITIAL);
        SmartDashboard.putDouble(DoryDefinitions.BOTTOM_ANEMONE_SPEED, DoryDefinitions.BOTTOM_ANEMONE_SPEED_INITIAL);
        bottomSpeed = SmartDashboard.getDouble(DoryDefinitions.BOTTOM_ANEMONE_SPEED, DoryDefinitions.BOTTOM_ANEMONE_SPEED_INITIAL);
    }

    public void run() {
        System.out.println("DoryBallCollection thread started!");
        while (runner.isAlive()) {
            if (isEnabled()) {
                try {
                    updateSpeeds();
                    if (controller.getY()) {
                        aquire();
                    } else if (controller.getA()) {
                        repel();
                    } else if (controller.getB() && controller.getX()) {
                        stop();
                    }
                    runner.sleep(20);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } else {
                stop();
            }
        }
    }    
    

    private void updateSpeeds() {
        try {
          //  topSpeed = SmartDashboard.getDouble(DoryDefinitions.TOP_ANEMONE_SPEED);
            bottomSpeed = SmartDashboard.getDouble(DoryDefinitions.BOTTOM_ANEMONE_SPEED);
        } catch (NetworkTableKeyNotDefined ex) {
            ex.printStackTrace();
        }
    }

    private void aquire() {
        try {
       //     topAnemone.setX(-1 * topSpeed);
            System.out.println("Set Speed: " + bottomSpeed);
            bottomAnemone.setX(bottomSpeed);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    private void repel() {
        try {
         //   topAnemone.setX(-1 * topSpeed);
            bottomAnemone.setX(-1 * bottomSpeed);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    private void stop() {
        try {
           // topAnemone.setX(0);
            bottomAnemone.setX(0);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

}
