package com.example.autocoach20.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.autocoach20.R;

public class MainActivity extends AppCompatActivity implements LaunchDialogFragment.LaunchDialogListener {
    public static MainActivity mainActivity;

    ImageButton sufolder;
    ImageButton sifolder;
    Button subtn;
    Button sibtn;
    TextView title;
    SignInActivity authActivity = new SignInActivity();


    public MainActivity() {
        mainActivity = this;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initializeUI();
    }

    public void initializeUI() {
        title = findViewById(R.id.projectTitle);
        sufolder = findViewById(R.id.signUpFolder);
        sifolder = findViewById(R.id.signInFolder);
        Button signIn = (Button) findViewById(R.id.buttonSignIn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(view);
            }
        });

        Button signUp = (Button) findViewById(R.id.buttonSignUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp(view);
            }
        });
        showAckDialog();
    }

    private void showAckDialog() {
        DialogFragment dialog = new LaunchDialogFragment();
        dialog.show(getSupportFragmentManager(), "LaunchDialogFragment");
    }

    public void signIn(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

}