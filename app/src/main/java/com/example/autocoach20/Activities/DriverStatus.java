package com.example.autocoach20.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autocoach20.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DriverStatus extends AppCompatActivity {
    private User user;
    private int userAge, userGender;
    String oBP, oHR, oF;
    TextView outTxtBP, outTxtHR, outTxtF;
    boolean checkBP, checkHR, checkF;
    public final static String
            MESSAGE_KEY ="com.example.autocoach20.message_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverstatus);
        Intent intent = getIntent();
        //userAge = intent.getIntExtra(MESSAGE_KEY,0);
        //userGender = intent.getIntExtra(MESSAGE_KEY,0);

    }
    private void initializeUI(){
        outTxtBP = (TextView) findViewById(R.id.outputBP);
        outTxtHR = (TextView) findViewById(R.id.outputHR);
        outTxtF = (TextView) findViewById(R.id.outputF);
    }
    private void updateUI(){
        outTxtBP.setText(oBP);
        outTxtHR.setText(oHR);
        outTxtF.setText(oF);
    }
    public void setUpUser(int curHR, int curBPmin, int curBPmax){
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        user = new User(u ,userAge, userGender);
        user.setCurrentHeartRate(curHR); //enter measured heartrate from device
        user.setCurrentBloodPressure(curBPmax,curBPmin); //enter measured bloodpressure from device
    }
    public void runCheck(){
        oBP = user.checkBloodPressure();
        oHR = user.checkHeartRate();
        oF = "COMING SOON"; //add face examination result
    }
    public User getReadyUser(){
        return user;
    }
    public void sendInfo(){
        Intent intent = new Intent(getBaseContext(), StartAutoCoachActivity.class);
        intent.putExtra(MESSAGE_KEY, (Parcelable) user);
        startActivity(intent);
    }

}
