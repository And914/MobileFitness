package it.polimi.jaa.mobilefitness.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import it.polimi.jaa.mobilefitness.R;
import it.polimi.jaa.mobilefitness.utils.UserInfo;

/**
 * Created by andre on 30/03/15.
 */
public class ProfileUserFragment extends Fragment {

    SharedPreferences mSharedPreferences;
    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";
    private static final String PREF_SURNAME = "surname";
    private static final String PREF_BIRTHDATE = "birthdate";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_WEIGHT = "weight";
    private static final String PREF_HEIGHT = "height";

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


    UserInfo getUserInfo (){
        UserInfo user = new UserInfo();
        // Access the device's key-value storage
        mSharedPreferences = this.getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        user.setName(mSharedPreferences.getString(PREF_NAME,""));
        user.setBirthDate(mSharedPreferences.getString(PREF_BIRTHDATE,""));
        user.setEmail(mSharedPreferences.getString(PREF_EMAIL, ""));
        user.setHeight(mSharedPreferences.getString(PREF_HEIGHT, ""));
        user.setSurname(mSharedPreferences.getString(PREF_SURNAME,""));
        user.setWeight(mSharedPreferences.getString(PREF_WEIGHT,""));

        return user;
    }

    void setUserInfo(UserInfo user, View view){
        TextView name = (TextView) view.findViewById(R.id.user_name);
        TextView surname = (TextView) view.findViewById(R.id.user_surname);
        TextView birthDate = (TextView) view.findViewById(R.id.user_birth_date);
        TextView height = (TextView) view.findViewById(R.id.user_height);
        TextView weight = (TextView) view.findViewById(R.id.user_weight);
        TextView email = (TextView) view.findViewById(R.id.user_email);
        email.setText("Email: " + user.getEmail());
        name.setText("Name: " + user.getName());
        surname.setText("Surname: " + user.getSurname());
        birthDate.setText("Birthdate: " + user.getBirthDate());
        height.setText("Height: " + user.getHeight());
        weight.setText("Weight: " + user.getWeight());
    }
}
