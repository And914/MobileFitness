package it.polimi.jaa.mobilefitness.profile;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.polimi.jaa.mobilefitness.R;
import it.polimi.jaa.mobilefitness.utils.UserInfo;

/**
 * Created by andre on 30/03/15.
 */
public class ProfileUserFragment extends Fragment {
    public ProfileUserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        UserInfo user = getUserInfo();
        setUserInfo(user, rootView);
        return rootView;
    }


    //TODO: da fare tutto XD
    UserInfo getUserInfo (){
     return new UserInfo("Name: allah","Surname : al bar","Birthdate: 20/12/1000","Height: 1,20 km","Weight: 120 t");
    }

    void setUserInfo(UserInfo user, View view){
        TextView name = (TextView) view.findViewById(R.id.user_name);
        TextView surname = (TextView) view.findViewById(R.id.user_surname);
        TextView birthDate = (TextView) view.findViewById(R.id.user_birth_date);
        TextView height = (TextView) view.findViewById(R.id.user_height);
        TextView weight = (TextView) view.findViewById(R.id.user_weight);
        name.setText(user.name);
        surname.setText(user.surname);
        birthDate.setText(user.birthDate);
        height.setText(user.height);
        weight.setText(user.weight);
    }
}
