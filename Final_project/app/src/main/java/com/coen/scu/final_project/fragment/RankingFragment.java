package com.coen.scu.final_project.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coen.scu.final_project.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RankingFragment extends Fragment {

    public RankingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        return view;

    }

}
