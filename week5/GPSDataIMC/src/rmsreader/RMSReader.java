/*
 * Java Embedded MOOC
 * 
 * February 2014
 *
 * Copyright © 2014, Oracle and/or its affiliates. All rights reserved.
 */
package rmsreader;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * Read records from an RMS
 * 
 * @author Simon
 */
public class RMSReader {
  private final RecordStore store;
  private final int recordCount;
  
  /**
   * Constructor
   * 
   * @param name The name of the record store to access
   * @throws RecordStoreException If the record store cannot be accessed
   */
  public RMSReader(String name) throws RecordStoreException {
    store = RecordStore.openRecordStore(name, false);
    recordCount = store.getNumRecords();
  }
  
  /**
   * Get the count of the number of records in the store
   * 
   * @return The number of records in the store 
   */
  public int getRecordCount() {
    return recordCount;
  }
  
  /**
   * Print all records in the store
   */
  public void printRecords() {
    for (int i = 1; i <= recordCount; i++)
      try {
        System.out.println("Record " + i + " = " + 
            new String(store.getRecord(i)));
      } catch (RecordStoreException ex) {
        System.out.println("Bad record: " + i);
      }
  }
}