/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package team2340;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/**
 *
 * @author Team 2340
 */
public abstract class DoryBase implements Runnable {
    
    boolean enabled;
    boolean isAuto;
    boolean isTeleOp;
    
    Thread runner;
    DoryLogger logger;
    
    DoryBase(String name) {
        enabled = false;
        isAuto = false;
        isTeleOp = false;
        runner = new Thread(this, name);
        logger = DoryLogger.getInstance();
    }
    
    synchronized public void init() {
        runner.start();
    }
    
    synchronized public void enable() {
        enabled = true;
    }
    
    synchronized public void disable() {
        enabled = false;
    }
    
    synchronized public boolean isEnabled() {
        return enabled;
    }
    
    synchronized public void setTeleOp(boolean _isTeleOp) { isTeleOp = _isTeleOp; }
    
    synchronized public void setAuto(boolean _isAuto) { isAuto = _isAuto; }
    
    synchronized public boolean isTeleOp() { return isTeleOp; }
   
    synchronized public boolean isAuto() { return isAuto; }
}
