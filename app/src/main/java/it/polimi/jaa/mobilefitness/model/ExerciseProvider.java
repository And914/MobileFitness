package it.polimi.jaa.mobilefitness.model;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by asna on 01/04/15.
 */
public class ExerciseProvider extends ContentProvider {
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private GymDbHelper gymDbHelper;

    static final int EXERCISE = 1;

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
                        GymContract.ExerciseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match){
            case EXERCISE:
                return GymContract.ExerciseEntry.CONTENT_TYPE;
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
                long _id = db.insert(GymContract.ExerciseEntry.TABLE_NAME, null, values);
                if (_id > 0){
                    returnUri = GymContract.ExerciseEntry.buildExerciseUri(_id);
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
                rowsDeleted = db.delete(GymContract.ExerciseEntry.TABLE_NAME,selection, selectionArgs);
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


        return uriMatcher;
    }
}
