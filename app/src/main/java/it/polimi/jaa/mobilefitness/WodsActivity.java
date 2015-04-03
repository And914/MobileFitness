package it.polimi.jaa.mobilefitness;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.polimi.jaa.mobilefitness.utils.ExerciseInfo;
import it.polimi.jaa.mobilefitness.utils.Utils;
import it.polimi.jaa.mobilefitness.utils.WodInfo;

/**
 * Created by Jacopo on 02/04/2015.
 */
public class WodsActivity extends ActionBarActivity {

    RecyclerView recyclerView;

    private static final String LOG_ACTIVITY = "WodsActivity";

    public WodsActivity() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_wods);
        recyclerView = (RecyclerView) findViewById(R.id.wod_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        SharedPreferences mSharedPreferences = getSharedPreferences(Utils.PREFS, Context.MODE_PRIVATE);

        if(savedInstanceState == null) {
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
                                    WodCardAdapter wodCardAdapter = new WodCardAdapter(createList(jsonArray));
                                    recyclerView.setAdapter(wodCardAdapter);

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
        else{
            //TODO:gestire caricamento senza passare da internet
        }
    }



    private List<WodInfo> createList(JSONArray jsonExercises) {

        List<WodInfo> result = new ArrayList<WodInfo>();

        for (int i = 0; i<jsonExercises.length();i++) {
            try {
                JSONObject exercise = jsonExercises.getJSONObject(i);

                WodInfo wi = new WodInfo("name " + exercise.getString("wod_name"), "gym " + exercise.getString("gym_name"),exercise.getInt("id"));

                result.add(wi);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
