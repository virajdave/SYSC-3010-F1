package com.cam.cammobileapp;

import android.location.Criteria;
import android.provider.Settings;
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


/**
 * Created by virajdave on 2017-03-24.
 */

public class ThirdMainActivity extends Activity {

    final Context prev = this;
    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_main);
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

        final ImageButton imageButton6 = (ImageButton) findViewById(R.id.btn_findLocation);
        imageButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog locationDialog = new AlertDialog.Builder(prev).create();
                View location_layout = getLayoutInflater().inflate(R.layout.location_layout, null);
                //Button getLocation = (Button) location_layout.findViewById(R.id.turnOnGPS);
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

            }
        });


        final ImageButton imageButton7 = (ImageButton) findViewById(R.id.btn_colour);
        imageButton7.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                final AlertDialog colourDialog = new AlertDialog.Builder(prev).create();
                View colour_layout = getLayoutInflater().inflate(R.layout.colour_layout, null);
                ProgressBar workingInProgress = (ProgressBar) colour_layout.findViewById(R.id.workInProgressColour);

                colourDialog.setView(colour_layout);
                colourDialog.show();

                        Toast.makeText(prev, "Need to insert Colour wheel shortly", Toast.LENGTH_LONG).show();
                    }
                });



    }
}
