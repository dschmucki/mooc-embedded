/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. 
 */
package gpsdata;

import gpsdata.persist.FilePersistentStore;
import gpsdata.persist.PersistentStore;
import gpsdata.persist.RMSPersistentStore;
import gpsdata.sensor.AdaFruitGPSUARTSensor;
import java.io.IOException;
import javax.microedition.midlet.MIDlet;
import static mooc.data.Messages.ERROR;
import mooc.data.gps.Position;
import mooc.data.gps.Velocity;

/**
 *
 * @author Tom McGinn
 */
public class RecorderMidlet extends MIDlet {

  private static final String SERIAL_PORT = "/dev/ttyAMA0";

  /**
   * Imlet lifecycle start method
   */
  @Override
  public void startApp() {
    System.out.println("GPS recording Imlet starting");
    // Experiment with both types of accessing the GPS device
    try (AdaFruitGPSUARTSensor gps = new AdaFruitGPSUARTSensor()) {

      gps.setVerbose(true);
      gps.setMessageLevel(ERROR);
      
      PersistentStore store = new RMSPersistentStore("gps-data");
      PersistentStore filePersistentStore = new FilePersistentStore("/rootfs/tmp/gps-data.txt", true, 0); 
      
      /* As an example we'll take one reading every second for 5 readings
       */
      for (int i = 0; i < 5; i++) {
        Position p = gps.getPosition();
        Velocity v = gps.getVelocity();
        System.out.println("Position and velocity: " + "^^" + p + "^" + v);
        store.saveData(p, v);
        filePersistentStore.saveData(p, v);
        Thread.sleep(1000);
      }
    } catch (IOException ioe) {
      System.out.println("GPSTestMidlet: IOException " + ioe.getMessage());
      ioe.printStackTrace();
    } catch (InterruptedException ex) {
      // Ignore
    }

    /* Terminate the Imlet correctly */
    System.out.println("GPSTestMidlet finished");
    notifyDestroyed();
  }

  @Override
  public void destroyApp(boolean unconditional) {
    // Nothing to do here, since we are using AutoCloseable to gracefully close connections
  }
}
