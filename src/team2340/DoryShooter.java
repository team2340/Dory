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
public class DoryShooter extends DoryBase {

    // ControlModes
    static final int CALCULATE = 0; // default
    static final int USE_DASHBOARD = 1;
    
    CANJaguar rightWheel;
    CANJaguar leftWheel;
    CANJaguar flipper;
    LogitechF310 controller;
    DoryCamera camera;
    SonicSensor sonicSensor;
    
    double p;
    double i;
    double d;
    double topTargetToPeak;
    double middleTargetToPeak;
    double bottomTargetToPeak;
    int controlMode;
    double degToRadians = Math.PI / 180;
    boolean flipping = false;
    double currentRPM = 0.0;
    DPadIncreaser dpad_shootingValue;
    
    public DoryShooter(LogitechF310 shootController) { //, DoryCamera camera) { // SonicSensor sonicSensor) {
        super(DoryDefinitions.DORY_SHOOTER_NAME);
        controller = shootController;
        dpad_shootingValue = new DPadIncreaser(shootController);
        //this.camera = camera;
        //this.sonicSensor = sonicSensor;
        /*
        SmartDashboard.putDouble(DoryDefinitions.SHOOTER_PROPORTIONAL, DoryDefinitions.SHOOTER_P_INITIAL);
        SmartDashboard.putDouble(DoryDefinitions.SHOOTER_INTEGRAL, DoryDefinitions.SHOOTER_I_INITIAL);
        SmartDashboard.putDouble(DoryDefinitions.SHOOTER_DIFFERENTIAL, DoryDefinitions.SHOOTER_D_INITIAL);
        p = SmartDashboard.getDouble(DoryDefinitions.SHOOTER_PROPORTIONAL, DoryDefinitions.SHOOTER_P_INITIAL);
        i = SmartDashboard.getDouble(DoryDefinitions.SHOOTER_INTEGRAL, DoryDefinitions.SHOOTER_I_INITIAL);
        d = SmartDashboard.getDouble(DoryDefinitions.SHOOTER_DIFFERENTIAL, DoryDefinitions.SHOOTER_D_INITIAL);
        SmartDashboard.putDouble(DoryDefinitions.SHOOTER_TOP_TARGET_TO_PEAK, DoryDefinitions.SHOOTER_TTTP_INITIAL);
        SmartDashboard.putDouble(DoryDefinitions.SHOOTER_MIDDLE_TARGET_TO_PEAK, DoryDefinitions.SHOOTER_MTTP_INITIAL);
        SmartDashboard.putDouble(DoryDefinitions.SHOOTER_BOTTOM_TARGET_TO_PEAK, DoryDefinitions.SHOOTER_BTTP_INITIAL);
        topTargetToPeak = SmartDashboard.getDouble(DoryDefinitions.SHOOTER_TOP_TARGET_TO_PEAK, DoryDefinitions.SHOOTER_TTTP_INITIAL);
        middleTargetToPeak = SmartDashboard.getDouble(DoryDefinitions.SHOOTER_MIDDLE_TARGET_TO_PEAK, DoryDefinitions.SHOOTER_MTTP_INITIAL);
        bottomTargetToPeak = SmartDashboard.getDouble(DoryDefinitions.SHOOTER_BOTTOM_TARGET_TO_PEAK, DoryDefinitions.SHOOTER_BTTP_INITIAL);
        SmartDashboard.putInt(DoryDefinitions.SHOOTER_CONTROL_MODE, DoryDefinitions.SHOOTER_CONTROL_MODE_INITIAL);
        SmartDashboard.putDouble(DoryDefinitions.SHOOTER_RPM_TARGET, 0.0);
        controlMode = SmartDashboard.getInt(DoryDefinitions.SHOOTER_CONTROL_MODE, DoryDefinitions.SHOOTER_CONTROL_MODE_INITIAL);
        */
        rightWheel = initializeCANJag(DoryDefinitions.SHOOTER_RIGHTWHEEL_JAG_ID, DoryDefinitions.CPR250);
        leftWheel = initializeCANJag(DoryDefinitions.SHOOTER_LEFTWHEEL_JAG_ID, DoryDefinitions.CPR250);
       
        SmartDashboard.putBoolean(DoryDefinitions.FLIPPER_AT_FORWARD_LIMIT, false);
        SmartDashboard.putBoolean(DoryDefinitions.FLIPPER_AT_REVERSE_LIMIT, false);
        try {
            flipper = new CANJaguar(DoryDefinitions.SHOOTER_FLIPPER_JAG_ID);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    private CANJaguar initializeCANJag(int id, int clicksPerRev) {
        CANJaguar canJag = null;
        try {
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

    synchronized public void run() {
        System.out.println("DoryShooter thread started!");
        while (runner.isAlive()) {
            if (isEnabled()) {
                if (isTeleOp()) {
                    if (!flipping) {
                        /*
                         * updatePID(); // updateTargetToPeaks(); //
                         * updateControlMode(); // updateLimitStates(); double
                         * distanceFromTargetCamera =
                         * camera.getDistanceFromTarget(); double
                         * distanceFromTargetSonic = sonicSensor.getDistance();
                         * double distanceFromTargetAvg =
                         * (distanceFromTargetCamera + distanceFromTargetSonic)
                         * / 2;
                         *
                         * if (currentRPM == 0.0) { setRPM(3000.0); }
                         */
                        try {

                            if (controller.getA()) {
                                logger.log(" Flip ");
                                flip();
                                //   camera.getDistanceFromTarget();

                            } else if (controller.getX()) {
                                TurnDrivesOn(DoryDefinitions.SHOOTER_MAX);
                                //  camera.getDistanceFromTarget();
                            } else if (controller.getB()) {
                                TurnDrivesOff();

                            } else if (controller.getRB()) {
                                TurnDrivesOn(DoryDefinitions.SHOOTER_INCREASE);
                            } else if (controller.getRT()) {
                                TurnDrivesOn(DoryDefinitions.SHOOTER_DECREASE);
                            }


                            runner.sleep(20);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                } // end teleOp
                else if (isAuto()) {
                    System.out.println("autonomous shooter");
                    TurnDrivesOn(DoryDefinitions.SHOOTER_MAX);
                    Timer.delay(2);
                    flip();
                    Timer.delay(2);
                    TurnDrivesOff();
                    isAuto = false;
                }
                else
                {
                    System.out.println("not auto and not tele op");
                }
            } else {
                System.out.println("not enabled");
                setRPM(0);
            }
        }
    }

    private void updatePID() {
        try {
            double newP = SmartDashboard.getDouble(DoryDefinitions.SHOOTER_PROPORTIONAL);
            double newI = SmartDashboard.getDouble(DoryDefinitions.SHOOTER_INTEGRAL);
            double newD = SmartDashboard.getDouble(DoryDefinitions.SHOOTER_DIFFERENTIAL);
            if ((newP != p) || (newI != i) || (newD != d)) {
                p = newP;
                i = newI;
                d = newD;
                rightWheel.setPID(p, i, d);
                leftWheel.setPID(p, i, d);
            }
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        } catch (NetworkTableKeyNotDefined ex) {
            ex.printStackTrace();
        }
    }

    private void updateTargetToPeaks() {
        try {
            double newTopTargetToPeak = SmartDashboard.getDouble(DoryDefinitions.SHOOTER_TOP_TARGET_TO_PEAK);
            double newMiddleTargetToPeak = SmartDashboard.getDouble(DoryDefinitions.SHOOTER_MIDDLE_TARGET_TO_PEAK);
            double newBottomTargetToPeak = SmartDashboard.getDouble(DoryDefinitions.SHOOTER_BOTTOM_TARGET_TO_PEAK);

            if ((newTopTargetToPeak != topTargetToPeak)
                    || (newMiddleTargetToPeak != middleTargetToPeak)
                    || (newBottomTargetToPeak != bottomTargetToPeak)) {
                topTargetToPeak = newTopTargetToPeak;
                middleTargetToPeak = newMiddleTargetToPeak;
                bottomTargetToPeak = newBottomTargetToPeak;
            }
        } catch (NetworkTableKeyNotDefined ex) {
            ex.printStackTrace();
        }
    }

    private void updateControlMode() {
        try {
            int newControlMode = SmartDashboard.getInt(DoryDefinitions.SHOOTER_CONTROL_MODE);

            if (newControlMode != controlMode) {
                controlMode = newControlMode;
            }
        } catch (NetworkTableKeyNotDefined ex) {
            ex.printStackTrace();
        }
    }

    private void updateLimitStates() {
        try {
            if (flipper.getForwardLimitOK()) {
                SmartDashboard.putBoolean(DoryDefinitions.FLIPPER_AT_FORWARD_LIMIT, true);
            } else {
                SmartDashboard.putBoolean(DoryDefinitions.FLIPPER_AT_FORWARD_LIMIT, false);
            }
            if (flipper.getReverseLimitOK()) {
                SmartDashboard.putBoolean(DoryDefinitions.FLIPPER_AT_REVERSE_LIMIT, true);
            } else {
                SmartDashboard.putBoolean(DoryDefinitions.FLIPPER_AT_REVERSE_LIMIT, false);
            }
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    synchronized private void shoot(double _targetHeightIn, double targetToPeak, double distanceFromTarget) {
        flipping = true;
        double rpm = 0.0;
        if (controlMode == CALCULATE) {
            double g = 9.81 * 39.370; // covert m/s^2 to in/s^2
            double targetHeightIn = _targetHeightIn - DoryDefinitions.SHOOTER_HEIGHT_IN;
            double timeToTravelToPeak = Math.sqrt((2 * (targetHeightIn + targetToPeak)) / g);
            double timeToTravelPeakToWall = Math.sqrt((2 * targetToPeak) / g);
            double velocityX = distanceFromTarget / (timeToTravelToPeak + timeToTravelPeakToWall);
            double exitSpeed = velocityX / Math.cos(60 * degToRadians);
            rpm = (60 * exitSpeed) / DoryDefinitions.SHOOTER_CIRCUMFERENCE_IN;
        } else if (controlMode == USE_DASHBOARD) {
            rpm = SmartDashboard.getDouble(DoryDefinitions.SHOOTER_RPM_TARGET, 0.0);
        }
        setRPM(rpm);
        int ms = 50;
        Timer.delay(ms / 1000);
        flip();
    }

    synchronized public void flip() {
        try {
            flipper.setX(-1.0);
            Timer.delay(.70);
            flipper.setX(0);
            Timer.delay(1);
            flipping = false;
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    private void setRPM(double rpm) {
        try {
            if (currentRPM != rpm) {
                rightWheel.setX(-1 * rpm);
                leftWheel.setX(rpm);
                currentRPM = rpm;
            }
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }

        SmartDashboard.putDouble(DoryDefinitions.SHOOTER_RPM_ACTUAL, rpm);
    }

    public void TurnDrivesOn(int speed) {
        try {
            if (speed == DoryDefinitions.SHOOTER_MAX)
            {
                leftWheel.setX(-0.8);
                rightWheel.setX(0.8);
                Timer.delay(.5);
            }
            else if (speed == DoryDefinitions.SHOOTER_INCREASE)
            {
                if (leftWheel.getX() != -1.0 && rightWheel.getX() != 1.0) {
                leftWheel.setX(leftWheel.getX() - .1);
                rightWheel.setX(rightWheel.getX() + .1);
               
                Timer.delay(.5);
                }
            }
            else if (speed == DoryDefinitions.SHOOTER_DECREASE)
            {
                if (leftWheel.getX() != 0 || rightWheel.getX() != 0 )
                {
                leftWheel.setX(leftWheel.getX() + .1);
                rightWheel.setX(rightWheel.getX() - .1);
               
                Timer.delay(.5);
                }
            }
            else if(speed == DoryDefinitions.SHOOTER_AUTO)
            {
                leftWheel.setX(-0.8);
                rightWheel.setX(0.8);
                Timer.delay(2);
                flip();
                Timer.delay(2);
                TurnDrivesOff();
            }
            logger.log(" Selection: " + speed + " LW : " + leftWheel.getX() + " RW : " + rightWheel.getX());
            
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    public void TurnDrivesOff() {
        try {
            leftWheel.setX(0.0);
            rightWheel.setX(0.0);
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
    
    public void setTele() 
    {
        try {
            leftWheel.disableControl();
            leftWheel.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            rightWheel.disableControl();
            rightWheel.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            flipper.disableControl();
            flipper.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            flipper.configNeutralMode(CANJaguar.NeutralMode.kBrake);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        
    }

  

}
