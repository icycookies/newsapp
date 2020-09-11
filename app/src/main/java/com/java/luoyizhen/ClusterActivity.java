package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ClusterActivity extends AppCompatActivity {

    private int curCluster;
    private NewsList newsList;
    private String[] keywords = new String[]{"新冠 武汉 疫情 殉职 确诊", "病毒 研究 团队 疫苗 治疗", "卫健委 钻石 公主 病例 通报", "cases new deaths united states", "first february events people report"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("新闻聚类");
        curCluster = 0;
        newsList = new NewsList("", this.getApplicationContext());

        LinearLayout clusterList = findViewById(R.id.cluster_list);
        for (int i = 0; i < 5; i++){
            View view = LayoutInflater.from(this.getApplicationContext()).inflate(android.R.layout.simple_list_item_1, null);
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText("分类" + i);
            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    curCluster = finalI;
                    update();
                }
            });
            clusterList.addView(view);
        }
        update();
    }
    private void update(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    newsList.getFeed(curCluster);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        });
        t.start();
    }
    final private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if (msg.what == 0){
                show();
            }
        }
    };
    private void show(){
        LinearLayout contentList = findViewById(R.id.cluster_content);
        contentList.removeAllViews();
        View view0 = LayoutInflater.from(this.getApplicationContext()).inflate(android.R.layout.simple_list_item_1, null);
        TextView textView = view0.findViewById(android.R.id.text1);
        textView.setText("关键词：" + keywords[curCluster]);
        contentList.addView(view0);
        /*for (final News news : newsList.getAll()){
            final View view = LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.news_item, null);
            final TextView title = view.findViewById(R.id.title);
            final TextView publisher = view.findViewById(R.id.publisher);
            final TextView date = view.findViewById(R.id.date);

            title.setText(news.getTitle());
            publisher.setText(news.getPublisher());
            date.setText(news.getDate());

            int color = ContextCompat.getColor(this, R.color.colorRead);
            contentList.addView(view);
        }*/
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
