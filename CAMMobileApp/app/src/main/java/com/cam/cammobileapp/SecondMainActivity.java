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

public class SecondMainActivity extends AppCompatActivity{

    final Context theWindow = this;
    int numtest = 0;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_main);
        Intent intent = getIntent();

        final TextView theCurrentTemp = (TextView) findViewById(R.id.currentTemp);
        ImageButton upButton = (ImageButton) findViewById(R.id.btn_up);
        upButton.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                numtest+= 1;
                TextView t = (TextView) findViewById(R.id.currentTemp);
                t.setText(numtest+"");

            }
        });

        ImageButton downButton = (ImageButton) findViewById(R.id.btn_down);
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numtest-=1;
                theCurrentTemp.setText(numtest+"");
            }
        });

        Button settingTemp = (Button) findViewById(R.id.setTemperature);
        settingTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentTempToBeSent = theCurrentTemp.getText().toString();
                String sendingTemperature = "12/id/";
                Toast.makeText(theWindow, "Successfully set temperature", Toast.LENGTH_LONG).show();
            }
        });

        Button resettingTemp = (Button) findViewById(R.id.resetTemp);
        resettingTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numtest = 0;
                TextView t = (TextView) findViewById(R.id.currentTemp);
                t.setText(numtest+"");
                Toast.makeText(theWindow, "Successfully reset temperature", Toast.LENGTH_LONG).show();
            }
        });

    }


}
