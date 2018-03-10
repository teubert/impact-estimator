package com.coen.scu.final_project.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.coen.scu.final_project.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 */
public class SingleChartFragment extends Fragment {
    private static final String DEBUG_TAG = "One Chart Fragment";
    private static final String ACTIVE_KEY = "Active";

    SummaryFragment.Chart activeChart = null;

    TwoChartFragment.ToggleChartFullscreenInterface mListener = null;

    public SingleChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TwoChartFragment.
     */
    public static SingleChartFragment newInstance(SummaryFragment.Chart chart) {
        SingleChartFragment singleChartFragment =  new SingleChartFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVE_KEY, chart);

        singleChartFragment.setArguments(bundle);
        return singleChartFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_chart, container, false);

        Bundle args = getArguments();
        if (args != null) {
            activeChart = (SummaryFragment.Chart) args.getSerializable(ACTIVE_KEY);
        }

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (activeChart) {
            case LINE:
                ft.add(R.id.chart1, new LineFragment());
                break;
            case PIE:
                ft.add(R.id.chart1, new PieFragment());
                break;
            default:
                ft.add(R.id.chart1, new LineFragment());
                break;
        }
        ImageButton fullChart1 = view.findViewById(R.id.chart1_btn);
        fullChart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SummaryFragment) getParentFragment()).toggle(SummaryFragment.Chart.PIE);
            }
        });
        ft.commit();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Fragment frag = getParentFragment();
        try {
            mListener = (TwoChartFragment.ToggleChartFullscreenInterface) frag;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    frag.toString() + " must implement ToggleChartFullscreenInterface");
        }
    }
}
