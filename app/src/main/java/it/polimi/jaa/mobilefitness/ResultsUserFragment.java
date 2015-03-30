package it.polimi.jaa.mobilefitness;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by andre on 30/03/15.
 */
public class ResultsUserFragment extends Fragment {
    public ResultsUserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_results, container, false);
        return rootView;
    }
}
