package com.example.autocoach20.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autocoach20.R;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public static MainActivity mainActivity;

    public UserTrip userTrip;
    ImageButton sufolder;
    ImageButton sifolder;
    Button subtn;
    Button sibtn;
    TextView title;
    SignInActivity authActivity = new SignInActivity();
    FirebaseUser currentUser= authActivity.getCurrentUser();
   // User current_user= new User(currentUser, 1, 21); //currently default, need to be taken by start activity
    public FirebaseUser getUser(){
        return currentUser;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);



    }

    public void initializeUI(){
        title = findViewById(R.id.projectTitle);
        sufolder = findViewById(R.id.signUpFolder);
        sifolder = findViewById(R.id.signInFolder);
        Button signIn = (Button) findViewById(R.id.buttonSignIn);
        signIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                signIn(view);
            }
        });

        Button signUp = (Button) findViewById(R.id.buttonSignUp);
        signUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                signUp(view);
            }
        });
    }
    public void signIn(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        Button signIn = (Button) findViewById(R.id.buttonSignIn);
        startActivity(intent);
    }
    public void signUp(View view){
        Intent intent = new Intent(this, DriverStatus.class);
        startActivity(intent);
    }


    public MainActivity(){
        mainActivity = this;
//        trip.setTripStartTime(System.currentTimeMillis());
    }

    public static MainActivity getMainActivity(){
        return mainActivity;
    }

    public void setUpUI(){
        TextView current_score, current_score_n;
        TextView total_coins, total_coins_n;
        TextView trip_score, trip_score_n;
        TextView add_coins_n;

        ImageView acc_bar_1, acc_bar_2, acc_bar_3, acc_bar_4, acc_bar_5, acc_bar_6, acc_bar_7, acc_bar_8, acc_bar_9, acc_bar_10;
        ImageView brake_bar_1, brake_bar_2, brake_bar_3, brake_bar_4, brake_bar_5, brake_bar_6, brake_bar_7, brake_bar_8, brake_bar_9, brake_bar_10;
        ImageView turn_bar_1, turn_bar_2, turn_bar_3, turn_bar_4, turn_bar_5, turn_bar_6, turn_bar_7, turn_bar_8, turn_bar_9, turn_bar_10;
        ImageView swerve_bar_1, swerve_bar_2, swerve_bar_3, swerve_bar_4, swerve_bar_5, swerve_bar_6, swerve_bar_7, swerve_bar_8, swerve_bar_9, swerve_bar_10;

        ImageView acceleration_threshold1, acceleration_threshold2, acceleration_threshold3, acceleration_threshold4, acceleration_threshold5;
        ImageView acceleration_threshold6, acceleration_threshold7, acceleration_threshold8, acceleration_threshold9, acceleration_threshold10;

        ImageView brakes_threshold1, brakes_threshold2, brakes_threshold3, brakes_threshold4, brakes_threshold5, brakes_threshold6;
        ImageView brakes_threshold7, brakes_threshold8, brakes_threshold9, brakes_threshold10;

        ImageView turns_threshold1, turns_threshold2, turns_threshold3, turns_threshold4, turns_threshold5, turns_threshold6, turns_threshold7;
        ImageView turns_threshold8, turns_threshold9, turns_threshold10;

        ImageView swerves_threshold1, swerves_threshold2, swerves_threshold3, swerves_threshold4, swerves_threshold5, swerves_threshold6;
        ImageView swerves_threshold7, swerves_threshold8, swerves_threshold9, swerves_threshold10;

        ImageView brake_icon, turn_icon, swerve_icon, acc_icon;

        ImageView car, coins_box, feedback_icon;
        ImageView add_coins;

        ImageView acceleration_glow, brake_glow, turn_glow, swerve_glow;

        Button end_btn;

        MediaPlayer coin = new MediaPlayer();
        MediaPlayer improved = new MediaPlayer();




        // ************************************************************************** //
        // INITIALIZE PARAMETERS AND IMAGES FOR THE SCREEN
        // ************************************************************************** //
        current_score_n = findViewById(R.id.score_n);    //It's the Current Score number
        total_coins_n = findViewById(R.id.totalcoins_n); //Total coins number
        trip_score_n = findViewById(R.id.tripscore_n);   //Trip score
        current_score = findViewById(R.id.currentscore); //Current Score Text
        total_coins = findViewById(R.id.totalcoins);     //Total Coins Text
        trip_score = findViewById(R.id.tripscore);       //Trip Score Text
        car = findViewById(R.id.car);                    //Car Icon - Image
        coins_box = findViewById(R.id.coinsbox);         // Coin Box Icon - Image

        //Feedback Icon - Center Image
        feedback_icon= findViewById(R.id.feedback_icon);

        //Give coins
        add_coins = findViewById(R.id.gold_coin);

        //Accelerometer Bar components
        acc_bar_1 = findViewById(R.id.acc_bar_1);
        acc_bar_2 = findViewById(R.id.acc_bar_2);
        acc_bar_3 = findViewById(R.id.acc_bar_3);
        acc_bar_4 = findViewById(R.id.acc_bar_4);
        acc_bar_5 = findViewById(R.id.acc_bar_5);
        acc_bar_6 = findViewById(R.id.acc_bar_6);
        acc_bar_7 = findViewById(R.id.acc_bar_7);
        acc_bar_8 = findViewById(R.id.acc_bar_8);
        acc_bar_9 = findViewById(R.id.acc_bar_9);
        acc_bar_10 = findViewById(R.id.acc_bar_10);

        //Brakes Bar components
        brake_bar_1 = findViewById(R.id.brake_bar_1);
        brake_bar_2 = findViewById(R.id.brake_bar_2);
        brake_bar_3 = findViewById(R.id.brake_bar_3);
        brake_bar_4 = findViewById(R.id.brake_bar_4);
        brake_bar_5 = findViewById(R.id.brake_bar_5);
        brake_bar_6 = findViewById(R.id.brake_bar_6);
        brake_bar_7 = findViewById(R.id.brake_bar_7);
        brake_bar_8 = findViewById(R.id.brake_bar_8);
        brake_bar_9 = findViewById(R.id.brake_bar_9);
        brake_bar_10 = findViewById(R.id.brake_bar_10);

        //Turns Bar components
        turn_bar_1 = findViewById(R.id.turn_bar_1);
        turn_bar_2 = findViewById(R.id.turn_bar_2);
        turn_bar_3 = findViewById(R.id.turn_bar_3);
        turn_bar_4 = findViewById(R.id.turn_bar_4);
        turn_bar_5 = findViewById(R.id.turn_bar_5);
        turn_bar_6 = findViewById(R.id.turn_bar_6);
        turn_bar_7 = findViewById(R.id.turn_bar_7);
        turn_bar_8 = findViewById(R.id.turn_bar_8);
        turn_bar_9 = findViewById(R.id.turn_bar_9);
        turn_bar_10 = findViewById(R.id.turn_bar_10);

        //Swerves Bar components
        swerve_bar_1 = findViewById(R.id.swerve_bar_1);
        swerve_bar_2 = findViewById(R.id.swerve_bar_2);
        swerve_bar_3 = findViewById(R.id.swerve_bar_3);
        swerve_bar_4 = findViewById(R.id.swerve_bar_4);
        swerve_bar_5 = findViewById(R.id.swerve_bar_5);
        swerve_bar_6 = findViewById(R.id.swerve_bar_6);
        swerve_bar_7 = findViewById(R.id.swerve_bar_7);
        swerve_bar_8 = findViewById(R.id.swerve_bar_8);
        swerve_bar_9 = findViewById(R.id.swerve_bar_9);
        swerve_bar_10 = findViewById(R.id.swerve_bar_10);


        // *************************************************************** //
        // Assigning values to Threshold Attributes
        // *************************************************************** //

        acceleration_threshold1 = findViewById(R.id.accel_threshold1);
        acceleration_threshold2 = findViewById(R.id.accel_threshold2);
        acceleration_threshold3 = findViewById(R.id.accel_threshold6);
        acceleration_threshold4 = findViewById(R.id.accel_threshold9);
        acceleration_threshold5 = findViewById(R.id.accel_threshold8);
        acceleration_threshold6 = findViewById(R.id.accel_threshold9);
        acceleration_threshold7 = findViewById(R.id.accel_threshold9);
        acceleration_threshold8 = findViewById(R.id.accel_threshold8);
        acceleration_threshold9 = findViewById(R.id.accel_threshold2);
        acceleration_threshold10 = findViewById(R.id.accel_threshold1);

        brakes_threshold1 = findViewById(R.id.brakes_threshold1);
        brakes_threshold2 = findViewById(R.id.brakes_threshold2);
        brakes_threshold3 = findViewById(R.id.brakes_threshold3);
        brakes_threshold4 = findViewById(R.id.brakes_threshold4);
        brakes_threshold5 = findViewById(R.id.brakes_threshold5);
        brakes_threshold6 = findViewById(R.id.brakes_threshold6);
        brakes_threshold7 = findViewById(R.id.brakes_threshold7);
        brakes_threshold8 = findViewById(R.id.brakes_threshold8);
        brakes_threshold9 = findViewById(R.id.brakes_threshold9);
        brakes_threshold10 = findViewById(R.id.brakes_threshold10);

        turns_threshold1 = findViewById(R.id.turns_threshold1);
        turns_threshold2 = findViewById(R.id.turns_threshold2);
        turns_threshold3 = findViewById(R.id.turns_threshold3);
        turns_threshold4 = findViewById(R.id.turns_threshold4);
        turns_threshold5 = findViewById(R.id.turns_threshold5);
        turns_threshold6 = findViewById(R.id.turns_threshold6);
        turns_threshold7 = findViewById(R.id.turns_threshold7);
        turns_threshold8 = findViewById(R.id.turns_threshold8);
        turns_threshold9 = findViewById(R.id.turns_threshold9);
        turns_threshold10 = findViewById(R.id.turns_threshold10);

        swerves_threshold1 = findViewById(R.id.swerves_threshold1);
        swerves_threshold2 = findViewById(R.id.swerves_threshold2);
        swerves_threshold3 = findViewById(R.id.swerves_threshold3);
        swerves_threshold4 = findViewById(R.id.swerves_threshold4);
        swerves_threshold5 = findViewById(R.id.swerves_threshold5);
        swerves_threshold6 = findViewById(R.id.swerves_threshold6);
        swerves_threshold7 = findViewById(R.id.swerves_threshold7);
        swerves_threshold8 = findViewById(R.id.swerves_threshold8);
        swerves_threshold9 = findViewById(R.id.swerves_threshold9);
        swerves_threshold10 = findViewById(R.id.swerves_threshold10);

        // *************************************************************** //
        // Setup Recommendation Bars Glow
        // *************************************************************** //

        acceleration_glow = findViewById(R.id.acceleration_glow);
        brake_glow = findViewById(R.id.brake_glow);
        turn_glow = findViewById(R.id.turn_glow);
        swerve_glow = findViewById(R.id.swerve_glow);

        acceleration_glow.setVisibility(View.INVISIBLE);
        brake_glow.setVisibility(View.INVISIBLE);
        turn_glow.setVisibility(View.INVISIBLE);
        swerve_glow.setVisibility(View.INVISIBLE);

        // *************************************************************** //
        // Initialize End button
        // *************************************************************** //

        end_btn = findViewById(R.id.btn_end);
    }
}