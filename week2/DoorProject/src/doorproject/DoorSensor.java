package doorproject;

import hardware.GPIOLED;
import hardware.GPIOSwitch;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceConfig;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;
import mooc.data.SwitchData;
import mooc.sensor.SwitchSensor;

/**
 *
 * @author Dominik
 */
public class DoorSensor implements PinListener, SwitchSensor {

    private GPIOLED greenLED;
    private GPIOLED redLED;
    private GPIOPin switchPin;
    
    private int switchPortID;
    private int switchPinID;
    private int doorClosedLEDPinNumber;
    private int doorOpenendLEDPinNumber;
    
    public DoorSensor(final int switchPortID, final int switchPinID, final int doorClosedLEDPinNumber, final int doorOpenendLEDPinNumber) {
        this.switchPortID = switchPortID;
        this.switchPinID = switchPinID;
        this.doorClosedLEDPinNumber = doorClosedLEDPinNumber;
        this.doorOpenendLEDPinNumber = doorOpenendLEDPinNumber;
    }
    
    public void start() {
        greenLED = new GPIOLED(doorClosedLEDPinNumber);
        redLED = new GPIOLED(doorOpenendLEDPinNumber);
        GPIOPinConfig switchConfig = new GPIOPinConfig(switchPortID, switchPinID, GPIOPinConfig.DIR_INPUT_ONLY,
            DeviceConfig.DEFAULT, GPIOPinConfig.TRIGGER_BOTH_EDGES, false);

        try {
            greenLED.start(true);
            redLED.start(false);
            //Open pin using the switchConfig information
            switchPin = (GPIOPin) DeviceManager.open(switchConfig);
            switchPin.setInputListener(this);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void stop() {
        try {
            if (greenLED != null) {
                greenLED.stop();
            }
            if (redLED != null) {
                redLED.stop();
            }
            if (switchPin != null) {
                switchPin.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * The valueChanged method performs the following tasks:
     * If the value of the switch is true, turn the door closed LED on and the door open LED off
     * If the value of the switch is false, turn the door closed LED off, and blink the open door LED three times and then leave it on
     * @param event 
     */
    @Override
    public void valueChanged(PinEvent event) {
        GPIOPin pin = (GPIOPin) event.getDevice();
        if (pin == switchPin) {
            if (event.getValue() == false) {
                try {
                    greenLED.setValue(true);
                    redLED.setValue(false);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                try {
                    greenLED.setValue(false);
                    redLED.blink(3, 1000);
                    redLED.setValue(true);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    /** 
     * The getState method should return the current state of the switch (true or false)
     * 
     * @return
     * @throws IOException 
     */
    @Override
    public boolean getState() throws IOException {
        return switchPin != null ? switchPin.getValue() : false;
    }

    @Override
    public SwitchData getSwitchData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
