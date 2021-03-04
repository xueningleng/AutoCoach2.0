package com.example.autocoach20.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.autocoach20.Activities.Model.Trip;
import com.example.autocoach20.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author Xuening Leng, Yuehan Cui
 * @since AutoCoach2.0
 */
public class StartAutoCoachActivity extends AppCompatActivity {
    public static StartAutoCoachActivity mainActivity;
    private static final String TAG = "StartAutoCoachActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LocationManager locationManager;

    //ui items
    private Button end_btn;
    private TextView display_uname;
    private TextView display_score;
    private TextView display_speed;
    private int speed = -1;
    //raspberry pi

    TextView gyro;
    private String new_rpi_input = "";
    double gyro_data = 0;

    TextView leftIndicator;
    TextView frontIndicator;
    TextView rightIndicator;


    boolean running;


    //trip info
    Operations dbOperations = new Operations();
    public int DBTripId;

    public int getDBTripId() {
        return DBTripId;
    }

    public Trip trip;
    public User user;

    public User getUser() {
        return user;
    }

    public FirebaseUser fbUser; //currentUser

    public FirebaseUser getFbUser() {
        return fbUser;
    }


    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Location Permission Granted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Location Permission Denied",
                            Toast.LENGTH_SHORT).show();
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

    public StartAutoCoachActivity() {
        mainActivity = this;
    }

    public static StartAutoCoachActivity getMainActivity() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_startcoach20);

        // Find all view elements
        display_uname = findViewById(R.id.display_name);
        display_score = findViewById(R.id.display_score);
        display_speed = findViewById(R.id.speednum);

        end_btn = findViewById(R.id.endBtn);
        //gyro module
        gyro = findViewById(R.id.gyrodata);

        leftIndicator = (TextView) findViewById(R.id.headpositiondebug_onleft);
        frontIndicator = (TextView) findViewById(R.id.headpositiondebug_onfront);
        rightIndicator = (TextView) findViewById(R.id.headpositiondebug_onright);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        user = new User(fbUser, 20, 0);

        long tripStartTime = System.currentTimeMillis();
        dbOperations.addToTableTrip(getApplicationContext(), fbUser.getUid(), 0, tripStartTime, 0, 0);
        trip = dbOperations.readCurrentTripDetails(this); //Read trip information
        DBTripId = trip.getTripId();
        updateUI();

        //android library
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Get available Location providers
        //Use GPS if possible. Otherwise use cellular network
        //Toast the user if neither is available
        List<String> providerList = locationManager.getProviders(true);
        String provider;
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
            Toast.makeText(this, "Got GPS as location provider",
                    Toast.LENGTH_SHORT).show();
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
            Toast.makeText(this, "Got NETWORK as location provider",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No location provider to use",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Use getLastKnownLocation can get the Location object with current location
        //And use showLocation() to show the location info
        //requestLocationUpdates is used for monitoring device location
        //The time interval of the manager is 5 sec. Distance is 5 meters (about 200 inches)
        //which means locationListener would update the location info every 5 sec or 5 meters
        //Toast.makeText(this, ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION),Toast.LENGTH_LONG).show();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Entered location check ...",
                    Toast.LENGTH_SHORT).show();

            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                Toast.makeText(this, "Current Location is " + location,
                        Toast.LENGTH_SHORT).show();
                int currentSpeed = updateSpeedByLocation(location);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                Toast.makeText(this, "Current Speed is " + currentSpeed,
                        Toast.LENGTH_SHORT).show();
                dbOperations.addToTableSpeedRecord(getApplicationContext(), getDBTripId(), currentSpeed, timestamp, new_rpi_input, gyro_data);
            }

            //Set the timer for 2 seconds to request location information
            locationManager.requestLocationUpdates(provider, 2000, 1, locationListener);
        } else {
            Toast.makeText(this, "Location permission not granted, asking ...",
                    Toast.LENGTH_SHORT).show();
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // ************************************************************************** //
        // CREATE USER AND ADD DATA TO GOOGLE FIREBASE STORE
        // ************************************************************************** //
        db.collection("user")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    /*
                    Save the User information to the local database
                    This code requires checking if ID is there, then don't store it anymore
                    But its okay for now
                     */
                        new Thread(() -> {
                            dbOperations.addToTableUser(documentReference, getApplicationContext(), fbUser);
                        }).start();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


        // ************************************************************************** //
        // END BUTTON ON CLICK
        // ************************************************************************** //
        end_btn.setMovementMethod(LinkMovementMethod.getInstance());
        end_btn.setOnClickListener(v -> {
            Log.d("END", "END BUTTON CLICKED");

            running = false; // This variable controls the Feedback Activity Threads while loop
            //Update end time for the trip
            long tripEndTime = System.currentTimeMillis();
            dbOperations.onClose(this);
            Operations op = new Operations();
            op.updateTripRecord(this, getDBTripId(), tripEndTime, fbUser.getUid(), trip.getTripScore());
            op.onClose(this);


            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        setTheme(R.style.Theme_AutoCoach20);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            //Needs permission to run getting location of the phone
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            initPermission(); //Zehua's code
        }

        return super.onCreateView(name, context, attrs);
    }


    // ************************************************************************** //
    // LOCATION MANAGER LISTENER
    // ************************************************************************** //
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        //Updates every 2 seconds
        @Override
        public void onLocationChanged(Location location) {
            // update speed by current location
            updateSpeedByLocation(location);
        }
    };

    //This calculates the speed -- no need to change it
    private int updateSpeedByLocation(Location location) {
        speed = (int) (location.getSpeed() * 2.23694); // m/s --> 3.6 for Km/h --> 2.23694 mph
        display_speed.setText(String.valueOf(speed));
        return speed;
    }

    public int getSpeed() {
        return speed;
    }


    //ON lower versions of Android permission is not required but for higher, we need to set permission
    @Override
    protected void onResume() {
        super.onResume();
        initPermission(); //For Android 6.0 or above //Initiate permission for we need file reading, writing, GPS, WiFI permission
    }

    //This function checks permission and asks for permission
    private void initPermission() {
        //Check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 1);
        }

    }


    //Request the sensor manager for your cellphone and after you have finished, you close them
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (locationManager != null) {
            //Remove the sensor listener after closing
            locationManager.removeUpdates(locationListener);
        }
    }


    public void onClickHeadBtn(View view) {
        PopUpHead popUpHead = new PopUpHead();
        popUpHead.showPopupWindow(view);
    }


    private void updateUI() {
        display_uname.setText(user.getUser_name());
        display_score.setText("100/100");
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.

    }


    public void gyroService(View view) {
        // TODO: Pop up window asking for ip and port
        final Handler handler = new Handler();

        Thread thread = new Thread((new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket("172.20.10.12", 80);
                    OutputStream out = s.getOutputStream();
                    PrintWriter output = new PrintWriter(out);
                    output.println("command");
                    output.flush();

                    while (true) {
                        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        final String st = input.readLine();
                        onUpdateGyro(st);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));

        thread.start();
    }

    private void onUpdateGyro(String g) {
        String[] arr = g.split(",", 2);
        try {
            String out = arr[0] + '\n' + arr[1];
            double angle = Double.parseDouble(arr[1]);
            gyro_data = angle;

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            dbOperations.addToTableSpeedRecord(getApplicationContext(), getDBTripId(), speed, timestamp, new_rpi_input, gyro_data);
            gyro.setText(out);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }
}
