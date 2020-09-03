package com.java.luoyizhen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        //TODO: get news from the server
    }
    public News getItem(int position){
        return news.get(position);
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
}
