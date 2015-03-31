package it.polimi.jaa.mobilefitness;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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

    private void registrationQuery(final View view){
        //mDialog.show();

        String urlServer = "http://192.168.1.187:80/users";
        String urlString = "/users";

        try {
            urlString = URLEncoder.encode(urlServer, "UTF-8");
        } catch (UnsupportedEncodingException e) {

            // if this fails for some reason, let the user know why
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Create a client to perform networking
        AsyncHttpClient client = new AsyncHttpClient();
        client.setProxy("192.168.1.187",80);
        RequestParams params = new RequestParams();
        params.put("email", emailText.getText().toString());
        params.put("password", passwordText.getText().toString());
        params.put("name", nameText.getText().toString());
        params.put("surname", surnameText.getText().toString());
        params.put("weight", weightText.getText().toString());
        params.put("height", heightText.getText().toString());
        params.put("birthdate", birthDateText.getText().toString());

        //client.get(urlServer,new JsonHttpResponseHandler());

        client.post(urlServer, params,  new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, String response) {

                if(response.equals("success")) {
                    //TODO: CREARE TUTTO QUELLO CHE SERVE IN LOCALE (DB)
                    Toast.makeText(view.getContext(), "Registration Successful " + nameText.getText().toString(), Toast.LENGTH_LONG).show();
                    Intent mainActivityIntent = new Intent(view.getContext(), MainActivity.class);
                    startActivity(mainActivityIntent);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
                if (statusCode == 401) {

                    Log.d("onFailure: ", response);
                }
            }

        });

        //mDialog.dismiss();
    }
}