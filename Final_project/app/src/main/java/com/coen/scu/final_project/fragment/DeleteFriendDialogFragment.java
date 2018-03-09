package com.coen.scu.final_project.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.coen.scu.final_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeleteFriendDialogFragment extends DialogFragment {

    public DeleteFriendDialogFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        final String mFreindUid;
        if(bundle != null) {
            mFreindUid = bundle.getString("id", "");
        } else {
            mFreindUid = "";
        }
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        return new AlertDialog.Builder(getActivity())
                // Set Dialog Title
                .setTitle("Delete this friend?")

                // Positive button

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("DEBUG_TAG", mUser.getUid());
                        Log.i("DEBUG_TAG", mFreindUid);
                        Map unfriendMap = new HashMap();
                        unfriendMap.put("friend_data/" + mUser.getUid() + "/" + mFreindUid, null);
                        unfriendMap.put("friend_data/" + mFreindUid + "/" + mUser.getUid(), null);
                        mRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            }
                        });
                        Map rankingMap = new HashMap();
                        rankingMap.put("ranking/" + mUser.getUid() + "/" + mFreindUid, null);
                        rankingMap.put("ranking/" + mFreindUid + "/" + mUser.getUid(), null);
                        mRef.updateChildren(rankingMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            }
                        });
                    }
                })

                // Negative Button
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,	int which) {
                        // Do something else
                    }
                }).create();
    }
    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }
}
