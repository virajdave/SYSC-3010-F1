package com.cam.cammobileapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    final Context prev = this;
    public static ServerOnApp server = new ServerOnApp();
    public static ParseThermo parsedThermostat = new ParseThermo();
    private ArrayList<Integer> id_thermo = new ArrayList<>();
    //public static ParseAlarm parsedAlarms = new ParseAlarms();
    //public static ParseMirror parsedMirrors = new ParseMirrors();

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
                Intent intent = new Intent(MainActivity.this, SecondMainActivity.class);
                startActivity(intent);
            /*
            Fill in code to show available thermostats
             */


            }
        });

        ImageButton imageButton2 = (ImageButton) findViewById(R.id.btn_magicMirror);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, ThirdMainActivity.class);
                startActivity(intent2);
                /*
                Fill in code to show available Magic Mirrors
                 */


            }
        });


        ImageButton imageButton3 = (ImageButton) findViewById(R.id.btn_bedroom);
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(MainActivity.this, FourthMainActivity.class);
                startActivity(intent3);

                /*
                Fill in code to show available alarms
                 */

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
            String result ="";
            for(int top: id_thermo){
                result += top;
            }
            Toast.makeText(prev, result, Toast.LENGTH_LONG).show();
        }
    }
}
