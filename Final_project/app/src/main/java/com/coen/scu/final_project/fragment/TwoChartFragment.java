package com.coen.scu.final_project.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.coen.scu.final_project.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TwoChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TwoChartFragment extends Fragment {
    private static final String DEBUG_TAG = "Two Chart Fragment";

    ToggleChartFullscreenInterface mListener = null;

    public TwoChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TwoChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TwoChartFragment newInstance() {
        return new TwoChartFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_two_chart, container, false);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.chart1, new PieFragment());
//        ImageButton fullChart1 = view.findViewById(R.id.chart1_btn);
//        fullChart1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((SummaryFragment) getParentFragment()).toggle(SummaryFragment.Chart.PIE);
//            }
//        });
        ft.add(R.id.chart2, new LineFragment());
//        ImageButton fullChart2 = view.findViewById(R.id.chart2_btn);
//        fullChart2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((SummaryFragment) getParentFragment()).toggle(SummaryFragment.Chart.LINE);
//            }
//        });
        ft.commit();

        return view;
    }

    public interface ToggleChartFullscreenInterface {
        void toggle(SummaryFragment.Chart chart);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Fragment frag = getParentFragment();
        try {
            mListener = (ToggleChartFullscreenInterface) frag;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    frag.toString() + " must implement ToggleChartFullscreenInterface");
        }
    }
}
