package com.example.autocoach20.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autocoach20.Activities.Model.Trip;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class StartAutoCoachActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    Operations dbOperations = new Operations();
    public int DBTripId;
    public int getDBTripId () { return DBTripId; }
    public UserTrip userTrip;
    public Trip trip;
    public User user;
    public FirebaseUser fbUser;
    public final static String
            MESSAGE_KEY ="com.example.autocoach20.message_key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        userTrip.tripStartTime =System.currentTimeMillis();
        userTrip.tripEndTime = 0;
        userTrip.addTripToDB();
        trip = dbOperations.readCurrentTripDetails(this);
        if (trip.getTripId()!=0)
            DBTripId = trip.getTripId();
        user = intent.getParcelableExtra(MESSAGE_KEY);

    }
    private void initializeUI(){

    }




}
