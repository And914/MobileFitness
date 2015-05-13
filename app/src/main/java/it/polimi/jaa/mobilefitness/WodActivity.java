package it.polimi.jaa.mobilefitness;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconManager;
import com.gimbal.android.BeaconSighting;
import com.gimbal.android.Communication;
import com.gimbal.android.CommunicationListener;
import com.gimbal.android.CommunicationManager;
import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Push;
import com.gimbal.android.Visit;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.parse.ParseException;
import com.parse.ParseObject;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

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
import it.polimi.jaa.mobilefitness.utils.WodExerciseInfo;
import it.polimi.jaa.mobilefitness.utils.WodInfo;

/**
 * Created by andre on 30/03/15.
 */
public class WodActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String idWod;
    private ExerciseCardAdapter exerciseCardAdapter;
    private List<ExerciseInfo> exerciseCardList;

    private PlaceEventListener placeEventListener;
    private CommunicationListener communicationListener;
    private BeaconEventListener beaconSightingListener;
    private BeaconManager beaconManager;
    private Boolean beaconEntered = false;
    final List<String> beaconsList = new ArrayList<>();

    private static final String LOG_ACTIVITY = "WodFragment";

    public WodActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wod);

        idWod = getIntent().getExtras().getString("id_wod");

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_wod);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.card_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        BackendFunctions.BFGetGym(new CallbackParseObject() {
            @Override
            public void done(final ParseObject parseObject) {
                BackendFunctions.BFGetGymBeacons(parseObject, new CallbackParseObjects() {
                    @Override
                    public void done(List<ParseObject> parseObjects) {
                        for (ParseObject parseObject : parseObjects) {
                            ParseObject equipment = parseObject.getParseObject(Utils.PARSE_GYMSBEACONS_EQUIPMENT);
                            try {
                                equipment.fetchIfNeeded();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            beaconsList.add(parseObject.getString(Utils.PARSE_GYMSBEACONS_BEACON));

                            String[] args = {String.valueOf(idWod)};
                            Cursor cursor = getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI,
                                    new String[]{GymContract.ExerciseEntry.COLUMN_ID,GymContract.ExerciseEntry.COLUMN_EQUIPMENT},
                                    GymContract.ExerciseEntry.COLUMN_ID_WOD + " = ?",
                                    args ,
                                    null
                            );

                            cursor.moveToFirst();
                            Boolean present = false;
                            String exerciseId = "";
                            while(!cursor.isAfterLast()) {
                                if (equipment.getString(Utils.PARSE_EQUIPMENT_NAME).equals(cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_EQUIPMENT)))) {
                                    present = true;
                                    exerciseId = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ID));
                                    break;
                                }
                                cursor.moveToNext();
                            }
                            cursor.close();

                            if (present) {
                                saveBeaconsOnDB(exerciseId,equipment.getString(Utils.PARSE_EQUIPMENT_NAME),parseObject.getString(Utils.PARSE_GYMSBEACONS_BEACON));
                            }
                        }

                    }

                    @Override
                    public void error(int error) {
                        Log.e("app", getString(error));
                    }
                });
            }

            @Override
            public void error(int error) {
                Log.e("app", getString(error));
            }
        });

        setExercisesFromLocalDB();

        placeEventListener = new PlaceEventListener() {
            @Override
            public void onVisitStart(Visit visit) {
                // This will be invoked when a place is entered. Example below shows a simple log upon enter
                Log.i("Info:", "Enter: " + visit.getPlace().getName() + ", at: " + new Date(visit.getArrivalTimeInMillis()));
            }

            @Override
            public void onVisitEnd(Visit visit) {
                // This will be invoked when a place is exited. Example below shows a simple log upon exit
                Log.i("Info:", "Exit: " + visit.getPlace().getName() + ", at: " + new Date(visit.getDepartureTimeInMillis()));
            }
        };
        PlaceManager.getInstance().addListener(placeEventListener);


        communicationListener = new CommunicationListener() {
            @Override
            public Collection<Communication> presentNotificationForCommunications(Collection<Communication> communications, Visit visit) {
                for (Communication comm : communications) {
                    Log.i("INFO", "Place Communication: " + visit.getPlace().getName() + ", message: " + comm.getTitle());
                }
                //allow Gimbal to show the notification for all communications
                return communications;
            }

            @Override
            public Collection<Communication> presentNotificationForCommunications(Collection<Communication> communications, Push push) {
                for (Communication comm : communications) {
                    Log.i("INFO", "Received a Push Communication with message: " + comm.getTitle());
                }
                //allow Gimbal to show the notification for all communications
                return communications;
            }

            @Override
            public void onNotificationClicked(List<Communication> communications) {
                Log.i("INFO", "Notification was clicked on");
            }
        };
        CommunicationManager.getInstance().addListener(communicationListener);

        beaconSightingListener = new BeaconEventListener() {

            @Override
            public void onBeaconSighting(BeaconSighting sighting) {


                if (beaconsList.contains(sighting.getBeacon().getIdentifier())) {
                    handleBeaconEnter(sighting);

                }
            }

            private void handleBeaconEnter(BeaconSighting sighting) {
                if (sighting.getRSSI() > -40 && !beaconEntered) {
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(750);
                    beaconEntered = true;
                    int i = 0;

                    String[] args = {sighting.getBeacon().getIdentifier()};
                    Cursor cursor = getContentResolver().query(GymContract.BeaconEntry.CONTENT_URI,
                            new String[]{GymContract.BeaconEntry.COLUMN_ID_EXERCISE},
                            GymContract.BeaconEntry.COLUMN_ID_BEACON + " = ?",
                            args,
                            null
                    );
                    cursor.moveToFirst();
                    String idExercise = cursor.getString(cursor.getColumnIndex(GymContract.BeaconEntry.COLUMN_ID_EXERCISE));
                    for(ExerciseInfo ei : exerciseCardList) {
                        if (ei.id.equals(idExercise)) {
                            break;
                        }
                        i++;
                    }
                    cursor.close();

                    final RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                    viewHolder.itemView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            beaconEntered = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.itemView.setBackgroundColor(getResources().getColor(android.R.color.background_light));
                                }
                            });
                        }
                    });
                    thread.start();
                }
            }


        };
        beaconManager = new BeaconManager();
        beaconManager.addListener(beaconSightingListener);

        PlaceManager.getInstance().startMonitoring();
        beaconManager.startListening();
        CommunicationManager.getInstance().startReceivingCommunications();

    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setExercisesInfoFromServer();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    private void setExercisesInfoFromServer() {
        SharedPreferences mSharedPreferences = getSharedPreferences(Utils.PREFS, Context.MODE_PRIVATE);

        BackendFunctions.BFGetWods(new CallbackParseObjects() {
            @Override
            public void done(List<ParseObject> parseObjects) {
                if (parseObjects.size() > 0) {
                    //WodCardAdapter wodCardAdapter = new WodCardAdapter(WodInfo.createList(parseObjects));
                    //recyclerView.setAdapter(wodCardAdapter);
                    saveExercisesOnDB(parseObjects);

                }
            }

            @Override
            public void error(int error) {

            }
        });
    }

    private void saveExercisesOnDB(List<ParseObject> parseObjects){

        final ContentValues contentValues = new ContentValues();
        //Set all the entry as deleted
        getContentResolver().update(GymContract.ExerciseEntry.CONTENT_URI, null, null, null);
        for (final ParseObject wod : parseObjects){
            BackendFunctions.BFGetExercisesWod(wod.getObjectId(), new CallbackParseObjects() {
                @Override
                public void done(List<ParseObject> wodExercises) {
                    for(ParseObject wodEx: wodExercises){
                        ParseObject exercise = wodEx.getParseObject(Utils.PARSE_WODSEXERCISES_EXERCISE);
                        try {
                            exercise.fetchIfNeeded();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //Get the exercises that matches the ones downloaded
                        String[] args = {wod.getObjectId(),String.valueOf(wodEx.getObjectId())};
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
                            ParseObject gym  = wod.getParseObject(Utils.PARSE_WODS_GYM);
                            try {
                                gym.fetchIfNeeded();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_ID_WOD, wod.getObjectId());
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_ID, wodEx.getObjectId());
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_NAME_WOD, wod.getString(Utils.PARSE_WODS_NAME));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_NAME, exercise.getString(Utils.PARSE_EXERCISES_NAME));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_EQUIPMENT, exercise.getString(Utils.PARSE_EXERCISES_EQUIPMENT));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_ROUNDS, wodEx.getInt(Utils.PARSE_WODSEXERCISES_ROUNDS));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_REPS, wodEx.getInt(Utils.PARSE_WODSEXERCISES_REPS));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_GYM_NAME, gym.getString(Utils.PARSE_GYMS_NAME));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_REST_TIME, wodEx.getInt(Utils.PARSE_WODSEXERCISES_REST));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_WEIGHT, wodEx.getInt(Utils.PARSE_WODSEXERCISES_WEIGHT));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_DURATION, wodEx.getInt(Utils.PARSE_WODSEXERCISES_DURATION));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_ICON_ID, exercise.getInt(Utils.PARSE_EXERCISES_ICONID));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_CATEGORY, wodEx.getInt(Utils.PARSE_WODSEXERCISES_CATEGORY));

                            getContentResolver().insert(GymContract.ExerciseEntry.CONTENT_URI, contentValues);
                            cursor.close();
                        }
                    }
                    setExercisesFromLocalDB();
                }

                @Override
                public void error(int error) {

                }
            });
        }
    }

    private void saveBeaconsOnDB(String exerciseId, String equipment, String beaconId){

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
                        GymContract.ExerciseEntry.COLUMN_WEIGHT,GymContract.ExerciseEntry.COLUMN_DURATION,GymContract.ExerciseEntry.COLUMN_ICON_ID, GymContract.ExerciseEntry.COLUMN_CATEGORY
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


}