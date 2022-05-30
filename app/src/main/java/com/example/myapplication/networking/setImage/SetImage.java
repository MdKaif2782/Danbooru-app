package com.example.myapplication.networking.setImage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SetImage {
    public Bitmap getBitmap(String url){
        Bitmap bitmap = null;
        try {
          bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
