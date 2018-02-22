package edu.scu.databaseexample;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by teubert on 2/21/18.
 */

public class ImpactDatabaseHelper {
    private FirebaseDatabase database;

    @IgnoreExtraProperties
    public static class User {

        public String email;
        public String name;
        public String car_type;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String email, String name, String car_type) {
            this.email = email;
            this.name = name;
            this.car_type = car_type;
        }
    }

    public static class GPS {
        public long timestamp; // Linux time (timestamp.getTime())
        public double lon;
        public double lat;

        public GPS() {

        }

        public GPS(long timestamp, double lon, double lat) {
            this.timestamp = timestamp;
            this.lon = lon;
            this.lat = lat;
        }
    }

    static public String emailToUsername(String email) {
        return email.replaceAll("[@.]", "_");
    }

    public ImpactDatabaseHelper() {
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
    }

    public void addNewUser(String email, String name, String car_type) {
        DatabaseReference myRef = database.getReference("users");
        User user = new User(email, name, car_type);
        String id = emailToUsername(user.email);
        Log.d("DatabaseHelper", id);
        //myRef.child(id).setValue(user);
        myRef.child(id).child("email").setValue(email);
        myRef.child(id).child("name").setValue(name);
        myRef.child(id).child("car_type").setValue(car_type);

        // Get timestamp for new user
        // Set timestamp for last signin
    }

    public void addNewGPSDataPoint(String id, GPS point) {
        DatabaseReference myRef = database.getReference("unfiltered_gps_data")
                .child(id)
                .child(Long.toString(point.timestamp));
        myRef.child("lat").setValue(point.lat);
        myRef.child("lon").setValue(point.lon);
    }
}
