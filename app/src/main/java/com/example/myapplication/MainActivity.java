package com.example.myapplication;



import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.example.myapplication.custom_filter_activity.StringAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public int pageOneItemCount = 0;
    public MultiAutoCompleteTextView searchBar;
    public String inputText;
    public ImageView imageView;
    ArrayList<String> tagList = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();
    String httpReqBody;
    public InputMethodManager inputManager;
    public int count =0;
    public int pageCount = 1;
    public ArrayList<String> tagSuggestions = new ArrayList<>();
    public ArrayAdapter<String> tagSugAdapter;
    public ArrayList<String> links = new ArrayList<>();
    public ArrayAdapter<String> adapter;
    public int httpCounter=1;
    public String finalUrl ;
    public CheckBox nsfw,sfw;


    public void refresh(){
        adapter=   new StringAdapter(this, android.R.layout.simple_dropdown_item_1line,
                tagList.toArray(new String[tagList.size()] )) ;
   //     adapter1 = new StringAdapter(this, android.R.layout.simple_dropdown_item_1line, tagList.toArray(new String[tagList.size()] )) ;
    }

    public void refreshLinks()  {
        boolean NSFW = nsfw.isChecked();
        boolean SFW = sfw.isChecked();


        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(httpReqBody);
            links.clear();
           for (int i=0; i<jsonArray.length();i++){
               JSONObject object = (JSONObject) jsonArray.get(i);
                try {
                    String rating = object.get("rating").toString();
                    if (NSFW){
                        if (rating.equalsIgnoreCase("e")){
                            links.add(object.get("file_url").toString());
                        }
                    }  if (SFW){
                        if (rating.equalsIgnoreCase("q")){
                            links.add(object.get("file_url").toString());
                        }
                    } else if (!NSFW && !SFW){
                        links.add(object.get("file_url").toString());
                    }


                }catch (JSONException e){
                    e.printStackTrace();
                }


           }



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    Context applicationContext;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applicationContext = this.getApplicationContext();


        nsfw = findViewById(R.id.nsfw_checkbox);
        sfw = findViewById(R.id.sfw_checkbox);
        searchBar = findViewById(R.id.textInputSearchBar);
       imageView = findViewById(R.id.TextView1);
       inputText = searchBar.getText().toString();
         inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Button nextImage = findViewById(R.id.next_image);
        nextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                MultiAutoCompleteTextView view = searchBar;
                String text = view.getText().toString();
                if (count == links.size()){
                    pageCount++;
                    links.clear();
                    count=0;
                    request(text,(pageCount));
                    if (pageCount==2){
                        count= (pageOneItemCount-1);
                    }

                }
                imageView.setImageDrawable(null);
                TextView page_text_view = findViewById(R.id.page_no_view);
                page_text_view.setText(pageCount+" : "+count+"/"+links.size());
                if (links.size()>0) {
                    new DownloadImageTask(imageView).execute(links.get(count));
                }
                Log.d("tag",text);
            }
        });
        Button prevImage = findViewById(R.id.previous_image);
        prevImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiAutoCompleteTextView view = searchBar;
                String text = view.getText().toString();
                count--;
                if (count == 0){
                    count=(links.size()-1);
                    pageCount--;
                    request(text,pageCount);
                    refreshLinks();

                }



                imageView.setImageDrawable(null);
                TextView page_text_view = findViewById(R.id.page_no_view);
                page_text_view.setText(pageCount+" : "+count+"/"+links.size());
                new DownloadImageTask(imageView).execute(links.get(count));
                Log.d("tag",text);
            }
        });





        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               httpCounter++;
               String baseLink = "https://danbooru.donmai.us/wiki_pages.json?commit=Search&search%5Border%5D=post_count&limit=100&search%5Btitle_or_body_matches%5D=";
               StringBuilder url = new StringBuilder(baseLink);
               String textOnBar = searchBar.getText().toString();



               if (!textOnBar.contains(", ")){
                   url.append(textOnBar);
               } else {
                   String[] tagarray = textOnBar.split(", ");
                   if (tagarray.length>1){
                       url.append(tagarray[1]);

                   }
               }
               finalUrl = url.toString();
                Log.d("finalurl",finalUrl);
                requestTags(url.toString());



                tagSugAdapter = new StringAdapter(getApplicationContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        tagSuggestions.toArray(new String[tagSuggestions.size()]));
                searchBar.setAdapter(tagSugAdapter);
                searchBar.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());


            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });



         findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 try {
                     count=0;
                     pageCount=1;
                     inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                     AutoCompleteTextView view = searchBar;
                     String text = view.getText().toString();
                     request(text,1);

                     imageView.setImageDrawable(null);
                     TextView page_text_view = findViewById(R.id.page_no_view);
                     pageOneItemCount=links.size();
                     page_text_view.setText(pageCount+" : "+count+"/"+links.size());
                   new DownloadImageTask(imageView).execute(links.get(0));
                     Log.d("tag",text);

                 }catch (Exception e){
                     Log.d("tag", String.valueOf(e));
                 }
             }
         });


    }

public void requestTags(String url){
    String spltUrl[] = url.split("body_matches%5D=");
    if (spltUrl.length>1) {
        String textOnBar = searchBar.getText().toString();
        Request request = new Request.Builder()
                .url(url.toString()).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("tag", "fetch failed\n" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                httpReqBody = response.body().string();

                try {
                    JSONArray array = new JSONArray(httpReqBody);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = (JSONObject) array.get(i);
                        String tag_name = object.get("title").toString().toLowerCase();

                        if (textOnBar.contains(",")) {
                            String[] splitTxt = textOnBar.split(", ");
                            if (splitTxt.length > 1) {
                                StringBuilder splt = new StringBuilder(splitTxt[1]);
                                if (tag_name.contains(splt.toString())) {
                                    tagSuggestions.add(tag_name);
                                }
                            }
                        } else if (tag_name.contains(textOnBar)) {
                            tagSuggestions.add(tag_name);
                        }

                    }
                    for (Object s : tagSuggestions.toArray()) {

                        String matchTag = "";
                        if (finalUrl.split("body_matches%5D=").length>1){
                             matchTag=    finalUrl.split("body_matches%5D=")[1];
                        }
                        if (!s.toString().contains(matchTag)) {
                            Log.d("tag", "tag removed: " + s.toString() + " for not matching with " + matchTag);
                            tagSuggestions.remove(s.toString());
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });

    }
}

    public void request(String tag1, int pageNo){
        StringBuilder url = new StringBuilder("https://danbooru.donmai.us/posts.json?tags=");
        if (tag1.contains(",")) {
            String tags[] = tag1.split(",");
            for (String tag : tags) {
                url.append(tag);
                url.append("+");
            }
        } else {
            url.append(tag1);
        }

        url.append("&page=");
        url.append(pageNo);
        Log.d("tag",url.toString());

        Request request = new Request.Builder().url(url.toString()).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Toast.makeText(getApplicationContext(),"Connection Failed",Toast.LENGTH_LONG).show();
                Log.d("expt",e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) {
                assert response.body() != null;
                try {
                   httpReqBody = response.body().string();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        refreshLinks();

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                bmImage.setImageBitmap(result);
            }catch (RuntimeException e){
                Log.e("error",e.toString());
            }


        }
    }

    public void createHandler(){
        tagSugAdapter = new StringAdapter(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,
                tagSuggestions.toArray(new String[tagSuggestions.size()]));
        searchBar.setAdapter(tagSugAdapter);
        searchBar.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        searchBar.showDropDown();
    }



}