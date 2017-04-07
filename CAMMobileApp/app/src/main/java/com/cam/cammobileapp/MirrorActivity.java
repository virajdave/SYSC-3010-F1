package com.cam.cammobileapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.cam.cammobileapp.util.Parse;
import com.cam.cammobileapp.util.Toasty;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

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
                final EditText route = (EditText) transportation_layout.findViewById(R.id.enterRoute);
                final EditText stop = (EditText) transportation_layout.findViewById(R.id.enterStop);
                final RadioGroup radioGroup = (RadioGroup) transportation_layout.findViewById(R.id.pickDir);

                busDialog.setView(transportation_layout);

                transportation_layout.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        busDialog.dismiss();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int radioButton = radioGroup.getCheckedRadioButtonId();

                                String rt = route.getText().toString();
                                String st = stop.getText().toString();
                                int dir;
                                if (radioButton == R.id.dirBoth) {
                                    dir = 0;
                                } else if (radioButton == R.id.dir1) {
                                    dir = 1;
                                } else {
                                    dir = 2;
                                }

                                MainActivity.server.sendBroadcast(Parse.toString("/", "12", id, "route", Parse.toString(",", rt, st, dir)));
                                String msg = MainActivity.server.recvWait(1000);
                                if (msg != null && msg.charAt(3) == '1') {
                                    Toasty.show(activity, "Successfully sent Bus Info to OCTranspo API");
                                } else {
                                    Toasty.show(activity, "Could not send Bus Info");
                                }
                            }
                        }).start();
                    }
                });
                busDialog.show();
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

