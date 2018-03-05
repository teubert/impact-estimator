// Arrow icons made by Dave Gandy at https://www.flaticon.com/free-icon/arrowhead-pointing-to-the-right_25446#term=right arrow&page=1&position=9

package edu.scu.databaseexample;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "Main Activity";
    private static final String ARG_DATE = "date";

    private Calendar mDate;

    public void onDateUpdate(Calendar date) {
        Log.v(DEBUG_TAG, "onDateUpdate: Creating fragment and commiting");
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        LogFragment fragment = LogFragment.newInstance(date);
        ft.replace(R.id.flContent, fragment).commit();
        Log.v(DEBUG_TAG, "onDateUpdate: done");
    }

    /**
     * get the current timestamp in "unix time"
     *
     * @return Current timestamp in unix time
     */
    static private long getTimestamp(Calendar calendar) {
        Log.v(DEBUG_TAG, "Getting current timestamp");

        // 2) get a java.util.Date from the calendar instance.
        //    this date will represent the current instant, or "now".
        java.util.Date now = calendar.getTime();

        // 3) a java current time (now) instance
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

        return currentTimestamp.getTime();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(DEBUG_TAG, "onCreate: Creating log view");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            long date = savedInstanceState.getLong(ARG_DATE);
            mDate = Calendar.getInstance();
            mDate.setTimeInMillis(date);
            Log.i(DEBUG_TAG, "onCreate: Restoring last date (" + mDate + ")");
            onDateUpdate(mDate);
        } else {
            Log.v(DEBUG_TAG, "onCreate: No previous date");
            onDateUpdate(Calendar.getInstance());
        }
        Log.v(DEBUG_TAG, "onCreate: done");
        TestdataDatabaseFiller.fill();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(DEBUG_TAG, "onSaveInstanceState: Saving instance state");
        super.onSaveInstanceState(outState);
        outState.putLong(ARG_DATE, getTimestamp(mDate));
        Log.v(DEBUG_TAG, "onSaveInstanceState: done");
    }
}
