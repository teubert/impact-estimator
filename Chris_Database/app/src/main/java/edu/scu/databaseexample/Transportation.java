package edu.scu.databaseexample;

import android.util.Log;

/**
 * Created by teubert on 2/26/18.
 */

public class Transportation {
    /**
     * Transport Modes
     */
    public enum TransportMode {
        //https://truecostblog.com/2010/05/27/fuel-efficiency-modes-of-transportation-ranked-by-mpg/
        // NAME (mpg, mpw)
        WALK (0,0),
        BIKE (0,0),
        AUTOMOBILE (1,1),
        AIRCRAFT (42.6, 0),
        TRAIN (71.6, 0),
        BOAT (340, 0);
        // TODO(CT): Add bus
        // BUS (38.3, 0)

        private final static String DEBUG_TAG = "TransportMode";
        private double mpg; // Miles per gallon
        private double mpw; // Miles per Watt
        // TODO(CT): Is this really the right metric?
        // TODO(CT): How is Natural Gas covered
        // TODO(CT): What about diesel?

        TransportMode(double mpg, double mpw) {
            Log.v(DEBUG_TAG, "Creating Transport Mode Object");
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

        public static TransportMode fromValue(String transportTypeName){
            for (TransportMode l : TransportMode.values()){
                if (l.name().equals(transportTypeName)){
                    return l;
                }
            }
            throw new IllegalArgumentException("Invalid transport mode: " + transportTypeName);
        }
    }

    /**
     *  Car Categories
     *  TODO(CT): Look up and choose actual efficiency numbers
     *  TODO(CT): Switch to metric
     */
    public enum CarType {
        // NAME  (mpg, mpw)
        MOTORCYCLE      (71.8, 0),
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

        public static CarType fromValue(String carTypeName){
            for (CarType l : CarType.values()){
                if (l.name().equals(carTypeName)){
                    return l;
                }
            }
            throw new IllegalArgumentException("Invalid car type: " + carTypeName);
        }
    }
}
