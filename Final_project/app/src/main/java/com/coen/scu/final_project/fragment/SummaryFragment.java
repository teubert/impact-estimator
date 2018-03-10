package com.coen.scu.final_project.fragment;

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
public class SummaryFragment extends Fragment {
    private static final String DEBUG_TAG = "Summary Fragment";

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

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.parent_frag, new TwoChartFragment());
        ft.commit();

        return view;
    }
}
