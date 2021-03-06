package com.coen.scu.final_project;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.coen.scu.final_project.java.DayTripsSummary;
import com.coen.scu.final_project.java.GPSPoint;
import com.coen.scu.final_project.java.Transportation;
import com.coen.scu.final_project.java.Trip;
import com.coen.scu.final_project.java.UserProfile;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class TripRecognitionService extends Service {
    private static final String DEBUG_TAG = "TripRecognitionService";

    // Configurables
    private static final int LOCATION_INTERVAL = 10000; // in milliseconds
    private static final float LOCATION_DISTANCE = 20f; // in meters
    private static final int WINDOW_SIZE = 5; // 20
    // Note: To work well, intertia should be significantly lower than window_size
    private static final int INERTIA = 2;
    public static final double MIN_WALK_SPEED_MPS = 0.5; // minimum walk speed in m/s
    public static final int MIN_BIKE_SPEED_MPS = 3;
    public static final int MIN_AUTO_SPEED_MPS = 9;
    public static final int MIN_TRAIN_SPEED_MPS = 35;
    public static final double MIN_AIRCRAFT_SPEED_MPS = 166.66; // 166.66 is fastest bullet train


    // Variables 
    private int mStartMode = START_STICKY;       // indicates how to behave if the service is killed
    private IBinder mBinder;      // interface for clients that bind
    private boolean mAllowRebind; // indicates whether onRebind should be used

    private LocationManager mLocationManager = null;
    private LinkedList<Location> locationQueue = new LinkedList<>();
    private Transportation.TransportMode currentMode;

    private int inertiaCounter = 0;
    private Location startLocation = null;
    private Location lastDeletedLocation = null;
    private double distance = 0; // km
    private UserProfile mUser;

    /**
     * Process the queue- the main trip inferring algorithm
     */
    void processQueue() {
        // Calculate average speed
        double averageSpeed = averageSpeed();

        // Check minimum requirements for continuing
        if (locationQueue.size() < WINDOW_SIZE && !aircraftTest(averageSpeed)) {
            // Skip if window not full & not travelling by aircraft
            // This is to handle situation where phone is turned off/not connected for flight, and
            // turned back on many miles from previous location
            Log.d(DEBUG_TAG, "Minimum threshold for trip not yet met, skipping");
            return;
        }

        // Infer mode
        Transportation.TransportMode mode = getModeFromSpeed(averageSpeed);

        if (mode == null) {
            Log.d(DEBUG_TAG, String.format("getModeFromSpeed returned null mode (speed %f)", averageSpeed));
            return;
        }

        // Trip binning logic
        if (currentMode == null) {
            // Start a new trip
            Log.d(DEBUG_TAG, "New trip begun of type " + mode.name());
            currentMode = mode;
            startLocation = locationQueue.peek();
        } else if (mode == currentMode) {
            // Continuation of current trip
            Log.d(DEBUG_TAG, "New point for current trip (" + currentMode.name() + ")");
            inertiaCounter = 0;
            handleAddedPoint();
        } else {
            // Different mode from current trip- could be new trip or momentary change
            //      (e.g., stop at stoplight)
            if (inertiaCounter < INERTIA && !aircraftTest(averageSpeed)) {
                // Immediately end if on aircraft
                // Take as part of current trip if within inertia
                Log.d(DEBUG_TAG, String.format("New point out of current trip (%s) mode, but in previous trip intertia (New Trip: %s)", currentMode.name(), mode.name()));
                inertiaCounter++;
                handleAddedPoint();
            } else {
                // Outside of inertia- trip has ended
                createTrip();
                Log.d(DEBUG_TAG,
                        String.format("New point out of current trip (%s) mode, outside of intertia. Ending trip (New Trip: %s)", currentMode.name(), mode.name()));
                inertiaCounter = 0;
                resetCounters();
            }
        }
    }

    /**
     * Test to see if active mode of transport is aircraft
     * Used to handle case where phone is turned off during flight
     *
     * @param speed Average speed
     * @return  If aircraft rule applies (if you were likely flying
     */
    boolean aircraftTest(final double speed) {
        return (speed > MIN_AIRCRAFT_SPEED_MPS || currentMode == Transportation.TransportMode.AIRCRAFT);
    }

    /**
     * Reset all the counters (called when trip finishes)
     */
    void resetCounters() {
        currentMode = null;
        startLocation = null;
        inertiaCounter = 0;
        distance = 0;
    }

    /**
     * Create a trip and write to database (called when trip finishes before reset)
     */
    void createTrip() {
        Log.i(DEBUG_TAG, "Trip ended of type " + currentMode.name());
        GPSPoint start = new GPSPoint(startLocation.getTime(),
                startLocation.getLongitude(),
                startLocation.getLatitude());
        while (locationQueue.size() > INERTIA) {
            // Keep one
            handleAddedPoint();
        }
        GPSPoint end = new GPSPoint(locationQueue.peek().getTime(),
                locationQueue.peek().getLongitude(),
                locationQueue.peek().getLatitude());

        Trip newTrip = new Trip(start, end, distance, currentMode, mUser.getCarType());
        Calendar date = Calendar.getInstance();
        Log.i(DEBUG_TAG, "Adding trip to " + DayTripsSummary.getDateString(date));
        DayTripsSummary.append(mUser.getId(), DayTripsSummary.getDateString(date), newTrip);
    }

    /**
     * Handle when a point was added- remove oldest point to move window
     */
    void handleAddedPoint() {
        Location loc = locationQueue.remove();
        if (lastDeletedLocation != null) {
            // Add to distance calculation (km)
            distance += lastDeletedLocation.distanceTo(loc) / 1000f;
            Log.v(DEBUG_TAG,
                    String.format("Removing point and adding to distance estimate (point=%f, total=%f)", lastDeletedLocation.distanceTo(loc) / 1000f, distance));
        }
        lastDeletedLocation = loc;
    }

    /**
     * Infer the current mode of transportation from the speed (uses binning)
     *
     * @param speed Average speed for window
     * @return  Inferred transport mode
     */
    Transportation.TransportMode getModeFromSpeed(double speed) {

        // All in m/s
        if (speed < MIN_WALK_SPEED_MPS) {
            return null;
        } else if (speed < MIN_BIKE_SPEED_MPS) {
            return Transportation.TransportMode.WALK;
        } else if (speed < MIN_AUTO_SPEED_MPS) {
            return Transportation.TransportMode.BIKE;
        } else if (speed < MIN_TRAIN_SPEED_MPS) {
            return Transportation.TransportMode.AUTOMOBILE;
        } else if (speed < MIN_AIRCRAFT_SPEED_MPS) {
            return Transportation.TransportMode.TRAIN;
        } else {
            return Transportation.TransportMode.AIRCRAFT;
        }
    }

    /**
     * Calculate the average speed for the locations in the location queue (current window)
     *
     * @return  Average speed (m/s)
     */
    double averageSpeed() {
        double averageSpeed = 0;
        Location lastForAverageSpeed = null;
        for (Location location : locationQueue) {
            if (lastForAverageSpeed == null) {
                // Convert to m/s
                averageSpeed += location.getSpeed();
            } else {
                // Note- distance is in m and time is in ms, so this comes out to m/s
                double dt = ((location.getTime() - lastForAverageSpeed.getTime())/1000.0);
                double dx = location.distanceTo(lastForAverageSpeed);
                averageSpeed += dx/dt;
            }
            lastForAverageSpeed = location;
        }
        return averageSpeed/locationQueue.size();
    }

    /**
     * Location listener- listens for changes in location
     */
    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation; // Last location for this mode

        // Create location listener by provider
        LocationListener(String provider) {
            Log.i(DEBUG_TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        /**
         * Called when location changes by at least min threshold (LOCATION_DISTANCE)
         *
         * @param location  New location
         */
        @Override
        public void onLocationChanged(Location location) {
            Log.i(DEBUG_TAG, "onLocationChanged: " + location);
            locationQueue.add(location);
            mLastLocation.set(location);
            processQueue();
        }

        /**
         * Called when provider is disabled
         *
         * @param provider  Provider that was disabled
         */
        @Override
        public void onProviderDisabled(String provider) {
            Log.i(DEBUG_TAG, "onProviderDisabled: " + provider);
        }

        /**
         * Called when provider is enabled
         *
         * @param provider  Provider that was enabled
         */
        @Override
        public void onProviderEnabled(String provider)
        {
            Log.i(DEBUG_TAG, "onProviderEnabled: " + provider);
        }

        /**
         * Called when status for provider changes
         *
         * @param provider  Provider where status changed
         * @param status    New status
         * @param extras    Extra information
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.i(DEBUG_TAG, "onStatusChanged: " + provider);
        }
    }

    /**
     * The location listeners (one for each provider)
     */
    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    /**
     * Called on creation of the service, starts the location managers
     */
    @Override
    public void onCreate() {
        // The service is being created
        String activeUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUser = UserProfile.getUserProfileById(activeUser);

        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(DEBUG_TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(DEBUG_TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(DEBUG_TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(DEBUG_TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    /**
     * Called on service start command
     *
     * @param intent    Used intent
     * @param flags     Flags
     * @param startId   Start id
     * @return  Start mode
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        Log.d(DEBUG_TAG, "onStartCommand");

        return mStartMode;
    }

    /**
     * Called on bind of the service
     *
     * @param intent    Binding intent
     * @return  Ibinder
     */
    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        Log.d(DEBUG_TAG, "onBind");
        return mBinder;
    }

    /**
     * Called on unbinding this service
     *
     * @param intent unbinding intent
     * @return  If successful/allowed
     */
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        Log.d(DEBUG_TAG, "onUnbind");
        return mAllowRebind;
    }

    /**
     * Called on rebinding the service
     *
     * @param intent    Rebinding intent
     */
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
        Log.d(DEBUG_TAG, "onRebind");
    }

    /**
     * Called on destroying the service- cleans the location managers
     */
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        Log.d(DEBUG_TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (LocationListener locationListener :  mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(locationListener);
                } catch (Exception ex) {
                    Log.i(DEBUG_TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    /**
     * Initialize the location managers
     */
    private void initializeLocationManager() {
        Log.d(DEBUG_TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
