package com.example.autocoach20.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autocoach20.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class UserInfoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    // DBOperations mydb = new DBOperations();
    public final static String
            MESSAGE_KEY = "com.example.autocoach20.message_key";
    private static final String TAG = "UserInfoActivity";
    public FirebaseUser fbUser; //currentUser
    int userAge;
    int userGender;
    Operations dbOperations = new Operations();
    private TextView dateText;
    private Button submitBtn;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexBtn, radioFemale, radioMale;
    private DatePicker datePicker;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinput);
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        initializeUI();
    }

    private void initializeUI() {
        dateText = findViewById(R.id.date_text);

        findViewById(R.id.show_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int selectedId = radioSexGroup.getCheckedRadioButtonId();
//                radioSexBtn = (RadioButton) findViewById(selectedId);
//                switch(selectedId){
//                    case R.id.radio_female:
//                        userGender = 0;
//                        break;
//                    case R.id.radio_male:
//                        userGender = 1;
//                        break;
//                }

                userGender = 0;
                //sendInfo();
                Intent intent = new Intent(UserInfoActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePickerDialog.OnDateSetListener) this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - year;
        userAge = age;
        month++;
        String d = month + "/" + dayOfMonth + "/" + year;
        dateText.setText(d);
    }

    public void sendInfo() {

        //intent.putExtra(MESSAGE_KEY,userAge);
        //intent.putExtra(MESSAGE_KEY,userGender);
        db.collection("user")
                .add(fbUser)
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
                            dbOperations.updateUser(documentReference, getApplicationContext(), fbUser, userGender, userAge);
                            dbOperations.onClose(getApplicationContext());
                        }).start();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }


}
