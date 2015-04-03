package it.polimi.jaa.mobilefitness.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asna on 03/04/15.
 */
public class WodExerciseInfo extends ExerciseInfo {
    public final int id_wod;
    public final int id_exercise;
    public final String name_wod;

    public WodExerciseInfo(int id_wod, int id_exercise, String name_exercise, String name_wod, String equipment, String rounds, String rep, String rest, String weight, String time, int image) {
        super(name_exercise, equipment, rounds, rep, rest, weight, time, image);
        this.id_wod = id_wod;
        this.id_exercise = id_exercise;
        this.name_wod = name_wod;
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
                        exercise.getString("name_wod"),
                        exercise.getString("equipment"),
                        exercise.getString("rounds"),
                        exercise.getString("reps"),
                        exercise.getString("rest_time"),
                        exercise.getString("weight"),
                        exercise.getString("duration"),
                        exercise.getInt("icon_id"));

                result.add(wodExerciseInfo);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
