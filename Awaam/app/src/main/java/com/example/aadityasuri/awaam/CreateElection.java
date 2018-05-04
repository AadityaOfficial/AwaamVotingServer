package com.example.aadityasuri.awaam;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateElection extends AppCompatActivity implements View.OnClickListener {
    Button startDate, startTime, endDate, endTime;
    EditText startDateText, startTimeText, endDateText, endTimeText, electionName, electionHeading, electionPara;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String name, heading, para, starttime, endtime;
    SessionManager session;
    HashMap<String, String> userdetail;
    String electionUrl = "http://aaditya21396.pythonanywhere.com/elections";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final RequestQueue electionCreateRequest = Volley.newRequestQueue(getApplicationContext());

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        userdetail = session.getUserDetails();
        setContentView(R.layout.activity_create_election);
        electionName = (EditText) findViewById(R.id.election_name);
        electionHeading = (EditText) findViewById(R.id.election_heading);
        electionPara = (EditText) findViewById(R.id.election_para);

        startDate = (Button) findViewById(R.id.btn_start_date);
        startTime = (Button) findViewById(R.id.btn_start_time);
        startDateText = (EditText) findViewById(R.id.start_date);
        startTimeText = (EditText) findViewById(R.id.start_time);

        endTime = (Button) findViewById(R.id.btn_end_time);
        endDate = (Button) findViewById(R.id.btn_end_date);
        endDateText = (EditText) findViewById(R.id.end_date);
        endTimeText = (EditText) findViewById(R.id.end_time);
        startDate.setOnClickListener((View.OnClickListener) this);
        startTime.setOnClickListener((View.OnClickListener) this);
        endDate.setOnClickListener((View.OnClickListener) this);
        endTime.setOnClickListener((View.OnClickListener) this);


        Button submitElection = (Button) findViewById(R.id.submit_button_form);
        submitElection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (electionHeading.length() < 1 || electionName.length() < 1 || electionPara.length() < 1 || startDate.length() < 1 || startTime.length() < 1 || endDate.length() < 1 || endTime.length() < 1) {
                    Toast.makeText(getApplicationContext(), " Please Complete the data", Toast.LENGTH_LONG).show();
                } else {
                    name = electionName.getText().toString();
                    heading = electionHeading.getText().toString();
                    para = electionPara.getText().toString();
                    starttime = startDateText.getText().toString() + " " + startTimeText.getText().toString() + ":00";
                    endtime = endDateText.getText().toString() + " " + endTimeText.getText().toString() + ":00";
                    Log.e("start", starttime);
                    Log.e("end", endtime);
                    final String username = userdetail.get(SessionManager.KEY_EMAIL);
                    final String password = userdetail.get(SessionManager.KEY_PASS);
                    final String selectedOrg = userdetail.get(SessionManager.KEY_ORG);

                    if (!electionUrl.contains("Organisation")) {
                        electionUrl = electionUrl + "/" + selectedOrg;
                    }
                    Map<String, String> jsonParams = new HashMap<String, String>();
                    jsonParams.put("name", name);
                    jsonParams.put("heading", heading);
                    jsonParams.put("status", "0");
                    jsonParams.put("creator", username);
                    jsonParams.put("para", para);
                    jsonParams.put("category", "Miscelleneous");
                    jsonParams.put("start_time", starttime);
                    jsonParams.put("end_time", endtime);
                    jsonParams.put("in_favour", "0");
                    jsonParams.put("not_in_favour", "0");
                    jsonParams.put("total_votes", "0");

                    JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, electionUrl, new JSONObject(jsonParams),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    Log.e("Response : ", String.valueOf(response));
                                    Toast.makeText(getApplicationContext(), "Creation Successful", Toast.LENGTH_LONG).show();
                                    Intent ElectionIntent = new Intent(CreateElection.this, ElectionClass.class);
                                    startActivity(ElectionIntent);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if (error == null || error.networkResponse == null) {

                                        return;
                                    }
                                    String body = "";
                                    final String statusCode = String.valueOf(error.networkResponse.statusCode);
                                    //get status code here
                                    //get response body and parse with appropriate encoding
                                    try {
                                        body = new String(error.networkResponse.data, "UTF-8");
                                        Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
                                    } catch (UnsupportedEncodingException e) {
                                        // exception
                                    }

                                }
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();

                            String credentials = username + ":" + password;
                            String auth = "Basic "
                                    + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                            headers.put("Authorization", auth);
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            return headers;
                        }
                    };
                    electionCreateRequest.add(postRequest);
                }

            }

        });
    }

    @Override
    public void onClick(View v) {

        if (v == startDate) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            startDateText.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == startTime) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            startTimeText.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

        if (v == endDate) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            endDateText.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == endTime) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            endTimeText.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }


}
