package com.example.aadityasuri.awaam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aadityasuri.awaam.Adapters.UserRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VerifyUser extends AppCompatActivity {
    String userurl = "http://aaditya21396.pythonanywhere.com/validatuser/";
    SessionManager session;
    HashMap<String, String> userdetail;
    RecyclerView recyclerView;
    UserRecyclerAdapter adapter;
    ArrayList<UserClass> userList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_user);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        final RequestQueue userRequest = Volley.newRequestQueue(getApplicationContext());
        userdetail = session.getUserDetails();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_list_user);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);


        userList = new ArrayList<>();
        final String username = userdetail.get(SessionManager.KEY_EMAIL);
        final String password = userdetail.get(SessionManager.KEY_PASS);
        final String selectedOrg = userdetail.get(SessionManager.KEY_ORG);
        userurl = userurl + username;


        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, userurl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.e("user response", response.toString());
                        UserClass newUser;
                        JSONArray users = null;
                        try {
                            users = response.getJSONArray("Users");
                            for (int i = users.length() - 1; i >= 0; i--) {
                                JSONObject currentUser = users.getJSONObject(i);
                                int userid = currentUser.getInt("id");
                                String name = currentUser.getString("username");
                                Log.e("user response", name);
                                String gender = currentUser.getString("gender");
                                String role = currentUser.getString("role");
                                String organisation = currentUser.getString("organisation");
                                int inFavour = currentUser.getInt("votes_in_favour");
                                int notInFavour = currentUser.getInt("votes_against");
                                int status = currentUser.getInt("status");
                                int totalVotes = currentUser.getInt("total_votes");
                                newUser = new UserClass(name, userid, inFavour, notInFavour, totalVotes, status, role, gender, organisation);
                                userList.add(newUser);
                            }
                            adapter.notifyDataSetChanged();
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

        userRequest.add(getRequest);
        adapter = new UserRecyclerAdapter(userList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}
