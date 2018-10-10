package com.lundih.android.swiftnewsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

class ArticleLoader extends AsyncTaskLoader<ArrayList<Article>>{
    private String mUrl;

    @Override
    public ArrayList<Article> loadInBackground() {
        if (mUrl == null){
            return null;
        }
        // Perform the network request, parse the response, and extract a list of earthquakes.
        ArrayList<Article> articles;
        articles = BackgroundJob.getArticlesList(mUrl);

        return articles;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    public ArticleLoader(Context context, String url){
        super(context);
        mUrl = url;
    }
}