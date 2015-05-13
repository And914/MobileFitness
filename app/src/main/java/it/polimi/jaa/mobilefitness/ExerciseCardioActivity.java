package it.polimi.jaa.mobilefitness;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;


import it.polimi.jaa.mobilefitness.model.GymContract;
import it.polimi.jaa.mobilefitness.utils.Utils;


/**
 * Created by Jacopo on 13/05/2015.
 */
public class ExerciseCardioActivity extends ActionBarActivity {

    private SharedPreferences mSharedPreferences;
    private String exerciseId;

    private ImageView exerciseImage;
    private TextView exerciseName;
    private Chronometer chronometer;
    private Button startExerciseButton;
    private boolean started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_cardio);

        mSharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_APP, MODE_PRIVATE);
        exerciseId = mSharedPreferences.getString(Utils.SHARED_PREFERENCES_ID_EXERCISE, "");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        exerciseImage = (ImageView) findViewById(R.id.exercise_icon);
        exerciseName = (TextView) findViewById(R.id.exercise_name);
        chronometer = (Chronometer) findViewById(R.id.chronometer_cardio);
        startExerciseButton = (Button) findViewById(R.id.button_timer_exercise);
        startExerciseButton.setText(R.string.start_wod_timer_cardio);

        started = false;


        startExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started) {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    started = true;
                    startExerciseButton.setText("STOP!");
                } else {
                    chronometer.stop();
                    started = false;
                    startExerciseButton.setText(R.string.start_wod_timer_cardio);
                }
            }
        });

        String[] args = {String.valueOf(exerciseId)};

        Cursor cursor = getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI,
                new String[]{GymContract.ExerciseEntry.COLUMN_NAME,GymContract.ExerciseEntry.COLUMN_ICON_ID},
                GymContract.ExerciseEntry.COLUMN_ID + " = ?",
                args ,
                null
        );

        cursor.moveToFirst();

        exerciseName.setText(cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_NAME)));
        exerciseImage.setImageResource(Utils.findIcon(cursor.getInt(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ICON_ID))));

        cursor.close();

    }
}
