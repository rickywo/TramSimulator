package com.company;


import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.*;

/**
 * Created by blahblah Team on 2016/8/9.
 */
public class TrackingService implements TrackingServiceInterface {

    private static final long serialVersionUID = -464196277362659008L;
    private static final int RANGE_TRAM_ID = 999;
    private static final int NOT_FOUND = 0;
    private Map<Integer, Route> routes = new HashMap();
    private Map<Integer, Integer> trams = new HashMap();

    boolean done = false;
    Random rand = new Random();

    TrackingService() {
        // initialize routes
        initRoutes();
    }


    public void updateTramLocation(int tramID, int routeID, int stop_number) {
        System.out.println("Tram: "+ tramID + " Route: " + routeID + " Stop: " + stop_number);

    }

    public int retrieveNextStop(int route_id,int current_stop_number,int previous_stop_number) {
        Route r = routes.get(route_id);
        return r.getNextStop(current_stop_number, previous_stop_number);
    }

    public int connect(int route_id) {
        // get a unique id for a new tram
        boolean found = true;
        int id = 0;
        while(found) {
            id = rand.nextInt(RANGE_TRAM_ID) + 1; // Tram id range is from 1-999
            found = trams.containsKey(id);
        }

        // if can't find this route
        Route r = routes.get(route_id);
        if(r == null) {
            return NOT_FOUND;
        }

        // if route is full
        if(!r.addTram()) {
            return  NOT_FOUND;
        }

        trams.put(id, id);
        return id;
    }

    void initRoutes() {
//        1=1,2,3,4,5
//        96=23,24,2,34,22
//        101=123,11,22,34,5,4,7
//        109=88,87,85,80,9,7,2,1
//        112=110,123,11,22,34,33,29,4

        int[] route_ids = {1, 96, 101, 109, 112};
        int[][] route_stops = {
                {1,2,3,4,5},
                {23,24,2,34,22},
                {123,11,22,34,5,4,7},
                {88,87,85,80,9,7,2,1},
                {110,123,11,22,34,33,29,4}
        };

        for(int i=0; i< route_ids.length; i++) {
            int route_id = route_ids[i];
            int[] stops = route_stops[i];
            Route r = new Route(route_id);
            r.setStops(stops);
            routes.put(route_id, r);
        }
    }

    public static void main(String args[]) {

        TrackingService obj = new TrackingService();
        try {

            TrackingServiceInterface stub = (TrackingServiceInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry  = LocateRegistry.createRegistry(1099);
            registry.bind("TrackingServiceInterface", stub);


            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

