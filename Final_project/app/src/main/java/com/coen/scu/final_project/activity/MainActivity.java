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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button mSignUpBtn;
    Button mLoginBtn;
    FirebaseAuth mAuth;
    EditText mEmail;
    EditText mPassword;
    ProgressBar mPBar;

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
    }

    @Override
    public void onBackPressed() {
        finish();
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
}
