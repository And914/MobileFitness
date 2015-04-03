package it.polimi.jaa.mobilefitness;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by andre on 27/03/15.
 */

public class HomeUserFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    Button startWodButton;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    /*public static it.polimi.jaa.mobilefitness.HomeUserFragment newInstance(int sectionNumber) {
        it.polimi.jaa.mobilefitness.HomeUserFragment fragment = new it.polimi.jaa.mobilefitness.HomeUserFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }*/

    public HomeUserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_home, container, false);

        startWodButton = (Button) rootView.findViewById(R.id.button_start_wod);


        //Click Button
        startWodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call main activity with intent
                Intent wodsActivityIntent = new Intent(getActivity().getApplicationContext(), WodsActivity.class);
                startActivity(wodsActivityIntent);
            }
        });



        return rootView;
    }


}

