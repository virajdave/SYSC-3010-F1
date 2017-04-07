package com.cam.cammobileapp;

import android.app.Activity;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import android.util.Log;

public class MirrorActivity extends AppCompatActivity {
    final Activity activity = this;

    TextView theLatCoord, theLongCoord;
    public LocationManager locationManager;
    public double longitude;
    public double latitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_third_main);


        Intent intent = getIntent();

        final ImageButton imageButton5 = (ImageButton) findViewById(R.id.btn_setTrans);
        imageButton5.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                final AlertDialog busDialog = new AlertDialog.Builder(activity).create();
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

                        Toasty.show(activity, "Successfully sent Bus Info to OCTranspo API");
                    }
                });

            }
        });


        final ImageButton imageButton7 = (ImageButton) findViewById(R.id.btn_colour);
        imageButton7.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                final ColorPicker cp = new ColorPicker(MirrorActivity.this, 0, 0, 0);
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
                        Toasty.show(activity, finalRGB);
                    }
                });
            }
        });


        final ImageButton imageButton6 = (ImageButton) findViewById(R.id.btn_setLocation);
        imageButton6.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final AlertDialog locationDialog = new AlertDialog.Builder(activity).create();
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
                        Toasty.show(activity, "Location sent to Magic Mirror");
                    }
                });



            }
        });
    }

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

