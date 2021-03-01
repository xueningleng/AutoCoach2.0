package com.example.autocoach20.Activities;

import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autocoach20.R;

import java.util.concurrent.atomic.AtomicBoolean;

public class PopUpHead{
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

    Operations dbOperations = new Operations();
    enum Direction{
        NONE,
        LEFT,
        FRONT,
        RIGHT
    }

    public void showPopupWindow(final View view) {
        //****following section setting pop up window***
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_popupface, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //****End of section setting pop up window***

        scroll = (ScrollView) popupView.findViewById(R.id.headpositiondebug_scroll);
        terminal = (TextView) popupView.findViewById(R.id.headpositiondebug_terminal);

        leftIndicator = (TextView) popupView.findViewById(R.id.headpositiondebug_onleft);
        frontIndicator = (TextView) popupView.findViewById(R.id.headpositiondebug_onfront);
        rightIndicator = (TextView) popupView.findViewById(R.id.headpositiondebug_onright);

        hostInput = (EditText) popupView.findViewById(R.id.headpositiondebug_hostedit);
        portInput = (EditText) popupView.findViewById(R.id.headpositiondebug_portedit);
        leftCalibrateAngle = (TextView) popupView.findViewById(R.id.headpositiondebug_leftanglevalue);
        frontCalibrateAngle = (TextView) popupView.findViewById(R.id.headpositiondebug_frontanglevalue);
        rightCalibrateAngle = (TextView) popupView.findViewById(R.id.headpositiondebug_rightanglevalue);
        intervalInput = (EditText) popupView.findViewById(R.id.headpositiondebug_intervaledit);

        connectionIndicator = (TextView) popupView.findViewById(R.id.headpositiondebug_connstatus);
        connectButton = (Button) popupView.findViewById(R.id.headpositiondebug_connectbtn);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //As an example, display the message
                Toast.makeText(view.getContext(), "Wow, popup action button", Toast.LENGTH_SHORT).show();

            }
        });
        calibrateButton = (Button) popupView.findViewById(R.id.headpositiondebug_calibratebtn);
        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //As an example, display the message
                Toast.makeText(view.getContext(), "Wow, popup action button", Toast.LENGTH_SHORT).show();

            }
        });
        startButton = (Button) popupView.findViewById(R.id.headpositiondebug_startbtn);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                //As an example, display the message
                Toast.makeText(view.getContext(), "Wow, popup action button", Toast.LENGTH_SHORT).show();

            }
        });

        displayDirection(HeadPositionDebugActivity.Direction.NONE);

        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }

    void displayDirection(HeadPositionDebugActivity.Direction direction){
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
            //Toast.makeText(getContext(),"Please stop running before disconnecting", Toast.LENGTH_SHORT).show();
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
        displayDirection(HeadPositionDebugActivity.Direction.FRONT);
        //Toast.makeText(this,"Please look at the front", Toast.LENGTH_SHORT).show();
        SystemClock.sleep(delay);
        Float front = hpdh.sampleAndAverage();

        // Left
        displayDirection(HeadPositionDebugActivity.Direction.LEFT);
        //Toast.makeText(this,"Please look at the left", Toast.LENGTH_SHORT).show();
        SystemClock.sleep(delay);
        Float left = hpdh.sampleAndAverage();

        // Right
        displayDirection(HeadPositionDebugActivity.Direction.RIGHT);
        //Toast.makeText(this,"Please look at the right", Toast.LENGTH_SHORT).show();
        SystemClock.sleep(delay);
        Float right = hpdh.sampleAndAverage();

        displayDirection(HeadPositionDebugActivity.Direction.NONE);

        if(front==null || left==null || right==null)
            return false;

        leftCalibrationAngle=left;
        frontCalibrationAngle=front;
        rightCalibrationAngle=right;
        return true;
    }


    public void btnCalibrateHandler(View view) {
        if(!connected){
            //Toast.makeText(this, "Please connect to the device first", Toast.LENGTH_SHORT).show();
            return;
        }

        if(running){
            //Toast.makeText(this, "Please stop running before calibration", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = tryCalibrate();

        if(success){
            //Toast.makeText(this,"Calibration Success",Toast.LENGTH_SHORT).show();
            calibrated=true;
            leftCalibrateAngle.setText(Float.toString(leftCalibrationAngle));
            frontCalibrateAngle.setText(Float.toString(frontCalibrationAngle));
            rightCalibrateAngle.setText(Float.toString(rightCalibrationAngle));
            hpdh.setRegularizationParam(frontCalibrationAngle, leftCalibrationAngle, rightCalibrationAngle);
        }else{
            //Toast.makeText(this,"Calibration Failed",Toast.LENGTH_SHORT).show();
        }
    }

//    boolean run(){
//        stopSignal.set(false);
//
//        final Handler handler= new Handler();
//        runner = new Thread(() -> {
//            threadStopped.set(false);
//
//            while (!stopSignal.get()){
//                Float angle = hpdh.getLastRegularizedAngle(3);
//
//                if(angle==null)
//                    continue;
//
//                HeadPositionDebugActivity.Direction dir;
//                if(angle<-70.0f)
//                    // Left
//                    dir= HeadPositionDebugActivity.Direction.LEFT;
//                else if(angle>70.0f)
//                    // Right
//                    dir= HeadPositionDebugActivity.Direction.RIGHT;
//                else
//                    // Front
//                    dir= HeadPositionDebugActivity.Direction.FRONT;
//
//
//                handler.post(()->{
//                    // Update Terminal
//                    CharSequence oldText = terminal.getText();
//                    CharSequence newText = oldText+"\nRegularized Angle: "+Float.toString(angle);
//                    terminal.setText(newText);
//
//                    scroll.fullScroll(View.FOCUS_DOWN);
//
//                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//                    dbOperations.addToTableSpeedRecord(getApplicationContext(), 65535, 0, timestamp, Float.toString(angle), 0.0);
//
//
//                    // Update flag
//                    displayDirection(dir);
//                });
//            }
//
//            threadStopped.set(true);
//        });
//
//        runner.start();
//        return true;
//    }
//
//    boolean stop(){
//        stopSignal.set(true);
//        while(!threadStopped.get())
//            SystemClock.sleep(100);
//
//        return true;
//
//    }

//    public void btnStartHandler(View view) {
//        if(!connected){
//            //Toast.makeText(this, "Please connect to the device first", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if(!calibrated){
//            //Toast.makeText(this, "Please calibrate first before using", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if(running){
//            boolean result = stop();
//            if(result){
//                startButton.setText("Start");
//                running=false;
//                displayDirection(HeadPositionDebugActivity.Direction.NONE);
//                intervalInput.setEnabled(true);
//            }
//        }else{
//            boolean result=run();
//            if(result){
//                startButton.setText("Stop");
//                running=true;
//                intervalInput.setEnabled(false);
//            }
//        }
//
//    }

}
