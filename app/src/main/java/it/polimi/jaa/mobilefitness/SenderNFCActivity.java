package it.polimi.jaa.mobilefitness;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Jacopo on 09/04/2015.
 */
public class SenderNFCActivity extends ActionBarActivity implements NfcAdapter.CreateNdefMessageCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sender_nfc);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        if (adapter != null) {
            adapter.setNdefPushMessageCallback(this,this);
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = ("Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis());
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { NdefRecord.createMime(DevicePolicyManager.MIME_TYPE_PROVISIONING_NFC, text.getBytes())});
        return msg;
    }
}
