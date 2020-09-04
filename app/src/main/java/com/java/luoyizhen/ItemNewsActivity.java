package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class ItemNewsActivity extends AppCompatActivity {
    private String url;
    private String file;
    private String content;
    private WebView webView;
    private boolean networkAvail;
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
        webView = findViewById(R.id.wrapper);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
    }

    private void showContent(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    content = Server.getHtml(url);
                }catch (ExceptionInInitializerError e){
                    networkAvail = false;
                }
            }
        });
        t.start();
        try {
            t.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        if (/*改为文件存在*/false){

        }else{
            if (!networkAvail)content = "请检查网络连接";
            webView.loadData(content, "text/html", Server.getCharset(url));
        }
    }
}
