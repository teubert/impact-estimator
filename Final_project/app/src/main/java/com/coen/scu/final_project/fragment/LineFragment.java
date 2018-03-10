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
public class LineFragment extends Fragment implements UserProfile.UserUpdateInterface, DayTripsSummary.TripUpdateInterface {
    private static final String DEBUG_TAG = "Summary Fragment";

    private Calendar mDate = null;
    UserProfile user;
    String userId;
    List<DayTripsSummary> list = new ArrayList<>();
    List<String> dayList = new ArrayList<>();
    private LineChart mLine;

    boolean userSet = false;
    private ArrayList<Integer> colors = new ArrayList<>();

    public LineFragment() {
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
        View view = inflater.inflate(R.layout.fragment_line, container, false);

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

        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();

        mLine = (LineChart) view.findViewById(R.id.line_chart);
        mLine.getLegend().setEnabled(false);
        mLine.setMinimumHeight(width*3/4);
        Description lineDesc = new Description();
        lineDesc.setText(getString(R.string.line_desc));
        mLine.setDescription(lineDesc);
        mLine.getAxisLeft().setEnabled(true);
        mLine.getAxisLeft().setTextSize(16f);
        mLine.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format(getString(R.string.line_left_label_format), value);
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
        // Prepare data
        List<Entry> lineEntries = new ArrayList<>();
        int i = 1;
        for (DayTripsSummary dayTripsSummary : list) {
            FootprintEstimate estimate = FootprintEstimate.generateEstimate(dayTripsSummary, user);
            lineEntries.add(new Entry((float) i++, (float) estimate.CO2));
        }

        // Create Line Chart
        LineDataSet lineDataSet = new LineDataSet(lineEntries, getString(R.string.line_dataset_label));
        lineDataSet.setDrawFilled(true);
        LineData lineData = new LineData(lineDataSet);
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
}
