package com.company;


public class Main {

    public static void main(String[] args) {
	// write your code here
        TrackingService ts = new TrackingService();

        TramClient R1 = new TramClient(ts, 1);
        R1.start();

        TramClient R2 = new TramClient(ts, 1);
        R2.start();
        TramClient R3 = new TramClient(ts, 1);
        R3.start();

        //TramClient R2 = new TramClient(ts, 96);
        //R2.start();
    }
}
