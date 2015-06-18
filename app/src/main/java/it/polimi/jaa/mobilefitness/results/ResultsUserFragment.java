package it.polimi.jaa.mobilefitness.results;

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
import it.polimi.jaa.mobilefitness.utils.ExerciseInfo;

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
        recyclerView = (RecyclerView) rootView.findViewById(R.id.results_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        //TODO:da togliere e mettere esericizi presi da db
        //ResultsCardAdapter resultsCardAdapter = new ResultsCardAdapter(createList(10));
        //recyclerView.setAdapter(resultsCardAdapter);

        return rootView;
    }
}
