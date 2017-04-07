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
                final AlertDialog locationDialog = new AlertDialog.Builder(activity).create();
                final View location_layout = getLayoutInflater().inflate(R.layout.location_layout, null);
                locationDialog.setView(location_layout);
                locationDialog.show();

                final TextView theLatCoord = (TextView) location_layout.findViewById(R.id.latCoord);
                final TextView theLongCoord = (TextView) location_layout.findViewById(R.id.longCoord);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Looper.prepare();
                            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            final LocationListener listenerForLocation = new LocationListener() {
                                @Override
                                public void onLocationChanged(final Location location) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            theLatCoord.setText(Double.toString(location.getLatitude()));
                                            theLongCoord.setText(Double.toString(location.getLongitude()));
                                        }
                                    });
                                }

                                @Override
                                public void onProviderDisabled(String s) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }

                                @Override
                                public void onStatusChanged(String s, int i, Bundle bundle) {}
                                @Override
                                public void onProviderEnabled(String s) {}
                            };
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listenerForLocation);
                            final Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    theLatCoord.setText(Double.toString(location.getLatitude()));
                                    theLongCoord.setText(Double.toString(location.getLongitude()));
                                }
                            });
                        } catch (Exception ce) {
                            locationDialog.dismiss();
                            Toasty.show(activity, "Could not lock GPS location");
                            Log.e("here", "ce", ce);
                        }
                    }
                }).start();

                locationDialog.findViewById(R.id.sendGPS).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.server.sendBroadcast(Parse.toString("/", "12", id, "loc", theLatCoord.getText() + "," + theLongCoord.getText()));
                                String msg = MainActivity.server.recvWait(1000);
                                if (msg != null && msg.charAt(3) == '1') {
                                    Toasty.show(activity, "Successfully set Magic Mirror location to " + theLatCoord.getText() + ", " + theLongCoord.getText());
                                } else {
                                    Toasty.show(activity, "Unable to send location to Magic Mirror");
                                }
                            }
                        }).start();
                    }
                });
            }
        });
    }
}

