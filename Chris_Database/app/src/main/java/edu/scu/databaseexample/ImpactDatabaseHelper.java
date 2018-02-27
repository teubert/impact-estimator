package edu.scu.databaseexample;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;

// TODO(CT): Add error handling
// TODO(CT): Remove GPS-> Archive GPS?
// TODO(CT): How are we going to handle email updates if email == id - do we want to support that
// TODO(CT): Get timestamp
// TODO(CT): How do we handle days

/**
 * Created by teubert on 2/21/18.
 */
public class ImpactDatabaseHelper {
    private static final String DEBUG_TAG = "DatabaseHelper";

    private FirebaseDatabase database;
    String id = null;

    private class UserMonitor implements ValueEventListener {
        public User user = new User();

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            user.name = dataSnapshot.child("name").getValue(String.class);
            user.email = dataSnapshot.child("email").getValue(String.class);
            user.car_type = dataSnapshot.child("car_type").getValue(Transportation.CarType.class);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            System.out.println("The read failed: " + databaseError.getCode());
        }
    }
    UserMonitor userMonitor = new UserMonitor();

    private class EstimateMonitor implements ValueEventListener {
        public Estimate estimate = new Estimate();
        public Estimate todaysEstimate = new Estimate();

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            estimate.CO2 = dataSnapshot.child("CO2").getValue(double.class);
            estimate.nDays = dataSnapshot.child("nDays").getValue(long.class);
            // TODO(CT): Get today
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            System.out.println("The read failed: " + databaseError.getCode());
        }
    }
    EstimateMonitor estimateMonitor = new EstimateMonitor();

    public enum TopLevelDirectories {
        USERS       ("users"),
        GPS         ("unfiltered_gps_data"),
        GPS_ARCHIVE ("gps_archive"),
        TRIPS       ("trips"),
        ESTIMATES   ("estimates");
        private final String name;

        TopLevelDirectories(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    /**
     * get the current timestamp in "unix time"
     *
     * @return Current timestamp in unix time
     */
    static private long getCurrentTimestamp() {
        Log.v(DEBUG_TAG, "Getting current timestamp");

        // 1) create a java calendar instance
        Calendar calendar = Calendar.getInstance();

        // 2) get a java.util.Date from the calendar instance.
        //    this date will represent the current instant, or "now".
        java.util.Date now = calendar.getTime();

        // 3) a java current time (now) instance
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

        return currentTimestamp.getTime();
    }

    /**
     * Constructor for Database Helper - no user
     */
    public ImpactDatabaseHelper() {
        Log.v(DEBUG_TAG, "Configuring ImpactDataBaseHelper");

        // Set Database
        database = FirebaseDatabase.getInstance();
    }

    /**
     * Constructor for Database Helper for specific user
     *
     * @param id    User Id
     */
    public ImpactDatabaseHelper(String id) {
        Log.v(DEBUG_TAG, "Configuring ImpactDataBaseHelper");

        // Set Database
        database = FirebaseDatabase.getInstance();

        setUser(id);
    }

    /**
     * Set User
     *
     * @param id USer Id
     */
    public void setUser(String id) {
        // TODO(CT): Update ID to use auth
        this.id = id;
        DatabaseReference myRef = database.getReference(TopLevelDirectories.USERS.getName())
                .child(id);
        myRef.addValueEventListener(userMonitor);

        myRef = database.getReference(TopLevelDirectories.ESTIMATES.getName())
                .child(id);
        myRef.addValueEventListener(estimateMonitor);
    }

    /**
     * Add a new user to the database
     *
     * @param email     Email address
     * @param name      Name
     * @param car_type  Car type
     */
    public void addNewUser(String email, String name, Transportation.CarType car_type) {
        Log.v(DEBUG_TAG, "addNewUser: Called");
        long currentTime = getCurrentTimestamp();
        DatabaseReference myRef = database.getReference(TopLevelDirectories.USERS.getName());
        User user = new User(email, name, car_type);
        String id = User.emailToUsername(user.email);

        Log.i(DEBUG_TAG, "Registering new user with id=" + id);
        //myRef.child(id).setValue(user);
        myRef = myRef.child(id);
        // TODO(CT): Check if user exists
        myRef.child("email").setValue(email);
        myRef.child("name").setValue(name);
        myRef.child("car_type").setValue(car_type);

        myRef.child("user_since").setValue(currentTime);
        myRef.child("last_login").setValue(currentTime);

        setUser(id);
        Log.v(DEBUG_TAG, "addNewUser: Finished");
    }

    /**
     * Update an existing user in the database
     *
     * @param name
     * @param car_type
     */
    public void updateUser(String name, Transportation.CarType car_type) {
        Log.v(DEBUG_TAG, "updateUser: Called");
        DatabaseReference myRef = database.getReference(TopLevelDirectories.USERS.getName())
                .child(id);
        Log.i(DEBUG_TAG, "Updating user info for user with id=" + id);
        if (name != null) {
            myRef.child("name").setValue(name);
        }
        if (car_type != null) {
            myRef.child("car_type").setValue(car_type);
        }
        Log.v(DEBUG_TAG, "updateUser: Finished");
    }

    /**
     * Get data for a specific user
     *
     * @return      User object for user with id
     */
    public User getUser() {
        Log.v(DEBUG_TAG, "getUser: Called");
        return userMonitor.user;
    }

    /**
     * add a new GPS datapoint for a user
     *
     * @param point     GPS point
     */
    public void addNewGPSDataPoint(Transportation.GPS point) {
        Log.v(DEBUG_TAG, "addNewGPSDataPoint: Called");
        DatabaseReference myRef = database.getReference(TopLevelDirectories.GPS.getName())
                .child(id)
                .child(Long.toString(point.timestamp));
        myRef.child("lat").setValue(point.lat);
        myRef.child("lon").setValue(point.lon);
        Log.v(DEBUG_TAG, "addNewGPSDataPoint: Finished");
    }

    /**
     * Update the last login time
     */
    public void updateLastLogin() {
        Log.v(DEBUG_TAG, "updateLastLogin: Called");
        DatabaseReference myRef = database.getReference(TopLevelDirectories.USERS.getName())
                .child(id);
        myRef.child("last_login").setValue(getCurrentTimestamp());
        Log.v(DEBUG_TAG, "updateLastLogin: Finished");
    }

    /**
     * Archive GPS points in range. This is called once they have been added to a trip
     *
     * @param start     Start time (unix time)
     * @param end       End time (unix time)
     */
    public void archiveGPSPoints(long start, long end) {
        Log.v(DEBUG_TAG, "archiveGPSPoints: Called");
        DatabaseReference myRef = database.getReference(TopLevelDirectories.GPS.getName())
                .child(id);

        // Getting points in range
        Log.i(DEBUG_TAG, "archiveGPSPoints: archiving points in range " + Long.toString(start) + "-" + Long.toString(end));
        List<Transportation.GPS> points = getGPSPoints(start, end);

        // Add to archive
        Log.v(DEBUG_TAG, "archiveGPSPoints: adding to archive");
        myRef = myRef.child(TopLevelDirectories.GPS_ARCHIVE.getName());
        for (Transportation.GPS point : points) {
            myRef.child("lat").setValue(point.lat);
            myRef.child("lon").setValue(point.lon);
        }

        // Removing
        Log.v(DEBUG_TAG, "archiveGPSPoints: removing old points");
        // TODO(CT): Implement Removal

        Log.v(DEBUG_TAG, "archiveGPSPoints: finished");
    }

    /**
     * Add a new trip
     *
     * @param trip              Trip to add
     *
     * TODO(CT): Replace String transport_mode with enum
     */
    public void addTrip(Transportation.Trip trip) {
        Log.v(DEBUG_TAG, "addTrip: Called");
        DatabaseReference myRef = database.getReference(TopLevelDirectories.TRIPS.getName())
                .child(id);
        // TODO(CT): Implement

        Log.v(DEBUG_TAG, "addTrip: Finished");
    }

    /**
     * Remove a trip
     *
     * TODO(CT): What else is needed?
     */
    public void removeTrip() {
        Log.v(DEBUG_TAG, "removeTrip: Called");
        DatabaseReference myRef = database.getReference(TopLevelDirectories.TRIPS.getName())
                .child(id);
        // TODO(CT): Implement

        Log.v(DEBUG_TAG, "removeTrip: Finished");
    }

    /**
     * Add to impact estimate for a day
     *
     * @param day           The day to which the amount is added
     * @param co2impact     Amount to add to day impact
     *
     * TODO(CT): How are we representing days?
     */
    public void addToDayEstimate(long day, Estimate co2impact) {
        Log.v(DEBUG_TAG, "addToDayEstimate: Called");
        DatabaseReference myRef = database.getReference(TopLevelDirectories.ESTIMATES.getName())
                .child(id);
        // TODO(CT): Implement

        // Add to day impact

        // Update total estimate

        Log.v(DEBUG_TAG, "addToDayEstimate: Finished");
    }

    /**
     * Get gps data points between range
     *
     * @param start     Start time
     * @param end       End time
     * @return          List of GPS points
     */
    public List<Transportation.GPS> getGPSPoints(long start, long end) {
        Log.v(DEBUG_TAG, "getGPSPoints: Called");
        DatabaseReference myRef = database.getReference(TopLevelDirectories.GPS.getName())
                .child(id);
        List<Transportation.GPS> gpsList = null;

        // TODO(CT): Implement

        Log.v(DEBUG_TAG, "getGPSPoints: finished");
        return gpsList;
    }

    /**
     * Get trips for day
     *
     * @param day   Day for which to receive trips
     * @return      Trips for that day
     */
    public List<Transportation.Trip> getTrips(long day) {
        Log.v(DEBUG_TAG, "getTrips: Called");
        DatabaseReference myRef = database.getReference(TopLevelDirectories.GPS.getName())
                .child(id);
        List<Transportation.Trip> tripList = null;

        // TODO(CT): Implement

        Log.v(DEBUG_TAG, "getTrips: finished");
        return tripList;
    }

    /**
     * Get CO2 Estimate for user
     *
     * @return  Estimate (all-time)
     */
    public Estimate getEstimate() {
        Log.v(DEBUG_TAG, "getEstimate: Called");
        return estimateMonitor.estimate;
    }

    /**
     * Get CO2 Estimate for user for day
     *
     * @param day   Day for id
     * @return  Estimate (for day)
     */
    public Estimate getEstimate(long day) {
        Log.v(DEBUG_TAG, "getEstimate: Called");
        return estimateMonitor.todaysEstimate;
    }
}
