package it.polimi.jaa.mobilefitness.utils;

import it.polimi.jaa.mobilefitness.R;

/**
 * Created by Jacopo on 02/04/2015.
 */
public class Utils {
    public static final String server_ip = "http://192.168.1.187:80";
    public static final String PREFS = "prefs";
    public static final String PREF_NAME = "name";
    public static final String PREF_SURNAME = "surname";
    public static final String PREF_BIRTHDATE = "birthdate";
    public static final String PREF_EMAIL = "email";
    public static final String PREF_WEIGHT = "weight";
    public static final String PREF_HEIGHT = "height";
    public static final String PREF_AVATAR = "avatar";


    public static final String PARSE_USER_WEIGHT = "weight";
    public static final String PARSE_USER_HEIGHT = "height";
    public static final String PARSE_USER_BIRTHDATE = "birthdate";
    public static final String PARSE_USER_NAME = "name";
    public static final String PARSE_USER_SURNAME = "surname";

    public static final String PARSE_WODS_USER = "id_user";
    public static final String PARSE_WODS_NAME = "wod_name";
    public static final String PARSE_WODS_GYM = "id_gym";

    public static final String PARSE_WODSEXERCISES_WOD = "id_wod";
    public static final String PARSE_WODSEXERCISES_EXERCISE = "id_exercise";
    public static final String PARSE_WODSEXERCISES_ROUNDS = "rounds";
    public static final String PARSE_WODSEXERCISES_REPS = "reps";
    public static final String PARSE_WODSEXERCISES_REST = "rest_time";
    public static final String PARSE_WODSEXERCISES_WEIGHT = "weight";
    public static final String PARSE_WODSEXERCISES_DURATION = "duration";
    public static final String PARSE_WODSEXERCISES_CATEGORY = "category";

    public static final String PARSE_USERSEXERCISES_USER = "id_user";
    public static final String PARSE_USERSEXERCISES_WODSEXERCISES = "wods_exercises";
    public static final String PARSE_USERSEXERCISES_RESULT = "result";


    public static final String PARSE_EXERCISES_NAME = "name";
    public static final String PARSE_EXERCISES_EQUIPMENT = "id_equipment";
    public static final String PARSE_EXERCISES_ICONID = "icon_id";

    public static final String PARSE_EQUIPMENT_NAME = "name";
    public static final String PARSE_EQUIPMENT_GYM = "id_gym";

    public static final String PARSE_GYMS_NAME = "gym_name";

    public static final String PARSE_GYMSBEACONS_GYM = "id_gym";
    public static final String PARSE_GYMSBEACONS_BEACON = "id_beacon";
    public static final String PARSE_GYMSBEACONS_EQUIPMENT = "id_equipment";

    public static final String PARSE_USERSGYMS_USER = "id_user";
    public static final String PARSE_USERSGYMS_GYM = "id_gym";

    public static final String PARSE_USERSRECORDS_EXERCISE = "id_exercise";
    public static final String PARSE_USERSRECORDS_USER = "id_user";
    public static final String PARSE_USERSRECORDS_RECORD = "personal_record";

    public static final String SHARED_PREFERENCES_APP = "shared_preferences_app";
    public static final String SHARED_PREFERENCES_ID_WOD = "id_wod";
    public static final String SHARED_PREFERENCES_ID_EXERCISE = "id_exercise";


    public static int findIcon(int ex){
        switch(ex){
            case 1:
                return R.drawable.cyclette;
            case 2:
                return R.drawable.rowergometer;
            case 3:
                return R.drawable.free_weights_icon;
            case 4:
                return R.drawable.accessories_icon;
            case 5:
                return R.drawable.treadmill;
            case 6:
                return R.drawable.step;
            default:
                return R.drawable.strength_icon;
        }
    }

    public static int findCategory(int category){
        switch (category){
            case 1:
                return R.drawable.cardio_icon;
            case 2:
                return R.drawable.strength_icon;
            default:
                return R.drawable.strength_icon;
        }
    }

}
