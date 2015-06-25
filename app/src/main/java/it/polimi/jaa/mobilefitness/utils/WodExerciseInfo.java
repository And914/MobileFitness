package it.polimi.jaa.mobilefitness.utils;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.polimi.jaa.mobilefitness.model.GymContract;

/**
 * Created by asna on 03/04/15.
 */
public class WodExerciseInfo {
    public final int id_wod;
    public final int id_exercise;
    public final String wod_name;
    public final int category;
    public final String gym_name;

    public WodExerciseInfo(int id_wod, int id_exercise, String name_exercise, String name_wod, String equipment, String rounds, String rep, String rest, String weight, String time, int image,int category,String gym_name) {
        //super(name_exercise, equipment, rounds, rep, rest, weight, time, image, category);
        this.id_wod = id_wod;
        this.id_exercise = id_exercise;
        this.wod_name = name_wod;
        this.category = category;
        this.gym_name = gym_name;
    }

    public static List<WodExerciseInfo> createListFromJSON(JSONArray jsonExercises) {

        List<WodExerciseInfo> result = new ArrayList<WodExerciseInfo>();

        for (int i = 0; i<jsonExercises.length();i++) {
            try {
                JSONObject exercise = jsonExercises.getJSONObject(i);

                WodExerciseInfo wodExerciseInfo = new WodExerciseInfo(
                        exercise.getInt("id_wod"),
                        exercise.getInt("id_exercise"),
                        exercise.getString("name_exercise"),
                        exercise.getString("wod_name"),
                        exercise.getString("equipment"),
                        exercise.getString("rounds"),
                        exercise.getString("reps"),
                        exercise.getString("rest_time"),
                        exercise.getString("weight"),
                        exercise.getString("duration"),
                        exercise.getInt("icon_id"),
                        exercise.getInt("category"),
                        exercise.getString("gym_name"));


                result.add(wodExerciseInfo);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }



    public static List<WodExerciseInfo> createWodExerciseListFromCursor(Cursor cursor) {

        ArrayList<WodExerciseInfo> mArrayList = new ArrayList<WodExerciseInfo>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            // The Cursor is now set to the right position
            int idWod = cursor.getInt(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ID_WOD));
            int idExercise = cursor.getInt(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ID));
            String exerciseName = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_NAME));
            String equipment = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_EQUIPMENT));
            String wodName = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_NAME_WOD));
            String rounds = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ROUNDS));
            String reps = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_REPS));
            String rest = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_REST_TIME));
            String weight = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_WEIGHT));
            String time = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_DURATION));
            int image = cursor.getInt(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ICON_ID));
            int category = cursor.getInt(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_CATEGORY));
            String gymName = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_GYM_NAME));

            WodExerciseInfo exerciseInfo = new WodExerciseInfo(idWod,idExercise,exerciseName,wodName,equipment,rounds,reps,rest,weight,time,image,category,gymName);
            if (!mArrayList.contains(exerciseInfo)){
                mArrayList.add(exerciseInfo);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return mArrayList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WodExerciseInfo wodExerciseInfo = (WodExerciseInfo) o;

        return (this.id_wod == wodExerciseInfo.id_wod) && (this.id_exercise == wodExerciseInfo.id_exercise);
    }

    @Override
    public int hashCode() {
        int result = id_wod;
        result = 31 * result + id_exercise;
        result = 31 * result + (wod_name != null ? wod_name.hashCode() : 0);
        result = 31 * result + category;
        result = 31 * result + (gym_name != null ? gym_name.hashCode() : 0);
        return result;
    }
}
