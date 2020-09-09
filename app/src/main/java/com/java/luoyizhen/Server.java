package com.java.luoyizhen;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;


public class Server {
    static public ArrayList<String> getFavor(){
        //TODO: get favored categories
        ArrayList<String> arr = new ArrayList<>();
        arr.addAll(Arrays.asList(new String[]{"收藏", "推荐", "疫情概况", "抗疫政策", "数据", "知疫学者"}));
        return arr;
    }
    static public void setFavor(ArrayList<String> favor){
        //TODO: set favored categories
    }
    static public void addHistory(News news){
        //TODO: add news to history
    }
    static public synchronized NewsList getHistory(){
        //TODO: return history news
        return null;
    }
    static public CovidData getCovidData(){
        CovidData coviddata = null;
        try {
            // fetch news list
            try (InputStream is = new URL("https://covid-dashboard.aminer.cn/api/dist/epidemic.json").openStream()) {
                ArrayList<Map.Entry<String, Integer>> province = new ArrayList<>(), country = new ArrayList<>();
                // parse JSON response
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                Iterator<String> keys = json.keys();
                while (keys.hasNext()) {
                    String name = keys.next();
                    if (json.get(name) instanceof JSONObject) {
                        // do something with jsonObject here
                        int level = 0;
                        for (int i = 0; i < name.length(); i++) {
                            if (name.charAt(i) == '|') {
                                level++;
                            }
                        }
                        JSONArray data = json.getJSONObject(name).getJSONArray("data");
                        JSONArray lastdata = data.getJSONArray(data.length() - 1);
                        int contracted = lastdata.getInt(0);
                        if (level == 0) {
                            country.add(new AbstractMap.SimpleEntry<String, Integer>(name, contracted));

                        }
                        if (level == 1) {
                            province.add(new AbstractMap.SimpleEntry<String, Integer>(name, contracted));
                        }
                    }
                }
                JSONArray data = json.getJSONObject("China").getJSONArray("data");
                JSONArray lastdata = data.getJSONArray(data.length() - 1);
                int confirmed = lastdata.getInt(0);
                int cured = lastdata.getInt(2);
                int dead = lastdata.getInt(3);
                coviddata = new CovidData(
                        confirmed,
                        confirmed - cured - dead,
                        dead,
                        province,
                        country,
                        null,
                        null
                );
            }
            return coviddata;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}
