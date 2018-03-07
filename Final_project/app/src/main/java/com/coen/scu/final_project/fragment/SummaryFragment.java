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
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
    List<String> dayList = new ArrayList<>();
    private PieChart mChart;
    private LineChart mLine;

    boolean userSet = false;
    private ArrayList<Integer> colors = new ArrayList<>();

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
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDate = Calendar.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        id = firebaseUser.getUid();
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

        user = UserProfile.getUserProfileById(id);
        user.addCallback(this);

        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);

        Calendar date = mDate;
        dayList.add(dayFormat.format(date.getTime()));
        DayTripsSummary dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayList.add(dayFormat.format(date.getTime()));
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayList.add(dayFormat.format(date.getTime()));
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayList.add(dayFormat.format(date.getTime()));
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayList.add(dayFormat.format(date.getTime()));
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayList.add(dayFormat.format(date.getTime()));
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayList.add(dayFormat.format(date.getTime()));
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(id, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

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
        mLine.getDescription().setEnabled(false);
        mLine.getLegend().setEnabled(false);
        mLine.setMinimumHeight(width*3/4);
        mLine.getAxisLeft().setEnabled(true);
        mLine.getAxisLeft().setTextSize(16f);
        mLine.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format("%.0f kg", value);
            }
        });
        mLine.getAxisRight().setEnabled(false);
        XAxis xAxis = mLine.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(20f);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(75);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dayList.get((int) value-1);
            }
        });

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
        // Initialize Data
        double trips = 0;
        double breathing = 0;
        double food = 0;
        double electricity = 0;
        double products = 0;
        double services = 0;

        // Prepare data
        List<Entry> lineEntries = new ArrayList<>();
        int i = 1;
        for (DayTripsSummary dayTripsSummary : list) {
            FootprintEstimate estimate = FootprintEstimate.generateEstimate(dayTripsSummary, user);
            trips       += estimate.trips;
            breathing   += estimate.breathing;
            food        += estimate.food;
            electricity += estimate.electricity;
            products    += estimate.products;
            services    += estimate.services;
            lineEntries.add(new Entry((float) i++, (float) estimate.CO2));
        }

        // Create Pie Chart
        ArrayList<PieEntry> entries = new ArrayList<>();
        double total = trips + breathing + food + electricity + products + services;
        entries.add(new PieEntry((float) (trips/total*100.0),       "Transport"));
        entries.add(new PieEntry((float) (food/total*100.0),        "Food"));
        entries.add(new PieEntry((float) (electricity/total*100.0), "Electricity"));
//        entries.add(new PieEntry((float) (breathing/total*100.0),   "Breathing"));
//        entries.add(new PieEntry((float) (products/total*100.0),    "Products"));
//        entries.add(new PieEntry((float) (services/total*100.0),    "Services"));
        entries.add(new PieEntry((float) ((services + products + breathing)/total*100), "Home"));

        PieDataSet dataSet = new PieDataSet(entries, "Week Results");
        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(16f);
        data.setValueTextColor(Color.BLACK);

        mChart.setData(data);

        // Create Line Chart
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "total");
        lineDataSet.setDrawFilled(true);
        LineData lineData = new LineData(lineDataSet);
        lineData.setDrawValues(false);
        mLine.setData(lineData);

        // Update
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
