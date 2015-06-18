package it.polimi.jaa.mobilefitness.results;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it.polimi.jaa.mobilefitness.ExerciseCardAdapter;
import it.polimi.jaa.mobilefitness.R;
import it.polimi.jaa.mobilefitness.model.GymContract;
import it.polimi.jaa.mobilefitness.utils.ExerciseInfo;
import it.polimi.jaa.mobilefitness.utils.ResultsInfo;

/**
 * Created by andre on 30/03/15.
 */
public class ResultsUserFragment extends Fragment {

    RecyclerView recyclerView;

    public ResultsUserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_results, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.results_recycler_list);
        recyclerView.setHasFixedSize(true);

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
}
