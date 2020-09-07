package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        final ArrayList<Map.Entry<String, Integer>> chartData = curField == 0 ? data.getProvince() : data.getCountry();
        int i = 0;
        for (Map.Entry<String, Integer> elem : chartData){
            barEntries.add(new BarEntry((float) i, elem.getValue().floatValue()));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries,"");
        BarData data = new BarData(barDataSet);
        barChart.setData(data);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return chartData.get((int)value).getKey();
            }
        });
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(1000, Easing.EaseInBack);
        barChart.animateX(1000, Easing.EaseInBack);
        Description desc = new Description();
        desc.setText(curField == 0 ? "全国各省份数据统计" : "全球各国家数据统计");
        barChart.setDescription(desc);
    }
}
