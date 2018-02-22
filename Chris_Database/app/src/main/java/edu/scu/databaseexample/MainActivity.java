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
        iDH.addNewUser("teubert@gmail.com","Chris Teubert","small_gas");

// 1) create a java calendar instance
        Calendar calendar = Calendar.getInstance();

// 2) get a java.util.Date from the calendar instance.
//    this date will represent the current instant, or "now".
        java.util.Date now = calendar.getTime();

// 3) a java current time (now) instance
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

        long currentTime = currentTimestamp.getTime();

        ImpactDatabaseHelper.GPS gpsPoint = new ImpactDatabaseHelper.GPS(currentTime, 51.5034070, -0.1275920);
        iDH.addNewGPSDataPoint(ImpactDatabaseHelper.emailToUsername("teubert@gmail.com"), gpsPoint);
    }
}
