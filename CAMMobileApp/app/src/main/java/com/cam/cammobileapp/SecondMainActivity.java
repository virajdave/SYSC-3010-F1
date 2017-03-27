package com.cam.cammobileapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by virajdave on 2017-03-20.
 */

public class SecondMainActivity extends Activity{


    int numtest = 0;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_main);
        Intent intent = getIntent();

        ImageButton imageButton4 = (ImageButton) findViewById(R.id.btn_up);
        imageButton4.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                numtest+= 1;
                TextView t = (TextView) findViewById(R.id.currentTemp);
                t.setText(numtest+"");

            }
        });
    }


}
