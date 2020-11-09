package com.example.autocoach20.Activities;

import com.google.firebase.auth.FirebaseUser;

public class User {
    private String user_id;
    private String user_email;
    private String user_name;

    public int current_score;
    public int total_coins;
    public int trip_score;

    private double personalAccelerationsScore = 82;
    private double personalBrakesScore = 80;
    private double personalTurnsScore = 79;
    private double personalSwervesScore = 87;
    private double driverPersonalScore = 86;

    double[] driverMeans = new double[]{
            //0.06031552, 0.05229149, 0.019399881, 0.017509023, 0.015382588, 0.8554055, 0.08283522, 0.06175925, 0.8509335, 0.09026337, 0.05880312, 0.8505389, 0.08672229, 0.06273885, 0.8562540, 0.08586243, 0.05788359
            0.02031552, 0.0429149, 0.02399881, 0.07009023, 0.018382588, 0.9554055, 0.07283522, 0.05175925, 0.9509335, 0.05626337, 0.04680312, 0.9005389, 0.05272229, 0.07073885, 0.5062540, 0.08386243, 0.06788359
    };

    public User(FirebaseUser u){
        user_id = u.getUid();
        user_email = u.getEmail();
        user_name = u.getDisplayName();
    };

}
