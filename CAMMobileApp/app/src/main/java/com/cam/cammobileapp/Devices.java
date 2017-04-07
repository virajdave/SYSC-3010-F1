package com.cam.cammobileapp;

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

    public void parse(String s) {
        if (s.substring(0, 2).equals("00")) {

            String parsed[] = s.substring(3).split("/");
            System.out.println("");

            for (String dev : parsed) {
                String[] split = dev.split(":");
                int id = Integer.parseInt(split[0]);
                int type = Integer.parseInt(split[1]);

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
}
