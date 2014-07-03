/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package tempdata;

import java.io.IOException;
import java.util.TimerTask;
import tempdata.client.TempSensorClient;
import tempdata.sensor.BMP180TemperatureSensor;

/**
 * This is a timer task run every TempReadFreq seconds
 *
 * @author Tom McGinn
 */
public class TempSensorTask extends TimerTask {

    private final BMP180TemperatureSensor temp;

    public TempSensorTask() throws IOException {
        temp = new BMP180TemperatureSensor();
    }

    @Override
    public void run() {
        reportTempEvent();
    }

    private void reportTempEvent() {
        System.out.println("TempSensorTask.reportTempEvent: a Temp event: " + temp.getTemperatureData().toString());

        /**
         * Your code goes here: Create an instance of TempSensorClient and send
         * the temperature string using the proper message format to
         * the system controller.
         */
        try (TempSensorClient client = new TempSensorClient("mooc.temp:1.0;")) {
            client.sendData(temp.getTemperatureData().toString());
        } catch (IOException ex) {
            System.out.println("reportGPSEvent: IOException: " + ex);
        }
    }

    /**
     *
     * @return Status of the canceled task
     */
    @Override
    public boolean cancel() {
        // Gracefully close the open temperature sensor
        try {
            temp.close();
        } catch (IOException ex) {
            System.out.println("TempSensorTask.cancel: IOException: " + ex);
        }
        return super.cancel();
    }

}
