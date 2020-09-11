package com.java.luoyizhen;


public class Expert {
    private String name;
    private String position;
    private String affiliation;
    private String avatar;
    private String homepage;

    Expert(String name, String position, String affiliation, String avatar, String homepage){
        this.name = name;
        this.position = position;
        this.affiliation = affiliation;
        this.avatar = avatar;
        this.homepage = homepage;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getHomepage() {
        return homepage;
    }
}
