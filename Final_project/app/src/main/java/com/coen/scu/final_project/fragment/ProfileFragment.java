package com.coen.scu.final_project.fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.coen.scu.final_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private TextView mUserName;
    private TextView mEmail;
    private TextView mCar;
    private CircleImageView mImage;
    private ProgressDialog mUpdateDialog;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRef = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fabEdit);
        fab.setBackgroundColor(0);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new ProfileEditFragment();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContent, fragment)
                        .addToBackStack(null)
                        .commit();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        mUserName = view.findViewById(R.id.my_profile_displayName);
        mEmail= view.findViewById(R.id.my_profile_email);
        mCar= view.findViewById(R.id.my_profile_car);
        mImage= view.findViewById(R.id.my_profile_image);
        displayPreUserInfo(mUser.getUid());
        return view;

    }

    private void displayPreUserInfo(final String uid) {
        mRef.child("users").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading Data",
                    "Please wait...", true);

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email = dataSnapshot.child(uid).child("email").getValue(String.class);
                String userName = dataSnapshot.child(uid).child("name").getValue(String.class);
                String carType = dataSnapshot.child(uid).child("car_type").getValue(String.class);
                String url = dataSnapshot.child(uid).child("image").getValue(String.class);

                //display
                if (userName != null) {
                    mUserName.setText(userName);
                }

                if (email!= null) {
                    mEmail.setText(email);
                }
                if (carType != null) {
                    mCar.setText(carType);
                }


                Picasso.with(getContext())
                        .load(url)
                        .resize(100, 100)
                        .into(mImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                progressDialog.dismiss();
                            }
                            @Override
                            public void onError() {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}