package com.company;

/**
 * Created by blahblah Team on 2016/8/9.
 */
public class Route {
    static final int MAX_TRAM_CAPACITY = 2;
    static final int NOT_FOUND = 0;
    static final int NO_PREVIOUS_STOP = 0;
    static final int NO_CURRENT_STOP = 0;
    int vacant;
    int id;
    int route_length;
    int[] stops;

    Route(int route_id) {
        id = route_id;
        vacant = 0;
    }

    void setStops(int[] stop_array) {
        int index = 2;
        int doubly_length = stop_array.length * 2;
        route_length = doubly_length - 2;

        stops = new int[route_length];

        for(int i = 0; i< route_length; i++) {

            if(i < stop_array.length) {
                stops[i] = stop_array[i];
            } else {
                stops[i] = stop_array[stop_array.length - index];
                index ++;
            }
        }
    }

    boolean addTram() {
        boolean result = false;
        if(vacant < MAX_TRAM_CAPACITY) {
            vacant++;
            result = true;
        }
        return result;
    }

    boolean removeTram() {
        boolean result = false;
        if(vacant > 0) {
            vacant --;
            result = true;
        }
        return result;
    }

    int getNextStop(int current_stop, int previous_stop) {
        // If it is begin of a route of a tram
        if(current_stop == NO_CURRENT_STOP) {
            return stops[0];
        }

        if (previous_stop == NO_PREVIOUS_STOP) {
            return stops[1];
        }

        for(int i = 0; i< route_length; i++) {
            int curr = i + 1;

            if(stops[i] == previous_stop && stops[curr % stops.length] == current_stop) {
                // go back to first stop
                int next = (curr + 1) % stops.length;
                return stops[next];

            }
        }
        return NOT_FOUND;
    }

}
