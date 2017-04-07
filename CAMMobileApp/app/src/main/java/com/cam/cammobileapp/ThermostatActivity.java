package com.cam.cammobileapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import org.w3c.dom.Text;

/**
 * Created by virajdave on 2017-03-20.
 */

public class ThermostatActivity extends AppCompatActivity{
    final Context theWindow = this;
    float numtest = 0f;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_main);

        Intent intent = getIntent();
        final int id = intent.getIntExtra("deviceID", -1);
        String temp = intent.getStringExtra("deviceInfo").substring(3);

        final TextView theCurrentTemp = (TextView) findViewById(R.id.currentTemp);
        theCurrentTemp.setText(temp);

        if (temp.equals("no temp ")) {
            return;
        }
        numtest = Float.parseFloat(temp);

        findViewById(R.id.btn_up).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                numtest++;
                theCurrentTemp.setText(Float.toString(numtest));

            }
        });

        findViewById(R.id.btn_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numtest--;
                theCurrentTemp.setText(Float.toString(numtest));
            }
        });

        findViewById(R.id.setTemperature).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentTempToBeSent = theCurrentTemp.getText().toString();
                new Thread(new DataRunnable(currentTempToBeSent, id) {
                    @Override
                    public void run() {
                        MainActivity.server.sendBroadcast("12/" + i.toString() + "/temp/" + data);
                        String msg = MainActivity.server.recvWait(1000);
                        runOnUiThread(new DataRunnable(msg, null) {
                            @Override
                            public void run() {
                                if (data != null) {
                                    Toast.makeText(theWindow, "Successfully set temperature", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(theWindow, "Could not send temp to server", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
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
                        MainActivity.server.sendBroadcast("13/" + id);
                        String msg = MainActivity.server.recvWait(1000);
                        if (msg != null) {
                            runOnUiThread(new DataRunnable(msg.substring(3), null) {
                                @Override
                                public void run() {
                                    theCurrentTemp.setText(data);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(theWindow, "Could not get temp from server", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
}
