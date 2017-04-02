package com.cam.cammobileapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;




/**
 * Created by virajdave on 2017-03-24.
 */

public class ThirdMainActivity extends AppCompatActivity {

    final Context prev = this;
    TextView theLatCoord, theLongCoord;
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnownLocation;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_third_main);
        theLatCoord = (TextView) findViewById(R.id.latCoord);
        theLongCoord = (TextView) findViewById(R.id.longCoord);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                theLatCoord.setText(location.getLatitude()+ "");
                theLongCoord.setText(location.getLongitude() + "");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        Intent intent = getIntent();


        final ImageButton imageButton5 = (ImageButton) findViewById(R.id.btn_setTrans);
        imageButton5.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                final AlertDialog busDialog = new AlertDialog.Builder(prev).create();
                View transportation_layout = getLayoutInflater().inflate(R.layout.transportation_layout, null);
                EditText route = (EditText) transportation_layout.findViewById(R.id.enterRoute);
                EditText stop = (EditText) transportation_layout.findViewById(R.id.enterStop);
                EditText dir = (EditText) transportation_layout.findViewById(R.id.enterDir);

                busDialog.setView(transportation_layout);
                busDialog.show();

                Button button = (Button) transportation_layout.findViewById(R.id.sendButton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        busDialog.dismiss();
                        Toast.makeText(prev, "Successfully sent Bus Info to OCTranspo API", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
        /*
        final ImageButton imageButton6 = (ImageButton) findViewById(R.id.btn_findLocation);
        imageButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog locationDialog = new AlertDialog.Builder(prev).create();
                View location_layout = getLayoutInflater().inflate(R.layout.location_layout, null);
                theLatCoord = (TextView) findViewById(R.id.latCoord);
                theLongCoord = (TextView) findViewById(R.id.longCoord);
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        theLatCoord.setText(location.getLatitude()+ "");
                        theLongCoord.setText(location.getLongitude() + "");
                    }
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                };
                checkLocationPermissions();
                if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    if(Build.VERSION.SDK_INT>=23){
                        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
                    }

                    else{
                        ActivityCompat.requestPermissions((Activity)prev, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
                    }
                   // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000,0,locationListener);
                }
                else{
                    Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent1);
                }
                Button getLocation = (Button) location_layout.findViewById(R.id.turnOnGPS);
                final TextView longCoordinates = (TextView) location_layout.findViewById(R.id.viewLong);
                final TextView latCoordinates = (TextView) location_layout.findViewById(R.id.viewLat);

                locationDialog.setView(location_layout);
                locationDialog.show();

                Button sendLocation = (Button) location_layout.findViewById(R.id.sendGPS);
                //getLocation.setOnClickListener(new View.OnClickListener() {
                    sendLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        locationDialog.dismiss();
                        Toast.makeText(prev, "Permissions still have to be set", Toast.LENGTH_SHORT).show();
                    }
                });

                /*getLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LocationManager locationManger = (LocationManager)prev.getSystemService(Context.LOCATION_SERVICE);
                        Criteria locationCriteria = new Criteria();
                        locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
                        locationCriteria.setAltitudeRequired(false);
                        locationCriteria.setBearingRequired(false);
                        locationCriteria.setCostAllowed(true);
                        locationCriteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
                        String nameOfProvider = locationManger.getBestProvider(locationCriteria, true);
                        if(nameOfProvider != null && locationManger.isProviderEnabled(nameOfProvider)){

                            locationManger.requestLocationUpdates(nameOfProvider, 20000, 100, locationListener);
                        }

                        else{
                            Toast.makeText(prev, "Please turn on the GPS", Toast.LENGTH_LONG).show();
                            Intent intent2 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent2);
                        }

                        Location currentLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        Double latitude = currentLocation.getLatitude();
                        Double longitude = currentLocation.getLongitude();

                        longCoordinates.setText(Double.toString(longitude));
                        latCoordinates.setText(Double.toString(latitude));
                    }

                }); */



        final ImageButton imageButton7 = (ImageButton) findViewById(R.id.btn_colour);
        imageButton7.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                final ColorPicker cp = new ColorPicker(ThirdMainActivity.this, 0,0,0);
                cp.show();
                Button colourConfirmed = (Button)cp.findViewById(R.id.okColorButton);
                colourConfirmed.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick (View v){
                        //selctedColourR = cp.getRed();
                        //selectedColourG = cp.getGreen();
                        //selectedColourB = cp.getBlue();

                        cp.dismiss();
                    }
                });
                        Toast.makeText(prev, "Need to insert Colour wheel shortly", Toast.LENGTH_LONG).show();
                    }
                });


                    }
                }



    /*public boolean checkLocationPermissions(){
        String thePermission = "android.permission.ACCESS_COARSE_LOCATION";
        int check = this.checkCallingOrSelfPermission(thePermission);
        return (check == PackageManager.PERMISSION_GRANTED);
    } */



