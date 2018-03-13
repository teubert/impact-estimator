package com.coen.scu.final_project.java;

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class GPSPoint {
    private final static String DEBUG_TAG = "GPSPoint";

    public long timestamp = -1; // Linux time (timestamp.getTime())
    public double lon; // Longitude (deg)
    public double lat; // Latitude (deg)
    public double speed; // km/s

    public GPSPoint() {
        // Default constructor required for calls to DataSnapshot.getValue(GPS.class)
    }

    public GPSPoint(long timestamp, double lon, double lat) {
        Log.v(DEBUG_TAG, "Creating GPS Object");
        this.timestamp = timestamp;
        this.lon = lon;
        this.lat = lat;
    }
}
