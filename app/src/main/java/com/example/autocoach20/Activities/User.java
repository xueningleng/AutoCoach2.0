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

    //biological information
    private int age;
    private int gender; // 0 for female, 1 for male
    //normal default sets to average for humans, could manually set to personalized
    private double normal_blood_pressure_systolic = 120; //mm Hg, maximum
    private double normal_blood_pressure_diastolic = 80; //mm Hg, minimum

    private double normal_heart_rate_max;

    public double current_heart_rate;
    public double current_blood_pressure_systolic;
    public double current_blood_pressure_diastolic;
    double[] driverMeans = new double[]{
            //0.06031552, 0.05229149, 0.019399881, 0.017509023, 0.015382588, 0.8554055, 0.08283522, 0.06175925, 0.8509335, 0.09026337, 0.05880312, 0.8505389, 0.08672229, 0.06273885, 0.8562540, 0.08586243, 0.05788359
            0.02031552, 0.0429149, 0.02399881, 0.07009023, 0.018382588, 0.9554055, 0.07283522, 0.05175925, 0.9509335, 0.05626337, 0.04680312, 0.9005389, 0.05272229, 0.07073885, 0.5062540, 0.08386243, 0.06788359
    };

    public User(FirebaseUser u, int userAge, int userGender){
        user_id = u.getUid();
        user_email = u.getEmail();
        user_name = u.getDisplayName();
        this.age = userAge;
        this.gender = userGender;
    };

    public String getUser_name(){
        return user_name;
    }
    public String getUser_id(){
        return user_id;
    }
    private void SetNormalHeartRate(int heartRateMax){
        if (heartRateMax <=0) { // no input
            this.normal_heart_rate_max = heartRateMax;
        }
        else{
            switch(gender){
                case 0: //female
                    this.normal_heart_rate_max = 84;
                case 1: //male
                    this.normal_heart_rate_max = 82;
            }
        }
    }

    public void setCurrentBloodPressure(int bpmax, int bpmin){
        this.current_blood_pressure_systolic = bpmax;
        this.current_blood_pressure_diastolic = bpmin;
    }

    public void setCurrentHeartRate(int hr){
        this.current_heart_rate = hr;
    }
    public String checkBloodPressure(){
        if (current_blood_pressure_systolic>normal_blood_pressure_systolic){
            return ("WARNING: HIGH BLOOD PRESSURE");
        }
        else if (current_blood_pressure_diastolic<normal_blood_pressure_diastolic){
            return ("WARNING: LOW BLOOD PRESSURE");
        }
        else{
            return ("NORMAL BLOOD PRESSURE");
        }
    }
    public String checkHeartRate(){
        if (current_heart_rate > normal_heart_rate_max) {
            return ("WARNING: DANGEROUS HEART RATE");
        }
        else{
            return ("NORMAL HEART RATE");
        }
    }
}
