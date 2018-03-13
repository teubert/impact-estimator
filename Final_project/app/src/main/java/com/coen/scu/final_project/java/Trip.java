package com.coen.scu.final_project.java;

/**
 * Created by teubert on 2/27/18.
 */

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Class to contain information for a single trip
 */
@IgnoreExtraProperties
public class Trip {
    private final static String DEBUG_TAG = "Trip";

    private GPSPoint start = null;
    private GPSPoint end = null;
    private String tripId = null;
    private Transportation.TransportMode transport_mode = Transportation.TransportMode.WALK;
    private Transportation.CarType car_type = Transportation.CarType.SMALL_CAR;
    private double distance = 0;
    private FootprintEstimate estimate = null;

    public GPSPoint getStart() {
        return start;
    }

    public GPSPoint getEnd() {
        return end;
    }

    public String getTripId() {
        return tripId;
    }

    public Transportation.TransportMode getTransport_mode() {
        return transport_mode;
    }

    public Transportation.CarType getCar_type() {
        return car_type;
    }

    public double getDistance() {
        return distance;
    }

    public FootprintEstimate getEstimate() {
        return estimate;
    }

    /**
     * Set the starting GPS point
     *
     * @param start The starting GPS Point
     */
    public void setStart(GPSPoint start) {
        this.start = start;
    }

    /**
     * Set the ending GPS point
     *
     * @param end   The end GPS point
     */
    public void setEnd(GPSPoint end) {
        this.end = end;
    }

    /**
     * Set trip id (used in Firebase)
     *
     * @param tripId    The trip id
     */
    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    /**
     * Set transportation mode used
     * note: this triggers re-estimation of CO2
     * @param transport_mode    Transportation used
     */
    public void setTransport_mode(Transportation.TransportMode transport_mode) {
        this.transport_mode = transport_mode;
        this.estimate = estimateImpact();
    }

    /**
     * Set the car type used
     * note: this triggers re-estimation of CO2
     *
     * @param car_type  The car type used
     */
    public void setCar_type(Transportation.CarType car_type) {
        this.car_type = car_type;
        this.estimate = estimateImpact();
    }

    /**
     * Set the distance travelled
     * note: this triggers re-estimation of CO2
     *
     * @param distance  Distance travelled (km)
     */
    public void setDistance(double distance) {
        this.distance = distance;
        this.estimate = estimateImpact();
    }

    /**
     * Set the estimate
     *
     * @param estimate  The footprint estimate
     */
    public void setEstimate(FootprintEstimate estimate) {
        this.estimate = estimate;
    }

    /**
     *  Create an empty trip
     */
    public Trip() {
    }

    /**
     * Estimate the footprint of this trip
     *
     * @return footprint estimate
     */
    public FootprintEstimate estimateImpact() {
        FootprintEstimate impact = new FootprintEstimate(1);

        final double lbsPerKWh = 1.222;
        final double lbsPerGal = 19.6;
        final double miPerkm = 0.621371;

        double gasEstimate = 0;
        double elecEstimate = 0;
        double purchaseEstimate = 0;

        if (transport_mode.getMpg() != 0) { // Uses gas
            gasEstimate = lbsPerGal /* lb/gal */
                    * distance /*km = lb*km/gal */
                    * miPerkm /*mi/km = lb*mi/gal */
                    / transport_mode.getMpg() /*gal/mi = lb*/;
        }

        if (transport_mode.getMpkwh() != 0) { // Uses electric
            elecEstimate = lbsPerKWh * distance / transport_mode.getMpkwh();
        }

        if (transport_mode == Transportation.TransportMode.AUTOMOBILE) {
            if (car_type.getMpg() != 0) {
                // No gas
                gasEstimate = 0;
            } else {
                gasEstimate /= car_type.getMpg();
            }
            if (car_type.getMpkwh() == 0) {
                // No elec
                elecEstimate = 0;
            } else {
                elecEstimate /= car_type.getMpkwh();
            }
            purchaseEstimate = distance * 0.120958; // CO2 for building the car- from
            // https://www.theguardian.com/environment/green-living-blog/2010/sep/23/carbon-footprint-new-car,
            // Used 20tons over 150k miles
        }

        double estimate = gasEstimate + elecEstimate + purchaseEstimate;

        Log.d(DEBUG_TAG, String.format("Estimate (distance: %f) (MPG: %f, MPW: %f) total: %f",
                distance, transport_mode.getMpg(), transport_mode.getMpkwh(), estimate));

        impact.addToEstimate(estimate);
        return impact;
    }

    /**
     *  Create a new trip
     *
     * @param start             Starting GPS point
     * @param end               Ending GPS point
     * @param distance          Distance travelled
     * @param transport_mode    Transportation mode used
     * @param car_type          Car type driven
     */
    public Trip(GPSPoint start, GPSPoint end, double distance,
                Transportation.TransportMode transport_mode,
                @Nullable Transportation.CarType car_type) {
        Log.v(DEBUG_TAG, "Creating Trip Object");
        this.start          = start;
        this.end            = end;
        this.distance       = distance;
        this.transport_mode = transport_mode;
        this.car_type       = car_type;
        this.estimate       = estimateImpact();
    }

    /**
     *  Create a new trip
     *
     * @param start             Starting GPS point
     * @param end               Ending GPS point
     * @param distance          Distance travelled
     * @param transport_mode    Transportation mode used
     * @param car_type          Car type driven
     * @param tripId            Id for trip
     * @param estimate          Footprint estimate for trip
     */
    public Trip(@Nullable GPSPoint start, @Nullable GPSPoint end, double distance,
                Transportation.TransportMode transport_mode,
                @Nullable Transportation.CarType car_type,
                String tripId, FootprintEstimate estimate) {
        Log.v(DEBUG_TAG, "Creating Trip Object");
        this.start          = start;
        this.end            = end;
        this.distance       = distance;
        this.transport_mode = transport_mode;
        this.car_type       = car_type;
        this.tripId         = tripId;
        this.estimate       = estimate;
    }
}
