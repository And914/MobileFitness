package it.polimi.jaa.mobilefitness.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Jacopo on 01/04/2015.
 */
public class GymContract {
    public static final String CONTENT_AUTHORITY = "it.polimi.jaa.mobilefitness";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_WOD = "wod";
    public static final String PATH_BEACON = "beacon";
    public static final String PATH_EXERCISE = "exercise";
    public static final String PATH_HISTORY = "history";
    public static final String PATH_WODEXERCISE = "wod_exercise";


    /* Inner class that defines the table contents of the location table */
    public static final class BeaconEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BEACON).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BEACON;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BEACON;

        // Table name
        public static final String TABLE_NAME = "beacon";

        // Human readable location string, provided by the API.  Because for styling,
        // "Mountain View" is more recognizable than 94043.
        public static final String COLUMN_ID_BEACON = "id_beacon";

        // In order to uniquely pinpoint the location on the map when we launch the
        // map intent, we store the latitude and longitude as returned by openweathermap.
        public static final String COLUMN_ID_EQUIPMENT = "id_equipment";

        public static Uri buildLBeaconUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the weather table */
    public static final class WodEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WOD).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WOD;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WOD;

        public static final String TABLE_NAME = "gyms";

        // Column with the foreign key into the location table.
        public static final String COLUMN_ID = "id";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_NAME = "name";

        public static Uri buildWodrUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ExerciseEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXERCISE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXERCISE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXERCISE;

        public static final String TABLE_NAME = "exercise";

        // Column with the foreign key into the location table.
        public static final String COLUMN_ID = "id";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_EQUIPMENT = "equipment";
        public static final String COLUMN_ICON_ID = "icon_id";
        public static final String COLUMN_PERSONAL_RECORD = "personal_record";


        public static Uri buildExerciseUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class HistoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HISTORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORY;

        public static final String TABLE_NAME = "history";

        // Column with the foreign key into the location table.
        public static final String COLUMN_ID_EXERC = "id_exerc";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_TIMESTAMP = "timestamp";

        public static Uri buildHistoryrUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        public static final String COLUMN_RESULT = "result";
    }

    public static final class WodExerciseEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WODEXERCISE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WODEXERCISE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WODEXERCISE;

        public static final String TABLE_NAME = "wod_exercise";

        // Column with the foreign key into the location table.
        public static final String COLUMN_ID_WOD = "id_wod";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_ID_EXERC = "id_exerc";

        public static final String COLUMN_ROUNDS = "rounds";
        public static final String COLUMN_REPS = "reps";

        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_REST_TIME = "rest_time";

        public static final String COLUMN_WEIGHT = "weight";


        public static Uri buildWodExerciseUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


}
