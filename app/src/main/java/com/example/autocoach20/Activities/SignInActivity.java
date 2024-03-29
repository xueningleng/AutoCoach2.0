package com.example.autocoach20.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autocoach20.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    EditText userEmail, userPassword;
    TextView pwordHint;
    Button signInBtn, homeBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        initializeUI();
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initializeUI() {
        userEmail = (EditText) findViewById(R.id.uEmail);
        userPassword = (EditText) findViewById(R.id.uPword);
        signInBtn = findViewById(R.id.signInButton);
        pwordHint = findViewById(R.id.result);
        homeBtn = findViewById(R.id.returnButton);
    }

    private void signIn() {
        String email, password;
        email = userEmail.getText().toString();
        password = userPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pwordHint.setText("Login Successful!");
                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignInActivity.this, StartAutoCoachActivity.class);
                            startActivity(intent);
                        } else {
                            pwordHint.setText("Login Failed! Please try again or return to the main page.");
                            Toast.makeText(getApplicationContext(), "Login failed! Please try again later", Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }

}

