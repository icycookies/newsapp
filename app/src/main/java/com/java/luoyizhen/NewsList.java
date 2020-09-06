package com.java.luoyizhen;

import android.util.Log;

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

//import java.net.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewsList {
    private String category;
    private ArrayList<News> news;

    NewsList(String category){
        this.category = category;
        this.news = new ArrayList<>();
    }
    public void setCategory(String category){
        this.category = category;
    }
    public void getFeed(){
        Log.i("wtf","wdnm1");
        try {
            InputStream is = new URL("https://covid-dashboard.aminer.cn/api/dist/events.json").openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                JSONArray datas = json.getJSONArray("datas");
                int n = datas.length();
                for (int i=0; i<n && i<10; ++i) {
                    JSONObject o = datas.getJSONObject(i);
                    News news1 = new News(
                        o.getString("title"),
                        o.getString("time"),
                        "wdnmd",
                        "http://example.com",
                        "The quick brown fox jumps over a lazy dog.",
                        new String[] {"http://p5.itc.cn/q_70/images03/20200807/9e87c806515a41aeb0ba94eae6bfdb30.png"},
                        false,
                        ""
                    );
                    news.add(news1);
                    String url1 = "https://covid-dashboard-api.aminer.cn/event/" + o.getString("_id");
                    InputStream is1 = new URL(url1).openStream();
                    BufferedReader rd1 = new BufferedReader(new InputStreamReader(is1, Charset.forName("UTF-8")));
                    JSONObject json1 = new JSONObject(readAll(rd1));
                    try {
                        news1.setUrl(json1.getJSONObject("data").getJSONArray("urls").getString(0));
                        news1.setPublisher(json1.getJSONObject("data").getString("source"));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                is.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getMore(){
        //TODO: get more news
        Log.i("getmore", "getmore");
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
