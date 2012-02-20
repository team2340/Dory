/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package team2340;

import edu.wpi.first.wpilibj.Gyro;

/**
 *
 * @author Team 2340
 */
public class GyroSensor {

    Gyro gyro;
    static double DEG_CONVERSION = 0.000544;
    static double TOTAL_DEGREES = 360 * DEG_CONVERSION;
    double lastAngle;
    
    public GyroSensor(int channel) {
        gyro = new Gyro(channel);
        gyro.reset();
        gyro.setSensitivity(12.5);
        lastAngle = 0.0;
    }

    public int getTotalTurns() {
        return (int) (gyro.getAngle() / TOTAL_DEGREES);
    }

    public int get360Angle() {
        return (int) ((((((gyro.getAngle() % TOTAL_DEGREES) / TOTAL_DEGREES)) * 100.0) * 360.0) / 100.0);
    }
}
