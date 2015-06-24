package it.polimi.jaa.mobilefitness;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconManager;
import com.gimbal.android.BeaconSighting;
import com.gimbal.android.Communication;
import com.gimbal.android.CommunicationListener;
import com.gimbal.android.CommunicationManager;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Push;
import com.gimbal.android.Visit;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import it.polimi.jaa.mobilefitness.backend.BackendFunctions;
import it.polimi.jaa.mobilefitness.backend.callbacks.CallbackParseObject;
import it.polimi.jaa.mobilefitness.backend.callbacks.CallbackParseObjects;
import it.polimi.jaa.mobilefitness.model.GymContract;
import it.polimi.jaa.mobilefitness.utils.ExerciseInfo;
import it.polimi.jaa.mobilefitness.utils.Utils;

/**
 * Created by andre on 30/03/15.
 */
public class WodActivity extends ActionBarActivity implements WodFragment.OnFragmentInteractionListener,WodsFragment.OnFragmentInteractionListener {


    /*private SharedPreferences mSharedPreferences;
    private RecyclerView recyclerView;
    private String idWod;
    private ExerciseCardAdapter exerciseCardAdapter;
    private List<ExerciseInfo> exerciseCardList;

    private PlaceEventListener placeEventListener;
    private CommunicationListener communicationListener;
    private BeaconEventListener beaconSightingListener;
    private BeaconManager beaconManager;
    private Boolean beaconEntered = false;
    final List<String> beaconsList = new ArrayList<>();

    private static final String LOG_ACTIVITY = "WodFragment";*/

    public static Boolean canVibrate;

    public WodActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wod);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.wod_fragment_container, new WodFragment());
        //getFragmentManager().beginTransaction().replace(R.id.wod_fragment_container, new WodFragment()).commit();
        if(findViewById(R.id.wods_fragment_container) != null){
            fragmentTransaction.replace(R.id.wods_fragment_container, new WodsFragment());
        }
        fragmentTransaction.commit();


    }

    /*private void saveBeaconsOnDB(String exerciseId, String equipment, String beaconId){

        final ContentValues contentValues = new ContentValues();
        //Set all the entry as deleted
        String[] args = {beaconId};

        Cursor cursor = getContentResolver().query(GymContract.BeaconEntry.CONTENT_URI, null,
                GymContract.BeaconEntry.COLUMN_ID_BEACON + "= ?", args, null);

        contentValues.put(GymContract.BeaconEntry.COLUMN_ID_EXERCISE, exerciseId);
        contentValues.put(GymContract.BeaconEntry.COLUMN_EQUIPMENT, equipment);
        contentValues.put(GymContract.BeaconEntry.COLUMN_ID_BEACON, beaconId);

        if(cursor.getCount()==0){
            getContentResolver().insert(GymContract.BeaconEntry.CONTENT_URI, contentValues);
        }
        cursor.close();

    }

    private void setExercisesFromLocalDB() {


        String[] args = {String.valueOf(idWod)};
        Cursor cursor = getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI,
                new String[]{GymContract.ExerciseEntry.COLUMN_ID, GymContract.ExerciseEntry.COLUMN_NAME,GymContract.ExerciseEntry.COLUMN_EQUIPMENT,
                        GymContract.ExerciseEntry.COLUMN_ROUNDS,GymContract.ExerciseEntry.COLUMN_REPS,GymContract.ExerciseEntry.COLUMN_REST_TIME,
                        GymContract.ExerciseEntry.COLUMN_WEIGHT,GymContract.ExerciseEntry.COLUMN_DURATION,GymContract.ExerciseEntry.COLUMN_ICON_ID,
                        GymContract.ExerciseEntry.COLUMN_CATEGORY,GymContract.ExerciseEntry.COLUMN_COMPLETED
                },
                GymContract.ExerciseEntry.COLUMN_ID_WOD + " = ?",
                args ,
                null
        );

        exerciseCardList = ExerciseInfo.createListFromCursor(cursor);
        exerciseCardAdapter = new ExerciseCardAdapter(exerciseCardList);
        cursor.close();
        recyclerView.setAdapter(exerciseCardAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        canVibrate = true;

        if (requestCode == 0) {
            setExercisesFromLocalDB();
            int totalEx = exerciseCardList.size();
            int counterCompleted = 0;
            for (ExerciseInfo exerciseCard : exerciseCardList) {
                if (exerciseCard.completed == 1) {
                    counterCompleted++;
                }
            }

            if (counterCompleted == totalEx) {
                Toast.makeText(getApplicationContext(),"Complimenti! Per oggi hai finito!",Toast.LENGTH_SHORT).show();
                resetExercises();
                this.finish();
            }
        }
    }

    private void resetExercises() {
        String[] args = {String.valueOf(idWod)};
        Cursor cursor = getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI,
                new String[]{GymContract.ExerciseEntry.COLUMN_ID},
                GymContract.ExerciseEntry.COLUMN_ID_WOD + " = ?",
                args ,
                null
        );

        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {

            String[] args1 = {cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ID))};

            ContentValues contentValues = new ContentValues();
            contentValues.put(GymContract.ExerciseEntry.COLUMN_COMPLETED,0);

            this.getContentResolver().update(GymContract.ExerciseEntry.CONTENT_URI,
                    contentValues,
                    GymContract.ExerciseEntry.COLUMN_ID + " = ?",
                    args1);
            cursor.moveToNext();
        }

        cursor.close();
    }*/

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}