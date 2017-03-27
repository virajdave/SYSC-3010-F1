package com.cam.cammobileapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
<<<<<<< HEAD
import android.widget.TextView;
=======
>>>>>>> 2df008bf5cf674af3a8094dcce31f02bd1bc56bc
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by virajdave on 2017-03-20.
 */

public class SecondMainActivity extends Activity{
<<<<<<< HEAD


    int numtest = 0;
=======
    @Override
>>>>>>> 2df008bf5cf674af3a8094dcce31f02bd1bc56bc
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_main);
        Intent intent = getIntent();
<<<<<<< HEAD

        ImageButton imageButton4 = (ImageButton) findViewById(R.id.btn_up);
        imageButton4.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                numtest+= 1;
                TextView t = (TextView) findViewById(R.id.currentTemp);
                t.setText(numtest+"");

            }
        });
    }


=======
    }

>>>>>>> 2df008bf5cf674af3a8094dcce31f02bd1bc56bc
}
