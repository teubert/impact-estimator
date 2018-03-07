package com.coen.scu.final_project.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coen.scu.final_project.R;
import com.coen.scu.final_project.java.DayTripsSummary;
import com.coen.scu.final_project.java.FootprintEstimate;
import com.coen.scu.final_project.java.UserProfile;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SummaryFragment extends Fragment implements UserProfile.UserUpdateInterface, DayTripsSummary.TripUpdateInterface {
    // TODO: Rename parameter arguments, choose names that match

    private static final String DEBUG_TAG = "Summary Fragment";

    private static final String ARG_DATE = "date";
    private static final String ARG_USER = "user";

    private Calendar mDate = null;
    UserProfile user;
    String id;
    List<DayTripsSummary> list = new ArrayList<>();
    private PieChart mChart;
    private LineChart mLine;

    boolean userSet = false;

    final boolean HALF_CHART = false;

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

    /**
     *
     */
    public SummaryFragment() {
        // Required empty public constructor
    }

    public static SummaryFragment newInstance() {
        return new SummaryFragment();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param date Parameter 1.
     * @return A new instance of fragment SummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SummaryFragment newInstance(Calendar date, String user) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DATE, getTimestamp(date));
        args.putString(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDate = Calendar.getInstance();
        id = "teubert_gmail_com";
        if (getArguments() != null) {
            long date = getArguments().getLong(ARG_DATE);
            mDate.setTimeInMillis(date);
            id = getArguments().getString(ARG_USER);
        }
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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

        id = "teubert_gmail_com";
        user = UserProfile.getUserProfileById(id);
        user.addCallback(this);

        Calendar date = mDate;
        DayTripsSummary dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        mChart = (PieChart) view.findViewById(R.id.chart1);
        mChart.getDescription().setEnabled(false);

        mChart.setCenterText("CO2e");
        mChart.setCenterTextSize(32f);

        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        mChart.setMinimumHeight(width*3/4);

        // radius of the center hole in percent of maximum radius
        mChart.setHoleRadius(45f);
        mChart.setTransparentCircleRadius(47f);
        mChart.getLegend().setEnabled(false);

        mLine = (LineChart) view.findViewById(R.id.chart2);
        mLine.setMinimumHeight(width*2/3);

        if (HALF_CHART) {
//        mChart.setMaxAngle(180f); // HALF CHART
//        mChart.setRotationAngle(180f);
        }

        return view;
    }

    /**
     *
     */
    @Override
    public void onTripUpdate() {
        if (userSet) {
            update();
        }
    }

    /**
     *
     */
    public void update() {
        double trips = 0;
        double breathing = 0;
        double food = 0;
        double electricity = 0;
        double products = 0;
        double services = 0;
        List<Entry> lineEntries = new ArrayList<>();
        int i = 0;
        for (DayTripsSummary dayTripsSummary : list) {
            FootprintEstimate estimate = FootprintEstimate.generateEstimate(dayTripsSummary, user);
            trips       += estimate.trips;
            breathing   += estimate.breathing;
            food        += estimate.food;
            electricity += estimate.electricity;
            products    += estimate.products;
            services    += estimate.services;
            lineEntries.add(new Entry((float) ++i, (float) estimate.CO2));
        }
        ArrayList<PieEntry> entries = new ArrayList<>();
        double total = trips + breathing + food + electricity + products + services;
        entries.add(new PieEntry((float) (trips/total*100.0),       "Transport"));
        entries.add(new PieEntry((float) (breathing/total*100.0),   "Breathing"));
        entries.add(new PieEntry((float) (food/total*100.0),        "Food"));
        entries.add(new PieEntry((float) (electricity/total*100.0), "Electricity"));
        entries.add(new PieEntry((float) (products/total*100.0),    "Products"));
        entries.add(new PieEntry((float) (services/total*100.0),    "Services"));
        ArrayList<Integer> colors = new ArrayList<>();

        // TODO(CT): Combine categories

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "total");
        LineData lineData = new LineData(lineDataSet);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        PieDataSet dataSet = new PieDataSet(entries, "Week Results");
        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        mLine.setData(lineData);
        mChart.invalidate();
        mLine.invalidate();
    }

    /**
     *
     */
    @Override
    public void onUserUpdate() {
        userSet = true;
        update();
    }

    /**
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(DEBUG_TAG, "onSaveInstanceState: Saving instance state");
        super.onSaveInstanceState(outState);
        outState.putLong(ARG_DATE, getTimestamp(mDate));
        Log.v(DEBUG_TAG, "onSaveInstanceState: done");
    }
}
