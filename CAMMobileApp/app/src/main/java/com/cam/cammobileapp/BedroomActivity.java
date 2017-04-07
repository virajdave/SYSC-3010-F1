package com.cam.cammobileapp;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
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
            lights = Parse.toBool(data[1]);
        } catch (Exception e) {
            lights = false;
        }
        try {
            hour = Parse.toInt(data[2].substring(0, 1));
            minute = Parse.toInt(data[2].substring(3, 4));
        } catch (Exception e) {
            hour = minute = null;
        }

        final TextView currentAlarm = (TextView) findViewById(R.id.currentAlarm);
        if (hour == null || minute == null) {
            currentAlarm.setText("Not Set");
        } else {
            currentAlarm.setText(hour + ":" + pad(minute));
        }

        findViewById(R.id.btn_alarm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (hour == null || minute == null) {
                    final Calendar c = Calendar.getInstance();
                    hour = c.get(Calendar.HOUR_OF_DAY);
                    minute = c.get(Calendar.MINUTE) + 1;
                }

                TimePickerDialog alarmPicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, final int h, final int m) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final String sHour = pad(h);
                                final String sMinute = pad(m);

                                MainActivity.server.sendBroadcast(Parse.toString("/", "12", id, "alarm", sHour + ":" + sMinute));
                                String msg = MainActivity.server.recvWait(1000);
                                if (msg != null && msg.charAt(3) == '1') {
                                    hour = h;
                                    minute = m;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            currentAlarm.setText(h + ":" + sMinute);
                                            Toasty.show(activity, "Set new alarm time");
                                        }
                                    });
                                } else {
                                    Toasty.show(activity, "Could not set alarm");
                                }
                            }
                        }).start();
                    }
                }, hour, minute, true);
                alarmPicker.setTitle("Select Time");
                alarmPicker.show();
            }
        });

        final Switch lightControl = (Switch) findViewById(R.id.switch1);
        lightControl.setChecked(lights);

        lightControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final boolean stateOfLights = lightControl.isChecked();

                        MainActivity.server.sendBroadcast(Parse.toString("/", "12", id, "l", stateOfLights));
                        String msg = MainActivity.server.recvWait(1000);
                        if (msg != null && msg.charAt(3) == '1') {
                            lights = stateOfLights;
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    lightControl.setChecked(lights);
                                }
                            });
                            Toasty.show(activity, "Could not turn the lights " + (stateOfLights ? "on" : "off"));
                        }
                    }
                }).start();
            }
        });
    }

    private String pad(int num) {
        return (num < 10 ? "0" : "") + num;
    }
}
