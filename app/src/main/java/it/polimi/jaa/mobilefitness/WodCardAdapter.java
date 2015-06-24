package it.polimi.jaa.mobilefitness;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.polimi.jaa.mobilefitness.utils.Utils;
import it.polimi.jaa.mobilefitness.utils.WodInfo;

/**
 * Created by Jacopo on 02/04/2015.
 */
public class WodCardAdapter extends RecyclerView.Adapter<WodCardAdapter.WodViewHolder>{
    private List<WodInfo> wodInfoList;

    public WodCardAdapter(List<WodInfo> wodInfoList) {
        this.wodInfoList = wodInfoList;
    }


    @Override
    public int getItemCount() {
        return wodInfoList.size();
    }

    @Override
    public void onBindViewHolder(WodViewHolder wodViewHolder, int i) {
        WodInfo wod = wodInfoList.get(i);
        wodViewHolder.vName.setText(wod.name);
        wodViewHolder.vGym.setText(wod.gym);
        wodViewHolder.id_wod = wod.id_wod;
    }

    @Override
    public WodViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.wod_card, viewGroup, false);

        return new WodViewHolder(itemView);
    }

    public static class WodViewHolder extends RecyclerView.ViewHolder {

        String id_wod;
        TextView vName;
        TextView vGym;

        private View view;

        public WodViewHolder(View v) {
            super(v);
            view = v;
            vName = (TextView) v.findViewById(R.id.wod_name);
            vGym = (TextView) v.findViewById(R.id.wod_gym);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences mSharedPreferences = view.getContext().getSharedPreferences(Utils.SHARED_PREFERENCES_APP, Context.MODE_PRIVATE);
                    mSharedPreferences.edit().putString(Utils.SHARED_PREFERENCES_ID_WOD,id_wod).apply();
                    WodFragment wodFragment = (WodFragment) ((WodsActivity) view.getContext()).getFragmentManager().findFragmentById(R.id.wod_fragment_container);
                    if (wodFragment != null){
                        wodFragment.setExercisesFromLocalDB();
                    }
                    else {
                        FragmentTransaction fragmentTransaction = ((WodsActivity) view.getContext()).getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.wods_fragment_container, new WodFragment());
                        fragmentTransaction.addToBackStack("wod_stack");
                        fragmentTransaction.commit();

                    }
                }
            });
        }
    }
}
