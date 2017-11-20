package com.example.aadityasuri.awaam;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aadityasuri on 23/10/17.
 */

public class ElectionDetailed extends AppCompatActivity {
    TextView startTime;
    TextView endTime;
    TextView heading;
    TextView para;
    String Votepost = "http://aaditya21396.pythonanywhere.com/voteelection/";
    RelativeLayout group;
    int id;
    String vote1 = "False", vote2 = "False";
    TextView yes, no;
    TextView castVote;
    SessionManager session;
    HashMap<String, String> userdetail;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        userdetail = session.getUserDetails();
        final RequestQueue votingRequest = Volley.newRequestQueue(ElectionDetailed.this);
        final String username = userdetail.get(SessionManager.KEY_EMAIL);
        final String password = userdetail.get(SessionManager.KEY_PASS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.election_detailed_view);
        startTime = (TextView) findViewById(R.id.start_date);
        endTime = (TextView) findViewById(R.id.end_date);
        heading = (TextView) findViewById(R.id.heading);
        para = (TextView) findViewById(R.id.paragraph);
        group = (RelativeLayout) findViewById(R.id.votegroup);
        castVote = (TextView) findViewById(R.id.cast_vote);
        final TextView dataResult = (TextView) findViewById(R.id.hidden_status);
        String startDate = getIntent().getStringExtra("startTime");
        String totalStartDate = getTimeFormatted(startDate);
        startTime.setText(totalStartDate);
        String endDate = getIntent().getStringExtra("endTime");
        String endDate2 = endDate + " IST";
        String totalEndDate = getTimeFormatted(endDate);
        DateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss z");
        Date date = new Date();
        String now = dt.format(date);
        int electionid = 0;
        electionid = getIntent().getIntExtra("id", electionid);
        if (Votepost.length() <= 52) {
            Votepost = Votepost + electionid;
        }
        try {
            Date nowD = dt.parse(now);
            Date end = dt.parse(endDate2);
            long unixTimeEnd = (long) end.getTime();
            Log.e("end", unixTimeEnd + "");
            long unixTimeNow = (long) nowD.getTime();
            Log.e("start", unixTimeNow + "");

            long difference = unixTimeEnd - unixTimeNow;
            Log.e("difference", difference + "");
            if (difference <= 0) {
                castVote.setVisibility(View.GONE);
                group.setVisibility(View.GONE);
                getStatusMethod(votingRequest, dataResult, username, password);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        endTime.setText(totalEndDate);
        String head = getIntent().getStringExtra("heading");
        heading.setText(head);
        String parag = getIntent().getStringExtra("para");
        para.setText(parag);


        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Votepost, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Value GET", String.valueOf(response));
                        try {
                            String total = response.getString("total_votes");
                            String positive = response.getString("positive_votes");
                            String negative = response.getString("negative_votes");
                            String user_votes = response.getString("user_votes");
                            if (user_votes.contains(username)) {
                                castVote.setVisibility(View.GONE);
                                group.setVisibility(View.GONE);
                            }
                            dataResult.setText("Total Votes :" + total + "\n" + "In Favour: " + positive + "  " + "Against: " + negative);
//                                    dataResult.setText("Hello Guys");
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

        votingRequest.add(getRequest);


        yes = (TextView) findViewById(R.id.yes);
        no = (TextView) findViewById(R.id.no);
        yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                group.setVisibility(View.GONE);
                castVote.setVisibility(View.GONE);
                Log.e("Value Vote", vote1 + "  diff " + vote2);
                Map<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("username", username);
                jsonParams.put("positive_vote", "True");
                jsonParams.put("negative_vote", "False");
                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.PUT, Votepost, new JSONObject(jsonParams),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                Log.e("Response : ", String.valueOf(response));
                                Toast.makeText(getApplicationContext(), "Vote Successful", Toast.LENGTH_LONG).show();


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
                                    Toast.makeText(getApplicationContext(), "Invalid Selection", Toast.LENGTH_LONG).show();
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
                votingRequest.add(postRequest);

            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.setVisibility(View.GONE);
                castVote.setVisibility(View.GONE);
                Log.e("Value Vote", vote1 + "  diff " + vote2);
                Map<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("username", username);
                jsonParams.put("positive_vote", "False");
                jsonParams.put("negative_vote", "True");
                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.PUT, Votepost, new JSONObject(jsonParams),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                Log.e("Response : ", String.valueOf(response));
                                Toast.makeText(getApplicationContext(), "Vote Successful", Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(getApplicationContext(), "Invalid Selection", Toast.LENGTH_LONG).show();
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
                votingRequest.add(postRequest);
            }
        });


    }


    public String getTimeFormatted(String timeObtained) {
        String getDate = timeObtained.substring(0, 10);
        String getTime = timeObtained.substring(10);
        String totalStartDate = getDate + "\n" + getTime;
        return totalStartDate;
    }

    public void getStatusMethod(RequestQueue votingRequest, final TextView dataResult, final String username, final String password) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Votepost, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Value GET", String.valueOf(response));
                        try {
                            String total = response.getString("total_votes");
                            String positive = response.getString("positive_votes");
                            String negative = response.getString("negative_votes");
                            dataResult.setText("Total Votes :" + total + "\n" + "In Favour: " + positive + "  " + "Against: " + negative);
//                                    dataResult.setText("Hello Guys");
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

        votingRequest.add(getRequest);

    }

}
