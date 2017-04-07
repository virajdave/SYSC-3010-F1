package com.cam.cammobileapp;

import android.app.Activity;
import android.os.Looper;
import android.widget.Toast;

public class Toasty {
    private static Toast currentToast;

    public static void show(final Activity activity, final String text) {
        if (Looper.getMainLooper().equals(Looper.myLooper())) {
            if (currentToast != null) {
                currentToast.cancel();
            }
            currentToast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);
            currentToast.show();
        } else {
            // Run this function again on the UI thread if it is not.
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    show(activity, text);
                }
            });
        }
    }
}
