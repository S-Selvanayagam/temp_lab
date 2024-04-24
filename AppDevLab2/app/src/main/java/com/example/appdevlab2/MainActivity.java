package com.example.appdevlab2;

import java.io.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.SharedPreferences;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;


public class MainActivity extends AppCompatActivity {

    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    Button mybtn1;
    Button mybtn2;
    TextView loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mybtn1 = (Button)(findViewById(R.id.button));
        mybtn2 = (Button)(findViewById(R.id.button2));
        loc = (TextView)(findViewById(R.id.textView3));

    }

    public void getLocation(View view) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gps_loc != null) {
            final_loc = gps_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else if (network_loc != null) {
            final_loc = network_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else {
            latitude = 0.0;
            longitude = 0.0;
        }
//        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("lat", String.valueOf(latitude));
//        editor.putString("long", String.valueOf(longitude));
//        editor.apply();

        String toshow = "Latitude: "+String.valueOf(latitude)+'\n'+"Longitude: "+String.valueOf(longitude)+"\n@";
        Context context = view.getContext();
        File path = context.getExternalFilesDir(null);
        File file = new File(path, "latlong.txt");
        Log.i("info", file.toString());
        try {
            FileOutputStream stream = new FileOutputStream(file, true);
            try {
                stream.write(toshow.getBytes());
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);
        Toast.makeText(MainActivity.this, "Location found successfully!", Toast.LENGTH_SHORT).show();
    }

    public void showLocation(View view) {

//        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
//        String latfromFile = sharedPreferences.getString("lat", "");
//        String longfromFile = sharedPreferences.getString("long", "");
//        String toshow = "Latitude: "+latfromFile+'\n'+"Longitude: "+longfromFile;

        Context context = view.getContext();
        File path = context.getExternalFilesDir(null);
        File file = new File(path, "latlong.txt");

        int length = (int) file.length();
        byte[] bytes = new byte[length];
        try {
            FileInputStream in = new FileInputStream(file);
            try {
                in.read(bytes);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String contents = new String(bytes);
        String[] arrOfStr = contents.split("@");
        String show = arrOfStr[arrOfStr.length-1];

        loc.setText(show);
    }

}