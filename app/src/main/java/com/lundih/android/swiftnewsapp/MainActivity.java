package com.lundih.android.swiftnewsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Article>> {

    public static final String THE_GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";
    ArticleAdapter adapter;
    TextView emptyListTextView;
    Button emptyListButton;
    ProgressBar loadingProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);

        // An instance of ArticleAdapter that takes an empty list of articles as input
        adapter = new ArticleAdapter(MainActivity.this, new ArrayList<Article>());

        // Use the adapter to populate the listView
        listView.setAdapter(adapter);

        // set the view to be show in the empty state of the list view
        emptyListTextView = (TextView) findViewById(R.id.textViewEmptyState);
        listView.setEmptyView(emptyListTextView);
        emptyListButton = (Button) findViewById(R.id.buttonEmptyState);
        listView.setEmptyView(emptyListButton);

        // Hide the emptyListButton before the first attempt to connect to the server
        emptyListButton.setVisibility(View.GONE);

        // Button to allow the user to try reconnecting in case the connection was not available but was restored later
        emptyListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyListTextView.setVisibility(View.INVISIBLE);
                emptyListButton.setVisibility(View.GONE);
                instantiateLoader();
            }
        });

        // Set an item onclick listener for the list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Article currentArticle = adapter.getItem(position);
                String articleUrl = currentArticle.getWebUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW );
                intent.setData(Uri.parse(articleUrl));
                startActivity(intent);
            }
        });

        // Method to check whether the user has internet connection hence instantiate the loader or inform the user
        // of no internet connection
        instantiateLoader();
    }

    @Override
    public Loader<ArrayList<Article>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieve String values from the preferences and provide default values for the preferences.
        String orderBy  = sharedPrefs.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));
        String articleContent = sharedPrefs.getString(getString(R.string.settings_content_key), getString(R.string.settings_content_default));
        String articleCount = sharedPrefs.getString(getString(R.string.settings_article_count_key), getString(R.string.settings_article_count_default));

        Uri baseUri = Uri.parse(THE_GUARDIAN_REQUEST_URL);

        // Prepare baseUri for addition of query parameters
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameters and their values.
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("page-size", articleCount);
        uriBuilder.appendQueryParameter("q", articleContent);
        uriBuilder.appendQueryParameter("api-key","27be8a9c-8f8e-45a3-bd9a-6fdfecd124ce");

        return new ArticleLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Article>> loader, ArrayList<Article> articleList) {
        // Clear the adapter of previous data
        adapter.clear();
        // Add articles to the adapters data set if they are present. This will cause the list view to update
        if (!articleList.isEmpty()) {
            adapter.addAll(articleList);
            emptyListTextView.setVisibility(View.GONE);
        } else {
            // Handle case where the user uses the recents button or home button to leave the app but lose
            // internet connection before coming back to the swift news app
            emptyListTextView.setVisibility(View.VISIBLE);
        }
        // Get rid of the loading progress bar
        loadingProgressbar = (ProgressBar) findViewById(R.id.progressBarLoading);
        if(loadingProgressbar != null){
            loadingProgressbar.setVisibility(View.GONE);
        }
        // Set text to emptyListTextView so that if no valid list of articles is found the user gets notified
        emptyListTextView.setText(R.string.text_view_no_news_found);
        // Hide the emptyListButton button as clicking on it will not fix the issue here
        emptyListButton.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Article>> loader) {
        // Clear out existing data in the adapter
        adapter.clear();
    }

    private void instantiateLoader(){
        // Check if the user is connected to a network
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(0, null, this);
            loadingProgressbar = (ProgressBar) findViewById(R.id.progressBarLoading);
            loadingProgressbar.setVisibility(View.VISIBLE);
        }
        else{
            // Get rid of the loading progress bar
            loadingProgressbar = (ProgressBar) findViewById(R.id.progressBarLoading);
            if (loadingProgressbar != null) {
                loadingProgressbar.setVisibility(View.GONE);
            }
            // Inform the user that there is no internet connection
            emptyListTextView.setVisibility(View.VISIBLE);
            emptyListTextView.setText(R.string.text_view_no_internet);
            // Ensure the emptyListButton is visible
            emptyListButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method is called whenever an item in the options menu is selected.
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}