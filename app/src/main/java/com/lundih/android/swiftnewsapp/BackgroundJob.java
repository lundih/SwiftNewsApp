package com.lundih.android.swiftnewsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public final class BackgroundJob {

    public static final String LOG_TAG = BackgroundJob.class.getName();

    private BackgroundJob(){}

    public static ArrayList<Article> getArticlesList(String urlString){
        ArrayList<Article> articlesList = new ArrayList<>();

        if (urlString != null && !urlString.isEmpty()){
            // Create a URL object from the urlString
            URL url = createUrl(urlString);

            // Perform a HTTP request on the URL
            String jsonResponse = "";
            try{
                jsonResponse = makeHttpRequest(url);
            }catch  (IOException e){
                Log.e(LOG_TAG, "IOException encountered.", e);
            }
            if(!jsonResponse.isEmpty()){
                // Try to parse the response
                try{
                    JSONObject rootJsonObject = new JSONObject(jsonResponse);
                    JSONObject response = rootJsonObject.optJSONObject("response");
                    JSONArray results = response.optJSONArray("results");

                    // Loop through the results
                    for (int i = 0; i < results.length(); i++){
                        JSONObject article = results.getJSONObject(i);
                        String title = article.optString("webTitle");
                        String section = article.optString("sectionName");
                        String webUrl = article.optString("webUrl");
                        JSONArray tags = article.optJSONArray("tags");
                        // If a publication date exists then get it.
                        String date = article.optString("webPublicationDate");
                        // If an author name exists then get that as well
                        JSONObject contributorInfo = tags.optJSONObject(0);
                        String author = "";
                        if (contributorInfo != null){
                            author = contributorInfo.optString("webTitle");
                        }

                        // Create an object instance of the Article class
                        articlesList.add(new Article(title, section, webUrl, date, author));
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "There was a problem parsing the JSON results.", e);
                }
            }
        }
        return articlesList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        if (url == null){
            return jsonResponse;
        }
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            // Check the server response and continue if OK else print response code to logs
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Encountered an IOException", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}