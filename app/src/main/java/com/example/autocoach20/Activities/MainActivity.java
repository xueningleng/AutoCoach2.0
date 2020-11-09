package com.example.autocoach20.Activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autocoach20.R;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private int speed;
    public static MainActivity mainActivity;

    private LocationManager locationManager;

    private Messenger toFeedbackMessenger = null;
    private MyReceiver detectReceiver;

    private TextView current_score, current_score_n;
    private TextView total_coins, total_coins_n;
    private TextView trip_score, trip_score_n;
    private TextView add_coins_n;

    //score manipulation
    public double tripOverallScore;
    public long tripStartTime;
    public long tripEndTime = 0;
    //public Trip trip;

    AuthenticationActivity authActivity = new AuthenticationActivity();
    FirebaseUser currentUser = authActivity.getCurrentUser();

    public double getTripOverallScore() {
        return tripOverallScore;
    }

    public void setTripOverallScore(double tripOverallScore) {
        this.tripOverallScore = tripOverallScore;
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

    double[] driverMeans = new double[]{
            //0.06031552, 0.05229149, 0.019399881, 0.017509023, 0.015382588, 0.8554055, 0.08283522, 0.06175925, 0.8509335, 0.09026337, 0.05880312, 0.8505389, 0.08672229, 0.06273885, 0.8562540, 0.08586243, 0.05788359
            0.02031552, 0.0429149, 0.02399881, 0.07009023, 0.018382588, 0.9554055, 0.07283522, 0.05175925, 0.9509335, 0.05626337, 0.04680312, 0.9005389, 0.05272229, 0.07073885, 0.5062540, 0.08386243, 0.06788359
    };
    // service connection to bind feedback service: communicate with svm lda and feedback
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            System.out.println("Feedback Service is Connected");
            toFeedbackMessenger = new Messenger(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //nothing
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button signIn = (Button) findViewById(R.id.buttonSignIn);
        signIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                signIn(view);
            }
        });
    }
    public void signIn(View view){
        Intent intent = new Intent(this, AuthenticationActivity.class);
        Button signIn = (Button) findViewById(R.id.buttonSignIn);
        startActivity(intent);
    }
    public void signUp(View view){
        Intent intent = new Intent(this, SignUp.class);
        Button signUp = (Button) findViewById(R.id.buttonSignUp);
        startActivity(intent);
    }


    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras(); //getExtras is a get function to get message in intent
            assert bundle != null;


            Message msg = Message.obtain(null,1,0); //message is 1, refer to Feedback Service
            msg.setData(bundle);
            try{
                toFeedbackMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public MainActivity(){
        mainActivity = this;
        tripStartTime = System.currentTimeMillis();
    }

    // ***************************************************************** //
    // SETTERS AND GETTERS
    // ***************************************************************** //
    FirebaseUser user;

    public FirebaseUser getUser(){
        return this.user;
    }

    public void setUser(FirebaseUser u){
        user = u;
    }

    public static MainActivity getMainActivity(){
        return mainActivity;
    }
}