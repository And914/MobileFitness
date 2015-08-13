package it.polimi.jaa.mobilefitness.utils;

import android.app.Activity;
import android.database.Cursor;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import it.polimi.jaa.mobilefitness.model.GymContract;

/**
 * Created by Jacopo on 02/04/2015.
 */
public class WodInfo {
    public final String name;
    public final String gym;
    public final String id_wod;
    public final String date;

    public WodInfo(String name, String gym, String id_wod, String date) {
        this.name = name;
        this.gym = gym;
        this.id_wod = id_wod;
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WodInfo wodInfo = (WodInfo) o;

        return (this.id_wod.equals(wodInfo.id_wod)) && (this.gym.equals((wodInfo.gym))) && (this.name.equals(wodInfo.name));
    }



    public static List<WodInfo> createListFromCursor(Activity activity) {
        Cursor cursor =   activity.getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI, new String[]{GymContract.ExerciseEntry.COLUMN_NAME_WOD, GymContract.ExerciseEntry.COLUMN_GYM_NAME,GymContract.ExerciseEntry.COLUMN_ID_WOD,GymContract.ExerciseEntry.COLUMN_CREATION_DATE},
                null, null, null);
        ArrayList<WodInfo> mArrayList = new ArrayList<WodInfo>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            // The Cursor is now set to the right position
            String wodName = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_NAME_WOD));
            String gymName = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_GYM_NAME));
            String wodId = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ID_WOD));
            String date = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_CREATION_DATE));
            WodInfo wodInfo = new WodInfo(wodName,gymName,wodId, date);
            if (!mArrayList.contains(wodInfo)){
                mArrayList.add(wodInfo);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return mArrayList;
    }

    public static List<WodInfo> createList(List<ParseObject> wodsParseObjects){
        List<WodInfo> result = new ArrayList<WodInfo>();
        for(ParseObject wod: wodsParseObjects){
            ParseObject gym = wod.getParseObject(Utils.PARSE_WODS_GYM);
            try {
                gym.fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(wod.getCreatedAt());
            String dateString = String.valueOf(calendar.get(Calendar.DATE)).concat("/")
                    .concat(String.valueOf(calendar.get(Calendar.MONTH))).concat("/")
                    .concat(String.valueOf(calendar.get(Calendar.YEAR)));

            WodInfo wi = new WodInfo(wod.getString(Utils.PARSE_WODS_NAME),gym.getString(Utils.PARSE_GYMS_NAME),wod.getObjectId(), dateString);
            if (!result.contains(wi)){
                result.add(wi);
            }
        }
        return result;
    }
}

