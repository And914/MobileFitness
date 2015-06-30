package it.polimi.jaa.mobilefitness;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.polimi.jaa.mobilefitness.authentication.LoginActivity;
import it.polimi.jaa.mobilefitness.utils.Utils;

/**
 * Created by Jacopo on 09/04/2015.
 */
public class SendResultsNFCActivity extends ActionBarActivity implements NfcAdapter.CreateNdefMessageCallback {
    private Button senderNFCButton;
    private static Boolean mFirst;
    private NfcAdapter mNfcAdapter;

    private TextView resultText;
    private TextView opponentResultText;
    private ImageView imageResult;
    private Button buttonHome;

    private static int result;
    private static int opponentResult;
    private static String opponentFbId;
    private static String opponentUsername;

    private SharedPreferences mSharedPreferencesChallenge;

    //MenuItem shareMenu;

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

        mSharedPreferencesChallenge = getSharedPreferences(Utils.SHARED_PREFERENCES_APP_CHALLENGE, MODE_PRIVATE);
        result = mSharedPreferencesChallenge.getInt(Utils.SHARED_PREFERENCES_CHALLENGE_RESULT, 0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        resultText = (TextView) findViewById(R.id.your_score);
        opponentResultText = (TextView) findViewById(R.id.opponent_score);
        resultText.setText(String.valueOf(result));

        imageResult = (ImageView) findViewById(R.id.image_result);
        buttonHome = (Button) findViewById(R.id.button_home_challenge);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.challenge, menu);

        /*shareMenu = menu.findItem(R.id.action_share_fb);
        if(shareMenu != null){
            shareMenu.setVisible(true);
        }*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share_fb) {
            ShareDialog shareDialog = new ShareDialog(this);
            List<String> fbIds = new ArrayList<>();
            fbIds.add(opponentFbId);
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle("Today's Challenge Result")
                        .setContentDescription(
                                "I won today's challenge. My score: " + result + " - " + opponentUsername + " score: " + opponentResult)
                        .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                        .setPeopleIds(fbIds)
                        .build();

                shareDialog.show(linkContent);
            }
            return true;


        }
        return super.onOptionsItemSelected(item);
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
            jsonObject.put("result", result);
            jsonObject.put("fbId", ParseUser.getCurrentUser().getString(Utils.PARSE_USER_FBID));
            jsonObject.put("username", ParseUser.getCurrentUser().getUsername());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return new NdefMessage(
                new NdefRecord[] { NdefRecord.createMime(
                        "application/it.polimi.jaa.android.beam1", jsonObject.toString().getBytes())
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

            opponentResult = jsonObject.getInt("result");
            opponentFbId = jsonObject.getString("fbId");
            opponentUsername = jsonObject.getString("username");
            opponentResultText.setText(String.valueOf(opponentResult));
            if(!mFirst) {
                SendBackDialogFragment sendBackDialogFragment = new SendBackDialogFragment();
                sendBackDialogFragment.show(getFragmentManager(), "send_back_dialog");
            }
            if (opponentResult > result){
                opponentResultText.setTypeface(null, Typeface.BOLD);
                imageResult.setImageResource(R.drawable.ic_don_t_give_up);

            } else if (opponentResult == result){
                opponentResultText.setTypeface(null, Typeface.BOLD);
                resultText.setTypeface(null, Typeface.BOLD);
                imageResult.setImageResource(R.drawable.ic_3256270_winner);
                mSharedPreferencesChallenge.edit().putBoolean("winner", true).apply();
            } else {
                resultText.setTypeface(null, Typeface.BOLD);
                imageResult.setImageResource(R.drawable.ic_3256270_winner);
                mSharedPreferencesChallenge.edit().putBoolean("winner", true).apply();
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

    public static class ShareOnFacebookFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            CharSequence[] info = {"Share your victory on Facebook!","Let other people know how good you are."};
            builder.setItems(info, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setTitle("You Won!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ShareDialog shareDialog = new ShareDialog(getActivity());
                            List<String> fbIds = new ArrayList<>();
                            fbIds.add(opponentFbId);
                            if (ShareDialog.canShow(ShareLinkContent.class)) {
                                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                        .setContentTitle("Today's Challenge Result")
                                        .setContentDescription(
                                                "I won today's challenge. My score: " + result + " - "+ opponentUsername +" score: " + opponentResult)
                                        .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                                        .setPeopleIds(fbIds)
                                        .build();

                                shareDialog.show(linkContent);
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });


            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}