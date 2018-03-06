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
import com.coen.scu.final_project.java.FriendUser;
import com.coen.scu.final_project.java.Notification;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
    private DatabaseReference mRef;
    private DatabaseReference mFriendDataRef;
    private DatabaseReference mFriendReqRef;
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
        mFriendReqRef = mRef.child("friend_req");
        mFriendDataRef = mRef.child("friend_data");
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
        FirebaseRecyclerAdapter<Notification, NotificationViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notification, NotificationViewHolder>(
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
                        final String currDate = DateFormat.getDateInstance().format(new Date());
                        FriendUser myFriendUser = new FriendUser(model.getmFromId(), currDate);
                        final FriendUser otherFriendUser = new FriendUser(mUser.getUid(), currDate);

                        //add friend, delete notification
                        mFriendDataRef.child(mUser.getUid()).child(model.getmFromId()).setValue(myFriendUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mFriendDataRef.child(model.getmFromId()).child(mUser.getUid()).setValue(otherFriendUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        deleteReqNoti(model);
                                    }
                                });
                            }
                        });
                        Toast.makeText(getContext(), "accept" + model.getmUserName(), Toast.LENGTH_SHORT).show();

                    }
                });

                viewHolder.mView.findViewById(R.id.reject_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteReqNoti(model);

                        Toast.makeText(getContext(), "reject" + model.getmUserName(), Toast.LENGTH_SHORT).show();
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

    private void deleteNotification(String to, String from) {
        Map notification = new HashMap();
        notification.put("notification/" + to + "/" + from, null);
        mRef.updateChildren(notification, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError == null) {

                } else {

                }
            }
        });
    }

    private void deleteReqNoti(final Notification model) {
        mFriendReqRef.child(mUser.getUid()).child(model.getmFromId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFriendReqRef.child(model.getmFromId()).child(mUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteNotification(mUser.getUid(), model.getmFromId());
                    }
                });
            }
        });
    }
}
