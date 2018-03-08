package com.coen.scu.final_project.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.coen.scu.final_project.fragment.MainPageFragment;
import com.coen.scu.final_project.fragment.NotificationFragment;
import com.coen.scu.final_project.fragment.ProfileEditFragment;
import com.coen.scu.final_project.R;
import com.coen.scu.final_project.fragment.ProfileFragment;
import com.coen.scu.final_project.fragment.RankingFragment;
import com.coen.scu.final_project.fragment.SummaryFragment;
import com.coen.scu.final_project.java.GPSPath;
import com.coen.scu.final_project.java.GPSPoint;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String DEBUG_TAG = "HomeActivity";

    private FirebaseAuth mAuth;
    private LocationManager lm;
    private String id;

    /**
     *  Start the location connection
     */
    void startConnection() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    GPSPoint gpsPoint = new GPSPoint(location.getTime(), location.getLongitude(), location.getLatitude());
                    gpsPoint.speed = location.getSpeed();
                    Log.v(DEBUG_TAG, String.format("LocationListener: Writing position update to database: %f, %f %f", gpsPoint.lat, gpsPoint.lon, gpsPoint.speed));
                    GPSPath.addNewGPSDataPoint(id, gpsPoint);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            Log.w(DEBUG_TAG, "startConnection: Missing required permissions (FINE_LOCATION)");
        }
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        id = firebaseUser.getUid();
        startConnection();

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

    /**
     *
     */
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

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();
        if(currUser == null) {
            sendToSart();
        }

    }

    /**
     *
     * @param item
     * @return
     */
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

    /**
     *
     */
    private void sendToSart() {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     *
     * @param item
     * @return
     */
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
        } else if (id == R.id.nav_notification_page) {
        fragmentClass = NotificationFragment.class;
        title = "Notification";
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
            Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     *
     * @param title
     */
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

}


