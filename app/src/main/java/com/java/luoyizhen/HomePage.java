package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePage extends AppCompatActivity {

    private ArrayList<String> favor;
    private String curCategory;
    private NewsList newsList;
    private NewsList historyNewsList;
    private NewsList[] moreNews = {null};
    private Context context;
    private LinearLayout newsListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        favor = Server.getFavor();
        curCategory = "推荐";
        newsList = new NewsList(curCategory);
        newsListView = this.findViewById(R.id.newslist);
        context = this.getApplicationContext();
        Intent intent = getIntent();

        fillCategory();
        bindEvents();
        if (intent.getBooleanExtra("reload_news", true))refresh();
    }

    private void fillCategory(){
        LinearLayout category_list = findViewById(R.id.category_list);
        category_list.removeAllViews();
        for (final String category : favor){
            View view = LayoutInflater.from(this.getApplicationContext()).inflate(android.R.layout.simple_list_item_1, null);
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(category);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    curCategory = category;
                    newsList.setCategory(curCategory);
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
        final View scrollView = findViewById(R.id.scroll0);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.i("scrollY", Integer.toString(scrollY));
                Log.i("parent", Integer.toString(scrollView.getHeight()));
                View view = findViewById(R.id.newslist);
                Log.i("child",Integer.toString(view.getHeight()));
                if (scrollY + v.getHeight() == view.getHeight()){
                    add();
                    Log.i("more", "more");
                }
            }
        });
    }

    final private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            NewsList selectedList = null;
            if (msg.what == 0){
                newsListView.removeAllViews();
                selectedList = newsList;
                Log.i("orz1", "orz");

            }else if (msg.what == 1) {
                Log.i("togetmore", "getmore");
                newsListView.removeViewAt(newsListView.getChildCount() - 1);
                selectedList = moreNews[0];
            }else{
                newsListView.removeAllViews();
                selectedList = historyNewsList;
            }
            for (final News news : selectedList.getAll()){
                addNews(newsListView, news);
            }
            View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText("正在努力加载中...");
            newsListView.addView(view);
        }
    };

    private void refresh() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Message msg = new Message();
                    if (curCategory == "历史") {
                        historyNewsList = Server.getHistory();
                        msg.what = 2;
                    }else{
                        newsList.getFeed();
                        msg.what = 0;
                    }
                    handler.sendMessage(msg);
                }catch (ExceptionInInitializerError e){
                    Toast.makeText(getApplicationContext(),"请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }
        });
        t.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 2000);
    }

    private void add(){
        moreNews = new NewsList[]{null};
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    moreNews[0] = newsList.getMore();
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }catch (ExceptionInInitializerError e){
                    Toast.makeText(getApplicationContext(),"请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }
        });
        t.start();
    }

    private void addNews(LinearLayout newsListView, final News news){
        final View view = LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.news_item, null);
        final TextView title = view.findViewById(R.id.title);
        final TextView publisher = view.findViewById(R.id.publisher);
        final TextView date = view.findViewById(R.id.date);

        title.setText(news.getTitle());
        publisher.setText(news.getPublisher());
        date.setText(news.getDate());

        int color = ContextCompat.getColor(this, R.color.colorRead);
        if (news.isViewed()){
            Log.i("orz", "viewed");
            title.setTextColor(color);
            publisher.setTextColor(color);
            date.setTextColor(color);
        }

        final Context context = this.getApplicationContext();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                news.view();
                Server.addHistory(news);
                int color = ContextCompat.getColor(context, R.color.colorRead);
                title.setTextColor(color);
                publisher.setTextColor(color);
                date.setTextColor(color);
                Intent intent = new Intent();
                intent.setClass(context, ItemNewsActivity.class);
                intent.putExtra("url", news.getUrl());
                intent.putExtra("file", news.getFile());
                startActivity(intent);
            }
        });
        newsListView.addView(view);
    }
}
