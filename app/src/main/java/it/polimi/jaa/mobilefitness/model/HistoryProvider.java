package it.polimi.jaa.mobilefitness.model;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by asna on 02/04/15.
 */
public class HistoryProvider extends ContentProvider {
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private GymDbHelper gymDbHelper;

    static final int HISTORY = 1;
    static final int HISTORY_EXERCISE = 2;

    private static final SQLiteQueryBuilder historyExerciseQueryBuilder;

    static {
        historyExerciseQueryBuilder = new SQLiteQueryBuilder();

        // history join exercise
        historyExerciseQueryBuilder.setTables(
                GymContract.HistoryEntry.TABLE_NAME + " INNER JOIN " +
                        GymContract.ExerciseEntry.TABLE_NAME + " ON " +
                        GymContract.HistoryEntry.TABLE_NAME + "." +
                        GymContract.HistoryEntry.COLUMN_ID_EXERC + "=" +
                        GymContract.ExerciseEntry.TABLE_NAME + "." +
                        GymContract.ExerciseEntry.COLUMN_ID
        );
    }

    private static final String[] projectionAllExerciseDone = new String[]{
            "DISTINCT " + GymContract.ExerciseEntry.TABLE_NAME + "." +
                    GymContract.ExerciseEntry.COLUMN_NAME + ", " +
                    GymContract.ExerciseEntry.TABLE_NAME + "." +
                    GymContract.ExerciseEntry.COLUMN_EQUIPMENT + ", " +
                    GymContract.ExerciseEntry.TABLE_NAME + "." +
                    GymContract.ExerciseEntry.COLUMN_ICON_ID
    };

    private static final String selectionExerciseFromHistory =
            GymContract.HistoryEntry.TABLE_NAME + "." +
                    GymContract.HistoryEntry.COLUMN_ID_EXERC + "= ?";


    @Override
    public boolean onCreate() {
        gymDbHelper = new GymDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (uriMatcher.match(uri)){

            case HISTORY:
                retCursor = historyExerciseQueryBuilder.query(
                        gymDbHelper.getReadableDatabase(),
                        projectionAllExerciseDone,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case HISTORY_EXERCISE:
                retCursor = getExerciseFromHistory(uri,projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri "+uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getExerciseFromHistory(Uri uri, String[] projection, String sortOrder){
        String[] arg = new String[]{uri.getPathSegments().get(1)};

        return historyExerciseQueryBuilder.query(
                gymDbHelper.getReadableDatabase(),
                projection,
                selectionExerciseFromHistory,
                arg,
                null,
                null,
                sortOrder
        );

    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match){
            case HISTORY:
                return GymContract.HistoryEntry.CONTENT_TYPE;
            case HISTORY_EXERCISE:
                return GymContract.HistoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri :" +uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = gymDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case HISTORY:
                long _id = db.insert(GymContract.HistoryEntry.TABLE_NAME, null, values);
                if(_id>0){
                    returnUri = GymContract.HistoryEntry.buildHistoryrUri(_id);
                }
                else{
                    throw new SQLException("Fail to insert row into " +uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = gymDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;

        if(selection==null){
            selection = "1";
        }

        switch (match){
            case HISTORY:
                rowsDeleted = db.delete(GymContract.HistoryEntry.TABLE_NAME,selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }

        if(rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = GymContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, GymContract.PATH_HISTORY, HISTORY);
        uriMatcher.addURI(authority, GymContract.PATH_HISTORY + "/*", HISTORY_EXERCISE);

        return uriMatcher;
    }
}
