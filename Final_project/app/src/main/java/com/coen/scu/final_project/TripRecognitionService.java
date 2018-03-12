package com.coen.scu.final_project;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.coen.scu.final_project.java.GPSPoint;
import com.coen.scu.final_project.java.Transportation;
import com.coen.scu.final_project.java.Trip;

import java.util.Queue;

public class TripRecognitionService extends Service {
    static final String DEBUG_TAG = "TripRecognitionService";
    int mStartMode;       // indicates how to behave if the service is killed
    IBinder mBinder;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    Queue<Location> locationQueue;
    Transportation.TransportMode currentMode;
    static final int WINDOW_SIZE = 20;
    static final int INERTIA = 3;
    int inertiaCounter = 0;
    Location startLocation = null;
    Location mGlobalLastLocation = null;

    void processQueue() {
        if (locationQueue.size() < WINDOW_SIZE) {
            return;
        }
        double averageSpeed = averageSpeed();
        Transportation.TransportMode mode = getModeFromSpeed(averageSpeed);

        if (currentMode == null) {
            mode = currentMode;
            startLocation = locationQueue.peek();
        } else if (mode == currentMode) {
            handleAddedPoint();
        } else {
            if (inertiaCounter < INERTIA) {
                inertiaCounter++;
                handleAddedPoint();
            } else {
                createTrip();
                resetCounters();
            }
        }
    }

    void resetCounters() {
        locationQueue.clear();
        currentMode = null;
        startLocation = null;
        inertiaCounter = 0;
    }

    void createTrip() {
        GPSPoint start = new GPSPoint(startLocation.getTime(),
                startLocation.getLongitude(),
                startLocation.getLatitude());

//        GPSPoint end = mGlobalLastLocation;
        Trip newTrip = new Trip();
    }

    void handleAddedPoint() {
        locationQueue.remove();
    }

    Transportation.TransportMode getModeFromSpeed(double speed) {
        // TODO(CT): Change to km

        if (speed < 2) {
            return null;
        } else if (speed < 10) {
            return Transportation.TransportMode.WALK;
        } else if (speed < 20) {
            return Transportation.TransportMode.BIKE;
        } else if (speed < 80) {
            return Transportation.TransportMode.AUTOMOBILE;
        } else if (speed < 200) {
            return Transportation.TransportMode.TRAIN;
        } else {
            return Transportation.TransportMode.AIRCRAFT;
        }
    }

    double averageSpeed() {
        double averageSpeed = 0;
        // TODO(CT): CACHE
        for (Location location : locationQueue) {
            averageSpeed += location.getSpeed();
        }
        // TODO(CT): CHANGE TO DISTANCE-BASED
        return averageSpeed/locationQueue.size();
    }

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(DEBUG_TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(DEBUG_TAG, "onLocationChanged: " + location);
            locationQueue.add(location);
            mLastLocation.set(location);
            mGlobalLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(DEBUG_TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(DEBUG_TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(DEBUG_TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };


    @Override
    public void onCreate() {
        // The service is being created
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
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()

        return mStartMode;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        Log.e(DEBUG_TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(DEBUG_TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(DEBUG_TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
