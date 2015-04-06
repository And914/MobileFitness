package it.polimi.jaa.mobilefitness;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import it.polimi.jaa.mobilefitness.model.GymContract;
import it.polimi.jaa.mobilefitness.utils.ExerciseInfo;

/**
 * Created by andre on 30/03/15.
 */
public class WodActivity extends ActionBarActivity{

    RecyclerView recyclerView;
    int idWod;
    private static final String LOG_ACTIVITY = "WodFragment";

    public WodActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wod);

        recyclerView = (RecyclerView) findViewById(R.id.card_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        idWod = getIntent().getExtras().getInt("id_wod");

        String[] args = {String.valueOf(idWod)};
        Cursor cursor = getContentResolver().query(GymContract.ExerciseEntry.CONTENT_URI,
                new String[]{GymContract.ExerciseEntry.COLUMN_NAME,GymContract.ExerciseEntry.COLUMN_EQUIPMENT,
                        GymContract.ExerciseEntry.COLUMN_ROUNDS,GymContract.ExerciseEntry.COLUMN_REPS,GymContract.ExerciseEntry.COLUMN_REST_TIME,
                        GymContract.ExerciseEntry.COLUMN_WEIGHT,GymContract.ExerciseEntry.COLUMN_DURATION,GymContract.ExerciseEntry.COLUMN_ICON_ID
                },
                "id_wod = ?", args , null);

        ExerciseCardAdapter exerciseCardAdapter = new ExerciseCardAdapter(ExerciseInfo.createListFromCursor(cursor));
        cursor.close();
        recyclerView.setAdapter(exerciseCardAdapter);

    }


}