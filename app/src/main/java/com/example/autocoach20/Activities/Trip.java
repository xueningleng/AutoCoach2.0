package com.example.autocoach20.Activities;

import android.annotation.SuppressLint;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class Trip {
    //score manipulation
    public double tripOverallScore;
    public long tripStartTime;
    public long tripEndTime = 0;

    Operations dbOperations = new Operations();
    public int DBTripId;

    public int getDBTripId () { return DBTripId; }

    public Trip(){

    }
    public double getTripOverallScore() {
        return tripOverallScore;
    }

    public void setTripOverallScore(double tripOverallScore) {
        this.tripOverallScore = tripOverallScore;
    }

    public long getTripStartTime() {
        return tripStartTime;
    }

    public void setTripStartTime(long tripStartTime) {
        this.tripStartTime = tripStartTime;
    }

    public long getTripEndTime() {
        return tripEndTime;
    }

    public void setTripEndTime(long tripEndTime) {
        this.tripEndTime = tripEndTime;
    }

    @SuppressLint("RestrictedApi")
    private void addTripToDB(long tripStartTime, long tripEndTime){
        //Don't use another thread, let it record as part of the main thread so app doesnt crash

        dbOperations.addToTableTrip(getApplicationContext(), null, getTripOverallScore(), tripStartTime, tripEndTime, 0);
    }

}
