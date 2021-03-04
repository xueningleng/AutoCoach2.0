package com.example.autocoach20.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.autocoach20.Activities.Databases.TripDatabase.DbHelper;
import com.example.autocoach20.Activities.Databases.TripDatabase.FeedReaderContract;
import com.example.autocoach20.Activities.Model.Trip;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import java.sql.Timestamp;

/**
 * @author Zahraa Marafie
 * @version 3.0
 * @since AutoCoach V4.2
 */
public class Operations {
    private static final String TAG = "DataBaseOperations";
    private static DbHelper dbHelper;

    // ****************************************************************** //
    // WRITE OPERATIONS
    // ****************************************************************** //

    /**
     * @param documentReference is the user UID
     * @param context           is the application context
     */
    synchronized public void addToTableUser(DocumentReference documentReference, Context context, FirebaseUser currentUser) {
        dbHelper = new DbHelper(context);

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            Log.d(TAG, "Storing User ID: " + documentReference.getId());

            // Create a new map of values, where column names are the keys
            ContentValues UserValues = new ContentValues();
            UserValues.put(FeedReaderContract.FeedEntry.COLUMN_USER_ID, documentReference.getId()); //This needs to be read from Firebase Authentication
            UserValues.put(FeedReaderContract.FeedEntry.COLUMN_FIRST_NAME, currentUser.getDisplayName()); //This needs to be read from Firebase Authentication
            UserValues.put(FeedReaderContract.FeedEntry.COLUMN_LAST_NAME, currentUser.getDisplayName()); //This needs to be read from Firebase Authentication
            UserValues.put(FeedReaderContract.FeedEntry.COLUMN_CREATED_AT, System.currentTimeMillis());

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_USER, null, UserValues);

            Log.i(TAG, "Primary Id of the inserted row into TABLE USER=" + newRowId);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // dbHelper.closeDB(db);
        }
    }

    synchronized public void updateUser(DocumentReference documentReference, Context context, FirebaseUser currentUser, int gender, int age) {
        dbHelper = new DbHelper(context);

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            Log.d(TAG, "Updating info for User ID: " + documentReference.getId());

            // Create a new map of values, where column names are the keys
            ContentValues UserValues = new ContentValues();
            UserValues.put(FeedReaderContract.FeedEntry.COLUMN_GENDER, gender); //This needs to be read from Firebase Authentication
            UserValues.put(FeedReaderContract.FeedEntry.COLUMN_AGE, age); //This needs to be read from Firebase Authentication

            db.update(FeedReaderContract.FeedEntry.TABLE_USER, UserValues, FeedReaderContract.FeedEntry.COLUMN_USER_ID + " = ?", new String[]{documentReference.getId()});
            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_USER, null, UserValues);

            Log.i(TAG, "Primary Id of the inserted row into TABLE USER=" + newRowId);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //dbHelper.closeDB(db);
        }
    }

    /**
     * @param context          is the application context
     * @param UID              holds the user's UID
     * @param tripOverallScore is the final average score of all windows in this trip
     * @param tripStartTime    the timestamp of the beginning time of the trip
     * @param tripEndTime      the timestamp of the event of the trip
     */
    synchronized public void addToTableTrip(Context context, String UID, double tripOverallScore, long tripStartTime, long tripEndTime, int sync) {
        dbHelper = new DbHelper(context);

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            dbHelper.onUpgrade(db, 0, 1);
            Log.d(TAG, "Inserting Trip: ");

            // Create a new map of values, where column names are the keys
            ContentValues TripValues = new ContentValues();
            TripValues.put(FeedReaderContract.FeedEntry.COLUMN_USER_ID, UID);
            TripValues.put(FeedReaderContract.FeedEntry.COLUMN_SCORE, tripOverallScore);
            TripValues.put(FeedReaderContract.FeedEntry.COLUMN_START_TIME, tripStartTime);
            TripValues.put(FeedReaderContract.FeedEntry.COLUMN_END_TIME, tripEndTime);
            TripValues.put(FeedReaderContract.FeedEntry.COLUMN_SYNC, sync);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_TRIP, null, TripValues);

            Log.i(TAG, "Primary Id of the inserted row into TABLE TRIP =" + newRowId);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // dbHelper.closeDB(db);
        }
    }

    synchronized public void addToTableSpeedRecord(Context context, int tripId, int speed, Timestamp time, int head_position, double gyro_data) {
        dbHelper = new DbHelper(context);

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            dbHelper.onUpgrade(db, 1, 2);
            Log.d(TAG, "Inserting Speed Record: ");

            // Create a new map of values, where column names are the keys
            ContentValues TripValues = new ContentValues();
            TripValues.put(FeedReaderContract.FeedEntry.COLUMN_TRIP_ID, tripId);
            TripValues.put(FeedReaderContract.FeedEntry.COLUMN_SPEED, speed);
            TripValues.put(FeedReaderContract.FeedEntry.COLUMN_TIMESTAMP, String.valueOf(time));
            TripValues.put(FeedReaderContract.FeedEntry.COLUMN_RASPI, head_position);
            TripValues.put(FeedReaderContract.FeedEntry.COLUMN_GYRO, gyro_data);


            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_SPEEDRECORD, null, TripValues);

            Log.i(TAG, "Primary Id of the inserted row into TABLE TRIP =" + newRowId);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // dbHelper.closeDB(db);
        }
    }


    // ****************************************************************** //
    // READ OPERATIONS
    // ****************************************************************** //

    public Trip readCurrentTripDetails(Context context) {
        Trip trip = null;

        int tripId;
        long tripStartTime;
        long tripEndTime;
        String tripUserId;
        double tripScore;

        dbHelper = new DbHelper(context);

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + FeedReaderContract.FeedEntry.TABLE_TRIP +
                " WHERE " + FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + " = (SELECT MAX(" +
                FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + ") FROM " +
                FeedReaderContract.FeedEntry.TABLE_TRIP + ")";

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            while (cursor.moveToNext()) {
                Log.d(TAG, "Reading Trip Details");
                tripId = cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_TRIP_ID));
                tripUserId = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_USER_ID));
                tripStartTime = cursor.getLong(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_START_TIME));
                tripScore = cursor.getLong(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_SCORE));
                tripEndTime = cursor.getLong(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_END_TIME));
                trip = new Trip(tripId, tripUserId, tripStartTime, tripEndTime, tripScore);
            }

        } catch (Exception e) {

            Log.d(TAG, "Error while trying to get trip details from database");
        } finally {
            //dbHelper.closeDB(db);
            if (cursor != null || !cursor.isClosed()) {
                cursor.close();
            }
        }

        return trip;
    }


    // ****************************************************************** //
    // UPDATE OPERATIONS
    // ****************************************************************** //

    public boolean updateTripRecord(Context context, int tripId, long tripEndTime,
                                    String userId, double tripScore) {
        dbHelper = new DbHelper(context);
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Trip trip = readCurrentTripDetails(context);
        boolean isUpdated = false;

        try {
            Log.d(TAG, "Inserting Trip: ");

            // Create a new map of values, where column names are the keys
            ContentValues TripValues = new ContentValues();
            TripValues.put(FeedReaderContract.FeedEntry.COLUMN_USER_ID, userId);
            TripValues.put(FeedReaderContract.FeedEntry.COLUMN_SCORE, tripScore);
            TripValues.put(FeedReaderContract.FeedEntry.COLUMN_START_TIME, trip.getTripStartTime());
            TripValues.put(FeedReaderContract.FeedEntry.COLUMN_END_TIME, tripEndTime);

            String whereClause = FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + " = " + tripId;


            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.update(FeedReaderContract.FeedEntry.TABLE_TRIP, TripValues, whereClause, null);
            Log.i(TAG, "Primary Id of the updated row into TABLE TRIP =" + newRowId);
            isUpdated = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //dbHelper.closeDB(db);
        }

        return isUpdated;
    }


    public void onClose(Context context) {
        dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.closeDB(db);
    }
    // ****************************************************************** //
    // PRIVATE METHODS
    // ****************************************************************** //


}
