package edu.scu.databaseexample;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by teubert on 2/27/18.
 */
public class GPSPath {
    private final static String DEBUG_TAG = "GPSPath";
    private static final String TOP_LEVEL_KEY = "unfiltered_gps_data";
    private static final String ARCHIVE_KEY = "archived_gps_data";
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    static GPSPath getPath(String id) {
        return new GPSPath();
    }

    static List<GPSPoint> getSubPath(String id, long start, long end) {
        GPSPath totalPath = getPath(id);
        return totalPath.getSubPath(start, end);
    }

    List<GPSPoint> getSubPath(long start, long end) {
        // TODO(CT): Implement
        return null;
    }

    /**
     * Archive GPS points in range. This is called once they have been added to a trip
     *
     * @param start     Start time (unix time)
     * @param end       End time (unix time)
     */
    public static void archiveGPSPoints(String id, long start, long end) {
        Log.v(DEBUG_TAG, "archiveGPSPoints: Called");
        DatabaseReference myRef = database.getReference(TOP_LEVEL_KEY)
                .child(id);

        // Getting points in range
        Log.i(DEBUG_TAG, "archiveGPSPoints: archiving points in range " + Long.toString(start) + "-" + Long.toString(end));
//        List<Transportation.GPS> points = getGPSPoints(start, end);
//
//        // Add to archive
//        Log.v(DEBUG_TAG, "archiveGPSPoints: adding to archive");
//        myRef = myRef.child(ARCHIVE_KEY);
//        for (Transportation.GPS point : points) {
//            myRef.child("lat").setValue(point.lat);
//            myRef.child("lon").setValue(point.lon);
//        }

        // Removing
        Log.v(DEBUG_TAG, "archiveGPSPoints: removing old points");
        // TODO(CT): Implement Removal

        Log.v(DEBUG_TAG, "archiveGPSPoints: finished");
    }

    /**
     * add a new GPS datapoint for a userProfile
     *
     * @param point     GPS point
     */
    public static void addNewGPSDataPoint(String id, GPSPoint point) {
        Log.v(DEBUG_TAG, "addNewGPSDataPoint: Called");
        DatabaseReference myRef = database.getReference(TOP_LEVEL_KEY)
                .child(id)
                .child(Long.toString(point.timestamp));
        myRef.child("lat").setValue(point.lat);
        myRef.child("lon").setValue(point.lon);
        Log.v(DEBUG_TAG, "addNewGPSDataPoint: Finished");
    }

}
