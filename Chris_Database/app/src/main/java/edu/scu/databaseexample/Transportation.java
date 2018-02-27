package edu.scu.databaseexample;

import android.util.Log;

/**
 * Created by teubert on 2/26/18.
 */

public class Transportation {
    /**
     * Class to contain GPS data (element in unfiltered_gps_data key in database)
     */
    public static class GPS {
        private final static String DEBUG_TAG = "GPS";

        public long timestamp; // Linux time (timestamp.getTime())
        public double lon; // Longitude (deg)
        public double lat; // Latitude (deg)

        public GPS() {
            // Default constructor required for calls to DataSnapshot.getValue(GPS.class)
        }

        public GPS(long timestamp, double lon, double lat) {
            Log.v(DEBUG_TAG, "Creating GPS Object");
            this.timestamp = timestamp;
            this.lon = lon;
            this.lat = lat;
        }
    }

    /**
     * Class to contain information for a single trip
     */
    public static class Trip {
        private final static String DEBUG_TAG = "Trip";

        public GPS start;
        public GPS end;
        public long day;
        TransportMode transport_mode;
        CarType car_type;

        public Trip() {
            // Default constructor required for calls to DataSnapshot.getValue(Trip.class)
        }

        public Trip(GPS start, GPS end,
                    Transportation.TransportMode transport_mode,
                    Transportation.CarType car_type) {
            Log.v(DEBUG_TAG, "Creating Trip Object");
            this.start = start;
            this.end = end;
            this.transport_mode = transport_mode;
            this.car_type = car_type;
            // TODO(CT): get day from range
        }

    }

    /**
     * Transport Modes
     */
    public enum TransportMode {
        WALK, BIKE, AUTOMOBILE, AIRCRAFT, TRAIN, BOAT
    }

    /**
     *  Car Categories
     *  TODO(CT): Look up and choose actual efficiency numbers
     *  TODO(CT): Switch to metric
     */
    public enum CarType {
        // NAME  (mgp, mpw)
        SMALL_CAR       (40, 0),
        MID_CAR         (30, 0),
        LARGE_CAR       (20, 0),  // Cars
        SMALL_TRUCK     (20, 0),
        LARGE_TRUCK     (10, 0),       // Trucks
        SUV             (10, 0),                            // SUV
        HYBRID_CAR      (40, 0),
        HYBRID_TRUCK    (25, 0),       // Hybrid
        ELECTRIC_CAR    (0,  100),
        ELECTRIC_TRUCK  (0,  50),   // Electric
        NGAS_CAR        (0,  0),
        NGAS_TRUCK      (0,  0);           // Natural Gas

        private final static String DEBUG_TAG = "CarType";
        private double mpg; // Miles per gallon
        private double mpw; // Miles per Watt
        // TODO(CT): Is this really the right metric?
        // TODO(CT): How is Natural Gas covered
        // TODO(CT): What about diesel?

        CarType(double mpg, double mpw) {
            Log.v(DEBUG_TAG, "Creating CarType Object");
            this.mpg = mpg;
            this.mpw = mpw;
        }

        /**
         * Get the gas fuel efficiency
         *
         * @return gas fuel efficiency (in miles per gallon)
         */
        public double getMpg() {
            Log.v(DEBUG_TAG, "Getting MPG");
            return this.mpg;
        }

        /**
         * Get the electric fuel efficiency
         *
         * @return electric fuel efficiency (in miles per watt)
         */
        public double getMpw() {
            Log.v(DEBUG_TAG, "Getting MPW");
            return this.mpw;
        }
    }
}
