package it.polimi.jaa.mobilefitness;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * Created by jaco on 01/07/15.
 */
public class ChromecastFragment extends Fragment {
    private MainActivity activity;
    private FrameLayout layout;
    private Button changeExerciseButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        layout = (FrameLayout) inflater.inflate(R.layout.fragment_chromecast, container, false);

        changeExerciseButton = (Button) layout.findViewById(R.id.change_exercise_button);

        changeExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onButtonClicked();
            }
        });

        return layout;
    }

    public interface OnButtonClicked {
        void onButtonClicked();
    }

}
