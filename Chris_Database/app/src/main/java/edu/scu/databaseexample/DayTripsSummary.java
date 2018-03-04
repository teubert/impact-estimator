package edu.scu.databaseexample;

import android.util.Log;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by teubert on 2/28/18.
 */

public class DayTripsSummary implements ValueEventListener, ChildEventListener {
    private final static String DEBUG_TAG = "DayTripsSummary";
    private static final String TOP_LEVEL_KEY = "trips";
    String id;

    ArrayList<Trip> trips = new ArrayList<>();
    ArrayList<TripUpdateInterface> callbacks = new ArrayList<TripUpdateInterface>();
    private DatabaseReference myRef;
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     *  Interface for callback for tripUpdate
     */
    public interface TripUpdateInterface {
        void onTripUpdate();
    }

    /**
     * Called when data is changed (called once on creation, afterwhich ChildEventListener used)
     *
     * @param dataSnapshot  Snapshot of data
     */
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d(DEBUG_TAG, "onDataChange: Updating day trip data");
        trips.clear();
        for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
            addTripSnapshot(tripSnapshot);
        }

        Log.v(DEBUG_TAG, "onDataChange: notifying callbacks");
        if (callbacks != null) {
            for (TripUpdateInterface callback : callbacks) {
                callback.onTripUpdate();
            }
        }
        Log.v(DEBUG_TAG, "onDataChange: complete");
    }

    private void addTripSnapshot(DataSnapshot tripSnapshot) {
        String key = tripSnapshot.getKey();
       try {
            Trip trip = new Trip();
            trip.car_type = Transportation.CarType.fromValue(
                    tripSnapshot.child("car_type").getValue(String.class));
            trip.transport_mode = Transportation.TransportMode.fromValue(
                    tripSnapshot.child("transportation_mode").getValue(String.class));
            trip.start = tripSnapshot.child("start").getValue(GPSPoint.class);
            trip.end = tripSnapshot.child("end").getValue(GPSPoint.class);
            trips.add(trip);
        } catch (java.lang.IllegalArgumentException ex) {
            Log.w(DEBUG_TAG, "Ran into unfinished trip (id=" + key +")");
        }
    }

    /**
     * Called upon adding a trip to the database
     *
     * @param tripSnapshot
     * @param prevChildKey
     */
    @Override
    public void onChildAdded(DataSnapshot tripSnapshot, String prevChildKey) {
        Log.d(DEBUG_TAG, "onChildAdded: new trip");
        addTripSnapshot(tripSnapshot);

        if (callbacks != null) {
            for (TripUpdateInterface callback : callbacks) {
                callback.onTripUpdate();
            }
        }
        Log.v(DEBUG_TAG, "onChildAdded: done");
    }

    /**
     * Called upon change of a trip
     *
     * @param dataSnapshot
     * @param prevChildKey
     */
    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
        Log.d(DEBUG_TAG, "onChildChanged: updated trip");
        if (callbacks != null) {
            for (TripUpdateInterface callback : callbacks) {
                callback.onTripUpdate();
            }
        }
        Log.v(DEBUG_TAG, "onChildChanged: done");
    }

    /**
     * Called on removal of a trip
     *
     * @param dataSnapshot Updated snapshot
     */
    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(DEBUG_TAG, "onChildRemoved: removed trip");
        if (callbacks != null) {
            for (TripUpdateInterface callback : callbacks) {
                callback.onTripUpdate();
            }
        }
        Log.v(DEBUG_TAG, "onChildRemoved: done");
    }

    /**
     * Called on moving a trip
     *
     * @param dataSnapshot
     * @param prevChildKey
     */
    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
        Log.d(DEBUG_TAG, "onChildMoved: moved tip- ignoring");
    }

    /**
     * Called when a database error has occured when receiving child or data update
     *
     * @param databaseError The database error
     */
    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e(DEBUG_TAG, "onCancelled: The read failed: " + databaseError.getCode());
    }

    /**
     * Add a callback TripUpdateInterface
     *
     * @param callback The callback to be added
     */
    public void addCallback(TripUpdateInterface callback) {
        Log.d(DEBUG_TAG, "addCallback: Adding callback");
        if (callback == null) {
            Log.e(DEBUG_TAG, "tried to add null callback");
            return;
        }
        callbacks.add(callback);
        myRef.addListenerForSingleValueEvent(this);
        Log.v(DEBUG_TAG, "addCallback: done");
    }

    /**
     * Append a new trip to the database
     *
     * @param id    User id
     * @param trip  The new trip to add
     */
    static public void append(String id, Trip trip) {
        Log.d(DEBUG_TAG, "append: Adding new trip to day");

        // Get day
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(trip.end.timestamp);
        String dayKey = getDateString(cal);

        // Push for day
        DatabaseReference myRef = database.getReference(TOP_LEVEL_KEY).child(id).child(dayKey);
        myRef = myRef.push();
        myRef.child("car_type").setValue(trip.car_type);
        myRef.child("transportation_mode").setValue(trip.transport_mode);
        myRef.child("start").setValue(trip.start);
        myRef.child("end").setValue(trip.end);

        Log.v(DEBUG_TAG, "append: Done");
    }

    /**
     * Get the trips for a specific calendar day
     *
     * @param id    User Id
     * @param day   Day for which to get trips
     * @return  The trips for the specified day
     */
    static public DayTripsSummary getDayTripsForDay(String id, Calendar day) {
        Log.d(DEBUG_TAG, "getDayTripsForDay: getting day trips for " + getDateString(day));
        return new DayTripsSummary(id, day);
    }

    public void setDay(Calendar day) {
        String dayKey = getDateString(day);
        Log.v(DEBUG_TAG, "DayTripsSummary: Setting reference " +
                TOP_LEVEL_KEY + "/" + id + "/" + dayKey);
        myRef = database.getReference(TOP_LEVEL_KEY).child(id).child(dayKey);

        myRef.addListenerForSingleValueEvent(this);
    }

    /**
     * Get the trips for today
     *
     * @param id    User Id
     * @return  The trips for today
     */
    static public DayTripsSummary getDayTripsForToday(String id) {
        Log.d(DEBUG_TAG, "getDayTripsForToday called");
        return new DayTripsSummary(id, Calendar.getInstance());
    }

    /**
     * Get date string of required format from calendar
     *
     * @param day   The day to which to get a string
     * @return  Get the string for that day
     */
    static public String getDateString(Calendar day) {
        Log.v(DEBUG_TAG, "getDateString called");
        String dayKey = Integer.toString(day.get(Calendar.YEAR)) + '-' +
                String.format("%02d", day.get(Calendar.MONTH)+1) + '-' +
                String.format("%02d", day.get(Calendar.DAY_OF_MONTH));
        return dayKey;
    }

    /**
     * Private constructor for day trip summary
     *
     * @param id    User Id
     * @param day   Day for which to get day
     */
    private DayTripsSummary(String id, Calendar day) {
        this.id = id;
        Log.d(DEBUG_TAG, "DayTripsSummary: creating day trips summary");
        setDay(day);
    }
}
