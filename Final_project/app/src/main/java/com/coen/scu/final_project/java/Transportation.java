package com.coen.scu.final_project.java;

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
        // NAME (mpg, mpkwh)
        WALK (0,0),
        BIKE (0,0),
        AUTOMOBILE (1,1),
        BUS (38.3, 0),
        AIRCRAFT (42.6, 0),
        TRAIN (71.6, 0),
        BOAT (340, 0);

        private final static String DEBUG_TAG = "TransportMode";
        private double mpg; // Miles per gallon
        private double mpkwh; // Miles per Watt

        TransportMode(double mpg, double mpkwh) {
            Log.v(DEBUG_TAG, "Creating Transport Mode Object");
            this.mpg = mpg;
            this.mpkwh = mpkwh;
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
         * @return electric fuel efficiency (in miles per kWh)
         */
        public double getMpkwh() {
            Log.v(DEBUG_TAG, "Getting MPkWh");
            return this.mpkwh;
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
     *  TODO(CT): Switch to metric
     */
    public enum CarType {
        // NAME  (mpg, mpkwh)
        MOTORCYCLE      (71.8, 0),
        SMALL_CAR       (40, 0),
        MID_CAR         (30, 0),
        LARGE_CAR       (20, 0),
        SMALL_TRUCK     (20, 0),
        LARGE_TRUCK     (10, 0),
        SUV             (15, 0),
        HYBRID_CAR      (40, 0),
        HYBRID_TRUCK    (20, 0),
        ELECTRIC_CAR    (0,  2.94),  // https://www.greencarreports.com/news/1082737_electric-car-efficiency-forget-mpge-it-should-be-miles-kwh
        ELECTRIC_TRUCK  (0,  1.5),
        UNKNOWN         (30, 0);

        private final static String DEBUG_TAG = "CarType";
        private double mpg; // Miles per gallon
        private double mpkwh; // Miles per Kwh
        // TODO(CT): How is Natural Gas covered
        // TODO(CT): What about diesel?

        CarType(double mpg, double mpkwh) {
            Log.v(DEBUG_TAG, "Creating CarType Object");
            this.mpg = mpg;
            this.mpkwh = mpkwh;
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
         * @return electric fuel efficiency (in miles per kwh)
         */
        public double getMpkwh() {
            Log.v(DEBUG_TAG, "Getting MPW");
            return this.mpkwh;
        }

        public static CarType fromValue(String carTypeName){
            for (CarType l : CarType.values()){
                if (l.name().equals(carTypeName)){
                    return l;
                }
            }
            Log.d(DEBUG_TAG, "Invalid car type: " + carTypeName);
            return UNKNOWN;
        }
    }
}
