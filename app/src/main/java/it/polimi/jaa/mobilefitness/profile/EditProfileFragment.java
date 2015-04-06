package it.polimi.jaa.mobilefitness.profile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import it.polimi.jaa.mobilefitness.R;
import it.polimi.jaa.mobilefitness.utils.Utils;

/**
 * Created by Jacopo on 02/04/2015.
 */
public class EditProfileFragment extends Fragment {

    SharedPreferences mSharedPreferences;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    EditText editTextProfile;

    private ProgressDialog mDialog;

    private String oldValue;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile_edit, container, false);
        editTextProfile = (EditText)rootView.findViewById(R.id.editTextProfile);

        mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Updating..");
        mDialog.setCancelable(false);

        editTextProfile.setHint(getArguments().getString("value"));
        switch (getArguments().getString("value")){
            case Utils.PREF_BIRTHDATE:
                editTextProfile.setInputType(InputType.TYPE_CLASS_DATETIME);
                dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ITALY);
                setDateTimeField();

                break;
            case Utils.PREF_WEIGHT:
                editTextProfile.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case Utils.PREF_HEIGHT:
                editTextProfile.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            default:

        }
        Button button = (Button) rootView.findViewById(R.id.modify_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.show();
                Boolean valid = true;
                //CHECK INPUT
                if(editTextProfile.getText().toString().length()==0) {
                    editTextProfile.setError(getString(R.string.error_field_required));
                    valid = false;
                }



                if(valid){
                    mSharedPreferences = getActivity().getSharedPreferences(Utils.PREFS, Context.MODE_PRIVATE);

                    //save old value
                    oldValue = mSharedPreferences.getString(getArguments().getString("value"), "");

                    SharedPreferences.Editor e = mSharedPreferences.edit();

                    e.putString(getArguments().getString("value"), editTextProfile.getText().toString());
                    e.apply();
                    sendNewProfile();

                }
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextProfile.requestFocus();
        if(!getArguments().getString("value").equals(Utils.PREF_BIRTHDATE)) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editTextProfile, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void sendNewProfile(){
        String urlServer = Utils.server_ip + "/users";

        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREFS, Context.MODE_PRIVATE);

        // Create a client to perform networking
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("email", mSharedPreferences.getString(Utils.PREF_EMAIL,""));
        params.put("name", mSharedPreferences.getString(Utils.PREF_NAME, ""));
        params.put("surname", mSharedPreferences.getString(Utils.PREF_SURNAME, ""));
        params.put("weight", mSharedPreferences.getString(Utils.PREF_WEIGHT,""));
        params.put("height", mSharedPreferences.getString(Utils.PREF_HEIGHT,""));
        params.put("birthdate", mSharedPreferences.getString(Utils.PREF_BIRTHDATE,""));


        client.put(urlServer, params,  new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, String response) {

                if(response.equals("updated")) {
                    mDialog.dismiss();
                    Toast.makeText(getActivity(),"Updated", Toast.LENGTH_LONG).show();
                    //remove soft keyboard
                    View focus = getActivity().getCurrentFocus();
                    if(focus!= null) {
                        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(focus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }

                    getFragmentManager().popBackStack();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
                mDialog.dismiss();
                Toast.makeText(getActivity(),"Error on update", Toast.LENGTH_LONG).show();

                //Rollback
                mSharedPreferences = getActivity().getSharedPreferences(Utils.PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();
                edit.putString(getArguments().getString("value"), oldValue);
                edit.apply();
            }

        });
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
