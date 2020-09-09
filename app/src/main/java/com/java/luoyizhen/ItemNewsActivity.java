package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class ItemNewsActivity extends AppCompatActivity {
    private String title;
    private String url;
    private String file;
    private String content;
    private Context context;
    private WebView webView;
    private TableLayout shareTable;
    private IWXAPI wxapi;
    final String appID = "e91e4a77b8463c3052ce8cda2f14a93d";
    private boolean networkAvail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_news);
        context = this.getApplicationContext();

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        file = intent.getStringExtra("file");
        wxapi = WXAPIFactory.createWXAPI(context, appID, true);
        wxapi.registerApp(appID);

        setupViews();
        bindEvents();
        showContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_item_menu, menu);
        return true;
    }

    private void setupViews(){
        shareTable = findViewById(R.id.share_activity);
        webView = findViewById(R.id.wrapper);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("新闻正文");

        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.getSettings().setBlockNetworkImage(false);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
    }

    private void bindEvents(){
        findViewById(R.id.wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!wxapi.isWXAppInstalled()){
                    Toast.makeText(context, "请安装微信", Toast.LENGTH_SHORT);
                    return;
                }
                WXMediaMessage msg = new WXMediaMessage();
                msg.title = title;
                msg.description = url;
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneSession;
                req.transaction = "sharednews";
                wxapi.sendReq(req);
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTable.setVisibility(View.INVISIBLE);
            }
        });
    }

    final private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if (msg.what == 0){
                if (/*改为文件存在*/false){

                }else{
                    if (!networkAvail)content = "请检查网络连接";
                    Log.i("=========url============", url);
                    webView.loadUrl(url);
                }
            }
        }
    };

    private void showContent() {
        networkAvail = true;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message msg = new Message();
                    msg.what = 0;
                    handler.sendMessage(msg);
                } catch (ExceptionInInitializerError e) {
                    networkAvail = false;
                }
            }
        });
        t.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("reload_news", false);
                this.finish();
            case R.id.nav_share:
                shareTable.setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
