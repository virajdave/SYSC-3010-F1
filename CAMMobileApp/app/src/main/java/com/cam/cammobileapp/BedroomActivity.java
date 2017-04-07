package com.cam.cammobileapp;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TimePicker;

import com.cam.cammobileapp.util.Parse;
import com.cam.cammobileapp.util.Toasty;

import java.util.Calendar;

public class BedroomActivity extends AppCompatActivity {
    private final Activity activity = this;

    private boolean lights;
    private Integer hour, minute;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bedroom);

        Intent intent = getIntent();
        final int id = intent.getIntExtra("deviceID", -1);
        String[] data = intent.getStringExtra("deviceInfo").split("/");
        try {
            lights = Parse.toBool(data[0]);
        } catch (NumberFormatException e) {
            lights = false;
        }
        try {
            hour = Parse.toInt(data[1].substring(0, 1));
            minute = Parse.toInt(data[1].substring(3, 4));
        } catch (Exception e) {
            hour = minute = null;
        }

        findViewById(R.id.btn_alarm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (hour == null || minute == null) {
                    final Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                }

                TimePickerDialog alarmPicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, final int hour, final int minute) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String sHour = (hour < 10 ? "0" : "") + hour;
                                String sMinute = (minute < 10 ? "0" : "") + minute;
                                Log.i("aaaa", sHour + ":" + sMinute);
                            }
                        }).start();
                    }
                }, hour, minute, true);
                alarmPicker.setTitle("Select Time");
                alarmPicker.show();

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