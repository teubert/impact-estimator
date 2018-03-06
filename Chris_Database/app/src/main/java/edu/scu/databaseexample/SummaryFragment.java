package edu.scu.databaseexample;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends android.app.Fragment implements UserProfile.UserUpdateInterface, DayTripsSummary.TripUpdateInterface {
    // TODO: Rename parameter arguments, choose names that match

    private static final String DEBUG_TAG = "Summary Fragment";
    private static final String ARG_DATE = "date";

    private Calendar mDate = null;
    UserProfile user;
    String id;
    Map<DayTripsSummary, TextView> map = new HashMap<>();

    TextView todayView;
    boolean userSet = false;

    public void onDateUpdate(Calendar date) {
        id = "teubert_gmail_com";
        user = UserProfile.getUserProfileById(id);
        user.addCallback(this);
        mDate = date;
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

    public SummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param date Parameter 1.
     * @return A new instance of fragment SummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SummaryFragment newInstance(Calendar date) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DATE, getTimestamp(date));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            long date = getArguments().getLong(ARG_DATE);
            mDate = Calendar.getInstance();
            mDate.setTimeInMillis(date);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_summary, container, false);

        if (mDate == null) {
            if (savedInstanceState != null) {
                long date = savedInstanceState.getLong(ARG_DATE);
                mDate = Calendar.getInstance();
                mDate.setTimeInMillis(date);
                Log.i(DEBUG_TAG, "onCreate: Restoring last date (" + mDate + ")");
            } else {
                Log.v(DEBUG_TAG, "onCreate: No previous date");
                mDate = Calendar.getInstance();
            }
        }
        onDateUpdate(mDate);

        Calendar date = mDate;
        DayTripsSummary dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        map.put(dayTripsSummary, (TextView) view.findViewById(R.id.today));

        date.add(Calendar.DATE, -1);
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        map.put(dayTripsSummary, (TextView) view.findViewById(R.id.yesterday));

        date.add(Calendar.DATE, -1);
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        map.put(dayTripsSummary, (TextView) view.findViewById(R.id.two_days_ago));

        date.add(Calendar.DATE, -1);
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        map.put(dayTripsSummary, (TextView) view.findViewById(R.id.three_days_ago));

        date.add(Calendar.DATE, -1);
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        map.put(dayTripsSummary, (TextView) view.findViewById(R.id.four_days_ago));

        date.add(Calendar.DATE, -1);
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        map.put(dayTripsSummary, (TextView) view.findViewById(R.id.five_days_ago));

        date.add(Calendar.DATE, -1);
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        map.put(dayTripsSummary, (TextView) view.findViewById(R.id.six_days_ago));


        return view;
    }

    @Override
    public void onTripUpdate() {
        if (userSet) {
            update();
        }
    }

    public void update() {
        for (Map.Entry<DayTripsSummary, TextView> mapEntry : map.entrySet()) {
            FootprintEstimate estimate = FootprintEstimate.generateEstimate(mapEntry.getKey(), user);
            mapEntry.getValue().setText(String.format("%.5f", estimate.CO2));
        }
    }

    @Override
    public void onUserUpdate() {
        userSet = true;
        update();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(DEBUG_TAG, "onSaveInstanceState: Saving instance state");
        super.onSaveInstanceState(outState);
        outState.putLong(ARG_DATE, getTimestamp(mDate));
        Log.v(DEBUG_TAG, "onSaveInstanceState: done");
    }
}
