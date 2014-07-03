/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. */
package systemcontroller;

import java.io.IOException;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.midlet.MIDlet;
import mooc.data.Messages;
import static mooc.data.Messages.INFO;
import persistservice.PersistService;
import rmspersistdata.RMSPersistService;

/**
 * The SystemControlMIDlet is a receiver and processor of events. This
 * application starts threads that listen for asynchronous events from the door
 * sensor, GPS and temperature sensors.
 *
 * @author Tom McGinn
 */
public class SystemControlMIDlet extends MIDlet implements Messages {

    // Each sensor type has its own service to handle recording events
    // to the choosen persistent store
    private RecordDataService switchService, gpsService, tempService;
    private final String switchIMC = "mooc.switch:1.0;";
    private final String GPSIMC = "mooc.gps:1.0;";
    private final String TempIMC = "mooc.temp:1.0;";
    private PersistService persistService;
    private String rmsStorename = "event-data";

    @Override
    public void startApp() {
        System.out.println("SystemControlMIDlet: starting");

        // If the property RMSStorename is defined, use that for the
        // RMS store name
        String rmsStorenameProp = getAppProperty("RMSStorename");
        if (rmsStorenameProp != null) {
            rmsStorename = rmsStorenameProp;
        }

        // Start up the services
        try {
            // Open the persistence service
            persistService = new RMSPersistService(rmsStorename);
            persistService.setMessageLevel(INFO);

            /*
            * Launch threads to handle the communication requests from sensors
            */
            
            // GPIO Switch events
            switchService = new RecordDataService(persistService, switchIMC);
            switchService.start();

            // GPS Events
            gpsService = new RecordDataService(persistService, GPSIMC);
            gpsService.start();

            // Temp Events
            tempService = new RecordDataService(persistService, TempIMC);
            tempService.start();

            System.out.println("SystemControlMIDlet: waiting for connections");

        } catch (ConnectionNotFoundException ex) {
            System.out.println("SystemController: Missing IMC connection: " + ex);
            System.out.println("Is a service loaded/started?");
        } catch (IOException ex) {
            System.out.println("SystemController: ERROR starting service: " + ex);
            //ex.printStackTrace();
        }
    }

    @Override
    public void pauseApp() {
    }

    @Override
    public void destroyApp(boolean unconditional) {
        if (persistService != null) {
            try {
            persistService.close();
            } catch (Exception ex) {
                System.out.println("Unable to close persistService: " + ex);
            }
        }
        if (switchService != null) {
            switchService.stopService();
        }
        if (gpsService != null) {
            gpsService.stopService();
        }
        if (tempService != null) {
            tempService.stopService();
        }
    }
}
