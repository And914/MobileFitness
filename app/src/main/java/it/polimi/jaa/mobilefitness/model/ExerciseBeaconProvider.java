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
 * Created by asna on 01/04/15.
 */
public class ExerciseBeaconProvider extends ContentProvider {
    static final int EXERCISE = 1;
    static final int EXERC_BEACON = 2;

    /*
    static final int WOD = 200;
    static final int EXERC = 300;
    static final int WOD_EXERC = 201;
    static final int HISTORY = 301;
    */

    private static final SQLiteQueryBuilder exerciseBeaconQueryBuilder;

    static{
        exerciseBeaconQueryBuilder = new SQLiteQueryBuilder();
        //beacon join excercise
        exerciseBeaconQueryBuilder.setTables(
                GymContract.BeaconEntry.TABLE_NAME + " INNER JOIN " +
                        GymContract.ExerciseEntry.TABLE_NAME + " ON " +
                        GymContract.BeaconEntry.TABLE_NAME + "." +
                        GymContract.BeaconEntry.COLUMN_ID_EQUIPMENT + "=" +
                        GymContract.ExerciseEntry.TABLE_NAME + "." +
                        GymContract.ExerciseEntry.COLUMN_ID);

    }

    private static final String selectionExerciseFromBeacon =
            GymContract.BeaconEntry.TABLE_NAME + "." +
            GymContract.BeaconEntry.COLUMN_ID_BEACON + "= ?";

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private GymDbHelper gymDbHelper;

    @Override
    public boolean onCreate() {
        gymDbHelper = new GymDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (uriMatcher.match(uri)){

            // exercise -> gives (exercise,beacon)
            case EXERCISE:
                retCursor = gymDbHelper.getReadableDatabase().query(
                        GymContract.BeaconEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case EXERC_BEACON:
                retCursor = getExerciseFromBeacon(uri,projection,sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getExerciseFromBeacon(Uri uri, String[] projection, String sortOrder){

        String[] arg = new String[] {uri.getPathSegments().get(1)};

        return exerciseBeaconQueryBuilder.query(
                gymDbHelper.getReadableDatabase(),
                projection,
                selectionExerciseFromBeacon,
                arg,
                null,
                null,
                sortOrder);
    }

    @Override
    public String getType(Uri uri) {

        final int match = uriMatcher.match(uri);

        switch (match){
            case EXERCISE:
                return GymContract.BeaconEntry.CONTENT_TYPE;

            case EXERC_BEACON:
                return GymContract.BeaconEntry.CONTENT_ITEM_TYPE;
            /*
            case EXERC:
                return GymContract.ExerciseEntry.CONTENT_TYPE;
            case WOD:
                return GymContract.WodEntry.CONTENT_TYPE;
            case HISTORY:
                return GymContract.HistoryEntry.CONTENT_TYPE;
            case WOD_EXERC:
                return GymContract.WodExerciseEntry.CONTENT_TYPE;
            */
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = gymDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case EXERCISE:
                long _id = db.insert(GymContract.BeaconEntry.TABLE_NAME, null, values);
                if (_id > 0){
                    returnUri = GymContract.BeaconEntry.buildLBeaconUri(_id);
                }
                else{
                    throw new SQLException("Fail to insert row into "+uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
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
            case EXERCISE:
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
        return 0;
    }

    static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = GymContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, GymContract.PATH_EXERCISE, EXERCISE);
        uriMatcher.addURI(authority, GymContract.PATH_EXERCISE + "/*", EXERC_BEACON);
        /*
        uriMatcher.addURI(authority, GymContract.PATH_EXERCISE, EXERC);
        //TODO :uriMatcher.addURI(authority, GymContract.PATH_EXERCISE+"/*", HISTORY);
        uriMatcher.addURI(authority, GymContract.PATH_WOD, WOD);
        //TODO: uriMatcher.addURI(authority, GymContract.PATH_WOD+"/*", WOD_EXERC);
        uriMatcher.addURI(authority, GymContract.PATH_HISTORY, HISTORY);
        uriMatcher.addURI(authority, GymContract.PATH_WODEXERCISE, WOD_EXERC);
        */
        return uriMatcher;
    }
}
