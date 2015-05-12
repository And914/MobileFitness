package it.polimi.jaa.mobilefitness.backend;

import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.helpers.Util;

import java.util.Date;
import java.util.List;

import it.polimi.jaa.mobilefitness.R;
import it.polimi.jaa.mobilefitness.backend.callbacks.Callback;
import it.polimi.jaa.mobilefitness.backend.callbacks.CallbackParseObject;
import it.polimi.jaa.mobilefitness.backend.callbacks.CallbackParseObjects;
import it.polimi.jaa.mobilefitness.utils.Utils;


/**
 * Created by andre on 06/05/15.
 */
public class BackendFunctions {

    public static void BFRegisterDevice(final Callback callback){
        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        parseInstallation.put("user",ParseUser.getCurrentUser());
        parseInstallation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    callback.done();
                } else {
                    callback.error(R.string.e_undefined);
                }
            }
        });
    }

    public static void BFLogin(String email, String psw, final Callback callback){
        ParseUser.logInInBackground(email, psw, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null)
                    callback.done();
                else
                    callback.error(R.string.e_undefined);
            }
        });
    }

    public static void BFSaveFacebookUser(JSONObject userInfo, final Callback callback){
        try {
            ParseUser parseUser = ParseUser.getCurrentUser();
            parseUser.setUsername(userInfo.getString("name"));
            String[] name = userInfo.getString("name").split(" ");
            parseUser.put(Utils.PARSE_USER_HEIGHT, 0);
            parseUser.put(Utils.PARSE_USER_WEIGHT,0);
            parseUser.put(Utils.PARSE_USER_HEIGHT,0);
            parseUser.put(Utils.PARSE_USER_NAME,name[0] );
            parseUser.put(Utils.PARSE_USER_SURNAME, name[1]);

            parseUser.put(Utils.PARSE_USER_BIRTHDATE, new Date());
            if(userInfo.has("email")){
                if(!userInfo.getString("email").isEmpty()){
                    parseUser.setEmail(userInfo.getString("email"));
                }
            }
            parseUser.put("fbId", userInfo.getString("id"));
            parseUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        callback.done();
                    } else {
                        callback.error(R.string.e_undefined);
                        Log.e("PARSE",e.toString());
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void BFRegistration(String email, String psw, String name, String surname, Date birthdate,int height, int weight, final Callback callback){
        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword(psw);
        user.put(Utils.PARSE_USER_BIRTHDATE, birthdate);
        user.put(Utils.PARSE_USER_HEIGHT, height);
        user.put(Utils.PARSE_USER_NAME, name);
        user.put(Utils.PARSE_USER_SURNAME, surname);
        user.put(Utils.PARSE_USER_WEIGHT, weight);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    callback.done();
                else
                    callback.error(R.string.e_undefined);
            }
        });
    }

    public static void BFGetWods(final CallbackParseObjects callbackParseObjects){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("wods");
        query.whereEqualTo(Utils.PARSE_WODS_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null)
                    callbackParseObjects.done(list);
                else
                    callbackParseObjects.error(R.string.e_undefined);
            }
        });
    }

    public static void BFGetGymBeacons(ParseObject gym, final CallbackParseObjects callbackParseObjects){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("gyms_beacons");
        query.whereEqualTo(Utils.PARSE_GYMSBEACONS_GYM, gym);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null)
                    callbackParseObjects.done(list);
                else
                    callbackParseObjects.error(R.string.e_undefined);
            }
        });
    }

    public static void BFGetGym(final CallbackParseObject callbackParseObject){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("users_gyms");
        query.whereEqualTo(Utils.PARSE_USERSGYMS_USER, ParseUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    ParseObject gym = parseObject.getParseObject(Utils.PARSE_USERSGYMS_GYM);
                    try {
                        gym.fetchIfNeeded();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    callbackParseObject.done(gym);
                }
                else
                    callbackParseObject.error(R.string.e_undefined);
            }
        });
    }

    public static void BFGetExercisesWod(String wodId, final CallbackParseObjects callbackParseObjects){
        BFGetWodObject(wodId, new CallbackParseObject() {
            @Override
            public void done(ParseObject parseObject) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("wods_exercises");
                query.whereEqualTo(Utils.PARSE_WODSEXERCISES_WOD, parseObject);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null)
                            callbackParseObjects.done(list);
                        else
                            callbackParseObjects.error(R.string.e_undefined);
                    }
                });
            }

            @Override
            public void error(int error) {
                callbackParseObjects.error(error);
            }
        });

    }

    public static void BFGetWodObject(String wodId, final CallbackParseObject callbackParseObject){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("wods");
        query.whereEqualTo("objectId", wodId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(e == null)
                    callbackParseObject.done(parseObject);
                else
                    callbackParseObject.error(R.string.e_undefined);
            }
        });
    }


}
