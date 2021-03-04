package com.example.autocoach20.Activities;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autocoach20.R;

import java.util.concurrent.atomic.AtomicBoolean;


public class PopUpHead {
    final AtomicBoolean stopSignal = new AtomicBoolean(false);
    final AtomicBoolean threadStopped = new AtomicBoolean(false);


    EditText cameraHostInput;
    EditText cameraPortInput;
    TextView leftCalibrateAngle;
    TextView frontCalibrateAngle;
    TextView rightCalibrateAngle;

    EditText gyroHostInput;
    EditText gyroPortInput;

    Button cameraConnectButton;
    Button calibrateButton;
    Button GyroConnectButton;


    HeadPositionDataHub headPositionDataHub;
    GyroDataHub gyroDataHub;


    public void showPopupWindow(final View view, HeadPositionDataHub headPositionDataHub, GyroDataHub gyroDataHub) {
        this.headPositionDataHub = headPositionDataHub;
        this.gyroDataHub = gyroDataHub;

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


        cameraHostInput = (EditText) popupView.findViewById(R.id.headpositiondebug_camerahostedit);
        cameraPortInput = (EditText) popupView.findViewById(R.id.headpositiondebug_cameraportedit);
        leftCalibrateAngle = (TextView) popupView.findViewById(R.id.headpositiondebug_leftanglevalue);
        frontCalibrateAngle = (TextView) popupView.findViewById(R.id.headpositiondebug_frontanglevalue);
        rightCalibrateAngle = (TextView) popupView.findViewById(R.id.headpositiondebug_rightanglevalue);

        gyroHostInput = (EditText) popupView.findViewById(R.id.headpositiondebug_gyrohostedit);
        gyroPortInput = (EditText) popupView.findViewById(R.id.headpositiondebug_gyroportedit);


        cameraConnectButton = (Button) popupView.findViewById(R.id.headpositiondebug_cameraconnectbtn);
        cameraConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = cameraHostInput.getText().toString();
                int port = Integer.parseInt(cameraPortInput.getText().toString());

                headPositionDataHub.run(host, port);


                Toast.makeText(view.getContext(), "Connected to Camera", Toast.LENGTH_SHORT).show();

            }
        });
        calibrateButton = (Button) popupView.findViewById(R.id.headpositiondebug_calibratebtn);
        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if connected to server
                if (!headPositionDataHub.isConnected()) {
                    Toast.makeText(view.getContext(), "Please connect to server first", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String[] directions = {"Front", "Left", "Right"};
                final String prompt = "Please look at %s";
                final float[] angles = new float[3];
                final TextView[] views = {frontCalibrateAngle, leftCalibrateAngle, rightCalibrateAngle};

                // Handler to toast message
                Handler handle = new Handler(Looper.getMainLooper());

                // Do Calibration
                Thread t = new Thread(() -> {
                    // Do calibration for all three directions
                    for (int i = 0; i < 3; i++) {
                        final String direction = directions[i];
                        final TextView currentView = views[i];


                        // Prompt user to look at direction
                        handle.post(() -> {
                            Toast.makeText(v.getContext(), String.format(prompt, direction), Toast.LENGTH_SHORT).show();
                        });

                        // Delay
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // clear queue
                        headPositionDataHub.clearQueue();

                        // get average
                        boolean full = headPositionDataHub.queueIsFull();
                        while (!full) {
                            // Display most recent value
                            Float val = headPositionDataHub.getLastValue();

                            if (val != null)
                                handle.post(() -> {
                                    currentView.setText(Float.toString(val));
                                });


                            // Delay
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            full = headPositionDataHub.queueIsFull();
                        }

                        Float angle = headPositionDataHub.getAveragedData();

                        if (angle == null) {
                            // Calibration failed
                            handle.post(() -> {
                                Toast.makeText(v.getContext(), "Failed to calibrate because no enough data point", Toast.LENGTH_SHORT).show();
                            });
                            return;
                        }

                        angles[i] = angle;
                        handle.post(() -> {
                            currentView.setText(Float.toString(angle));
                        });
                    }

                    // Assert left < front < right
                    if (angles[0] < angles[1] || angles[2] < angles[0]) {
                        // Calibration failed
                        handle.post(() -> {
                            Toast.makeText(v.getContext(), "Failed to calibrate because angles are not in correct order", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }

                    // Set regularization parameter
                    headPositionDataHub.setRegularizationParam(angles[0], angles[1], angles[2]);

                    // Prompt user operation was successful
                    handle.post(() -> {
                        Toast.makeText(v.getContext(), "Calibration success", Toast.LENGTH_SHORT).show();
                    });


                });

                t.start();
            }
        });


        GyroConnectButton = (Button) popupView.findViewById(R.id.headpositiondebug_gyroconnectbtn);
        GyroConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String host = gyroHostInput.getText().toString();
                int port = Integer.parseInt(gyroPortInput.getText().toString());

                gyroDataHub.run(host, port);

                Toast.makeText(view.getContext(), "Connected to Gyro", Toast.LENGTH_SHORT).show();
            }
        });


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


}
