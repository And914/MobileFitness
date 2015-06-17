package it.polimi.jaa.mobilefitness;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.polimi.jaa.mobilefitness.model.GymContract;
import it.polimi.jaa.mobilefitness.utils.Utils;


/**
 * Created by Jacopo on 13/05/2015.
 */
public class ExerciseCardioActivity extends ActionBarActivity {

    private SharedPreferences mSharedPreferences;
    private static String exerciseId;

    private ImageView exerciseImage;
    private TextView exerciseName;
    private TextView chronometer;
    private CountDownTimer countDownTimer;
    private Button startExerciseButton;
    private boolean started;
    private long savedMillisecondsUntilFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_cardio);

        mSharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_APP, MODE_PRIVATE);
        exerciseId = mSharedPreferences.getString(Utils.SHARED_PREFERENCES_ID_EXERCISE, "");
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        exerciseImage = (ImageView) findViewById(R.id.exercise_icon);
        exerciseName = (TextView) findViewById(R.id.exercise_name);
        chronometer = (TextView) findViewById(R.id.timer_cardio);
        startExerciseButton = (Button) findViewById(R.id.button_timer_exercise);
        startExerciseButton.setText(R.string.start_wod_timer_cardio);

        started = false;




        String[] args = {String.valueOf(exerciseId)};

        final Cursor cursor = getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI,
                new String[]{GymContract.ExerciseEntry.COLUMN_NAME,GymContract.ExerciseEntry.COLUMN_ICON_ID,GymContract.ExerciseEntry.COLUMN_DURATION},
                GymContract.ExerciseEntry.COLUMN_ID + " = ?",
                args ,
                null
        );

        cursor.moveToFirst();

        exerciseName.setText(cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_NAME)));
        exerciseImage.setImageResource(Utils.findIcon(cursor.getInt(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ICON_ID))));

        int duration = cursor.getInt(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_DURATION));
        savedMillisecondsUntilFinish = duration*1000;

        chronometer.setText(convertIntToTimeString(duration));


        startExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started) {

                    countDownTimer = new CountDownTimer(savedMillisecondsUntilFinish,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            savedMillisecondsUntilFinish = millisUntilFinished;
                            int secondsUntilFinished = (int)(millisUntilFinished / 1000);
                            chronometer.setText(convertIntToTimeString(secondsUntilFinished));
                        }

                        @Override
                        public void onFinish() {
                            chronometer.setText("TIME IS UP!");
                            TimerExpiredFragment timerExpiredFragment = new TimerExpiredFragment();
                            timerExpiredFragment.show(getFragmentManager(), "timer_expired");
                        }
                    };
                    countDownTimer.start();
                    started = true;
                    startExerciseButton.setText("STOP!");
                } else {
                    countDownTimer.cancel();
                    started = false;
                    startExerciseButton.setText(R.string.start_wod_timer_cardio);
                }
            }
        });

        cursor.close();

    }

    private static String convertIntToTimeString(int timeInSeconds) {
        String minutes = String.valueOf(timeInSeconds / 60);
        String seconds = String.valueOf(timeInSeconds % 60);
        if (minutes.length() == 1) {
            minutes = "0".concat(minutes);
        }
        if (seconds.length() == 1) {
            seconds = "0".concat(seconds);
        }
        return minutes + ":" + seconds;
    }

    public static class TimerExpiredFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            CharSequence[] info = {"You completed the exercise"};
            builder.setItems(info, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setTitle("GREAT!!")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dismiss();
                            getActivity().finish();

                            String[] args = {String.valueOf(exerciseId)};

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(GymContract.ExerciseEntry.COLUMN_COMPLETED,1);

                            getActivity().getContentResolver().update(GymContract.ExerciseEntry.CONTENT_URI,
                                    contentValues,
                                    GymContract.ExerciseEntry.COLUMN_ID + " = ?",
                                    args);
                        }
                    });


            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}