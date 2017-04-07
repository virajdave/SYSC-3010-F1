package com.cam.cammobileapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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


public class MainActivity extends AppCompatActivity {
    final Context prev = this;
    public static ServerOnApp server = new ServerOnApp();
    public ListView listView;
    public Devices devices = new Devices();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        server.start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.sendMessage();

        ImageButton imageButton1 = (ImageButton) findViewById(R.id.btn_thermostat);
        imageButton1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, SecondMainActivity.class);
                //startActivity(intent);

                if(devices.mirror.isEmpty()){
                    Toast.makeText(prev, "No active thermostats. Please refresh list of devices", Toast.LENGTH_LONG).show();
                }

                else{
                    Toast.makeText(prev, "Thermostats are here", Toast.LENGTH_LONG).show();
                }
            /*
            Fill in code to show available thermostats
             */


            }
        });

        ImageButton imageButton2 = (ImageButton) findViewById(R.id.btn_magicMirror);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                ImageButton refreshButton = (ImageButton) findViewById(R.id.btn_refresh);
                refreshButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMessage();
                    }
                });

                String[] mirrorIds = new String[devices.mirror.size()];
                int index = 0;
                for(Integer id: devices.mirror){
                    mirrorIds[index++]=id.toString();
                    Log.i("info",mirrorIds[index-1]);
                }

                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                LayoutInflater inflater = getLayoutInflater();
                View listView = (View) inflater.inflate(R.layout.customlist, null);
                alertDialog.setView(listView);
                alertDialog.setTitle("List");
                ListView lv = (ListView) listView.findViewById(R.id.listView);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(prev, android.R.layout.simple_list_item_1, mirrorIds);
                lv.setAdapter(adapter);

                try {
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                            try {
                                Integer devID = devices.mirror.get(position);
                                Log.i("hhhh", devID.toString());
                                alertDialog.dismiss();

                                new Thread(new DataRunnable(devID.toString()) {
                                    @Override
                                    public void run() {
                                        server.sendBroadcast("13/" + data);
                                        String msg = server.recvWait(1000);
                                        if (msg != null) {
                                            Intent i = new Intent(MainActivity.this, ThirdMainActivity.class);
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
                } catch(Exception e) {
                    Log.e("hhhh", "fail", e);
                }
                alertDialog.show();
            }
        });



        ImageButton imageButton3 = (ImageButton) findViewById(R.id.btn_bedroom);
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent3 = new Intent(MainActivity.this, FourthMainActivity.class);
                //startActivity(intent3);

                /*
                Fill in code to show available alarms
                 */
                if(devices.bed.isEmpty()){
                    Toast.makeText(prev, "No active alarms. Please refresh list of devices", Toast.LENGTH_LONG).show();
                }

                else{
                    Toast.makeText(prev, "Alarms are here.", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public void sendMessage() {
        String message = "10";
        server.sendBroadcast(message);
        String s = server.recvWait(1000);
        //String s = "00/0:3:1/1:0:1/2:2:1";//"00/2:3:0/6:2:1/7:2:1/8:2:0";
        if (s == null) {
            Toast.makeText(prev, "Message is null, server not connected", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(prev, s, Toast.LENGTH_LONG).show();
            devices.parse(s);
        }
    }
}
