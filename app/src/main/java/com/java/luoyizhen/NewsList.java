package com.java.luoyizhen;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

//import org.asynchttpclient.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewsList {
    private String category;
    private ArrayList<News> news;
    JSONArray datas;

    NewsList(String category){
        this.category = category;
        this.news = new ArrayList<>();
    }
    public void setCategory(String category){
        this.category = category;
    }
    private Thread addNews(final JSONObject o)
    {
        try {
            final News news1 = new News(
                    o.getString("title"),
                    o.getString("time"),
                    "Source: unknown",
                    "http://example.com",
                    "The quick brown fox jumps over a lazy dog.",
                    new String[]{"http://p5.itc.cn/q_70/images03/20200807/9e87c806515a41aeb0ba94eae6bfdb30.png"},
                    false,
                    ""
            );
            news.add(news1);
            // fetch more info in background
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String url = "https://covid-dashboard-api.aminer.cn/event/" + o.getString("_id");
                        InputStream is = new URL(url).openStream();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                        JSONObject json = new JSONObject(readAll(rd));
                        news1.setUrl(json.getJSONObject("data").getJSONArray("urls").getString(0));
                        news1.setPublisher(json.getJSONObject("data").getString("source"));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            return t;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    public void getFeed(){
        // return first 10 entries of news list
        Log.i("wtf","wdnm1");
        try {
            // fetch news list
            InputStream is = new URL("https://covid-dashboard.aminer.cn/api/dist/events.json").openStream();
            try {
                // parse JSON response
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                datas = json.getJSONArray("datas");
            } finally {
                is.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        news.clear();
        getMore();
    }
    public void getMore(){
        // return next 10 entries of news list
        try {
            int start = news.size();
            int n = datas.length();
            ArrayList<Thread> threadpool = new ArrayList<>();
            for (int i=start; i<n && i<start+10; ++i) {
                Log.i("trying adding news #", Integer.toString(i));
                // extract title etc,.
                final JSONObject o = datas.getJSONObject(i);
                Thread t = addNews(o);
                threadpool.add(t);
            }
            for (Thread t: threadpool) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public News getItem(int position){
        return news.get(position);
    }
    public ArrayList<News> getAll(){
        return news;
    }
    public ArrayList<Map<String, Object>> getAllItems(){
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        for (News piece : news){
            HashMap<String, Object> item = new HashMap<>();
            item.put("title", piece.getTitle());
            item.put("date", piece.getDate());
            data.add(item);
        }
        return data;
    }
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
}
