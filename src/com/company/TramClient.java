package com.company;

import java.rmi.RemoteException;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by blahblah Team on 2016/8/9.
 */
class TramClient implements Runnable {

    static final int ZERO = 0;
    static final int NO_PREVIOUS_STOP = 0;
    static final int NO_CURRENT_STOP = 0;
    static final int NO_NEXT_STOP = -1;

    private Thread t;
    private TrackingServiceInterface ts;
    Random rand = new Random();


    private int id;
    private int route_id;
    private int previous_stop;
    private int current_stop;
    private int request_id; // request counter of this client

    private static final Set<Long> usedIds = new HashSet<Long>();

    TramClient(TrackingServiceInterface ts, int route_id){
        this.ts = ts;
        this.route_id = route_id;
        this.request_id = ZERO;
        this.previous_stop = NO_PREVIOUS_STOP;
        this.current_stop = NO_CURRENT_STOP;

        try {
            this.id = ts.connect(route_id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Creating " +  id );
    }

    TramClient(TrackingServiceInterface ts, int route_id, int tram_id){
        this.ts = ts;
        this.route_id = route_id;
        this.request_id = ZERO;
        this.previous_stop = NO_PREVIOUS_STOP;
        this.current_stop = NO_CURRENT_STOP;

        try {
            this.id = ts.connect(route_id, tram_id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Creating " +  id );
    }

    public void run() {
        while(id != 0) {
            try {
                retrieveNextStop();
                updateTramLocation();
                int n = rand.nextInt(5) + 1;
                Thread.sleep(1000 * n);
            } catch (InterruptedException e) {
                System.out.println("Thread " + id + " interrupted.");
            }
        }
        if(id == Route.MAX_CAPACITY_EXCEEDED) {
            System.out.println("Exceed the maximum capacity of route [" + route_id + "].");
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

    private void retrieveNextStop() {

        // Encapsulate message obj
        RPCMessage rpc_m = new RPCMessage();
        rpc_m.setMessageType(RPCMessage.MessageType.REQUEST);
        rpc_m.setTransactionId(getUniqueId());
        rpc_m.setRPCId(getUniqueId());
        rpc_m.setRequestId(request_id);
        rpc_m.setProcedureId(RPCMessage.REQUEST_NEXT_STOP);
        rpc_m.setCsv_data(route_id + ","+current_stop+ ","+previous_stop);
        rpc_m.setStatus(RPCMessage.SUCCESS);


        Message tx_m = new Message();
        tx_m.marshal(rpc_m);

        Message rx_m;
        RPCMessage rx_rm = new RPCMessage();
        try {
            rx_m = ts.getRequest(tx_m);
            request_id ++;
            rx_rm = rx_m.unMarshal();
        } catch (RemoteException e) {
            System.out.println("Tram id: "+ id + " retrieveNextStop: Connection refused.");
            //e.printStackTrace();
        }

        processReply(rpc_m, rx_rm);

    }

    private void processReply(RPCMessage o_m, RPCMessage r_m) {
        if(r_m.getMessageType() != RPCMessage.MessageType.REPLY) {
            System.out.println("Message Type Error." + r_m.getMessageType());
            return;
        }
        if(o_m.getTransactionId()!=r_m.getTransactionId()) {
            System.out.println("Transaction ID does not match.");
            return;
        }
        if(o_m.getRPCId()!=r_m.getRPCId()) {
            System.out.println("RPC ID does not match.");
            return;
        }
        if(o_m.getRequestId()!=r_m.getRequestId()) {
            System.out.println("Request ID does not match.");
            return;
        }
        if(r_m.getStatus()!=RPCMessage.SUCCESS) {
            System.out.println("Request failed.");
            return;
        }
        switch(r_m.getProcedureId()) {
            case RPCMessage.REPLY_NEXT_STOP:
                List<String> para = Arrays.asList(r_m.getCsv_data().split("\\s*,\\s*"));
                int next_stop = Integer.valueOf(para.get(RPCMessage.NEXT_STOP_INDEX));
                previous_stop = current_stop;
                current_stop = next_stop;
                break;
            case RPCMessage.REPLY_CURRENT_LOC:
                break;
            default:
                break;
        }
    }

    private void updateTramLocation() {
        // Encapsulate message obj
        RPCMessage rpc_m = new RPCMessage();
        rpc_m.setMessageType(RPCMessage.MessageType.REQUEST);
        rpc_m.setTransactionId(getUniqueId());
        rpc_m.setRPCId(getUniqueId());
        rpc_m.setRequestId(request_id);
        rpc_m.setProcedureId(RPCMessage.REQUEST_CURRENT_LOC);
        rpc_m.setCsv_data(route_id + ","+id+ ","+current_stop);
        rpc_m.setStatus(RPCMessage.SUCCESS);

        Message tx_m = new Message();
        tx_m.marshal(rpc_m);

        Message rx_m;
        RPCMessage rx_rm = new RPCMessage();
        try {
            rx_m = ts.getRequest(tx_m);
            request_id ++;
            rx_rm = rx_m.unMarshal();
        } catch (RemoteException e) {
            System.out.println("Tram id: "+ id + " UpdateTramLocation: Connection refused.");
            //e.printStackTrace();
        }

        processReply(rpc_m, rx_rm);
    }

    private static synchronized long getUniqueId() {
        long millis;
        do {
            millis = System.currentTimeMillis();
        } while (!usedIds.add(millis));
        return millis;
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