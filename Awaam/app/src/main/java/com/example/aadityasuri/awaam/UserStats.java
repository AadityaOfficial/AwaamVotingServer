package com.example.aadityasuri.awaam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class UserStats extends AppCompatActivity {
    SessionManager session;
    HashMap<String, String> userdetail;
    String userStat = "http://aaditya21396.pythonanywhere.com/userdata/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stats);
        final RequestQueue statRequest = Volley.newRequestQueue(UserStats.this);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        userdetail = session.getUserDetails();
        final String username = userdetail.get(SessionManager.KEY_EMAIL);
        final String password = userdetail.get(SessionManager.KEY_PASS);
        final String selectedOrg = userdetail.get(SessionManager.KEY_ORG);
        userStat = userStat + username;
        File f = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), username + ".jpg");
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ImageView img = (ImageView) findViewById(R.id.user_picture);
        img.setImageBitmap(b);
        img.setRotation(-90);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, userStat, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("userstat", response.toString());
                        TextView usernameTb = (TextView) findViewById(R.id.user_name_stat);
                        usernameTb.setText(username);
                        TextView organisationTb = (TextView) findViewById(R.id.user_org_stat);
                        organisationTb.setText(selectedOrg);



                        try {
                            String negativeVotes = response.getString("negative_vote");
                            String positiveVotes = response.getString("positive_votes");
                            String TotalVotes = response.getString("total_votes");
                            TextView negativeVotesTb = (TextView) findViewById(R.id.negatice_votes_stat);
                            negativeVotesTb.setText(negativeVotes);
                            TextView positiveVotesTb = (TextView) findViewById(R.id.positive_votes_stat);
                            positiveVotesTb.setText(positiveVotes);
                            TextView totalVotesTb = (TextView) findViewById(R.id.total_vote_stat);
                            totalVotesTb.setText(TotalVotes);
                            Log.e("userstat22", negativeVotes + "," + positiveVotes + "," + totalVotesTb);

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

        statRequest.add(getRequest);
    }
}
