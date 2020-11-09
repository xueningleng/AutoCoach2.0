package com.example.autocoach20.Activities.Model;


//The pattern window
public class Window {

    int id;
    int windowId;
    long windowTimestamp;
    String pattern; // e.g. ~abdef
    double windowScore; //get from the LDA probabilities and then use the scoring Engine
    int tripId;
    String behavior;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWindowId() {
        return windowId;
    }

    public void setWindowId(int windowId) {
        this.windowId = windowId;
    }

    public long getWindowTimestamp() {
        return windowTimestamp;
    }

    public void setWindowTimestamp(long windowTimestamp) {
        this.windowTimestamp = windowTimestamp;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public double getWindowScore() {
        return windowScore;
    }

    public void setWindowScore(double windowScore) {
        this.windowScore = windowScore;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getBehavior() { return behavior; }

    public void setBehavior(String behavior) { this.behavior = behavior; }
}
