package com.example.pareeya.ahelp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class HomeActivity extends AppCompatActivity {

    //Explicit
    private Button button;
    private ImageView img;
    private String truePasswordString, userPasswordString,
            idUserString, nameString, idCallString;
    private boolean statusABoolean = true;
    private LocationManager locationManager;
    private Criteria criteria;
    private double latADouble = 13.859882, lngADouble=100.481604;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //setting ขออนุญาติใช้ server
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);

        // image animation
        // Load the ImageView that will host the animation and
        // set its background to our AnimationDrawable XML resource.
        img = (ImageView) findViewById(R.id.imageView3);
        img.setBackgroundResource(R.drawable.butalarm);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();

        //Call 1669
        call1669();

        //Img Controller
        imgController();

        //Find Id user
        findIDuser();

        //My Loop
        myLoop();


    }   // Main Method

    @Override
    protected void onResume() {
        super.onResume();

        Location networkLocation = myFindLocation(LocationManager.NETWORK_PROVIDER);
        if (networkLocation != null) {
            latADouble = networkLocation.getLatitude();
            lngADouble = networkLocation.getLongitude();

        }

        Location gpsLocation = myFindLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null) {
            latADouble = gpsLocation.getLatitude();
            lngADouble = gpsLocation.getLongitude();
        }
        Log.d("22decV3", "lat==>" + latADouble);
        Log.d("22decV3", "lng==>" + lngADouble);
    }//onResume

    @Override
    protected void onStop() {
        super.onStop();

        locationManager.removeUpdates(locationListener);

    }

    public Location myFindLocation(String strProvider) {

        Location location = null;
        if (locationManager.isProviderEnabled(strProvider)) {

            locationManager.requestLocationUpdates(strProvider,1000,10,locationListener);//การค้นหาพิกัดทุกๆ1วินาที,ถ้ามีการเปลี่ยนพิกัด10เมตร ให้ทำการค้นหา


        }

        return location;
    }

    //location อัตโนมัติ

    public LocationListener locationListener= new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            latADouble = location.getLatitude();
            lngADouble = location.getLongitude();

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void findIDuser() {
        //Find idUser
        try {

            SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.database_name,
                    MODE_PRIVATE, null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM userTABLE", null);
            cursor.moveToFirst();
            nameString = cursor.getString(cursor.getColumnIndex(MyManage.column_Name));
            truePasswordString = cursor.getString(cursor.getColumnIndex(MyManage.column_Password));
            Log.d("8decV3", "truePass ==> " + truePasswordString);
            cursor.close();



            FindIDuser findIDuser = new FindIDuser(HomeActivity.this,
                    nameString, truePasswordString);
            findIDuser.execute();
            String strJSON = findIDuser.get();
            Log.d("8decV3", "JSON ==> " + strJSON);

            JSONArray jsonArray = new JSONArray(strJSON);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            idUserString = jsonObject.getString("id");

            Log.d("8decV3", "idUser ==> " + idUserString);

//            //Find idCall
//            findIDcall();

        } catch (Exception e) {
            e.printStackTrace();
        }   // try
    }

    private void myLoop() {

        //My To do
        try {

            SynAhelp synAhelp = new SynAhelp(HomeActivity.this, idUserString);
            synAhelp.execute();
            String strJSON = synAhelp.get();
            Log.d("8decV3", "JSON ==> " + strJSON);

            JSONArray jsonArray = new JSONArray(strJSON);
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            String strAHlep = jsonObject.getString("Ahelp");

            if (!strAHlep.equals("")) {

                if (Integer.parseInt(strAHlep)!=0) {
                    myNotification();
                }//if2
            }//if1


        } catch (Exception e) {
            Log.d("8decV3", "e myLoop ==> " + e.toString());
        }


        //Delay
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (statusABoolean) {
                    myLoop();
                }
            }
        }, 1000);



    }   // myLoop

    private void myNotification() {

        statusABoolean = false;

        Log.d("8decV4", "Noti OK");
        Intent intent = new Intent(HomeActivity.this, NotificationMaps.class);
        intent.putExtra("idUser", idUserString);
        PendingIntent pendingIntent = PendingIntent.getActivity(HomeActivity.this,
                (int) System.currentTimeMillis(), intent,0);
        Uri uri = RingtoneManager.getDefaultUri(Notification.DEFAULT_SOUND);
        Notification.Builder builder = new Notification.Builder(HomeActivity.this);
        builder.setTicker("Ahelp");
        builder.setContentTitle("ข้อความจาก");
        builder.setContentText("เกิดเหตุฉุกเฉินกับ"+"กรุณามาช่วยเหลือที่ตำแหน่ง");
        builder.setSmallIcon(R.drawable.alert);
        builder.setSound(uri);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);


    }//noti

    //คลิกปุ่มแจ้งเตือน
    private void imgController() {

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                confirmPassword();

            }   // onClick
        });

    }

    private void confirmPassword() {

        //Get Password from SQLite
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.database_name,
                MODE_PRIVATE, null);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM userTABLE", null);
        cursor.moveToFirst();
        nameString = cursor.getString(cursor.getColumnIndex(MyManage.column_Name));
        truePasswordString = cursor.getString(cursor.getColumnIndex(MyManage.column_Password));
        Log.d("8decV2", "truePass ==> " + truePasswordString);
        cursor.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.alert);
        builder.setTitle("ยืนยันรหัสผ่าน");
        builder.setMessage("กรุณากรอกรหัสผ่านของคุณ");

        final EditText editText = new EditText(HomeActivity.this);
        builder.setView(editText);

        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                userPasswordString = editText.getText().toString().trim();
                if (userPasswordString.equals(truePasswordString)) {
                    //Password True
                    callFriend();
                    dialogInterface.dismiss();
                } else {
                    passwordFalse();
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();

    }   // confirm

    private void passwordFalse() {
        MyAlert myAlert = new MyAlert();
        myAlert.myDialog(HomeActivity.this, "รหัสผ่านผิด",
                "กรุณากรอกรหัสผ่านใหม่");
    }

    private void callFriend() {

        //Find idUser
        try {

            //ค้นหาข้อมูลคนที่กำลัง LogIn อยู่ โดยต้องการ ID ของ User ที่ LogIn ==>idUserString

            FindIDuser findIDuser = new FindIDuser(HomeActivity.this,
                    nameString, truePasswordString);
            findIDuser.execute();
            String strJSON = findIDuser.get();
            Log.d("8decV2", "JSoN ==> " + strJSON);

            JSONArray jsonArray = new JSONArray(strJSON);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            idUserString = jsonObject.getString("id");

            Log.d("8decV2", "idUser ==> " + idUserString);

            //Find idCall
            findIDcall();

        } catch (Exception e) {
            e.printStackTrace();
        }   // try




    }   // callFriend

    private void findIDcall() {
        try {

            SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.database_name,
                    MODE_PRIVATE, null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM phoneTABLE", null);
            cursor.moveToFirst();

            for (int i=0;i<cursor.getCount();i++) {

                String strAHeip = idUserString; //id ของ User ที่กดเรียกเพื่อน
                //id ของเพื่อนที่ บันนทึกไว้ใน phoneTABLE
                String idUser = cursor.getString(cursor.getColumnIndex(MyManage.column_idCall));


                //ในแต่ระรอบจะส่ง id ของคนกดเรียกเพื่อน และ เพื่อนไปที่ editAhelp

                Log.d("12janV1", "ตำแหน่ง Lat ==>" + latADouble);
                Log.d("12janV1", "ตำแหน่ง Lng ==>" + lngADouble);

                editAhelp(idUser,strAHeip);

                cursor.moveToNext();
            }//for
            idCallString = cursor.getString(cursor.getColumnIndex(MyManage.column_idCall));
            Log.d("8decV2", "idCall ==> " + idCallString);


        } catch (Exception e) {
            Log.d("8decV2", "e find idCall ==> " + e.toString());
        }
    }

    private void editAhelp(String idUser, String strAHelp) {

        try {

            EditAhelp editAhelp = new EditAhelp(HomeActivity.this,
                    idUser, strAHelp, Double.toString(latADouble),Double.toString(lngADouble));
            editAhelp.execute("http://swiftcodingthai.com/fai/edit_Ahelp_where_id.php");

            if (Boolean.parseBoolean(editAhelp.get())) {
                Toast.makeText(HomeActivity.this, "ส่งข้อความเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(HomeActivity.this, "การส่งข้อความไม่สำเร็จ", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.d("8decV2", "e edit AHelp ==> " + e.toString());
        }
    }

    private void call1669() {
        button = (Button) findViewById(R.id.call);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent CallIntent = new Intent(Intent.ACTION_CALL);
                CallIntent.setData(Uri.parse("tel:=1669"));
                startActivity(CallIntent);

                }//onClick


        });
    }//call1669

    public void clickHomeGoSetting(View view) {

        try {

            CheckInternet checkInternet = new CheckInternet(HomeActivity.this);
            checkInternet.execute();

            if (Boolean.parseBoolean(checkInternet.get())) {
                startActivity(new Intent(HomeActivity.this, SettingActivity.class));
            } else {
                Toast.makeText(HomeActivity.this, "กรุณาตรวจสอบ Internet",
                        Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }   // clickHome

}   // Main Class
