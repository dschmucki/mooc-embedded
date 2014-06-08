/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package gpioswitchtest;

import java.io.IOException;
import javax.microedition.midlet.MIDlet;
import jdk.dio.DeviceNotFoundException;

/**
 *
 * @author Angela
 */
public class GPIOSwitchTest extends MIDlet {

  GPIOSwitch pinTest;

  /**
   * Imlet lifecycle start method
   */
  @Override
  public void startApp() {

    //Create a GPIOPin on Port 0 and Pin 17
    pinTest = new GPIOSwitch(0, 17);

    try {
      pinTest.start();
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
      pinTest.stop();
    } catch (IOException ex) {
      System.out.println("IOException: " + ex);
    }
  }
}
