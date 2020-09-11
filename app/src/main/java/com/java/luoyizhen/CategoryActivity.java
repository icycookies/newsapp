package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CategoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String[] categoryAll = new String[]{"历史", "推荐", "国内", "国际", "数据", "知疫学者", "前沿", "新闻聚类"};
    private ArrayList<String> selected;
    private ArrayList<String> unselected;
    private LinearLayout selectedLayout, unselectedLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        selected = Server.getFavor();
        unselected = new ArrayList<>();
        selectedLayout = findViewById(R.id.selected);
        unselectedLayout= findViewById(R.id.unselected);

        showContent();
    }

    private void showContent(){
        HashMap<String, String> selectedMap = new HashMap<>();
        for (String category : selected)selectedMap.put(category, category);
        unselected.clear();
        for (String category : categoryAll) {
            if (!selectedMap.containsKey(category))
                unselected.add(category);
        }
        selected.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        unselected.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        fill();
    }
    private void fill(){
        fillList(selectedLayout, selected);
        fillList(unselectedLayout, unselected);
    }

    private void fillList(final LinearLayout layout, ArrayList<String> items){
        int idx = 0;
        for (String item: items){
            final View view = LayoutInflater.from(this.getApplicationContext()).inflate(android.R.layout.simple_list_item_1, null);
            final TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(item);
            view.setBackgroundColor(Color.argb(0, 255,255,255));
            textView.setBackgroundColor(Color.argb(0, 255,255,255));
            final int finalIdx = idx;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ArrayList<String> toAdd, toDelete;
                    int flag;
                    if (layout == selectedLayout){
                        toAdd = unselected;
                        toDelete = selected;
                        flag = 1;
                    }else{
                        toAdd = selected;
                        toDelete = unselected;
                        flag = -1;
                    }
                    int h = v.getHeight();
                    for (int i = finalIdx; i < layout.getChildCount(); i++){
                        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(layout.getChildAt(i), "translationY", 0, -h);
                        objectAnimator.setDuration(500);
                        objectAnimator.start();
                    }
                    ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(v, "translationX", 0, 400 * flag);
                    ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(v, "translationY", 0, h * (toAdd.size() - finalIdx));
                    objectAnimatorX.setDuration(500);
                    objectAnimatorY.setDuration(500);
                    objectAnimatorX.start();
                    objectAnimatorY.start();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String tmp = toDelete.get(finalIdx);
                            toDelete.remove(finalIdx);
                            toAdd.add(tmp);
                            selectedLayout.removeAllViews();
                            unselectedLayout.removeAllViews();
                            fill();
                        }
                    }, 500);
                }
            });
            layout.addView(view);
            idx += 1;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO: add animation
        if (parent == findViewById(R.id.selected)){
            selected.remove(position);
        }else if (parent == findViewById(R.id.unselected)){
            selected.add(unselected.get(position));
        }
        showContent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                Server.setFavor(selected);
                Intent intent = new Intent();
                intent.putExtra("reload_news", false);
                this.finish();
                //test
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
