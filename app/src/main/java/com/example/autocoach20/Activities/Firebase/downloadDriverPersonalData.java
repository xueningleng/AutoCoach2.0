package com.example.autocoach20.Activities.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class downloadDriverPersonalData {

    //Get Single Record:
    //https://stackoverflow.com/questions/51579458/get-single-record-from-firestore-using-vue-js
    private static final String TAG = "downloadDriverPersonalData";

    public Map<String,Object> readPersonalData (FirebaseFirestore db, String userId){

        Map<String,Object> userInfo = new HashMap<String, Object>();

        db.collection("personalities").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        System.out.println("WE ARE HERE");
                        if (task.isSuccessful()) {

                            userInfo.put("acc_score", task.getResult().get("acce_score"));
                            userInfo.put("brake_score", task.getResult().get("brake_score"));
                            userInfo.put("turn_score", task.getResult().get("turn_score"));
                            userInfo.put("swerve_score", task.getResult().get("swerve_score"));
                            userInfo.put("overall_score", task.getResult().get("overall_score"));

                            Log.d(TAG, task.getResult().getId());
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                        System.out.println("HERE 5");

                    }
                });

        return userInfo;
    }

}
