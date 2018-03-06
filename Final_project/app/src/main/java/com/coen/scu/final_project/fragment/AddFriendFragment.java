package com.coen.scu.final_project.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.coen.scu.final_project.R;
import com.coen.scu.final_project.activity.HomeActivity;
import com.coen.scu.final_project.java.Notification;
import com.coen.scu.final_project.java.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFriendFragment extends Fragment {
    private String mCurrUserName;
    private EditText mSearchEmail;
    private Button mSearchBtn;
    private CircleImageView mFriendImage;
    private TextView mFriendName;
    private Button mRequestBtn;
    private Button mDeclineBtn;
    private DatabaseReference mRef;
    private DatabaseReference mFriendReqRef;
    private DatabaseReference mFriendDataRef;
    private DatabaseReference mNotificationRef;
    private FirebaseUser mUser;
    private String mFreindUid;
    private int mCurrent_state;  // 0: not friend; 1: request sent; 2: request received; 3:friends;

    public AddFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set title bar
        ((HomeActivity) getActivity())
                .setActionBarTitle("Add Friend");
        mRef = FirebaseDatabase.getInstance().getReference();
        mFriendReqRef = mRef.child("friend_req");
        mFriendDataRef = mRef.child("friend_data");
        mNotificationRef = mRef.child("notification");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        findUserName(mUser.getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);
        mSearchEmail = view.findViewById(R.id.search_userName);
        mSearchBtn = view.findViewById(R.id.search_btn);
        mDeclineBtn = view.findViewById(R.id.friend_decline_btn);
        mFriendImage = view.findViewById(R.id.friend_image);
        mFriendName = view.findViewById(R.id.friend_name);
        mRequestBtn = view.findViewById(R.id.friend_send_req_btn);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCurrent_state = 0;

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendEmail = mSearchEmail.getText().toString().trim();
                findUser(Util.emailToUser(friendEmail));
            }
        });

        mRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if not friends yet sent request
                if (mCurrent_state == 0) {
                    mDeclineBtn.setVisibility(View.GONE);
                    mFriendReqRef.child(mUser.getUid()).child(mFreindUid).child("request_type").setValue("req_sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mFriendReqRef.child(mFreindUid).child(mUser.getUid()).child("request_type").setValue("req_received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Notification notification = new Notification(mUser.getUid(), mCurrUserName);
                                        mNotificationRef.child(mFreindUid).child(mUser.getUid()).setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(), "Request sent", Toast.LENGTH_SHORT).show();
                                                mCurrent_state = 1;
                                                mRequestBtn.setText("Cancel Friend Request");
                                                mDeclineBtn.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "Fail to send request", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                //cancel request
                if (mCurrent_state == 1) {
                    mFriendReqRef.child(mUser.getUid()).child(mFreindUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqRef.child(mFreindUid).child(mUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mCurrent_state = 0;
                                    mRequestBtn.setText("Send Friend Request");
                                    mDeclineBtn.setVisibility(View.GONE);
                                    deleteNotification(mFreindUid, mUser.getUid());
                                }
                            });
                        }
                    });
                }

                //received request and accept
                if (mCurrent_state == 2) {
                    final String currDate = DateFormat.getDateInstance().format(new Date());
                    mFriendDataRef.child(mUser.getUid()).child(mFreindUid).setValue(currDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendDataRef.child(mFreindUid).child(mUser.getUid()).setValue(currDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqRef.child(mUser.getUid()).child(mFreindUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendReqRef.child(mFreindUid).child(mUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mCurrent_state = 3;
                                                    mRequestBtn.setText("Unfriend");
                                                    mDeclineBtn.setVisibility(View.GONE);
                                                    deleteNotification(mUser.getUid(), mFreindUid);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });

                }

                //delete friend
                if (mCurrent_state == 3) {
                    Map unfriendMap = new HashMap();
                    unfriendMap.put("friend_data/" + mUser.getUid() + "/" + mFreindUid, null);
                    unfriendMap.put("friend_data/" + mFreindUid + "/" + mUser.getUid(), null);

                    mRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if (databaseError == null) {

                                mCurrent_state = 0;
                                mRequestBtn.setText("Send Friend Request");
                                mDeclineBtn.setVisibility(View.GONE);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
            }
        });
        mDeclineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendReqRef.child(mUser.getUid()).child(mFreindUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFriendReqRef.child(mFreindUid).child(mUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                deleteNotification(mUser.getUid(),mFreindUid);
                                mCurrent_state = 0;
                                mRequestBtn.setText("Send Friend Request");
                                mDeclineBtn.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        });
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


    private void findUserName(final String uid) {

        mRef.child("users").getRef().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child(uid).child("name").getValue(String.class);

                //display
                if (userName != null) {
                    mCurrUserName = userName;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
            }
        });
        // Read from the database
//        mRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                mCurrUserName = dataSnapshot.child("users").child(uid).child("name").getValue(String.class);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//            }
//        });
    }

    private void findUser(final String friendEmail) {
        mRef.child("userList").getRef().addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(friendEmail)) {
                    mFreindUid = dataSnapshot.child(friendEmail).getValue(String.class);
                    displayUser(mFreindUid);
//                    Toast.makeText(getContext(), "find user", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "User not exist", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayUser(final String uid) {
        mFriendName.setVisibility(View.VISIBLE);
        mFriendImage.setVisibility(View.VISIBLE);
        mRequestBtn.setVisibility(View.VISIBLE);
        mRef.child("users").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "User Found",
                    "Loading Data...", true);

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child(uid).child("name").getValue(String.class);
                String userImageUrl = dataSnapshot.child(uid).child("image").getValue(String.class);

                //display
                if (userName != null) {
                    mFriendName.setText(userName);
                }

                if (userImageUrl != null) {

                    String url = dataSnapshot.child(uid).child("image").getValue(String.class);
                    Picasso.with(getContext())
                            .load(url)
                            .placeholder(R.mipmap.ic_launcher)
                            .into(mFriendImage, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    progressDialog.dismiss();

                                }

                                @Override
                                public void onError() {

                                }
                            });
                }

                mFriendReqRef.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(mFreindUid)) {
                            String req_type = dataSnapshot.child(mFreindUid).child("request_type").getValue().toString();
                            if (req_type.equals("req_received")) {
                                mCurrent_state = 2;
                                mRequestBtn.setText("Accept Friend Request");
                                mDeclineBtn.setVisibility(View.VISIBLE);
                            } else if (req_type.equals("req_sent")) {
                                mCurrent_state = 1;
                                mRequestBtn.setText("Cancel Friend Request");
                            }
                        } else {
                            mFriendDataRef.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(mFreindUid)) {
                                        mCurrent_state = 3;
                                        mRequestBtn.setText("Unfriend");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
