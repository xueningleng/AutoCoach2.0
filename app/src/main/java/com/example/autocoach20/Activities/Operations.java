package com.example.autocoach20.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.autocoach20.Activities.Databases.TripDatabase.DbHelper;
import com.example.autocoach20.Activities.Databases.TripDatabase.FeedReaderContract;
import com.example.autocoach20.Activities.Model.Event;
import com.example.autocoach20.Activities.Model.Recommendation;
import com.example.autocoach20.Activities.Model.Trip;
import com.example.autocoach20.Activities.Model.Window;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     *
     * @param documentReference is the user UID
     * @param context is the application context
     */
    synchronized public void addToTableUser (DocumentReference documentReference, Context context, FirebaseUser currentUser){
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
        }
        finally {
            dbHelper.closeDB(db);
        }
    }


    /**
     *
     * @param context is the application context
     * @param UID holds the user's UID
     * @param tripOverallScore is the final average score of all windows in this trip
     * @param tripStartTime the timestamp of the beginning time of the trip
     * @param tripEndTime the timestamp of the event of the trip
     */
    synchronized public void addToTableTrip (Context context, String UID, double tripOverallScore,  long tripStartTime, long tripEndTime, int sync){
        dbHelper = new DbHelper(context);

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
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
        }
        finally {
            dbHelper.closeDB(db);
        }
    }

    /**
     *
     * @param context is the application context
     * @param eventId is the id of the event happening in this trip, this is a serial number
     * @param eventTypeId is a number from 0 to 11, where 0~2 represent safe, medium, high-risk accelerations,
     *                    3~5 for accelerations,
     *                    6~8 for turns
     *                    9~11 for swerves
     * @param duration is the time duration of the event -> how long did the event happen
     * @param score is the event score
     */

    synchronized public void addToTableEvent (Context context, int eventId, double eventTypeId,
                                              long eventStartTime, double duration, double score,
                                              int windowId, int tripId, String filteredData,
                                              String rawData, int sync){
        dbHelper = new DbHelper(context);

        String[] eventDetails;
        eventDetails = getEventDetails(eventTypeId);

        String eventType = eventDetails[0]; //eventDetails[0] holds the value of the eventType
        String riskLevel = eventDetails[1]; //eventDetails[1] holds the value of the event riskLevel

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            Log.d(TAG, "Storing Event: " + eventTypeId + " Duration: " + duration);

            // Create a new map of values, where column names are the keys
            ContentValues EventValues = new ContentValues();
            EventValues.put(FeedReaderContract.FeedEntry.COLUMN_EVENT_ID, eventId);
            EventValues.put(FeedReaderContract.FeedEntry.COLUMN_EVENT_START_TIME, eventStartTime);
            EventValues.put(FeedReaderContract.FeedEntry.COLUMN_EVENT_TYPE, eventType);
            EventValues.put(FeedReaderContract.FeedEntry.COLUMN_EVENT_DURATION, duration);
            EventValues.put(FeedReaderContract.FeedEntry.COLUMN_EVENT_RISK_LEVEL, riskLevel);
            EventValues.put(FeedReaderContract.FeedEntry.COLUMN_SCORE, score);
            EventValues.put(FeedReaderContract.FeedEntry.COLUMN_EVENT_RAW_DATA, rawData);
            EventValues.put(FeedReaderContract.FeedEntry.COLUMN_EVENT_FILTERED_DATA, filteredData);
            EventValues.put(FeedReaderContract.FeedEntry.COLUMN_WINDOW_ID, windowId);
            EventValues.put(FeedReaderContract.FeedEntry.COLUMN_TRIP_ID, tripId);
            EventValues.put(FeedReaderContract.FeedEntry.COLUMN_SYNC, sync);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_EVENT, null, EventValues);

            Log.i(TAG, "Primary Id of the inserted row into TABLE EVENT=" + newRowId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            dbHelper.closeDB(db);
        }
    }


    /**
     *
     * @param context is the application context
     * @param tripId the tripId where this recommendation is given
     * @param eventId is the id of the event that was shown on the bar and we used it for giving this recommnedation
     * @param recommendationRiskLevel how much dangerous is this event we gave recommendation for (orange of red glow)
     * @param eventId is the id of the event which has been used to give the feedback
     */
    synchronized public void addToTableRecommendation (Context context, int recommendationId,
                                                       int tripId, int eventId,
                                                       String recommendationRiskLevel, int sync){
        dbHelper = new DbHelper(context);

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            Log.d(TAG, "Inserting Recommendation: ");

            // Create a new map of values, where column names are the keys
            ContentValues RecommendationValues = new ContentValues();
            RecommendationValues.put(FeedReaderContract.FeedEntry.COLUMN_RECOMMENDATION_ID, recommendationId);
            RecommendationValues.put(FeedReaderContract.FeedEntry.COLUMN_TRIP_ID, tripId);
            RecommendationValues.put(FeedReaderContract.FeedEntry.COLUMN_EVENT_ID, eventId);
            RecommendationValues.put(FeedReaderContract.FeedEntry.COLUMN_RECOMMENDATION_RISK_LEVEL, recommendationRiskLevel);
            RecommendationValues.put(FeedReaderContract.FeedEntry.COLUMN_SYNC, sync);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_RECOMMENDATION, null, RecommendationValues);

            Log.i(TAG, "Primary Id of the inserted row into TABLE RECOMMENDATION=" + newRowId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            dbHelper.closeDB(db);
        }
    }

    /**
     * @param context is the application context
     * @param windowTimestamp is the start of the window timestamp
     * @param windowScore is the score of the pattern in this window
     * @param tripId is the id of the trip this window belongs to
     */
    synchronized public void addToTablePatternWindow (Context context, int windowId, long windowTimestamp,
                                                      String pattern, double windowScore, int tripId,
                                                      String behavior, int sync){
        dbHelper = new DbHelper(context);

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            Log.d(TAG, "Inserting Pattern Window: " + pattern);

            // Create a new map of values, where column names are the keys
            ContentValues PatternValues = new ContentValues();
            PatternValues.put(FeedReaderContract.FeedEntry.COLUMN_WINDOW_ID, windowId);
            PatternValues.put(FeedReaderContract.FeedEntry.COLUMN_WINDOW_TIMESTAMP, windowTimestamp);
            PatternValues.put(FeedReaderContract.FeedEntry.COLUMN_PATTERN, pattern);
            PatternValues.put(FeedReaderContract.FeedEntry.COLUMN_SCORE, windowScore);
            PatternValues.put(FeedReaderContract.FeedEntry.COLUMN_TRIP_ID, tripId);
            PatternValues.put(FeedReaderContract.FeedEntry.COLUMN_BEHAVIOR, behavior);
            PatternValues.put(FeedReaderContract.FeedEntry.COLUMN_SYNC, sync);


            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_PATTERN_WINDOW, null, PatternValues);

            Log.i(TAG, "Primary Id of the inserted row into TABLE PATTERN WINDOW =" + newRowId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            dbHelper.closeDB(db);
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
            while(cursor.moveToNext()){
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
            dbHelper.closeDB(db);
            if (cursor != null || !cursor.isClosed()) {
                cursor.close();
            }
        }

        return trip;
    }

    // ****************************************************************** //
    // READ ALL DATA OPERATIONS
    // ****************************************************************** //

    /**
     * This class fetches all events of the current trip
     * @param context
     * @return
     */
    public List<Event> fetchTripEvents(Context context) {
        dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Event> events = new ArrayList<>();
        Trip trip = readCurrentTripDetails(context);
        int tripId = trip.getTripId();

        String selectQuery = "SELECT * FROM " + FeedReaderContract.FeedEntry.TABLE_EVENT
                + " WHERE " + FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + "=" + tripId;

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            while(cursor.moveToNext()){
                Log.d(TAG, "Reading Event Info");
                Event newEvent = new Event();
                newEvent.setId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_ID)));
                newEvent.setTripEventId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_EVENT_ID)));
                newEvent.setStartTime( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_EVENT_START_TIME)));
                newEvent.setEventType( cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_EVENT_TYPE)));
                newEvent.setEventRisk( cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_EVENT_RISK_LEVEL)));
                newEvent.setScore( cursor.getDouble(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_SCORE)));
                newEvent.setDuration( cursor.getDouble(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_EVENT_DURATION)));
                newEvent.setWindowId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_WINDOW_ID)));
                newEvent.setTripId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_TRIP_ID)));
                newEvent.setRawData( cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_EVENT_RAW_DATA)));
                newEvent.setFilteredData( cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_EVENT_FILTERED_DATA)));

                events.add(newEvent);
            }

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get events from database");
        } finally {
            dbHelper.closeDB(db);
            if (cursor != null || !cursor.isClosed()) {
                cursor.close();
            }
        }
        return events;
    }


    public List<Recommendation> fetchTripRecommendations(Context context) {
        dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Recommendation> recommendations = new ArrayList<>();
        Trip trip = readCurrentTripDetails(context);
        int tripId = trip.getTripId();

        String selectQuery = "SELECT * FROM " + FeedReaderContract.FeedEntry.TABLE_RECOMMENDATION
                + " WHERE " + FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + "=" + tripId;

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            while(cursor.moveToNext()){
                Log.d(TAG, "Reading Recommendation Info");
                Recommendation newRecommendation = new Recommendation();
                newRecommendation.setId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_ID)));
                newRecommendation.setRecommendationId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_RECOMMENDATION_ID)));
                newRecommendation.setTripId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_TRIP_ID)));
                newRecommendation.setEventId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_EVENT_ID)));
                newRecommendation.setRecommendationRiskLevel( cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_RECOMMENDATION_RISK_LEVEL)));

                recommendations.add(newRecommendation);
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get events from database");
        } finally {
            dbHelper.closeDB(db);
            if (cursor != null || !cursor.isClosed()) {
                cursor.close();
            }
        }
        return recommendations;
    }


    public List<Window> fetchTripWindows(Context context) {
        dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Window> recommendations = new ArrayList<>();
        Trip trip = readCurrentTripDetails(context);
        int tripId = trip.getTripId();

        String selectQuery = "SELECT * FROM " + FeedReaderContract.FeedEntry.TABLE_PATTERN_WINDOW
                + " WHERE " + FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + "=" + tripId;

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            while(cursor.moveToNext()){
                Log.d(TAG, "Reading Window Info");
                Window newWindow = new Window();
                newWindow.setId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_ID)));
                newWindow.setWindowId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_WINDOW_ID)));
                newWindow.setWindowTimestamp( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_WINDOW_TIMESTAMP)));
                newWindow.setPattern( cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_PATTERN)));
                newWindow.setWindowScore( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_SCORE)));
                newWindow.setTripId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_TRIP_ID)));
                newWindow.setBehavior( cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_BEHAVIOR)));

                recommendations.add(newWindow);
            }

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get events from database");
        } finally {
            dbHelper.closeDB(db);
            if (cursor != null || !cursor.isClosed()) {
                cursor.close();
            }
        }
        return recommendations;
    }


    // ****************************************************************** //
    // FETCH SOME RECORDS
    // ****************************************************************** //

    /**
     * This class some events of the current trip
     * @param context
     * @param rowCount is the number of rows to return
     * @return
     */
    public List<Event> fetchEvents(Context context, int rowCount) {
        dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Event> events = new ArrayList<>();
        Trip trip = readCurrentTripDetails(context);
        int tripId = trip.getTripId();

        boolean hasRows = false;

        String selectQuery = "SELECT * FROM " + FeedReaderContract.FeedEntry.TABLE_EVENT
                + " WHERE " + FeedReaderContract.FeedEntry.COLUMN_SYNC + "=" + 0 +
                " LIMIT " + rowCount;

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            while(cursor.moveToNext()){
                hasRows = true;

                Log.d(TAG, "Reading Event Info");
                String rawData = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_EVENT_RAW_DATA));
                //check the length of a single event to make sure that it does not exceed the Firebase constrain
                final int constrain = 1048487;
                if(rawData.getBytes().length>=constrain){
                    Log.d("Upload", "raw data length exceeds the Firebase constrain. It has been discarded.");
                    rawData = "[]";
                }
                Event newEvent = new Event();
                newEvent.setId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_ID)));
                newEvent.setTripEventId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_EVENT_ID)));
                newEvent.setStartTime( cursor.getLong(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_EVENT_START_TIME)));
                newEvent.setEventType( cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_EVENT_TYPE)));
                newEvent.setEventRisk( cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_EVENT_RISK_LEVEL)));
                newEvent.setScore( cursor.getDouble(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_SCORE)));
                newEvent.setDuration( cursor.getDouble(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_EVENT_DURATION)));
                newEvent.setWindowId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_WINDOW_ID)));
                newEvent.setTripId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_TRIP_ID)));
                newEvent.setRawData(rawData);
                newEvent.setFilteredData( cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_EVENT_FILTERED_DATA)));

                events.add(newEvent);
            }

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get events from database");
        } finally {
            dbHelper.closeDB(db);
            if (cursor != null || !cursor.isClosed()) {
                cursor.close();
            }
        }

        if (hasRows == true)
            return events;
        else
            return null;
    }


    public List<Recommendation> fetchRecommendations(Context context, int rowCount) {
        dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Recommendation> recommendations = new ArrayList<>();
        Trip trip = readCurrentTripDetails(context);
        int tripId = trip.getTripId();

        boolean hasRows = false;

        String selectQuery = "SELECT * FROM " + FeedReaderContract.FeedEntry.TABLE_RECOMMENDATION
                + " WHERE " + FeedReaderContract.FeedEntry.COLUMN_SYNC + "=" + 0 +
                " LIMIT " + rowCount;

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            while(cursor.moveToNext()){
                Log.d(TAG, "Reading Recommendation Info");
                hasRows = true;

                Recommendation newRecommendation = new Recommendation();
                newRecommendation.setId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_ID)));
                newRecommendation.setRecommendationId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_RECOMMENDATION_ID)));
                newRecommendation.setTripId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_TRIP_ID)));
                newRecommendation.setEventId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_EVENT_ID)));
                newRecommendation.setRecommendationRiskLevel( cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_RECOMMENDATION_RISK_LEVEL)));

                recommendations.add(newRecommendation);
            }
            //toString(recommendations);

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get events from database");
        } finally {
            dbHelper.closeDB(db);
            if (cursor != null || !cursor.isClosed()) {
                cursor.close();
            }
        }
        if (hasRows == true)
            return recommendations;
        else
            return null;
    }


    public List<Window> fetchWindows(Context context, int rowCount) {
        dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Window> windows = new ArrayList<>();
        Trip trip = readCurrentTripDetails(context);
        int tripId = trip.getTripId();

        boolean hasRows = false;

        String selectQuery = "SELECT * FROM " + FeedReaderContract.FeedEntry.TABLE_PATTERN_WINDOW
                + " WHERE " + FeedReaderContract.FeedEntry.COLUMN_SYNC + "=" + 0 +
                " LIMIT " + rowCount;

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            while(cursor.moveToNext()){
                Log.d(TAG, "Reading Window Info");
                hasRows = true;

                Window newWindow = new Window();
                newWindow.setId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_ID)));
                newWindow.setWindowId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_WINDOW_ID)));
                newWindow.setWindowTimestamp( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_WINDOW_TIMESTAMP)));
                newWindow.setPattern( cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_PATTERN)));
                newWindow.setWindowScore( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_SCORE)));
                newWindow.setTripId( cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_TRIP_ID)));
                newWindow.setBehavior( cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_BEHAVIOR)));

                windows.add(newWindow);
            }

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get events from database");
        } finally {
            dbHelper.closeDB(db);
            if (cursor != null || !cursor.isClosed()) {
                cursor.close();
            }
        }

        if (hasRows == true)
            return windows;
        else
            return null;
    }


    // ****************************************************************** //
    // UPDATE OPERATIONS
    // ****************************************************************** //

    public boolean updateTripRecord(Context context, int tripId, long tripEndTime,
                                    String userId, double tripScore){
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
        }
        finally {
            dbHelper.closeDB(db);
        }

        return isUpdated;
    }

    public boolean updateSyncedTripRecord(Context context, int tripId, long tripEndTime,
                                          String userId, double tripScore){
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
            TripValues.put(FeedReaderContract.FeedEntry.COLUMN_SYNC, 1);

            String whereClause = FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + " = " + tripId;

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.update(FeedReaderContract.FeedEntry.TABLE_TRIP, TripValues, whereClause, null);
            Log.i(TAG, "Primary Id of the updated row into TABLE TRIP =" + newRowId);
            isUpdated = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            dbHelper.closeDB(db);
        }

        return isUpdated;
    }

    // ****************************************************************** //
    // BULK UPDATE
    // ****************************************************************** //
    public void bulkRecommendationsUpdate(Context context, List<Map<String, Object>> recommendations) {
        Log.w(TAG, "Starting BULK RECOMMENDATIONS Update !!");
        ArrayList<Integer> ids = new ArrayList<>();
        dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues recommendationValues = new ContentValues();
        String whereClause;
        for (Map<String, Object> recommendation : recommendations) {
            recommendationValues.put(FeedReaderContract.FeedEntry.COLUMN_ID, (int)recommendation.get("id"));
            recommendationValues.put(FeedReaderContract.FeedEntry.COLUMN_RECOMMENDATION_ID, (int)recommendation.get("recommendationId"));
            recommendationValues.put(FeedReaderContract.FeedEntry.COLUMN_RECOMMENDATION_RISK_LEVEL, (String)recommendation.get("recommendationRiskLevel"));
            recommendationValues.put(FeedReaderContract.FeedEntry.COLUMN_EVENT_ID, (int)recommendation.get("eventId"));
            recommendationValues.put(FeedReaderContract.FeedEntry.COLUMN_TRIP_ID, (int)recommendation.get("tripId"));
            recommendationValues.put("sync", 1);

            whereClause = FeedReaderContract.FeedEntry.COLUMN_ID + " = " + (int)recommendation.get("id");
            long rowId = db.update(FeedReaderContract.FeedEntry.TABLE_RECOMMENDATION, recommendationValues, whereClause, null);
            Log.i(TAG, "UPDATED " + rowId + " FROM TABLE RECOMMENDATION");
        }

        /**
         String sqlQuery = "UPDATE " + FeedReaderContract.FeedEntry.TABLE_RECOMMENDATION + " SET " + FeedReaderContract.FeedEntry.COLUMN_SYNC
         + "= 1 WHERE " + FeedReaderContract.FeedEntry.COLUMN_ID + " =? " ;

         db.beginTransaction();
         SQLiteStatement upd=db.compileStatement(sqlQuery);
         int i;
         for (i = 0; i < ids.size(); i++) {
         upd.bindLong(1, ids.get(i));
         upd.execute();
         }
         db.endTransaction();
         */
    }

    public void bulkEventsUpdate(Context context, List<Map<String, Object>> events) {
        Log.w(TAG, "Starting BULK EVENTS Update !!");
        ArrayList<Integer> ids = new ArrayList<>();
        dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues eventsValues = new ContentValues();
            String whereClause = null;
            for (Map<String, Object> event : events) {

                eventsValues.put(FeedReaderContract.FeedEntry.COLUMN_ID, (int)event.get("id"));
                eventsValues.put(FeedReaderContract.FeedEntry.COLUMN_EVENT_ID, (int)event.get("tripEventId"));
                eventsValues.put(FeedReaderContract.FeedEntry.COLUMN_EVENT_TYPE, (String)event.get("eventType"));
                eventsValues.put(FeedReaderContract.FeedEntry.COLUMN_EVENT_RISK_LEVEL, (String)event.get("riskLevel"));
                eventsValues.put(FeedReaderContract.FeedEntry.COLUMN_EVENT_START_TIME, (long)event.get("startTime"));
                eventsValues.put(FeedReaderContract.FeedEntry.COLUMN_SCORE, (double)event.get("score"));
                eventsValues.put(FeedReaderContract.FeedEntry.COLUMN_EVENT_DURATION, (double)event.get("duration"));
                eventsValues.put(FeedReaderContract.FeedEntry.COLUMN_WINDOW_ID, (int)event.get("windowId"));
                eventsValues.put(FeedReaderContract.FeedEntry.COLUMN_TRIP_ID, (int)event.get("tripId"));
                eventsValues.put(FeedReaderContract.FeedEntry.COLUMN_EVENT_RAW_DATA, (String)event.get("rawData"));
                eventsValues.put(FeedReaderContract.FeedEntry.COLUMN_EVENT_FILTERED_DATA, (String)event.get("filteredData"));
                eventsValues.put(FeedReaderContract.FeedEntry.COLUMN_SYNC, 1);

                whereClause = FeedReaderContract.FeedEntry.COLUMN_ID + " = " + (int)event.get("id");
                long rowId = db.update(FeedReaderContract.FeedEntry.TABLE_EVENT, eventsValues, whereClause, null);
                Log.i(TAG, "UPDATED " + rowId + " FROM TABLE EVENT");


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            dbHelper.closeDB(db);
        }
    }

    public void bulkWindowsUpdate(Context context, List<Map<String, Object>> windows) {
        Log.w(TAG, "Starting BULK WINDOWS Update !!");
        ArrayList<Integer> ids = new ArrayList<>();
        dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues windowValues = new ContentValues();
            String whereClause = null;
            for (Map<String, Object> window : windows) {
                windowValues.put(FeedReaderContract.FeedEntry.COLUMN_ID, (int)window.get("id"));
                windowValues.put(FeedReaderContract.FeedEntry.COLUMN_TRIP_ID, (int)window.get("tripId"));
                windowValues.put(FeedReaderContract.FeedEntry.COLUMN_WINDOW_ID, (int)window.get("windowId"));
                windowValues.put(FeedReaderContract.FeedEntry.COLUMN_PATTERN, (String)window.get("pattern"));
                windowValues.put(FeedReaderContract.FeedEntry.COLUMN_WINDOW_TIMESTAMP, (long)window.get("windowTimestamp"));windowValues.put(FeedReaderContract.FeedEntry.COLUMN_SCORE, (double)window.get("windowScore"));
                windowValues.put(FeedReaderContract.FeedEntry.COLUMN_BEHAVIOR, (String)window.get("behavior"));
                windowValues.put(FeedReaderContract.FeedEntry.COLUMN_SYNC, 1);

                whereClause = FeedReaderContract.FeedEntry.COLUMN_ID + " = " + (int)window.get("id");
                long rowId = db.update(FeedReaderContract.FeedEntry.TABLE_PATTERN_WINDOW, windowValues, whereClause, null);
                Log.i(TAG, "UPDATED " + rowId + " FROM TABLE PATTERN_WINDOW");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            dbHelper.closeDB(db);
        }
    }


    // ****************************************************************** //
    // PRIVATE METHODS
    // ****************************************************************** //

    /**
     * This function takes the value of level and identifies for each level id,
     * what is the match event type and risk level of event
     * @param level is a number from 0 to 11, where 0~2 represent safe, medium, high-risk accelerations,
     *                    3~5 for accelerations,
     *                    6~8 for turns
     *                    9~11 for swerves
     * @return eventDetails is a String holding two values eventDetails[0] holds the event type and
     *                    eventDetails[1] holds the value of the event riskLevel
     */
    private static String[] getEventDetails(double level) {

        String riskLevel = "";
        String eventType = "";
        String eventDetails[] = new String[2];

        if (level == 0){
            riskLevel = "Safe";
            eventType = "Acceleration";
        }
        else if (level == 1) {
            riskLevel = "Medium-Risk";
            eventType = "Acceleration";
        }
        else if (level == 2){
            riskLevel = "High-Risk";
            eventType = "Acceleration";
        }

        else if (level == 3) {
            riskLevel = "Safe";
            eventType = "Brake";
        }
        else if (level == 4) {
            riskLevel = "Medium-Risk";
            eventType = "Brake";
        }

        else if (level == 5){
            riskLevel = "High-Risk";
            eventType = "Brake";
        }

        else if (level == 6) {
            riskLevel = "Safe";
            eventType = "Turn";
        }

        else if (level == 7) {
            riskLevel = "Medium-Risk";
            eventType = "Turn";
        }

        else if (level == 8) {
            riskLevel = "High-Risk";
            eventType = "Turn";
        }

        else if (level == 9) {
            riskLevel = "Safe";
            eventType = "Swerve";

        }
        else if (level == 10) {
            riskLevel = "Medium-Risk";
            eventType = "Swerve";
        }

        else if (level == 11){
            riskLevel = "High-Risk";
            eventType = "Swerve";
        }

        eventDetails[0] = eventType;
        eventDetails[1] = riskLevel;

        System.out.println("eventType: " + eventType + " riskLevel: " + riskLevel);
        return eventDetails;
    }


    private void toString (List<Recommendation> array){

        for (Recommendation rec : array) {
            System.out.print("Recommendation ID: " + rec.getId());
            System.out.print(" Recommendation Risk Level: " + rec.getRecommendationRiskLevel());
            System.out.println (" and Event ID " + rec.getEventId());

        }
    }

}
