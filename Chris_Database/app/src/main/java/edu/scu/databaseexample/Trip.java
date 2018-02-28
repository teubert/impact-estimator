package edu.scu.databaseexample;

/**
 * Created by teubert on 2/27/18.
 */

import android.util.Log;

/**
 * Class to contain information for a single trip
 */
public class Trip {
    private final static String DEBUG_TAG = "Trip";

    public GPSPoint start;
    public GPSPoint end;
    public long day;
    Transportation.TransportMode transport_mode;
    Transportation.CarType car_type;

    public Trip() {
        // Default constructor required for calls to DataSnapshot.getValue(Trip.class)
    }

    public Trip(GPSPoint start, GPSPoint end,
                Transportation.TransportMode transport_mode,
                Transportation.CarType car_type) {
        Log.v(DEBUG_TAG, "Creating Trip Object");
        this.start = start;
        this.end = end;
        this.transport_mode = transport_mode;
        this.car_type = car_type;
        // TODO(CT): get day from range
    }

}