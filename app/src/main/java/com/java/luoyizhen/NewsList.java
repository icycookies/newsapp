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
//        try {
//            JSONObject json = readJsonFromUrl("https://covid-dashboard.aminer.cn/api/dist/events.json");
//            Log.i("JSON",json.toString());
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
        News news1 = new News(
            "Firefox 79稳定版导致部分Mac用户陷入不明卡顿",
            "2020-08-07 16:18",
            "cnBeta",
            "https://www.sohu.com/a/411973899_99956743",
            "Firefox 79 是 Mozilla 目前提供的最新稳定版本，包含了功能和安全性方面的全面保障。在 Windows 平台上，Firefox 79 的 WebRender 可充分发挥 Intel 和 AMD 显卡的性能。然而在 macOS 上，Firefox 79 的改进不仅不明显（主要集中在底层），甚至还引入了一个奇怪的 bug 。\nSoftpedia 编辑 Bogdan Popa 援引某位同事的话称，在更新到 Firefox 79 之后，他感觉浏览器的响应变得非常缓慢，偶尔还会卡住，然后又恢复“正常”的工作状态。\n现在我们只能寄希望于 Mozilla 在新版中彻底修复困扰 macOS 用户的这个 bug（预计 8 月 25 日发布），等不了的用户，不妨想想其它办法（回滚至旧版、或切换到其它浏览器）。",
                new String[] {"http://p5.itc.cn/q_70/images03/20200807/9e87c806515a41aeb0ba94eae6bfdb30.png"},
            false,
            ""
        );
        //news.clear();
        for (int i = 0; i < 10; i++)
            news.add(news1);
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
