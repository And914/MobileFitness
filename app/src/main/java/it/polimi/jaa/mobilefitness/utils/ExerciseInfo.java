package it.polimi.jaa.mobilefitness.utils;

import android.app.Activity;
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
    public final int completed;
    public Boolean selected;

    public ExerciseInfo(String id, String name, String equipment, String rounds, String rep, String rest, String weight, String time, int image, int category, int completed) {
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
        this.completed = completed;
        this.selected = false;
    }

    public static List<ExerciseInfo> createListFromCursor(Activity activity, String[] args) {

        Cursor cursor = activity.getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI,
                new String[]{GymContract.ExerciseEntry.COLUMN_ID, GymContract.ExerciseEntry.COLUMN_NAME, GymContract.ExerciseEntry.COLUMN_EQUIPMENT,
                        GymContract.ExerciseEntry.COLUMN_ROUNDS, GymContract.ExerciseEntry.COLUMN_REPS, GymContract.ExerciseEntry.COLUMN_REST_TIME,
                        GymContract.ExerciseEntry.COLUMN_WEIGHT, GymContract.ExerciseEntry.COLUMN_DURATION, GymContract.ExerciseEntry.COLUMN_ICON_ID,
                        GymContract.ExerciseEntry.COLUMN_CATEGORY, GymContract.ExerciseEntry.COLUMN_COMPLETED
                },
                GymContract.ExerciseEntry.COLUMN_ID_WOD + " = ?",
                args,
                null
        );

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
            int completed = cursor.getInt(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_COMPLETED));

            ExerciseInfo exerciseInfo = new ExerciseInfo(id, exerciseName,equipment,rounds,reps,rest,weight,time,image, category,completed);
            if (!mArrayList.contains(exerciseInfo)){
                mArrayList.add(exerciseInfo);
            }
            cursor.moveToNext();
        }
        cursor.close();

        return mArrayList;
    }
}
