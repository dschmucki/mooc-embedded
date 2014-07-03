/*
 * Java Embedded MOOC
 * 
 * March 2014
 *
 * Copyright © 2014, Oracle and/or its affiliates. All rights reserved.
 */
package rmspersistdata;

import java.io.IOException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import mooc.data.Messages;
import persistservice.PersistService;

/**
 * Persist data received from IMC client to the RMS
 *
 * @author Simon Ritter
 */
public class RMSPersistService extends PersistService implements Messages {

    private RecordStore store;

    /**
     * Constructor
     *
     * @param recordStoreName The name of the record store to use
     * @throws IOException If there is an IO error
     */
    public RMSPersistService(String recordStoreName) throws IOException {

        try {
            printMessage("constructor opening record store", INFO);
            store = RecordStore.openRecordStore(recordStoreName, true);
        } catch (RecordStoreException rse) {
            printMessage("Unable to open record store: " + recordStoreName, ERROR);
            System.out.println(rse.getMessage());
        }
    }

    /**
     * Save GPS data: position and velocity
     *
     * @param data The data to save, as a String
     * @throws IOException If there is an IO error
     */
    @Override
    public void saveData(String data) throws IOException {
        byte[] dataBytes = data.getBytes();

        try {
            int recordNumber = store.addRecord(dataBytes, 0, dataBytes.length);
            printMessage("saved record number " + recordNumber, INFO);
        } catch (RecordStoreException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    /**
     * Close the persistent store to ensure data is in a consistent state
     */
    @Override
    public void close() {
        try {
            printMessage("closing record store", INFO);
            store.closeRecordStore();
        } catch (RecordStoreException ex) {
            printMessage("Failed to close the record store", ERROR);
        }
    }

    @Override
    public int getRecordCount() {
        int recordCount = 0;
        try {
            recordCount = store.getNumRecords();
        } catch (RecordStoreNotOpenException ex) {
            printMessage("Error reading record count: " + ex, ERROR);
        }
        return recordCount;
    }

}
