package com.cam.cammobileapp;

import android.util.Log;

import com.cam.cammobileapp.util.Parse;

import java.util.ArrayList;

public class Devices {
    public  ArrayList<Integer> mirror;
    public  ArrayList<Integer> thermo;
    public  ArrayList<Integer> bed;

    public Devices() {
        mirror = new ArrayList<>();
        thermo = new ArrayList<>();
        bed = new ArrayList<>();
    }

    //Parse the incoming NETINF message, and add each type of device
    //to it's respectable ArrayList of device ID's to display on app
    public void parse(String s) {
        mirror.clear();
        thermo.clear();
        bed.clear();

        try {
            if (s.substring(0, 2).equals("00")) {

                String parsed[] = s.substring(3).split("/");

                for (String dev : parsed) {
                    if (dev.length() != 0) {
                        String[] split = dev.split(":");
                        int id = Parse.toInt(split[0]);
                        int type = Parse.toInt(split[1]);
                        //boolean dead = Parse.toBool(split[2]);

                        if (type == 2) {
                            mirror.add(id);
                        } else if (type == 3) {
                            thermo.add(id);
                        } else if (type == 4) {
                            bed.add(id);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Devices", "error parsing net info", e);
        }
    }
}
