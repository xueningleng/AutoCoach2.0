package com.example.autocoach20.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autocoach20.R;

import java.util.concurrent.atomic.AtomicBoolean;

public class HeadPositionDebugActivity extends AppCompatActivity {

    ScrollView scroll;
    TextView terminal;

    TextView leftIndicator;
    TextView frontIndicator;
    TextView rightIndicator;

    EditText hostInput;
    EditText portInput;
    TextView leftCalibrateAngle;
    TextView frontCalibrateAngle;
    TextView rightCalibrateAngle;
    EditText intervalInput;

    TextView connectionIndicator;
    Button connectButton;
    Button calibrateButton;
    Button startButton;

    boolean connected;
    boolean calibrated;

    float leftCalibrationAngle;
    float frontCalibrationAngle;
    float rightCalibrationAngle;

    boolean running;

    HeadPositionDataHub hpdh;

    Thread runner;
    final AtomicBoolean stopSignal = new AtomicBoolean(false);
    final AtomicBoolean threadStopped = new AtomicBoolean(false);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_position_debug);

        scroll = (ScrollView) findViewById(R.id.headpositiondebug_scroll);
        terminal = (TextView) findViewById(R.id.headpositiondebug_terminal);

        leftIndicator = (TextView) findViewById(R.id.headpositiondebug_onleft);
        frontIndicator = (TextView) findViewById(R.id.headpositiondebug_onfront);
        rightIndicator = (TextView) findViewById(R.id.headpositiondebug_onright);

        hostInput = (EditText) findViewById(R.id.headpositiondebug_hostedit);
        portInput = (EditText) findViewById(R.id.headpositiondebug_portedit);
        leftCalibrateAngle = (TextView) findViewById(R.id.headpositiondebug_leftanglevalue);
        frontCalibrateAngle = (TextView) findViewById(R.id.headpositiondebug_frontanglevalue);
        rightCalibrateAngle = (TextView) findViewById(R.id.headpositiondebug_rightanglevalue);
        intervalInput = (EditText) findViewById(R.id.headpositiondebug_intervaledit);

        connectionIndicator = (TextView) findViewById(R.id.headpositiondebug_connstatus);
        connectButton = (Button) findViewById(R.id.headpositiondebug_connectbtn);
        calibrateButton = (Button) findViewById(R.id.headpositiondebug_calibratebtn);
        startButton = (Button) findViewById(R.id.headpositiondebug_startbtn);

        displayDirection(Direction.NONE);
    }

    enum Direction{
        NONE,
        LEFT,
        FRONT,
        RIGHT
    }

    void displayDirection(Direction direction){
        leftIndicator.setVisibility(View.INVISIBLE);
        frontIndicator.setVisibility(View.INVISIBLE);
        rightIndicator.setVisibility(View.INVISIBLE);

        switch(direction){
            case NONE:
                break;
            case LEFT:
                leftIndicator.setVisibility(View.VISIBLE);
                break;
            case FRONT:
                frontIndicator.setVisibility(View.VISIBLE);
                break;
            case RIGHT:
                rightIndicator.setVisibility(View.VISIBLE);
                break;
        }
    }


    void connect(){
        String host = hostInput.getText().toString();
        int port;
        try{
            port=Integer.parseInt(portInput.getText().toString());
        }catch (Exception e){
            return;
        }

        hpdh = new HeadPositionDataHub(host, port);
        connected=true;
        connectionIndicator.setText("Connected");
        connectButton.setText("Disconnect");
        hostInput.setEnabled(false);
        portInput.setEnabled(false);

        if(calibrated)
            hpdh.setRegularizationParam(frontCalibrationAngle, leftCalibrationAngle,rightCalibrationAngle);
    }

    void disconnect(){
    hpdh=null;
    connected=false;
    connectionIndicator.setText("Disconnected");
    connectButton.setText("Connect");
    hostInput.setEnabled(true);
    portInput.setEnabled(true);
    }

    public void btnConnectHandler(View view) {
        if(running){
            Toast.makeText(this,"Please stop running before disconnecting", Toast.LENGTH_SHORT).show();
            return;
        }

        if(connected)
            disconnect();
        else
            connect();
    }

    boolean tryCalibrate(){
        final int delay=3000;

        // Front
        displayDirection(Direction.FRONT);
        Toast.makeText(this,"Please look at the front", Toast.LENGTH_SHORT).show();
        SystemClock.sleep(delay);
        Float front = hpdh.sampleAndAverage();

        // Left
        displayDirection(Direction.LEFT);
        Toast.makeText(this,"Please look at the left", Toast.LENGTH_SHORT).show();
        SystemClock.sleep(delay);
        Float left = hpdh.sampleAndAverage();

        // Right
        displayDirection(Direction.RIGHT);
        Toast.makeText(this,"Please look at the right", Toast.LENGTH_SHORT).show();
        SystemClock.sleep(delay);
        Float right = hpdh.sampleAndAverage();

        displayDirection(Direction.NONE);

        if(front==null || left==null || right==null)
            return false;

        leftCalibrationAngle=left;
        frontCalibrationAngle=front;
        rightCalibrationAngle=right;
        return true;
    }


    public void btnCalibrateHandler(View view) {
        if(!connected){
            Toast.makeText(this, "Please connect to the device first", Toast.LENGTH_SHORT).show();
            return;
        }

        if(running){
            Toast.makeText(this, "Please stop running before calibration", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = tryCalibrate();

        if(success){
            Toast.makeText(this,"Calibration Success",Toast.LENGTH_SHORT).show();
            calibrated=true;
            leftCalibrateAngle.setText(Float.toString(leftCalibrationAngle));
            frontCalibrateAngle.setText(Float.toString(frontCalibrationAngle));
            rightCalibrateAngle.setText(Float.toString(rightCalibrationAngle));
            hpdh.setRegularizationParam(frontCalibrationAngle, leftCalibrationAngle, rightCalibrationAngle);
        }else{
            Toast.makeText(this,"Calibration Failed",Toast.LENGTH_SHORT).show();
        }
    }

    boolean run(){
        stopSignal.set(false);

        final Handler handler= new Handler();
        runner = new Thread(() -> {
            threadStopped.set(false);

            while (!stopSignal.get()){
                Float angle = hpdh.getLastRegularizedAngle(3);

                if(angle==null)
                    continue;

                Direction dir;
                if(angle<-70.0f)
                    // Left
                    dir=Direction.LEFT;
                else if(angle>70.0f)
                    // Right
                    dir=Direction.RIGHT;
                else
                    // Front
                    dir=Direction.FRONT;


                handler.post(()->{
                    // Update Terminal
                    CharSequence oldText = terminal.getText();
                    CharSequence newText = oldText+"\nRegularized Angle: "+Float.toString(angle);
                    terminal.setText(newText);

                    scroll.fullScroll(View.FOCUS_DOWN);


                    // Update flag
                    displayDirection(dir);
                });
            }

            threadStopped.set(true);
        });

        runner.start();
        return true;
    }

    boolean stop(){
        stopSignal.set(true);
        while(!threadStopped.get())
            SystemClock.sleep(100);

        return true;

    }

    public void btnStartHandler(View view) {
        if(!connected){
            Toast.makeText(this, "Please connect to the device first", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!calibrated){
            Toast.makeText(this, "Please calibrate first before using", Toast.LENGTH_SHORT).show();
            return;
        }

        if(running){
            boolean result = stop();
            if(result){
                startButton.setText("Start");
                running=false;
                displayDirection(Direction.NONE);
                intervalInput.setEnabled(true);
            }
        }else{
            boolean result=run();
            if(result){
                startButton.setText("Stop");
                running=true;
                intervalInput.setEnabled(false);
            }
        }

    }
}