package edu.scu.databaseexample;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

public class SummaryActivity extends AppCompatActivity {
    final static String DEBUG_TAG = "summary";

    public void onDateUpdate(Calendar date) {
        Log.v(DEBUG_TAG, "onDateUpdate: Creating fragment and commiting");
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        SummaryFragment fragment = SummaryFragment.newInstance(date);
        ft.replace(R.id.flContent, fragment).commit();
        Log.v(DEBUG_TAG, "onDateUpdate: done");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(DEBUG_TAG, "onCreate: Creating log view");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        onDateUpdate(Calendar.getInstance());
    }
}
