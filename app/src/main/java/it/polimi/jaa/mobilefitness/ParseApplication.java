package it.polimi.jaa.mobilefitness;


import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

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
    }
}
