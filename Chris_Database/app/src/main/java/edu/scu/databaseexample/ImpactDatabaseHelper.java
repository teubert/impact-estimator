package edu.scu.databaseexample;
//
//import android.util.Log;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.Calendar;
//import java.util.List;
//
//// TODO(CT): Add error handling
//// TODO(CT): Remove GPS-> Archive GPS?
//// TODO(CT): How are we going to handle email updates if email == id - do we want to support that
//// TODO(CT): Get timestamp
//// TODO(CT): How do we handle days
//
///**
// * Created by teubert on 2/21/18.
// */
public class ImpactDatabaseHelper {
    public ImpactDatabaseHelper() {

    }
//    private static final String DEBUG_TAG = "DatabaseHelper";
//
//    private FirebaseDatabase database;
//    String id = null;
//
//    private class EstimateMonitor implements ValueEventListener {

//    }
//    EstimateMonitor estimateMonitor = new EstimateMonitor();
//
//    public enum TopLevelDirectories {
//        GPS_ARCHIVE ("gps_archive"),
//        TRIPS       ("trips"),
//        ESTIMATES   ("estimates");
//        private final String name;
//
//        TopLevelDirectories(String name) {
//            this.name = name;
//        }
//
//        public String getName() {
//            return this.name;
//        }
//    }
//
//    /**
//     * Constructor for Database Helper - no userProfile
//     */
//    public ImpactDatabaseHelper() {
//        Log.v(DEBUG_TAG, "Configuring ImpactDataBaseHelper");
//
//        // Set Database
//        database = FirebaseDatabase.getInstance();
//    }
//
//    /**
//     * Constructor for Database Helper for specific userProfile
//     *
//     * @param id    UserProfile Id
//     */
//    public ImpactDatabaseHelper(String id) {
//        Log.v(DEBUG_TAG, "Configuring ImpactDataBaseHelper");
//
//        // Set Database
//        database = FirebaseDatabase.getInstance();
//
//        setUser(id);
//    }
//
//    /**
//     * Add a new trip
//     *
//     * @param trip              Trip to add
//     *
//     * TODO(CT): Replace String transport_mode with enum
//     */
//    public void addTrip(Transportation.Trip trip) {
//        Log.v(DEBUG_TAG, "addTrip: Called");
//        DatabaseReference myRef = database.getReference(TopLevelDirectories.TRIPS.getName())
//                .child(id);
//        // TODO(CT): Implement
//
//        Log.v(DEBUG_TAG, "addTrip: Finished");
//    }
//
//    /**
//     * Remove a trip
//     *
//     * TODO(CT): What else is needed?
//     */
//    public void removeTrip() {
//        Log.v(DEBUG_TAG, "removeTrip: Called");
//        DatabaseReference myRef = database.getReference(TopLevelDirectories.TRIPS.getName())
//                .child(id);
//        // TODO(CT): Implement
//
//        Log.v(DEBUG_TAG, "removeTrip: Finished");
//    }
//
//    /**
//     * Add to impact estimate for a day
//     *
//     * @param day           The day to which the amount is added
//     * @param co2impact     Amount to add to day impact
//     *
//     * TODO(CT): How are we representing days?
//     */
//    public void addToDayEstimate(long day, FootprintEstimate co2impact) {
//        Log.v(DEBUG_TAG, "addToDayEstimate: Called");
//        DatabaseReference myRef = database.getReference(TopLevelDirectories.ESTIMATES.getName())
//                .child(id);
//        // TODO(CT): Implement
//
//        // Add to day impact
//
//        // Update total estimate
//
//        Log.v(DEBUG_TAG, "addToDayEstimate: Finished");
//    }
//
//    /**
//     * Get trips for day
//     *
//     * @param day   Day for which to receive trips
//     * @return      Trips for that day
//     */
//    public List<Transportation.Trip> getTrips(long day) {
//        Log.v(DEBUG_TAG, "getTrips: Called");
//        DatabaseReference myRef = database.getReference(TopLevelDirectories.GPS.getName())
//                .child(id);
//        List<Transportation.Trip> tripList = null;
//
//        // TODO(CT): Implement
//
//        Log.v(DEBUG_TAG, "getTrips: finished");
//        return tripList;
//    }
//
//    /**
//     * Get CO2 FootprintEstimate for userProfile
//     *
//     * @return  FootprintEstimate (all-time)
//     */
//    public FootprintEstimate getEstimate() {
//        Log.v(DEBUG_TAG, "getEstimate: Called");
//        return estimateMonitor.estimate;
//    }
//
//    /**
//     * Get CO2 FootprintEstimate for userProfile for day
//     *
//     * @param day   Day for id
//     * @return  FootprintEstimate (for day)
//     */
//    public FootprintEstimate getEstimate(long day) {
//        Log.v(DEBUG_TAG, "getEstimate: Called");
//        return estimateMonitor.todaysEstimate;
//    }
}
