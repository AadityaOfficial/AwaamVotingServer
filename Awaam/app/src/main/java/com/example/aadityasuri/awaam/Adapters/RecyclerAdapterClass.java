package com.example.aadityasuri.awaam.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aadityasuri.awaam.Election;
import com.example.aadityasuri.awaam.ElectionDetailed;
import com.example.aadityasuri.awaam.ItemClickListener;
import com.example.aadityasuri.awaam.R;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by aadityasuri on 22/10/17.
 */

public class RecyclerAdapterClass extends RecyclerView.Adapter<MyViewHolder> {
    ArrayList<Election> elections;

    public RecyclerAdapterClass(ArrayList<Election> electionsRecieved) {
        elections = electionsRecieved;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mName.setText(elections.get(position).getmName());
        try {
            holder.mTime.setText(elections.get(position).getTimeRemaining());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.mHeading.setText(elections.get(position).getmHeading());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent detailedIntent = new Intent(view.getContext(), ElectionDetailed.class);
                detailedIntent.putExtra("id",elections.get(position).getmElectionId());
                detailedIntent.putExtra("heading", elections.get(position).getmHeading());
                detailedIntent.putExtra("para", elections.get(position).getmPara());
                detailedIntent.putExtra("startTime", elections.get(position).getmStart_date());
                detailedIntent.putExtra("endTime", elections.get(position).getmEnd_date());
                view.getContext().startActivity(detailedIntent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return elections.size();
    }


}
