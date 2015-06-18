package it.polimi.jaa.mobilefitness.utils;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import it.polimi.jaa.mobilefitness.model.GymContract;

/**
 * Created by Jacopo on 18/06/2015.
 */
public class ResultsInfo {
    public final String id;
    public final String name;
    public final String equipment;
    public final String date;
    public final int image;
    public final int category;
    public final String result;

    public ResultsInfo(String id, String name, String date, String equipment, int image, int category, String result) {
        this.id = id;
        this.name = name;
        this.equipment = equipment;
        this.image = image;
        this.date = date;
        this.category = category;
        this.result = result;
    }

    public static List<ResultsInfo> createListFromCursor(Cursor cursor, Context context) {
        ArrayList<ResultsInfo> mArrayList = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            // The Cursor is now set to the right position
            String exerciseId = cursor.getString(cursor.getColumnIndex(GymContract.HistoryEntry.COLUMN_ID_EXERC));

            String[] args = {exerciseId};

            Cursor cursorExercise = context.getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI, new String[]{GymContract.ExerciseEntry.COLUMN_NAME, GymContract.ExerciseEntry.COLUMN_EQUIPMENT, GymContract.ExerciseEntry.COLUMN_CATEGORY, GymContract.ExerciseEntry.COLUMN_ICON_ID},
                    GymContract.ExerciseEntry.COLUMN_ID + " = ?" , args, null);

            cursorExercise.moveToFirst();

            String exerciseName = cursorExercise.getString(cursorExercise.getColumnIndex(GymContract.ExerciseEntry.COLUMN_NAME));
            String equipment = cursorExercise.getString(cursorExercise.getColumnIndex(GymContract.ExerciseEntry.COLUMN_EQUIPMENT));
            int category = cursorExercise.getInt(cursorExercise.getColumnIndex(GymContract.ExerciseEntry.COLUMN_CATEGORY));
            int image = cursorExercise.getInt(cursorExercise.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ICON_ID));

            String date = cursor.getString(cursor.getColumnIndex(GymContract.HistoryEntry.COLUMN_TIMESTAMP));
            String result = cursor.getString(cursor.getColumnIndex(GymContract.HistoryEntry.COLUMN_RESULT));

            ResultsInfo wodInfo = new ResultsInfo(exerciseId,exerciseName,date,equipment,image,category,result);
            if (!mArrayList.contains(wodInfo)){
                mArrayList.add(wodInfo);
            }
            cursor.moveToNext();

            cursorExercise.close();
        }
        return mArrayList;
    }
}
