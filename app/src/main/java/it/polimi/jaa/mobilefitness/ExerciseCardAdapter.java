package it.polimi.jaa.mobilefitness;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import it.polimi.jaa.mobilefitness.utils.ExerciseInfo;
import it.polimi.jaa.mobilefitness.utils.Utils;

/**
 * Created by andre on 30/03/15.
 */
public class ExerciseCardAdapter extends RecyclerView.Adapter<ExerciseCardAdapter.ExerciseViewHolder> {

    private static List<ExerciseInfo> exerciseInfoList;

    public ExerciseCardAdapter(List<ExerciseInfo> contactList) {
        exerciseInfoList = contactList;
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
        if(ex.rep.equals("0"))
            exerciseViewHolder.vRep.setText("-");
        else
            exerciseViewHolder.vRep.setText(ex.rep);

        if(ex.rest.equals("0"))
            exerciseViewHolder.vRest.setText("-");
        else
            exerciseViewHolder.vRest.setText(ex.rest);

        if(ex.rounds.equals("0"))
            exerciseViewHolder.vRounds.setText("-");
        else
            exerciseViewHolder.vRounds.setText(ex.rounds);

        if(ex.weight.equals("0"))
            exerciseViewHolder.vWeight.setText("-");
        else
            exerciseViewHolder.vWeight.setText(ex.weight);

        if(ex.time.equals("0"))
            exerciseViewHolder.vTime.setText("-");
        else
            exerciseViewHolder.vTime.setText(ex.time);

        if(ex.completed == 1) {
            exerciseViewHolder.itemView.setBackgroundResource(android.R.color.holo_red_dark);
        }

        /*
        if(ex.time.equals("") || ex.time.equals("null"))
            exerciseViewHolder.vTime.setText("-");
        else
            exerciseViewHolder.vTime.setText(ex.time);
            */

        exerciseViewHolder.vImage.setImageResource(findIcon(ex.image));
        exerciseViewHolder.vCategory.setImageResource(findCategory(ex.category));
    }

    private int findIcon(int ex){
        switch(ex){
            case 1:
                return R.drawable.cyclette;
            case 2:
                return R.drawable.rowergometer;
            case 3:
                return R.drawable.free_weights_icon;
            case 4:
                return R.drawable.accessories_icon;
            case 5:
                return R.drawable.treadmill;
            case 6:
                return R.drawable.step;
            default:
                return R.drawable.strength_icon;
        }
    }

    private int findCategory(int category){
        switch (category){
            case 1:
                return R.drawable.cardio_icon;
            case 2:
                return R.drawable.strength_icon;
            default:
                return R.drawable.strength_icon;
        }
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
        ImageView vImage;
        ImageView vCategory;
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
            vImage = (ImageView) v.findViewById(R.id.ex_image);
            vCategory = (ImageView) v.findViewById(R.id.ex_category);

            //TODO:gestire click card
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExerciseInfo ei = exerciseInfoList.get(getAdapterPosition());
                    SharedPreferences mSharedPreferences = view.getContext().getSharedPreferences(Utils.SHARED_PREFERENCES_APP, Context.MODE_PRIVATE);
                    mSharedPreferences.edit().putString(Utils.SHARED_PREFERENCES_ID_EXERCISE,ei.id).apply();
                    if (ei.completed == 0) {
                        //if cardio
                        if (ei.category == 1) {
                            Intent intent = new Intent(view.getContext(),ExerciseCardioActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        //if strength
                        else if (ei.category == 2) {
                            Intent intent = new Intent(view.getContext(),ExerciseStrengthActivity.class);
                            view.getContext().startActivity(intent);
                        }
                    }

                }
            });
        }
    }
}