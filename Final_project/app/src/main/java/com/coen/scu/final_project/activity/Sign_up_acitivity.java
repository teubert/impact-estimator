package com.coen.scu.final_project.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.coen.scu.final_project.R;
import com.coen.scu.final_project.TripRecognitionService;
import com.coen.scu.final_project.java.RankingUser;
import com.coen.scu.final_project.java.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sign_up_acitivity extends AppCompatActivity {
    EditText mEmail;
    EditText mPassword;
    EditText mRepeatedPassword;
    Button mSignUp;
    ProgressBar mPBar;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mEmail = findViewById(R.id.emailEdit2);
        mPassword = findViewById(R.id.passwordEdit2);
        mRepeatedPassword = findViewById(R.id.passwordRepEdit);
        mSignUp = findViewById(R.id.signUpBtn2);
        mPBar= findViewById(R.id.progressBar2);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        signUpToDataBase();
    }

    public void signUpToDataBase() {
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = mEmail.getText().toString().trim();
                String passwordString = mPassword.getText().toString().trim();
                String password2String = mRepeatedPassword.getText().toString().trim();

                if (emailString.isEmpty()) {
                    mEmail.setError("Email is required"); mEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
                    mEmail.setError("Please enter a valid mEmail");
                    mEmail.requestFocus();
                    return;
                }

                if (passwordString.isEmpty()) {
                    mPassword.setError("Password is required");
                    mPassword.requestFocus();
                    return;
                }

                if (passwordString.length() < 6) {
                    mPassword.setError("Minimum lenght of mPassword should be 6");
                    mPassword.requestFocus();
                    return;
                }

                if (!passwordString.equals(password2String)) {
                    mPassword.setError("Same mPassword is required");
                    mPassword.requestFocus();
                    return;
                }

                createAccount(emailString, passwordString);

            }
        });
    }

    public void createAccount(String email, String password) {
        mPBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mPBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
//                            Toast.makeText(getApplicationContext(), "Registration succeed",
//                                    Toast.LENGTH_SHORT).show();

//                            Intent serviceIntent = new Intent(getBaseContext(), TripRecognitionService.class);
//                            startService(serviceIntent);

                            mUser = mAuth.getCurrentUser();
                            String userEmail = mEmail.getText().toString().trim();
                            addToFireBase(userEmail);
                            Intent intent = new Intent(Sign_up_acitivity.this,HomeActivity.class);
                            intent.putExtra("newUser", true);
                            intent.putExtra("email", mEmail.getText().toString().trim());
                            mEmail.setText("");
                            mPassword.setText("");
                            mRepeatedPassword.setText("");
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "Email has been registered",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void addToFireBase(String email){
        String userId = mUser.getUid();
        List<String> friendList = new ArrayList<>();
        mDatabase.child("userList").child(Util.emailToUser(email)).setValue(userId);
        mDatabase.child("idEmailMap").child(userId).setValue(email);
        Map<String, List<String>> friendMap = new HashMap<>();
        mDatabase.child("friendMap").child(userId).setValue(friendMap);
        mDatabase.child("users").child(userId).child("name").setValue("Unnamed User");
        mDatabase.child("users").child(userId).child("email").setValue(email);
        mDatabase.child("users").child(userId).child("car_type").setValue("UNKOWN");
        mDatabase.child("users").child(userId).child("diet_type").setValue("Balanced");
        String url = "https://firebasestorage.googleapis.com/v0/b/android-final-project-471bd.appspot.com/o/Portrait%2Fdefault.png?alt=media&token=922d687a-6c4a-4b08-940b-303b570e6894";
        mDatabase.child("users").child(userId).child("image").setValue(url);
    }


}
