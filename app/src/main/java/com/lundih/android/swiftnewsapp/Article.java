package com.lundih.android.swiftnewsapp;

class Article {

    private String mTitle;
    private String mSection;
    private String mWebUrl;
    private String mDate;
    private String mAuthor;

    public String getTitle() {
        return mTitle;
    }

    public String getSection() {
        return mSection;
    }

    public String getWebUrl() {
        return mWebUrl;
    }

    public String getDate() {
        return mDate;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public Article(String title, String section, String webUrl){
        mTitle = title;
        mSection = section;
        mWebUrl = webUrl;
    }

    public Article(String title, String section, String webUrl, String date){
        mTitle = title;
        mSection = section;
        mWebUrl = webUrl;
        mDate = date;
    }

    public Article(String title, String section, String webUrl, String date, String author){
        mTitle = title;
        mSection = section;
        mWebUrl = webUrl;
        mDate = date;
        mAuthor = author;
    }
}