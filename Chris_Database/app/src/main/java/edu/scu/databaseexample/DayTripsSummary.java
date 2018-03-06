package edu.scu.databaseexample;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by teubert on 2/28/18.
 */

// TODO(CT): Error handling

public class DayTripsSummary implements ChildEventListener {
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

    static public void updateTrip(String id, Trip trip) {
        Log.d(DEBUG_TAG, "Updating trip " + trip.getTripId());
        // Get day
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(trip.getEnd().timestamp);
        String dayKey = getDateString(cal);

        // Update
        DatabaseReference myRef = database.getReference(TOP_LEVEL_KEY).child(id).child(dayKey).child(trip.getTripId());
        myRef.child("car_type").setValue(trip.getCar_type());
        myRef.child("transportation_mode").setValue(trip.getTransport_mode());
        myRef.child("distance").setValue(trip.getDistance());
        myRef.child("estimates").child("CO2").setValue(trip.getEstimate().CO2);
        myRef.child("start").setValue(trip.getStart());
        myRef.child("end").setValue(trip.getEnd());
    }

    /**
     *
     * @param tripSnapshot
     */
    private Trip addTripSnapshot(DataSnapshot tripSnapshot) {
        String key = tripSnapshot.getKey();
       try {
            GPSPoint start = tripSnapshot.child("start").getValue(GPSPoint.class);
            GPSPoint end = tripSnapshot.child("end").getValue(GPSPoint.class);
            double dist = tripSnapshot.child("distance").getValue(Double.class);
            Transportation.TransportMode mode = Transportation.TransportMode.fromValue(
                   tripSnapshot.child("transportation_mode").getValue(String.class));
            Transportation.CarType carType = Transportation.CarType.fromValue(
                   tripSnapshot.child("car_type").getValue(String.class));
            double co2 = tripSnapshot.child("estimates").child("CO2").getValue(Double.class);
            FootprintEstimate estimate = new FootprintEstimate(co2, 1);
            return new Trip(start, end, dist, mode, carType, tripSnapshot.getKey(), estimate);
        } catch (java.lang.IllegalArgumentException ex) {
            Log.w(DEBUG_TAG, "Ran into unfinished trip (id=" + key +")");
            return null;
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

        Trip trip = addTripSnapshot(tripSnapshot);

        if (prevChildKey != null) {
            int id = 0;
            for (; id < trips.size(); id++) {
                if (trips.get(id).getTripId().equals(prevChildKey)) {
                    break;
                }
            }
            if (id == trips.size()) {
                trips.add(trip);
            } else {
                trips.add(id + 1, trip);
            }
        } else {
            trips.add(0, trip);
        }

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
     * @param tripSnapshot
     * @param prevChildKey
     */
    @Override
    public void onChildChanged(DataSnapshot tripSnapshot, String prevChildKey) {
        Log.d(DEBUG_TAG, "onChildChanged: updated trip");
        if (callbacks != null) {
            for (TripUpdateInterface callback : callbacks) {
                callback.onTripUpdate();
            }
        }

        Trip newTrip = addTripSnapshot(tripSnapshot);
        for (int pos = 0; pos < trips.size(); pos++) {
            if (trips.get(pos).getTripId().equals(newTrip.getTripId())) {
                trips.set(pos, newTrip);
            }
        }

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
        //myRef.addListenerForSingleValueEvent(this); TODO(CT): FIX THIS
        Log.v(DEBUG_TAG, "addCallback: done");
    }

    public void removeCallback(TripUpdateInterface callbackToRemove) {
        boolean success = callbacks.remove(callbackToRemove);
        if (!success) {
            Log.w(DEBUG_TAG, "Could not remove callback");
        }
        Log.v(DEBUG_TAG, "Removing Callback. " + Integer.toString(callbacks.size()) + " remaining");
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
        cal.setTimeInMillis(trip.getEnd().timestamp);
        String dayKey = getDateString(cal);

        // Push for day
        DatabaseReference myRef = database.getReference(TOP_LEVEL_KEY).child(id).child(dayKey);
        myRef = myRef.push();
        myRef.child("car_type").setValue(trip.getCar_type());
        myRef.child("transportation_mode").setValue(trip.getTransport_mode());
        myRef.child("distance").setValue(trip.getDistance());
        myRef.child("estimates").child("CO2").setValue(trip.getEstimate().CO2);
        myRef.child("start").setValue(trip.getStart());
        myRef.child("end").setValue(trip.getEnd());

        Log.v(DEBUG_TAG, "append: Done");
    }

    /**
     *
     * @param tripId
     * @return
     */
    public Trip getTrip(String tripId) {
        Log.d(DEBUG_TAG, "GetTrip: getting trip with id=" + tripId);
        for (Trip trip : trips) {
            if (trip.getTripId().equals(tripId)) {
                Log.d(DEBUG_TAG, "GetTrip: trip found, returning trip");
                return trip;
            }
        }
        Log.w(DEBUG_TAG, "getTrip: Trip doesn't exist");

        return null;
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

    /**
     *
     * @param day
     */
    public void setDay(Calendar day) {
        String dayKey = getDateString(day);
        Log.v(DEBUG_TAG, "DayTripsSummary: Setting reference " +
                TOP_LEVEL_KEY + "/" + id + "/" + dayKey);
        if (myRef != null) {
            myRef.removeEventListener(DayTripsSummary.this);
        }
        trips.clear();
        myRef = database.getReference(TOP_LEVEL_KEY).child(id).child(dayKey);

        myRef.addChildEventListener(this);
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

    // TODO(CT): Get a specific day
}
