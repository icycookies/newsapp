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
import java.util.ArrayList;
import java.util.Arrays;


public class Server {
    static public String getHtml(String url){
        //TODO: get html code from url
        String text = "Error";
        try {
            Log.i("URL!",url);
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                text = readAll(rd);
            } finally {
                is.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }
    static public String getCharset(String html){
        //TODO: get charset of page
        return "utf-8";
    }
    static public ArrayList<String> getFavor(){
        //TODO: get favored categories
        ArrayList<String> arr = new ArrayList<>();
        arr.addAll(Arrays.asList(new String[]{"收藏", "推荐", "疫情概况", "抗疫政策", "数据", "知疫学者"}));
        return arr;
    }
    static public void setFavor(ArrayList<String> favor){
        //TODO: set favored categories
    }
    static public CovidData getCovidData(){
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
