package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart(){
        super.onStart();
        final SharedPreferences settings = getSharedPreferences("prefs", 0);
        final boolean firstRun = settings.getBoolean("first_run", true);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (firstRun){
                    settings.edit().putBoolean("first_run", false).apply();
                    startActivity(new Intent(getApplicationContext(), OpeningActivity.class));
                }else{
                    startActivity(new Intent(getApplicationContext(), HomePage.class));
                }
                finish();
            }
        };
        timer.schedule(task, 3000);
    }
}
