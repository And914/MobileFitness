package it.polimi.jaa.mobilefitness;


import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.gimbal.android.Gimbal;


import com.gimbal.android.PlaceManager;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.Place;
import com.gimbal.android.Visit;

import com.gimbal.android.CommunicationManager;
import com.gimbal.android.CommunicationListener;
import com.gimbal.android.Communication;
import com.gimbal.android.Push;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconManager;
import com.gimbal.android.BeaconSighting;

/**
 * Created by Jacopo on 05/05/2015.
 */
public class ParseApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        ParseCrashReporting.enable(this);

        Parse.enableLocalDatastore(this);

        Parse.initialize(this);

        ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        // And the user can read and modify its own objects
        ParseACL.setDefaultACL(defaultACL, true);

        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(getApplicationContext());

        Gimbal.setApiKey(this, getString(R.string.gimbal_key));

    }
}
