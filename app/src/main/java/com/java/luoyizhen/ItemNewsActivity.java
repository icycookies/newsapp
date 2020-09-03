package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ItemNewsActivity extends AppCompatActivity {
    private String url;
    private String file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_news);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        file = intent.getStringExtra("file");
        setupViews();
        showContent();
    }

    private void setupViews(){

    }
    private void showContent(){
        //TODO: use webview to show the news
    }
}
