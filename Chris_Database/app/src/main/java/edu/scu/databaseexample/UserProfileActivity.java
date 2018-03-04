package edu.scu.databaseexample;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UserProfileActivity extends AppCompatActivity implements ProfileFragment.ToggleEdit {

    private boolean edit_mode = false;

    private void load() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        String id = UserProfile.emailToUsername("teubert@gmail.com");
        if (edit_mode) {
            ft.replace(R.id.flContent, EditProfileFragment.newInstance(id)).commit();
        } else {
            ft.replace(R.id.flContent, ProfileFragment.newInstance(id)).commit();
        }
    }

    @Override
    public void toggle() {
        edit_mode = !edit_mode;
        load();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        load();

    //  UserProfile user = UserProfile.getUserProfileById(UserProfile.emailToUsername("teubert@gmail.com"));
    //  user.updateLastLogin();
    //  user.setName("New User");
        // TODO(CT): Test the rest
        // TODO(CT): Move to unit tests
    }
}
