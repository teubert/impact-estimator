package edu.scu.databaseexample;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends  android.app.Fragment implements UserProfile.UserUpdateInterface, View.OnClickListener {
    private final static String DEBUG_TAG = "EditProfile";
    private ProfileFragment.ToggleEdit mListener;
    private UserProfile user;
    TextView email;
    TextView name;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id User Id
     * @return A new instance of fragment EditProfileFragment.
     */
    public static EditProfileFragment newInstance(String id) {
        EditProfileFragment fragment = new EditProfileFragment();
        fragment.user = UserProfile.getUserProfileById(id);
        return fragment;
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
        View v =  inflater.inflate(R.layout.fragment_edit_profile, container, false);
        user.addCallback(this);
        v.findViewById(R.id.submit_button).setOnClickListener(this);
        name = v.findViewById(R.id.name_edit);
        email = v.findViewById(R.id.email_edit);
        return v;
    }

    /**
     * Called on attach. Handles receipt of listener interface
     *
     * @param context   Attached context
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
     * Called on click of "complete" button
     *
     * @param v Active view
     */
    @Override
    public void onClick(View v) {
        user.setName(name.getText().toString());
        user.setEmail(email.getText().toString());
        mListener.toggle();
    }

    /**
     *  Called on detach- handles removal of listener
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     *
     */
    @Override
    public void onUserUpdate() {
        Log.d(DEBUG_TAG, "Setting User Email Field to " + user.getEmail());
        email.setText(user.getEmail());
        name.setText(user.getName());
    }
}
