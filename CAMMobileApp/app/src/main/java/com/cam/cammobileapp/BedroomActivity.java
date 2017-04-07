package com.cam.cammobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.app.Activity;
import android.widget.EditText;

import com.cam.cammobileapp.util.Toasty;


/**
 * Created by virajdave on 2017-03-26.
 */

public class BedroomActivity extends AppCompatActivity {
    final Activity activity = this;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth_main);
        Intent intent = getIntent();

        ImageButton imageButton8 = (ImageButton) findViewById(R.id.btn_alarm);
        imageButton8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final AlertDialog alarmDialog = new AlertDialog.Builder(activity).create();
                View alarm_layout = getLayoutInflater().inflate(R.layout.alarm_layout, null);
                final EditText hour = (EditText) alarm_layout.findViewById(R.id.enterHour);
                final EditText minute = (EditText) alarm_layout.findViewById(R.id.enterMinute);

                alarmDialog.setView(alarm_layout);
                alarmDialog.show();

                Button alarm_Button = (Button) alarm_layout.findViewById(R.id.saveAlarm);
                alarm_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmDialog.dismiss();
                        //Less than 10, add a zero in the front of the hour
                        String finalHour = hour.getText().toString();
                        String finalMin  = minute.getText().toString();
                        String messageToAlarmDriver="12/id/" + finalHour+ "," + minute;

                        //server.getNetInfo()
                        Toasty.show(activity, "Successfully sent the Alarm Desired to the Alarm");
                    }

                });

            }
        });

        final Switch lightControl = (Switch) findViewById(R.id.switch1);

        lightControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Boolean stateOfLights = lightControl.isChecked();
                if (stateOfLights) {
                    Toasty.show(activity, "Turned On the Lights");
                    String messageToLightDriver="12/id/l/0)";
                }
                else if(!stateOfLights) {
                    Toasty.show(activity, "Turned Off the Lights");
                    String messageToLightDriver="12/id/l/f)";
                    }

                //o - on, f - off

                }
            });
        }
    }