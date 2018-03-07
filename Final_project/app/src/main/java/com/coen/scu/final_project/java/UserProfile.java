package com.coen.scu.final_project.java;

/**
 * Created by teubert on 2/26/18.
 */

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Vector;

/**
 * Class to contain userProfile information (element in users key in database)
 */
public class UserProfile implements ValueEventListener {
    private static final String DEBUG_TAG = "UserProfile";
    private static final String TOP_LEVEL_KEY = "users";

    private String email = null;
    private String name = null;
    private String id = null;
    private Transportation.CarType car_type = null;

    private DatabaseReference myRef;
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    Vector<UserUpdateInterface> callbacks = new Vector<UserUpdateInterface>();

    /**
     *
     * @param email
     */
    public void setEmail(String email) {
        if (email != null) {
            this.email = email;
            myRef.child("email").setValue(email);
        }
    }

    /**
     *
     */
    public interface UserUpdateInterface {
        void onUserUpdate();
    }

    /**
     *
     * @param callback
     */
    public void addCallback(UserUpdateInterface callback) {
        if (callback == null) {
            Log.e(DEBUG_TAG, "tried to add null callback");
            return;
        }
        myRef.addListenerForSingleValueEvent(this);
        callbacks.add(callback);
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        if (name != null) {
            this.name = name;
            myRef.child("name").setValue(name);
        }
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param car_type
     */
    public void setCarType(Transportation.CarType car_type) {
        this.car_type = car_type;
    }

    /**
     *
     * @return
     */
    public Transportation.CarType getCarType() {
        return car_type;
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
     *
     * @param dataSnapshot
     */
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.v(DEBUG_TAG, "onDataChange: Updating user data");
        this.name = dataSnapshot.child("name").getValue(String.class);
        this.email = dataSnapshot.child("email").getValue(String.class);
        this.car_type = Transportation.CarType.fromValue(dataSnapshot.child("car_type").getValue(String.class));
        Log.d(DEBUG_TAG, "onDataChange: Updated user to " + this.name + " (" + this.email + ")");
        if (callbacks != null) {
            for (UserUpdateInterface callback : callbacks) {
                callback.onUserUpdate();
            }
        }
    }

    /**
     *
     * @param databaseError
     */
    @Override
    public void onCancelled(DatabaseError databaseError) {
        System.out.println("The read failed: " + databaseError.getCode());
    }

    /**
     * Update the last login time
     */
    public void updateLastLogin() {
        Log.v(DEBUG_TAG, "updateLastLogin: Called");
        myRef.child("last_login").setValue(getCurrentTimestamp());
        Log.v(DEBUG_TAG, "updateLastLogin: Finished");
    }

    /**
     * Add a new userProfile to the database
     *
     * @param email     Email address
     * @param name      Name
     * @param car_type  Car type
     */
    static public UserProfile addNewUserProfile(String email, String name, Transportation.CarType car_type) {
        UserProfile userProfile = new UserProfile(emailToUsername(email));
        DatabaseReference myRef = database.getReference(TOP_LEVEL_KEY);

        Log.v(DEBUG_TAG, "addNewUser: Called");
        long currentTime = getCurrentTimestamp();
        String id = UserProfile.emailToUsername(email);

        Log.i(DEBUG_TAG, "Registering new userProfile with id=" + id);
        //myRef.child(id).setValue(userProfile);
        myRef = myRef.child(id);
        // TODO(CT): Check if userProfile exists
        myRef.child("email").setValue(email);
        myRef.child("name").setValue(name);
        myRef.child("car_type").setValue(car_type);

        myRef.child("user_since").setValue(currentTime);
        myRef.child("last_login").setValue(currentTime);
        Log.v(DEBUG_TAG, "addNewUser: Finished");

        return userProfile;
    }

    static public UserProfile getUserProfileById(String id) {
        return new UserProfile(id);
    }

    static public UserProfile getUserProfileByEmail(String email) {
        String id = emailToUsername(email);
        // TODO(CT): Get id from lookup
        return new UserProfile(id);
    }

    /**
     * Create a new User Profile by id
     *
     * @param id    Id of user
     */
    private UserProfile(String id) {
        this.id = id;
        myRef = database.getReference(TOP_LEVEL_KEY)
                .child(id);
        myRef.addValueEventListener(this);
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
