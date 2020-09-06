package com.java.luoyizhen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class CovidData {
    private int allCount, curCount, deadCount;
    private ArrayList<Map.Entry<String, Integer>> province, country;
    private ArrayList<Integer> growDomestic, growGlobal;

    CovidData(int allCount, int curCount, int deadCount,
              ArrayList<Map.Entry<String, Integer>> province, ArrayList<Map.Entry<String, Integer>> country,
              ArrayList<Integer> growDomestic, ArrayList<Integer> growGlobal){
        this.allCount = allCount;
        this.curCount = curCount;
        this.deadCount = deadCount;
        this.province = province;
        this.country = country;
        this.growDomestic = growDomestic;
        this.growGlobal = growGlobal;
    }
    int getAllCount(){
        return allCount;
    }
    int getCurCount(){
        return curCount;
    }
    int getDeadCount(){
        return deadCount;
    }
    ArrayList<Map.Entry<String, Integer>> getProvince(){
        province.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        return province;
    }
    ArrayList<Map.Entry<String, Integer>> getCountry(){
        country.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        return country;
    }
    ArrayList<Integer> getGrowDomestic(){
        return growDomestic;
    }
    ArrayList<Integer> getGrowGlobal(){
        return growGlobal;
    }
    void setAllCount(int allCount){
        this.allCount = allCount;
    }
    void setCurCount(int curCount){
        this.curCount = curCount;
    }
    void setDeadCount(int deadCount){
        this.deadCount = deadCount;
    }
    void setProvince(ArrayList<Map.Entry<String, Integer>> province){
        this.province = province;
    }
    void setCountry(ArrayList<Map.Entry<String, Integer>> country){
        this.country = country;
    }
    void setGrowDomestic(ArrayList<Integer> growDomestic){
        this.growDomestic = growDomestic;
    }
    void setGrowGlobal(ArrayList<Integer> growGlobal){
        this.growDomestic = growGlobal;
    }
}
