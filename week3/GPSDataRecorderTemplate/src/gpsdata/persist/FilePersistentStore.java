package gpsdata.persist;

import java.io.IOException;
import java.io.PrintStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import mooc.data.Messages;
import mooc.data.gps.Position;
import mooc.data.gps.Velocity;

/**
 *
 * @author Dominik
 */
public class FilePersistentStore implements Messages, PersistentStore {

    private final FileConnection connection;
    private final PrintStream fileWriter;
    private int count = 0;
    
    public FilePersistentStore(final String fileName, final boolean verbose, final int messageLevel) throws IOException {
        // open instance of file
        connection = (FileConnection) Connector.open("file://" + fileName, Connector.READ_WRITE);
        
        if (!connection.exists()) {
            connection.create();
        }
        
        fileWriter = new PrintStream(connection.openOutputStream());
        
        // store values of verbose and messageLevel
        fileWriter.println("Verbose: " + verbose + ", MessageLevel: " + messageLevel);
    }
    
    @Override
    public int saveData(Position position, Velocity velocity) throws IOException {
        fileWriter.println(position + "^" + velocity);
        count++;
        return 1;
    }

    @Override
    public int getRecordCount() {
        return count;
    }

    @Override
    public void close() throws Exception {
        fileWriter.close();
        connection.close();
    }
    
}
