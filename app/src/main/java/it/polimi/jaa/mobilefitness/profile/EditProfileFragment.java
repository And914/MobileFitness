package it.polimi.jaa.mobilefitness.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import it.polimi.jaa.mobilefitness.R;
import it.polimi.jaa.mobilefitness.utils.UserInfo;

/**
 * Created by Jacopo on 02/04/2015.
 */
public class EditProfileFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile_edit, container, false);
        EditText editTextProfile = (EditText)rootView.findViewById(R.id.editTextProfile);
        editTextProfile.setText(getArguments().getString("value"));
        return rootView;
    }
}
