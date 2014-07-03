/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package tempdata.sensor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Formatter;
import mooc.data.Messages;
import mooc.data.TemperatureData;
import mooc.sensor.TemperatureSensor;

/**
 *
 * @author Angela Finally the Class for the temperature sensor.
 */
public class BMP180TemperatureSensor extends BMP180Sensor implements Messages, TemperatureSensor, AutoCloseable {

    private boolean verbose = false;
    private int messageLevel = 1;

    private Formatter formatter = new Formatter();

    // Temperature read address
    static final int tempAddr = 0xF6;
    // Read temperature command
    static final byte getTempCmd = (byte) 0x2E;

    //Uncompensated Temperature data
    int UT;

    // Shared ByteBuffers
    ByteBuffer uncompTemp;

    /**
     * Temperature sensor constructor. Just invoke the parent constructor
     *
     * @throws java.io.IOException
     */
    public BMP180TemperatureSensor() throws IOException {
        super();
        uncompTemp = ByteBuffer.allocateDirect(2);
    }

    /**
     * Temperature Sensor constructor. It just invoke the parent's constructor
     *
     * @param i2cBus
     * @param address
     * @param addressSizeBits
     * @param serialClock
     * @throws java.io.IOException
     */
    public BMP180TemperatureSensor(int i2cBus, int address, int addressSizeBits, int serialClock) throws IOException {
        super(i2cBus, address, addressSizeBits, serialClock);
        uncompTemp = ByteBuffer.allocateDirect(2);
    }

    /**
     * Method for reading the temperature. Remember the sensor will provide us
     * with raw data, and we need to transform in some analyzed value to make
     * sense. All the calculations are normally provided by the manufacturer. In
     * our case we use the calibration data collected at construction time.
     *
     * @return Temperature in Celsius as a double
     * @throws IOException If there is an IO error reading the sensor
     */
    @Override
    public double getTemperatureInCelsius() throws IOException {
        // Write the read temperature command to the command register
        writeOneByte(controlRegister, getTempCmd);

        // Delay before reading the temperature
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
        }

        //Read uncompressed data
        uncompTemp.clear();
        int result = myDevice.read(tempAddr, subAddressSize, uncompTemp);
        if (result < 2) {
            printMessage("Not enough data for temperature read", ERROR);
        }

        // Get the uncompensated temperature as a signed two byte word
        uncompTemp.rewind();
        byte[] data = new byte[2];
        uncompTemp.get(data);
        UT = ((data[0] << 8) & 0xFF00) + (data[1] & 0xFF);

        // Calculate the actual temperature
        int X1 = ((UT - AC6) * AC5) >> 15;
        int X2 = (MC << 11) / (X1 + MD);
        B5 = X1 + X2;
        float celsius = (float) ((B5 + 8) >> 4) / 10;

        return celsius;
    }

    /* 
     * Read the temperature value as a Celsius value and the convert the value to Farenheit.
     *
     * @return Temperature in Celsius as a double
     * @throws IOException If there is an IO error reading the sensor
     */
    @Override
    public double getTemperatureInFahrenheit() throws IOException {
        return celsiusToFahrenheit(getTemperatureInCelsius());
    }

    /*
     * Calculate temperature in Fahrenheit based on a celsius temp
     * 
     * @param temp - The temperature in degrees Celsius to convert to Fahrenheit
     * @return double - Temperature in degrees Fahrenheit, converted from Celsius
     */
    private double celsiusToFahrenheit(double temp) {
        return ((temp * 1.8) + 32);
    }

    /**
     * Read the temperature from the device and return as a timestamped
     * TemperatureData object
     *
     * @return current temperature data
     */
    @Override
    public TemperatureData getTemperatureData() {
        Date now = new Date();
        long timeStamp = now.getTime() / 1000;
        double c = 0;
        try {
            c = getTemperatureInCelsius();
            printMessage(formatter.format("BMP180BaroTempSensor: temp = %.2fC\n", c).toString(), INFO);
        } catch (IOException ex) {
            printMessage("IOException reading Temperature: " + ex, ERROR);
        }
        return new TemperatureData(timeStamp, c);
    }

    /**
     * Turn on or off verbose messaging
     *
     * @param verbose Whether to enable verbose messages
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Set the level of messages to display, 1 = ERROR, 2 = INFO
     *
     * @param level The level for messages
     */
    public void setMessageLevel(int level) {
        messageLevel = level;
    }

    /**
     * Print a message if verbose messaging is turned on
     *
     * @param message The message to print
     * @param level What level to log these messages at
     */
    protected void printMessage(String message, int level) {
        if (verbose && level <= messageLevel) {
            System.out.println("BMP180TemperatureSensor: " + message);
        }
    }
}
