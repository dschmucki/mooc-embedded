/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package gpioswitchtest;

import java.io.IOException;
import jdk.dio.DeviceConfig;
import jdk.dio.DeviceManager;
import jdk.dio.DeviceNotFoundException;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;

/**
 * This class is use to create a pin for input (a switch in our case) and to
 * listen to the in changes of the pin value
 *
 * @author Angela
 */
public class GPIOSwitch implements PinListener {

  //Switch GPIO Port.  Default value 0
  private int switchPortID = 0;
  //Switch GPIO Pin. Default value 17
  private int switchPinID = 17;

  private GPIOPin switchPin;

  /**
   * Constructor for GPIOSwitch
   *
   * @param portID port to be use
   * @param pinID pin where the switch is connected
   */
  public GPIOSwitch(int portID, int pinID) {
    switchPortID = portID;
    switchPinID = pinID;
  }

  /**
   * Method to create the GPIOPin and register listening
   *
   * @throws IOException
   * @throws DeviceNotFoundException
   */
  public void start() throws IOException, DeviceNotFoundException {
    System.out.println("Starting GPIOSwitchTest...");
    // Config information for the switch
    GPIOPinConfig config1 = new GPIOPinConfig(switchPortID, switchPinID, GPIOPinConfig.DIR_INPUT_ONLY,
            DeviceConfig.DEFAULT, GPIOPinConfig.TRIGGER_BOTH_EDGES, false);

    //Open pin using the config1 information
    switchPin = (GPIOPin) DeviceManager.open(config1);

    // Add this class as a pin listener to the buttons
    switchPin.setInputListener(this);

  }

  /**
   * Method to stop connection to the pin
   *
   * @throws IOException
   */
  public void stop() throws IOException {
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
    GPIOPin pin = (GPIOPin) event.getDevice();
    if (pin == switchPin) {
      if (event.getValue() == true) {
        System.out.println(" true");
      } else {
        System.out.println(" false");
      }
    }
  }
}
