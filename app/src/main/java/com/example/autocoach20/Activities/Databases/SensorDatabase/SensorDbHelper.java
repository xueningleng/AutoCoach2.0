package com.example.autocoach20.Activities.Databases.SensorDatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.autocoach20.Activities.DBOperations;
import com.example.autocoach20.Activities.Model.Trip;
import com.example.autocoach20.Activities.StartAutoCoachActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sandeepchawan on 2017-10-26.
 */

public class SensorDbHelper extends SQLiteOpenHelper {
    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    public static final String DATABASE_NAME = "sensor.db";
    private static final int DATABASE_VERSION = 2; //Version 2 included the trip ID

    private static final String TAG = "SensorDbHelper";
    private DBOperations mydb = new DBOperations();
    private static SensorDbHelper sensorDbHelper;


    public SensorDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our sensor data.
         */
        final String SQL_CREATE_SENSOR_TABLE =

                "CREATE TABLE " + SensorContract.SensorEntry.TABLE_NAME + " (" +

                /*
                 * SensorEntry did not explicitly declare a column called "_ID". However,
                 * SensorEntry implements the interface, "BaseColumns", which does have a field
                 * named "_ID". We use that here to designate our table's primary key.
                 */
                        SensorContract.SensorEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        SensorContract.SensorEntry.COLUMN_DATE       + " INTEGER NOT NULL, "                 +

                        SensorContract.SensorEntry.COLUMN_SPEED       + " INTEGER DEFAULT 0, "    +

                        SensorContract.SensorEntry.COLUMN_ACC_X + " DOUBLE DEFAULT NULL, "                       +
                        SensorContract.SensorEntry.COLUMN_ACC_Y   + " DOUBLE DEFAULT NULL, "                    +
                        SensorContract.SensorEntry.COLUMN_ACC_Z   + " DOUBLE DEFAULT NULL, "                    +

                        SensorContract.SensorEntry.COLUMN_GYRO_X + " FLOAT DEFAULT NULL, "                       +
                        SensorContract.SensorEntry.COLUMN_GYRO_Y   + " FLOAT DEFAULT NULL, "                    +
                        SensorContract.SensorEntry.COLUMN_GYRO_Z   + " FLOAT DEFAULT NULL, "                    +

                        SensorContract.SensorEntry.COLUMN_MAGNETO_X + " FLOAT DEFAULT NULL, "                       +
                        SensorContract.SensorEntry.COLUMN_MAGNETO_Y   + " FLOAT DEFAULT NULL, "                    +
                        SensorContract.SensorEntry.COLUMN_MAGNETO_Z   + " FLOAT DEFAULT NULL, "                    +

                        SensorContract.SensorEntry.COLUMN_GPS_LAT       + " DOUBLE DEFAULT NULL, "    +
                        SensorContract.SensorEntry.COLUMN_GPS_LONG       + " DOUBLE DEFAULT NULL, "    +

                        SensorContract.SensorEntry.COLUMN_TRIP_ID      + " INTEGER DEFAULT NULL, " +

                        SensorContract.SensorEntry.COLUMN_CLASSIFICATION      + " INTEGER DEFAULT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_SENSOR_TABLE); //exec the sql to create the table
    }

    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SensorContract.SensorEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    /**
     * This function reads all sensor data for this current trip
     * @param context
     * @return all sensor data
     */
    public List<Map<String,Object>> fetchAllSensorData (Context context){

        List<Map<String,Object>> allSensorData = new ArrayList<Map<String,Object>>();

        sensorDbHelper = new SensorDbHelper(context);
        SQLiteDatabase db = sensorDbHelper.getReadableDatabase();

        String userId = StartAutoCoachActivity.getMainActivity().getUser().getUser_id();

        //GET THE TRIP ID
        Trip trip = mydb.fetchCurrentTripData();
        int tripId = trip.getTripId();

        //ALL DATA FOR THIS CURRENT TRIP QUERY
        String selectQuery = "SELECT * FROM " + SensorContract.SensorEntry.TABLE_NAME
                + " WHERE " + SensorContract.SensorEntry.COLUMN_TRIP_ID + "=" + tripId;
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            while(cursor.moveToNext()){
                Map<String, Object> data = new HashMap<>();
/*
                cursor.getInt(cursor.getColumnIndex(SensorContract.SensorEntry.COLUMN_DATE));
                data.put("trip_id", cursor.getInt(cursor.getColumnIndex(SensorContract.SensorEntry.COLUMN_TRIP_ID)));

                data.put("acc_x", cursor.getInt(cursor.getColumnIndex(SensorContract.SensorEntry.COLUMN_ACC_X)));
                data.put("acc_y", cursor.getInt(cursor.getColumnIndex(SensorContract.SensorEntry.COLUMN_ACC_Y)));
                data.put("acc_z", cursor.getString(cursor.getColumnIndex(SensorContract.SensorEntry.COLUMN_ACC_Z)));

                data.put("gyro_x", cursor.getString(cursor.getColumnIndex(SensorContract.SensorEntry.COLUMN_GYRO_X)));
                data.put("gyro_y", cursor.getDouble(cursor.getColumnIndex(SensorContract.SensorEntry.COLUMN_GYRO_Y)));
                data.put("gyro_z", cursor.getDouble(cursor.getColumnIndex(SensorContract.SensorEntry.COLUMN_GYRO_Z)));

                data.put("magno_x", cursor.getInt(cursor.getColumnIndex(SensorContract.SensorEntry.COLUMN_MAGNETO_X)));
                data.put("magno_y", cursor.getInt(cursor.getColumnIndex(SensorContract.SensorEntry.COLUMN_MAGNETO_Y)));
                data.put("magno_z", cursor.getInt(cursor.getColumnIndex(SensorContract.SensorEntry.COLUMN_MAGNETO_Z)));

                data.put("gps_lat", cursor.getInt(cursor.getColumnIndex(SensorContract.SensorEntry.COLUMN_GPS_LAT)));
                data.put("gps_long", cursor.getInt(cursor.getColumnIndex(SensorContract.SensorEntry.COLUMN_GPS_LONG)));

                data.put("speed", cursor.getInt(cursor.getColumnIndex(SensorContract.SensorEntry.COLUMN_SPEED)));
                data.put("classification", cursor.getInt(cursor.getColumnIndex(SensorContract.SensorEntry.COLUMN_CLASSIFICATION)));
*/


                //ADD USER ID TOO FOR FIRESTORE
                data.put("user_id", userId);



                allSensorData.add(data);
            }

            sensorDbHelper.closeDB(db);

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get sensor data from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return allSensorData;
    }

    public void closeDB(SQLiteDatabase db) {
        db = getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
