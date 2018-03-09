package com.coen.scu.final_project.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.coen.scu.final_project.java.RankingUser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RankingFragment extends Fragment {
    private DatabaseReference mRef;
    private FirebaseUser mUser;
    private String mUid;
    private RecyclerView mRecyclerView;


    public RankingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.keepSynced(true);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUid = mUser.getUid();

        generateRankingData();

    }

    private void generateRankingData() {
                //add self
        mRef.child("users").getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child(mUid).child("name").getValue(String.class);
                String userEmission = dataSnapshot.child(mUid).child("total_emission").getValue(String.class);
                String userImageUrl = dataSnapshot.child(mUid).child("image").getValue(String.class);
                RankingUser rankingUser = new RankingUser(userName, userEmission, userImageUrl,mUid);
                mRef.child("ranking").child(mUid).child(mUid).setValue(rankingUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //loop through each friend
        mRef.child("friend_data").getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot FriendUserData : dataSnapshot.child(mUid).getChildren()) {
                    FriendUser friendUser = FriendUserData.getValue(FriendUser.class);
                    final String uid = friendUser.getmId();
                    //generate ranking class
                    mRef.child("users").getRef().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String userName = dataSnapshot.child(uid).child("name").getValue(String.class);
                            String userEmission = dataSnapshot.child(uid).child("total_emission").getValue(String.class);
                            String userImageUrl = dataSnapshot.child(uid).child("image").getValue(String.class);
                            RankingUser rankingUser = new RankingUser(userName, userEmission, userImageUrl, uid);
                            mRef.child("ranking").child(mUid).child(uid).setValue(rankingUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            });
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fltAddBtn);
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new AddFriendFragment();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContent, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        mRecyclerView = view.findViewById(R.id.ranking_recycleview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("DEBUG_TAG", mUser.getUid());
        FirebaseRecyclerAdapter<RankingUser, FriendViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<RankingUser, FriendViewHolder>(
                RankingUser.class,
                R.layout.list_item,
                FriendViewHolder.class,
                mRef.child("ranking").child(mUid).orderByChild("mEmission")
        ) {
            @Override
            protected void populateViewHolder(final FriendViewHolder viewHolder, final RankingUser model, int position) {

                viewHolder.setUser(model.getmName());
                viewHolder.setEmission("Emission: " + model.getmEmission());
                viewHolder.setImage(model.getmImageUrl(), getContext(),model.getmName());
                viewHolder.mView.findViewById(R.id.ranking_unfriend).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DeleteFriendDialogFragment dialog = new DeleteFriendDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("id", model.getmId());
                        dialog.setArguments(bundle);
                        // Show Alerto DialogFragment
                        dialog.show(getFragmentManager(), "Alert Dialog Fragment");
                    }
                });
            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FriendViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setUser(String message) {
            TextView name = mView.findViewById(R.id.ranking_name);
            name.setText(message);
        }

        public void setEmission(String message) {
            TextView emission = mView.findViewById(R.id.ranking_emission);
            emission.setText(message);
        }

        public void setImage(final String url, final Context ctx, final String name) {
            CircleImageView mFriendImage = mView.findViewById(R.id.ranking_image);
            Picasso.with(ctx)
                    .load(url)
                    .resize(100,100)
                    .into(mFriendImage);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        generateRankingData();
    }

    @Override
    public void onPause() {
        super.onPause();
        Map delete= new HashMap();
        delete.put("ranking/" + mUser.getUid(), null);

        mRef.updateChildren(delete, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });

    }
}





