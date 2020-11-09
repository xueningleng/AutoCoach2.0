package com.example.autocoach20.Activities.Model;

public class Recommendation {

    int id;
    int recommendationId;
    int eventId;
    int tripId;
    String recommendationRiskLevel; //The glow of the recommendation ~ either red (high-risk) of orange (medium-risk)


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(int recommendationId) {
        this.recommendationId = recommendationId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getRecommendationRiskLevel() {
        return recommendationRiskLevel;
    }

    public void setRecommendationRiskLevel(String recommendationRiskLevel) {
        this.recommendationRiskLevel = recommendationRiskLevel;
    }




}
