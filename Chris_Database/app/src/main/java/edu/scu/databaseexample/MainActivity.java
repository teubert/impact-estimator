package edu.scu.databaseexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserProfile user = UserProfile.addNewUserProfile("test@gmail.com", "Test User", Transportation.CarType.SMALL_CAR);
        user.updateLastLogin();
        user.setName("New User");
        // TODO(CT): Test the rest
        // TODO(CT): Move to unit tests
    }
}
