package com.coen.scu.final_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.coen.scu.final_project.fragment.MainPageFragment;
import com.coen.scu.final_project.fragment.ProfileEditFragment;
import com.coen.scu.final_project.R;
import com.coen.scu.final_project.fragment.ProfileFragment;
import com.coen.scu.final_project.fragment.RankingFragment;
import com.coen.scu.final_project.fragment.SummaryFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                HomeActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
//        View header =navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView name = (TextView)header.findViewById(R.id.head_name);
        name.setText("bla");

        Intent intent = getIntent();
        boolean isNewUser = intent.getBooleanExtra("newUser", false);
        boolean isOldUser = intent.getBooleanExtra("preUser", false);
        String newUserEmail = intent.getStringExtra("email");
        if(isNewUser){
            Class fragmentClass = ProfileEditFragment.class;
            try {
                Bundle bundle = new Bundle();
                bundle.putInt("key", 1);
                bundle.putString("email",newUserEmail);
                Fragment fragment = (Fragment) fragmentClass.newInstance();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.flContent, fragment, "initial_profile_edit").commit();
                getSupportActionBar().setTitle("My Profile");
            } catch (Exception e) {
                Toast.makeText(HomeActivity.this, "failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }

        if(isOldUser){
            Class fragmentClass = MainPageFragment.class;
            try {
                Fragment fragment = (Fragment) fragmentClass.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.flContent, fragment).commit();
                getSupportActionBar().setTitle("Home Page");
            } catch (Exception e) {
                Toast.makeText(HomeActivity.this, "failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("initial_profile_edit");
            if (fragment instanceof ProfileEditFragment) {
                sendToSart();
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();
        if(currUser == null) {
            sendToSart();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            sendToSart();

        }

        return super.onOptionsItemSelected(item);
    }

    private void sendToSart() {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Class fragmentClass = null;
        String title = "";

        if (id == R.id.nav_home_page) {
            fragmentClass = MainPageFragment.class;
            title = "Home Page";
        } else if (id == R.id.nav_summary_page) {
            fragmentClass = SummaryFragment.class;
            title = "Summary";
        } else if (id == R.id.nav_ranking_page) {
            fragmentClass = RankingFragment.class;
            title = "Ranking";
        } else if (id == R.id.nav_profile_page) {
            fragmentClass = ProfileFragment.class;
            title = "My Profile";
        }

        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.flContent, fragment)
                    .addToBackStack(null)
                    .commit();
            getSupportActionBar().setTitle(title);
        } catch (Exception e) {
            Toast.makeText(HomeActivity.this, "failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}


