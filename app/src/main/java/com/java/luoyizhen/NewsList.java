package com.java.luoyizhen;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

public class NewsList implements java.io.Serializable {
    private String category;
    private ArrayList<News> news;
    private Context context;
    private int numpage = 0;

    NewsList(String category, Context context){
        this.category = category;
        this.news = new ArrayList<>();
        this.context = context;
    }
    NewsList(String category, Context context, ArrayList<News> news){
        this.category = category;
        this.news = news;
        this.context = context;
    }
    public void setCategory(String category){
        this.category = category;
    }
    public void setContext(Context context){
        this.context = context;
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
                        try {
                            news1.setPublisher(json.getJSONObject("data").getString("source"));
                        }
                        catch (Exception e) {
                        }
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
    private String cachefilename() {
        return context.getFilesDir() + "cache_newslist_" + category;
    }
    public void saveCache() {
        Log.i("Saving news list to", cachefilename());
        try {
            FileOutputStream fileOut = new FileOutputStream(cachefilename());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(news);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
    private void loadCache() {
        Log.i("Loading news list from",cachefilename());
        try {
            FileInputStream fileIn = new FileInputStream(cachefilename());
            ObjectInputStream in = new ObjectInputStream(fileIn);
            news = (ArrayList<News>) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("newslist class not found");
            c.printStackTrace();
            return;
        }
    }
    public void view(int index){
        News t = news.get(index);
        if (!t.viewed) {
            Log.i("aha", "view");
            t.viewed = true;
            saveCache();
        }
    }
    public void getFeed(String topic) {
        // return first 10 entries of news list
        numpage = 0;
        getMore(topic);
    }
    public void getFeed(int clusterId){
        //TODO: get feed from the cluster
    }
    public synchronized NewsList getMore(String topic){
        numpage += 1;
        Log.i("Getting news list page=", Integer.toString(numpage));
        news.clear();
        try {
            // fetch news list
            JSONArray datas;
            try (InputStream is = new URL("https://covid-dashboard.aminer.cn/api/events/list?page=" + numpage).openStream()) {
                // parse JSON response -> datas
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                datas = json.getJSONArray("data");
            }
            // add news to list & launch detail-fetch threads
            ArrayList<Thread> threadpool = new ArrayList<>();
            for (int i=0; i<datas.length(); ++i) {
                // extract title etc,.
                final JSONObject o = datas.getJSONObject(i);
                Thread t = addNews(o);
                threadpool.add(t);
            }
            // join threads
            for (Thread t: threadpool) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            saveCache();
        }
        catch (IOException e) {
            Log.i("Loading cached...","");
            loadCache();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(Integer.toString(news.size()), "news fetched");
        return this;
    }
    public News getItem(int position){
        return news.get(position);
    }
    public synchronized ArrayList<News> getAll(){
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
    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        }
    }
}
