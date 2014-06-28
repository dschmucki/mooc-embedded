package mrsreader;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author Dominik
 */
public class ReaderMidlet extends MIDlet {

    @Override
    protected void startApp() throws MIDletStateChangeException {
        try {
            RecordStore recordStore = RecordStore.openRecordStore("gps-data", false);
            System.out.println("Record count: " + recordStore.getNumRecords());

            // we don't use the filter here
            RecordEnumeration re = recordStore.enumerateRecords(null, null, false);
            while (re.hasNextElement()) {
                String data = new String(re.nextRecord());
                System.out.println(data);
            }

            //for (int i = 1; i <= recordStore.getNumRecords(); i++) {
            //    System.out.println(String.valueOf(recordStore.getRecord(i)));
            //}
        } catch (RecordStoreException ex) {
            System.out.println("Error opening RecordStore: " + ex);
        }
        
        /* Terminate the Imlet correctly */
        System.out.println("ReaderMidlet finished");
        notifyDestroyed();
    }

    @Override
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }
}
