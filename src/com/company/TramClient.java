package com.company;

import java.rmi.RemoteException;
import java.util.Random;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by blahblah Team on 2016/8/9.
 */
class TramClient implements Runnable {

    static final int NO_PREVIOUS_STOP = 0;
    static final int NO_CURRENT_STOP = 0;

    private Thread t;
    private TrackingServiceInterface ts;
    Random rand = new Random();


    private int id;
    private int route_id;
    private int previous_stop;
    private int current_stop;

    TramClient(TrackingServiceInterface ts, int route_id){
        this.ts = ts;
        this.route_id = route_id;
        this.previous_stop = NO_PREVIOUS_STOP;
        this.current_stop = NO_CURRENT_STOP;

        try {
            this.id = ts.connect(route_id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Creating " +  id );
    }

    public void run() {
        System.out.println("Running " +  id );
        while(id != 0) {
            try {
                int next_stop = 0;
                try {
                    next_stop = ts.retrieveNextStop(route_id, current_stop, previous_stop);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                previous_stop = current_stop;
                current_stop = next_stop;
                try {
                    ts.updateTramLocation(id, route_id, current_stop);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                int n = rand.nextInt(5) + 1;
                Thread.sleep(1000 * n);
            } catch (InterruptedException e) {
                System.out.println("Thread " + id + " interrupted.");
            }
        }
    }

    public void start ()
    {
        System.out.println("Starting " +  id );
        if (t == null)
        {
            t = new Thread (this);
            t.start ();
        }
    }

    public void Alert(String message) throws RemoteException {
        System.out.println(message);
    }

    public static void main(String[] args) {



        try {

            Registry registry = LocateRegistry.getRegistry("localhost",1099);
            TrackingServiceInterface stub = (TrackingServiceInterface) registry.lookup("TrackingServiceInterface");
            TramClient R1 = new TramClient(stub, 1);
            R1.start();

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

}