package edu.scu.databaseexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImpactDatabaseHelper iDH = new ImpactDatabaseHelper();
        iDH.addNewUser("teubert@gmail.com","Chris Teubert", Transportation.CarType.SMALL_CAR);

// 1) create a java calendar instance
        Calendar calendar = Calendar.getInstance();

// 2) get a java.util.Date from the calendar instance.
//    this date will represent the current instant, or "now".
        java.util.Date now = calendar.getTime();

// 3) a java current time (now) instance
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

        long currentTime = currentTimestamp.getTime();

        Transportation.GPS gpsPoint = new Transportation.GPS(currentTime, 51.5034070, -0.1275920);
        iDH.addNewGPSDataPoint(gpsPoint);

        iDH.addTrip(new Transportation.Trip(gpsPoint,
                gpsPoint,
                Transportation.TransportMode.AUTOMOBILE,
                Transportation.CarType.HYBRID_CAR));

        // TODO(CT): Test the rest
        // TODO(CT): Move to unit tests
    }
}
