package com.example;

import java.util.TimerTask;

/**
 *
 * @author Dominik
 */
public class SecondThread extends TimerTask {

    @Override
    public void run() {
        System.out.println("Listening...");
    }
}
