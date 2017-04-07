package com.cam.cammobileapp.util;

import android.content.Intent;

public abstract class DataRunnable implements Runnable {
    protected String data;
    protected Object i;

    public DataRunnable(String data, Object i) {
        this.data = data;
        this.i = i;
    }
}
