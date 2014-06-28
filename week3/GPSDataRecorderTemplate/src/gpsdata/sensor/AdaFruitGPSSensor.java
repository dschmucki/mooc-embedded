/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. 
 * Java Embedded MOOC
 * 
 * February 2014
 */
package gpsdata.sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import mooc.data.Messages;
import static mooc.data.Messages.ERROR;
import static mooc.data.Messages.INFO;
import mooc.data.gps.Position;
import mooc.data.gps.Velocity;
import mooc.sensor.GPSSensor;

/**
 * Common code for the AdaFruit Ultimate GPS sensor. This is an abstract class
 * so the implementation of the readDataLine method can be implemented in
 * subclasses that can use different methods to communicate with the GPS
 * receiver.
 *
 * @author Simon
 */
public abstract class AdaFruitGPSSensor implements GPSSensor, Messages {

  private static final String POSITION_TAG = "GPGGA";
  private static final String VELOCITY_TAG = "GPVTG";

  private final ArrayList<String> fields = new ArrayList<>();
  protected BufferedReader serialBufferedReader;
  private boolean verbose = false;
  private int messageLevel = 1;

  /**
   * Get a line of raw data from the GPS sensor
   *
   * @return The complete line of data
   * @throws IOException If there is an IO error
   */
  protected String readDataLine() throws IOException {
    String dataLine = null;

    /**
     * All data lines start with a '$' character so keep reading until we find a
     * valid line of data
     */
    dataLine = serialBufferedReader.readLine();
    while (!dataLine.startsWith("$")) {
        dataLine = serialBufferedReader.readLine();
    }

    /* Return what we got */
    return dataLine;
  }

  /**
   * Get a string of raw data from the GPS receiver. How this happens is
   * sub-class dependent.
   *
   * @param type The type of data to be retrieved
   * @return A line of data for that type
   * @throws IOException If there is an IO error
   */
  @Override
  public String getRawData(String type) throws IOException {
    boolean foundGGAData = false;
    String dataLine;
    String result = "";

    /*
     * Read continuously from the device until type is matched
     */
    while (!foundGGAData) {
        dataLine = serialBufferedReader.readLine();
        
    /* Extract the type of the data - past the leading $*/
        if (dataLine.length() > 6)  {
            String extractedType = dataLine.substring(1, 6);
    /* Compare the type extracted from the string with the type passed in 
     * If there is match, break out of the loop
     */
         if (extractedType.equalsIgnoreCase(type)) {
                foundGGAData = true;
                result = dataLine.substring(7);
            }
        }
    }
    /*
     * Return only the substring that starts at character 7
     */
    return result;
  }

  /**
   * Get the current position
   *
   * @return The position data
   * @throws IOException If there is an IO error
   */
  @Override
  public Position getPosition() throws IOException {
    String rawData;
    long timeStamp = 0;
    double latitude = 0;
    double longitude = 0;
    double altitude = 0;
    char latitudeDirection = 0;
    char longitudeDirection = 0;

    /* Read data repeatedly, until we have valid data */
    while (true) {
      rawData = getRawData(POSITION_TAG);

      /* Handle situation where we didn't get data */
      if (rawData == null) {
        printMessage("NULL position data received", ERROR);
        continue;
      }

      if (rawData.contains("$GP")) {
        printMessage("Corrupt position data", ERROR);
        continue;
      }

      if (splitCSVString(rawData) < 10) {
          printMessage("Not enough data", ERROR);
          continue;
      };
      
      //timeStamp = Long.parseLong(fields.get(0));
      latitude = Double.parseDouble(fields.get(1));
      longitude = Double.parseDouble(fields.get(3));
      altitude = Double.parseDouble(fields.get(8));
      latitudeDirection = fields.get(2).charAt(0);
      longitudeDirection = fields.get(4).charAt(0);
      
      /* Passed all the tests so we have valid data */
      break;
    }

    /* Record a time stamp for the reading */
    Date now = new Date();
    timeStamp = now.getTime() / 1000;

    /* Return the encapsulated data */
    return new Position(timeStamp, latitude, latitudeDirection,
            longitude, longitudeDirection, altitude);
  }

  /**
   * Get the current velocity
   *
   * @return The velocity data
   * @throws IOException If there is an IO error
   */
  @Override
  public Velocity getVelocity() throws IOException {
    String rawData = getRawData(VELOCITY_TAG);
    double track = 0;
    double speed = 0;

    while (true) {
      /* Handle the situation where we didn't get valid data */
      if (rawData == null) {
        printMessage("NULL velocity data received", ERROR);
        continue;
      }

      if (splitCSVString(rawData) < 8) {
          printMessage("Not enough data", ERROR);
          continue;
      };
      
      // TODO check for the right fields
      track = Double.parseDouble(fields.get(0));
      speed = Double.parseDouble(fields.get(6));
      
      break;
    }

    printMessage("velocity data = " + rawData, INFO);

    /* Record a time stanp for the reading */
    Date now = new Date();
    long timeStamp = now.getTime() / 1000;

    printMessage("Bearing = " + fields.get(0), DATA);
    printMessage("speed = " + fields.get(6), DATA);

    /* Return the Velocity object */
    return new Velocity(timeStamp, track, speed);
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
   * Break a comma separated value string into its individual fields. We need to
   * have this as explicit code because Java ME does not support String.split or
   * java.util.regex and StringTokenizer has a bug that affects empty fields.
   *
   * @param input The CSV input string
   * @return The number of fields extracted
   */
  private int splitCSVString(String input) {
    /* Clear the list of data fields */
    fields.clear();
    int start = 0;
    int end;

    while ((end = input.indexOf(",", start)) != -1) {
      fields.add(input.substring(start, end));
      start = end + 1;
    }

    return fields.size();
  }

  /**
   * Print a message if verbose messaging is turned on
   *
   * @param message The message to print
   * @param level Message level
   */
  protected void printMessage(String message, int level) {
    if (verbose && level <= messageLevel) {
      System.out.println("AdaFruit GPS Sensor: " + message);
    }
  }
}
