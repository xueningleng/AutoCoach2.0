package com.example.autocoach20.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autocoach20.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DriverStatus extends AppCompatActivity {
    private User user;
    private int userAge, userGender;
    String outputBP, outputHR, outputF;
    public final static String
            MESSAGE_KEY ="com.example.autocoach20.message_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverstatus);
        Intent intent = getIntent();
        userAge = intent.getIntExtra(MESSAGE_KEY,0);
        userGender = intent.getIntExtra(MESSAGE_KEY,0);

    }
    public void setUpUser(){
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        user = new User(u ,userAge, userGender);

    }

}
