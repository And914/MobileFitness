package it.polimi.jaa.mobilefitness;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

import it.polimi.jaa.mobilefitness.backend.BackendFunctions;
import it.polimi.jaa.mobilefitness.backend.callbacks.CallbackParseObjects;
import it.polimi.jaa.mobilefitness.model.GymContract;
import it.polimi.jaa.mobilefitness.utils.Utils;
import it.polimi.jaa.mobilefitness.utils.WodInfo;


public class WodsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private LinearLayout layout;
    private FragmentActivity activity;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private OnWodSelectedListener mListener;

    public WodsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = (ActionBarActivity) this.getActivity();
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_wods, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container_wods);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) layout.findViewById(R.id.wod_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        Cursor cursor = activity.getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI, new String[]{GymContract.ExerciseEntry.COLUMN_NAME_WOD, GymContract.ExerciseEntry.COLUMN_GYM_NAME,GymContract.ExerciseEntry.COLUMN_ID_WOD},
                null, null, null);

        WodCardAdapter wodCardAdapter = new WodCardAdapter(WodInfo.createListFromCursor(cursor));
        cursor.close();
        if (wodCardAdapter.getItemCount()>0){
            recyclerView.setAdapter(wodCardAdapter);
        }
        else {
            setWodsInfoFromServer();
        }
        return layout;
    }

    public void onWodSelected(String wodId) {
        if (mListener != null) {
            mListener.onWodSelected(wodId);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnWodSelectedListener) activity;
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
    public interface OnWodSelectedListener {
        public void onWodSelected(String wodId);
    }

    private void setWodsInfoFromServer() {

        BackendFunctions.BFGetWods(new CallbackParseObjects() {
            @Override
            public void done(List<ParseObject> parseObjects) {
                if (parseObjects.size() > 0) {
                    WodCardAdapter wodCardAdapter = new WodCardAdapter(WodInfo.createList(parseObjects));
                    recyclerView.setAdapter(wodCardAdapter);
                    saveOnDB(parseObjects);
                }
            }

            @Override
            public void error(int error) {

            }
        });
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setWodsInfoFromServer();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    private void saveOnDB(List<ParseObject> parseObjects){
        final ContentValues contentValues = new ContentValues();
        //Set all the entry as deleted
        contentValues.put(GymContract.ExerciseEntry.COLUMN_DELETED, 1);
        activity.getContentResolver().update(GymContract.ExerciseEntry.CONTENT_URI,contentValues,null,null);
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
                        Cursor cursor = activity.getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI_DELETED, null,
                                GymContract.ExerciseEntry.COLUMN_ID_WOD + "= ? AND " + GymContract.ExerciseEntry.COLUMN_ID +"= ?", args, null);

                        //Handle error if cursor null
                        if (null == cursor) {
                            //Log.e(LOG_ACTIVITY, "DATABASE CURSOR NULL");
                            //Handle when entry already in the sqlite database
                        } else if (cursor.getCount() >= 1) {
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_DELETED,0);
                            activity.getContentResolver().update(GymContract.ExerciseEntry.CONTENT_URI,contentValues,GymContract.ExerciseEntry.COLUMN_ID_WOD + "= ? AND " + GymContract.ExerciseEntry.COLUMN_ID +"= ?",args);
                            cursor.close();
                            //Handle insert when no entry in the database found
                        } else {
                            ParseObject gym = wod.getParseObject(Utils.PARSE_WODS_GYM);
                            try {
                                gym.fetchIfNeeded();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            ParseObject equipment = exercise.getParseObject(Utils.PARSE_EXERCISES_EQUIPMENT);

                            try {
                                equipment.fetchIfNeeded();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            contentValues.put(GymContract.ExerciseEntry.COLUMN_ID_WOD, wod.getObjectId());
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_ID, wodEx.getObjectId());
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_NAME_WOD, wod.getString(Utils.PARSE_WODS_NAME));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_NAME, exercise.getString(Utils.PARSE_EXERCISES_NAME));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_EQUIPMENT, equipment.getString(Utils.PARSE_EQUIPMENT_NAME));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_ROUNDS, wodEx.getInt(Utils.PARSE_WODSEXERCISES_ROUNDS));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_REPS, wodEx.getInt(Utils.PARSE_WODSEXERCISES_REPS));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_GYM_NAME, gym.getString(Utils.PARSE_GYMS_NAME));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_REST_TIME, wodEx.getInt(Utils.PARSE_WODSEXERCISES_REST));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_WEIGHT, wodEx.getInt(Utils.PARSE_WODSEXERCISES_WEIGHT));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_DURATION, wodEx.getInt(Utils.PARSE_WODSEXERCISES_DURATION));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_ICON_ID, exercise.getInt(Utils.PARSE_EXERCISES_ICONID));
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_CATEGORY, wodEx.getInt(Utils.PARSE_WODSEXERCISES_CATEGORY));

                            activity.getContentResolver().insert(GymContract.ExerciseEntry.CONTENT_URI, contentValues);
                            cursor.close();
                        }
                    }
                }

                @Override
                public void error(int error) {

                }
            });
        }

    }


}
