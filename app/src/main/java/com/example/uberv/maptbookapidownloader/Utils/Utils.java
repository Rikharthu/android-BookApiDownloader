package com.example.uberv.maptbookapidownloader.Utils;

public abstract class Utils {

    public static void delay(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
