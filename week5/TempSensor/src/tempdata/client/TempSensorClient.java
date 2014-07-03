package tempdata.client;

import java.io.IOException;

/**
 *
 * @author Dominik
 */
public class TempSensorClient extends ServiceClient {

    public TempSensorClient(String serviceName) throws IOException {
        super(serviceName);
    }
    
    public void sendData(String temperature) throws IOException {
        printMessage("persistData", INFO);
        String message = "^" + temperature + "^^";
        
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
