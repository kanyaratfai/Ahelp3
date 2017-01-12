package com.example.pareeya.ahelp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class NotificationMaps extends FragmentActivity implements OnMapReadyCallback {

    //Explicit
    private String idUserString;
    private GoogleMap mMap;
    private TextView nameTextView, phoneNumberTextView;
    private ImageView imageView;
    private String nameString,phoneString;
    private double latADouble, lngADouble;
    private String aHelp; //หาว่า id ไหนเรียกให้ช่วยเหลือ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_layout);

        //Bind Widget
        nameTextView = (TextView) findViewById(R.id.textView);
        phoneNumberTextView = (TextView) findViewById(R.id.textView2);
        imageView = (ImageView) findViewById(R.id.imageView16);

        //Setting
        idUserString = getIntent().getStringExtra("idUser");
        Log.d("22decV2", "idUser==>" + idUserString);

        //id ขอความช่วยเหลือ
        try {

            SynJSON synJSON = new SynJSON(NotificationMaps.this);
            synJSON.execute("http://swiftcodingthai.com/fai/get_User_kanyarat.php");
            String strJSON = synJSON.get();
            Log.d("22decV2", "JSON==>" + strJSON);

            JSONArray jsonArray = new JSONArray(strJSON);
            for (int i=0;i<jsonArray.length();i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (idUserString.equals(jsonObject.getString("id"))) {
                    aHelp = jsonObject.getString("Ahelp");
                    latADouble = Double.parseDouble(jsonObject.getString("Lat"));
                    lngADouble = Double.parseDouble(jsonObject.getString("Lng"));
                }


            }//for

            Log.d("22decV2", "aHelp หรือ id ของคนที่เรียกให้ช่วยเหลือ==>" + aHelp);
            Log.d("22decV2", "Lat ==>" + latADouble);
            Log.d("22decV2", "Lng ==>" + lngADouble);


            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (aHelp.equals(jsonObject.getString("id"))) {
                    nameString = jsonObject.getString("Name");
                    phoneString = jsonObject.getString("Phone");
                }
            }//for

            nameTextView.setText(nameString);
            phoneNumberTextView.setText("Phone=" + phoneString);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:=" + phoneString));
                    startActivity(intent);


                }//onClick
            });


            //Edit Ahelp
            editAhelp();


        } catch (Exception e) {
            Log.d("22decV2", "e==>" + e.toString());
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }//main Method

    private void editAhelp() {
        try {
            EditAhelp editAhelp = new EditAhelp(NotificationMaps.this,
                    idUserString, "0", "0", "0");
            editAhelp.execute("http://swiftcodingthai.com/fai/edit_Ahelp_where_id_only_aHelp.php");
            Log.d("22decV2", "Result  ==>" + editAhelp.get());

        } catch (Exception e) {
            Log.d("22decV2", "e Thread ==>" + e.toString());

        }//try
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng latLng = new LatLng(latADouble, lngADouble);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        mMap.addMarker(new MarkerOptions().position(latLng));
    }//on MapReady
}//main Class
