package it.polimi.jaa.mobilefitness.utils;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.polimi.jaa.mobilefitness.model.GymContract;

/**
 * Created by Jacopo on 02/04/2015.
 */
public class WodInfo {
    public final String name;
    public final String gym;
    public final int id_wod;

    public WodInfo(String name, String gym, int id_wod) {
        this.name = name;
        this.gym = gym;
        this.id_wod = id_wod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WodInfo wodInfo = (WodInfo) o;

        return (this.id_wod == wodInfo.id_wod) && (this.gym.equals((wodInfo.gym))) && (this.name.equals(wodInfo.name));
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (gym != null ? gym.hashCode() : 0);
        result = 31 * result + id_wod;
        return result;
    }

    public static List<WodInfo> createListFromCursor(Cursor cursor) {
        ArrayList<WodInfo> mArrayList = new ArrayList<WodInfo>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            // The Cursor is now set to the right position
            String wodName = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_NAME_WOD));
            String gymName = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_GYM_NAME));
            int wodId = cursor.getInt(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ID_WOD));
            WodInfo wodInfo = new WodInfo(wodName,gymName,wodId);
            if (!mArrayList.contains(wodInfo)){
                mArrayList.add(wodInfo);
            }
            cursor.moveToNext();
        }
        return mArrayList;
    }

    public static List<WodInfo> createList(JSONArray jsonExercises) {

        List<WodInfo> result = new ArrayList<WodInfo>();

        for (int i = 0; i<jsonExercises.length();i++) {
            try {
                JSONObject exercise = jsonExercises.getJSONObject(i);

                WodInfo wi = new WodInfo("name " + exercise.getString("wod_name"), "gym " + exercise.getString("gym_name"),exercise.getInt("id_wod"));
                if (!result.contains(wi)){
                    result.add(wi);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}

