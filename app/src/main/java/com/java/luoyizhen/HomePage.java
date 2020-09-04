package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
        favor = Server.getFavor();                          //暂定如此，用文件保存吧
        curCategory = "推荐";
        newsList = new NewsList(curCategory);

        fillCategory();
        bindEvents();
        loadHistory();
        loadFavor();
        refresh();
    }

    private void fillCategory(){
        LinearLayout category_list = findViewById(R.id.category_list);
        for (final String category : favor){
            View view = LayoutInflater.from(this.getApplicationContext()).inflate(android.R.layout.simple_list_item_1, null);
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(category);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    curCategory = category;
                    Log.i("category_change", curCategory);
                    refresh();
                }
            });
            category_list.addView(view);
        }
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
        //下拉刷新
        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.content);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Log.i("event", "下拉刷新成功");
                        refresh();
                    }
                }, 2000);
            }
        });
        //上拉加载
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
        }
    }
}
