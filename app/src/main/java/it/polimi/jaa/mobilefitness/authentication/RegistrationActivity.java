package it.polimi.jaa.mobilefitness.authentication;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.polimi.jaa.mobilefitness.MainActivity;
import it.polimi.jaa.mobilefitness.R;
import it.polimi.jaa.mobilefitness.backend.BackendFunctions;
import it.polimi.jaa.mobilefitness.backend.callbacks.Callback;
import it.polimi.jaa.mobilefitness.utils.Utils;

/**
 * Created by andre on 31/03/15.
 */

public class RegistrationActivity extends ActionBarActivity{

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
        birthDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
                //remove soft keyboard
                View focus = getCurrentFocus();
                if(focus!= null) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(focus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                birthDateText.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }
    
    private boolean checkData(){
        boolean valid = true;
        if(!isEmailValid(emailText.getText().toString())){
            emailText.setError(getString(R.string.reg_error_email));
            valid = false;
        }
        if(passwordText.getText().toString().length()<=4){
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
        //TODO:verificare che sia una data
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

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private void registrationQuery(final View view){
        mDialog.show();

        Date date = null;
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ITALIAN);

        try {
            date = format.parse(birthDateText.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        BackendFunctions.BFRegistration(emailText.getText().toString(), passwordText.getText().toString(), nameText.getText().toString(), surnameText.getText().toString(), date, Integer.parseInt(weightText.getText().toString()), Integer.parseInt(heightText.getText().toString()), new Callback() {
            @Override
            public void done() {
                mDialog.dismiss();
                setPreferences();
                BackendFunctions.BFRegisterDevice(new Callback() {
                    @Override
                    public void done() {
                        Toast.makeText(view.getContext(), "Registration Successful " + nameText.getText().toString(), Toast.LENGTH_LONG).show();
                        Intent mainActivityIntent = new Intent(view.getContext(), MainActivity.class);
                        startActivity(mainActivityIntent);
                    }

                    @Override
                    public void error(int error) {
                        Toast.makeText(view.getContext(),getString(error), Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void error(int error) {
                mDialog.dismiss();
                Toast.makeText(view.getContext(),getString(error), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setPreferences(){
        // Access the device's key-value storage
        mSharedPreferences = getSharedPreferences(Utils.PREFS, MODE_PRIVATE);
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.putString(Utils.PREF_NAME, nameText.getText().toString());
        e.putString(Utils.PREF_BIRTHDATE, birthDateText.getText().toString());
        e.putString(Utils.PREF_EMAIL, emailText.getText().toString());
        e.putString(Utils.PREF_HEIGHT, heightText.getText().toString());
        e.putString(Utils.PREF_WEIGHT, weightText.getText().toString());
        e.putString(Utils.PREF_SURNAME, surnameText.getText().toString());

        e.apply();

    }
}