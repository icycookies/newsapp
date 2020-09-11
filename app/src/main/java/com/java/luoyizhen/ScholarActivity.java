package com.java.luoyizhen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.mbms.MbmsErrors;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ScholarActivity extends AppCompatActivity {

    private ArrayList<Expert> scholars;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scholar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("知疫学者");
        context = this.getApplicationContext();

        getData();
    }
    private void getData(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    scholars = Server.getExperts();
                    Message msg = new Message();
                    msg.what = 0;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
    final private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if (msg.what == 0){
                findViewById(R.id.loading_view_3).setVisibility(View.INVISIBLE);
                show();
            }
        }
    };
    private void show(){
        for (final Expert scholar : scholars){
            final View view = LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.scholar, null);
            final TextView name = view.findViewById(R.id.scholar_name);
            final TextView position = view.findViewById(R.id.scholar_position);
            final TextView affiliation = view.findViewById(R.id.scholar_affiliation);
            final ImageView image = view.findViewById(R.id.scholar_image);

            name.setText(scholar.getName());
            position.setText(scholar.getPosition());
            affiliation.setText(scholar.getAffiliation());
            //Log.i("description", entity.getDescription());
            Picasso.with(this).load(scholar.getAvatar()).into(image);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, ItemScholarActivity.class);
                    intent.putExtra("url", scholar.getHomepage());
                    startActivity(intent);
                }
            });
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("reload_news", true);
                intent.putExtra("topic", "推荐");
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
