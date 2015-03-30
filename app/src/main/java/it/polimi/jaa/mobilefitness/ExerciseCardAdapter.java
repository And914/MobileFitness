package it.polimi.jaa.mobilefitness;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import it.polimi.jaa.mobilefitness.utils.ExerciseInfo;

/**
 * Created by andre on 30/03/15.
 */
public class ExerciseCardAdapter extends RecyclerView.Adapter<ExerciseCardAdapter.ExerciseViewHolder> {

    private List<ExerciseInfo> exerciseInfoList;

    public ExerciseCardAdapter(List<ExerciseInfo> contactList) {
        this.exerciseInfoList = contactList;
    }


    @Override
    public int getItemCount() {
        return exerciseInfoList.size();
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder exerciseViewHolder, int i) {
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
    public ExerciseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.exercise_card, viewGroup, false);

        return new ExerciseViewHolder(itemView);
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {

        TextView vName;
        TextView vEquipment;
        TextView vRounds;
        TextView vRep;
        TextView vRest;
        TextView vWeight;
        TextView vTime;
        View view;


        public ExerciseViewHolder(final View v) {
            super(v);
            view = v;
            vName = (TextView) v.findViewById(R.id.ex_name);
            vEquipment = (TextView) v.findViewById(R.id.ex_equip);
            vRounds = (TextView) v.findViewById(R.id.ex_rounds);
            vRep = (TextView) v.findViewById(R.id.ex_reps);
            vRest = (TextView) v.findViewById(R.id.ex_rest);
            vWeight = (TextView) v.findViewById(R.id.ex_weight);
            vTime = (TextView) v.findViewById(R.id.ex_time);

            //TODO:gestire click card
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Sucaaaa "+vName.getText(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}