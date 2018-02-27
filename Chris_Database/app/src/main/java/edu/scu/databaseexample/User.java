package edu.scu.databaseexample;

/**
 * Created by teubert on 2/26/18.
 */

import android.util.Log;

/**
 * Class to contain user information (element in users key in database)
 */
public class User {
    private static final String DEBUG_TAG = "User";

    public String email;
    public String name;
    public Transportation.CarType car_type;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String name, Transportation.CarType car_type) {
        Log.v(DEBUG_TAG, "Creating User Object");
        this.email = email; // Email Address
        this.name = name; // Name
        this.car_type = car_type; // Car type
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
}
