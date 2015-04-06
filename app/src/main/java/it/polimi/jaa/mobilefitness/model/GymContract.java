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

    public static final String PATH_BEACON = "beacon";
    public static final String PATH_EXERCISE = "exercise";
    public static final String PATH_EXERCISE_DELETED = "exercise_deleted";
    public static final String PATH_HISTORY = "history";


    public static final class BeaconEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BEACON).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BEACON;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BEACON;

        public static final String TABLE_NAME = "beacon";

        public static final String COLUMN_ID_BEACON = "id_beacon";

        public static final String COLUMN_ID_EXERCISE = "id_exercise";

        public static final String COLUMN_EQUIPMENT = "equipment";

        public static Uri buildLBeaconUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ExerciseEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXERCISE).build();

        public static final Uri CONTENT_URI_DELETED =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXERCISE_DELETED).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXERCISE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXERCISE;

        public static final String TABLE_NAME = "exercise";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ID_WOD = "id_wod";
        public static final String COLUMN_NAME_WOD = "wod_name";
        public static final String COLUMN_GYM_NAME = "gym_name";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_EQUIPMENT = "equipment";
        public static final String COLUMN_ICON_ID = "icon_id";
        public static final String COLUMN_ROUNDS = "rounds";
        public static final String COLUMN_REPS = "reps";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_REST_TIME = "rest_time";
        public static final String COLUMN_WEIGHT = "weight";

        public static final String COLUMN_DELETED = "deleted";


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

        public static final String COLUMN_ID_EXERC = "id_exerc";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_RESULT = "result";


        public static Uri buildHistoryrUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
