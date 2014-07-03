package gpsdata.client;

import java.io.IOException;

/**
 *
 * @author Dominik
 */
public class GPSSensorClient extends ServiceClient {

    public GPSSensorClient(String serviceName) throws IOException {
        super(serviceName);
    }
    
    public void sendData(String position, String velocity) throws IOException {
        printMessage("persistData", INFO);
        String message = "^^" + position + "^" + velocity;
        
        printMessage("writing data", INFO);
        output.write(message.getBytes());
        byte[] response = new byte[2];
        input.read(response);
        String responseString = new String(response);
        printMessage("got response string " + responseString, INFO);

        /* Check that the response is correct */
        if (!responseString.startsWith("OK")) {
            throw new IOException("Incorrect response code received");
        }
    }
}
