package it.polimi.jaa.mobilefitness;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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


public class WodFragment extends Fragment {

    private SharedPreferences mSharedPreferences;
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

    FragmentActivity faActivity = null;

    private static final String LOG_ACTIVITY = "WodFragment";

    private LinearLayout llLayout;

    private OnExerciseSelectedListener mListener;


    public WodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        faActivity = super.getActivity();

        llLayout = (LinearLayout) inflater.inflate(R.layout.fragment_wod, container, false);

        mSharedPreferences = faActivity.getSharedPreferences(Utils.SHARED_PREFERENCES_APP, Context.MODE_PRIVATE);

        idWod = mSharedPreferences.getString(Utils.SHARED_PREFERENCES_ID_WOD, "");

        recyclerView = (RecyclerView) llLayout.findViewById(R.id.card_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(faActivity);
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
                            Cursor cursor = faActivity.getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI,
                                    new String[]{GymContract.ExerciseEntry.COLUMN_ID, GymContract.ExerciseEntry.COLUMN_EQUIPMENT},
                                    GymContract.ExerciseEntry.COLUMN_ID_WOD + " = ?",
                                    args,
                                    null
                            );

                            cursor.moveToFirst();
                            Boolean present = false;
                            String exerciseId = "";
                            while (!cursor.isAfterLast()) {
                                if (equipment.getString(Utils.PARSE_EQUIPMENT_NAME).equals(cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_EQUIPMENT)))) {
                                    present = true;
                                    exerciseId = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ID));
                                    break;
                                }
                                cursor.moveToNext();
                            }
                            cursor.close();

                            if (present) {
                                saveBeaconsOnDB(exerciseId, equipment.getString(Utils.PARSE_EQUIPMENT_NAME), parseObject.getString(Utils.PARSE_GYMSBEACONS_BEACON));
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

        int totalEx = exerciseCardList.size();
        int counterCompleted = 0;
        for (ExerciseInfo exerciseCard : exerciseCardList) {
            if (exerciseCard.completed == 1) {
                counterCompleted++;
            }
        }

        if (counterCompleted == totalEx) {
            Toast.makeText(faActivity.getApplicationContext(), "Complimenti! Per oggi hai finito!", Toast.LENGTH_SHORT).show();
            resetExercises();
            faActivity.finish();
        }

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
                if (sighting.getRSSI() > -40 && !beaconEntered && isAdded()) {
                    beaconEntered = true;
                    int i = 0;

                    String[] args = {sighting.getBeacon().getIdentifier()};
                    Cursor cursor = faActivity.getContentResolver().query(GymContract.BeaconEntry.CONTENT_URI,
                            new String[]{GymContract.BeaconEntry.COLUMN_ID_EXERCISE},
                            GymContract.BeaconEntry.COLUMN_ID_BEACON + " = ?",
                            args,
                            null
                    );
                    cursor.moveToFirst();
                    if(cursor.getCount() == 0) {
                        beaconEntered = false;
                        return;
                    }
                    boolean found = false;
                    String idExercise = cursor.getString(cursor.getColumnIndex(GymContract.BeaconEntry.COLUMN_ID_EXERCISE));
                    for(ExerciseInfo ei : exerciseCardList) {
                        if (ei.id.equals(idExercise)) {
                            found = true;
                            break;
                        }
                        i++;
                    }
                    cursor.close();
                    if(!found) {
                        beaconEntered = false;
                        return;
                    }

                    final ExerciseInfo exerciseInfo = exerciseCardList.get(i);

                    if(exerciseInfo.completed == 0) {


                        final RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);

                        if (viewHolder != null) {

                            Vibrator vibrator = (Vibrator) faActivity.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(750);
                            viewHolder.itemView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

                            exerciseInfo.selected = true;
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(5000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    beaconEntered = false;
                                    faActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(isAdded()) {
                                                viewHolder.itemView.setBackgroundColor(getResources().getColor(android.R.color.background_light));
                                                exerciseInfo.selected = false;
                                            }
                                        }
                                    });
                                }
                            });
                            thread.start();
                        }
                    }

                }
            }


        };
        beaconManager = new BeaconManager();
        beaconManager.addListener(beaconSightingListener);

        PlaceManager.getInstance().startMonitoring();
        beaconManager.startListening();
        CommunicationManager.getInstance().startReceivingCommunications();
        return llLayout;
    }

    public void onExerciseSelected(ExerciseInfo exerciseInfo) {
        if (mListener != null) {
            mListener.onExerciseSelected(exerciseInfo);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnExerciseSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnExerciseSelectedListener {
        void onExerciseSelected(ExerciseInfo exerciseInfo);
    }

    private void saveBeaconsOnDB(String exerciseId, String equipment, String beaconId){

        final ContentValues contentValues = new ContentValues();
        //Set all the entry as deleted
        String[] args = {beaconId};

        Cursor cursor = faActivity.getContentResolver().query(GymContract.BeaconEntry.CONTENT_URI, null,
                GymContract.BeaconEntry.COLUMN_ID_BEACON + "= ?", args, null);

        contentValues.put(GymContract.BeaconEntry.COLUMN_ID_EXERCISE, exerciseId);
        contentValues.put(GymContract.BeaconEntry.COLUMN_EQUIPMENT, equipment);
        contentValues.put(GymContract.BeaconEntry.COLUMN_ID_BEACON, beaconId);

        if(cursor.getCount()==0){
            faActivity.getContentResolver().insert(GymContract.BeaconEntry.CONTENT_URI, contentValues);
        }
        cursor.close();

    }

    protected void setExercisesFromLocalDB() {


        String[] args = {String.valueOf(idWod)};
        Cursor cursor = faActivity.getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI,
                new String[]{GymContract.ExerciseEntry.COLUMN_ID, GymContract.ExerciseEntry.COLUMN_NAME, GymContract.ExerciseEntry.COLUMN_EQUIPMENT,
                        GymContract.ExerciseEntry.COLUMN_ROUNDS, GymContract.ExerciseEntry.COLUMN_REPS, GymContract.ExerciseEntry.COLUMN_REST_TIME,
                        GymContract.ExerciseEntry.COLUMN_WEIGHT, GymContract.ExerciseEntry.COLUMN_DURATION, GymContract.ExerciseEntry.COLUMN_ICON_ID,
                        GymContract.ExerciseEntry.COLUMN_CATEGORY, GymContract.ExerciseEntry.COLUMN_COMPLETED
                },
                GymContract.ExerciseEntry.COLUMN_ID_WOD + " = ?",
                args,
                null
        );

        exerciseCardList = ExerciseInfo.createListFromCursor(cursor);
        exerciseCardAdapter = new ExerciseCardAdapter(exerciseCardList);
        cursor.close();
        recyclerView.setAdapter(exerciseCardAdapter);
    }

    private void resetExercises() {
        String[] args = {String.valueOf(idWod)};
        Cursor cursor = faActivity.getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI,
                new String[]{GymContract.ExerciseEntry.COLUMN_ID},
                GymContract.ExerciseEntry.COLUMN_ID_WOD + " = ?",
                args,
                null
        );

        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {

            String[] args1 = {cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ID))};

            ContentValues contentValues = new ContentValues();
            contentValues.put(GymContract.ExerciseEntry.COLUMN_COMPLETED, 0);

            faActivity.getContentResolver().update(GymContract.ExerciseEntry.CONTENT_URI,
                    contentValues,
                    GymContract.ExerciseEntry.COLUMN_ID + " = ?",
                    args1);
            cursor.moveToNext();
        }

        cursor.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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
                Toast.makeText(getActivity().getApplicationContext(),"Complimenti! Per oggi hai finito!",Toast.LENGTH_SHORT).show();
                resetExercises();
                }
            }
    }
}
