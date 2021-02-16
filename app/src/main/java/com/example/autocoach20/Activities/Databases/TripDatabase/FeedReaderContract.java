package com.example.autocoach20.Activities.Databases.TripDatabase;

import android.provider.BaseColumns;

/**
 * @author Zahraa Marafie
 * @version 2.0
 * @since AutoCoach V4.2
 */
public class FeedReaderContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    public FeedReaderContract() {
    }

    /* Inner class that defines the table contents of the sensor table */
    public static final class FeedEntry implements BaseColumns {

        public static final String COLUMN_ID = "id"; //Serial number ~ used for any table
        public static final String COLUMN_SYNC = "sync"; //Sync the data to the cloud ~ 0 or 1

        //TABLE USER
        public static final String TABLE_USER = "users";
        public static final String COLUMN_UID = "uid";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_AGE = "age";

        //TABLE SPEEDRECORD FOR TEMPORARY DEBUG USE
        public static final String TABLE_SPEEDRECORD = "speed_record";
        public static final String COLUMN_RECORD_ID = "srecord_id";
        public static final String COLUMN_SPEED = "capture_speed";
        public static final String COLUMN_TIMESTAMP = "capture_time";
        public static final String COLUMN_RASPI = "raspi_data";

        //TABLE TRIP
        public static final String TABLE_TRIP = "trip";
        public static final String COLUMN_TRIP_ID = "trip_id";
        public static final String COLUMN_USER_ID = "uid";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";

        //TABLE EVENT
        public static final String TABLE_EVENT = "event";
        public static final String COLUMN_EVENT_START_TIME = "event_start_time";
        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_EVENT_TYPE = "event_type";
        public static final String COLUMN_EVENT_RISK_LEVEL = "event_risk_level";
        public static final String COLUMN_SCORE = "score"; //pattern score and event score, and overall score
        public static final String COLUMN_EVENT_DURATION = "event_duration";
        public static final String COLUMN_WINDOW_ID = "window_id";
        public static final String COLUMN_EVENT_FILTERED_DATA = "filtered_data";
        public static final String COLUMN_EVENT_RAW_DATA = "raw_data";


        //TABLE RECOMMENDATION
        public static final String TABLE_RECOMMENDATION = "recommendation";
        public static final String COLUMN_RECOMMENDATION_ID = "recommendation_id";
        public static final String COLUMN_RECOMMENDATION_RISK_LEVEL = "recommendation_risk_level";

        //TABLE PATTERN WINDOW
        public static final String TABLE_PATTERN_WINDOW = "pattern_window";
        public static final String COLUMN_WINDOW_TIMESTAMP = "window_timestamp";
        public static final String COLUMN_PATTERN = "pattern";
        public static final String COLUMN_BEHAVIOR = "behavior";
    }
}