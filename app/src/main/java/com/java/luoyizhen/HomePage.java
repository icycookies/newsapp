package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePage extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String[] favor;
    private String curCategory;
    private NewsList newsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        favor = Server.getFavor();
        curCategory = "推荐";
        newsList = new NewsList(curCategory);

        fillCategory();
        bindEvents();
        loadHistory();
        loadFavor();
        refresh();
    }

    private void fillCategory(){
        ListView categoryList = this.findViewById(R.id.category_list);
        List<Map<String, Object>> data = new ArrayList<>();
        for (String category : favor){
            HashMap<String, Object> item = new HashMap<>();
            item.put("category", category);
            data.add(item);
            Log.i("category", category);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_1,
                new String[] {"category"},
                new int[] {android.R.id.text1});
        categoryList.setAdapter(adapter);
        categoryList.setOnItemClickListener((AdapterView.OnItemClickListener) this);
    }

    private void bindEvents(){
        Button btn = findViewById(R.id.categoryOption);
        //设置分类列表
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                fillCategory();
            }
        });
        //上拉刷新
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.content);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                }, 2000);
            }
        });
        //下拉加载
        ListView listView = this.findViewById(R.id.newslist);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            newsList.getMore();
                            refresh();
                        }
                    }, 2000);
                }
            }
        });
    }

    private void loadHistory(){

    }

    private void loadFavor() {

    }

    private void refresh() {
        ListView newsListView = this.findViewById(R.id.newslist);
        //TODO get a list of news
        newsList.setCategory(curCategory);
        newsList.getFeed();
        SimpleAdapter adapter = new SimpleAdapter(this, newsList.getAllItems(), android.R.layout.simple_list_item_2,
                new String[] {"title", "date"},
                new int[] {android.R.id.text1, android.R.id.text2});
        newsListView.setAdapter(adapter);
        newsListView.setOnItemClickListener((AdapterView.OnItemClickListener) this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == findViewById(R.id.newslist)) {
            News clicked = newsList.getItem(position);
            clicked.view();
            Intent intent = new Intent();
            intent.setClass(this, ItemNewsActivity.class);
            intent.putExtra("url", clicked.getUrl());
            intent.putExtra("file", clicked.getFile());
            startActivity(intent);
        } else {
            curCategory = favor[position];
            refresh();
        }
    }
}
