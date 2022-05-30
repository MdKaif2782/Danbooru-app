package com.example.myapplication.api_handleing;

import android.util.Log;
import android.webkit.HttpAuthHandler;

import java.util.ArrayList;

public class JSONRequest {
    public ArrayList<String> request(){
        HttpHandler handler = new HttpHandler();
        String url = "https://danbooru.donmai.us/posts.json?&tags=pussy";
        String jsonStr = handler.makeServiceCall(url);
        Log.d("tag",jsonStr);

        return null;
    }
}
