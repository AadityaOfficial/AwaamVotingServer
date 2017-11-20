
package com.example.aadityasuri.awaam;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aadityasuri.awaam.Adapters.SpinnerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SignUpScreen extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ArrayList<String> organisationNames;
    EditText email;
    EditText password;
    int statuscode;
    TextView signup;
    TextView login;
    String signUpUrl = "http://aaditya21396.pythonanywhere.com/registeruser";
    String loginUrl = "http://aaditya21396.pythonanywhere.com/loginuser";
    String getOrgUrl = "http://aaditya21396.pythonanywhere.com/getorg";
    String selectedOrg;
    SessionManager session;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        final RequestQueue signUpRequest = Volley.newRequestQueue(SignUpScreen.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);


        Spinner org = (Spinner) findViewById(R.id.spinnner);
        organisationNames = new ArrayList<>();
        org.setOnItemSelectedListener(this);
        final SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getApplicationContext(), organisationNames);
        org.setAdapter(spinnerAdapter);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, getOrgUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        JSONArray orgArray = null;
                        try {
                            orgArray = response.getJSONArray("Organistaions");
                            for (int i = 0; i < orgArray.length(); i++) {
                                JSONObject currentOrg = orgArray.getJSONObject(i);
                                String orgName = currentOrg.getString("name");
                                organisationNames.add(orgName);
                            }
                            spinnerAdapter.notifyDataSetChanged();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", "Dont Know");
                    }
                }


        );

        signUpRequest.add(getRequest);


        signup = (TextView) findViewById(R.id.submitButton);
        login = (TextView) findViewById(R.id.loginButton);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = (EditText) findViewById(R.id.email_id);
                password = (EditText) findViewById(R.id.password);
                final String username = email.getText().toString();
                String passw = password.getText().toString();
                if (username.equals("") || passw.equals("")) {
                    Toast.makeText(getApplicationContext(), "Blank username or password not allowed", Toast.LENGTH_LONG).show();
                } else {
                    Map<String, String> jsonParams = new HashMap<String, String>();
                    jsonParams.put("username", username);
                    jsonParams.put("password", passw);
                    jsonParams.put("status", "0");
                    jsonParams.put("role", "0");
                    JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, signUpUrl, new JSONObject(jsonParams),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    Log.e("Response : ", String.valueOf(response));
                                    Toast.makeText(getApplicationContext(), "Register Successful, Now LogIn", Toast.LENGTH_LONG).show();
//                                    Intent pinIntent = new Intent(SignUpScreen.this, .class);
//                                    startActivity(pinIntent);

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
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            return headers;
                        }
                    };
                    signUpRequest.add(postRequest);
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = (EditText) findViewById(R.id.email_id);
                password = (EditText) findViewById(R.id.password);
                session = new SessionManager(getApplicationContext());
                final String username = email.getText().toString();
                final String passw = password.getText().toString();
                if (username.equals("") || passw.equals("")) {
                    Toast.makeText(getApplicationContext(), "Blank username or password not allowed", Toast.LENGTH_LONG).show();
                } else {
                    Map<String, String> jsonParams = new HashMap<String, String>();
                    jsonParams.put("username", username);
                    jsonParams.put("password", passw);
                    JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, loginUrl, new JSONObject(jsonParams),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("Response of req ", String.valueOf(response));
                                    String data = String.valueOf(response);
                                    int valid = data.indexOf("valid");
                                    String validString = data.substring(valid + 7);
                                    Log.e("Valid or not", validString);
                                    if (validString.equals("true}")) {

                                        //Created Session
                                        Log.e("selectedd org", selectedOrg);
                                        session.createLoginSession(username, passw, "0", selectedOrg);
                                        Intent pinIntent = new Intent(SignUpScreen.this, FingerPrint.class);
                                        pinIntent.putExtra("username", username);
                                        pinIntent.putExtra("password", passw);
                                        startActivity(pinIntent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Invalid Login Credentials", Toast.LENGTH_LONG).show();
                                    }
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
                                        Toast.makeText(getApplicationContext(), "Invalid Credentials / Not Verified", Toast.LENGTH_LONG).show();
                                    } catch (UnsupportedEncodingException e) {
                                        // exception
                                    }

                                }
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            return headers;
                        }


                    };
                    signUpRequest.add(postRequest);
                }
            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedOrg = organisationNames.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

