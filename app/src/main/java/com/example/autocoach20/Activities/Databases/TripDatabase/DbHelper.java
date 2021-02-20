package com.example.autocoach20.Activities.Databases.TripDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Zahraa Marafie
 * @modified Xuening Leng
 * @version 6.0
 * @since AutoCoach2.0
 */
public class DbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    // New in version 11: added filtered data
    public static final int DATABASE_VERSION = 14;
    public static final String DATABASE_NAME = "TripReader.db";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_TRIP);
        db.execSQL(SQL_CREATE_SPEEDRECORD);
        db.execSQL(SQL_CREATE_EVENT);
        db.execSQL(SQL_CREATE_PATTERN_WINDOW);
        db.execSQL(SQL_CREATE_RECOMMENDATION);
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_USER);
        db.execSQL(SQL_DELETE_TRIP);
        db.execSQL(SQL_DELETE_SPEEDRECORD);
        db.execSQL(SQL_DELETE_EVENT);
        db.execSQL(SQL_DELETE_PATTERN_WINDOW);
        db.execSQL(SQL_DELETE_RECOMMENDATION);

        onCreate(db);
    }


    public void closeDB(SQLiteDatabase db) {
        //db = getReadableDatabase();
        if (db != null || db.isOpen())
            db.close();
    }


    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    private static final String SQL_CREATE_USER =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_USER + " (" +
                    FeedReaderContract.FeedEntry.COLUMN_UID + " VARCHAR PRIMARY KEY, " +
                    FeedReaderContract.FeedEntry.COLUMN_FIRST_NAME + " TEXT, " +
                    FeedReaderContract.FeedEntry.COLUMN_LAST_NAME + " TEXT, " +
                    FeedReaderContract.FeedEntry.COLUMN_CREATED_AT + " LONG," +
                    FeedReaderContract.FeedEntry.COLUMN_GENDER + " INT, " +
                    FeedReaderContract.FeedEntry.COLUMN_AGE + " INT " +
                    ")";

    private static final String SQL_CREATE_TRIP =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_TRIP + " (" +
                    FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    FeedReaderContract.FeedEntry.COLUMN_USER_ID + " VARCHAR, " +
                    FeedReaderContract.FeedEntry.COLUMN_SCORE + " DOUBLE, " +
                    FeedReaderContract.FeedEntry.COLUMN_START_TIME + " LONG, " +
                    FeedReaderContract.FeedEntry.COLUMN_END_TIME + " LONG, " +
                    FeedReaderContract.FeedEntry.COLUMN_SYNC + " INTEGER, " +
                    "FOREIGN KEY (" + FeedReaderContract.FeedEntry.COLUMN_USER_ID + ") REFERENCES " + FeedReaderContract.FeedEntry.TABLE_USER + " (" + FeedReaderContract.FeedEntry.COLUMN_UID + ")" +
                    ")";

    private static final String SQL_CREATE_SPEEDRECORD =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_SPEEDRECORD+ " (" +
                    FeedReaderContract.FeedEntry.COLUMN_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    FeedReaderContract.FeedEntry.COLUMN_TRIP_ID+ " INTEGER, " +
                    FeedReaderContract.FeedEntry.COLUMN_SPEED + " INT, " +
                    FeedReaderContract.FeedEntry.COLUMN_TIMESTAMP + " TIMESTAMP, " +
                    FeedReaderContract.FeedEntry.COLUMN_RASPI + " VARCHAR, " +
                    FeedReaderContract.FeedEntry.COLUMN_GYRO+ " DOUBLE , " +
                    "FOREIGN KEY (" + FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + ") REFERENCES " + FeedReaderContract.FeedEntry.TABLE_TRIP + " (" + FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + ")" +
                    ")";
    private static final String SQL_CREATE_EVENT =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_EVENT + " (" +
                    FeedReaderContract.FeedEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + //For all trips
                    FeedReaderContract.FeedEntry.COLUMN_EVENT_ID + " INTEGER, " + //For this trip
                    FeedReaderContract.FeedEntry.COLUMN_EVENT_START_TIME + " INTEGER, " +
                    FeedReaderContract.FeedEntry.COLUMN_EVENT_TYPE + " TEXT, " +
                    FeedReaderContract.FeedEntry.COLUMN_EVENT_RISK_LEVEL + " TEXT, " +
                    FeedReaderContract.FeedEntry.COLUMN_SCORE + " DOUBLE, " +
                    FeedReaderContract.FeedEntry.COLUMN_EVENT_DURATION + " DOUBLE, " +
                    FeedReaderContract.FeedEntry.COLUMN_WINDOW_ID + " INTEGER, " +
                    FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + " INTEGER, " +
                    FeedReaderContract.FeedEntry.COLUMN_EVENT_RAW_DATA + " TEXT, " +
                    FeedReaderContract.FeedEntry.COLUMN_EVENT_FILTERED_DATA + " TEXT, " +
                    FeedReaderContract.FeedEntry.COLUMN_SYNC + " INTEGER, " +
                    "FOREIGN KEY (" + FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + ") REFERENCES " + FeedReaderContract.FeedEntry.TABLE_TRIP + " (" + FeedReaderContract.FeedEntry.COLUMN_ID + "), " +
                    "FOREIGN KEY (" + FeedReaderContract.FeedEntry.COLUMN_WINDOW_ID + ") REFERENCES " + FeedReaderContract.FeedEntry.TABLE_PATTERN_WINDOW + " (" + FeedReaderContract.FeedEntry.COLUMN_WINDOW_ID + ") " +
                    ")";


    private static final String SQL_CREATE_RECOMMENDATION =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_RECOMMENDATION + " (" +
                    FeedReaderContract.FeedEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + //For all trips
                    FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + " INTEGER, " +
                    FeedReaderContract.FeedEntry.COLUMN_RECOMMENDATION_ID + " INTEGER, " + //For this trip
                    FeedReaderContract.FeedEntry.COLUMN_EVENT_ID + " INTEGER, " +
                    FeedReaderContract.FeedEntry.COLUMN_RECOMMENDATION_RISK_LEVEL + " VARCHAR, " +
                    FeedReaderContract.FeedEntry.COLUMN_SYNC + " INTEGER, " +
                    "FOREIGN KEY (" + FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + ") REFERENCES " + FeedReaderContract.FeedEntry.TABLE_TRIP + " (" + FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + "), " +
                    "FOREIGN KEY (" + FeedReaderContract.FeedEntry.COLUMN_EVENT_ID + ") REFERENCES " + FeedReaderContract.FeedEntry.TABLE_EVENT + " (" + FeedReaderContract.FeedEntry.COLUMN_EVENT_ID + ") " +
                    ")";


    private static final String SQL_CREATE_PATTERN_WINDOW =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_PATTERN_WINDOW + " (" +
                    FeedReaderContract.FeedEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + //For all trips
                    FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + " INTEGER, " +
                    FeedReaderContract.FeedEntry.COLUMN_WINDOW_ID + " INTEGER, " +  //For this trip
                    FeedReaderContract.FeedEntry.COLUMN_PATTERN + " VARCHAR, " +
                    FeedReaderContract.FeedEntry.COLUMN_WINDOW_TIMESTAMP + " LONG, " +
                    FeedReaderContract.FeedEntry.COLUMN_SCORE + " DOUBLE, " +
                    FeedReaderContract.FeedEntry.COLUMN_BEHAVIOR + " VARCHAR, " +
                    FeedReaderContract.FeedEntry.COLUMN_SYNC + " INTEGER, " +
                    "FOREIGN KEY (" + FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + ") REFERENCES " + FeedReaderContract.FeedEntry.TABLE_TRIP + " (" + FeedReaderContract.FeedEntry.COLUMN_TRIP_ID + ") " +
                    ")";


    private static final String SQL_DELETE_PATTERN_WINDOW =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_PATTERN_WINDOW;

    private static final String SQL_DELETE_SPEEDRECORD =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_SPEEDRECORD;

    private static final String SQL_DELETE_EVENT =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_EVENT;

    private static final String SQL_DELETE_RECOMMENDATION =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_RECOMMENDATION;

    private static final String SQL_DELETE_USER =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_USER;

    private static final String SQL_DELETE_TRIP =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_TRIP;
}
