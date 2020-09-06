package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;

import org.w3c.dom.Text;

public class VisualizeActivity extends AppCompatActivity {

    private int curField;       //current field: 0:domestic 1:global
    private CovidData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize);

        data = Server.getCovidData();
        curField = 0;
        bindEvents();
        show();
    }

    private void bindEvents() {
        Button button0 = findViewById(R.id.localData);
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curField = 0;
                show();
            }
        });
        Button button1 = findViewById(R.id.globalData);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curField = 1;
                show();
            }
        });
    }

    private void show(){
        TextView text0 = findViewById(R.id.allCount);
        TextView text1 = findViewById(R.id.curCount);
        TextView text2 = findViewById(R.id.deadCount);
        text0.setText(Integer.toString(data.getAllCount()));
        text1.setText(Integer.toString(data.getCurCount()));
        text2.setText(Integer.toString(data.getDeadCount()));
        BarChart barChart = findViewById(R.id.rank);

    }
}
