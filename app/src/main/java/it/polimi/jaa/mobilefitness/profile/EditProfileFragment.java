package it.polimi.jaa.mobilefitness.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import it.polimi.jaa.mobilefitness.R;
import it.polimi.jaa.mobilefitness.utils.UserInfo;

/**
 * Created by Jacopo on 02/04/2015.
 */
public class EditProfileFragment extends Fragment {

    SharedPreferences mSharedPreferences;
    private static final String PREFS = "prefs";
    private static final String PREF_BIRTHDATE = "birthdate";
    private static final String PREF_WEIGHT = "weight";
    private static final String PREF_HEIGHT = "height";

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    EditText editTextProfile;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile_edit, container, false);
        editTextProfile = (EditText)rootView.findViewById(R.id.editTextProfile);
        editTextProfile.setHint(getArguments().getString("value"));
        switch (getArguments().getString("value")){
            case PREF_BIRTHDATE:
                editTextProfile.setInputType(InputType.TYPE_CLASS_DATETIME);
                dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ITALY);
                setDateTimeField();

                break;
            case PREF_WEIGHT:
                editTextProfile.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case PREF_HEIGHT:
                editTextProfile.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            default:

        }
        Button button = (Button) rootView.findViewById(R.id.modify_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean valid = true;
                //CHECK INPUT
                if(editTextProfile.getText().toString().length()==0) {
                    editTextProfile.setError(getString(R.string.error_field_required));
                    valid = false;
                }



                if(valid){
                    mSharedPreferences = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor e = mSharedPreferences.edit();

                    e.putString(getArguments().getString("value"), editTextProfile.getText().toString());
                    e.apply();

                    //remove soft keyboard
                    View focus = getActivity().getCurrentFocus();
                    if(focus!= null) {
                        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(focus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }

                    getFragmentManager().popBackStack();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextProfile.requestFocus();
        if(!getArguments().getString("value").equals(PREF_BIRTHDATE)) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editTextProfile, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void setDateTimeField() {
        editTextProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
                //remove soft keyboard
                View focus = getActivity().getCurrentFocus();
                if(focus!= null) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(focus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                editTextProfile.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }
}
