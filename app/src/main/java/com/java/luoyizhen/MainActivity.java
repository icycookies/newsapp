package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
    }

    @Override
    protected void onStart(){
        super.onStart();
        final SharedPreferences settings = getSharedPreferences("prefs", 0);
        final boolean firstRun = settings.getBoolean("first_run", true);
        Log.i("firstRun?", Boolean.toString(firstRun));
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), HomePage.class));
                finish();
            }
        };
        timer.schedule(task, 3000);
    }
}
