/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package tempbarosensor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import mooc.data.TemperatureData;
import mooc.sensor.PressureSensor;
import mooc.sensor.TemperatureSensor;

/**
 *
 * @author Angela Finally the Class for the temperature sensor.
 */
public class BMP180TemperaturePressureSensor extends BMP180Sensor implements TemperatureSensor, PressureSensor {

    // oversampling setting (0: ultra low power mode, 1: standard, 2: high resolution, 3: ultra high resolution)
    private static int oss = BMP180Mode.STANDARD.getOSS();

    // Temperature read address
    private static final int tempAddr = 0xF6;
    // Read temperature command
    private static final byte getTempCmd = (byte) 0x2E;
    // Temperature read address
    private static final int pressAddr = 0xF6;
    // Read temperature command
    private static final byte getPressCmd = (byte) (0x34 + (oss << 6));

    //Uncompensated Temperature data
    private int UT;

    // Shared ByteBuffers
    private ByteBuffer uncompTemp;

    //Uncompensated Pressure data
    private int UP;

    // Shared ByteBuffers
    private ByteBuffer uncompPress;

    /**
     * Temperature sensor constructor. Just invoke the parent constructor
     */
    public BMP180TemperaturePressureSensor() {
        super();
        uncompTemp = ByteBuffer.allocateDirect(2);
        uncompPress = ByteBuffer.allocateDirect(3);
    }

    /**
     * Temperature Sensor constructor. It just invoke the parent's constructor
     *
     * @param i2cBus
     * @param address
     * @param addressSizeBits
     * @param serialClock
     */
    public BMP180TemperaturePressureSensor(int i2cBus, int address, int addressSizeBits, int serialClock) {
        super(i2cBus, address, addressSizeBits, serialClock);
        uncompTemp = ByteBuffer.allocateDirect(2);
        uncompPress = ByteBuffer.allocateDirect(3);
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

        // Delay before reading the temperature - 4.5 ms per the data sheet
        try {
            Thread.sleep(5);
        } catch (InterruptedException ex) {
        }

        //Read uncompressed data
        uncompTemp.clear();
        int result = myDevice.read(tempAddr, subAddressSize, uncompTemp);
        if (result < 2) {
            System.out.println("Enough data for temperature not read");
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
     * Calculate temperature in Fahrenheit based on a Celsius temp
     * 
     * @param temp - The temperature in degrees Celsius to convert to Fahrenheit
     * @return double - Temperature in degrees Fahrenheit, converted from Celsius
     */
    public double celsiusToFahrenheit(double temp) {
        return ((temp * 1.8) + 32);
    }

    /**
     * Return the current temperature and timestamp in a TemperatureData object
     *
     * @return TemperatureData instance
     */
    @Override
    public TemperatureData getTemperatureData() {
        double temp = 0;
        try {
            temp = getTemperatureInCelsius();
        } catch (IOException ex) {
            System.out.println("BMP180TemperatureSensor: getTemperatureDate: " + ex);
        }
        return new TemperatureData(new Date().getTime(), temp);
    }

    /**
     * Method for reading the pressure. Remember the sensor will provide us with
     * raw data, and we need to transform in some analyzed value to make sense.
     * All the calculations are normally provided by the manufacturer. In our
     * case we use the calibration data collected at construction time.
     *
     * @return Pressure in HPa as a double
     * @throws IOException If there is an IO error reading the sensor
     */
    @Override
    public double getPressureInHPa() throws IOException {
        // Write the read pressure command to the command register
        writeOneByte(controlRegister, getPressCmd);

        // Delay before reading the pressure - 4.5 ms per the data sheet
        try {
            Thread.sleep(5);
        } catch (InterruptedException ex) {
        }

        //Read uncompressed data
        uncompPress.clear();
        int result = myDevice.read(pressAddr, subAddressSize, uncompPress);
        if (result < 3) {
            System.out.println("Not enough data for pressure read");
        }

        // Get the uncompensated temperature as a signed two byte word
        uncompPress.rewind();
        byte[] data = new byte[3];
        uncompPress.get(data);
        UP = (((data[0] << 16) & 0xFF0000) + ((data[1] << 8) & 0xFF00) + (data[2] & 0xFF)) >> (8 - oss);

        // Calculate the actual pressure
        int B6 = B5 - 4000;
        int X1 = (B2 * (B6 * B6) >> 12) >> 11;
        int X2 = AC2 * B6 >> 11;
        int X3 = X1 + X2;
        int B3 = ((((AC1 * 4) + X3) << oss) + 2) / 4;
        X1 = AC3 * B6 >> 13;
        X2 = (B1 * ((B6 * B6) >> 12)) >> 16;
        X3 = ((X1 + X2) + 2) >> 2;
        int B4 = (AC4 * (X3 + 32768)) >> 15;
        int B7 = (UP - B3) * (5000 >> oss);

        int pa;
        if (B7 < 0x80000000) {
            pa = (B7 * 2) / B4;
        } else {
            pa = (B7 / B4) * 2;
        }
        X1 = (pa >> 8) ^ 2;
        X1 = (X1 * 3038) >> 16;
        X2 = (-7357 * pa) >> 16;
        pa += ((X1 + X2 + 3791) >> 4);

        return (double) pa / 100;
    }

    @Override
    public double getPressureInInchesMercury() throws IOException {
        return (double) (getPressureInHPa() * 0.0296);
    }
}
