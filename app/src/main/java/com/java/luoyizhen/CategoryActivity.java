package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CategoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String[] categoryAll = new String[]{"收藏", "推荐", "疫情概况", "抗疫政策", "数据", "知疫学者", "测试1", "测试2", "测试3", "测试4"};
    private ArrayList<String> selected;
    private ArrayList<String> unselected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        selected = Server.getFavor();
        unselected = new ArrayList<>();

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

        ListView selectedView = findViewById(R.id.selected);
        fillList(selectedView, selected);

        ListView unselectedView = findViewById(R.id.unselected);
        fillList(unselectedView, unselected);
    }

    private void fillList(ListView view, ArrayList<String> items){
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        for (String category : items){
            HashMap<String, Object> item = new HashMap<>();
            item.put("category", category);
            Log.i("category", category);
            data.add(item);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_1,
                new String[] {"category"},
                new int[] {android.R.id.text1});
        view.setAdapter(adapter);
        view.setOnItemClickListener((AdapterView.OnItemClickListener) this);
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
