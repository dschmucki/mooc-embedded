/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package doordata;

import java.io.IOException;
import javax.microedition.midlet.MIDlet;
import jdk.dio.DeviceNotFoundException;

/**
 * The DoorSensorMIDlet detects changes in the door switch state. Note that this
 * MIDlet uses different logic from the rest of the course - the door is in a
 * closed state when the pushbutton is not pressed, and opened when the switch
 * is pressed.
 *
 * @author Tom McGinn
 */
public class DoorSensorMIDlet extends MIDlet {

  private DoorSensorTask doorSensorTask;
  /**
   * doorOpenDelay is the number of seconds to wait before a door event is
   * triggered. This delay prevents spurious changes - ie: when a door is
   * bouncing around or not completely closed, but not open either.
   */
  private int doorOpenDelay = 5;
  private final int GPIO_PORT = 0;
  private final int SWITCH_PIN = 17;
  private final int OPENLED_PIN = 24;

  @Override
  public void startApp() {

    // If a property is defined - use that as the delay
    try {
      String delayProp = getAppProperty("DoorOpenDelay");
      if (delayProp != null) {
        doorOpenDelay = Integer.parseInt(delayProp);
      }
    } catch (NumberFormatException ex) {
      System.out.println("NumberFormatException: DoorOpenDelay: " + ex);
    }

    // Create an instance of the door sensor task
    doorSensorTask = new DoorSensorTask(GPIO_PORT, SWITCH_PIN, OPENLED_PIN);

    try {
      doorSensorTask.start(doorOpenDelay);
    } catch (DeviceNotFoundException ex) {
      System.out.println("DeviceException: " + ex.getMessage());
      notifyDestroyed();
    } catch (IOException ex) {
      System.out.println("IOException: " + ex);
      notifyDestroyed();
    }
  }

  /**
   * Imlet lifecycle termination method
   *
   * @param unconditional If the imlet should be terminated whatever
   */
  @Override
  public void destroyApp(boolean unconditional) {
    try {
      doorSensorTask.close();
    } catch (IOException ex) {
      System.out.println("IOException closing DoorSensor: " + ex);
    }
  }

}
