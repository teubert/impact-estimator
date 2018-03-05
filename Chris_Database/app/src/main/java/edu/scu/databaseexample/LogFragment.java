package edu.scu.databaseexample;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogFragment extends android.app.ListFragment implements DayTripsSummary.TripUpdateInterface, DatePickerDialog.OnDateSetListener {
    private static final String DEBUG_TAG = "LogFragment";

    private static final String ARG_DATE = "date";
    private LogAdapter logAdapter;

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, dayOfMonth);
        mDate = cal;
        dayTripsSummary.setDay(cal);
        updateView();
    }

    public class LogAdapter extends ArrayAdapter<Trip> {
        private Context mContext;
        private ArrayList<Trip> mTrips;

        public LogAdapter(@NonNull Context context, ArrayList<Trip> trips) {
            super(context, R.layout.fragment_log, trips);
            Log.v(DEBUG_TAG, "LogAdapter: Creating");
            this.mContext = context;
            this.mTrips = new ArrayList<>();
            this.mTrips = trips;
        }

        @Override
        public void clear() {
            Log.v(DEBUG_TAG, "LogAdapter.clear: clearing trips");
            mTrips.clear();
        }

        @Override
        public void add(Trip trip) {
            Log.v(DEBUG_TAG, "LogAdapter.add: Adding trip");
            mTrips.add(trip);
        }

        @Override
        public int getCount() {
            Log.v(DEBUG_TAG, "LogAdapter.getCount (" + Integer.toString(mTrips.size()) + ")");
            return mTrips.size();
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Log.v(DEBUG_TAG, "LogAdapter.getView");
            View listItem = convertView;
            if(listItem == null) {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.log_entry, parent, false);
            }

            final Trip currentTrip = mTrips.get(position);
            if (currentTrip == null) {
                Log.e(DEBUG_TAG, String.format("Null trip in position %d", position));
                return listItem;
            }

            Log.v(DEBUG_TAG, "LogAdapter.getView: Setting mode");
            TextView mode = (TextView) listItem.findViewById(R.id.text_mode);
            if (currentTrip.transport_mode == null) {
                Log.e(DEBUG_TAG, String.format("Null trip mode in position %d", position));
            }
            mode.setText(currentTrip.transport_mode.name());

            Log.v(DEBUG_TAG, "LogAdapter.getView: Setting impact");
            TextView impact = (TextView) listItem.findViewById(R.id.text_impact);
            impact.setText("XXX KG CO2 Emissions");

            Log.v(DEBUG_TAG, "LogAdapter.getView: Setting timestamp");
            TextView time = (TextView) listItem.findViewById(R.id.text_time);
            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(currentTrip.start.timestamp);
            Calendar end = Calendar.getInstance();
            end.setTimeInMillis(currentTrip.end.timestamp);
            int startHr     = start.get(Calendar.HOUR);
            int startMin    = start.get(Calendar.MINUTE);
            int endHr       = end.get(Calendar.HOUR);
            int endMin      = end.get(Calendar.MINUTE);
            time.setText(String.format("%02d:%02d - %02d:%02d", startHr, startMin, endHr, endMin));

            Log.v(DEBUG_TAG, "LogAdapter.getView: Setting icon");
            ImageView icon = listItem.findViewById(R.id.image_icon);
            switch (currentTrip.transport_mode) {
                case BIKE:
                    Log.d(DEBUG_TAG, "LogAdapter.getView: Setting icon bike");
                    icon.setImageResource(R.drawable.ic_bike);
                    break;

                case BOAT:
                    Log.d(DEBUG_TAG, "LogAdapter.getView: Setting icon boat");
                    icon.setImageResource(R.drawable.ic_boat);
                    break;

                case WALK:
                    Log.d(DEBUG_TAG, "LogAdapter.getView: Setting icon walk");
                    icon.setImageResource(R.drawable.ic_walk);
                    break;

                case TRAIN:
                    Log.d(DEBUG_TAG, "LogAdapter.getView: Setting icon train");
                    icon.setImageResource(R.drawable.ic_train);
                    break;

                case AIRCRAFT:
                    Log.d(DEBUG_TAG, "LogAdapter.getView: Setting icon aircraft");
                    icon.setImageResource(R.drawable.ic_aircraft);
                    break;

                case AUTOMOBILE:
                    Log.d(DEBUG_TAG, "LogAdapter.getView: Setting icon car");
                    icon.setImageResource(R.drawable.ic_car);
                    break;

                default:
                    Log.w(DEBUG_TAG, "LogAdapter.getView: Transport Mode not recognized");
                    break;
            }

            ImageButton edit = listItem.findViewById(R.id.edit_button);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // DialogFragment.show() will take care of adding the fragment
                    // in a transaction.  We also want to remove any currently showing
                    // dialog, so make our own transaction and take care of that here.
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);

                    // Create and show the dialog.
                    DialogFragment newFragment = EditTripDialogFragment.newInstance(currentTrip.tripId, getTimestamp(mDate), userId);
                    newFragment.show(ft, "dialog");
                }
            });

            TextView estimate = listItem.findViewById(R.id.text_impact);
            estimate.setText(Double.toString(currentTrip.estimate.CO2) + " kg CO2 emitted");
            // TODO(CT): Switch to km

            Log.v(DEBUG_TAG, "LogAdapter.getView: done");

            return listItem;
        }
    }

    // TODO: Rename and change types of parameters
    private Calendar mDate;
    private DayTripsSummary dayTripsSummary;
    private String userId;

    public LogFragment() {
        // Required empty public constructor
    }

    /**
     * get the current timestamp in "unix time"
     *
     * @return Current timestamp in unix time
     */
    static private long getTimestamp(Calendar calendar) {
        Log.v(DEBUG_TAG, "Getting current timestamp");

        // 2) get a java.util.Date from the calendar instance.
        //    this date will represent the current instant, or "now".
        java.util.Date now = calendar.getTime();

        // 3) a java current time (now) instance
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

        return currentTimestamp.getTime();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param calendar Date for fragment
     * @return A new instance of fragment LogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogFragment newInstance(Calendar calendar) {
        Log.d(DEBUG_TAG, "newInstance: Configuring new instance of LogFragment for date " + DayTripsSummary.getDateString(calendar));
        LogFragment fragment = new LogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DATE, getTimestamp(calendar));
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
        Log.d(DEBUG_TAG, "onCreate: Creating new LogFragment");
        super.onCreate(savedInstanceState);
        mDate = Calendar.getInstance();
        if (getArguments() != null) {
            mDate.setTimeInMillis(getArguments().getLong(ARG_DATE));
            Log.d(DEBUG_TAG, "onCreate: date set to " + mDate);
        } else {
            Log.d(DEBUG_TAG, "onCreate: no set day... using today");
        }
        userId = "teubert_gmail_com";
        dayTripsSummary = DayTripsSummary.getDayTripsForDay(userId, mDate);

        logAdapter = new LogAdapter(getActivity(), dayTripsSummary.trips);
        setListAdapter(logAdapter);

        Log.v(DEBUG_TAG, "onCreate: done");
    }

    public void updateView() {
        Button date = getActivity().findViewById(R.id.button_date);
        if (date != null) {
            date.setText(DayTripsSummary.getDateString(mDate));
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
        Log.d(DEBUG_TAG, "onCreateView: inflating view");
        View v = inflater.inflate(R.layout.fragment_log, container, false);

        ImageButton left = v.findViewById(R.id.left_button);
        left.setOnClickListener(onClickLeft);

        ImageButton right = v.findViewById(R.id.right_button);
        right.setOnClickListener(onClickRight);

        Button date = v.findViewById(R.id.button_date);
        if (date != null) {
            date.setText(DayTripsSummary.getDateString(mDate));
        }
        date.setOnClickListener(onClickDate);

        return v;
    }

    public View.OnClickListener onClickLeft = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(DEBUG_TAG, "Clicked left arrow- going back one day");
            mDate.add(Calendar.DATE, -1);
            dayTripsSummary.setDay(mDate);
            updateView();
        }
    };

    public View.OnClickListener onClickDate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(DEBUG_TAG, "Clicked date- opening date selector");
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    LogFragment.this,
                    mDate.get(Calendar.YEAR),
                    mDate.get(Calendar.MONTH),
                    mDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }
    };

    public View.OnClickListener onClickRight = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(DEBUG_TAG, "Clicked right arrow- going forward one day");
            mDate.add(Calendar.DATE, 1);
            dayTripsSummary.setDay(mDate);
            updateView();
        }
    };

    /**
     *
     */
    @Override
    public void onTripUpdate() {
        Log.d(DEBUG_TAG, "onTripUpdate: updating list (" +
                Integer.toString(dayTripsSummary.trips.size()) + " item(s) )");
        logAdapter.notifyDataSetChanged();
        Log.v(DEBUG_TAG, "onTripUpdate: done");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(DEBUG_TAG, "Removing callback");
        dayTripsSummary.removeCallback(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(DEBUG_TAG, "Adding callback");
        dayTripsSummary.addCallback(this);
    }
}
