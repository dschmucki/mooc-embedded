/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package doorproject;

import java.io.IOException;
import java.util.Date;
import jdk.dio.DeviceConfig;
import jdk.dio.DeviceManager;
import jdk.dio.DeviceNotFoundException;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;
import mooc.data.SwitchData;
import mooc.sensor.SwitchSensor;

/**
 *
 * @author Angela
 */
/**
 * This class contains two LEDs (GPIOLED objects) and one switch. the idea is
 * that when the doors is open, we set a red LED on (or we make it blink), to
 * make sure we alert about the door). When the door is close the greenLED is
 * on, to show everything is fine
 */
public class DoorSensor implements PinListener, SwitchSensor, AutoCloseable {

  //Switch GPIO Port.  Default value 0
  private int switchPortID = 0;
  //Switch GPIO Pin. Default value 17
  private int switchPinID = 17;

  //
  private final boolean filteringPreviousState = true;

  //LED definition
  private final GPIOLED redLED;   // Blinks when the door opens
  private final GPIOLED greenLED;  //On when the door is closed

  //Door switch
  private GPIOPin switchPin;

  /**
   * Constructor for DoorSensor
   *
   * @param portID port to be use
   * @param pinID pin where the switch is connected
   * @param redLEDPin pin where the red LED is connected
   * @param greenLEDPin pint where the green LED is connected
   */
  public DoorSensor(int portID, int pinID, int redLEDPin, int greenLEDPin) {
    switchPortID = portID;
    switchPinID = pinID;
    redLED = new GPIOLED(redLEDPin);
    greenLED = new GPIOLED(greenLEDPin);

  }

  /**
   * Method to create the Door sensor: A group of 2 LEDs and one switch
   *
   * @throws IOException
   * @throws DeviceNotFoundException
   */
  public void start() throws IOException, DeviceNotFoundException {

    // Config information for the switch
    GPIOPinConfig config1 = new GPIOPinConfig(switchPortID, switchPinID, GPIOPinConfig.DIR_INPUT_ONLY,
            DeviceConfig.DEFAULT, GPIOPinConfig.TRIGGER_BOTH_EDGES, false);

    //Open pin using the config1 information
    switchPin = DeviceManager.open(config1);

    //Create the connection to the LEDs
    redLED.start();
    greenLED.start();
    //Door is closed by default, as the default value of the switch (unpressed) is false
    redLED.setValue(false);
    greenLED.setValue(true);

    // Add this class as a pin listener to the buttons
    switchPin.setInputListener(this);

  }

  /**
   * Method to close connection to the pin
   *
   * @throws java.io.IOException
   */
  @Override
  public void close() throws IOException {
    //close connections on the LEDs
    redLED.close();
    greenLED.close();
    if (switchPin != null) {
      switchPin.close();
    }
  }

  /**
   * Method to be invoked when the value of the pin changed. In our case we are
   * listening to Switch changes
   *
   * @param event
   */
  @Override
  public void valueChanged(PinEvent event) {
    GPIOPin pin = event.getDevice();
    //Verify the event come from the switch
    try {
      if (pin == switchPin) {
        // A door open event is the result of the switch being pressed
        if (event.getValue() == true) {
          greenLED.setValue(false);
          redLED.setValue(false);
          //Turn your LED red or make it blink
          redLED.blink(3);

        } else {
          greenLED.setValue(true);
          redLED.setStopBlink(true);
          redLED.setValue(false);
        }
      }
    } catch (IOException ex) {
      System.out.println("DoorSensor: IOException: " + ex);
    }
  }

  /**
   * Gets doors state, if the door is open or close
   *
   * @return True if the door is close, or false if the door is open
   * @throws IOException
   */
  @Override
  public boolean getState() throws IOException {
    return switchPin.getValue();
  }

  /**
   * Get the door state as a SwitchData object - used to write an event
   *
   * @return Current SwitchData object (timestamp + switch state)
   */
  @Override
  public SwitchData getSwitchData() {
    boolean state = false;
    try {
      state = getState();
    } catch (IOException ex) {
      System.out.println("DoorSensor: getSwitchData: " + ex);
    }
    return new SwitchData(new Date().getTime(), state);
  }
}
