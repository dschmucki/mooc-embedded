package com.example;

import java.util.Date;
import java.util.Timer;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author Dominik
 */
public class ThreadIMlet extends MIDlet {

    private TimeTask task;
    
    @Override
    public void startApp() {
        System.out.printf("%s %tT\n", "Starting time:", new Date());
        
        task = new TimeTask();
        
        Timer timer = new Timer();
        timer.schedule(task, task.init(), 5*60*1000);
    }
    
    @Override
    public void destroyApp(boolean unconditional) {
        task.cancel();
    }
}
