package edu.scu.databaseexample;

/**
 * Created by teubert on 2/27/18.
 */

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Class to contain information for a single trip
 */
@IgnoreExtraProperties
public class Trip {
    private final static String DEBUG_TAG = "Trip";

    public GPSPoint start;
    public GPSPoint end;
    String tripId = null;
    Transportation.TransportMode transport_mode;
    Transportation.CarType car_type;

    public Trip() {

    }

    public Trip(GPSPoint start, GPSPoint end,
                Transportation.TransportMode transport_mode,
                Transportation.CarType car_type) {
        Log.v(DEBUG_TAG, "Creating Trip Object");
        this.start = start;
        this.end = end;
        this.transport_mode = transport_mode;
        this.car_type = car_type;
    }

    public Trip(GPSPoint start, GPSPoint end,
                Transportation.TransportMode transport_mode,
                Transportation.CarType car_type, String tripId) {
        Log.v(DEBUG_TAG, "Creating Trip Object");
        this.start = start;
        this.end = end;
        this.transport_mode = transport_mode;
        this.car_type = car_type;
        this.tripId = tripId;
    }
}