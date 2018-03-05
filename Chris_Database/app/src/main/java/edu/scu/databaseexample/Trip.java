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

    public GPSPoint start;
    public GPSPoint end;
    public String tripId = null;
    public Transportation.TransportMode transport_mode;
    public Transportation.CarType car_type;

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
        this.estimate = getEstimate();
    }

    public void setCar_type(Transportation.CarType car_type) {
        this.car_type = car_type;
        this.estimate = getEstimate();
    }

    public void setDistance(double distance) {
        this.distance = distance;
        this.estimate = getEstimate();
    }

    public void setEstimate(FootprintEstimate estimate) {
        this.estimate = estimate;
    }

    public double distance;
    public FootprintEstimate estimate;

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

        double gasEstimate = distance * transport_mode.getMpg();
        double elecEstimate = distance * transport_mode.getMpw();
        if (transport_mode == Transportation.TransportMode.AUTOMOBILE) {
            gasEstimate *= car_type.getMpg();
            elecEstimate *= car_type.getMpw();
        }
        // TODO(CT): Include electricity production in elec estimate
        // TODO(CT): Add cost of owning car

        impact.addToEstimate(gasEstimate + elecEstimate);
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