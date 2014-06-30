package serviceemulatorpi;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;

/**
 *
 * @author Dominik
 */
class DataServer implements Runnable {

    private int port;
    private String[] dataSource;
    
    private final ServerSocketConnection server;
    private boolean running = true;
    
    public DataServer(final String[] dataSource, final int port) throws IOException {
        this.port = port;
        this.dataSource = dataSource;
        
        server = (ServerSocketConnection)Connector.open("socket://:" + port);
    }
    
    @Override
    public void run() {
        System.out.println("DataServer running...");
        
        while (running) {
            try {
                StreamConnection clientConnection = server.acceptAndOpen();
                System.out.println("Connection received");
                DataConnection connection = new DataConnection(dataSource, clientConnection);
                new Thread(connection).start();
            } catch (IOException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void stop() {
        running = false;
    }
    
}
