package com.coen.scu.final_project.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coen.scu.final_project.R;
import com.coen.scu.final_project.java.DayTripsSummary;
import com.coen.scu.final_project.java.UserProfile;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SummaryFragment extends Fragment implements TwoChartFragment.ToggleChartFullscreenInterface {
    private static final String DEBUG_TAG = "Summary Fragment";
    private static final String FULL_KEY = "Full";
    private static final String ACTIVE_KEY = "Active";

    private static boolean mFullscreen = false;
    private static Chart mActiveChart = null;
    static ArrayList<Integer> colors = new ArrayList<>();
    static List<String> dayList = new ArrayList<>();
    static Calendar mDate = null;
    static UserProfile user = null;
    static List<DayTripsSummary> list = new ArrayList<>();

    public enum Chart {
        PIE, LINE
    }

    @Override
    public void toggle(Chart chart) {
        mFullscreen = !mFullscreen;
        mActiveChart = chart;
        draw();
    }

    private void draw() {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (mFullscreen && mActiveChart != null) {
            switch (mActiveChart) {
                case PIE:
                case LINE:
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    ft.replace(R.id.parent_frag, SingleChartFragment.newInstance(mActiveChart));
                    break;

                default:
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    ft.replace(R.id.parent_frag, new TwoChartFragment());
                    break;
            }
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            ft.replace(R.id.parent_frag, new TwoChartFragment());
        }
        ft.commit();
    }

    /**
     * Construct a new summary fragment
     */
    public SummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Get newInstance of Summary Fragment, same as 'new SummaryFragment()'
     *
     * @return  New instance of a SummaryFragment
     */
    public static SummaryFragment newInstance() {
        return new SummaryFragment();
    }

    /**
     * Create the summary fragment view, involves setting callbacks and creating chart
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
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        if (mDate == null) {
            mDate = Calendar.getInstance();
        }
        if (user == null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            user = UserProfile.getUserProfileById(userId);
        }

        if (list.isEmpty()) {
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.US);

            Calendar date = mDate;
            dayList.add(dayFormat.format(date.getTime()));
            DayTripsSummary dayTripsSummary = DayTripsSummary.getDayTripsForDay(user.getId(), date);
            list.add(dayTripsSummary);

            date.add(Calendar.DATE, -1);
            dayList.add(dayFormat.format(date.getTime()));
            dayTripsSummary = DayTripsSummary.getDayTripsForDay(user.getId(), date);
            list.add(dayTripsSummary);

            date.add(Calendar.DATE, -1);
            dayList.add(dayFormat.format(date.getTime()));
            dayTripsSummary = DayTripsSummary.getDayTripsForDay(user.getId(), date);
            list.add(dayTripsSummary);

            date.add(Calendar.DATE, -1);
            dayList.add(dayFormat.format(date.getTime()));
            dayTripsSummary = DayTripsSummary.getDayTripsForDay(user.getId(), date);
            list.add(dayTripsSummary);

            date.add(Calendar.DATE, -1);
            dayList.add(dayFormat.format(date.getTime()));
            dayTripsSummary = DayTripsSummary.getDayTripsForDay(user.getId(), date);
            list.add(dayTripsSummary);

            date.add(Calendar.DATE, -1);
            dayList.add(dayFormat.format(date.getTime()));
            dayTripsSummary = DayTripsSummary.getDayTripsForDay(user.getId(), date);
            list.add(dayTripsSummary);

            date.add(Calendar.DATE, -1);
            dayList.add(dayFormat.format(date.getTime()));
            dayTripsSummary = DayTripsSummary.getDayTripsForDay(user.getId(), date);
            list.add(dayTripsSummary);
        }

        if (colors.isEmpty()) {
            for (int c : ColorTemplate.LIBERTY_COLORS) {
                colors.add(c);
            }
        }

        if (savedInstanceState != null) {
            mFullscreen = savedInstanceState.getBoolean(FULL_KEY);
            mActiveChart = (Chart) savedInstanceState.getSerializable(ACTIVE_KEY);
        }

        draw();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(FULL_KEY, mFullscreen);
        savedInstanceState.putSerializable(ACTIVE_KEY, mActiveChart);
    }
}
