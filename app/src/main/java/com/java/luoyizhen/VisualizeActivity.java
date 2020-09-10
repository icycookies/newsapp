package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VisualizeActivity extends AppCompatActivity {

    private int curField;       //current field: 0:domestic 1:global
    private CovidData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("疫情数据");

        getData();
        bindEvents();
    }
    final private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if (msg.what == 0){
                show();
            }
        }
    };
    private void getData(){
        curField = 0;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    data = Server.getCovidData();
                    Message msg = new Message();
                    msg.what = 0;
                    handler.sendMessage(msg);
                }catch (ExceptionInInitializerError e){
                    e.printStackTrace();
                }
            }
        });
        t.start();
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
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        final ArrayList<Map.Entry<String, Integer>> chartData = curField == 0 ? data.getProvince() : data.getCountry();
        int i = 0;
        for (Map.Entry<String, Integer> elem : chartData){
            i += 1;
            Log.i("orz", elem.getValue() + " " +elem.getKey());
            barEntries.add(new BarEntry(i - 0.5f, elem.getValue().floatValue()));
            if (i >= 10)break;
        }
        BarDataSet barDataSet = new BarDataSet(barEntries,"");
        barDataSet.setColor(Color.RED);
        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.4f);
        barChart.setData(data);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setLabelCount(10);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                //Log.i("value", Float.toString(value));
                try {
                    String str = chartData.get((int)value).getKey();
                    if (str.startsWith("China|")){
                        str = str.substring(6);
                    }else if (str.startsWith("United States")){
                        str = "US";
                    }
                    return str;
                }catch (Exception e){
                    return "";
                }
            }
        });
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(1500, Easing.EaseInBack);
        barChart.animateX(1500, Easing.EaseInBack);
        barChart.getDescription().setEnabled(false);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("reload_news", false);
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
