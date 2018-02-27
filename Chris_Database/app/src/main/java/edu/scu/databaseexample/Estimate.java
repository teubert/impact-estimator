package edu.scu.databaseexample;

import android.util.Log;

/**
 * Class to contain an impact estimate (initially just CO2)
 *
 * Created by teubert on 2/26/18.
 */
public class Estimate {
    private static final String DEBUG_TAG = "Estimate";

    // It's own class for extendability
    public double CO2 = 0;
    // Future versions could include other metrics

    public long nDays;

    public Estimate() {
        // Default constructor required for calls to DataSnapshot.getValue(Estimate.class)
    }

    /**
     * Create an estimate
     *
     * @param CO2   CO2 Use
     * @param nDays Number of days impact estimate is over
     */
    public Estimate(double CO2, long nDays) {
        Log.v(DEBUG_TAG, "Creating Estimate Object");
        this.CO2 = CO2;
        this.nDays = nDays;
    }

    /**
     * Create an empty estimate
     *
     * @param nDays Number of days impact estimate is over
     */
    public Estimate(long nDays) {
        this.nDays = nDays;
    }

    /**
     * Add CO2 use to estimate
     *
     * @param CO2Addition   CO2 impact to add to estimate
     */
    public void addToEstimate(double CO2Addition) {
        this.CO2 += CO2Addition;
    }
}
