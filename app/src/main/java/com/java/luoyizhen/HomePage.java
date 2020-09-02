package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HomePage extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String currentTopic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        loadHistory();
        loadFavor();
        refresh();
    }

    private void loadHistory(){

    }

    private void loadFavor() {

    }

    private void refresh() {
        ListView newsList = this.findViewById(R.id.newslist);
        //TODO get a list of news

        SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
                new String[] {"title", "date"},
                new int[] {android.R.id.text1, android.R.id.text2});
        newsList.setAdapter(adapter);
        newsList.setOnItemLongClickListener((AdapterView.OnItemLongClickListener) this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
