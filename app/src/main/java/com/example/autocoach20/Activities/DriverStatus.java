package com.example.autocoach20.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autocoach20.R;

public class DriverStatus extends AppCompatActivity {
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverstatus);

        Button statusGood = (Button) findViewById(R.id.buttonGood);
        statusGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView t = (TextView) findViewById(R.id.textView);
                t.setText("GOOD");
            }
        });
        Button statusBad = (Button) findViewById(R.id.buttonBad);
        statusBad.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 TextView t = (TextView) findViewById(R.id.textView);
                 t.setText("BAD");
             }

        });
    }
    public void setUpUser(){

    }

}
