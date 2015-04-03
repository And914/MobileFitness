package it.polimi.jaa.mobilefitness;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.polimi.jaa.mobilefitness.utils.ExerciseInfo;
import it.polimi.jaa.mobilefitness.utils.Utils;

/**
 * Created by andre on 30/03/15.
 */
public class WodFragment extends ActionBarActivity{

    RecyclerView recyclerView;
    int idWod;
    private static final String LOG_ACTIVITY = "WodFragment";

    public WodFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wod);

        recyclerView = (RecyclerView) findViewById(R.id.card_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        idWod = getIntent().getExtras().getInt("id_wod");

        //TODO:FARE QUERY IN LOCALE PER PRENDERE ESERCIZI DEL WOD IDWOD
        //ExerciseCardAdapter exerciseCardAdapter = new ExerciseCardAdapter(createList(jsonArray));
        //recyclerView.setAdapter(exerciseCardAdapter);

    }

    private List<ExerciseInfo> createList(JSONArray jsonExercises) {

        List<ExerciseInfo> result = new ArrayList<ExerciseInfo>();

        for (int i = 0; i<jsonExercises.length();i++) {
            try {
                JSONObject exercise = jsonExercises.getJSONObject(i);

                ExerciseInfo ei = new ExerciseInfo("name " + exercise.getString("name"), "equipment " + exercise.getString("equipment"), "rounds " + exercise.getString("rounds"),
                        "reps " + exercise.getString("reps"), "rest " + exercise.getString("rest_time"), "weight " + exercise.getString("weight"), "time "
                        + exercise.getString("duration"), exercise.getInt("icon_id"));

                result.add(ei);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}