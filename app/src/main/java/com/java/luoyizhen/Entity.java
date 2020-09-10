package com.java.luoyizhen;

import java.util.ArrayList;
import java.util.Map;

public class Entity {
    private String name;
    private String description;
    private ArrayList<Map.Entry<String, String>> relation;
    private String image;

    Entity(String name, String description, ArrayList<Map.Entry<String, String>> relation, String image){
        this.name = name;
        this.description = description;
        this.relation = relation;
        this.image = image;
    }
    String getName(){
        return name;
    }
    String getDescription(){
        return description;
    }
    ArrayList<Map.Entry<String, String>> getRelation(){
        return relation;
    }
    String getImage(){
        return image;
    }
}
