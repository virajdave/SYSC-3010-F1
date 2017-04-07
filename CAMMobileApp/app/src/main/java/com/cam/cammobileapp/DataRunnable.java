package com.cam.cammobileapp;

import android.content.Intent;

public abstract class DataRunnable implements Runnable {
    protected String data;
    protected Intent i;

    public DataRunnable(String data, Intent i) {
        this.data = data;
        this.i = i;
    }
}
