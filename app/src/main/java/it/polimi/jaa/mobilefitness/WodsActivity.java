package it.polimi.jaa.mobilefitness;

import android.app.FragmentTransaction;
import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import it.polimi.jaa.mobilefitness.backend.BackendFunctions;
import it.polimi.jaa.mobilefitness.backend.callbacks.CallbackParseObjects;
import it.polimi.jaa.mobilefitness.model.GymContract;
import it.polimi.jaa.mobilefitness.utils.ExerciseInfo;
import it.polimi.jaa.mobilefitness.utils.Utils;
import it.polimi.jaa.mobilefitness.utils.WodExerciseInfo;
import it.polimi.jaa.mobilefitness.utils.WodInfo;

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


        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
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
        WodFragment wodFragment = (WodFragment) getFragmentManager().findFragmentById(R.id.wod_fragment_container);
        if (wodFragment != null){
            wodFragment.setExercisesFromLocalDB();
        }
        else {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.wods_fragment_container, new WodFragment());
            fragmentTransaction.addToBackStack("wod_stack");
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
                WodFragment wodFragment = (WodFragment) getFragmentManager().findFragmentById(R.id.wod_fragment_container);
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
                WodFragment wodFragment = (WodFragment) getFragmentManager().findFragmentById(R.id.wod_fragment_container);
                if(wodFragment != null) {
                    getFragmentManager().findFragmentById(R.id.wod_fragment_container).startActivityForResult(intent, 0);
                }
                else {
                    getFragmentManager().findFragmentById(R.id.wods_fragment_container).startActivityForResult(intent, 0);
                }
            }
        }
    }
}
