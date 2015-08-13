package it.polimi.jaa.mobilefitness.results;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

import it.polimi.jaa.mobilefitness.R;
import it.polimi.jaa.mobilefitness.WodCardAdapter;
import it.polimi.jaa.mobilefitness.backend.BackendFunctions;
import it.polimi.jaa.mobilefitness.backend.callbacks.CallbackParseObjects;
import it.polimi.jaa.mobilefitness.model.GymContract;
import it.polimi.jaa.mobilefitness.utils.ResultsInfo;
import it.polimi.jaa.mobilefitness.utils.Utils;
import it.polimi.jaa.mobilefitness.utils.WodInfo;

/**
 * Created by andre on 30/03/15.
 */
public class ResultsUserFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog dialog;

    public ResultsUserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_results, container, false);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Fetching Results...");

        recyclerView = (RecyclerView) rootView.findViewById(R.id.results_recycler_list);
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container_results);
        swipeRefreshLayout.setOnRefreshListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        new FetchFromLocalDB().execute();

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
        dialog.show ();
        getActivity().getContentResolver().delete(GymContract.HistoryEntry.CONTENT_URI, null, null);

        BackendFunctions.BFGetResults(new CallbackParseObjects() {
            @Override
            public void done(List<ParseObject> parseObjects) {
                for (ParseObject parseObject : parseObjects) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(GymContract.HistoryEntry.COLUMN_ID_EXERC,parseObject.getParseObject(Utils.PARSE_USERSEXERCISES_WODSEXERCISES).getObjectId());
                    contentValues.put(GymContract.HistoryEntry.COLUMN_TIMESTAMP, String.valueOf(parseObject.getCreatedAt()));
                    contentValues.put(GymContract.HistoryEntry.COLUMN_RESULT, parseObject.getInt(Utils.PARSE_USERSEXERCISES_RESULT));
                    getActivity().getContentResolver().insert(GymContract.HistoryEntry.CONTENT_URI, contentValues);
                }

                ResultsCardAdapter resultsCardAdapter = new ResultsCardAdapter(ResultsInfo.createListFromCursor(getActivity()));

                recyclerView.setAdapter(resultsCardAdapter);

                if (dialog.isShowing()) {
                   dialog.dismiss();
                }
            }

            @Override
            public void error(int error) {

            }
        });
    }

    private class FetchFromLocalDB extends AsyncTask<Void, Void, List<ResultsInfo>> {


        @Override
        protected List<ResultsInfo> doInBackground(Void... params) {
            return ResultsInfo.createListFromCursor(getActivity());
        }


        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(List<ResultsInfo> resultsInfos) {
            ResultsCardAdapter resultsCardAdapter = new ResultsCardAdapter(resultsInfos);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (resultsCardAdapter.getItemCount()>0){
                recyclerView.setAdapter(resultsCardAdapter);
            }
            else {
                setResultsFromServer();
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
