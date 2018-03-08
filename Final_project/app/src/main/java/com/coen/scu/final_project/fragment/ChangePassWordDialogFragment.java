package com.coen.scu.final_project.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.coen.scu.final_project.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePassWordDialogFragment extends DialogFragment {


    public ChangePassWordDialogFragment() {
        // Required empty public constructor
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //allowing you to display standard alerts that is managed by a fragment
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //getting layout of custom_dialog
        View v = inflater.inflate(R.layout.fragment_change_pass_word_dialog,null);

        final EditText password = v.findViewById(R.id.new_password1);
        final EditText passwordRep = v.findViewById(R.id.new_password2);
        Button button = v.findViewById(R.id.update_password);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final String newPass = password.getText().toString();
                final String newPassRep = passwordRep.getText().toString();
                Log.i("DEBUG_TAG", newPass);
                Log.i("DEBUG_TAG", newPassRep);
                if (newPass.isEmpty()) {
                    password.setError("Password is required");
                    password.requestFocus();
                    return;
                }

                if (newPass.length() < 6) {
                    password.setError("Minimum lenght of mPassword should be 6");
                    password.requestFocus();
                    return;
                }

                if (!newPass.equals(newPassRep)) {
                    password.setError("Same mPassword is required");
                    password.requestFocus();
                    return;
                }
                updatePassword(newPass);
            }
        });
        //setting the custom view to builder
        builder.setView(v);

        //returning dialog object
        return builder.create();
    }

    private void updatePassword(String password) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUser.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(),"password updated", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }


}
