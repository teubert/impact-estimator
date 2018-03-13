package com.coen.scu.final_project.java;

import android.util.Log;

/**
 * Class to contain an impact estimate (initially just CO2)
 *
 * Created by teubert on 2/26/18.
 */
public class FootprintEstimate {
    private static final String DEBUG_TAG = "FootprintEstimate";

    //https://www.sciencedaily.com/releases/2008/04/080428120658.htm
    final double lbsPerGal = 19.6;

    // https://www.fhwa.dot.gov/ohim/onh00/bar8.htm
    // Average distance 13,476 mi per year
    // = 36.9 mi/day
    // Assuming 30 mi/gal
    // 1.23 gal/day
    // Transport = 1.23*19.6 + 0.120958 * 36.9
    // = 28.59 kg/day
    // + 7.666 + 8.214
    // = 44.47
    // + 1.0437f
    // = 45.5137
    // + 6.85 +2.4787 * 2414 * 1e-8
    // = 52.364
    // + 0.55428988 * 29.4757
    // = 68.7

    public static final double AVERAGE_US_CO2 = 68.7; //54.79;
    public static final double AVERAGE_CO2 = 13.74; // 10.96;

    // It's own class for extendability
    public double CO2 = 0;
    // Future versions could include other metrics

    public double trips = 0;
    public double breathing = 0;
    public double food = 0;
    public double electricity = 0;
    public double products = 0;
    public double services = 0;
    public double reductions = 0;

    public long nDays;

    public static FootprintEstimate generateEstimate(DayTripsSummary dayTrips, UserProfile user) {
        Log.d(DEBUG_TAG, "Producing Estimate");

        FootprintEstimate footprint = new FootprintEstimate(1);

        // http://shrinkthatfootprint.com/what-is-your-carbon-footprint

        footprint.trips = 0;
        // Add trips portion
        for (Trip trip : dayTrips.trips) {
            footprint.trips += trip.getEstimate().CO2;
        }
        footprint.CO2 += footprint.trips;

        // Add breathing
        // From http://www.slate.com/articles/news_and_politics/explainer/2009/08/7_billion_carbon_sinks.html
        final double humanCO2Breathing = 1.0437f; // kg
        footprint.breathing = humanCO2Breathing;
        footprint.CO2 += footprint.breathing;

        // Add food
        // 	= Σ[CO2 from farm] + Σ[CO2 from transport]
        //  - Transport Decreased through farmers market
        //  - Farm decreased by going vegetarian/vegan
        final double foodCO2 = user.getDiet().getEmissions(); // kg/day

        // https://www.npr.org/sections/thesalt/2011/12/31/144478009/the-average-american-ate-literally-a-ton-this-year
        final double food_consumption   = 2.4787; // kg food/day

        // Freight
        // https://carbonfund.org/how-we-calculate/
        final double air_cargo          = 8.196e-7;     // kg CO2/kg-km
        final double truck              = 9.1e-8;       // kg CO2/kg-km
        final double train              = 1.5e-8;       // kg CO2/kg-km
        final double sea                = 3.741e-8;     // kg CO2/kg-km

        // https://cuesa.org/learn/how-far-does-your-food-travel-get-your-plate
        final double far_distance       = 3500;      // km - guess
        final double average_distance   = 2414;      // km - how far food travels
        final double local_distance     = 125;       // km

        // Frieght impact (kg CO2/ kg food)
        final double freight_far        = far_distance * (air_cargo + train + sea)/3; // average // kg/kg
        final double freight_average    = average_distance * (air_cargo + train + sea)/3; // average // kg/kg
        final double freight_local      = local_distance * truck;  // local typically truck // kg/kg

        footprint.food = foodCO2 + freight_average * food_consumption;
        footprint.CO2 += footprint.food;

        // Add electricity
        //= Σ[Source efficiency]*[Percentage from source]*[Total Amount]
       // - Decreased through more efficient sources (get solar panel, etc)
        // - Decreased by degrading amount
        final double kgPerKWh = 0.55428988;
        final double averageKWh = 29.4757; // from: https://www.eia.gov/tools/faqs/faq.php?id=97&t=3
        footprint.electricity = averageKWh*kgPerKWh;
        footprint.CO2 += footprint.electricity;

        // Add other sources
        // http://shrinkthatfootprint.com/what-is-your-carbon-footprint
        footprint.products = 7.666; //kg/day
        footprint.services = 8.214; //kg/day
        footprint.CO2 += footprint.products + footprint.services;

        // Reductions
        footprint.reductions  = 0;
        footprint.CO2 -= footprint.reductions;

        return footprint;
    }

    /**
     * Create an estimate
     *
     * @param CO2   CO2 Use
     * @param nDays Number of days impact estimate is over
     */
    public FootprintEstimate(double CO2, long nDays) {
        Log.v(DEBUG_TAG, "Creating FootprintEstimate Object");
        this.CO2 = CO2;
        this.nDays = nDays;
    }

    /**
     * Create an empty estimate
     *
     * @param nDays Number of days impact estimate is over
     */
    public FootprintEstimate(long nDays) {
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
