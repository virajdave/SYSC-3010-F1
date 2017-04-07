package com.cam.cammobileapp;

public abstract class DataRunnable implements Runnable {
    protected String data;

    public DataRunnable(String data) {
        this.data = data;
    }
}
