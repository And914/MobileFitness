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
public class WodExerciseInfo extends ExerciseInfo {
    public final int id_wod;
    public final int id_exercise;
    public final String wod_name;
    public final int category;
    public final String gym_name;

    public WodExerciseInfo(int id_wod, int id_exercise, String name_exercise, String name_wod, String equipment, String rounds, String rep, String rest, String weight, String time, int image,int category,String gym_name) {
        super(name_exercise, equipment, rounds, rep, rest, weight, time, image);
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
}
