/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package tempbarosensor;

import java.io.IOException;
import javax.microedition.midlet.MIDlet;

/**
 * Midlet for testing our sensor
 *
 * @author Angela Caicedo
 */
public class TempSensorTest extends MIDlet {

    private BMP180TemperaturePressureSensor myDev;
   
    @Override
    public void startApp() {
        System.out.println("Starting TempPressSensorTest...");
        myDev = new BMP180TemperaturePressureSensor();
        
        // An example of an anonymous thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(1000);
                        double tempC = myDev.getTemperatureInCelsius();
                        double tempF = myDev.celsiusToFahrenheit(tempC);
                        double pressPa = myDev.getPressureInHPa();
                        double pressInHg = myDev.getPressureInInchesMercury();
                        System.out.format("Temp: %.2fC\t%.2fF\t\tPressure: %.4fHPa\t%.4finHg\n", tempC, tempF, pressPa, pressInHg);
                    }
                } catch (IOException | InterruptedException e) {
                    System.out.println("startApp: Exception: " + e);
                }
            }
        }).start();
    }

    @Override
    public void destroyApp(boolean unconditional) {
    }
}
