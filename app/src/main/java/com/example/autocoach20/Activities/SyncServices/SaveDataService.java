package com.example.autocoach20.Activities.SyncServices;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.autocoach20.Activities.Databases.TripDatabase.DbHelper;
import com.example.autocoach20.Activities.Databases.TripDatabase.FeedReaderContract;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

/**
 * We need to store drivers data in order to train the personality model
 * The current features in the personality model includes the following information:
 * Percentage of High Risk Events
 * Percentage of Low Scores
 * Density of Angry Behavior Duration
 * Reckless Behavior Duration
 * Anxious Behavior Duration
 * Percentage of Safe Brakes
 * Percentage of Medium Risk Brakes
 * Percentage of High Risk Brakes
 * Percentage of Safe Acceleration
 * Percentage of Medium Risk Acceleration
 * Percentage of High Risk Acceleration
 * Percentage of Safe Turns
 * Percentage of Medium Risk Turns
 * Percentage of High Risk Turns
 * Percentage of Safe Swerves
 * Percentage of Medium Risk Swerves
 * Percentage of High Risk Swerves
 *
 *
 *  So, we need to store timestamp, pattern, pattern score, event and events durations
 *
 *  THIS SERVICE STORES THE DATA TO THE LOCAL DATABASE
 */


/**
 * @deprecated not used
 */
public class SaveDataService extends Service {

    private static final String TAG = "";
    private static DbHelper dbHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    // This is the object that receives interactions from clients. See
    // RemoteService for a more complete example.
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public SaveDataService getService() {
            return SaveDataService.this;
        }
    }




    synchronized public void addToTableUser(Context context, ArrayList<ContentValues> values, DocumentReference documentReference){
        Log.d("DriverDataSave", "****** Inserting User Information ******");
        try {

            dbHelper = new DbHelper(getApplicationContext());

            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Log.d(TAG, "Storing User ID: " + documentReference.getId());

            // Create a new map of values, where column names are the keys
            ContentValues TripValues = new ContentValues();
            TripValues.put(FeedReaderContract.FeedEntry.COLUMN_USER_ID, documentReference.getId());

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_USER, null, TripValues);

            System.out.println ("Primary Id of the inserted row =" + newRowId);

            //if (values != null && values.size() != 0) {
            //    /* Get a handle on the ContentResolver to insert data */
            //    //Check manifest, where SensorContent provider is defined, to allow contents
            //    ContentResolver contentResolver = context.getContentResolver();

            //    /* Convert the array list to normal array */
            //    ContentValues[] cvalues = new ContentValues[values.size()];
            //    for (int i=0; i<values.size(); i++) {
            //        cvalues[i] = values.get(i);

            //        if(values.get(i).get(FeedReaderContract.FeedEntry.COLUMN_USER_ID)==null){
            //            System.out.println("USER_ID value is NULL");
            //        }
            //    }

            Log.d("DriverDataSave", "****** User Information Inserted ******");
            /* If the code reaches this point, we have successfully performed our insert into localDB */

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    synchronized public static void addToTableTrip (Context context, ArrayList<ContentValues> values){

    }

    synchronized public static void addToTableEvent (Context context, ArrayList<ContentValues> values){

    }

    synchronized public static void addToTableEventFeedback (Context context, ArrayList<ContentValues> values){

    }

    synchronized public static void addToTableRecommendation (Context context, ArrayList<ContentValues> values){

    }

    synchronized public static void addToTablePatternWindow (Context context, ArrayList<ContentValues> values){

    }
}