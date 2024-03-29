package com.example.autocoach20.Activities;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
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
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.autocoach20.Activities.Model.SpeedRecord;
import com.example.autocoach20.Activities.Model.Trip;
import com.example.autocoach20.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Xuening Leng, Yuehan Cui
 * @since AutoCoach2.0
 */
public class StartAutoCoachActivity extends AppCompatActivity {
    private static final String TAG = "StartAutoCoachActivity";
    private static final int DATA_QUEUE_SIZE = 10;
    private static final float HEAD_DIR_THRESHOLD = 66.6f;
    private static final int UPDATE_INTERVAL_MS = 250;

    // Handler
    final Handler handler = new Handler();

    //
    public StartAutoCoachActivity mainActivity;
    public int DBTripId;
    public Trip trip;
    public User user;
    public FirebaseUser fbUser; //currentUser

    //raspberry pi
    TextView leftIndicator;
    TextView frontIndicator;
    TextView rightIndicator;
    TextView gyroIndicator;

    //trip info
    Operations dbOperations = new Operations();

    // Data Hubs
    HeadPositionDataHub headPositionDataHub = new HeadPositionDataHub(DATA_QUEUE_SIZE, HEAD_DIR_THRESHOLD);
    GyroDataHub gyroDataHub = new GyroDataHub(DATA_QUEUE_SIZE);

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LocationManager locationManager;

    //ui items
    private Button end_btn, warn_btn;
    private TextView display_uname;
    private TextView display_score;
    private TextView display_speed;

    // Data to be send to DB
    private int speed = -1;
    private float acc = 0;
    private double gyro_data = 0.0;
    private int headPosition = 0;

    // data for realtime feedback
    private long headStart = 0;
    private long headCountStart = 0;
    private int headCount = 0;
    private int last_speed;
    private long last_time = 0;
    private long lastTurnTime = 0;
    private SpeedRecord sr = new SpeedRecord();
    private int score = 1000;
    boolean turning=false;

    // Worker thread
    Thread t;

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


    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                }
            });

    public StartAutoCoachActivity() {
        mainActivity = this;
    }


    public int getDBTripId() {
        return DBTripId;
    }

    public User getUser() {
        return user;
    }

    public FirebaseUser getFbUser() {
        return fbUser;
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
        warn_btn = findViewById(R.id.warnBtn);
        warn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangerAlert(v, 2);
            }
        });
        //gyro module
        gyroIndicator = findViewById(R.id.gyrodata);

        leftIndicator = (TextView) findViewById(R.id.headpositiondebug_onleft);
        frontIndicator = (TextView) findViewById(R.id.headpositiondebug_onfront);
        rightIndicator = (TextView) findViewById(R.id.headpositiondebug_onright);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        user = new User(fbUser, 20, 0);

        long tripStartTime = System.currentTimeMillis();
        dbOperations.addToTableTrip(getApplicationContext(), fbUser.getUid(), 0, tripStartTime, 0, 0);
        trip = dbOperations.readCurrentTripDetails(this); //Read trip information
        DBTripId = trip.getTripId();
        display_uname.setText(user.getUser_name());
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
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {

                int currentSpeed = updateSpeedByLocation(location);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                dbOperations.addToTableSpeedRecord(getApplicationContext(), getDBTripId(), currentSpeed, timestamp, headPosition, gyro_data);
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

            //Update end time for the trip
            long tripEndTime = System.currentTimeMillis();
            dbOperations.onClose(this);
            Operations op = new Operations();
            op.updateTripRecord(this, getDBTripId(), tripEndTime, fbUser.getUid(), trip.getTripScore());
            op.onClose(this);

            // Export DB
            dbOperations.exportDB();

            Intent intent = new Intent(this, SummaryActivity.class);
            intent.putExtra("SCORE", score + "/1000");
            startActivity(intent);
        });

        // Start thread to write data to db
        t = new Thread(() -> {
            while (true) {
                // Collect speed, gyro_data, and headPosition
                updateDirection();
                updateGyro();


                // Write to DB
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                dbOperations.addToTableSpeedRecord(
                        getApplicationContext(),
                        getDBTripId(),
                        speed,
                        timestamp,
                        headPosition,
                        gyro_data);

                // Sleep
                try {
                    Thread.sleep(UPDATE_INTERVAL_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
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

    //This calculates the speed -- no need to change it
    private int updateSpeedByLocation(Location location) {
        if (speed != -1) last_speed = speed;
        else last_speed = 0;


        speed = (int) (location.getSpeed() * 2.23694); // m/s --> 3.6 for Km/h --> 2.23694 mph

        updateAccBySpeed(speed);

        last_time = System.currentTimeMillis();

        display_speed.setText(Integer.toString(speed));

        return speed;
    }

    private float updateAccBySpeed(int cur_speed) {
        //retrieve last stored speed and calculate acceleration
        //sr = dbOperations.lastSpeedRecord(getApplicationContext());
        //if (sr == null) return acc;
        if ((System.currentTimeMillis() - last_time) != 0)
            acc = (cur_speed - last_speed) / (System.currentTimeMillis() - last_time);

        return acc;
    }

    private void updateDirection() {
        HeadPositionDataHub.Direction dir = headPositionDataHub.getLastHeadDirection();

        handler.post(() -> {
            leftIndicator.setVisibility(View.INVISIBLE);
            frontIndicator.setVisibility(View.INVISIBLE);
            rightIndicator.setVisibility(View.INVISIBLE);
        });

        TextView viewToUpdate = null;
        switch (dir) {
            case NONE:
                headPosition = 0;
                break;

            case FRONT:
                viewToUpdate = frontIndicator;
                headPosition = 1;
                if (headStart != 0) {
                    //head turned back
                    long timepassed = System.currentTimeMillis() - headStart;

                    if (timepassed >= 3000) {
                        handler.post(() -> {
                            Toast.makeText(this, "Time without looking front is " + timepassed / 1000,
                                    Toast.LENGTH_SHORT).show();
                        });
                        View v = new View(this);
                        dangerAlert(v, 1);

                    }
                    headStart = 0;

                }
                break;

            case LEFT:
                viewToUpdate = leftIndicator;
                headPosition = 2;
                lastTurnTime = System.currentTimeMillis();
                if (headStart == 0) {
                    headStart = System.currentTimeMillis();

                    headCount++;
                    if (headCountStart == 0)
                        headCountStart = System.currentTimeMillis();
                }
                break;

            case RIGHT:
                viewToUpdate = rightIndicator;
                headPosition = 3;
                lastTurnTime = System.currentTimeMillis();
                if (headStart == 0) {
                    headStart = System.currentTimeMillis();

                    headCount++;
                    if (headCountStart == 0)
                        headCountStart = System.currentTimeMillis();
                }
                break;
        }
        if (headCount >= 3) {// dangerous operation #2
            long timepassed = System.currentTimeMillis() - headCountStart;
            if (timepassed <= 10000) {//10seconds
                View v = new View(this);
                dangerAlert(v, 2);
                handler.post(() -> {
                    Toast.makeText(this, "Don't looking around for more than 3 times within 10 seconds.",
                            Toast.LENGTH_SHORT).show();
                });
            }
            headCount = 0;
            headCountStart = 0;
        }

        final TextView v = viewToUpdate;
        if (v != null)
            handler.post(() -> {
                v.setVisibility(View.VISIBLE);
            });
    }

    private void updateGyro() {
        Double gyroData = gyroDataHub.getLastValue();

        if (gyroData == null)
            gyro_data = 0.0;
        else
            gyro_data = gyroData;

        double absGyroData = Math.abs(gyro_data);

        if(absGyroData<45)
            turning=false;

        if (absGyroData > 45) {//lane change, turn, etc
            if(!turning) {
                turning=true;
                long timepassed = System.currentTimeMillis() - lastTurnTime;
                if (timepassed > 3000) {// dangerous operation #5
                    View v = new View(this);
                    dangerAlert(v, 5);
                    handler.post(()->{
                        Toast.makeText(this, "Please look to the side before turning" ,
                                Toast.LENGTH_SHORT).show();
                    });

                }
            }
        }
        if (absGyroData > 90) { // dangerous operation #3
            if (speed > 20) {
                View v = new View(this);
                dangerAlert(v, 3);
                handler.post(() -> {
                    Toast.makeText(this, "Reduce speed below 20 when turn > 45 degree",
                            Toast.LENGTH_SHORT).show();
                });
            }

        }
        if (absGyroData > 130) { // dangerous operation #4
            if (speed > 10) {
                View v = new View(this);
                dangerAlert(v, 4);

                handler.post(() -> {
                    Toast.makeText(this, "Reduce speed below 10 when turn > 90 degree",
                            Toast.LENGTH_SHORT).show();
                });

            }
        }
        String outValue = String.format("%.2f", gyro_data);
        handler.post(() -> {
            gyroIndicator.setText(outValue);
        });
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
        popUpHead.showPopupWindow(view, headPositionDataHub, gyroDataHub);
    }


    private void updateUI() {
        handler.post(() -> {
            display_score.setText(score + "/1000");
        });
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.

    }

    public void dangerAlert(View v, int type) {
        Log.d("Danger Alert", "Type:" + type);

        handler.post(() -> {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            RingtoneManager.getRingtone(getApplicationContext(), alarmSound).play();

//            NotificationCompat.Builder mBuilder =
//                    new NotificationCompat.Builder(StartAutoCoachActivity.this, "dangerAlert")
//                            .setSmallIcon(R.drawable.ic_launcher_foreground)
//                            .setContentTitle("Danger")
//                            .setContentText("Drive Careful");
//            NotificationManager mNotificationManager = (NotificationManager)
//                    getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotificationManager.notify((int) System.currentTimeMillis(),
//                    mBuilder.build());


            PopUpAlert alert = new PopUpAlert(type);
            alert.showPopupAlert(v);
        });

//        (new Handler()).postDelayed(new Runnable() {
//            public void run() {
//                mp.stop();
//            }
//        }, 5000);
        switch (type) {
            case 1:
                score -= 20;
                updateUI();
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                score -= 10;
                updateUI();
                break;
            default:
        }
    }

}
