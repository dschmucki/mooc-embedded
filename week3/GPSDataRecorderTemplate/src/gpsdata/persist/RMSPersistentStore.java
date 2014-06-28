package gpsdata.persist;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import mooc.data.gps.Position;
import mooc.data.gps.Velocity;

/**
 *
 * @author Dominik
 */
public class RMSPersistentStore implements PersistentStore {

    private RecordStore recordStore;
    
    public RMSPersistentStore(final String name) {
        try {
            recordStore = RecordStore.openRecordStore(name, true);
        } catch (RecordStoreException ex) {
            System.out.println("Error creating RecordStore: " + ex);
        }
    }
    
    //SaveData should write the Position and Velocity to the persistent store using the message format defined in part 3.
    @Override
    public int saveData(Position position, Velocity velocity) throws IOException {
        byte[] data = ("^^" + position + "^" + velocity).getBytes();
        try {
            return recordStore.addRecord(data, 0, data.length);
        } catch (RecordStoreException ex) {
            System.out.println("Error: " + ex);
        }
        return 0;
    }

    @Override
    public int getRecordCount() {
        try {
            return recordStore.getNumRecords();
        } catch (RecordStoreNotOpenException ex) {
            System.out.println("Error: " + ex);
        }
        return 0;
    }

    @Override
    public void close() throws Exception {
        recordStore.close();
    }
    
}
