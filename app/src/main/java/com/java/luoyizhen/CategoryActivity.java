package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryActivity extends AppCompatActivity {

    private String[] favor;
    private String[] categoryAll = new String[]{"", "", "", "", "", "", "", "", "", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        favor = Server.getFavor();

        showContent();
    }

    private void showContent(){
        ListView selected = findViewById(R.id.selected);
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        /*SimpleAdapter adapter = new SimpleAdapter(this, newsList.getAllItems(), android.R.layout.simple_list_item_1,
                new String[] {"category"},
                new int[] {android.R.id.text1});
        selected.setAdapter(adapter);
        selected.setOnItemClickListener((AdapterView.OnItemClickListener) this);*/
    }
}
