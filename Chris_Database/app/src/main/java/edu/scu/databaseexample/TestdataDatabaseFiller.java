package edu.scu.databaseexample;

import android.util.Log;
import android.view.View;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by teubert on 3/3/18.
 */


public class TestdataDatabaseFiller {
    private static final String DEBUG_TAG = "Testdata";
    /**
     * get the current timestamp in "unix time"
     *
     * @return Current timestamp in unix time
     */
    static private long getCurrentTimestamp() {
        Log.v(DEBUG_TAG, "Getting current timestamp");

        // 1) create a java calendar instance
        Calendar calendar = Calendar.getInstance();

        // 2) get a java.util.Date from the calendar instance.
        //    this date will represent the current instant, or "now".
        java.util.Date now = calendar.getTime();

        // 3) a java current time (now) instance
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

        return currentTimestamp.getTime();
    }

    static public void fill() {
        // Fill Users
        UserProfile.addNewUserProfile("teubert@gmail.com","Chris Teubert", Transportation.CarType.SMALL_CAR);
        UserProfile.addNewUserProfile("testUser@notanemail.com", "Test User", Transportation.CarType.ELECTRIC_CAR);
        UserProfile.addNewUserProfile("testUser2@notanemail.com", "Test User 2", Transportation.CarType.SUV);

        // Fill Trips
        // 1) create a java calendar instance
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24 - calendar.get(Calendar.HOUR));

        // 2) get a java.util.Date from the calendar instance.
        //    this date will represent the current instant, or "now".
        java.util.Date now = calendar.getTime();

        // 3) a java current time (now) instance
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

        GPSPoint start = new GPSPoint(currentTimestamp.getTime()-16*60*60*1000, -121.9, 37.39);
        GPSPoint end = new GPSPoint(start.timestamp + 75*60 * 1000, -121.89, 37.38);
        Trip newTrip = new Trip(start, end, 20, Transportation.TransportMode.AUTOMOBILE, Transportation.CarType.SUV);
        DayTripsSummary.append("teubert_gmail_com",newTrip);

        newTrip.setStart(newTrip.getEnd());
        GPSPoint gpsEnd = newTrip.getEnd();
        gpsEnd.lat -= 0.04;
        gpsEnd.timestamp += 180*60*1000;
        newTrip.setEnd(gpsEnd);
        newTrip.setTransport_mode(Transportation.TransportMode.AIRCRAFT);
        DayTripsSummary.append("teubert_gmail_com",newTrip);

        newTrip.setStart(newTrip.getEnd());
        gpsEnd.lat -= 0.01;
        gpsEnd.timestamp += 25*60*1000;
        newTrip.setEnd(gpsEnd);
        newTrip.setTransport_mode(Transportation.TransportMode.TRAIN);
        DayTripsSummary.append("teubert_gmail_com",newTrip);

        newTrip.setStart(newTrip.getEnd());
        gpsEnd.lat -= 0.01;
        gpsEnd.timestamp += 10*60*1000;
        newTrip.setEnd(gpsEnd);
        newTrip.setTransport_mode(Transportation.TransportMode.BIKE);
        DayTripsSummary.append("teubert_gmail_com",newTrip);

        newTrip.setStart(newTrip.getEnd());
        gpsEnd.lat -= 0.01;
        gpsEnd.timestamp += 25*60*1000;
        newTrip.setEnd(gpsEnd);
        DayTripsSummary.append("teubert_gmail_com",newTrip);

        newTrip.setStart(newTrip.getEnd());
        gpsEnd.lat -= 0.01;
        gpsEnd.timestamp += 45*60*1000;
        newTrip.setEnd(gpsEnd);
        newTrip.setTransport_mode(Transportation.TransportMode.BOAT);
        DayTripsSummary.append("teubert_gmail_com",newTrip);

        newTrip.setStart(newTrip.getEnd());
        gpsEnd.lat -= 0.01;
        gpsEnd.timestamp += 60*60*1000;
        newTrip.setEnd(gpsEnd);
        newTrip.setTransport_mode(Transportation.TransportMode.WALK);
        DayTripsSummary.append("teubert_gmail_com",newTrip);

        newTrip.setStart(newTrip.getEnd());
        gpsEnd.lat -= 0.01;
        gpsEnd.timestamp += 25*60*1000;
        newTrip.setEnd(gpsEnd);
        DayTripsSummary.append("teubert_gmail_com",newTrip);
    }
}
