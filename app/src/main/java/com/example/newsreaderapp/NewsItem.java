package com.example.newsreaderapp;


public class NewsItem {
    private String title;
    private String describtion;
    private String link;
    private String date;

    public NewsItem(String title, String describtion, String link, String date) {
        this.title = title;
        this.describtion = describtion;
        this.link = link;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribtion() {
        return describtion;
    }

    public void setDescribtion(String describtion) {
        this.describtion = describtion;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

