/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package team2340;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Watchdog;
import team2340.DoryDefinitions;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.networktables.NetworkTableKeyNotDefined;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Team 2340
 */
public class DoryDrive extends DoryBase {

    CANJaguar frontLeft;
    CANJaguar frontRight;
    CANJaguar backLeft;
    CANJaguar backRight;
    LogitechF310 controller;
    double p;
    double i;
    double d;

    public DoryDrive(LogitechF310 driveController) {
        super(DoryDefinitions.DORY_DRIVE_NAME);
        controller = driveController;
        SmartDashboard.putDouble(DoryDefinitions.DRIVE_PROPORTIONAL, DoryDefinitions.DRIVE_P_INITIAL);
        SmartDashboard.putDouble(DoryDefinitions.DRIVE_INTEGRAL, DoryDefinitions.DRIVE_I_INITIAL);
        SmartDashboard.putDouble(DoryDefinitions.DRIVE_DIFFERENTIAL, DoryDefinitions.DRIVE_D_INITIAL);
        p = SmartDashboard.getDouble(DoryDefinitions.DRIVE_PROPORTIONAL, DoryDefinitions.DRIVE_P_INITIAL);
        i = SmartDashboard.getDouble(DoryDefinitions.DRIVE_INTEGRAL, DoryDefinitions.DRIVE_I_INITIAL);
        d = SmartDashboard.getDouble(DoryDefinitions.DRIVE_DIFFERENTIAL, DoryDefinitions.DRIVE_D_INITIAL);
        frontLeft = initializeCANJag(DoryDefinitions.FRONTLEFT_DRIVE_JAG_ID, DoryDefinitions.CPR250);
        frontRight = initializeCANJag(DoryDefinitions.FRONTRIGHT_DRIVE_JAG_ID, DoryDefinitions.CPR250);
        backLeft = initializeCANJag(DoryDefinitions.BACKLEFT_DRIVE_JAG_ID, DoryDefinitions.CPR360);
        backRight = initializeCANJag(DoryDefinitions.BACKRIGHT_DRIVE_JAG_ID, DoryDefinitions.CPR360);
    }

    private void directionalDrive(Direction direction) throws CANTimeoutException {
        direction.x *= .75;
        direction.y *= .75;
        double mag = Math.sqrt(direction.y * direction.y + direction.x * direction.x);
        SmartDashboard.putDouble("Driving direction.x", direction.x);
        SmartDashboard.putDouble("Driving direction.y", direction.y);

        if (direction.y > 0 && direction.x == 0) {
            // Forward
            frontRight.setX(direction.y);
            frontLeft.setX(-1 * direction.y);
            backRight.setX(direction.y);
            backLeft.setX(-1 * direction.y);
        } else if (direction.y > 0 && direction.x > 0) {
            // 45 diagional front right
            frontRight.setX(0);
            frontLeft.setX(mag * -1);
            backRight.setX(mag);
            backLeft.setX(0);
        } else if (direction.y == 0 && direction.x > 0) {
            frontRight.setX(direction.x * -1);
            frontLeft.setX(-1 * direction.x);
            backRight.setX(direction.x);
            backLeft.setX(direction.x);
        } else if (direction.y < 0 && direction.x > 0) {
            frontRight.setX(mag);
            frontLeft.setX(0);
            backRight.setX(0);
            backLeft.setX(mag * -1);
        } else if (direction.y < 0 && direction.x == 0) {
            frontRight.setX(direction.y);
            frontLeft.setX(direction.y * -1);
            backRight.setX(direction.y);
            backLeft.setX(direction.y * -1);
        } else if (direction.y < 0 && direction.x < 0) {
            frontRight.setX(0);
            frontLeft.setX(mag * -1);
            backRight.setX(mag);
            backLeft.setX(0);
        } else if (direction.y == 0 && direction.x < 0) {
            frontRight.setX(direction.x * -1);
            frontLeft.setX(direction.x * -1);
            backRight.setX(direction.x);
            backLeft.setX(direction.x);
        } else if (direction.x < 0 && direction.y > 0) {
            frontRight.setX(mag * -1);
            frontLeft.setX(0);
            backRight.setX(0);
            backLeft.setX(mag);
        } else if (controller.getDPad().getX() > 0) {
            frontRight.setX(-0.6);
            frontLeft.setX(-0.6);
            backRight.setX(-0.6);
            backLeft.setX(-0.6);
        } else if (controller.getDPad().getX() < 0) {
            frontRight.setX(0.6);
            frontLeft.setX(0.6);
            backRight.setX(0.6);
            backLeft.setX(0.6);
        } else {
            frontRight.setX(0);
            frontLeft.setX(0);
            backRight.setX(0);
            backLeft.setX(0);
        }
        logger.log("FR : " + frontRight.getX() + " RL : " + frontLeft.getX() 
                + " BR : " + backRight.getX() + " BL : " + backLeft.getX());
        //System.out.println("FR : " + frontRight.getX() + " RL : " + frontLeft.getX() 
        //        + " BR : " + backRight.getX() + " BL : " + backLeft.getX());
        
    }

    private CANJaguar initializeCANJag(int id, int clicksPerRev) {
        CANJaguar canJag = null;
        try {
          logger.log("initialize driveJag", id);
           
            canJag = new CANJaguar(id, CANJaguar.ControlMode.kSpeed);
            canJag.configEncoderCodesPerRev(clicksPerRev);
            canJag.setPID(p, i, d);
            canJag.setSpeedReference(CANJaguar.SpeedReference.kEncoder);
            canJag.changeControlMode(CANJaguar.ControlMode.kSpeed);
            canJag.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
            canJag.enableControl();
           
            canJag.configNeutralMode(CANJaguar.NeutralMode.kCoast);
  
  } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        return canJag;
    }

    public void run() {
        System.out.println("DoryDrive thread started!");
        while (runner.isAlive()) {
            if (isEnabled()) {
                try {
         //           updatePID();
                    drive();
                    runner.sleep(20);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    frontRight.setX(0);
                    frontLeft.setX(0);
                    backRight.setX(0);
                    backLeft.setX(0);
                } catch (CANTimeoutException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void updatePID() {
        try {
            double newP = SmartDashboard.getDouble(DoryDefinitions.DRIVE_PROPORTIONAL);
            double newI = SmartDashboard.getDouble(DoryDefinitions.DRIVE_INTEGRAL);
            double newD = SmartDashboard.getDouble(DoryDefinitions.DRIVE_DIFFERENTIAL);
            if ((newP != p) || (newI != i) || (newD != d)) {
                p = newP;
                i = newI;
                d = newD;
                frontLeft.setPID(p, i, d);
                frontRight.setPID(p, i, d);
                backLeft.setPID(p, i, d);
                backRight.setPID(p, i, d);
            }
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        } catch (NetworkTableKeyNotDefined ex) {
            ex.printStackTrace();
        }
    }

    private void drive() {
        try {
            Direction direction = rangeCheck(controller.getRightStick());
            
            directionalDrive(direction);

            if (controller.getRB()) {
                frontRight.configNeutralMode(CANJaguar.NeutralMode.kBrake);
                frontLeft.configNeutralMode(CANJaguar.NeutralMode.kBrake);
                backRight.configNeutralMode(CANJaguar.NeutralMode.kBrake);
                backLeft.configNeutralMode(CANJaguar.NeutralMode.kBrake);
            } else {
                frontRight.configNeutralMode(CANJaguar.NeutralMode.kCoast);
                frontLeft.configNeutralMode(CANJaguar.NeutralMode.kCoast);
                backRight.configNeutralMode(CANJaguar.NeutralMode.kCoast);
                backLeft.configNeutralMode(CANJaguar.NeutralMode.kCoast);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Direction rangeCheck(Direction dir) {
        Direction returnDir = new Direction(dir.x, dir.y);
        if (dir.x == 0.0) {
            double noSlope = Math.tan(75.0);
            double zeroSlope = Math.tan(15.0);
            double calculatedSlope = (dir.y / dir.x);

            if (Math.abs(calculatedSlope) > noSlope) {
                returnDir.x = 0.0;
                returnDir.y = dir.y;
            } else if (Math.abs(dir.y / dir.x) < zeroSlope) {
                returnDir.x = dir.x;
                returnDir.y = 0.0;
            }
        }
        return returnDir;
    }

    void stop() {
        try {
            directionalDrive(new Direction(0, 0));
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }
    void drive(Direction direction) {
        try {
            directionalDrive(direction);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }
    
    
    synchronized public void setTeleOp(boolean _isTeleOp)
    {
        isTeleOp = _isTeleOp;
        if(isTeleOp)
        {
            setTele();
        }
    }
    
    public void setTele() {
        try {
            logger.log(" Set TELE ");
            frontLeft.disableControl();
            frontLeft.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            frontRight.disableControl();
            frontRight.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            backLeft.disableControl();
            backLeft.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            backRight.disableControl();
            backRight.changeControlMode(CANJaguar.ControlMode.kPercentVbus);

        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        
    }
    
    
}
