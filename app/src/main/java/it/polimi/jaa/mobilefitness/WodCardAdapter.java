package it.polimi.jaa.mobilefitness;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

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

        int id_wod;
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
                    Intent wodActivityIntent = new Intent(view.getContext().getApplicationContext(), WodActivity.class);
                    wodActivityIntent.putExtra("id_wod", id_wod);
                    view.getContext().startActivity(wodActivityIntent);
                }
            });
        }
    }
}
