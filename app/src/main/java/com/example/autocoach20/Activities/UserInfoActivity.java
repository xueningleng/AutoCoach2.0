package com.example.autocoach20.Activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autocoach20.R;

import java.util.Calendar;

public class UserInfoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private int userAge;
    EditText userGender;
    private TextView dateText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinput);
        dateText= findViewById(R.id.date_text);
        findViewById(R.id.show_date).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

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
}
