package com.example.myapplication.api_handleing;

import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.HttpCookie;
import java.util.List;
import java.util.Map;

public class SetConnection {
    public void CL(String url) {
        CloudFlare cloudFlare = new CloudFlare(url);
        cloudFlare.setUser_agent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        cloudFlare.getCookies(new CloudFlare.cfCallback() {
            @Override
            public void onSuccess(List<HttpCookie> cookieList) {
                Map<String, String> cookies = CloudFlare.List2Map(cookieList);
                Log.d("COOKIES : ", cookies.toString());

                try {
                    Connection.Response response = Jsoup.connect("YOUR URL HERE").userAgent("YOUR USER AGENT HERE").cookies(cookies).execute();
                    Document doc = response.parse();
                    Log.d("THE DOCUMENT : ", doc.toString());

                } catch (Exception M){
                    M.printStackTrace();
                }

            }

            @Override
            public void onFail() {
                Log.d("COOKIES","OMG IT FAILED!!!");
            }
        });
    }
}
