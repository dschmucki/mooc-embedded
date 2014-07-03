/*
 * Java Embedded MOOC
 * 
 * April 2014
 *
 * Copyright © 2014, Oracle and/or its affiliates. All rights reserved.
 */
package gpsdata;

import java.io.IOException;
import java.util.Timer;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * MIDlet to read data from the GPS. Values are read from the device in
 * GPSReadFreq intervals and sent to the listening SystemController
 *
 * @author Simon Ritter, Tom McGinn
 */
public class GPSSensorMIDlet extends MIDlet {

    /**
     * GPSReadFreq is the number of seconds between readings of the GPS sensor.
     * By default, the value is one reading every 30 seconds.
     */
    private int GPSReadFreq = 30;
    private GPSSensorTask gpsSensorTask;

    /**
     * MIDlet lifecycle start method
     */
    @Override
    public void startApp() {

        // If a property is defined - use that as the delay
        try {
            String delayProp = getAppProperty("GPSReadFreq");
            if (delayProp != null) {
                GPSReadFreq = Integer.parseInt(delayProp);
            }
        } catch (NumberFormatException ex) {
            System.out.println("NumberFormatException: GPSReadFreq: " + ex);
        }

        // Output the read frequency
        System.out.println("GPSReadFreq = " + GPSReadFreq);

        // Construct an instance of the GPS sensor task
        try {
            gpsSensorTask = new GPSSensorTask(GPSReadFreq);
            Timer t = new Timer();
            t.schedule(gpsSensorTask, 0, (GPSReadFreq * 1000));
        } catch (IOException ioe) {
            System.out.println("GPSSensorMIDlet: IOException " + ioe.getMessage());
            notifyDestroyed();
        }
    }

    @Override
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        // Gracefully quit the thread and close the GPS device through the task
        gpsSensorTask.cancel();
    }

    @Override
    protected void pauseApp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
