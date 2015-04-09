package it.polimi.jaa.mobilefitness;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Jacopo on 09/04/2015.
 */
public class ReceiverNFCActivity extends ActionBarActivity {
    private Button senderNFCButton;
    private Boolean mFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_receiver_nfc);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        senderNFCButton = (Button) findViewById(R.id.button_sender_nfc);

        senderNFCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call wods activity with intent
                Intent senderNFCActivityIntent = new Intent(getApplicationContext(), SenderNFCActivity.class);
                startActivity(senderNFCActivityIntent);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onStart() {
        super.onStart();
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        if (adapter != null) {
            String text = ("first");
            NdefMessage msg = new NdefMessage(
                    new NdefRecord[] { NdefRecord.createMime("application/it.polimi.jaa.android.beam", text.getBytes())});
            adapter.setNdefPushMessage(msg, this);
        }
        else {
            Toast.makeText(this,"NFC NOT FOUND!",Toast.LENGTH_SHORT).show();
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            mFirst = processIntent(getIntent());

            if (mFirst) {
                String text = ("second");
                NdefMessage msg = new NdefMessage(
                        new NdefRecord[] { NdefRecord.createMime("application/it.polimi.jaa.android.beam", text.getBytes())});
                NfcAdapter.getDefaultAdapter(this).setNdefPushMessage(msg, this);
                mFirst = false;

            }
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
    boolean processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        Toast.makeText(this, new String(msg.getRecords()[0].getPayload()), Toast.LENGTH_SHORT).show();

        return new String(msg.getRecords()[0].getPayload()).equals("first");
    }

}
