package com.example;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Dominik
 */
public class TimeTask extends TimerTask {

    private SecondThread secondThread;
    
    // Return the number of milliseconds until the 5 after event
    public long init() {
        Date now = new Date();
        int mins = Integer.parseInt(now.toString().substring(14, 16));
        int dif = 4 - (mins % 5);
        int secs = Integer.parseInt(now.toString().substring(17, 19));
        return (dif * 60000 + ((60 - secs) * 1000));
    }
    
    @Override
    public void run() {
        Date now = new Date();
        int mins = Integer.parseInt(now.toString().substring(14, 16));
        if (mins % 10 == 5) {
            Timer timer = new Timer();
            secondThread = new SecondThread();
            timer.schedule(secondThread, 0, 30*1000);
        } else {
            if (secondThread != null) {
                secondThread.cancel();
            } 
        }
        System.out.printf("%tT\n", now);       
    }
    
    @Override
    public boolean cancel() {
        if (secondThread != null) {
            secondThread.cancel();
        }
        return true;
    }
}
