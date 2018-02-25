package edu.scu.databaseexample;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Calendar;

// TODO(CT): Add error handling
// TODO(CT): Remove GPS-> Archive GPS?
// TODO(CT): Remove hard-coded strings
// TODO(CT): How are we going to handle email updates if email == id - do we want to support that

/**
 * Created by teubert on 2/21/18.
 */
public class ImpactDatabaseHelper {
    private static final String DEBUG_TAG = "DatabaseHelper";

    private FirebaseDatabase database;

    /**
     *  Car Categories
     */
    public enum car_categories {
        //TODO(CT): Implement and integrate
    }

    /**
     * Transport Mode
     */
    public enum transport_mode {
        //TODO(CT): Implement and integrate
    }

    /**
     * Class to contain user information (element in users key in database)
     */
    @IgnoreExtraProperties
    public static class User {
        public String email;
        public String name;
        public String car_type;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String email, String name, String car_type) {
            Log.v(DEBUG_TAG, "Creating User Object");
            this.email = email; // Email Address
            this.name = name; // Name
            this.car_type = car_type; // Car type
        }
    }

    /**
     * Class to contain GPS data (element in unfiltered_gps_data key in database)
     */
    public static class GPS {
        public long timestamp; // Linux time (timestamp.getTime())
        public double lon; // Longitude (deg)
        public double lat; // Latitude (deg)

        public GPS() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public GPS(long timestamp, double lon, double lat) {
            Log.v(DEBUG_TAG, "Creating GPS Object");
            this.timestamp = timestamp;
            this.lon = lon;
            this.lat = lat;
        }
    }

    /**
     * Convert from email to username (which is email with forbidden characters replaced
     *
     * @param email Email Address
     * @return  Associated Username
     */
    static public String emailToUsername(String email) {
        Log.v(DEBUG_TAG, "Converting email:" + email + "to username");
        return email.replaceAll("[@.]", "_");
    }

    /**
     * get the current timestamp in "unix time"
     *
     * @return Current timestamp in unix time
     */
    static public long getCurrentTimestamp() {
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
     * Constructor for Database Helper
     */
    public ImpactDatabaseHelper() {
        Log.v(DEBUG_TAG, "Configuring ImpactDataBaseHelper");

        // Set Database
        database = FirebaseDatabase.getInstance();
    }

    /**
     * Add a new user to the database
     *
     * @param email     Email address
     * @param name      Name
     * @param car_type  Car type
     */
    public void addNewUser(String email, String name, String car_type) {
        Log.v(DEBUG_TAG, "addNewUser: Called");
        long currentTime = getCurrentTimestamp();
        DatabaseReference myRef = database.getReference("users");
        User user = new User(email, name, car_type);
        String id = emailToUsername(user.email);

        Log.i(DEBUG_TAG, "Registering new user with id=" + id);
        //myRef.child(id).setValue(user);
        myRef = myRef.child(id);
        // TODO(CT): Check if user exists
        myRef.child("email").setValue(email);
        myRef.child("name").setValue(name);
        myRef.child("car_type").setValue(car_type);

        myRef.child("user_since").setValue(currentTime);
        myRef.child("last_login").setValue(currentTime);
        Log.v(DEBUG_TAG, "addNewUser: Finished");
    }

    /**
     * Update an existing user in the database
     *
     * @param id
     * @param name
     * @param car_type
     */
    public void updateUser(String id, String name, String car_type) {
        Log.v(DEBUG_TAG, "updateUser: Called");
        DatabaseReference myRef = database.getReference("users").child(id);
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
     * add a new GPS datapoint for a user
     *
     * @param id        User Id
     * @param point     GPS point
     */
    public void addNewGPSDataPoint(String id, GPS point) {
        Log.v(DEBUG_TAG, "addNewGPSDataPoint: Called");
        DatabaseReference myRef = database.getReference("unfiltered_gps_data")
                .child(id)
                .child(Long.toString(point.timestamp));
        myRef.child("lat").setValue(point.lat);
        myRef.child("lon").setValue(point.lon);
        Log.v(DEBUG_TAG, "addNewGPSDataPoint: Finished");
    }

    /**
     * Update the last login time
     *
     * @param id    User id
     */
    public void updateLastLogin(String id) {
        Log.v(DEBUG_TAG, "updateLastLogin: Called");
        DatabaseReference myRef = database.getReference("users").child(id);
        myRef.child("last_login").setValue(getCurrentTimestamp());
        Log.v(DEBUG_TAG, "updateLastLogin: Finished");
    }

    /**
     * Remove GPS points in range
     *
     * @param id        User id
     * @param start     Start time (unix time)
     * @param end       End time (unix time)
     */
    public void removeGPSPoints(String id, Long start, Long end) {
        Log.v(DEBUG_TAG, "removeGPSPoints: Called");
        DatabaseReference myRef = database.getReference("users").child(id);
        // TODO(CT): Implement

        Log.v(DEBUG_TAG, "removeGPSPoints: finished");
    }

    /**
     * Add a new trip
     *
     * @param id                User id
     * @param transport_mode    Mode of tranportation
     * @param start             Start GPS location
     * @param end               End GPS location
     *
     * TODO(CT): Replace String transport_mode with enum
     */
    public void addTrip(String id, String transport_mode, GPS start, GPS end) {
        Log.v(DEBUG_TAG, "addTrip: Called");
        DatabaseReference myRef = database.getReference("users").child(id);
        // TODO(CT): Implement

        Log.v(DEBUG_TAG, "addTrip: Finished");
    }

    /**
     * Remove a trip
     *
     * @param id    User id
     *
     * TODO(CT): What else is needed?
     */
    public void removeTrip(String id) {
        Log.v(DEBUG_TAG, "removeTrip: Called");
        DatabaseReference myRef = database.getReference("users").child(id);
        // TODO(CT): Implement

        Log.v(DEBUG_TAG, "removeTrip: Finished");
    }

    /**
     * Add to impact estimate for a day
     *
     * @param id            User id
     * @param day           The day to which the amount is added
     * @param co2impact     Amount to add to day impact
     *
     * TODO(CT): How are we representing days?
     */
    public void addToDayEstimate(String id, long day, double co2impact) {
        Log.v(DEBUG_TAG, "addToDayEstimate: Called");
        DatabaseReference myRef = database.getReference("users").child(id);
        // TODO(CT): Implement

        // Add to day impact

        // Update total estimate

        Log.v(DEBUG_TAG, "addToDayEstimate: Finished");
    }
}
