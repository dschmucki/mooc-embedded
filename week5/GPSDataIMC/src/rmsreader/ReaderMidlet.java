/*
 * Java Embedded MOOC
 * 
 * February 2014
 *
 * Copyright © 2014, Oracle and/or its affiliates. All rights reserved.
 */
package rmsreader;

import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStoreException;

/**
 * Simple Imlet to read and print GPS data stored in the RMS
 * 
 * @author Simon
 */
public class ReaderMidlet extends MIDlet {
  /**
   * Start application lifecycle method
   */
  @Override
  public void startApp() {
    try {
      System.out.println("Opening RMS");
      RMSReader reader = new RMSReader("gps-data");
      System.out.println("RMS has " + reader.getRecordCount() + " records");
      reader.printRecords();
      System.out.println("Done");
    } catch (RecordStoreException ex) {
      System.out.println("Unable to access RMS");
      System.out.println(ex.getMessage());
    }
    notifyDestroyed();
  }

  /**
   * Application pause lifecycle method
   */
  @Override
  public void pauseApp() {
  }

  /**
   * Application destroy lifecycle method
   * 
   * @param unconditional How to terminate the application
   */
  @Override
  public void destroyApp(boolean unconditional) {
  }
}