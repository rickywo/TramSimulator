package com.company;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {


    private static void demo1(TrackingServiceInterface stub) {
        // Exceed route capacity

        for(int i = 0; i < 6; i++) {
            new TramClient(stub, 1).start();
        }

    }

    private static void demo2(TrackingServiceInterface stub) {
        // DUPLICATE TRAM ID
        new TramClient(stub, 1, 999).start();
        new TramClient(stub, 1, 999).start();

    }

    private static void demo3(TrackingServiceInterface stub) {
        // CANNOT FIND ROUTE ID
        new TramClient(stub, 2).start();

    }

    private static void demo4(TrackingServiceInterface stub) {
        // Normal Demo
        // route_ids = {1, 96, 101, 109, 112}
        for(int i =0 ; i< 5; i++) {
            new TramClient(stub, 1).start();
            new TramClient(stub, 96).start();
            new TramClient(stub, 101).start();
            new TramClient(stub, 109).start();
            new TramClient(stub, 112).start();
        }
    }

    private static void manual(TrackingServiceInterface stub) {
        Scanner reader = new Scanner(System.in);


        List<String> arguments;
        while(true) {
            System.out.print("Please input Tram id and Route id as following format \"1, 234\":");
            String input = reader.nextLine();
            arguments = Arrays.asList(input.split("\\s*,\\s*"));
            new TramClient(stub, Integer.valueOf(arguments.get(0)), Integer.valueOf(arguments.get(1))).start();
        }
    }

    public static void main(String[] args) {

        final String MENU =
                "[0]. Manual Operation \n[1]. Demo1: Exceed maximum route capacity\n[2]. Demo2: Duplicate Tram id\n[3]. Demo3: Cannot find route id\n[4]. Demo4: Normal demo";

        Registry registry;
        TrackingServiceInterface stub = null;
        Scanner reader = new Scanner(System.in);

        TrackingService obj = TrackingService.getInstance();

        // Initialising and register a stub for server
        try {

            stub = (TrackingServiceInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            registry  = LocateRegistry.createRegistry(1099);
            registry.bind("TrackingServiceInterface", stub);


            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            //e.printStackTrace();
        }

        // Initial stub for clients
        try {

            registry = LocateRegistry.getRegistry("127.0.0.1",1099);
            stub = (TrackingServiceInterface) registry.lookup("TrackingServiceInterface");


        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        System.out.println(MENU);

        int option = reader.nextInt();
        switch(option){
            case 0:
                manual(stub);
                break;
            case 1:
                demo1(stub);
                break;
            case 2:
                demo2(stub);
                break;
            case 3:
                demo3(stub);
                break;
            case 4:
                demo4(stub);
                break;
            default:
                break;
        }





        //TramClient R2 = new TramClient(ts, 96);
        //R2.start();
    }
}
