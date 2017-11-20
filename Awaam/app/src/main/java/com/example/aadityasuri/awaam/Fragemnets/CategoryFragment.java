package com.example.aadityasuri.awaam.Fragemnets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aadityasuri.awaam.Adapters.CategorySpinnerAdapter;
import com.example.aadityasuri.awaam.Adapters.RecyclerAdapterClass;
import com.example.aadityasuri.awaam.Election;
import com.example.aadityasuri.awaam.R;
import com.example.aadityasuri.awaam.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aadityasuri on 22/10/17.
 */

public class CategoryFragment extends Fragment {
    String electionUrl3 = "http://aaditya21396.pythonanywhere.com/elections";
    SessionManager session;
    HashMap<String, String> userdetail;
    RecyclerAdapterClass adapter;
    ArrayList<Election> recentElections;
    String category;
    ArrayList<String> catNames;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getActivity().getApplicationContext());
        session.checkLogin();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        session.checkLogin();
        final RequestQueue electionRequest = Volley.newRequestQueue(getContext().getApplicationContext());
        userdetail = session.getUserDetails();
        recentElections = new ArrayList<>();
        final String username = userdetail.get(SessionManager.KEY_EMAIL);
        final String password = userdetail.get(SessionManager.KEY_PASS);
        final String selectedOrg = userdetail.get(SessionManager.KEY_ORG);
        if (!electionUrl3.contains("Organisation")) {
            electionUrl3 = electionUrl3 + "/" + selectedOrg;
        }
        View rootView = inflater.inflate(R.layout.category_elections, container, false);
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recycler_list_category);
        rv.setHasFixedSize(true);
        //Spinner filling
        Spinner cat = (Spinner) rootView.findViewById(R.id.category_spinner);
        catNames = new ArrayList<>();
        catNames.add("Miscellaneous");
        catNames.add("Timings");
        catNames.add("Human Resource");
        catNames.add("Technical");
        catNames.add("Finance");
        cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = catNames.get(position);
                if (!electionUrl3.contains("Technical") && !electionUrl3.contains("Miscellaneous") && !electionUrl3.contains("Human Resource") && !electionUrl3.contains("Timings") && !electionUrl3.contains("Finance")) {
                    electionUrl3 = electionUrl3 + "/" + selectedOrg;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final CategorySpinnerAdapter spinnerAdapter = new CategorySpinnerAdapter(getContext().getApplicationContext(), catNames);
        cat.setAdapter(spinnerAdapter);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, electionUrl3, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // display response

                        Election newElection;
                        JSONArray elections = null;
                        try {
                            elections = response.getJSONArray("Elections");
                            for (int i = elections.length() - 1; i >= 0; i--) {
                                JSONObject currentElection = elections.getJSONObject(i);
                                int id = currentElection.getInt("id");
                                String name = currentElection.getString("name");
                                String heading = currentElection.getString("heading");
                                String para = currentElection.getString("paragraph");
                                String category = currentElection.getString("category");
                                String startTime = currentElection.getString("start_time");
                                String endTime = currentElection.getString("end_time");
                                int inFavour = currentElection.getInt("in_favour");
                                int notInFavour = currentElection.getInt("not_in_favour");
                                int status = currentElection.getInt("status");
                                int totalVotes = currentElection.getInt("total_votes");
                                String userFavour = currentElection.getString("user_favour");
                                String userAgainst = currentElection.getString("user_against");
                                String totalUser = currentElection.getString("user_total");
                                newElection = new Election(name, heading, para, startTime, endTime, id, category, inFavour, notInFavour, totalVotes, status, userFavour, userAgainst, totalUser);
                                recentElections.add(newElection);
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

        electionRequest.add(getRequest);


        adapter = new RecyclerAdapterClass(recentElections);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        return rootView;

    }


}


