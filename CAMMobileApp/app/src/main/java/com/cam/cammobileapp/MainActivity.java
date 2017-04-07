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
import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {
    final Context prev = this;
    public static ServerOnApp server = new ServerOnApp();
    public static ParseThermo parsedThermostat = new ParseThermo();
    public static ParseAlarm parsedAlarms = new ParseAlarm();
    public static ParseMirror parsedMirrors = new ParseMirror();
    private ArrayList<Integer> id_thermo = new ArrayList<>();
    private ArrayList<Integer> id_mirror = new ArrayList<>();
    private ArrayList<Integer> id_alarm = new ArrayList<>();
    public ListView listView;


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

                if(id_thermo.isEmpty()){
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

                String[] mirrorIDS = new String[id_mirror.size()];
                int index = 0;
                for (Integer id : id_mirror) {
                    mirrorIDS[index++] = id.toString();
                    Log.i("info", mirrorIDS[index - 1]);
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View listView = (View) inflater.inflate(R.layout.listviewformirror, null);
                builder.setView(listView);
                builder.setTitle("Select Mirror");
                ListView lv = (ListView) listView.findViewById(R.id.listMirror);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(prev, android.R.layout.simple_list_item_1, mirrorIDS);
                lv.setAdapter(adapter);
                final AlertDialog showMirrors = builder.create();
                showMirrors.show();

                try {
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent2 = new Intent(MainActivity.this, ThirdMainActivity.class);
                            startActivity(intent2);
                            showMirrors.dismiss();
                        }
                    });
                } catch (Exception ce) {
                    ce.printStackTrace();
                    Log.e("WHY", "This");
                }
            }
            });
                /*
                @Override
                public void onClick(DialogInterface builder,int which){
                    Intent intent2 = new Intent(MainActivity.this, ThirdMainActivity.class);
                    startActivity(intent2);
                }
            }
            */





        ImageButton imageButton3 = (ImageButton) findViewById(R.id.btn_bedroom);
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent3 = new Intent(MainActivity.this, FourthMainActivity.class);
                //startActivity(intent3);

                /*
                Fill in code to show available alarms
                 */
                if(id_alarm.isEmpty()){
                    Toast.makeText(prev, "No active alarms. Please refresh list of devices", Toast.LENGTH_LONG).show();
                }

                else{
                    Toast.makeText(prev, "Alarms are here.", Toast.LENGTH_LONG).show();
                }

            }
        });

        ImageButton refreshButton = (ImageButton) findViewById(R.id.btn_refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    public void sendMessage() {
        //String message = "10";
        //server.sendBroadcast(message);
        //String s = server.recvWait(1000);
        String s = "00/2:3:0/6:2:1/7:2:1/8:2:0";
        if (s == null) {
            Toast.makeText(prev, "Message is null, server not connected", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(prev, s, Toast.LENGTH_LONG).show();
            id_thermo = parsedThermostat.parseString(s);
            id_alarm = parsedAlarms.parseString(s);
            id_mirror = parsedMirrors.parseString(s);
            String result ="";

        }
    }
}
