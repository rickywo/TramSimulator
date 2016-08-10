package com.company;

/**
 * Created by blahblah Team on 2016/8/10.
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TrackingServiceInterface extends Remote {

    void updateTramLocation(int tramID, int routeID, int stop_number) throws RemoteException;

    int retrieveNextStop(int route_id,int current_stop_number,int previous_stop_number) throws RemoteException;

    int connect(int route_id) throws RemoteException;

}