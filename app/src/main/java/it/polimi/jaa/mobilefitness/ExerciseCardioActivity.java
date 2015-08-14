package it.polimi.jaa.mobilefitness;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import it.polimi.jaa.mobilefitness.backend.BackendFunctions;
import it.polimi.jaa.mobilefitness.backend.callbacks.CallbackBoolean;
import it.polimi.jaa.mobilefitness.model.GymContract;
import it.polimi.jaa.mobilefitness.utils.Utils;


/**
 * Created by Jacopo on 13/05/2015.
 */
public class ExerciseCardioActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;
    private static SharedPreferences mSharedPreferencesChallenge;
    private static String exerciseId;

    private TextView exerciseName;
    private TextView chronometer;
    private CountDownTimer countDownTimer;
    private Button startExerciseButton;
    private boolean started;
    private long savedMillisecondsUntilFinish;
    private static int duration;

    private static Boolean isChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_cardio);

        mSharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_APP, MODE_PRIVATE);
        exerciseId = mSharedPreferences.getString(Utils.SHARED_PREFERENCES_ID_EXERCISE, "");

        mSharedPreferencesChallenge = getSharedPreferences(Utils.SHARED_PREFERENCES_APP_CHALLENGE, MODE_PRIVATE);
        isChallenge = mSharedPreferencesChallenge.getBoolean(Utils.SHARED_PREFERENCES_ISCHALLENGE, false);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        exerciseName = (TextView) findViewById(R.id.exercise_name);
        chronometer = (TextView) findViewById(R.id.timer_cardio);
        startExerciseButton = (Button) findViewById(R.id.button_timer_exercise);
        startExerciseButton.setText(R.string.start_wod_timer_cardio);

        started = false;


        if(!isChallenge) {
            String[] args = {String.valueOf(exerciseId)};

            final Cursor cursor = getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI,
                    new String[]{GymContract.ExerciseEntry.COLUMN_NAME, GymContract.ExerciseEntry.COLUMN_ICON_ID, GymContract.ExerciseEntry.COLUMN_DURATION},
                    GymContract.ExerciseEntry.COLUMN_ID + " = ?",
                    args,
                    null
            );

            cursor.moveToFirst();

            exerciseName.setText(cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_NAME)));

            duration = cursor.getInt(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_DURATION));
            cursor.close();
        } else {
            exerciseName.setText(mSharedPreferencesChallenge.getString(Utils.SHARED_PREFERENCES_CHALLENGE_NAME,""));
            duration = mSharedPreferencesChallenge.getInt(Utils.SHARED_PREFERENCES_CHALLENGE_DURATION,0);
        }
        savedMillisecondsUntilFinish = duration*1000;

        chronometer.setText(convertIntToTimeString(duration));


        startExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started) {

                    countDownTimer = new CountDownTimer(savedMillisecondsUntilFinish, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            savedMillisecondsUntilFinish = millisUntilFinished;
                            int secondsUntilFinished = (int) (millisUntilFinished / 1000);
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
                    int results = duration -  (int) (savedMillisecondsUntilFinish / 1000);
                    if (isChallenge){
                        mSharedPreferencesChallenge.edit().putInt(Utils.SHARED_PREFERENCES_CHALLENGE_RESULT, results).apply();
                        Intent intent = new Intent(getApplicationContext(), SendResultsNFCActivity.class);
                        startActivity(intent);
                    }
                    else {
                        saveResult(results, getParent());
                    }
                    finish();
                }
            }
        });



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
                            if(isChallenge){
                                //save results
                                mSharedPreferencesChallenge.edit().putInt(Utils.SHARED_PREFERENCES_CHALLENGE_RESULT, duration).apply();
                                Intent intent = new Intent(getActivity().getApplicationContext(), SendResultsNFCActivity.class);
                                startActivity(intent);
                            } else {
                                saveResult(duration,getActivity());
                            }
                        }
                    });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    private static void saveResult(int result, final Activity activity) {
        String[] args = {String.valueOf(exerciseId)};

        ContentValues contentValues = new ContentValues();
        contentValues.put(GymContract.ExerciseEntry.COLUMN_COMPLETED, 1);

        activity.getContentResolver().update(GymContract.ExerciseEntry.CONTENT_URI,
                contentValues,
                GymContract.ExerciseEntry.COLUMN_ID + " = ?",
                args);

        BackendFunctions.BFSaveResult(exerciseId, result);

        contentValues = new ContentValues();
        contentValues.put(GymContract.HistoryEntry.COLUMN_ID_EXERC, String.valueOf(exerciseId));
        contentValues.put(GymContract.HistoryEntry.COLUMN_RESULT, result);
        contentValues.put(GymContract.HistoryEntry.COLUMN_TIMESTAMP, String.valueOf(new Date()));

        activity.getContentResolver().insert(GymContract.HistoryEntry.CONTENT_URI, contentValues);
        BackendFunctions.BFSaveRecord(exerciseId, result, new CallbackBoolean() {
            @Override
            public void done(boolean result) {
                if(result){
                    Toast.makeText(activity.getApplicationContext(),"New record!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void error(int error) {
                Toast.makeText(activity.getApplicationContext(),activity.getString(error), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
