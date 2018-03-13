package com.coen.scu.final_project.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coen.scu.final_project.R;
import com.coen.scu.final_project.java.DayTripsSummary;
import com.coen.scu.final_project.java.FootprintEstimate;
import com.coen.scu.final_project.java.UserProfile;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PieFragment extends Fragment implements UserProfile.UserUpdateInterface, DayTripsSummary.TripUpdateInterface {
    private static final String DEBUG_TAG = "Summary Fragment";

    private PieChart mChart;

    private boolean userSet = false;

    public PieFragment() {
        // Required empty public constructor
    }

    /**
     * Create the pie fragment view, involves setting callbacks and creating chart
     *
     * @param inflater              Layout inflater
     * @param container             Viewgroup container
     * @param savedInstanceState    Saved Instance State
     * @return  Inflated View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "onCreateView: Creating Pie Fragment View");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pie, container, false);


        for (DayTripsSummary dayTripsSummary : SummaryFragment.list) {
            dayTripsSummary.addCallback(this);
        }
        SummaryFragment.user.addCallback(this);

        mChart = view.findViewById(R.id.pie_chart);

        mChart.setCenterText(getString(R.string.pie_chart_center_text));
        mChart.setCenterTextSize(32f);

        // radius of the center hole in percent of maximum radius
        mChart.setHoleRadius(45f);
        mChart.setTransparentCircleRadius(47f);
        mChart.getLegend().setEnabled(false);
        mChart.getDescription().setEnabled(false);

//        final boolean HALF_CHART = false;
//        if (HALF_CHART) {
//            mChart.setMaxAngle(180f); // HALF CHART
//            mChart.setRotationAngle(180f);
//        }

        update();

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
        if (!isAdded()) {
            // Not attached to activity
            return;
        }
        Log.d(DEBUG_TAG, "update: Updating Pie Chart");

        // Initialize Data
        double trips = 0;
        double breathing = 0;
        double food = 0;
        double electricity = 0;
        double products = 0;
        double services = 0;

        // Prepare data
        for (DayTripsSummary dayTripsSummary : SummaryFragment.list) {
            FootprintEstimate estimate = FootprintEstimate.generateEstimate(dayTripsSummary, SummaryFragment.user);
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
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);
        dataSet.setColors(SummaryFragment.colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(16f);
        data.setValueTextColor(Color.BLACK);

        mChart.setData(data);
        mChart.setEntryLabelColor(Color.BLACK);
        mChart.setEntryLabelTextSize(16f);

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
        for (DayTripsSummary tripsSummary : SummaryFragment.list) {
            tripsSummary.addCallback(this);
        }
        SummaryFragment.user.addCallback(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        for (DayTripsSummary tripsSummary : SummaryFragment.list) {
            tripsSummary.removeCallback(this);
        }
        SummaryFragment.user.removeCallback(this);
    }
}
