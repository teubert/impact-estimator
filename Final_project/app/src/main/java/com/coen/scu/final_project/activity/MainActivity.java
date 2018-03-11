package com.coen.scu.final_project.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.coen.scu.final_project.R;
import com.coen.scu.final_project.java.CommonUtil;
import com.coen.scu.final_project.java.FirebaseUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Button mSignUpBtn;
    Button mLoginBtn;
    FirebaseAuth mAuth;
    EditText mEmail;
    EditText mPassword;
    ProgressBar mPBar;

    private final String TAG = "FACELOG";
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSignUpBtn = findViewById(R.id.signUpBtn);
        mLoginBtn = findViewById(R.id.loginBtn);
        mEmail= findViewById(R.id.emailEdit);
        mPassword= findViewById(R.id.passwordEdit);
        mPBar= findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        signUp();
        login();
        init();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setupGoogleLoginButton() {

    }

    private void init() {
        setupEmailLoginButton();
//        setupFBLoginButton();
        setupGoogleLoginButton();
        setupSignUpButton();
    }

    private void setupSignUpButton() {
        mSignUpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this,Sign_up_acitivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupEmailLoginButton() {
        mLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if(email.isEmpty()){
                    mEmail.setError("Email required");
                    mEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmail.setError("Please enter a valid email");
                    mEmail.requestFocus();
                    return;
                }
                if(password.isEmpty()){
                    mPassword.setError("Password required");
                    mPassword.requestFocus();
                    return;
                }
                emailLogin(email, password);
            }
        });
    }

    private void onLoginSuccess() {
        // Sign in success, update UI with the signed-in user's information
        Toast.makeText(MainActivity.this, "Login succeed",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("preUser", true);
        startActivity(intent);
    }

    private void onLoginFailure(String msg) {
        // If sign in fails, display a message to the user.
        Toast.makeText(MainActivity.this, msg,
                Toast.LENGTH_SHORT).show();
    }

    private void setupFBLoginButton() {
        mCallbackManager = CallbackManager.Factory.create();

        Button loginButton = findViewById(R.id.facebookLogin);
//        loginButton.setReadPermissions("email", "public_profile");
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
//        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.d(TAG, "facebook:onSuccess:" + loginResult);
//                AuthCredential credential = FacebookAuthProvider
//                        .getCredential(loginResult.getAccessToken().getToken());
//                mAuth.signInWithCredential(credential)
//                        .addOnCompleteListener(
//                                MainActivity.this, new OnCompleteListener<AuthResult>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<AuthResult> task) {
//                                        if (task.isSuccessful()) {
//                                            Log.d(TAG, "signInWithCredential:success");
//                                            FirebaseUser user = mAuth.getCurrentUser();
//                                            syncFBUser(user);
//                                            onLoginSuccess();
//                                        } else {
//                                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                                            onLoginFailure("Failed to login with Facebook!");
//                                        }
//                                    }
//                                });
//
//            }
//
//            @Override
//            public void onCancel() {
//                Log.d(TAG, "facebook:onCancel");
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                Log.d(TAG, "facebook:onError", exception);
//                onLoginFailure(exception.getMessage());
//
//            }
//        });
    }

    /**
     * sync FB user with Firebase. Update anything that needs to be stored in Firebase here.
     */
    private void syncFBUser(final FirebaseUser user) {
        final String email = user.getEmail() + "_facebook_login";

        DatabaseReference database = FirebaseUtils.getDatabase();
        database.child("userList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot==null || !dataSnapshot.hasChild(CommonUtil.emailToUser(email))) {
                    Toast.makeText(MainActivity.this, "New FB User!", Toast.LENGTH_SHORT).show();
                    FirebaseUtils.addUserToFirebase(user, email);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void signUp() {
        mSignUpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this,Sign_up_acitivity.class);
                startActivity(intent);
            }
        });
    }

    public void login() {
        mLoginBtn.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view) {
               String email = mEmail.getText().toString().trim();
               String password = mPassword.getText().toString().trim();
               if(email.isEmpty()){
                   mEmail.setError("Email required");
                   mEmail.requestFocus();
                   return;
               }

               if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                   mEmail.setError("Please enter a valid email");
                   mEmail.requestFocus();
                   return;
               }
               if(password.isEmpty()){
                   mPassword.setError("Password required");
                   mPassword.requestFocus();
                   return;
               }
               userLogin(email, password);
           }
       });
    }

    public void userLogin(String email, String password) {
        mPBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mPBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Toast.makeText(MainActivity.this, "Login succeed",
//                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            intent.putExtra("preUser", true);
                            mEmail.setText("");
                            mPassword.setText("");
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void emailLogin(String email, String password) {
        mPBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mPBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            onLoginSuccess();
                        } else {
                            onLoginFailure(task.getException().getMessage());
                        }

                        // ...
                    }
                });
    }
}
