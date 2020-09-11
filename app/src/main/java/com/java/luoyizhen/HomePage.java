package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.Charset;
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
    private Entity entity;
    private Context context;
    private LinearLayout newsListView;
    private SearchView searchView;
    private boolean scrollEnabled;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        favor = Server.getFavor();
        curCategory = "推荐";

        context = this.getApplicationContext();
        newsList = new NewsList(curCategory, context);
        newsListView = this.findViewById(R.id.newslist);
        searchView = this.findViewById(R.id.search);
        searchView.setSubmitButtonEnabled(true);
        getSupportActionBar().setTitle("Covid19-News");

        Server.setContext(context);

        Intent intent = getIntent();
        String topic = intent.getStringExtra("topic");
        if (topic != null){
            curCategory = topic;
        }
        fillCategory();
        bindEvents();
        if (intent.getBooleanExtra("reload_news", true))refresh();
        Log.i("WDNMWDNM","");
    }

    private void fillCategory(){
        LinearLayout category_list = findViewById(R.id.category_list);
        category_list.removeAllViews();
        for (final String category : favor){
            View view = LayoutInflater.from(this.getApplicationContext()).inflate(android.R.layout.simple_list_item_1, null);
            TextView textView = view.findViewById(android.R.id.text1);
            if (category.equals(curCategory) && !curCategory.equals("数据") && !curCategory.equals("知疫学者") && !curCategory.equals("新闻聚类")){
                textView.setTextColor(Color.RED);
                textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                textView.getPaint().setAntiAlias(true);
            }
            textView.setText(category);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    curCategory = category;
                    scrollEnabled = true;
                    Log.i("category_change", curCategory);
                    if (curCategory.equals("数据")) {
                        startActivity(new Intent(context, VisualizeActivity.class));
                    }else if (curCategory.equals("知疫学者")){
                        startActivity(new Intent(context, ScholarActivity.class));
                    }else if (curCategory.equals("新闻聚类")){
                        startActivity(new Intent(context, ClusterActivity.class));
                    }else{
                        if (curCategory.equals("历史"))scrollEnabled = false;
                        fillCategory();
                    }
                    refresh();
                }
            });
            category_list.addView(view);
        }
    }

    private void bindEvents(){
        ImageButton btn = findViewById(R.id.categoryOption);
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
                if (!scrollEnabled){
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
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
                if (scrollY + v.getHeight() == view.getHeight() && scrollEnabled){
                    add();
                    Log.i("more", "more");
                }
            }
        });
        //搜索
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                newsListView.removeAllViews();
                findViewById(R.id.loading_view_0).setVisibility(View.VISIBLE);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            scrollEnabled = false;
                            newsList = Server.search(query);
                            Log.i("fuck", newsList.toString());
                            entity = Server.getEntity(query);
                            Message msg = new Message();
                            msg.what = 3;
                            handler.sendMessage(msg);
                        }catch (ExceptionInInitializerError e){
                            Toast.makeText(getApplicationContext(),"请检查网络设置", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                t.start();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    final private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            NewsList selectedList = null;
            findViewById(R.id.loading_view_0).setVisibility(View.INVISIBLE);
            if (msg.what == 0){
                selectedList = newsList;
                Log.i("orz1", "orz");
            }else if (msg.what == 1) {
                Log.i("togetmore", "getmore");
                newsListView.removeViewAt(newsListView.getChildCount() - 1);
                selectedList = moreNews[0];
            }else if (msg.what == 2) {
                newsListView.removeAllViews();
                selectedList = historyNewsList;
            }else if (msg.what == 3) {
                if (entity != null)addEntity();
                selectedList = newsList;
            }
            assert selectedList != null;
            for (final News news : selectedList.getAll()) {
                addNews(newsListView, news);
            }
            if (scrollEnabled) {
                View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText("正在努力加载中...");
                newsListView.addView(view);
            }
        }
    };

    private void refresh() {
        newsListView.removeAllViews();
        findViewById(R.id.loading_view_0).setVisibility(View.VISIBLE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Message msg = new Message();
                    if (curCategory == "历史") {
                        historyNewsList = Server.getHistory();
                        msg.what = 2;
                    }else{
                        newsList.getFeed(curCategory);
                        msg.what = 0;
                    }
                    handler.sendMessage(msg);
                }catch (ExceptionInInitializerError e){
                    Toast.makeText(getApplicationContext(),"请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }
        });
        t.start();
    }

    private void add(){
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    moreNews[0] = newsList.getMore(curCategory);
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
                newsList.saveCache();
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

    private void addEntity(){
        final View view = LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.entity, null);
        final TextView name = view.findViewById(R.id.entity_name);
        final TextView description = view.findViewById(R.id.entity_description);
        final ImageView image = view.findViewById(R.id.entity_image);
        final ListView relations = view.findViewById(R.id.relations);

        name.setText(entity.getName());
        description.setText(entity.getDescription());
        //Log.i("description", entity.getDescription());
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        Picasso.with(this).load(entity.getImage()).into(image);
        for (Map.Entry<String, String> relation : entity.getRelation()){
            HashMap<String, Object> item = new HashMap<>();
            item.put("relation_type", relation.getKey());
            item.put("relation_entity", relation.getValue());
            data.add(item);
        }
        SimpleAdapter adapter = new SimpleAdapter(context, data, R.layout.simple_relation,
                new String[]{"relation_type", "relation_entity"},
                new int[]{R.id.orz0, R.id.orz1});
        relations.setAdapter(adapter);
        newsListView.addView(view);
    }
}
