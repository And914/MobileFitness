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
public class GymProvider extends ContentProvider {
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private GymDbHelper gymDbHelper;

    static final int HISTORY = 1;
    static final int BEACON = 2;
    static final int EXERCISE = 3;
    static final int EXERCISE_DELETED = 4;


    private static final SQLiteQueryBuilder historyExerciseQueryBuilder;

    static {
        historyExerciseQueryBuilder = new SQLiteQueryBuilder();

        // history join exercise
        historyExerciseQueryBuilder.setTables(
                GymContract.HistoryEntry.TABLE_NAME + " JOIN " +
                        GymContract.ExerciseEntry.TABLE_NAME + " ON " +
                        GymContract.HistoryEntry.TABLE_NAME + "." +
                        GymContract.HistoryEntry.COLUMN_ID_EXERC + "=" +
                        GymContract.ExerciseEntry.TABLE_NAME + "." +
                        GymContract.ExerciseEntry.COLUMN_ID
        );
    }


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
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            // exercise -> gives (exercise,beacon)
            case BEACON:
                retCursor = gymDbHelper.getReadableDatabase().query(
                        GymContract.BeaconEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case EXERCISE:
                if (selection == null) {
                    selection = GymContract.ExerciseEntry.COLUMN_DELETED+"=0";
                }
                else {
                    selection = selection + " AND " + GymContract.ExerciseEntry.COLUMN_DELETED+"=0";
                }
                retCursor = gymDbHelper.getReadableDatabase().query(
                        GymContract.ExerciseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case EXERCISE_DELETED:
                if (selection == null) {
                    selection = GymContract.ExerciseEntry.COLUMN_DELETED+"=1";
                }
                else {
                    selection = selection + " AND " + GymContract.ExerciseEntry.COLUMN_DELETED+"=1";
                }
                retCursor = gymDbHelper.getReadableDatabase().query(
                        GymContract.ExerciseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri "+uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match){
            case HISTORY:
                return GymContract.HistoryEntry.CONTENT_TYPE;
            case EXERCISE:
                return GymContract.ExerciseEntry.CONTENT_TYPE;
            case BEACON:
                return GymContract.BeaconEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri :" +uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = gymDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        long _id;

        switch (match){
            case HISTORY:
                _id = db.insert(GymContract.HistoryEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = GymContract.HistoryEntry.buildHistoryrUri(_id);
                } else {
                    throw new SQLException("Fail to insert row into " + uri);
                }
                break;
            case EXERCISE:
                values.put(GymContract.ExerciseEntry.COLUMN_DELETED, 0);
                values.put(GymContract.ExerciseEntry.COLUMN_COMPLETED,0);
                _id = db.insert(GymContract.ExerciseEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = GymContract.ExerciseEntry.buildExerciseUri(_id);
                } else {
                    throw new SQLException("Fail to insert row into " + uri);
                }
                break;
            case EXERCISE_DELETED:
                throw new UnsupportedOperationException("Cannot insert an exercise already deleted");
            case BEACON:
                _id = db.insert(GymContract.BeaconEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = GymContract.BeaconEntry.buildLBeaconUri(_id);
                } else {
                    throw new SQLException("Fail to insert row into " + uri);
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
            case EXERCISE_DELETED:
                rowsDeleted = db.delete(GymContract.ExerciseEntry.TABLE_NAME,selection, selectionArgs);
                break;
            case EXERCISE:
                rowsDeleted = db.delete(GymContract.ExerciseEntry.TABLE_NAME,selection, selectionArgs);
                break;
            case BEACON:
                rowsDeleted = db.delete(GymContract.BeaconEntry.TABLE_NAME,selection, selectionArgs);
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
        final SQLiteDatabase db = gymDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);

        int rowsUpdated;
        switch (match) {
            case HISTORY:
                rowsUpdated = db.update(GymContract.HistoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BEACON:
                rowsUpdated = db.update(GymContract.BeaconEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case EXERCISE:
                rowsUpdated = db.update(GymContract.ExerciseEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case EXERCISE_DELETED:
                rowsUpdated = db.update(GymContract.ExerciseEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = GymContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, GymContract.PATH_HISTORY, HISTORY);
        uriMatcher.addURI(authority, GymContract.PATH_EXERCISE, EXERCISE);
        uriMatcher.addURI(authority,GymContract.PATH_EXERCISE_DELETED, EXERCISE_DELETED);
        uriMatcher.addURI(authority, GymContract.PATH_BEACON, BEACON);

        return uriMatcher;
    }
}
