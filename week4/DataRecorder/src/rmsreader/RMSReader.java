/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. 
 * RMSReader.java
 */
package rmsreader;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import server.DataSource;

/**
 * Read records from an RMS
 *
 * @author Simon Ritter @speakjava
 * @author Jim Weaver @JavaFXpert
 */
public class RMSReader implements DataSource {

  private final RecordStore store;

  /**
   * Constructor
   *
   * @param name The name of the record store to access
   * @throws RecordStoreException If the record store cannot be accessed
   */
  public RMSReader(String name) throws RecordStoreException {
    store = RecordStore.openRecordStore(name, false);
  }

  /**
   * Get the count of the number of records in the store
   *
   * @return The number of records in the store
   */
  @Override
  public int getRecordCount() {
    int count = 0;
    try {
      count = store.getNumRecords();
    } catch (RecordStoreNotOpenException ex) {
      System.out.println("RMSReader: Error getting record count: " + ex);
    }
    return count;
  }

  /**
   * Print all records in the store
   */
  public void printRecords() {
    for (int i = 1; i <= getRecordCount(); i++) {
      try {
        System.out.println("Record " + i + " = "
                + new String(store.getRecord(i)));
      } catch (RecordStoreException ex) {
        System.out.println("RMSReader: Bad record: " + i);
      }
    }
  }

  /**
   * Get a record with the specific ID.
   *
   * @param recordId
   * @return String contents of this record ID
   * @throws InvalidRecordIDException
   */
  @Override
  public String getRecord(int recordId) throws InvalidRecordIDException {
    String result = null;
    try {
      result = new String(store.getRecord(recordId));
    } catch (RecordStoreException ex) {
      System.out.println("RMSReader: RecordStore Exception: " + ex);
    }
    return result;
  }

  /**
   * Concatenate all of the records into a String
   *
   * @return String concatenation all of the records in the store (newline
   * terminated)
   */
  @Override
  public String toString() {
    String retVal = "";
    for (int i = 1; i <= getRecordCount(); i++) {
      try {
        retVal += new String(store.getRecord(i));
        retVal += "\n";
      } catch (RecordStoreException ex) {
        System.out.println("RMSReader#getDelimitedString: Bad record: " + i);
      }
    }
    return retVal;
  }
}
