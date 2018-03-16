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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.coen.scu.final_project.R;
import com.coen.scu.final_project.TripRecognitionService;
import com.coen.scu.final_project.java.CommonUtil;
import com.coen.scu.final_project.java.FirebaseUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Button mSignUpBtn;
    Button mLoginBtn;
    SignInButton mGoogleBtn;
    FirebaseAuth mAuth;
    EditText mEmail;
    EditText mPassword;
    ProgressBar mPBar;
    RelativeLayout mRootLayout;

    private final String TAG = "FACELOG";
    private CallbackManager mCallbackManager;

    private static final String TAG_GOOGLE = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSignUpBtn = findViewById(R.id.signUpBtn);
        mLoginBtn = findViewById(R.id.loginBtn);
        mGoogleBtn = findViewById(R.id.googleLogin);
        mEmail= findViewById(R.id.emailEdit);
        mPassword= findViewById(R.id.passwordEdit);
        mPBar= findViewById(R.id.progressBar);
        mRootLayout = findViewById(R.id.main_root_layout);
        mAuth = FirebaseAuth.getInstance();
        mRootLayout.setVisibility(View.VISIBLE);

        init();

    }

    private void init() {
        setupEmailLoginButton();
        setupFBLoginButton();
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

    private void setupFBLoginButton() {
        mCallbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = findViewById(R.id.facebookLogin);
        loginButton.setReadPermissions("email", "public_profile");
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                AuthCredential credential = FacebookAuthProvider
                        .getCredential(loginResult.getAccessToken().getToken());
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(
                                MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "signInWithCredential:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                           loginExternalUser(user, "facebook");
                                        } else {
                                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                                            onLoginFailure("Failed to login with Facebook!");
                                        }
                                    }
                                });

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "facebook:onError", exception);
                onLoginFailure(exception.getMessage());

            }
        });
    }

    private void setupGoogleLoginButton() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG_GOOGLE, "firebaseAuthWithGoogle:" + account.getId());

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithCredential:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    loginExternalUser(user, "google");
                                } else {
                                    // If sign in fails, display a message to the user.
                                    onLoginFailure("Google Login Failed! " + task.getException().getMessage() );

                                }
                            }
                        });
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                onLoginFailure("Google Login Failed! " + e.getMessage());
                //updateUI(null);
                // [END_EXCLUDE]
            }
        }

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void emailLogin(String email, String password) {
        //mRootLayout.setVisibility(View.INVISIBLE);
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
                            //mRootLayout.setVisibility(View.VISIBLE);
                        }

                        // ...
                    }
                });
    }

    private void onLoginSuccess() {
        // Sign in success, update UI with the signed-in user's information
        Toast.makeText(MainActivity.this, "Login succeed",
                Toast.LENGTH_SHORT).show();
        Intent serviceIntent = new Intent(getBaseContext(), TripRecognitionService.class);
        startService(serviceIntent);
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("preUser", true);
        startActivity(intent);
    }

    private void onLoginFailure(String msg) {
        // If sign in fails, display a message to the user.
        Toast.makeText(MainActivity.this, msg,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * sync FB and Google user with Firebase. Update anything that needs to be stored in Firebase here.
     */
    private void loginExternalUser(final FirebaseUser user, String source) {

        // Comment out  the code below since we are not using userList table for now.
        /*
        final String email = user.getEmail() + "_" + source;

        DatabaseReference database = FirebaseUtils.getDatabase();
        database.child("userList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot==null || !dataSnapshot.hasChild(CommonUtil.emailToUser(email))) {
                    FirebaseUtils.addUserToFirebase(user, email);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */
        //mRootLayout.setVisibility(View.INVISIBLE);

        DatabaseReference database = FirebaseUtils.getDatabase();
        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot==null || !dataSnapshot.hasChild(user.getUid())) {
                    FirebaseUtils.addUserToFirebase(user);
                }
                onLoginSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
