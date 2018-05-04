package com.example.aadityasuri.awaam.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aadityasuri.awaam.R;
import com.example.aadityasuri.awaam.UserClass;

import java.util.ArrayList;

/**
 * Created by aadityasuri on 23/03/18.
 */

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserVIewHolder> {
    ArrayList<UserClass> users;


    public UserRecyclerAdapter(ArrayList<UserClass> userRecieved) {
        users = userRecieved;
    }

    @Override
    public UserVIewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.usercard, parent, false);
        UserVIewHolder vh = new UserVIewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(UserVIewHolder holder, int position) {
        holder.mName.setText(users.get(position).getmName());
        holder.Organisation.setText(users.get(position).getmOrganisation());
        holder.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


}

