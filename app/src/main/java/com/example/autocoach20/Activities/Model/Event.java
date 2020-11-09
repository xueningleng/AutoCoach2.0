package com.example.autocoach20.Activities.Model;

/**
 * Class Event represents an object of an event
 * @author Zahraa Marafie
 * @since V2.0
 */
public class Event {
    // ****************************************************************** //
    //   CLASS FIELDS
    // ****************************************************************** //

    private int id;             //serial number (key) in the local database
    
    private int tripEventId;    /* This ID represents the type of the event where
                                 * 0~2 for brakes
                                 * 3~5 for accelerations
                                 * 6~8 for turns
                                 * 9~11 for swerves
                                 */
    private long startTime;     //Start Time of the event
    private String eventType;   //brakes, acceleration, turn, change-lanes
    private String eventRisk;   //safe, medium-risk, and high-risk
    private double score;       //score based on duration of event
    private double duration;    //Duration of the event
    private int windowId;       //In which window the event occured
    private int tripId;         //In which trip the event occured
    private String rawData;     //Raw data like [[0.1,0.2], [0.3,0.4]]
    private String filteredData;//Currently disabled

    // ****************************************************************** //
    //   SETTERS AND GETTERS
    // ****************************************************************** //

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTripEventId() {
        return tripEventId;
    }

    public void setTripEventId(int tripEventId) {
        this.tripEventId = tripEventId;
    }

    public long getStartTime() { return startTime; }

    public void setStartTime(long startTime) { this.startTime = startTime; }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventRisk() {
        return eventRisk;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getRawData() {
        return rawData;
    }

    public void setFilteredData(String filteredData) {
        this.filteredData = filteredData;
    }

    public String getFilteredData() {
        return filteredData;
    }

    public void setEventRisk(String eventRisk) {
        this.eventRisk = eventRisk;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int getWindowId() {
        return windowId;
    }

    public void setWindowId(int windowId) {
        this.windowId = windowId;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }
}
