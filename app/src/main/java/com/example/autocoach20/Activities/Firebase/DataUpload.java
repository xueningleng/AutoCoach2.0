package com.example.autocoach20.Activities.Firebase;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.autocoach20.Activities.Databases.SensorDatabase.SensorDbHelper;
import com.example.autocoach20.Activities.Model.Event;
import com.example.autocoach20.Activities.Model.Recommendation;
import com.example.autocoach20.Activities.Model.Trip;
import com.example.autocoach20.Activities.Model.Window;
import com.example.autocoach20.Activities.Operations;
import com.example.autocoach20.Activities.StartAutoCoachActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class does the work for uploading data to Google Firebase
 * @author Zahraa Marafie
 * @version 2.0
 */
public class DataUpload {

    // ****************************************************************** //
    // FIELDS
    // ****************************************************************** //

    private static final String TAG = "DataUpload";
    FirebaseUser user = StartAutoCoachActivity.getMainActivity().getFbUser();
    //TODO: Can we have userId stored on a file locally?

    // ****************************************************************** //
    // PUBLIC METHODS
    // ****************************************************************** //

    /**
     * @since Version 1.0
     * @param context
     * @param db
     */
    public void uploadRecordedEvents (Context context, FirebaseFirestore db, int rowCount) {
        Operations op = new Operations();
        //List<Event> eventsList = op.fetchTripEvents(context); //Fetch list of events

        while (op.fetchEvents(context, rowCount) != null) {
            List<Event> eventsList = op.fetchEvents(context, rowCount); //Fetch list of events
            List<Map<String, Object>> events;
            events = convertListToHashMap(eventsList); //Convert to list of HashMap

            Log.d("DataUpload", "Inside uploadRecordedEvents");

            //LOOP TO ADD INDIVIDUAL EVENTS
            for (Map<String, Object> event : events) {
                final String startTimeField = "startTime";
                long startTime = (long) event.get(startTimeField);
                Log.w("StartTime", "firebase hashmap "+startTime);
                event.put(startTimeField, new Timestamp(new Date(startTime)));
                // Add a new document with a generated ID
                db.collection("events")
                        .add(event)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "Event document added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding event document", e);
                            }
                        });
                event.put(startTimeField, startTime);
            }

            op.bulkEventsUpdate(context, events);

            showToast(context, "Events data is being uploaded ...");
        }
    }


    /**
     * @since Version 1.0
     * @param context
     * @param db
     */
    public void uploadRecordedRecommendations (Context context, FirebaseFirestore db, int rowCount) {
        Operations op = new Operations();
        //List<Recommendation> recommendationList = op.fetchTripRecommendations(context); //Fetch list of events

        while(op.fetchRecommendations(context, rowCount) != null) {
            List<Recommendation> recommendationList = op.fetchRecommendations(context, rowCount); //Fetch list of events
            List<Map<String, Object>> recommendations;
            recommendations = convertRecommendationListToHashMap(recommendationList); //Convert to list of HashMap

            Log.d("DataUpload", "Inside uploadRecordedRecommendations");

            //LOOP TO ADD INDIVIDUAL Recommendations
            for (Map<String, Object> recommendation : recommendations) {
                db.collection("recommendations")
                        .add(recommendation)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }

            op.bulkRecommendationsUpdate(context, recommendations);

            showToast(context, "Recommendation data is being uploaded ...");
        }
    }


    /**
     * @since Version 1.0
     * @param context
     * @param db
     */
    public void uploadRecordedWindows (Context context, FirebaseFirestore db, int rowCount) {
        Operations op = new Operations();
        //List<Window> windowList = op.fetchTripWindows(context); //Fetch list of events

        while (op.fetchWindows(context, rowCount) != null) {
            List<Window> windowList = op.fetchWindows(context, rowCount); //Fetch list of events
            List<Map<String, Object>> windows;
            windows = convertWindowListToHashMap(windowList); //Convert to list of HashMap

            Log.d("DataUpload", "Inside uploadRecordedWindows");

            //LOOP TO ADD INDIVIDUAL EVENTS
            for (Map<String, Object> window : windows) {
                // Add a new document with a generated ID
                db.collection("windows")
                        .add(window)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }

            op.bulkWindowsUpdate(context, windows);

            showToast(context, "Windows data is being uploaded ...");
        }

    }


    /**
     * @since Version 1.0
     * @param context
     * @param db
     */
    public void uploadRecordedTrip (Context context, FirebaseFirestore db) {
        Operations op = new Operations();
        Trip tripInfo = op.readCurrentTripDetails(context); //Fetch list of events
        Map<String, Object> trip;
        trip = convertTripToHashMap(tripInfo); //Convert to list of HashMap

        Log.d("DataUpload", "Inside uploadRecordedWindows");

        // Add a new document with a generated ID
        db.collection("trips")
                .add(trip)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        showToast(context, "Trip data is being uploaded ...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        op.updateSyncedTripRecord(context, tripInfo.getTripId(), tripInfo.getTripEndTime(),
                tripInfo.getTripUserId(), tripInfo.getTripScore());
    }


    // ****************************************************************** //
    // UPLOADING DATA FROM SENSOR DATABASE
    // ****************************************************************** //

    /**
     * @since Version 2.0
     * @param context
     * @param db
     */
    public void uploadSensorDB (Context context, FirebaseFirestore db) {
        SensorDbHelper sensorDbHelper = new SensorDbHelper(context);
        List<Map<String, Object>> allSensorData = sensorDbHelper.fetchAllSensorData(context);

        Log.d("DataUpload", "Inside uploadSensorDB");

        //LOOP TO ADD INDIVIDUAL EVENTS
        for (Map<String, Object> data : allSensorData) {
            // Add a new document with a generated ID
            db.collection("allSensorDbData")
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }
        showToast(context, "Sensor data is being uploaded ...");

    }


    // ****************************************************************** //
    // PRIVATE METHODS
    // ****************************************************************** //

    private List<Map<String,Object>> convertListToHashMap (List<Event> eventsList){

        List<Map<String,Object>> events = new ArrayList<Map<String,Object>>();
        Log.d("DataUpload", "Inside convertListToHashMap");
        // Create a new user with a first and last name
        Map<String, Object> event = new HashMap<>();

        for (Event e : eventsList) {
            event.put("id", e.getId());
            event.put("tripEventId", e.getTripEventId());
            event.put("startTime", e.getStartTime());
            event.put("eventType", e.getEventType());
            event.put("duration", e.getDuration());
            event.put("riskLevel", e.getEventRisk());
            event.put("score", e.getScore());
            event.put("windowId", e.getWindowId());
            event.put("tripId", e.getTripId());
            event.put("rawData", e.getRawData());
            event.put("filteredData", e.getFilteredData());

            //Add user id
            event.put("user_id", user.getUid());

            events.add(event); //Add to Array of Maps
            event = new HashMap<>(); //reset

        }
        return events;
    }

    private List<Map<String,Object>> convertRecommendationListToHashMap (List<Recommendation> recommendationsList){

        List<Map<String,Object>> recommendations = new ArrayList<Map<String,Object>>();
        Log.d("DataUpload", "Inside convertRecommendationListToHashMap");
        Map<String, Object> recommendation = new HashMap<>();

        for (Recommendation r : recommendationsList) {
            recommendation.put("id", r.getId());
            recommendation.put("recommendationId", r.getRecommendationId());
            recommendation.put("recommendationRiskLevel", r.getRecommendationRiskLevel());
            recommendation.put("eventId", r.getEventId());
            recommendation.put("tripId", r.getTripId());

            //Add User Id
            recommendation.put("user_id", user.getUid());

            recommendations.add(recommendation); //Add to Array of Maps
            System.out.println("Recommendation ID = " + recommendation.get("id"));
            recommendation = new HashMap<>(); //reset
        }

        return recommendations;
    }



    private List<Map<String,Object>> convertWindowListToHashMap (List<Window> windowsList){

        List<Map<String,Object>> windows = new ArrayList<Map<String,Object>>();
        Log.d("DataUpload", "Inside convertWindowsListToHashMap");
        // Create a new user with a first and last name
        Map<String, Object> window = new HashMap<>();

        for (Window w : windowsList) {
            window.put("id", w.getId());
            window.put("windowId", w.getWindowId());
            window.put("windowTimestamp", w.getWindowTimestamp());
            window.put("pattern", w.getPattern());
            window.put("windowScore", w.getWindowScore());
            window.put("tripId", w.getTripId());
            window.put("behavior", w.getBehavior());

            //Add user Id
            window.put("user_id", user.getUid());

            windows.add(window); //Add to Array of Maps
            window = new HashMap<>(); //reset

        }
        return windows;
    }


    private Map<String,Object> convertTripToHashMap (Trip trip){

        Map<String,Object> currentTrip = new HashMap<>() ;
        Log.d("DataUpload", "Inside convertWindowsListToHashMap");

        currentTrip.put("id", trip.getTripId());
        currentTrip.put("user_id", trip.getTripUserId());
        currentTrip.put("overall_score", trip.getTripScore());
        currentTrip.put("start_time", trip.getTripStartTime());
        currentTrip.put("end_time", trip.getTripEndTime());

        return currentTrip;
    }

/*
DEPRECATED

    public void uploadTripInfoToast(Context context, String docRef){
        Toast toast = Toast.makeText(context,
                "Please wait ...", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();

        toast = Toast.makeText(context,
                "Trip data is being uploaded ..", Toast.LENGTH_SHORT);
        toast.show();
    }
 */


    public void showToast(Context context, String msg){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        }, 10 );
    }
/*
DEPRECATED

    public void uploadWindowsToast(Context context){
        Toast toast = Toast.makeText(context,
                "Please wait ...", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();

        toast = Toast.makeText(context,
                "Windows Data is being uploaded", Toast.LENGTH_LONG);
        toast.show();
    }


    public void uploadRecommendationsSuccessfulToast(Context context){
        Toast toast = Toast.makeText(context,
                "Please wait ...", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();

        toast =  Toast.makeText(context,
                "Recommendation data is being uploaded ...", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }


    public void uploadSensorDataSuccessfulToast(Context context){

        Toast toast = Toast.makeText(context,
                "Please wait ...", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();

        toast =  Toast.makeText(context,
                "Sensor data is being uploaded ...", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }
     */
}
