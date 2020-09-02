package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart(){
        super.onStart();
        SharedPreferences settings = getSharedPreferences("prefs", 0);
        boolean firstRun = settings.getBoolean("first_run", true);
        if (firstRun){
            settings.edit().putBoolean("first_run", false).commit();
            startActivity(new Intent(getApplicationContext(), OpeningActivity.class));
        }else{
            startActivity(new Intent(getApplicationContext(), HomePage.class));
        }
    }
}
