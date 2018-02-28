package edu.scu.databaseexample;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        String id = UserProfile.emailToUsername("teubert@gmail.com");
        ft.replace(R.id.flContent, ProfileFragment.getInstance(id)).commit();

//        UserProfile user = UserProfile.getUserProfileById(UserProfile.emailToUsername("teubert@gmail.com"));
//        user.updateLastLogin();
//        user.setName("New User");
        // TODO(CT): Test the rest
        // TODO(CT): Move to unit tests
    }
}
