package it.polimi.jaa.mobilefitness;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jacopo on 09/04/2015.
 */
public class SendResultsNFCActivity extends ActionBarActivity implements NfcAdapter.CreateNdefMessageCallback {
    private Button senderNFCButton;
    private static Boolean mFirst;
    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirst = true;

        setContentView(R.layout.activity_send_results_nfc);

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

        senderNFCButton = (Button) findViewById(R.id.button_sender_nfc);

        senderNFCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call wods activity with intent
                Intent senderNFCActivityIntent = new Intent(getApplicationContext(), ChallengeNFCActivity.class);
                startActivity(senderNFCActivityIntent);
            }
        });
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (mFirst) {
                jsonObject.put("number",1);
            }
            else {
                jsonObject.put("number",2);
            }
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
            if (jsonObject.getInt("number") == 1) {
                mFirst = false;
            }
            if(!mFirst) {
                SendBackDialogFragment sendBackDialogFragment = new SendBackDialogFragment();
                sendBackDialogFragment.show(getFragmentManager(), "send_back_dialog");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static class SendBackDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            CharSequence[] info = {"You received your friend results.","To send him yours, detach and reattach the devices."};
            builder.setItems(info, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setTitle("Results received!")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dismiss();
                        }
                    });


            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}