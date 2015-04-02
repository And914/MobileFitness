package it.polimi.jaa.mobilefitness;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * Created by andre on 30/03/15.
 */
public class WodFragment extends Fragment{

    RecyclerView recyclerView;
    int idWod;
    private static final String PREFS = "prefs";
    private static final String PREF_EMAIL = "email";
    private static final String LOG_ACTIVITY = "WodFragment";

    public WodFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wod, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        //TODO: METTERE IL WOD DINAMICO.....AGGIUNGERE UNA PAGINA PRIMA IN CUI SELEZIONARLO...OPPURE FORZARLO A FARNE UNO E SOLO UNO
        SharedPreferences mSharedPreferences = this.getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        String urlServer = "http://192.168.1.187:80/wods/1/exercises";
        AsyncHttpClient client = new AsyncHttpClient();
        client.setProxy("192.168.1.187",80);
        //RequestParams requestParams = new RequestParams();
        //requestParams.put("email", mSharedPreferences.getString(PREF_EMAIL,""));


        client.get(urlServer,
                new TextHttpResponseHandler() {

                    @Override
                    public void onSuccess(int i, Header[] headers, String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            Log.d(LOG_ACTIVITY, jsonArray.toString());
                            if (jsonArray.length() > 0) {
                                //set the content of the recycler view
                                ExerciseCardAdapter exerciseCardAdapter = new ExerciseCardAdapter(createList(jsonArray));
                                recyclerView.setAdapter(exerciseCardAdapter);

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

        return rootView;
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