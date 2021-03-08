package com.example.autocoach20.Activities.Model;

public class SpeedRecord {
    public int record_id, trip_id;
    public Long current_t;
    public int speed;
    public int raspi; //0 default, 1 left, 2 right, 3 front
    public float gyro=10000;

}
