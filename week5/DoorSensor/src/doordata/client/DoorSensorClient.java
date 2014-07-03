/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package doordata.client;

import java.io.IOException;

/**
 * The DoorSensorClient opens a IMC connection to the system controller when
 * a door event has occurred.
 * 
 * @author Tom McGinn
 */
public class DoorSensorClient extends ServiceClient {

    /**
     * Constructor for the DoorSensorClient
     *
     * @param serviceName
     * @throws IOException
     */
    public DoorSensorClient(String serviceName) throws IOException {
        super(serviceName);
    }

    /**
     * 
     * @param doorEvent One or more String door events
     * @throws IOException 
     */
    public void sendData(String... doorEvent) throws IOException {
        printMessage("persistData", INFO);
        String message = "";
        // Create a comma separated set of data values
        for (int i = 0; i < doorEvent.length;) {
            message += doorEvent[i];
            i++;
            if (i < doorEvent.length) {
                message += ",";
            }
        }
        // Terminate the data with the appropriate carat sequence
        message += "^^^";
        printMessage("writing data", INFO);
        output.write(message.getBytes());
        byte[] response = new byte[2];
        input.read(response);
        //input.readFully(response);
        String responseString = new String(response);
        printMessage("got response string " + responseString, INFO);

        /* Check that the response is correct */
        if (!responseString.startsWith("OK")) {
            throw new IOException("Incorrect response code received");
        }
        
    }
}