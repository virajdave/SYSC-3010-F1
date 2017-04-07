package com.cam.cammobileapp;

import android.app.Activity;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.cam.cammobileapp.util.Parse;
import com.cam.cammobileapp.util.Toasty;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import android.util.Log;

public class MirrorActivity extends AppCompatActivity {
    final Activity activity = this;

    private LocationManager locationManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirror);

        Intent intent = getIntent();
        final int id = intent.getIntExtra("deviceID", -1);

        findViewById(R.id.btn_setTrans).setOnClickListener(new View.OnClickListener() {

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

        findViewById(R.id.btn_colour).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final ColorPicker cp = new ColorPicker(activity, 0, 0, 0);
                cp.show();
                cp.findViewById(R.id.okColorButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cp.dismiss();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // Convert RGB to a hex colour value.
                                String HexRed = Integer.toHexString(cp.getRed());
                                String HexGreen = Integer.toHexString(cp.getGreen());
                                String HexBlue = Integer.toHexString(cp.getBlue());
                                String finalRGB = "#" + HexRed + HexGreen + HexBlue;

                                // Send colour to server and check it worked.
                                MainActivity.server.sendBroadcast(Parse.toString("/", "12", id, "colour", finalRGB));
                                String msg = MainActivity.server.recvWait(1000);
                                if (msg != null && msg.charAt(3) == '1') {
                                    Toasty.show(activity, "Set the mirror colour to " + finalRGB);
                                } else {
                                    Toasty.show(activity, "Could not set the mirror colour");
                                }
                            }
                        }).start();
                    }
                });
            }
        });

        findViewById(R.id.btn_setLocation).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Looper.prepare();
                            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            final String finalLong = Double.toString(location.getLongitude());
                            final String finalLat = Double.toString(location.getLatitude());

                            MainActivity.server.sendBroadcast(Parse.toString("/", "12", id, "loc", finalLong + "," + finalLat));
                            String msg = MainActivity.server.recvWait(1000);
                            if (msg != null && msg.charAt(3) == '1') {
                                Toasty.show(activity, "Successfully set Magic Mirror location to " + finalLong + ", " + finalLat);
                            } else {
                                Toasty.show(activity, "Unable to send location to Magic Mirror");
                            }
                        } catch (Exception ce) {
                            Toasty.show(activity, "Could not lock GPS location");
                            Log.e("here", "ce", ce);
                        }
                    }
                }).start();
            }
        });
    }
}

