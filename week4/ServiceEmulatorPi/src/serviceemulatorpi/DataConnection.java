package serviceemulatorpi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.StreamConnection;

/**
 *
 * @author Dominik
 */
class DataConnection implements Runnable {

    private static final byte NEWLINE = 13;
    private static final String READ_DATA = "READ";
    private static final String DISCONNECT = "DISCONNECT";
    
    private String[] dataSource;
    private final DataInputStream input;
    private final DataOutputStream output;
    private boolean running = true;
    private boolean verbose = true;
    private int messageLevel = 1;
    
    public DataConnection(String[] dataSource, StreamConnection connection) throws IOException {
        this.dataSource = dataSource;
        input = connection.openDataInputStream();
        output = connection.openDataOutputStream();
    }
    
    public void stopThread() {
        running = false;
    }
    
    @Override
    public void run() {
        byte[] buffer = new byte[128];
        
        while(running) {
            try {
                int commandLength = input.read(buffer);
                String command = new String(buffer, 0, commandLength);
                System.out.println("Command received: " + command);
                
                if (command.compareTo(READ_DATA) == 0) {
                    System.out.println("Sending " + dataSource.length);
                    output.write(String.valueOf(dataSource.length).getBytes());
                    output.write(NEWLINE);
                    for (int i = 0; i < dataSource.length; i++) {
                        output.write(dataSource[i].getBytes());
                        output.write(NEWLINE);
                    }                    
                    output.flush();
                } else if (command.compareTo(DISCONNECT) == 0) {
                    break;
                } else {
                    System.out.println("Unrecognized command [" + command + "]");
                }
            } catch (IOException ex) {
                Logger.getLogger(DataConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
