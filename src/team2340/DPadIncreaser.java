/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package team2340;

/**
 *
 * @author NAZareX Robotics
 */
public class DPadIncreaser {
    private final LogitechF310 controller1;
    private double value  = 0.0;
    
    public DPadIncreaser(LogitechF310 controller)
    {
        this.controller1 = controller;
    }
    
        private void getShooterValue() {
        if (controller1.getDPad().getY() > 0) {
            value += .1;
            System.out.println("Shooter Value: " + value);
        } else if (controller1.getDPad().getY() < 0) {

            value -= .1;
            System.out.println("Shooter Value: " + value);
        }
        if (controller1.getLB()) {
            value = 0.0;
        }


        if (value > 1.0) {
            value = 1.0;
        } else if (value < -1.0) {
            value = -1.0;
        }

    }

    
}
