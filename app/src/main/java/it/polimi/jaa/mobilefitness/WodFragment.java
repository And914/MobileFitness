package it.polimi.jaa.mobilefitness;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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

import it.polimi.jaa.mobilefitness.model.GymContract;
import it.polimi.jaa.mobilefitness.utils.ExerciseInfo;
import it.polimi.jaa.mobilefitness.utils.Utils;
import it.polimi.jaa.mobilefitness.utils.WodInfo;

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

        Cursor cursor = getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI,
                new String[]{GymContract.ExerciseEntry.COLUMN_NAME,GymContract.ExerciseEntry.COLUMN_EQUIPMENT,
                        GymContract.ExerciseEntry.COLUMN_ROUNDS,GymContract.ExerciseEntry.COLUMN_REPS,GymContract.ExerciseEntry.COLUMN_REST_TIME,
                        GymContract.ExerciseEntry.COLUMN_WEIGHT,GymContract.ExerciseEntry.COLUMN_DURATION,GymContract.ExerciseEntry.COLUMN_ICON_ID
                },
                "id_wod = "+ idWod, null, null);

        ExerciseCardAdapter exerciseCardAdapter = new ExerciseCardAdapter(ExerciseInfo.createList(cursor));
        recyclerView.setAdapter(exerciseCardAdapter);

    }


}