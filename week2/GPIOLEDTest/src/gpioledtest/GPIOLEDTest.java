/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package gpioledtest;

import java.io.IOException;
import javax.microedition.midlet.MIDlet;
import jdk.dio.DeviceNotFoundException;

/**
 *
 * @author Angela Caicedo
 */
public class GPIOLEDTest extends MIDlet {

  GPIOLED pinTest;

  /**
   * Imlet lifecycle start method
   *
   * This method creates a GPIOLED, and invoked the blink method, to blink the
   * LED X number of times, in our case 8 times.
   */
  @Override
  public void startApp() {
    System.out.println("Starting GPIOLEDTest....");
    pinTest = new GPIOLED(24);
    try {
      pinTest.start();
    } catch (DeviceNotFoundException ex) {
      System.out.println("DeviceException: " + ex.getMessage());
      notifyDestroyed();
    } catch (IOException ex) {
      System.out.println("startApp: IOException: " + ex);
      notifyDestroyed();
    }
    pinTest.blink(8, 200);
  }

  /**
   * Imlet lifecycle termination method
   *
   * @param unconditional If the imlet should be terminated whatever
   */
  @Override
  public void destroyApp(boolean unconditional) {
    try {
      if (pinTest != null) {
        pinTest.stop();
      }
    } catch (IOException ex) {
      System.out.println("IOException: " + ex);
    }
  }

}
