package edu.scu.databaseexample;

/**
 * Created by teubert on 2/27/18.
 */

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Class to contain information for a single trip
 */
@IgnoreExtraProperties
public class Trip {
    private final static String DEBUG_TAG = "Trip";

    private GPSPoint start;
    private GPSPoint end;
    private String tripId = null;
    private Transportation.TransportMode transport_mode = Transportation.TransportMode.WALK;
    private Transportation.CarType car_type = Transportation.CarType.SMALL_CAR;
    private double distance = 0;
    private FootprintEstimate estimate;

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

    public void setStart(GPSPoint start) {
        this.start = start;
    }

    public void setEnd(GPSPoint end) {
        this.end = end;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public void setTransport_mode(Transportation.TransportMode transport_mode) {
        this.transport_mode = transport_mode;
        this.estimate = estimateImpact();
    }

    public void setCar_type(Transportation.CarType car_type) {
        this.car_type = car_type;
        this.estimate = estimateImpact();
    }

    public void setDistance(double distance) {
        this.distance = distance;
        this.estimate = estimateImpact();
    }

    public void setEstimate(FootprintEstimate estimate) {
        this.estimate = estimate;
    }

    /**
     *
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

        double gasEstimate = 0;
        double elecEstimate = 0;
        double purchaseEstimate = 0;

        if (transport_mode.getMpg() != 0) {
            gasEstimate = lbsPerGal * distance / transport_mode.getMpg();
        }
        if (transport_mode.getMpw() != 0) {
            elecEstimate = lbsPerKWh * distance / transport_mode.getMpw();
        }

        if (transport_mode == Transportation.TransportMode.AUTOMOBILE) {
            if (car_type.getMpg() == 0) {
                // No gas
                gasEstimate = 0;
            } else {
                gasEstimate /= car_type.getMpg();
            }
            if (car_type.getMpw() == 0) {
                // No elec
                elecEstimate = 0;
            } else {
                elecEstimate /= car_type.getMpw();
            }
            purchaseEstimate = distance * 0.120958; // CO2 for building the car- from
            // https://www.theguardian.com/environment/green-living-blog/2010/sep/23/carbon-footprint-new-car,
            // Used 20tons over 150k miles
        }

        double estimate = gasEstimate + elecEstimate + purchaseEstimate;

        Log.d(DEBUG_TAG, String.format("Estimate (distance: %f) (MPG: %f, MPW: %f) total: %f",
                distance, transport_mode.getMpg(), transport_mode.getMpw(), estimate));

        impact.addToEstimate(estimate);
        return impact;
    }

    /**
     *
     *
     * @param start
     * @param end
     * @param distance
     * @param transport_mode
     * @param car_type
     */
    public Trip(GPSPoint start, GPSPoint end, double distance,
                Transportation.TransportMode transport_mode,
                Transportation.CarType car_type) {
        Log.v(DEBUG_TAG, "Creating Trip Object");
        this.start          = start;
        this.end            = end;
        this.distance       = distance;
        this.transport_mode = transport_mode;
        this.car_type       = car_type;
        this.estimate       = estimateImpact();
    }

    /**
     *
     *
     * @param start
     * @param end
     * @param distance
     * @param transport_mode
     * @param car_type
     * @param tripId
     * @param estimate
     */
    public Trip(GPSPoint start, GPSPoint end, double distance,
                Transportation.TransportMode transport_mode,
                Transportation.CarType car_type, String tripId, FootprintEstimate estimate) {
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