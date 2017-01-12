package com.example.pareeya.ahelp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ShowNotification extends AppCompatActivity {

    //Explicit
    private String idUserString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notification);

        //Setting
        idUserString = getIntent().getStringExtra("idUser");
        Log.d("22decV2", "idUser==>" + idUserString);

        //Edit Ahelp
        try {
            EditAhelp editAhelp = new EditAhelp(ShowNotification.this,
                    idUserString, "0", "0", "0");
            editAhelp.execute("http://swiftcodingthai.com/fai/edit_Ahelp_where_id_only_aHelp.php");
            Log.d("22decV2", "Result  ==>" + editAhelp.get());

        } catch (Exception e) {
            Log.d("22decV2", "e Thread ==>" + e.toString());

        }//try

    }//Main Method


}//Main Class
