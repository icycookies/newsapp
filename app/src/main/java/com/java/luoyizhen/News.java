package com.java.luoyizhen;

public class News {
    private String title;               //新闻标题
    private String date;                //发布时间
    private String publisher;           //发布者
    private String url;                 //正文链接
    private String content;             //正文内容
    private String[] image;             //图片链接

    private boolean viewed;             //是否已读

    //聚类
    //private News[] related;             //相关新闻
    //private double[] score;             //相关性

    News(final String title, final String date, final String publisher, final String url,
         final String content, final String[] image, final boolean viewed){
        this.title = title;
        this.date = date;
        this.publisher = publisher;
        this.url = url;
        this.content = content;
        this.image = image;
        this.viewed = viewed;
    }

    //getter
    public String getTitle(){
        return title;
    }
    public String getDate(){
        return date;
    }
    public String getPublisher(){
        return publisher;
    }
    public String getUrl(){
        return url;
    }
    public String getContent(){
        return content;
    }
    public String[] getImage(){
        return image;
    }
    public boolean isViewed() {
        return viewed;
    }

    //setter
    public void setTitle(String title){
        this.title = title;
    }
    public void setDate(String date){
        this.date = date;
    }
    public void setPublisher(String publisher){
        this.publisher = publisher;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public void setContent(String content){
        this.content = content;
    }
    public void setImage(String[] image){
        this.image = image;
    }
    public void view(){
        //TODO: add cache path of the file
        this.viewed = true;
    }
}
