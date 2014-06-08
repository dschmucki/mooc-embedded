package doorproject;

import javax.microedition.midlet.MIDlet;

/**
 *
 * @author Dominik
 */
public class DoorProject extends MIDlet {
    
    DoorSensor doorSensor;
    
    @Override
    public void startApp() {
        System.out.println("Starting DoorProject...");
        doorSensor = new DoorSensor(0, 17, 23, 24);
        doorSensor.start();
    }
    
    @Override
    public void destroyApp(boolean unconditional) {
        System.out.println("Destroying DoorProject...");
        doorSensor.stop();
    }
}
