/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;


import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;
import team2340.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends SimpleRobot {

    
    LogitechF310 driveController;
    LogitechF310 shooterController;
    DoryDrive drive;
    DoryShooter shooter;
    DoryTurret turret;
    DoryBallCollection ballCollection;
    DoryArm arm;
    DoryCamera camera;
    SonicSensor sonicSensor;
    GyroSensor gyroSensor;
    
    protected void robotInit() {
        System.out.println("Commander Dory reporting for duty!");
        driveController = new LogitechF310(DoryDefinitions.DRIVE_CONTROLLER, 1);
        driveController.init();
        shooterController = new LogitechF310(DoryDefinitions.SHOOTER_CONTROLLER, 2);
        shooterController.init();
        
        camera = new DoryCamera();
        sonicSensor = new SonicSensor(DoryDefinitions.SONIC_SENSOR_ANALOG_CHANNEL);
        gyroSensor = new GyroSensor(DoryDefinitions.GYRO_SENSOR_CHANNEL);
        
        drive = new DoryDrive(driveController);
        shooter = new DoryShooter(shooterController, camera, sonicSensor);
        turret = new DoryTurret(shooterController);
        ballCollection = new DoryBallCollection(driveController);
        arm = new DoryArm(shooterController);
        
        drive.init();
        shooter.init();
        turret.init();
        ballCollection.init();
        arm.init();
    }

    protected void disabled() {
        drive.disable();
        shooter.disable();
        turret.disable();
        ballCollection.disable();
        arm.disable();
    }

    void enable() {
        drive.enable();
        shooter.enable();
        turret.enable();
        ballCollection.enable();
        arm.enable();
    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {
        
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        enable();
        System.out.println("operator control - go!");
        while (isEnabled() && isOperatorControl()) {
            Timer.delay(0.02);

        }
    }
}
