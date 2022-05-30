package com.example.myapplication.onClick;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.networking.setImage.SetImage;

import java.io.InputStream;
import java.util.Objects;

public class searchBar implements View.OnClickListener {
    MainActivity activity = new MainActivity();
    Context context;

    public searchBar(Context ctx){
        context = ctx;
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.searchButton:
               try {
                   AutoCompleteTextView view = activity.searchBar;
                   String text = view.getText().toString();
                   activity.request(text,1);
                   activity.refreshLinks();

//                   new DownloadImageTask(imageView).execute(activity.links.get(1));
                   Log.d("tag",text);
                    activity.inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


               }catch (Exception e){
                   Log.d("tag", String.valueOf(e));
               }
               break;
       }
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
            bmImage.setImageBitmap(result);
        }
    }
}
