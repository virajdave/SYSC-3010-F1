package com.cam.cammobileapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.cam.cammobileapp.util.Parse;
import com.cam.cammobileapp.util.Toasty;

public class ThermostatActivity extends AppCompatActivity{
    final Activity activity = this;
    float setTemp = 0f;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);

        Intent intent = getIntent();
        final int id = intent.getIntExtra("deviceID", -1);
        final String temp = intent.getStringExtra("deviceInfo").substring(3);

        final TextView theCurrentTemp = (TextView) findViewById(R.id.currentTemp);
        theCurrentTemp.setText(temp);

        if (temp.equals("no temp ")) {
            findViewById(R.id.btn_up).setEnabled(false);
            findViewById(R.id.btn_down).setEnabled(false);
            findViewById(R.id.setTemp).setEnabled(false);
            findViewById(R.id.resetTemp).setEnabled(false);
            theCurrentTemp.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
            return;
        }
        setTemp = Parse.toFloat(temp);

        findViewById(R.id.btn_up).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                theCurrentTemp.setText(Parse.toString(++setTemp));
            }
        });

        findViewById(R.id.btn_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theCurrentTemp.setText(Parse.toString(--setTemp));
            }
        });

        findViewById(R.id.setTemp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String currentTempToBeSent = theCurrentTemp.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.server.sendBroadcast(Parse.toString("/", "12", id, "temp", currentTempToBeSent));
                        String msg = MainActivity.server.recvWait(1000);

                        if (msg != null) {
                            Toasty.show(activity, "Successfully set temperature");
                        } else {
                            Toasty.show(activity, "Could not send temp to server");
                        }
                    }
                }).start();
            }
        });

        findViewById(R.id.resetTemp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Get the device info again and parse out the temperature.
                        MainActivity.server.sendBroadcast("13/" + id);
                        String msg = MainActivity.server.recvWait(1000);
                        if (msg != null) {
                            final String temp = msg.substring(3);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    theCurrentTemp.setText(temp);
                                }
                            });
                        } else {
                            Toasty.show(activity, "Could not get temp from server");
                        }
                    }
                }).start();
            }
        });
    }
}
