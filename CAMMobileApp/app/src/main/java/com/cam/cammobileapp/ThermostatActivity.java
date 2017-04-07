package com.cam.cammobileapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

import com.cam.cammobileapp.util.DataRunnable;
import com.cam.cammobileapp.util.Toasty;

/**
 * Created by virajdave on 2017-03-20.
 */

public class ThermostatActivity extends AppCompatActivity{
    final Activity activity = this;
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
                            Toasty.show(activity, "Could not get temp from server");
                        }
                    }
                }).start();
            }
        });
    }
}
