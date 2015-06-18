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

/**
 * Created by andre on 30/03/15.
 */
public class ResultsCardAdapter extends RecyclerView.Adapter<ResultsCardAdapter.ResultsViewHolder> {

    private List<ExerciseInfo> exerciseInfoList;

    public ResultsCardAdapter(List<ExerciseInfo> contactList) {
        this.exerciseInfoList = contactList;
    }


    @Override
    public int getItemCount() {
        return exerciseInfoList.size();
    }

    @Override
    public void onBindViewHolder(ResultsViewHolder exerciseViewHolder, int i) {
        ExerciseInfo ex = exerciseInfoList.get(i);
        exerciseViewHolder.vName.setText(ex.name);
        exerciseViewHolder.vEquipment.setText(ex.equipment);
        exerciseViewHolder.vRep.setText(ex.rep);
        exerciseViewHolder.vRest.setText(ex.rest);
        exerciseViewHolder.vRounds.setText(ex.rounds);
        exerciseViewHolder.vWeight.setText(ex.weight);
        exerciseViewHolder.vTime.setText(ex.time);
    }

    @Override
    public ResultsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.exercise_card, viewGroup, false);

        return new ResultsViewHolder(itemView);
    }

    public static class ResultsViewHolder extends RecyclerView.ViewHolder {

        TextView vName;
        TextView vEquipment;
        TextView vRounds;
        TextView vRep;
        TextView vRest;
        TextView vWeight;
        TextView vTime;
        View view;


        public ResultsViewHolder(final View v) {
            super(v);
            view = v;
            vName = (TextView) v.findViewById(R.id.ex_name);
            vEquipment = (TextView) v.findViewById(R.id.ex_equip);
            vRounds = (TextView) v.findViewById(R.id.ex_rounds);
            vRep = (TextView) v.findViewById(R.id.ex_reps);
            vRest = (TextView) v.findViewById(R.id.ex_rest);
            vWeight = (TextView) v.findViewById(R.id.ex_weight);
            vTime = (TextView) v.findViewById(R.id.ex_time);
        }
    }
}