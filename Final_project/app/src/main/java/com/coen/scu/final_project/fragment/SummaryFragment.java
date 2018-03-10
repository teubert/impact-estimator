package com.coen.scu.final_project.fragment;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coen.scu.final_project.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SummaryFragment extends Fragment implements TwoChartFragment.ToggleChartFullscreenInterface {
    private static final String DEBUG_TAG = "Summary Fragment";
    private static final String FULL_KEY = "Full";
    private static final String ACTIVE_KEY = "Active";

    private static boolean mFullscreen = false;
    private static Chart mActiveChart = null;

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
     *
     */
    public SummaryFragment() {
        // Required empty public constructor
    }

    /**
     * @return
     */
    public static SummaryFragment newInstance() {
        return new SummaryFragment();
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

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
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(FULL_KEY, mFullscreen);
        savedInstanceState.putSerializable(ACTIVE_KEY, mActiveChart);
    }
}
