/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package doordata;

import doordata.client.DoorSensorClient;
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
 * This class manages the state of the container door (switch). When the door is
 * opened, an event is signaled, and we check the state of the door for the
 * doorDelay seconds. If the state of the door is still open after that period,
 * we fire a door open event to the system controller. Note: for simplicity,
 * this class does not use the two LED's.
 *
 * @author Tom McGinn
 *
 *
 */
public class DoorSensorTask implements PinListener, SwitchSensor, Runnable, AutoCloseable {

  /**
   * Door delay - this value determines the length of time the door sensor must
   * be open before an event is recorded.
   */
  private int doorDelayMs = 0;

  // Door switch reading interval in millseconds
  private final int doorInterval = 100;

  // Door thread
  private Thread doorThread = null;
  // Door thread running status
  private boolean doorThreadRunning = false;

  //Switch GPIO Port.  Default value 0
  private int switchPortID = 0;
  //Switch GPIO Pin. Default value 17
  private int switchPinID = 17;

  // LEDs for status (not required)
  private int openedPinID = -1;

  private GPIOPin switchPin;
  private GPIOPin openLED;

  /**
   * Constructor for DoorSensor without any LEDs
   *
   * @param portID port to be use
   * @param pinID pin where the switch is connected
   */
  public DoorSensorTask(int portID, int pinID) {
    switchPortID = portID;
    switchPinID = pinID;
  }

  /**
   * Constructor for DoorSensor with LEDs
   *
   * @param portID GPIO Port for pins
   * @param switchPinID
   * @param openedPinID
   */
  public DoorSensorTask(int portID, int switchPinID, int openedPinID) {
    switchPortID = portID;
    this.switchPinID = switchPinID;
    this.openedPinID = openedPinID;
  }

  /**
   * Method to create a door sensor with a specific door delay
   *
   * @param doorDelay Time in seconds before a door event is triggered.
   * @throws java.io.IOException
   * @throws jdk.dio.DeviceNotFoundException
   */
  public void start(int doorDelay) throws IOException, DeviceNotFoundException {
    doorDelayMs = doorDelay * 1000;
    start();
  }

  /**
   * Method to create the Door sensor: A switch
   *
   * @throws IOException
   * @throws DeviceNotFoundException
   */
  public void start() throws IOException, DeviceNotFoundException {

    // Config information for the switch
    GPIOPinConfig config1 = new GPIOPinConfig(switchPortID, switchPinID, GPIOPinConfig.DIR_INPUT_ONLY,
            DeviceConfig.DEFAULT, GPIOPinConfig.TRIGGER_RISING_EDGE, false);

    //Open pin using the config1 information
    switchPin = DeviceManager.open(config1);
    switchPin.setInputListener(this);

    // If we are using LED's, open those pins also
    if (openedPinID >= 0) {
      openLED = DeviceManager.open(openedPinID);
      // Set the open door LED off by default
      openLED.setValue(false);
    }
  }

  /**
   * Method to stop connection to the pin
   *
   * @throws java.io.IOException
   */
  @Override
  public void close() throws IOException {
    if (switchPin != null) {
      switchPin.close();
    }
    if (openLED != null) {
      openLED.close();
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
    GPIOPin pin = (GPIOPin) event.getDevice();
    // if the switch did not generate the event, log it
    if (pin != switchPin) {
      System.out.println("Event not created by the switch: " + pin);
      return;
    }

    // Start a timer task to evaluate if the door is open for the entire delay period.
    // Check the state of the pin every 100 milliseconds
    // If the switch resets and then retriggers, kill the current task and start over   
    if (doorThread == null) {
      doorThread = new Thread(this);
      doorThreadRunning = true;
      doorThread.start();
    } else {
      doorThreadRunning = false;
      doorThread.interrupt();
      doorThread = new Thread(this);
      doorThreadRunning = true;
      doorThread.start();
    }

  }

  /**
   * Gets door state, if the door is open or close
   *
   * @return True if the door is close, or false if the door is open
   * @throws IOException
   */
  @Override
  public boolean getState() throws IOException {
    return switchPin.getValue();
  }

  @Override
  public SwitchData getSwitchData() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * Thread to manage the continuous reading of the door sensor, once an event
   * has been triggered.
   */
  @Override
  public void run() {
    //System.out.println("In timing loop");
    int doorDelayCountDown = doorDelayMs;
    boolean doorStatus = true;  // yes, it is open
    try {
      while (doorThreadRunning && doorDelayCountDown > 0) {
        // As long as the door is still open, stay in this run loop
        if (!getState()) {
          doorStatus = false;
          break;
        } else {
          // If there is a door warning LED, turn it on
          if (openLED != null) {
            openLED.setValue(true);
          }
          try {
            Thread.sleep(doorInterval);
          } catch (InterruptedException ex) {
          }
          doorDelayCountDown -= doorInterval;
        }
      }
      // Turn off the door open warning light
      if (openLED != null) {
        openLED.setValue(false);
      }
      // If we got to the end of this loop, trigger an actual event
      // if the door status is still open (true)
      if (doorStatus) {
        reportDoorEvent(doorStatus);
      }

    } catch (IOException ex) {
      System.out.println("IOException reading the pin: ");
    }
  }

  /**
   * Report that the door was opened for the period of time dictated by the
   * doorDelay value
   *
   * @param event
   */
  public void reportDoorEvent(boolean event) {
    System.out.println("reportDoorEvent: A door open event occurred");
    // Create a SwitchData object and send it
    SwitchData data = new SwitchData(new Date().getTime() / 1000, event);
    try (DoorSensorClient client = new DoorSensorClient("mooc.switch:1.0;")) {
      client.sendData(data.toString());
    } catch (IOException ex) {
      System.out.println("reportDoorEvent: IOException: " + ex);
    }
  }
}
