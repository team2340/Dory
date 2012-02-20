/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package team2340;

/**
 *
 * @author Team 2340
 */
public abstract class DoryBase implements Runnable {
    
    boolean enabled;
    
    Thread runner;
    
    DoryBase(String name) {
        enabled = false;
        runner = new Thread(this, name);
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
        
}
