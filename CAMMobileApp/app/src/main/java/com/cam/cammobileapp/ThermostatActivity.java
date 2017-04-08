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

public class ThermostatActivity extends AppCompatActivity {
    final Activity activity = this;
    float setTemp = 0f;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);

        Intent intent = getIntent();
        final int id = intent.getIntExtra("deviceID", -1);
        final String temp = intent.getStringExtra("deviceInfo").substring(3);

        final TextView currentTemp = (TextView) findViewById(R.id.currentTemp);
        currentTemp.setText(temp);

        if (temp.equals("no temp ")) {
            findViewById(R.id.btn_up).setEnabled(false);
            findViewById(R.id.btn_down).setEnabled(false);
            findViewById(R.id.setTemp).setEnabled(false);
            findViewById(R.id.resetTemp).setEnabled(false);
            currentTemp.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
            return;
        }
        setTemp = Parse.toFloat(temp);

        //When the Increase temp button is invoked, increase the current temperature
        //by 1 degree and display it
        findViewById(R.id.btn_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTemp.setText(Parse.toString(++setTemp));
            }
        });

        //When the Decrease temp button is invoked, decrease the current temperature
        //by 1 degree and display it
        findViewById(R.id.btn_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTemp.setText(Parse.toString(--setTemp));
            }
        });

        //When the send temp button is invoked, send the changed temperature to the server
        findViewById(R.id.setTemp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String currentTempToBeSent = currentTemp.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.requestCheck(
                                Parse.toString("/", "12", id, "temp", currentTempToBeSent),
                                "Successfully set temperature",
                                "Could not send temp to server",
                                activity
                        );
                    }
                }).start();
            }
        });

        //When the reset temperature is invoked, change the temperature back to what it was before
        //the change
        findViewById(R.id.resetTemp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Get the device info again and parse out the temperature.
                        String msg = MainActivity.server.request("13/" + id, MainActivity.TIMEOUT);
                        if (msg != null && msg.length() > 3) {
                            final String temp = msg.substring(3);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    currentTemp.setText(temp);
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
