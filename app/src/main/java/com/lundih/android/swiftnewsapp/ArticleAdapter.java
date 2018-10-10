package com.lundih.android.swiftnewsapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class ArticleAdapter extends ArrayAdapter<Article>{
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item, parent, false);
        }

        Article currentArticle = getItem(position);

        TextView title = (TextView) listItemView.findViewById(R.id.textViewTitle);
        title.setText(currentArticle.getTitle());

        TextView section = (TextView) listItemView.findViewById(R.id.textViewSection);
        // Set background colour for the section circle depending on the section of the article
        GradientDrawable sectionCircle = (GradientDrawable) section.getBackground();
        int sectionColour = getSectionColour(getContext(), currentArticle.getSection());
        sectionCircle.setColor(sectionColour);
        section.setText(currentArticle.getSection());

        TextView date = (TextView) listItemView.findViewById(R.id.textViewDate);
        // Get well formatted date and time
        date.setText(formatDate(currentArticle.getDate()));

        TextView author = (TextView) listItemView.findViewById(R.id.textViewAuthor);
        String authorText = "";
        if (currentArticle.getAuthor() != ""){
            authorText = getContext().getString(R.string.text_to_go_with_author) + " " + currentArticle.getAuthor();
        }
        author.setText(authorText);

        return listItemView;
    }

    public ArticleAdapter(Activity context, ArrayList<Article> articles){
        super(context, 0, articles);
    }

    // Return colour of section circle
    private int getSectionColour(Context context, String section){
        int category;
        int colour;

        // Group similar sections together to have almost similar sections show up with the same colour
        if (section.toLowerCase().contains("science") || section.toLowerCase().contains("ology") ||
                section.toLowerCase().contains("physics") || section.toLowerCase().contains("math")){
            category = 1;
        } else if (section.toLowerCase().contains("art") || section.toLowerCase().contains("books") ||
                section.toLowerCase().contains("culture") || section.toLowerCase().contains("film") ||
                section.toLowerCase().contains("fashion")){
            category = 2;
        } else if (section.toLowerCase().contains("sport") || section.toLowerCase().contains("football") ||
                section.toLowerCase().contains("ball")){
            category = 3;
        } else {
            category = 0;
        }

        switch(category){
            case 1: colour = ContextCompat.getColor(context, R.color.category1);
                break;
            case 2: colour = ContextCompat.getColor(context, R.color.category2);
                break;
            case 3: colour = ContextCompat.getColor(context, R.color.category3);
                break;
            default: colour = ContextCompat.getColor(context, R.color.category4);
                break;
        }
        return colour;
    }

    // Return well formatted date and time
    private  String formatDate(String date) {
        String dateAndTime = date;

        // Remove the letter "T"
        if (date.contains("T")){
            String [] dateParts = date.split("T");
            String dateHalf = dateParts[0];
            String timeHalf = dateParts[1];
            dateAndTime = getContext().getString(R.string.text_to_go_with_date) + " " + dateHalf + " " + getContext().getString(R.string.text_to_go_with_time) + " " + timeHalf;
        }

        // Remove the letter "Z"
        if (date.contains("Z")){
            StringBuilder stringBuilder = new StringBuilder(dateAndTime);
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            dateAndTime = stringBuilder.toString();
        }
        return dateAndTime;
    }
}