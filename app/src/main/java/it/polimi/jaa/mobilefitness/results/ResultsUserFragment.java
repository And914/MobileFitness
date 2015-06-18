package it.polimi.jaa.mobilefitness.results;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import it.polimi.jaa.mobilefitness.ExerciseCardAdapter;
import it.polimi.jaa.mobilefitness.R;
import it.polimi.jaa.mobilefitness.backend.BackendFunctions;
import it.polimi.jaa.mobilefitness.backend.callbacks.CallbackParseObjects;
import it.polimi.jaa.mobilefitness.model.GymContract;
import it.polimi.jaa.mobilefitness.utils.ExerciseInfo;
import it.polimi.jaa.mobilefitness.utils.ResultsInfo;
import it.polimi.jaa.mobilefitness.utils.Utils;

/**
 * Created by andre on 30/03/15.
 */
public class ResultsUserFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;


    public ResultsUserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_results, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.results_recycler_list);
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container_results);
        swipeRefreshLayout.setOnRefreshListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        Cursor cursor = getActivity().getContentResolver().query(GymContract.HistoryEntry.CONTENT_URI, new String[]{GymContract.HistoryEntry.COLUMN_ID_EXERC, GymContract.HistoryEntry.COLUMN_RESULT,GymContract.HistoryEntry.COLUMN_TIMESTAMP},
                null, null, null);

        ResultsCardAdapter resultsCardAdapter = new ResultsCardAdapter(ResultsInfo.createListFromCursor(cursor, getActivity().getApplicationContext()));
        cursor.close();

        recyclerView.setAdapter(resultsCardAdapter);

        return rootView;
    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setResultsFromServer();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    private void setResultsFromServer(){

        getActivity().getContentResolver().delete(GymContract.HistoryEntry.CONTENT_URI, null, null);

        BackendFunctions.BFGetResults(new CallbackParseObjects() {
            @Override
            public void done(List<ParseObject> parseObjects) {
                for (ParseObject parseObject : parseObjects) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(GymContract.HistoryEntry.COLUMN_ID_EXERC,parseObject.getParseObject("wods_exercises").getObjectId());
                    contentValues.put(GymContract.HistoryEntry.COLUMN_TIMESTAMP, String.valueOf(parseObject.getCreatedAt()));
                    contentValues.put(GymContract.HistoryEntry.COLUMN_RESULT, parseObject.getInt("result"));
                    getActivity().getContentResolver().insert(GymContract.HistoryEntry.CONTENT_URI, contentValues);
                }
                Cursor cursor = getActivity().getContentResolver().query(GymContract.HistoryEntry.CONTENT_URI, new String[]{GymContract.HistoryEntry.COLUMN_ID_EXERC, GymContract.HistoryEntry.COLUMN_RESULT,GymContract.HistoryEntry.COLUMN_TIMESTAMP},
                        null, null, null);

                ResultsCardAdapter resultsCardAdapter = new ResultsCardAdapter(ResultsInfo.createListFromCursor(cursor, getActivity().getApplicationContext()));
                cursor.close();

                recyclerView.setAdapter(resultsCardAdapter);
            }

            @Override
            public void error(int error) {

            }
        });
    }
}
