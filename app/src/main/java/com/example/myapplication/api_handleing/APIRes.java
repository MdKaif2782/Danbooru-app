package com.example.myapplication.api_handleing;

import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class APIRes {
    public ArrayList<String> getImagesDanbooru(String tag, int pageNo){
        StringBuilder reqURL = new StringBuilder("https://danbooru.donmai.us/posts.json?&tags=");
        String finalURL ;




        if (tag.contains("(") || tag.contains("'")){
            if (tag.contains("(")) {
                tag = tag.replace("(", "%28");
                tag = tag.replace(")", "%29");
            }
            if (tag.contains("'")){
                tag=tag.replace("'","%27");
            }
        }
        reqURL.append(tag);
        reqURL.append("&page=").append(pageNo);
        finalURL=reqURL.toString();
        Log.d("tag",finalURL);
        try {
            URL url = new URL(finalURL);
            URLConnection connection = url.openConnection();
            connection.connect();
            Log.d("tag","Connected");
//            JSONParser jsonParser = new JSONParser();
//            JSONArray jsonArray = (JSONArray) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
//            Log.d("tag",jsonArray.get(1).toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
