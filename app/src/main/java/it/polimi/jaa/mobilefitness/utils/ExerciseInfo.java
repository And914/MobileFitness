package it.polimi.jaa.mobilefitness.utils;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import it.polimi.jaa.mobilefitness.model.GymContract;

/**
 * Created by andre on 30/03/15.
 */
public class ExerciseInfo {
    public final String id;
    public final String name;
    public final String equipment;
    public final String rounds;
    public final String rep;
    public final String rest;
    public final String weight;
    public final String time;
    public final int image;
    public final int category;

    public ExerciseInfo(String id, String name, String equipment, String rounds, String rep, String rest, String weight, String time, int image, int category) {
        this.id = id;
        this.name = name;
        this.equipment = equipment;
        this.rounds = rounds;
        this.rep = rep;
        this.rest = rest;
        this.weight = weight;
        this.time = time;
        this.image = image;
        this.category = category;
    }

    public static List<ExerciseInfo> createListFromCursor(Cursor cursor) {

        ArrayList<ExerciseInfo> mArrayList = new ArrayList<ExerciseInfo>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            // The Cursor is now set to the right position
            String id = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ID));
            String exerciseName = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_NAME));
            String equipment = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_EQUIPMENT));
            String rounds = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ROUNDS));
            String reps = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_REPS));
            String rest = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_REST_TIME));
            String weight = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_WEIGHT));
            String time = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_DURATION));
            int image = cursor.getInt(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ICON_ID));
            int category = cursor.getInt(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_CATEGORY));

            ExerciseInfo exerciseInfo = new ExerciseInfo(id, exerciseName,equipment,rounds,reps,rest,weight,time,image, category);
            if (!mArrayList.contains(exerciseInfo)){
                mArrayList.add(exerciseInfo);
            }
            cursor.moveToNext();
        }
        return mArrayList;
    }
}
