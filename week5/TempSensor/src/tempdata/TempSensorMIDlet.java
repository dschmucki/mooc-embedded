/*
 * Java Embedded MOOC
 * 
 * April 2014
 *
 * Copyright © 2014, Oracle and/or its affiliates. All rights reserved.
 */
package tempdata;

import java.io.IOException;
import java.util.Timer;
import javax.microedition.midlet.MIDlet;

/**
 * TempSensorMIDlet reports a temperature reading to the SystemController every
 * TempReadFreq seconds.
 * 
 * @author Tom McGinn
 */
public class TempSensorMIDlet extends MIDlet {

    /**
     * TempReadFreq is the number of seconds between readings of the temperature
     * sensor. By default, the value is one reading every 30 seconds.
     */
    private int TempReadFreq = 30;
    private TempSensorTask tempTask;

    @Override
    public void startApp() {
        // If a property is defined - use that as the delay
        try {
            String delayProp = getAppProperty("TempReadFreq");
            if (delayProp != null) {
                TempReadFreq = Integer.parseInt(delayProp);
            }
        } catch (NumberFormatException ex) {
            System.out.println("NumberFormatException: GPSReadFreq: " + ex);
        }

        // Output the read frequency
        System.out.println("TempReadFreq = " + TempReadFreq);

        // Construct an instance of the Temp sensor task and schedule it for regular execution
        try {
            tempTask = new TempSensorTask();
            Timer t = new Timer();
            t.schedule(tempTask, 0, (TempReadFreq * 1000));
        } catch (IOException ioe) {
            System.out.println("TempSensorMIDlet: IOException " + ioe.getMessage());
            notifyDestroyed();
        }
    }

    @Override
    public void destroyApp(boolean unconditional) {
        // Cancel the temperature task
        tempTask.cancel();
    }

    @Override
    public void pauseApp() {
    }
}
