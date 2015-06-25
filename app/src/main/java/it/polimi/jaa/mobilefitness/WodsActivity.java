package it.polimi.jaa.mobilefitness;


import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import it.polimi.jaa.mobilefitness.utils.ExerciseInfo;
import it.polimi.jaa.mobilefitness.utils.Utils;

/**
 * Created by Jacopo on 02/04/2015.
 */
public class WodsActivity extends ActionBarActivity implements WodsFragment.OnWodSelectedListener,WodFragment.OnExerciseSelectedListener {

    public WodsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wods);


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.wods_fragment_container, new WodsFragment());
        if(findViewById(R.id.wod_fragment_container) != null){
            fragmentTransaction.replace(R.id.wod_fragment_container, new WodFragment());
        }
        fragmentTransaction.commit();
    }



    @Override
    public void onWodSelected(String wodId) {
        SharedPreferences mSharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_APP, Context.MODE_PRIVATE);
        mSharedPreferences.edit().putString(Utils.SHARED_PREFERENCES_ID_WOD, wodId).apply();

        if (findViewById(R.id.wod_fragment_container) != null){
            WodFragment wodsFragment = (WodFragment) getSupportFragmentManager().findFragmentById(R.id.wod_fragment_container);
            wodsFragment.setExercisesFromLocalDB();
        }
        else {
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
                    getFragmentManager().findFragmentById(R.id.wod_fragment_container).startActivityForResult(intent, 0);
                }
                else {
                    getFragmentManager().findFragmentById(R.id.wods_fragment_container).startActivityForResult(intent, 0);
                }
            }
            //if strength
            else if (exerciseInfo.category == 2) {
                Intent intent = new Intent(this,ExerciseStrengthActivity.class);
                WodFragment wodFragment = (WodFragment) getSupportFragmentManager().findFragmentById(R.id.wod_fragment_container);
                if(wodFragment != null) {
                    getFragmentManager().findFragmentById(R.id.wod_fragment_container).startActivityForResult(intent, 0);
                }
                else {
                    getFragmentManager().findFragmentById(R.id.wods_fragment_container).startActivityForResult(intent, 0);
                }
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
