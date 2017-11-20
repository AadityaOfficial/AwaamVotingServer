package com.example.aadityasuri.awaam;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aadityasuri on 21/10/17.
 */

public class PinScreen extends AppCompatActivity {
    EditText pinText;
    TextView submitButton;
    int statuscode;
    String pinUrl = "http://aaditya21396.pythonanywhere.com/userpin";
    SessionManager session;
    HashMap<String, String> userdetail;
    String value;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_screen);
        pinText = (EditText) findViewById(R.id.pin_text);


        final RequestQueue pinRequest = Volley.newRequestQueue(PinScreen.this);
        session = new SessionManager(getApplicationContext());
        final String username = getIntent().getStringExtra("username");
        final String password = getIntent().getStringExtra("password");
        pinUrl = pinUrl + "/" + username;
        Log.e("loginURl", pinUrl);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, pinUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try {
                            value = response.getString("user_pin");
//                            session.setPin(value);
                            Log.e("value of pin", value);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", "Dont Know");
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

        pinRequest.add(getRequest);
//        if(session.getPin().equals("null")){
//            Log.e("log pin","This is correct");
//        }
        submitButton = (TextView) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = pinText.getText().toString();
            }
        });
    }


}

