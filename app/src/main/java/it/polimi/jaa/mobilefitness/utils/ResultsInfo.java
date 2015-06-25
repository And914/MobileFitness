package it.polimi.jaa.mobilefitness.utils;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import it.polimi.jaa.mobilefitness.model.GymContract;

/**
 * Created by Jacopo on 18/06/2015.
 */
public class ResultsInfo implements Comparable {
    public final String id;
    public final String name;
    public final String equipment;
    public final String date;
    public final int image;
    public final int category;
    public final String result;
    public Boolean firstDate;
    public String dateTitle;

    public ResultsInfo(String id, String name, String date, String equipment, int image, int category, String result) {
        this.id = id;
        this.name = name;
        this.equipment = equipment;
        this.image = image;
        this.date = date;
        this.category = category;
        this.result = result;
        this.firstDate = false;
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

        Collections.sort(mArrayList);

        Date oldDate = null;
        Date currentDate = null;

        for (ResultsInfo ri : mArrayList) {
            String stringDate = ri.date.replace("CEST ","");
            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
            try {
                currentDate = format.parse(stringDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(currentDate);
            int date1 = Integer.parseInt(String.valueOf(calendar.get(Calendar.YEAR))
                    .concat(String.valueOf(calendar.get(Calendar.MONTH)))
                    .concat(String.valueOf(calendar.get(Calendar.DATE))));

            String dateString = String.valueOf(calendar.get(Calendar.DATE)).concat("/")
                    .concat(String.valueOf(calendar.get(Calendar.MONTH))).concat("/")
                    .concat(String.valueOf(calendar.get(Calendar.YEAR)));

            if (oldDate != null) {
                calendar.setTime(oldDate);
                int date2 = Integer.parseInt(String.valueOf(calendar.get(Calendar.YEAR))
                        .concat(String.valueOf(calendar.get(Calendar.MONTH)))
                        .concat(String.valueOf(calendar.get(Calendar.DATE))));

                if (date1 < date2) {

                    ri.dateTitle = dateString;
                    ri.firstDate = true;
                }
            }
            else {

                ri.firstDate = true;
                ri.dateTitle = dateString;
            }

            oldDate = currentDate;
        }

        return mArrayList;
    }


    @Override
    public int compareTo(@NonNull Object another) {
        ResultsInfo resultsInfoOther = (ResultsInfo) another;
        try {
            String stringDate1 = resultsInfoOther.date.replace("CEST ","");
            String stringDate2 = this.date.replace("CEST ","");
            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
            Date date1 = format.parse(stringDate1);
            Date date2 = format.parse(stringDate2);
            return date1.compareTo(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
