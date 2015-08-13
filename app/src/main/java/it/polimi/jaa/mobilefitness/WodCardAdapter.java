package it.polimi.jaa.mobilefitness;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        wodViewHolder.vCreationDate.setText(wod.date);

        switch (i % 4) {
            case 0:
                wodViewHolder.vImageView.setImageResource(R.drawable.logo_bag);
                break;
            case 1:
                wodViewHolder.vImageView.setImageResource(R.drawable.logo_bike);
                break;
            case 2:
                wodViewHolder.vImageView.setImageResource(R.drawable.logo_hearth);
                break;
            case 3:
                wodViewHolder.vImageView.setImageResource(R.drawable.logo_medal);
                break;
        }

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
        TextView vCreationDate;
        ImageView vImageView;

        private View view;

        public WodViewHolder(View v) {
            super(v);
            view = v;
            vName = (TextView) v.findViewById(R.id.wod_name);
            vGym = (TextView) v.findViewById(R.id.wod_gym);
            vImageView = (ImageView) v.findViewById(R.id.wod_icon);
            vCreationDate = (TextView) v.findViewById(R.id.wod_creation_date);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((WodsActivity) view.getContext()).onWodSelected(id_wod);
                }
            });
        }
    }
}
