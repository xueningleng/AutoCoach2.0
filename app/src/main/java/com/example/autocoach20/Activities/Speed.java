package com.example.autocoach20.Activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import java.util.List;

public abstract class Speed extends Context {
    public int speed;
    private LocationManager locationManager;
    public void getLocation(){
        // ************************************************************************** //
        // SETUP LOCATION MANAGER TO CALCULATE SPEED
        // ************************************************************************** //
        //This gets the GPS to calculate the speed from the cellphone
        LocationManager locationManager;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Get available Location providers
        //Use GPS if possible. Otherwise use cellular network
        //Toast the user if neither is available
        List<String> providerList = locationManager.getProviders(true);
        //private static final Object TAG = null;
        String provider;
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            //
            return;
        }
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            //Get location service
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            /*
            Get the currently available location providers
            List<String> list = locationManager.getProviders(true);
            */

            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                updateSpeedByLocation(location);
            }
            //Set the timer for 5 seconds to request location information
            locationManager.requestLocationUpdates(provider, 5000, 1,
                    locationListener);
        }
    }


    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        //Updates every 5 seconds
        @Override
        public void onLocationChanged(Location location) {
            // update speed by current location
            updateSpeedByLocation(location);
        }
    };
    private void updateSpeedByLocation(Location location) {
        speed = (int) (location.getSpeed() * 3.6); // m/s --> Km/h
    }

    public int getSpeed() {
        return speed;
    }


}
