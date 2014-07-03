/*
 * Java Embedded MOOC
 * 
 * April 2014
 *
 * Copyright © 2014, Oracle and/or its affiliates. All rights reserved.
 */
package doordata.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.IMCConnection;
import mooc.data.Messages;

/**
 * Client connection to a service provided by another MIDlet. Interaction
 * handled by Inter-MIDlet Communication
 *
 * @author Simon Ritter
 */
public abstract class ServiceClient implements Messages, AutoCloseable {

    protected final DataInputStream input;
    protected final DataOutputStream output;
    private final IMCConnection imcConnection;
    private boolean verbose = true;
    private int messageLevel = ERROR;

    /**
     * Constructor
     *
     * @param serviceName The name of the service to connect to
     * @throws IOException If there is an IO error
     */
    public ServiceClient(String serviceName) throws IOException {
        String imcURL = "imc://*:" + serviceName;
        printMessage("constructor: " + imcURL, INFO);
        imcConnection = (IMCConnection) Connector.open(imcURL);
        output = imcConnection.openDataOutputStream();
        input = imcConnection.openDataInputStream();
        printMessage("connection open", INFO);
    }

    /**
     * Turn on or off verbose messaging
     *
     * @param verbose Whether to enable verbose messages
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Set the level of messages to display, 1 = ERROR, 2 = INFO
     *
     * @param level The level for messages
     */
    public void setMessageLevel(int level) {
        messageLevel = level;
    }

    /**
     * Print a message if verbose messaging is turned on
     *
     * @param message The message to print
     * @param level
     */
    protected void printMessage(String message, int level) {
        if (verbose && level <= messageLevel) {
            System.out.println("ServiceClient: " + message);
        }
    }

    /**
     * Close the two streams and then connection
     *
     * @throws java.io.IOException
     */
    @Override
    public void close() throws IOException {
        output.close();
        input.close();
        imcConnection.close();
    }
}
