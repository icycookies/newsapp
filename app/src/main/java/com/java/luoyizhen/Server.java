package com.java.luoyizhen;

import java.util.ArrayList;
import java.util.Arrays;

public class Server {
    static public String getHtml(String url){
        //TODO: get html code from url
        return null;
    }
    static public String getCharset(String url){
        //TODO: get charset of url
        return null;
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
}
