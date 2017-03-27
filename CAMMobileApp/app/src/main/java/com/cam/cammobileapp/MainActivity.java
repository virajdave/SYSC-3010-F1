package com.cam.cammobileapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< HEAD

    ImageButton imageButton1 = (ImageButton) findViewById(R.id.btn_thermostat);
    imageButton1.setOnClickListener(new View.OnClickListener(){

        public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, SecondMainActivity.class);
        startActivity(intent);

            }
        });

        ImageButton imageButton2 = (ImageButton) findViewById(R.id.btn_magicMirror);
        imageButton2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent2 = new Intent(MainActivity.this, ThirdMainActivity.class);
                startActivity(intent2);
            }
        });


        ImageButton imageButton3 = (ImageButton) findViewById(R.id.btn_bedroom);
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(MainActivity.this, FourthMainActivity.class);
                startActivity(intent3);
            }
        });

=======
        /**
        imageButton1 = (ImageButton)

                findViewById(R.id.btn_thermostat);

        imageButton1.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondMainActivity.class);
                startActivity(intent);
            }
        });
    }
    **/
    /**public void addistenerOnButton(){

     imageButton1.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    Toast.makeText(MainActivity.this, "Clicked Thermostat", Toast.LENGTH_SHORT).show();
    }
    });

     } **/



    ImageButton imageButton1 = (ImageButton) findViewById(R.id.btn_thermostat);

    imageButton1.setOnClickListener(new View.OnClickListener(){

        public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, SecondMainActivity.class);
        startActivity(intent);

            }
        });
>>>>>>> 2df008bf5cf674af3a8094dcce31f02bd1bc56bc
    }
}
