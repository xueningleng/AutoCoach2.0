package com.example.autocoach20.Activities;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.autocoach20.R;

public class PopUpAlert {
    public int alert_type = 0; //valid 1-5
    TextView alert_msg;
    String out_msg;
    int alert_duration = 3000;//3s
    public PopUpAlert(int atype){
        alert_type = atype;
        switch(atype){
            case 1:
            case 2:
                out_msg = "LOOK FRONT!";
                break;
            case 3:
            case 4:
                out_msg = "SLOW DOWN!";
                break;
            case 5:
                out_msg = "CHECK SIDES!";
                break;
            default:
                out_msg = "DANGEROUS";
        }
    }
    public int getAlertType(){
        return alert_type;
    }
    public void showPopupAlert(final View view){
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_popupalert, null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        alert_msg = popupView.findViewById(R.id.alert_text);
        alert_msg.setText(out_msg);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                popupWindow.dismiss();
            }
        }, alert_duration);

    }
}
