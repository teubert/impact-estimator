package edu.scu.databaseexample;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static edu.scu.databaseexample.DayTripsSummary.updateTrip;

/**
 * A simple {@link Fragment} subclass.
agment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditTripDialogFragment extends android.app.DialogFragment implements DayTripsSummary.TripUpdateInterface{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TRIPID = "tripId";
    private static final String ARG_DAYID  = "dayId";
    private static final String ARG_USERID = "userId";

    private static final String DEBUG_TAG  = "EditTrip";
    Map<Transportation.TransportMode, View> transportButtons;

    // TODO: Rename and change types of parameters
    Trip trip = null;
    EditText editText;
    DayTripsSummary tripSummary;
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
    public static EditTripDialogFragment newInstance(String trip, long timestamp, String user) {
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
            mUserId = getArguments().getString(ARG_USERID);
            tripSummary = DayTripsSummary.getDayTripsForDay(mUserId, cal);
            tripSummary.addCallback(this);
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
                trip.setDistance(Double.parseDouble(editText.getText().toString()));
                trip.setTransport_mode(activeTransportMode);
                DayTripsSummary.updateTrip(mUserId, trip);
                EditTripDialogFragment.this.dismiss();
            }
        });

        return v;
    }

    @Override
    public void onTripUpdate() {
        trip = tripSummary.getTrip(mTripId);
        if (trip != null) {
            // Editing trip
            updateSelected(trip.transport_mode);
            editText.setText(Double.toString(trip.distance));
        }
    }
}
