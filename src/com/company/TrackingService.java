package com.company;


import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import java.util.*;

/**
 * Created by blahblah Team on 2016/8/9.
 */
public class TrackingService implements TrackingServiceInterface {

    private static TrackingService instance = null;
    private static final long serialVersionUID = -464196277362659008L;
    private static final int RANGE_TRAM_ID = 999;
    private static final int NOT_FOUND = 0;
    private Map<Integer, Route> routes = new HashMap();
    private Map<Integer, Integer> trams = new HashMap();

    boolean done = false;
    Random rand = new Random();

    public static TrackingService getInstance() {
        if(instance == null) {
            instance = new TrackingService();
        }
        return instance;
    }

    private TrackingService() {
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

    @Override
    public Message getRequest(Message message) {

        Message reply_msg = new Message();
        RPCMessage rpc_message = message.unMarshal();
        List<String> para = Arrays.asList(rpc_message.getCsv_data().split("\\s*,\\s*"));
        int route_id = Integer.valueOf(para.get(0));
        RPCMessage rpc_rply = new RPCMessage();
        rpc_rply.setMessageType(RPCMessage.MessageType.REPLY);
        rpc_rply.setTransactionId(rpc_message.getTransactionId());
        rpc_rply.setRPCId(rpc_message.getRPCId());
        rpc_rply.setRequestId(rpc_message.getRequestId());
        rpc_rply.setStatus(RPCMessage.SUCCESS);
        switch (rpc_message.getProcedureId()) {
            case RPCMessage.REQUEST_NEXT_STOP:
                int current_stop_number = Integer.valueOf(para.get(1));
                int previous_stop_number = Integer.valueOf(para.get(2));
                int next_stop_number = retrieveNextStop(route_id, current_stop_number, previous_stop_number);
                // Encapsulate RPC
                rpc_rply.setProcedureId(RPCMessage.REPLY_NEXT_STOP);
                rpc_rply.setCsv_data(String.valueOf(next_stop_number));
                rpc_rply.setStatus(RPCMessage.SUCCESS);
                reply_msg.marshal(rpc_rply);
                return reply_msg;
            case RPCMessage.REQUEST_CURRENT_LOC:
                int tram_id = Integer.valueOf(para.get(1));
                int stop_number = Integer.valueOf(para.get(2));
                updateTramLocation(tram_id, route_id, stop_number);
                // Encapsulate RPC
                rpc_rply.setProcedureId(RPCMessage.REPLY_CURRENT_LOC);
                rpc_rply.setCsv_data("");
                reply_msg.marshal(rpc_rply);
                return reply_msg;
            default:
                break;

        }
        return reply_msg;
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

    public int connect(int route_id, int tram_id) {
        // get a unique id for a new tram
        boolean found = true;
        int id = tram_id;

        // If this tram id is taken
        if(trams.containsKey(id)) {
            return NOT_FOUND;
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

