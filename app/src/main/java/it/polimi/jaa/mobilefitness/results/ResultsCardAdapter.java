package it.polimi.jaa.mobilefitness.results;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import it.polimi.jaa.mobilefitness.utils.ExerciseInfo;
import it.polimi.jaa.mobilefitness.R;
import it.polimi.jaa.mobilefitness.utils.ResultsInfo;

/**
 * Created by andre on 30/03/15.
 */
public class ResultsCardAdapter extends RecyclerView.Adapter<ResultsCardAdapter.ResultsViewHolder> {

    private List<ResultsInfo> resultInfoList;

    public ResultsCardAdapter(List<ResultsInfo> resultList) {
        this.resultInfoList = resultList;
    }


    @Override
    public int getItemCount() {
        return resultInfoList.size();
    }

    @Override
    public void onBindViewHolder(ResultsViewHolder exerciseViewHolder, int i) {
        ResultsInfo ex = resultInfoList.get(i);
        exerciseViewHolder.vName.setText(ex.name);
        exerciseViewHolder.vEquipment.setText(ex.equipment);
        exerciseViewHolder.vDate.setText(ex.date);
        exerciseViewHolder.vResult.setText(ex.result);
    }

    @Override
    public ResultsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.result_card, viewGroup, false);

        return new ResultsViewHolder(itemView);
    }

    public static class ResultsViewHolder extends RecyclerView.ViewHolder {

        TextView vName;
        TextView vEquipment;
        TextView vDate;
        TextView vResult;
        View view;

        public ResultsViewHolder(final View v) {
            super(v);
            view = v;
            vName = (TextView) v.findViewById(R.id.exercise_name_result);
            vEquipment = (TextView) v.findViewById(R.id.exercise_equipment_result);
            vDate = (TextView) v.findViewById(R.id.exercise_date_result);
            vResult = (TextView) v.findViewById(R.id.exercise_result_result);
        }
    }
}