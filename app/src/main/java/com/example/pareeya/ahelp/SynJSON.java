package com.example.pareeya.ahelp;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * Created by pareeya on 1/12/2017.
 */

public class SynJSON extends AsyncTask<String, Void, String> {


    private Context context;

    public SynJSON(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            OkHttpClient okHttpClient = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            Request request = builder.url(strings[0]).build();
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();



        } catch (Exception e) {
            e.printStackTrace();

            return null;

        }


    }
}//main Class
