package edu.scu.databaseexample;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
agment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditTripDialogFragment extends android.app.DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TRIPID = "tripId";
    private static final String ARG_DAYID  = "dayId";
    private static final String ARG_USERID = "userId";

    // TODO: Rename and change types of parameters
    private String mTripId;
    private long mTimestamp;
    private String mUserId;


    public EditTripDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param trip
     * @param timestamp
     * @param user
     * @return A new instance of fragment EditTripDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditTripDialogFragment newInstance(String trip, long timestamp, String user) {
        EditTripDialogFragment fragment = new EditTripDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRIPID, trip);
        args.putLong(ARG_DAYID, timestamp);
        args.putString(ARG_USERID, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTripId = getArguments().getString(ARG_TRIPID);
            mTimestamp = getArguments().getLong(ARG_DAYID);
            mUserId = getArguments().getString(ARG_USERID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_trip_dialog, container, false);
    }
}
