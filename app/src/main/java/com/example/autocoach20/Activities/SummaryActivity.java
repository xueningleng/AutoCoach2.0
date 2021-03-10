package com.example.autocoach20.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autocoach20.R;

public class SummaryActivity extends AppCompatActivity {
    private static final String TAG = "SummaryActivity";

    TextView score;
    Button exit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String display_score = (String) getIntent().getStringExtra("SCORE");
        //Toast.makeText(this, display_score, Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_summary);
        score = findViewById(R.id.score);
        score.setText(display_score);
        exit = findViewById(R.id.returnBtn);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public void finish(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
