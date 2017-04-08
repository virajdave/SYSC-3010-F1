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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.cam.cammobileapp.util.Parse;
import com.cam.cammobileapp.util.Toasty;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.util.ArrayList;

public class MirrorActivity extends AppCompatActivity {
    final Activity activity = this;

    //Need instance of LocationManager to get locaton
    private LocationManager locationManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirror);

        Intent intent = getIntent();
        final int id = intent.getIntExtra("deviceID", -1);

        //When Bus Button is invoked, open new dialog to enter information
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

                                MainActivity.requestCheck(
                                        Parse.toString("/", "12", id, "route", Parse.toString(",", st, rt, dir)),
                                        "Successfully sent Bus Info to OCTranspo API",
                                        "Could not send Bus Info",
                                        activity
                                );
                            }
                        }).start();
                    }
                });
                busDialog.show();
            }
        });

        //When Colour Button is invoked, open new dialog to choose the colour of the mirror
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
                                String HexRed = pad(Integer.toHexString(cp.getRed()));
                                String HexGreen = pad(Integer.toHexString(cp.getGreen()));
                                String HexBlue = pad(Integer.toHexString(cp.getBlue()));
                                String finalRGB = "#" + HexRed + HexGreen + HexBlue;

                                // Send colour to server and check it worked.
                                MainActivity.requestCheck(
                                        Parse.toString("/", "12", id, "colour", finalRGB),
                                        "Set the mirror colour to " + finalRGB,
                                        "Could not set the mirror colour",
                                        activity
                                );
                            }
                        }).start();
                    }
                });
            }
        });

        //When location button is invoked, get location, and display coordinates as a toast alert
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

                            MainActivity.requestCheck(
                                    Parse.toString("/", "12", id, "loc", finalLong + "," + finalLat),
                                    "Successfully set Magic Mirror location to " + finalLong + ", " + finalLat,
                                    "Unable to send location to Magic Mirror",
                                    activity
                            );
                        } catch (Exception ce) {
                            Toasty.show(activity, "Could not lock GPS location");
                            Log.e("here", "ce", ce);
                        }
                    }
                }).start();
            }
        });

        //When Thermostat Link button is invoked, get the currrent thermo temperature and send
        //it to the mirror
        findViewById(R.id.btn_temp).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final ArrayList<Integer> list = MainActivity.devices.thermo;

                if (list.size() == 0) {
                    Toasty.show(activity, "No thermostat devices are connected");
                    return;
                }

                // Convert the integer list to a string array so it can be used in a ListView.
                String[] ids = new String[list.size()];
                int index = 0;
                for (Integer ida : list) {
                    ids[index++] = ida.toString();
                    Log.i("info", ids[index - 1]);
                }

                // Build the dialog to pick a device ID from a list.
                final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(activity).create();
                View listView = (View) getLayoutInflater().inflate(R.layout.custom_list, null);
                alertDialog.setView(listView);
                alertDialog.setTitle("Choose ID");
                ListView lv = (ListView) listView.findViewById(R.id.listView);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, ids);
                lv.setAdapter(adapter);

                // When a device ID is picked get the info from the server and start the associated activity.
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long ida) {
                        try {
                            // Get the ID selected and dismiss the dialog, so nothing else could be picked.
                            final int devID = list.get(position);
                            alertDialog.dismiss();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.requestCheck(
                                            Parse.toString("/", "12", id, "thermo",  devID),
                                            "Thermostat " + devID + " connected to Mirror",
                                            "Could not connect Thermostat " + devID,
                                            activity
                                    );
                                }
                            }).start();
                        } catch (Exception e) {
                            Log.e("CAM", "on device pick failure", e);
                        }
                    }
                });
                alertDialog.show();
            }
        });
    }

    private String pad(String num) {
        return (num.length() == 1 ? "0" : "") + num;
    }
}

