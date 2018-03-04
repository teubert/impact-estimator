package edu.scu.databaseexample;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends android.app.Fragment implements UserProfile.UserUpdateInterface, View.OnClickListener {

    public interface ToggleEdit {
        void toggle();
    }

    static final String DEBUG_TAG = "ProfileFragment";

    UserProfile user; // User
    private ProfileFragment.ToggleEdit mListener;
    TextView email;
    TextView name;

    /**
     *
     *
     * @param id
     * @return
     */
    static public ProfileFragment newInstance(String id) {
        ProfileFragment profileFragment = new ProfileFragment();
        Log.d(DEBUG_TAG, "Creating Profile Fragment for "+id);
        profileFragment.user = UserProfile.getUserProfileById(id);
        return profileFragment;
    }

    /**
     *
     */
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     *
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        user.addCallback(this);
        view.findViewById(R.id.edit_button).setOnClickListener(this);
        email = view.findViewById(R.id.email_view);
        name = view.findViewById(R.id.name_view);
        return view;

    }

    @Override
    public void onUserUpdate() {
        Log.d(DEBUG_TAG, "Setting User Email Field to " + user.getEmail());
        email.setText(user.getEmail());
        Calendar c = Calendar.getInstance();
        c.set(2017,11,13);
        email.setText(DayTripsSummary.getDateString(c));
        name.setText(user.getName());
    }

    @Override
    public void onClick(View v) {
        mListener.toggle();
    }

    /**
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFragment.ToggleEdit) {
            mListener = (ProfileFragment.ToggleEdit) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onDateUpdateListener");
        }
    }

    /**
     *
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
