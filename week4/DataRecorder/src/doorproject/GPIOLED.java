/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package doorproject;

import java.io.IOException;
import jdk.dio.DeviceManager;
import jdk.dio.DeviceNotFoundException;
import jdk.dio.gpio.GPIOPin;

/**
 *
 * @author Angela
 */
public class GPIOLED {

  //Pin where the LED is connected. Be default we are using Pin 23.
  private int ledID = 23;

  private GPIOPin LED;   // LED
  public boolean stopBlink = false;
  public boolean iAmBlinking = false;

  /**
   * GPIOLED Constructor
   *
   * @param pinNumber Pin where the LED is connected
   */
  public GPIOLED(int pinNumber) {
    ledID = pinNumber;
  }

  /**
   * This method start the connection to the LED, and turns it on.
   *
   * @throws IOException
   * @throws jdk.dio.DeviceNotFoundException
   */
  public void start() throws IOException, DeviceNotFoundException {

    // Open the LED pin (Output)
    LED = DeviceManager.open(ledID);

    //Set LED initial value
    LED.setValue(false);

    //Small pause is introduce
    try {
      Thread.sleep(1000);
    } catch (InterruptedException ex) {
    }

  }

  /**
   * This method set the value for the LED
   *
   * @param value new value for the LED
   * @throws IOException
   */
  public void setValue(boolean value) throws IOException {
    LED.setValue(value);
  }

  /**
   * This method set the value for the LED
   *
   * @return It returns the value of the LED
   * @throws IOException
   */
  public boolean getValue() throws IOException {
    return LED.getValue();
  }

  /**
   * This method allows to stop the LED blinking process
   *
   * @param status Status for the stopBlinking variable. If true, this will
   * cause an active blinking to stop.
   */
  public void setStopBlink(boolean status) {
    stopBlink = status;
  }

  /**
   * This method will blink the LED. We used a separate thread so the program
   * won't get paused for other actions.
   *
   * @param times Number of LED's blinks.
   */
  public void blink(final int times) {
    stopBlink = false;
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          if (!iAmBlinking) {  //If I'm already blinking dont blink again!
            iAmBlinking = true;  //LED blinking, don't start another blinking process again
            for (int i = 0; (i < (times * 2)) && !stopBlink; i++) {
              try {
                setValue(!getValue());  //Blink
                Thread.sleep(1000);
              } catch (InterruptedException e) {
                System.out.println(e.getMessage());
              }
            }
            iAmBlinking = false;  //blinking done, it's free for a new blink
            if (!stopBlink) {
              setValue(true);  // Set the value of the LED to true
            }
          }
        } catch (IOException e) {
          System.out.println(e.getMessage());
        }
      }
    }).start();
  }

  /**
   * Method to turn off the LED before ending the application nd closing the
   * connection with the LED.
   *
   * @throws IOException
   */
  public void close() throws IOException {
    if (LED != null) {
      LED.setValue(false);
      LED.close();
    }
  }
}
