package it.polimi.jaa.mobilefitness.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by asna on 01/04/15.
 */
public class GymDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "mobile_fitness.db";

    public GymDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Set the queries to create the databases
        final String SQL_CREATE_BEACON_TABLE = "CREATE TABLE " + GymContract.BeaconEntry.TABLE_NAME + " (" +
                GymContract.BeaconEntry.COLUMN_ID_BEACON +" TEXT PRIMARY KEY, " +
                GymContract.BeaconEntry.COLUMN_ID_EQUIPMENT + " INTEGER NOT NULL " +
                " );";

        final String SQL_CREATE_WOD_TABLE = "CREATE TABLE " + GymContract.WodEntry.TABLE_NAME + " (" +
                GymContract.WodEntry.COLUMN_ID +" INTEGER PRIMARY KEY, " +
                GymContract.WodEntry.COLUMN_NAME + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_WOD_EXERC_TABLE = "CREATE TABLE " + GymContract.WodExerciseEntry.TABLE_NAME + " (" +
                GymContract.WodExerciseEntry.COLUMN_ID_WOD + " INTEGER, " +
                GymContract.WodExerciseEntry.COLUMN_ID_EXERC + " INTEGER, " +
                GymContract.WodExerciseEntry.COLUMN_DURATION + " INTEGER, " +
                GymContract.WodExerciseEntry.COLUMN_REPS + " INTEGER, " +
                GymContract.WodExerciseEntry.COLUMN_REST_TIME + " INTEGER, " +
                GymContract.WodExerciseEntry.COLUMN_ROUNDS+ " INTEGER, " +
                GymContract.WodExerciseEntry.COLUMN_WEIGHT + " INTEGER, " +
                "PRIMARY KEY(" + GymContract.WodExerciseEntry.COLUMN_ID_WOD + ", " +
                GymContract.WodExerciseEntry.COLUMN_ID_EXERC + ") " +
                " );";

        final String SQL_CREATE_EXERC_TABLE = "CREATE TABLE " + GymContract.ExerciseEntry.TABLE_NAME + " (" +
                GymContract.ExerciseEntry.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                GymContract.ExerciseEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                GymContract.ExerciseEntry.COLUMN_EQUIPMENT + " TEXT NOT NULL, " +
                GymContract.ExerciseEntry.COLUMN_ICON_ID + " INTEGER, " +
                GymContract.ExerciseEntry.COLUMN_PERSONAL_RECORD + " REAL " +
                " );";

        final String SQL_CREATE_HISTORY_TABLE = "CREATE TABLE " + GymContract.HistoryEntry.TABLE_NAME + " (" +
                GymContract.HistoryEntry.COLUMN_ID_EXERC + " INTEGER, " +
                GymContract.HistoryEntry.COLUMN_TIMESTAMP + " TEXT, " +
                GymContract.HistoryEntry.COLUMN_RESULT + " REAL NOT NULL, " +
                "PRIMARY KEY(" + GymContract.HistoryEntry.COLUMN_ID_EXERC + ", " +
                GymContract.HistoryEntry.COLUMN_TIMESTAMP + ") " +
                " );";



        //execute queries
        db.execSQL(SQL_CREATE_BEACON_TABLE);
        db.execSQL(SQL_CREATE_WOD_TABLE);
        db.execSQL(SQL_CREATE_EXERC_TABLE);
        db.execSQL(SQL_CREATE_WOD_EXERC_TABLE);
        db.execSQL(SQL_CREATE_HISTORY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //drop all the tables
        db.execSQL("DROP TABLE IF EXISTS " + GymContract.BeaconEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GymContract.WodEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GymContract.ExerciseEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GymContract.WodExerciseEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GymContract.HistoryEntry.TABLE_NAME);

        //rebuild all the tables
        onCreate(db);
    }
}