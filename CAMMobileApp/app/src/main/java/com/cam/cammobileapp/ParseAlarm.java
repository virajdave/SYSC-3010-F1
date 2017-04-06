package com.cam.cammobileapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;
import java.util.*;

/**
 * Created by virajdave on 2017-04-05.
 */

public class ParseAlarm extends AppCompatActivity {

    final Context prev = this;
    private String messageRecieved;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thermos_available);
    }

    public ParseAlarm(){

    }
    public ArrayList<Integer> parseString(String message){


        System.out.println("helllo");
        ArrayList<Integer> id_alarm = new ArrayList<>();
        String parsingColon[] = new String[10000];

        if (message.substring(0, 2).equals("00")) {

            String parsed[] = message.substring(3).split("/");
            System.out.println("");
            for (int i = 0; i<4; i++){
                System.out.println(parsed[i]);
            }
            for (int i = 0; i<4; i++){
                char placeHolder = parsed[i].charAt(2);
                System.out.println(placeHolder);


                if(placeHolder == '4'){
                    char addedId = parsed[i].charAt(0);
                    System.out.println(addedId);
                    System.out.println("");


                    //System.out.println(addedId);
                    id_alarm.add(Character.getNumericValue(addedId));

                }

                else{
                    System.out.println("Nope not this device");
                    System.out.println("");
                }

            }


        }
        System.out.println("");
        System.out.println("In ArrayList");
        System.out.println("");
        for(int value: id_alarm){

            System.out.println(value);

        }
        return id_alarm;

    }
}
