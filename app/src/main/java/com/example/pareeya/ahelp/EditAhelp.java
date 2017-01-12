package com.example.pareeya.ahelp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**
 * Created by masterUNG on 12/8/2016 AD.
 */

public class EditAhelp extends AsyncTask<String, Void, String>{

    //Explicit
    private Context context;
    private String idString, aHelpString, latString, lngString;
    private static final String urlPHP = "http://swiftcodingthai.com/fai/edit_Ahelp_where_id.php";

    public EditAhelp(Context context,
                     String idString,
                     String aHelpString,
                     String latString,
                     String lngString) {
        this.context = context;
        this.idString = idString;
        this.aHelpString = aHelpString;
        this.latString = latString;
        this.lngString = lngString;
    }


    @Override
    protected String doInBackground(String... strings) {


        try {

            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = new FormEncodingBuilder()
                    .add("isAdd", "true")
                    .add("id", idString)
                    .add("Ahelp", aHelpString)
                    .add("Lat", latString)
                    .add("Lng",lngString)
                    .build();
            Request.Builder builder = new Request.Builder();
            Request request = builder.url(strings[0]).post(requestBody).build();
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();

        } catch (Exception e) {
            Log.d("8decV2", "e doIn ==> " + e.toString());
            return null;
        }
    }
}   // Main Class
