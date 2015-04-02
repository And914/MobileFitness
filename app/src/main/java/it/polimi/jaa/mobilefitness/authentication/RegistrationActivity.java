package it.polimi.jaa.mobilefitness.authentication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import it.polimi.jaa.mobilefitness.MainActivity;
import it.polimi.jaa.mobilefitness.R;
import it.polimi.jaa.mobilefitness.utils.Utils;

/**
 * Created by andre on 31/03/15.
 */

public class RegistrationActivity extends Activity implements View.OnClickListener {

    //UI References
    private EditText birthDateText;
    private EditText nameText;
    private EditText surnameText;
    private EditText weightText;
    private EditText heightText;
    private EditText emailText;
    private EditText passwordText;
    private Button registrationButton;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    private ProgressDialog mDialog;

    SharedPreferences mSharedPreferences;
    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";
    private static final String PREF_SURNAME = "surname";
    private static final String PREF_BIRTHDATE = "birthdate";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_WEIGHT = "weight";
    private static final String PREF_HEIGHT = "height";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ITALY);

        findViewsById();

        setDateTimeField();

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Registration..");
        mDialog.setCancelable(false);

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkData())
                    registrationQuery(view);
            }
        });
    }

    private void findViewsById() {
        birthDateText = (EditText) findViewById(R.id.registration_birthdate);
        birthDateText.setInputType(InputType.TYPE_NULL);

        registrationButton = (Button) findViewById(R.id.email_registration_button);
        emailText = (EditText) findViewById(R.id.registration_email);
        nameText = (EditText) findViewById(R.id.registration_name);
        surnameText = (EditText) findViewById(R.id.registration_surname);
        weightText = (EditText) findViewById(R.id.registration_weight);
        heightText = (EditText) findViewById(R.id.registration_height);
        passwordText = (EditText) findViewById(R.id.registration_password);

    }

    private void setDateTimeField() {
        birthDateText.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                birthDateText.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


    @Override
    public void onClick(View view) {
        if(view == birthDateText) {
            datePickerDialog.show();
        }
    }
    
    private boolean checkData(){
        boolean valid = true;
        if(!emailText.getText().toString().contains("@")){
            emailText.setError(getString(R.string.reg_error_email));
            valid = false;
        }
        if(passwordText.getText().toString().length()<4){
            passwordText.setError(getString(R.string.reg_error_psw));
            valid = false;
        }
        if(nameText.getText().toString().equals("")){
            nameText.setError(getString(R.string.reg_error_name));
            valid = false;
        }
        if(surnameText.getText().toString().equals("")){
            surnameText.setError(getString(R.string.reg_error_surname));
            valid = false;
        }
        if(birthDateText.getText().toString().equals("")){
            birthDateText.setError(getString(R.string.reg_error_birthdate));
            valid = false;
        }
        if(weightText.getText().toString().equals("")){
            weightText.setError(getString(R.string.reg_error_weight));
            valid = false;
        }
        if(heightText.getText().toString().equals("")){
            heightText.setError(getString(R.string.reg_error_height));
            valid = false;
        }

        return valid;
    }

    private void registrationQuery(final View view){
        mDialog.show();

        String urlServer = Utils.server_ip + "/users";

        // Create a client to perform networking
        AsyncHttpClient client = new AsyncHttpClient();
        //client.setProxy("192.168.1.187",80);
        RequestParams params = new RequestParams();
        params.put("email", emailText.getText().toString());
        params.put("password", passwordText.getText().toString());
        params.put("name", nameText.getText().toString());
        params.put("surname", surnameText.getText().toString());
        params.put("weight", weightText.getText().toString());
        params.put("height", heightText.getText().toString());
        params.put("birthdate", birthDateText.getText().toString());
        

        client.post(urlServer, params,  new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, String response) {

                if(response.equals("success")) {
                    mDialog.dismiss();
                    setPreferences();
                    Toast.makeText(view.getContext(), "Registration Successful " + nameText.getText().toString(), Toast.LENGTH_LONG).show();
                    Intent mainActivityIntent = new Intent(view.getContext(), MainActivity.class);
                    startActivity(mainActivityIntent);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
                if (statusCode == 401) {
                    mDialog.dismiss();
                    Log.d("onFailure: ", response);
                }
            }

        });

        setPreferences();
        Toast.makeText(view.getContext(), "Registration Successful " + nameText.getText().toString(), Toast.LENGTH_LONG).show();
        Intent mainActivityIntent = new Intent(view.getContext(), MainActivity.class);
        startActivity(mainActivityIntent);

    }

    private void setPreferences(){
        // Access the device's key-value storage
        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.putString(PREF_NAME, nameText.getText().toString());
        e.putString(PREF_BIRTHDATE, birthDateText.getText().toString());
        e.putString(PREF_EMAIL, emailText.getText().toString());
        e.putString(PREF_HEIGHT, heightText.getText().toString());
        e.putString(PREF_WEIGHT, weightText.getText().toString());
        e.putString(PREF_SURNAME, surnameText.getText().toString());

        e.apply();

    }
}