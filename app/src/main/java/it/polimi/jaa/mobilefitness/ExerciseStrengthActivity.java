package it.polimi.jaa.mobilefitness;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import it.polimi.jaa.mobilefitness.model.GymContract;
import it.polimi.jaa.mobilefitness.utils.Utils;

/**
 * Created by Jacopo on 13/05/2015.
 */
public class ExerciseStrengthActivity extends ActionBarActivity {

    private SharedPreferences mSharedPreferences;
    private static String exerciseId;

    private ImageView exerciseImage;
    private TextView exerciseName;
    private TextView completedRounds;
    private TextView totalRounds;
    private Button completeRoundButton;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_strength);

        mSharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_APP, MODE_PRIVATE);
        exerciseId = mSharedPreferences.getString(Utils.SHARED_PREFERENCES_ID_EXERCISE, "");
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        exerciseImage = (ImageView) findViewById(R.id.exercise_icon);
        exerciseName = (TextView) findViewById(R.id.exercise_name);
        completedRounds = (TextView) findViewById(R.id.completed_rounds);
        totalRounds = (TextView) findViewById(R.id.total_rounds);
        completeRoundButton = (Button) findViewById(R.id.button_complete_exercise);

        completeRoundButton.setText("Next round");

        String[] args = {String.valueOf(exerciseId)};

        final Cursor cursor = getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI,
                new String[]{GymContract.ExerciseEntry.COLUMN_NAME,GymContract.ExerciseEntry.COLUMN_ICON_ID,GymContract.ExerciseEntry.COLUMN_ROUNDS,GymContract.ExerciseEntry.COLUMN_REST_TIME},
                GymContract.ExerciseEntry.COLUMN_ID + " = ?",
                args ,
                null
        );

        cursor.moveToFirst();

        exerciseName.setText(cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_NAME)));
        exerciseImage.setImageResource(Utils.findIcon(cursor.getInt(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ICON_ID))));

        String rounds = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_ROUNDS));
        final String restTime = cursor.getString(cursor.getColumnIndex(GymContract.ExerciseEntry.COLUMN_REST_TIME));

        completedRounds.setText("0");
        totalRounds.setText(rounds);

        completeRoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rounds = Integer.parseInt(completedRounds.getText().toString()) + 1;
                if (rounds < Integer.parseInt(totalRounds.getText().toString())) {
                    completedRounds.setText(String.valueOf(rounds));
                    countDownTimer = new CountDownTimer(Integer.parseInt(restTime) * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            int secondsUntilFinished = (int) (millisUntilFinished / 1000);
                            completeRoundButton.setText(convertIntToTimeString(secondsUntilFinished));
                        }

                        @Override
                        public void onFinish() {
                            completeRoundButton.setText("Next Round");

                        }
                    };
                    countDownTimer.start();
                } else {
                    completedRounds.setText(String.valueOf(rounds));
                    CompletedRoundsFragment timerExpiredFragment = new CompletedRoundsFragment();
                    timerExpiredFragment.show(getFragmentManager(), "completed_rounds");
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

    public static class CompletedRoundsFragment extends DialogFragment {

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
