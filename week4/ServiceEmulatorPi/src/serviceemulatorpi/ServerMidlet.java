package serviceemulatorpi;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author Dominik
 */
public class ServerMidlet extends MIDlet {
    
    String[] _msgToSend = {"^1389744181,5^^", "^1389747781,-3^^", "^1389751561,7^^", "^1389753606,10^^", "^1389756000,12^^", "^1389758577,8^^", "^^1387202580,40.5583739,N,85.6591442,E,157.25^", "^^1387202967,41.1234567,S,86.1234567,W,135.89^"};
    DataServer server;
    
    
    @Override
    public void startApp() {
        try {
            server = new DataServer(_msgToSend, 8042);
            new Thread(server).start();
        } catch (IOException ex) {
            Logger.getLogger(ServerMidlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void destroyApp(boolean unconditional) {
        server.stop();
    }
}
