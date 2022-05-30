package com.example.myapplication.custom_filter_activity;

import android.content.Context;

import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


public class StringAdapter extends ArrayAdapter<String> {
    private String[] taglist;
    public StringAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
        taglist = objects;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }



    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();
            ArrayList<String> suggestions = new ArrayList<>();
            if (constraint==null || constraint.length()==0){
                suggestions.addAll(Arrays.asList(taglist));
            }else {
                String filterPatern = constraint.toString().toLowerCase().trim();
                for (String s:taglist){

                    if (s.toLowerCase().contains(filterPatern)){
                        suggestions.add(s);
                    }
                }
            }
            filterResults.values=suggestions;
            filterResults.count=suggestions.size();

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {






        }

    };
    public ArrayList<String> reverseArrayList(ArrayList<String> alist)
    {
        // Arraylist for storing reversed elements
        ArrayList<String> revArrayList = new ArrayList<>();
        for (int i = alist.size() - 1; i >= 0; i--) {

            // Append the elements in reverse order
            revArrayList.add(alist.get(i));
        }

        // Return the reversed arraylist
        return revArrayList;
    }
}
