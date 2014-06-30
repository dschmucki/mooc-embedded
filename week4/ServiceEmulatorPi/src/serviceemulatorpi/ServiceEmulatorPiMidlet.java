package serviceemulatorpi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.Connector;
import javax.microedition.io.PushRegistry;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author Dominik
 */
public class ServiceEmulatorPiMidlet extends MIDlet {
    
    private static final byte NEWLINE = 13;
    private static final String READ_DATA = "READ";
    private static final String DISCONNECT = "DISCONNECT";
   
    String[] dataSource = {"^1389744181,5^^", "^1389747781,-3^^", "^1389751561,7^^", "^1389753606,10^^", "^1389756000,12^^", "^1389758577,8^^", "^^1387202580,40.5583739,N,85.6591442,E,157.25^", "^^1387202967,41.1234567,S,86.1234567,W,135.89^"};
    DataServer server;
    StreamConnection socket;
    
    
    @Override
    public void startApp() {
        String[] activeConns = PushRegistry.listConnections(true);
        if (activeConns.length > 0) {
            for (String conn : activeConns) {
                System.out.println(conn);
                if (conn.startsWith("socket")) {
                    try {
                        // server
                        ServerSocketConnection serverSocket = (ServerSocketConnection) Connector.open(conn);
                        System.out.println("ServerSocket waiting on client connection");
                        socket = serverSocket.acceptAndOpen();
                        
                        // client
                        openClientSocket();
                    } catch (IOException ex) {
                        Logger.getLogger(ServerMidlet.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                }
            }
        }
        notifyDestroyed();
    }
    
    private void openClientSocket() throws IOException {
        DataInputStream input = socket.openDataInputStream();
        DataOutputStream output = socket.openDataOutputStream();
        
        byte[] buffer = new byte[128];
        
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
            //break;
        } else {
            System.out.println("Unrecognized command [" + command + "]");
        }
    }
    
    @Override
    public void destroyApp(boolean unconditional) {
        server.stop();
    }
}
