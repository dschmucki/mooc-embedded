 /* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package gpsdata;

import gpsdata.client.GPSSensorClient;
import gpsdata.sensor.AdaFruitGPSSensor;
import gpsdata.sensor.AdaFruitGPSUARTSensor;
import java.io.IOException;
import java.util.TimerTask;

/**
 * The GPSSensorTask takes a GPS position and velocity reading every
 * readInterval seconds and sends it to the system controller using IMC.
 *
 * @author Tom McGinn
 */
public class GPSSensorTask extends TimerTask {

    private final AdaFruitGPSSensor gps;
    // Interval between reads in milliseconds
    private final int readIntervalMs;
    private volatile boolean running = true;

    public GPSSensorTask(int readInterval) throws IOException {
        this.readIntervalMs = readInterval * 1000;
        gps = new AdaFruitGPSUARTSensor();
    }

    /**
     * Thread to manage the continuous reading of the GPS sensor, once an event
     * has been triggered.
     */
    @Override
    public void run() {
        // Report a GPS event
        reportGPSEvent();
    }

    /**
     * Report the GPS position and velocity to the system controller
     *
     */
    private void reportGPSEvent() {
        try {
            System.out.println("reportGPSEvent: a GPS event: " + gps.getPosition().toString() + " " + gps.getVelocity().toString());

            /**
             * Your code goes here: Create an instance of GPSSensorClient and
             * send the position and velocity string using the proper message
             * format to the system controller
             */
            try (GPSSensorClient client = new GPSSensorClient("mooc.gps:1.0;")) {
                client.sendData(gps.getPosition().toString(), gps.getVelocity().toString());
            } catch (IOException ex) {
                System.out.println("reportGPSEvent: IOException: " + ex);
            }
            
        } catch (IOException ex) {
            System.out.println("reportGPSEvent: " + ex);
        }
    }

    /**
     * Cleanly stop the thread and clean up the GPS resource
     *
     * @return Status of the canceled task
     */
    @Override
    public boolean cancel() {
        // Gracefully close the GPS device
        try {
            gps.close();
        } catch (Exception ex) {
            System.out.println("GPSSensorTask.cancel: IOException: " + ex);
        }
        return super.cancel();
    }

}
