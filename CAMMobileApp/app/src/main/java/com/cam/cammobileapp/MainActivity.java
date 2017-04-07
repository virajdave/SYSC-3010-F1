package com.cam.cammobileapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.widget.ListView;

import com.cam.cammobileapp.util.DataRunnable;
import com.cam.cammobileapp.util.Toasty;
import com.cam.cammobileapp.net.Server;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final Activity activity = this;
    public static Server server = new Server();
    public Devices devices = new Devices();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start up the server if hasn't been done yet.
        if (!server.isAlive()) {
            server.start();

            // Wait for thread to startup.
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Get the net info.
        new Thread(new Runnable() {
            @Override
            public void run() {
                getNetInfo();
            }
        }).start();

        findViewById(R.id.btn_thermostat).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (devices.thermo.isEmpty()){
                    Toasty.show(activity, "No active thermostats. Please refresh list of devices");
                } else {
                    showList(devices.thermo, new Intent(MainActivity.this, ThermostatActivity.class));
                }
            }
        });

        findViewById(R.id.btn_magicMirror).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (devices.mirror.isEmpty()){
                    Toasty.show(activity, "No active mirrors. Please refresh list of devices");
                } else {
                    showList(devices.mirror, new Intent(MainActivity.this, MirrorActivity.class));
                }
            }
        });

        findViewById(R.id.btn_bedroom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (devices.bed.isEmpty()){
                    Toasty.show(activity, "No active alarms. Please refresh list of devices");
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
                        getNetInfo();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, mirrorIds);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    final Integer devID = list.get(position);
                    alertDialog.dismiss();

                    new Thread(new DataRunnable(devID.toString(), i) {
                        @Override
                        public void run() {
                            server.sendBroadcast("13/" + data);
                            String msg = server.recvWait(1000);
                            if (msg != null) {
                                Intent intent = (Intent)(i);
                                intent.putExtra("deviceID", Integer.parseInt(data));
                                intent.putExtra("deviceInfo", msg);
                                startActivity(intent);
                            } else {
                                Toasty.show(activity, "Could not get dev info");
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    Log.e("CAM", "on device pick failure", e);
                }
            }
        });
        alertDialog.show();
    }

    public void getNetInfo() {
        String message = "10";
        server.sendBroadcast(message);
        String s = server.recvWait(1000);
        //String s = "00/0:3:1/1:0:1/2:2:1";//"00/2:3:0/6:2:1/7:2:1/8:2:0";
        if (s == null) {
            Toasty.show(activity, "Message is null, server not connected");
        } else {
            Toasty.show(activity, s);
            devices.parse(s);
        }
    }
}
