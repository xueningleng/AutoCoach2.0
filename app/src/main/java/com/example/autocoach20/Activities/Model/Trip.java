package com.example.autocoach20.Activities.Model;

public class Trip {

    private int tripId;
    private long tripStartTime;
    private long tripEndTime;
    private String tripUserId;
    private double tripScore; //Average score of all trip windows

    public Trip(int tripId, long tripStartTime, String tripUserId) {
        this.tripId = tripId;
        this.tripStartTime = tripStartTime;
        this.tripUserId = tripUserId;
    }

    public Trip(int tripId, String tripUserId, long tripStartTime, long tripEndTime, double tripScore) {
        this.tripId = tripId;
        this.tripStartTime = tripStartTime;
        this.tripUserId = tripUserId;
        this.tripEndTime = tripEndTime;
        this.tripScore = tripScore;
    }


    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
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

    public String getTripUserId() {
        return tripUserId;
    }

    public void setTripUserId(String tripUserId) {
        this.tripUserId = tripUserId;
    }

    public double getTripScore() {
        return tripScore;
    }

    public void setTripScore(double tripScore) {
        this.tripScore = tripScore;
    }
}
