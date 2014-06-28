/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. 
 * Java Embedded MOOC
 * 
 * February 2014
 */
package gpsdata.sensor;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import jdk.dio.DeviceManager;
import jdk.dio.uart.UART;

/**
 * AdaFruit GPS sensor accessed through the UART interface of the device IO API.
 *
 * @author Simon
 */
public class AdaFruitGPSUARTSensor extends AdaFruitGPSSensor
        implements AutoCloseable {

    private static final int UART_DEVICE_ID = 40;
    private static final int BAUD_RATE = 9600;

    private UART uart;

    /**
     * Constructor
     *
     * @throws IOException If there is an IO error
     */
    public AdaFruitGPSUARTSensor() throws IOException {
        uart = DeviceManager.open(UART_DEVICE_ID);
        uart.setBaudRate(BAUD_RATE);
        InputStream in = Channels.newInputStream(uart);
        InputStreamReader isr = new InputStreamReader(in);
        serialBufferedReader = new BufferedReader(isr);
        
        System.out.println("AdaFruit GPS Sensor: DIO API UART opened");
    }

    /**
     * Close the connection to the GPS receiver via the UART
     *
     * @throws IOException If there is an IO error
     */
    @Override
    public void close() throws IOException {
        serialBufferedReader.close();
        uart.close();
    }
}
