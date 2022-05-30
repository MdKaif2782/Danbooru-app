package com.example.myapplication.api_handleing;

import android.os.Build;

import androidx.annotation.RequiresApi;

import net.beardbot.nhentai.NHentai;

public class nhentai {
    NHentai nHentai = new NHentai();
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String link(){
        return nHentai.galleries().getGallery(177013).get().getPages().get(1).getImage().getDownloadUrl();
    }
}
