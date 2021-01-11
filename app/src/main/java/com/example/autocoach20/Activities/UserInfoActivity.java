package com.example.autocoach20.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autocoach20.R;

import java.util.Calendar;

public class UserInfoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    int userAge;
    int userGender;
    private TextView dateText;
    private Button submitBtn;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexBtn, radioFemale, radioMale;
    private DatePicker datePicker;
    public final static String
            MESSAGE_KEY ="com.example.autocoach20.message_key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinput);
        Intent intent = getIntent();
        initializeUI();

    }
    private void initializeUI(){
        dateText = findViewById(R.id.date_text);
        findViewById(R.id.show_date).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showDatePickerDialog();
            }
        });
        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int selectedId = radioSexGroup.getCheckedRadioButtonId();
                radioSexBtn = (RadioButton) findViewById(selectedId);
                switch(selectedId){
                    case R.id.radio_female:
                        userGender = 0;
                        break;
                    case R.id.radio_male:
                        userGender = 1;
                        break;
                }
                sendInfo();

            }
        });
    }
    private void showDatePickerDialog(){
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
        String d = month + "/"+dayOfMonth+"/"+year+" (mm/dd/yyyy)";
        dateText.setText(d);
    }
    public void sendInfo(){
        Intent intent = new Intent(UserInfoActivity.this, DriverStatus.class);
        intent.putExtra(MESSAGE_KEY,userAge);
        intent.putExtra(MESSAGE_KEY,userGender);
        startActivity(intent);
    }
}
