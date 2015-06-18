package it.polimi.jaa.mobilefitness;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import it.polimi.jaa.mobilefitness.backend.BackendFunctions;
import it.polimi.jaa.mobilefitness.backend.callbacks.CallbackParseObject;
import it.polimi.jaa.mobilefitness.backend.callbacks.CallbackParseObjects;
import it.polimi.jaa.mobilefitness.utils.Utils;

/**
 * Created by Jacopo on 09/04/2015.
 */
public class ChallengeNFCActivity extends ActionBarActivity implements NfcAdapter.CreateNdefMessageCallback{

    private SharedPreferences mSharedPreferences;

    private NfcAdapter mNfcAdapter;
    private Spinner equipmentSpinner;
    private EditText exerciseNameText;

    private LinearLayout roundsLayout;
    private LinearLayout repsLayout;
    private LinearLayout weightsLayout;
    private LinearLayout restTimeLayout;
    private LinearLayout durationLayout;

    private TextView rounds;
    private TextView reps;
    private TextView weight;
    private TextView restTime;
    private TextView duration;

    private Button startButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_challenge_nfc);

        mSharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_APP_CHALLENGE, MODE_PRIVATE);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


        equipmentSpinner = (Spinner) findViewById(R.id.equipment_spinner);
        exerciseNameText = (EditText) findViewById(R.id.challenge_exercise_name);

        int NFCDone = mSharedPreferences.getInt("NFCDone",0);

        if (exerciseNameText.getText().length() == 0 && NFCDone == 0) {

            String loading[] = new String[1];
            loading[0] = "Loading .. ";
            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, loading);
            spinnerArrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
            equipmentSpinner.setAdapter(spinnerArrayAdapter);

            BackendFunctions.BFGetGymEquipments(new CallbackParseObjects() {
                @Override
                public void done(List<ParseObject> equipments) {
                    String[] spinnerArray = new String[equipments.size()];
                    int i = 0;
                    for (ParseObject equipment : equipments) {
                        spinnerArray[i] = equipment.getString(Utils.PARSE_EQUIPMENT_NAME);
                        i++;
                    }

                    mSharedPreferences.edit().putStringSet("spinnerArray", new HashSet<>(Arrays.asList(spinnerArray))).apply();

                    Arrays.sort(spinnerArray);

                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                    spinnerArrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
                    equipmentSpinner.setAdapter(spinnerArrayAdapter);
                }

                @Override
                public void error(int error) {

                }
            });

            mSharedPreferences.edit().putInt("NFCDone",1).apply();
        }

        roundsLayout = (LinearLayout) findViewById(R.id.rounds_layout);
        repsLayout = (LinearLayout) findViewById(R.id.reps_layout);
        weightsLayout = (LinearLayout) findViewById(R.id.weights_layout);
        restTimeLayout = (LinearLayout) findViewById(R.id.rest_time_layout);
        durationLayout = (LinearLayout) findViewById(R.id.duration_layout);

        rounds = (TextView) findViewById(R.id.rounds);
        reps = (TextView) findViewById(R.id.reps);
        weight = (TextView) findViewById(R.id.weights);
        restTime = (TextView) findViewById(R.id.rest_time);
        duration = (TextView) findViewById(R.id.duration);

        startButton = (Button) findViewById(R.id.button_start);

        equipmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getChildCount() > 0) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);

                    String selected = adapterView.getItemAtPosition(i).toString();
                    switch (selected) {
                        case "Rower":
                            roundsLayout.setVisibility(View.GONE);
                            repsLayout.setVisibility(View.GONE);
                            weightsLayout.setVisibility(View.GONE);
                            restTimeLayout.setVisibility(View.GONE);
                            durationLayout.setVisibility(View.VISIBLE);
                            break;
                        case "Cyclette":
                            roundsLayout.setVisibility(View.GONE);
                            repsLayout.setVisibility(View.GONE);
                            weightsLayout.setVisibility(View.GONE);
                            restTimeLayout.setVisibility(View.GONE);
                            durationLayout.setVisibility(View.VISIBLE);
                            break;
                        case "Rope":
                            roundsLayout.setVisibility(View.GONE);
                            repsLayout.setVisibility(View.GONE);
                            weightsLayout.setVisibility(View.GONE);
                            restTimeLayout.setVisibility(View.GONE);
                            durationLayout.setVisibility(View.VISIBLE);
                            break;
                        default:
                            roundsLayout.setVisibility(View.VISIBLE);
                            repsLayout.setVisibility(View.VISIBLE);
                            weightsLayout.setVisibility(View.VISIBLE);
                            restTimeLayout.setVisibility(View.VISIBLE);
                            durationLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String equipment = equipmentSpinner.getSelectedItem().toString();
                Intent intent;
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean(Utils.SHARED_PREFERENCES_ISCHALLENGE,true);
                editor.putString(Utils.SHARED_PREFERENCES_CHALLENGE_EQUIPMENT, equipment);
                editor.putString(Utils.SHARED_PREFERENCES_CHALLENGE_NAME, exerciseNameText.getText().toString());
                switch (equipment) {
                    case "Rower":
                        editor.putInt(Utils.SHARED_PREFERENCES_CHALLENGE_DURATION, Integer.parseInt(duration.getText().toString()));
                        editor.apply();
                        intent = new Intent(view.getContext(),ExerciseCardioActivity.class);
                        break;
                    case "Cyclette":
                        editor.putInt(Utils.SHARED_PREFERENCES_CHALLENGE_DURATION, Integer.parseInt(duration.getText().toString()));
                        editor.apply();
                        intent = new Intent(view.getContext(),ExerciseCardioActivity.class);
                        break;
                    case "Rope":
                        editor.putInt(Utils.SHARED_PREFERENCES_CHALLENGE_DURATION, Integer.parseInt(duration.getText().toString()));
                        editor.apply();
                        intent = new Intent(view.getContext(),ExerciseCardioActivity.class);
                        break;
                    default:

                        editor.putInt(Utils.SHARED_PREFERENCES_CHALLENGE_REPS, Integer.parseInt(reps.getText().toString()));
                        editor.putInt(Utils.SHARED_PREFERENCES_CHALLENGE_RESTTIME, Integer.parseInt(restTime.getText().toString()));
                        editor.putInt(Utils.SHARED_PREFERENCES_CHALLENGE_ROUNDS, Integer.parseInt(rounds.getText().toString()));
                        editor.putInt(Utils.SHARED_PREFERENCES_CHALLENGE_WEIGHTS, Integer.parseInt(weight.getText().toString()));
                        editor.apply();
                        intent = new Intent(view.getContext(),ExerciseStrengthActivity.class);
                }
                ((Activity)view.getContext()).startActivity(intent);
            }
        });


    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        JSONObject jsonObject = new JSONObject();

        String equipment = equipmentSpinner.getSelectedItem().toString();

        try {switch (equipment) {
            case "Rower":
                jsonObject.put("duration", duration.getText());
                break;
            case "Cyclette":
                jsonObject.put("duration", duration.getText());
                break;
            case "Rope":
                jsonObject.put("duration", duration.getText());
                break;
            default:
                jsonObject.put("rounds", rounds.getText());
                jsonObject.put("reps", reps.getText());
                jsonObject.put("weight", weight.getText());
                jsonObject.put("restTime", restTime.getText());
        }
            jsonObject.put("equipment",equipment);
            jsonObject.put("name",exerciseNameText.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return new NdefMessage(
                new NdefRecord[] { NdefRecord.createMime(
                        "application/it.polimi.jaa.android.beam", jsonObject.toString().getBytes())
                        /**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                         */
                        //,NdefRecord.createApplicationRecord("com.example.android.beam")
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {

        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);

        // record 0 contains the MIME type, record 1 is the AAR, if present
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(new String(msg.getRecords()[0].getPayload()));

            if (jsonObject.length() > 0) {
                String exerciseName = jsonObject.getString("name");
                String equipment = jsonObject.getString("equipment");

                String[] spinnerArray = {equipment};

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                spinnerArrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
                equipmentSpinner.setAdapter(spinnerArrayAdapter);



                if (jsonObject.has("duration")) {
                    String exerciseDuration = jsonObject.getString("duration");
                    duration.setText(exerciseDuration);
                    durationLayout.setVisibility(View.VISIBLE);

                }

                if (jsonObject.has("rounds")) {
                    String exerciseDuration = jsonObject.getString("rounds");
                    rounds.setText(exerciseDuration);
                    roundsLayout.setVisibility(View.VISIBLE);

                }

                if (jsonObject.has("reps")) {
                    String exerciseDuration = jsonObject.getString("reps");
                    reps.setText(exerciseDuration);
                    repsLayout.setVisibility(View.VISIBLE);

                }

                if (jsonObject.has("weight")) {
                    String exerciseDuration = jsonObject.getString("weight");
                    weight.setText(exerciseDuration);
                    weightsLayout.setVisibility(View.VISIBLE);

                }

                if (jsonObject.has("restTime")) {
                    String exerciseDuration = jsonObject.getString("restTime");
                    restTime.setText(exerciseDuration);
                    restTimeLayout.setVisibility(View.VISIBLE);

                }
                exerciseNameText.setText(exerciseName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
