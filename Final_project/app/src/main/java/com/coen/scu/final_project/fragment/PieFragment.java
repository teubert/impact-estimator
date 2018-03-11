package com.coen.scu.final_project.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.github.mikephil.charting.components.Description;
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
public class PieFragment extends Fragment implements UserProfile.UserUpdateInterface, DayTripsSummary.TripUpdateInterface {
    private static final String DEBUG_TAG = "Summary Fragment";

    private Calendar mDate = null;
    UserProfile user;
    String userId;
    List<DayTripsSummary> list = new ArrayList<>();
    List<String> dayList = new ArrayList<>();
    private PieChart mChart;

    boolean userSet = false;
    private ArrayList<Integer> colors = new ArrayList<>();

    final boolean HALF_CHART = false;

    public PieFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_pie, container, false);

        mDate = Calendar.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        user = UserProfile.getUserProfileById(userId);
        user.addCallback(this);

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.US);

        Calendar date = mDate;
        dayList.add(dayFormat.format(date.getTime()));
        DayTripsSummary dayTripsSummary = DayTripsSummary.getDayTripsForDay(userId, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayList.add(dayFormat.format(date.getTime()));
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(userId, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayList.add(dayFormat.format(date.getTime()));
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(userId, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayList.add(dayFormat.format(date.getTime()));
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(userId, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayList.add(dayFormat.format(date.getTime()));
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(userId, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayList.add(dayFormat.format(date.getTime()));
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(userId, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        date.add(Calendar.DATE, -1);
        dayList.add(dayFormat.format(date.getTime()));
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(userId, date);
        dayTripsSummary.addCallback(this);
        list.add(dayTripsSummary);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        mChart = (PieChart) view.findViewById(R.id.pie_chart);

        mChart.setCenterText(getString(R.string.pie_chart_center_text));
        mChart.setCenterTextSize(32f);

        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
//        mChart.setMinimumHeight(width*3/4);


        // radius of the center hole in percent of maximum radius
        mChart.setHoleRadius(45f);
        mChart.setTransparentCircleRadius(47f);
        mChart.getLegend().setEnabled(false);
        Description chartDesc = new Description();
        chartDesc.setText(getString(R.string.pie_desc));
        mChart.setDescription(chartDesc);

        if (HALF_CHART) {
            mChart.setMaxAngle(180f); // HALF CHART
            mChart.setRotationAngle(180f);
        }

        return view;
    }

    /**
     *
     */
    @Override
    public void onTripUpdate() {
        if (userSet && isAdded()) {
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
        for (DayTripsSummary dayTripsSummary : list) {
            FootprintEstimate estimate = FootprintEstimate.generateEstimate(dayTripsSummary, user);
            trips       += estimate.trips;
            breathing   += estimate.breathing;
            food        += estimate.food;
            electricity += estimate.electricity;
            products    += estimate.products;
            services    += estimate.services;
        }

        // Create Pie Chart
        ArrayList<PieEntry> entries = new ArrayList<>();
        double total = trips + breathing + food + electricity + products + services;
        entries.add(new PieEntry((float) (trips/total*100.0),        getString(R.string.transport_category_label)));
        entries.add(new PieEntry((float) (food/total*100.0),         getString(R.string.food_category_label)));
        entries.add(new PieEntry((float) (electricity/total*100.0),  getString(R.string.elec_category_label)));
//        entries.add(new PieEntry((float) (breathing/total*100.0),   "Breathing"));
//        entries.add(new PieEntry((float) (products/total*100.0),    "Products"));
//        entries.add(new PieEntry((float) (services/total*100.0),    "Services"));
        entries.add(new PieEntry((float) ((services + products + breathing)/total*100), getString(R.string.home_category_label)));

        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.pie_dataset_label));
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

        // Update
        mChart.invalidate();
    }

    /**
     *
     */
    @Override
    public void onUserUpdate() {
        userSet = true;
        if(!isAdded ())
            return;
        update();
    }

    @Override
    public void onResume() {
        super.onResume();
        for (DayTripsSummary tripsSummary : list) {
            tripsSummary.addCallback(this);
        }
        user.addCallback(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        for (DayTripsSummary tripsSummary : list) {
            tripsSummary.removeCallback(this);
        }
        user.removeCallback(this);
    }
}
