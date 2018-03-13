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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LineFragment extends Fragment implements UserProfile.UserUpdateInterface, DayTripsSummary.TripUpdateInterface {
    private static final String DEBUG_TAG = "Summary Fragment";

    private LineChart mLine;

    boolean userSet = false;
    public LineFragment() {
        // Required empty public constructor
    }

    /**
     * Create the list fragment view, involves setting callbacks and creating chart
     *
     * @param inflater              Layout inflater
     * @param container             Viewgroup container
     * @param savedInstanceState    Saved Instance State
     * @return  Inflated View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(DEBUG_TAG, "onCreateView: Creating Line Fragment");
        View view = inflater.inflate(R.layout.fragment_line, container, false);

        for (DayTripsSummary dayTripsSummary : SummaryFragment.list) {
            dayTripsSummary.addCallback(this);
        }
        SummaryFragment.user.addCallback(this);

        mLine = view.findViewById(R.id.line_chart);
        mLine.getLegend().setEnabled(true);
        mLine.getLegend().setTextSize(16f);
        mLine.getDescription().setEnabled(false);
        mLine.getAxisLeft().setEnabled(true);
        mLine.getAxisLeft().setTextSize(20f);
        mLine.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format(getString(R.string.line_left_label_format), value);
            }
        });
        mLine.getAxisRight().setEnabled(false);
        mLine.setExtraBottomOffset(4);
        XAxis xAxis = mLine.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(16f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return SummaryFragment.dayList.get((int) value-1);
            }
        });
        update();

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
        if (!isAdded()) {
            // Not attached to activity
            return;
        }
        Log.d(DEBUG_TAG, "update: Updating Line Chart");

        // Prepare data
        List<Entry> lineEntries = new ArrayList<>();
        int i = 1;
        for (DayTripsSummary dayTripsSummary : SummaryFragment.list) {
            FootprintEstimate estimate = FootprintEstimate.generateEstimate(dayTripsSummary,
                    SummaryFragment.user);
            lineEntries.add(new Entry((float) i++, (float) estimate.CO2));
        }

        // Create Line Chart
        LineDataSet lineDataSet = new LineDataSet(lineEntries, getString(R.string.line_dataset_label));
        lineDataSet.setDrawFilled(true);

        List<Entry> aveLineEntries = new ArrayList<>();
        aveLineEntries.add(new Entry((float) 1, (float) FootprintEstimate.AVERAGE_US_CO2));
        aveLineEntries.add(new Entry((float) 7, (float) FootprintEstimate.AVERAGE_US_CO2));
        LineDataSet aveLineDataSet = new LineDataSet(aveLineEntries, "average");
        aveLineDataSet.setColor(Color.BLUE);
        aveLineDataSet.setLineWidth(3);

        List<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);
        iLineDataSets.add(aveLineDataSet);

        LineData lineData = new LineData(iLineDataSets);
        lineData.setDrawValues(false);
        mLine.setData(lineData);

        // Update
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
