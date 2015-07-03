package it.polimi.jaa.mobilefitness;


import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.slf4j.helpers.Util;

import it.polimi.jaa.mobilefitness.utils.ExerciseInfo;
import it.polimi.jaa.mobilefitness.utils.Utils;

/**
 * Created by Jacopo on 02/04/2015.
 */
public class WodsActivity extends AppCompatActivity implements WodsFragment.OnWodSelectedListener,WodFragment.OnExerciseSelectedListener {

    private SharedPreferences mSharedPreferences;
    private Boolean wodDetails;

    public WodsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wods);

        mSharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_APP, MODE_PRIVATE);
        wodDetails = mSharedPreferences.getBoolean("wodDetails", false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(findViewById(R.id.wod_fragment_container) != null){
            fragmentTransaction.replace(R.id.wod_fragment_container, new WodFragment());
            fragmentTransaction.replace(R.id.wods_fragment_container, new WodsFragment());

        }
        else {
            if (wodDetails) {
                fragmentTransaction.replace(R.id.wods_fragment_container, new WodFragment());
            }
            else {
                fragmentTransaction.replace(R.id.wods_fragment_container, new WodsFragment());
            }
        }
        fragmentTransaction.commit();
    }



    @Override
    public void onWodSelected(String wodId) {
        SharedPreferences mSharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_APP, Context.MODE_PRIVATE);
        mSharedPreferences.edit().putString(Utils.SHARED_PREFERENCES_ID_WOD, wodId).apply();

        if (findViewById(R.id.wod_fragment_container) != null){
            WodFragment wodsFragment = (WodFragment) getSupportFragmentManager().findFragmentById(R.id.wod_fragment_container);
            wodsFragment.setExercisesFromLocalDB(wodId);
        }
        else {
            mSharedPreferences.edit().putBoolean("wodDetails",true).apply();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.wods_fragment_container, new WodFragment());
            fragmentTransaction.addToBackStack("tag");
            fragmentTransaction.commit();

        }
    }

    @Override
    public void onExerciseSelected(ExerciseInfo exerciseInfo) {

        SharedPreferences mSharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_APP, Context.MODE_PRIVATE);
        mSharedPreferences.edit().putString(Utils.SHARED_PREFERENCES_ID_EXERCISE, exerciseInfo.id).apply();

        if (exerciseInfo.completed == 0 && exerciseInfo.selected) {
            //if cardio
            if (exerciseInfo.category == 1) {
                Intent intent = new Intent(this,ExerciseCardioActivity.class);
                WodFragment wodFragment = (WodFragment) getSupportFragmentManager().findFragmentById(R.id.wod_fragment_container);
                if(wodFragment != null) {
                    wodFragment.startActivityForResult(intent, 0);
                    wodFragment.setMenuVisibility(false);
                }
                else {
                    getSupportFragmentManager().findFragmentById(R.id.wods_fragment_container).startActivityForResult(intent, 0);
                    getSupportFragmentManager().findFragmentById(R.id.wods_fragment_container).setMenuVisibility(false);

                }
            }
            //if strength
            else if (exerciseInfo.category == 2) {
                Intent intent = new Intent(this,ExerciseStrengthActivity.class);
                WodFragment wodFragment = (WodFragment) getSupportFragmentManager().findFragmentById(R.id.wod_fragment_container);
                if(wodFragment != null) {
                    wodFragment.startActivityForResult(intent, 0);
                    wodFragment.setMenuVisibility(false);
                }
                else {
                    getSupportFragmentManager().findFragmentById(R.id.wods_fragment_container).startActivityForResult(intent, 0);
                    getSupportFragmentManager().findFragmentById(R.id.wods_fragment_container).setMenuVisibility(false);
                }
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                wodDetails = mSharedPreferences.getBoolean("wodDetails", false);
                if (wodDetails) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.wods_fragment_container, new WodsFragment());
                    fragmentTransaction.commit();
                    mSharedPreferences.edit().putBoolean("wodDetails",false).apply();
                }
                else {
                    finish();
                }
        }
        return super.onOptionsItemSelected(item);
    }

}
