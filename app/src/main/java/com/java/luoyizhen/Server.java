package com.java.luoyizhen;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import static java.lang.Math.min;


public class Server {
    static private Context context;

    static public void setContext(Context context1){
        context = context1;
    }
    static public ArrayList<String> getFavor(){
        //TODO: get favored categories
        ArrayList<String> arr = new ArrayList<>();
        arr.addAll(Arrays.asList(new String[]{"历史", "推荐", "国内", "国际", "数据", "知疫学者"}));
        return arr;
    }
    static public void setFavor(ArrayList<String> favor){
        //TODO: set favored categories
    }
    private static String cachefilename() {
        return context.getFilesDir() + "cache_history";
    }
    // history
    private static ArrayList<News> history;
    static public boolean inHistory(News news) {
        if (history == null)
            getHistory();
        if (history == null)
            return false;
        for (News o: history)
            if (o.getUrl() == news.getUrl())
                return true;
        return false;
    }
    static public void addHistory(News news){
        if (history == null)
            history = new ArrayList<>();
        history.add(news);
        // write history to local files
        Log.i("Saving History list to", cachefilename());
        try {
            FileOutputStream fileOut = new FileOutputStream(cachefilename());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(history);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
    static public synchronized NewsList getHistory(){
        // load from local files
        if (history == null) {
            Log.i("Loading news list from", cachefilename());
            try {
                FileInputStream fileIn = new FileInputStream(cachefilename());
                ObjectInputStream in = new ObjectInputStream(fileIn);
                history = (ArrayList<News>) in.readObject();
                in.close();
                fileIn.close();
            } catch (Exception e) {
                e.printStackTrace();
                history = null;
                return new NewsList("History",context);
            }
        }
        return new NewsList("History",context,history);
    }

    static ArrayList<News> allnews;
    static public void initSearch() {
        // get all news
        String url = "https://covid-dashboard.aminer.cn/api/dist/events.json";
        JSONArray datas;
        allnews = new ArrayList<>();
        Log.i("-----INITIALIZING SEARCH....","");
        try {
            try (InputStream is = new URL(url).openStream()) {
                // parse JSON response -> datas
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                datas = json.getJSONArray("datas");
            }
            Log.i("-----INITIALIZING SEARCH data",Integer.toString(datas.length()));
            for (int i = 0; i < datas.length(); ++i) {
                // extract title etc,.
                final JSONObject o = datas.getJSONObject(i);
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
                allnews.add(news1);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    static public NewsList search(String query){
        int cnt = 0, maxcnt = 20;
        ArrayList<News> result = new ArrayList<>();
        Log.i("SEARCHING Q=",query);
        Log.i("ALLNEWS SIZE:",Integer.toString(allnews.size()));
        for (News o: allnews) {
            if (o.getTitle().contains(query)) {
                Log.i("GOT TITLE", o.getTitle());
                result.add(o);
                cnt+=1;
            }
            if (cnt >= maxcnt)
                break;
        }
        Log.i("SEARCH RESULT SIZE:", Integer.toString(result.size()));
        return new NewsList("result",context,result);
    }
    static public Entity getEntity(String query){
        // return entity if exists
        String url = "https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity=" + query;
        try (InputStream is = new URL(url).openStream()) {
            Log.i("tryinput", "entity");
            // parse JSON response (first entry) -> data
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            if (!json.getString("msg").equals("success")) return null;
            JSONObject data = json.getJSONArray("data").getJSONObject(0);
            JSONArray relobj = data.getJSONObject("abstractInfo").getJSONObject("COVID").getJSONArray("relations");
            int nrelation = min(relobj.length(), 10);
            ArrayList<Map.Entry<String, String>> relation = new ArrayList<>();
            for (int i=0; i<nrelation; ++i) {
                JSONObject o = relobj.getJSONObject(i);
                relation.add(new AbstractMap.SimpleEntry<String, String>(o.getString("relation"), o.getString("label")));
            }
            return new Entity(
                    data.getString("label"),
                    data.getJSONObject("abstractInfo").getString("baidu"),
                    relation,
                    data.getString("img"));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
                        if (level == 1 && name.substring(0,6).equals("China|")) {
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
        Log.i("WDNM","WDNM");
        return null;
    }

    public static ArrayList<Expert> getExperts() throws Exception{
        String url = "https://innovaapi.aminer.cn/predictor/api/v1/valhalla/highlight/get_ncov_expers_list?v=2";
        ArrayList<Expert> experts = new ArrayList<>();
        Log.i("tryinput", "wdnmd");
        try (InputStream is = new URL(url).openStream()) {
            Log.i("tryinput", "entity");
            // parse JSON response (first entry) -> data
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            if (!json.getString("message").equals("success")) return null;
            JSONArray data = json.getJSONArray("data");
            // convert to Expert object
            for (int i=0; i<data.length(); ++i) {
                Log.i("i=", Integer.toString(i));
                JSONObject o = data.getJSONObject(i);
                JSONObject p = o.getJSONObject("profile");
                experts.add(new Expert(
                        o.getString("name_zh"),
                        p.getString("position"),
                        p.getString("affiliation"),
                        o.getString("avatar"),
                        p.getString("homepage")
                ));
            }
        }
        return experts;
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
