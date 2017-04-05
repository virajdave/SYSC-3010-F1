package com.cam.cammobileapp;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

/**
 * Created by virajdave on 2017-04-05.
 */

public class grabLocation extends Service implements LocationListener {

    final Context prev = this;
    private Context thisContext;
    boolean isGPSOn = false;
    boolean isNetworkOn = false;
    boolean isLocationPossible = false;

    Location theLocation;
    double theLatitude, theLongitude;

    LocationManager locationManager;
    final AlertDialog alertMan = new AlertDialog.Builder(prev).create();

    public grabLocation(Context thisContext) {
        this.thisContext = thisContext;
        getLocation();
    }

    private Location getLocation() {
        try {
            locationManager = (LocationManager) thisContext.getSystemService(Context.LOCATION_SERVICE);
            isGPSOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkOn = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSOn && !isNetworkOn) {
                Toast.makeText(prev, "Turn on the GPS or Your Network", Toast.LENGTH_LONG).show();
            } else {
                this.isLocationPossible = true;

                if (isNetworkOn) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 3, this);

                    if (locationManager != null) {
                        theLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (theLocation != null) {
                            theLatitude = theLocation.getLatitude();
                            theLongitude = theLocation.getLongitude();
                        }
                    }
                }

                if (isGPSOn) {
                    if (theLocation == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 3, this);
                        if (locationManager != null) {
                            theLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (theLocation != null) {
                                theLatitude = theLocation.getLatitude();
                                theLongitude = theLocation.getLongitude();
                            }
                        }
                    }

                } else {
                    enableSettings();
                }
            }
        } catch (Exception ce) {
            ce.printStackTrace();
        }
        return theLocation;
    }

    public void enableSettings() {
        AlertDialog.Builder alertDialogForSettings = new AlertDialog.Builder(grabLocation.this);

        alertDialogForSettings.setTitle("GPS' Settings");

        alertDialogForSettings.setMessage("GPS isn't turned on. Do you wanna turn it on");

        alertDialogForSettings.setPositiveButton("Set it", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int value) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.cancel();
            }
        });

        //if cancelled
        alertDialogForSettings.setNegativeButton("Cancelled", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int value) {
                dialog.cancel();
            }
        });

        //show the alert
        alertDialogForSettings.show();
    }

    public double getTheLatitude() {
        if (theLocation != null) {
            theLatitude = theLocation.getLatitude();
        }
        return theLatitude;
    }

    public double getTheLongitude() {
        if (theLocation != null) {
            theLongitude = theLocation.getLongitude();
        }
        return theLongitude;
    }

    public boolean isLocationPossible() {
        return this.isLocationPossible;
    }

    @Override
    public void onLocationChanged(Location theLocation) {
        if (theLocation != null) {
            this.theLocation = theLocation;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
}

    }
