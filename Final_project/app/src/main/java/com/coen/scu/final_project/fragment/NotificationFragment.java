package com.coen.scu.final_project.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.coen.scu.final_project.R;
import com.coen.scu.final_project.java.Notification;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
    private DatabaseReference mRef;
    private FirebaseUser mUser;
    private RecyclerView mRecyclerView;


    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.keepSynced(true);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        mRecyclerView = view.findViewById(R.id.notification_recycleview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("DEBUG_TAG", mUser.getUid());
        FirebaseRecyclerAdapter<Notification, NotificationViewHolder>  firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notification, NotificationViewHolder>(
                Notification.class,
                R.layout.notification_item,
                NotificationViewHolder.class,
                mRef.child("notification").child(mUser.getUid())
        ) {
            @Override
            protected void populateViewHolder(NotificationViewHolder viewHolder, final Notification model, int position) {

                viewHolder.setMessage(model.getmMessage());
                viewHolder.mView.findViewById(R.id.accept_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "accept"+ model.getmUserName(), Toast.LENGTH_SHORT).show();

                    }
                });

                viewHolder.mView.findViewById(R.id.reject_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "reject"+ model.getmUserName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public NotificationViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setMessage(String message) {
            TextView messageText = mView.findViewById(R.id.friend_request_name);
            messageText.setText(message);

        }
    }
}
