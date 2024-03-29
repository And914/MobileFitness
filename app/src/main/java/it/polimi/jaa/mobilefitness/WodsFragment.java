package it.polimi.jaa.mobilefitness;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
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
    private ProgressDialog dialog;

    private OnWodSelectedListener mListener;

    public WodsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = this.getActivity();
        dialog = new ProgressDialog(activity);
        dialog.setMessage("Fetching WODs...");
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_wods, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container_wods);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) layout.findViewById(R.id.wod_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        new FetchFromLocalDB().execute();

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
        void onWodSelected(String wodId);
    }

    private void setWodsInfoFromServer() {
        dialog.show();

        BackendFunctions.BFGetWods(new CallbackParseObjects() {
            @Override
            public void done(List<ParseObject> parseObjects) {
                if (parseObjects.size() > 0) {
                    WodCardAdapter wodCardAdapter = new WodCardAdapter(WodInfo.createList(parseObjects));
                    saveOnDB(parseObjects);

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    recyclerView.setAdapter(wodCardAdapter);
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
                            Log.e("WodsFragment", "DATABASE CURSOR NULL");
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

                            GregorianCalendar calendar = new GregorianCalendar();
                            calendar.setTime(wodEx.getCreatedAt());

                            String dateString = String.valueOf(calendar.get(Calendar.DATE)).concat("/")
                                    .concat(String.valueOf(calendar.get(Calendar.MONTH))).concat("/")
                                    .concat(String.valueOf(calendar.get(Calendar.YEAR)));

                            contentValues.put(GymContract.ExerciseEntry.COLUMN_CREATION_DATE, dateString);
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

    private class FetchFromLocalDB extends AsyncTask<Void, Void, List<WodInfo>> {


        @Override
        protected List<WodInfo> doInBackground(Void... params) {
            return WodInfo.createListFromCursor(getActivity());
        }


        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(List<WodInfo> wodInfos) {
            WodCardAdapter wodCardAdapter = new WodCardAdapter(wodInfos);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (wodCardAdapter.getItemCount()>0){
                recyclerView.setAdapter(wodCardAdapter);
            }
            else {
                setWodsInfoFromServer();
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


}
