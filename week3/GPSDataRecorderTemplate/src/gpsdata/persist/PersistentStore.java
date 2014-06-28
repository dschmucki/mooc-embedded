/* Copyright Â© 2014, Oracle and/or its affiliates. All rights reserved.
 * Java Embedded MOOC
 * 
 * January 2014
 */
package gpsdata.persist;

import mooc.data.gps.Position;
import mooc.data.gps.Velocity;
import java.io.IOException;

/**
 * Interface for classes that can store persistent GPS data, for example RMS
 * or a file.
 * 
 * @author Simon Ritter
 */
public interface PersistentStore extends AutoCloseable {
  /**
   * Save GPS data: position and velocity
   * 
   * @param position The position data to save
   * @param velocity The velocity data to save
   * @return The number of the record saved
   * @throws IOException If there is an IO error
   */
  public int saveData(Position position, Velocity velocity) throws IOException;
  
  /**
   * Get the record count
   * 
   * @return The number of records in the persistent store
   */
  public int getRecordCount();
}