package it.polimi.jaa.mobilefitness;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import it.polimi.jaa.mobilefitness.model.GymContract;
import it.polimi.jaa.mobilefitness.utils.Utils;
import it.polimi.jaa.mobilefitness.utils.WodExerciseInfo;
import it.polimi.jaa.mobilefitness.utils.WodInfo;

/**
 * Created by Jacopo on 02/04/2015.
 */
public class WodsActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    private static final String LOG_ACTIVITY = "WodsActivity";

    public WodsActivity() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wods);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_wods);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.wod_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        Cursor cursor = getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI, new String[]{GymContract.ExerciseEntry.COLUMN_NAME_WOD, GymContract.ExerciseEntry.COLUMN_GYM_NAME,GymContract.ExerciseEntry.COLUMN_ID_WOD},
                null, null, null);

        WodCardAdapter wodCardAdapter = new WodCardAdapter(WodInfo.createListFromCursor(cursor));
        cursor.close();
        if (wodCardAdapter.getItemCount()>0){
            recyclerView.setAdapter(wodCardAdapter);
        }
        else {
            setWodsInfoFromServer();
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                setWodsInfoFromServer();
                swipeRefreshLayout.setRefreshing(false);
            }
        },1000);
    }

    private void setWodsInfoFromServer() {
        SharedPreferences mSharedPreferences = getSharedPreferences(Utils.PREFS, Context.MODE_PRIVATE);

        String urlServer = Utils.server_ip + "/wods/user/" + mSharedPreferences.getString(Utils.PREF_EMAIL, "");
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(urlServer,
                new TextHttpResponseHandler() {

                    @Override
                    public void onSuccess(int i, Header[] headers, String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            Log.d(LOG_ACTIVITY, jsonArray.toString());
                            if (jsonArray.length() > 0) {
                                //set the content of the recycler view
                                WodCardAdapter wodCardAdapter = new WodCardAdapter(WodInfo.createList(jsonArray));
                                recyclerView.setAdapter(wodCardAdapter);

                                saveOnDB(jsonArray);

                            } else {
                                Log.e(LOG_ACTIVITY, jsonArray.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                        Log.e(LOG_ACTIVITY, statusCode + throwable.getMessage());
                    }
                });
    }

    private void saveOnDB(JSONArray jsonArray){
        List<WodExerciseInfo> wodExerciseInfos = WodExerciseInfo.createListFromJSON(jsonArray);
        ContentValues contentValues;

        //Set all the entry as deleted
        getContentResolver().update(GymContract.ExerciseEntry.CONTENT_URI,null,null,null);


        for(WodExerciseInfo we: wodExerciseInfos){

            //Get the exercises that matches the ones downloaded
            String[] args = {String.valueOf(we.id_wod),String.valueOf(we.id_exercise)};
            Cursor cursor = getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI_DELETED, null,
                    GymContract.ExerciseEntry.COLUMN_ID_WOD + "= ? AND " + GymContract.ExerciseEntry.COLUMN_ID +"= ?", args, null);

            //Handle error if cursor null
            if (null == cursor) {
                Log.e(LOG_ACTIVITY,"DATABASE CURSOR NULL");

            //Handle when entry already in the sqlite database
            } else if (cursor.getCount() >= 1) {

                getContentResolver().update(GymContract.ExerciseEntry.CONTENT_URI_DELETED,null,GymContract.ExerciseEntry.COLUMN_ID_WOD + "= ? AND " + GymContract.ExerciseEntry.COLUMN_ID +"= ?",args);
                cursor.close();

            //Handle insert when no entry in the database found
            } else {
                contentValues = new ContentValues();

                contentValues.put(GymContract.ExerciseEntry.COLUMN_ID_WOD, we.id_wod);
                contentValues.put(GymContract.ExerciseEntry.COLUMN_ID, we.id_exercise);
                contentValues.put(GymContract.ExerciseEntry.COLUMN_NAME_WOD, we.wod_name);
                contentValues.put(GymContract.ExerciseEntry.COLUMN_NAME, we.name);
                contentValues.put(GymContract.ExerciseEntry.COLUMN_EQUIPMENT, we.equipment);
                contentValues.put(GymContract.ExerciseEntry.COLUMN_ROUNDS, we.rounds);
                contentValues.put(GymContract.ExerciseEntry.COLUMN_REPS, we.rep);
                contentValues.put(GymContract.ExerciseEntry.COLUMN_GYM_NAME, we.gym_name);
                contentValues.put(GymContract.ExerciseEntry.COLUMN_REST_TIME, we.rest);
                contentValues.put(GymContract.ExerciseEntry.COLUMN_WEIGHT, we.weight);
                contentValues.put(GymContract.ExerciseEntry.COLUMN_DURATION, we.time);
                contentValues.put(GymContract.ExerciseEntry.COLUMN_ICON_ID, we.image);
                contentValues.put(GymContract.ExerciseEntry.COLUMN_CATEGORY, we.category);

                getContentResolver().insert(GymContract.ExerciseEntry.CONTENT_URI, contentValues);
                cursor.close();
            }
        }
    }
}
