package com.cam.cammobileapp;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ListView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final Context prev = this;
    public static ServerOnApp server = new ServerOnApp();
    public Devices devices = new Devices();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!server.isAlive()) {
            server.start();
        }

        // Wait for server to start.
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Get the net info.
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessage();
            }
        }).start();

        findViewById(R.id.btn_thermostat).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (devices.thermo.isEmpty()){
                    Toast.makeText(prev, "No active thermostats. Please refresh list of devices", Toast.LENGTH_LONG).show();
                } else {
                    showList(devices.thermo, new Intent(MainActivity.this, ThermostatActivity.class));
                }
            }
        });

        findViewById(R.id.btn_magicMirror).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (devices.mirror.isEmpty()){
                    Toast.makeText(prev, "No active mirrors. Please refresh list of devices", Toast.LENGTH_LONG).show();
                } else {
                    showList(devices.mirror, new Intent(MainActivity.this, MirrorActivity.class));
                }
            }
        });

        findViewById(R.id.btn_bedroom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (devices.bed.isEmpty()){
                    Toast.makeText(prev, "No active alarms. Please refresh list of devices", Toast.LENGTH_LONG).show();
                } else {
                    showList(devices.mirror, new Intent(MainActivity.this, BedroomActivity.class));
                }
            }
        });

        findViewById(R.id.btn_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendMessage();
                    }
                }).start();
            }
        });
    }

    public void showList(final ArrayList<Integer> list, final Intent i) {

        String[] mirrorIds = new String[list.size()];
        int index = 0;
        for(Integer id: list){
            mirrorIds[index++]=id.toString();
            Log.i("info",mirrorIds[index-1]);
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View listView = (View) inflater.inflate(R.layout.customlist, null);
        alertDialog.setView(listView);
        alertDialog.setTitle("Choose ID");
        ListView lv = (ListView) listView.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(prev, android.R.layout.simple_list_item_1, mirrorIds);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Integer devID = list.get(position);
                    Log.i("hhhh", devID.toString());
                    alertDialog.dismiss();

                    new Thread(new DataRunnable(devID.toString(), i) {
                        @Override
                        public void run() {
                            server.sendBroadcast("13/" + data);
                            String msg = server.recvWait(1000);
                            if (msg != null) {
                                i.putExtra("deviceInfo", msg);
                                startActivity(i);
                            } else {
                                Log.i("hhhh", "aaasasfsa");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(prev, "Could not get dev info", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    Log.e("hhhh", "non", e);
                }
            }
        });
        alertDialog.show();
    }

    public void sendMessage() {
        String message = "10";
        server.sendBroadcast(message);
        String s = server.recvWait(1000);
        //String s = "00/0:3:1/1:0:1/2:2:1";//"00/2:3:0/6:2:1/7:2:1/8:2:0";
        if (s == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(prev, "Message is null, server not connected", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            runOnUiThread(new DataRunnable(s, null) {
                @Override
                public void run() {
                    Toast.makeText(prev, data, Toast.LENGTH_LONG).show();
                }
            });
            devices.parse(s);
        }
    }
}
