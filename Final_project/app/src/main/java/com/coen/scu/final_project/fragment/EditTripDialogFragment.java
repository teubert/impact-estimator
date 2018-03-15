package com.coen.scu.final_project.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.coen.scu.final_project.java.DayTripsSummary;
import com.coen.scu.final_project.java.Transportation;
import com.coen.scu.final_project.java.Trip;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.coen.scu.final_project.R;

/**
 * A simple {@link Fragment} subclass.
agment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditTripDialogFragment extends DialogFragment { //implements DayTripsSummary.TripUpdateInterface
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TRIPID = "tripId";
    private static final String ARG_DAYID  = "dayId";
    private static final String ARG_USERID = "userId";

    private static final String DEBUG_TAG  = "EditTrip";
    Map<Transportation.TransportMode, View> transportButtons;

    // TODO: Rename and change types of parameters
    Trip trip = new Trip();
    EditText editText;
    DayTripsSummary tripSummary;
    String mDate;
    Transportation.TransportMode activeTransportMode = null;
    String mTripId;
    String mUserId;

    /**
     *
     */
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
    public static EditTripDialogFragment newInstance(@Nullable String trip, long timestamp, @NonNull String user) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        Log.d(DEBUG_TAG, "Configuring EditTripDialog (" + user + "/" +
                DayTripsSummary.getDateString(cal) + "/" + trip + ")");
        EditTripDialogFragment fragment = new EditTripDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRIPID, trip);
        args.putLong(ARG_DAYID, timestamp);
        args.putString(ARG_USERID, user);
        fragment.setArguments(args);
        Log.v(DEBUG_TAG, "newInstance: done");
        return fragment;
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTripId = getArguments().getString(ARG_TRIPID);
            long mTimestamp = getArguments().getLong(ARG_DAYID);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(mTimestamp);
            mDate = DayTripsSummary.getDateString(cal);
            mUserId = getArguments().getString(ARG_USERID);
//            tripSummary = DayTripsSummary.getDayTripsForDay(mUserId, cal);
//            if (mTripId != null) {
//                tripSummary.addCallback(this);
//            }
        }
    }

    /**
     *
     * @param transportMode
     */
    public void updateSelected(Transportation.TransportMode transportMode) {
        activeTransportMode = transportMode;

        if (getActivity() == null) {
            return;
        }
        ColorStateList lightGrey = getActivity().getResources().getColorStateList(R.color.light_grey);
        ColorStateList white = getActivity().getResources().getColorStateList(R.color.white);

        for (Map.Entry<Transportation.TransportMode, View> transportModeViewPair : transportButtons.entrySet()) {
            if (transportMode == transportModeViewPair.getKey()) {
                Log.d(DEBUG_TAG, "Selecting " + transportMode.name());
                transportModeViewPair.getValue().setBackgroundTintList(lightGrey);
            } else {
                transportModeViewPair.getValue().setBackgroundTintList(white);
            }
        }
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
        View v = inflater.inflate(R.layout.fragment_edit_trip_dialog, container, false);

        Button cancel = v.findViewById(R.id.cancel_action);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTripDialogFragment.this.dismiss();
            }
        });

        editText = v.findViewById(R.id.distance);

        transportButtons = new HashMap<Transportation.TransportMode, View>();
        transportButtons.put(Transportation.TransportMode.AIRCRAFT,   v.findViewById(R.id.aircraft_button));
        transportButtons.put(Transportation.TransportMode.AUTOMOBILE, v.findViewById(R.id.car_button));
        transportButtons.put(Transportation.TransportMode.BIKE,       v.findViewById(R.id.bike_button));
        transportButtons.put(Transportation.TransportMode.BOAT,       v.findViewById(R.id.boat_button));
        transportButtons.put(Transportation.TransportMode.WALK,       v.findViewById(R.id.walk_button));
        transportButtons.put(Transportation.TransportMode.TRAIN,      v.findViewById(R.id.train_button));

        for (final Map.Entry<Transportation.TransportMode, View> transportButton : transportButtons.entrySet()) {
            transportButton.getValue().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateSelected(transportButton.getKey());
                }
            });
        }

        Button submit = v.findViewById(R.id.submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(DEBUG_TAG, "onClick(Submit): Updating trip");
                try {
                    trip.setDistance(Double.parseDouble(editText.getText().toString()));
                } catch (java.lang.NumberFormatException ex) {
                    Log.e(DEBUG_TAG, "Could not parse distance");
                }

                try {
                    trip.setTransport_mode(activeTransportMode);
                } catch (Exception ex) {
                    Log.e(DEBUG_TAG, "Could not parse transport mode");
                }

                if (mTripId == null) {
                    // New Trip
                    DayTripsSummary.append(mUserId, mDate, trip);
                } else {
                    DayTripsSummary.updateTrip(mUserId, mDate, trip);
                }
                EditTripDialogFragment.this.dismiss();
            }
        });

        if (mTripId != null) {
            trip = ((MainPageFragment) getParentFragment()).dayTripsSummary.getTrip(mTripId);
            if (trip != null) {
                // Editing trip
                updateSelected(trip.getTransport_mode());
                editText.setText(Double.toString(trip.getDistance()));
            }
        }

        return v;
    }
}
