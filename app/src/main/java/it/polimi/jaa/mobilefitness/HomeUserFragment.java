package it.polimi.jaa.mobilefitness;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

import it.polimi.jaa.mobilefitness.utils.Utils;

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
    Button challengeButton;
    SharedPreferences mSharedPreferences;

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
        challengeButton = (Button) rootView.findViewById(R.id.button_challenge);

        //Set title
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREFS, Context.MODE_PRIVATE);
        TextView userHomeText = (TextView) rootView.findViewById(R.id.homeTextView);
        userHomeText.setText("Hi " + mSharedPreferences.getString(Utils.PREF_NAME,""));

        //Set phrase
        TextView homePhraseText = (TextView) rootView.findViewById(R.id.home_phrase);
        homePhraseText.setText(randomPhrase());


        //Click Buttons
        startWodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call wods activity with intent
                Intent wodsActivityIntent = new Intent(getActivity().getApplicationContext(), WodsActivity.class);
                startActivity(wodsActivityIntent);
            }
        });

        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call wods activity with intent
                Intent challengeNFCActivityIntent = new Intent(getActivity().getApplicationContext(), ChallengeNFCActivity.class);
                startActivity(challengeNFCActivityIntent);
            }
        });

        return rootView;
    }

    private String randomPhrase(){
        String phrase;
        Random random = new Random();
        switch (random.nextInt(7)){
            case 0: phrase = "The only easy day was yesterday";
                break;
            case 1: phrase = "Go hard or go home";
                break;
            case 2: phrase = "When it starts to hurt, that's when the set starts";
                break;
            case 3: phrase = "Good is not enough if better is possible";
                break;
            case 4: phrase = "No pain, no gain!";
                break;
            case 5: phrase = "If you're not first, you're last";
                break;
            case 6: phrase = "Fall down seven times, get up eight";
                break;
            default: phrase = "No pain, no gain!";
        }

        return phrase;
    }


}

