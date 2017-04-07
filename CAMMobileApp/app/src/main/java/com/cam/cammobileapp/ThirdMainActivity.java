package com.cam.cammobileapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.os.Build;
import android.provider.Settings;
import android.renderscript.Double2;
import android.support.annotation.ColorInt;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.location.Criteria;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.provider.Settings;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import android.util.Log;




/**
 * Created by virajdave on 2017-03-24.
 */

public class ThirdMainActivity extends AppCompatActivity {

    final Context prev = this;
    TextView theLatCoord, theLongCoord;
    public LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastKnownLocation;
    public double longitude;
    public double latitude;

    public static ServerOnApp server = new ServerOnApp();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_third_main);

        /*locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                theLatCoord.setText(location.getLatitude() + "");
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
        }; */

        Intent intent = getIntent();
        server.start();

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

                String finalRoute = route.getText().toString();
                String finalStation = stop.getText().toString();
                String finaldirection = dir.getText().toString();
                String messageToOC = "12/id/route" + finalStation + "," + finalRoute + "," + finaldirection;
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


        final ImageButton imageButton7 = (ImageButton) findViewById(R.id.btn_colour);
        imageButton7.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                final ColorPicker cp = new ColorPicker(ThirdMainActivity.this, 0, 0, 0);
                cp.show();
                Button colourConfirmed = (Button) cp.findViewById(R.id.okColorButton);
                colourConfirmed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int redRGB = cp.getRed();
                        int greenRGB = cp.getGreen();
                        int blueRGB = cp.getBlue();
                        int theRGBCode = redRGB+greenRGB+blueRGB;
                        String inString = Integer.toString(theRGBCode);
                        String HexRed = Integer.toHexString(redRGB);
                        String HexGreen = Integer.toHexString(greenRGB);
                        String HexBlue = Integer.toHexString(blueRGB);
                        String finalRGB = "#"+HexRed+HexGreen+HexBlue;

                        //10/id/colour/(Colourcode)
                        // id(must get it with the a list of devices)

                        cp.dismiss();
                        String messageForColour = "12/id/colour/" + finalRGB;
                        Toast.makeText(prev, finalRGB, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


        final ImageButton imageButton6 = (ImageButton) findViewById(R.id.btn_setLocation);
        imageButton6.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final AlertDialog locationDialog = new AlertDialog.Builder(prev).create();
                final View location_layout = getLayoutInflater().inflate(R.layout.location_layout, null);
                locationDialog.setView(location_layout);
                locationDialog.show();
                Button enableLocation = (Button) locationDialog.findViewById(R.id.turnOnGPS);
                Button sendLocation = (Button) locationDialog.findViewById(R.id.sendGPS);

                theLatCoord = (TextView) location_layout.findViewById(R.id.latCoord);
                theLongCoord = (TextView) location_layout.findViewById(R.id.longCoord);


                enableLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        /*
                        grabLocation gettingGPS = new grabLocation(prev);
                        longitude = gettingGPS.getTheLongitude();
                        latitude = gettingGPS.getTheLatitude();

                            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            Criteria accurateCriteria = new Criteria();
                            accurateCriteria.setAccuracy(Criteria.ACCURACY_FINE);
                            String provider = locationManager.getBestProvider(accurateCriteria, false);

                            LocationListener theListener = new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    getLocation(location, theLatCoord, theLongCoord);
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

                            locationManager.requestLocationUpdates(provider, 0, 0, theListener);
                            getLocation(locationManager.getLastKnownLocation(provider), theLatCoord, theLongCoord);
                        }
                        catch (Exception ce){
                            Log.e("Here", "This", ce);
                        }
                        */

                        try{
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        final LocationListener listenerForLocation = new ListenerForLocation();
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listenerForLocation);
                        theLatCoord = (TextView) location_layout.findViewById(R.id.latCoord);
                        theLongCoord = (TextView) location_layout.findViewById(R.id.longCoord);
                        Location theCoord = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        longitude = theCoord.getLongitude();
                        latitude = theCoord.getLatitude();
                            String finalLongitude = Double.toString(longitude);
                            String finalLatitude = Double.toString(latitude);
                            Log.e("Here", finalLatitude+finalLongitude);
                            theLatCoord.setText(finalLatitude);
                            theLongCoord.setText(finalLongitude);
                    }

                    catch (Exception ce){
                        Log.e("Here", "This", ce);
                    }

                    }
                });

                final String finalLong = theLongCoord.getText().toString();
                final String finalLat = theLatCoord.getText().toString();
                sendLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String locationToMirror = "12/id/loc/" + finalLong + "," + finalLat;
                        locationDialog.dismiss();
                        Toast.makeText(prev, "Location sent to Magic Mirror", Toast.LENGTH_LONG).show();
                    }
                });



            }
        });
    }

    private void getLocation(Location location, TextView text1, TextView text2){

        if(location == null){
            return;
        }
        else {
            text1.setText(Double.toString(location.getLatitude()));
            text2.setText(Double.toString(location.getLongitude()));
        }

    }

    /*protected void retrieveLocation() {
        Location theCoord = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = theCoord.getLatitude();
        latitude = theCoord.getLatitude();

        theLatCoord.setText(Double.toString(latitude));
        theLongCoord.setText(Double.toString(longitude));

    }
    */



    class ListenerForLocation implements LocationListener {
        //TextView theLatCoord, theLongCoord;

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            theLatCoord.setText(Double.toString(location.getLatitude()));
            theLongCoord.setText(Double.toString(location.getLongitude()));

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    }


}

                /*
                locationListener = new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {
                        theLatCoord.setText(location.getLatitude() + "");
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

                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                    if (Build.VERSION.SDK_INT >= 23) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                //Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET, 10);
                    }
                    else {
                       ActivityCompat.requestPermissions((Activity) prev, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);}
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000,0,locationListener);
                } else {
                    Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent1);
                }
                }
                Button getLocation = (Button) location_layout.findViewById(R.id.turnOnGPS);
                final TextView longCoordinates = (TextView) location_layout.findViewById(R.id.viewLong);
                final TextView latCoordinates = (TextView) location_layout.findViewById(R.id.viewLat);

                locationDialog.setView(location_layout);
                locationDialog.show();

                Button sendLocation = (Button) location_layout.findViewById(R.id.sendGPS);
                getLocation.setOnClickListener(new View.OnClickListener() {
                sendLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        locationDialog.dismiss();
                        Toast.makeText(prev, "Permissions still have to be set", Toast.LENGTH_SHORT).show();
                    }
                });
                    }
                });
            }
       }

                getLocation.setOnClickListener(new View.OnClickListener() {
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

                });
                */

