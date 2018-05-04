package com.example.aadityasuri.awaam.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.aadityasuri.awaam.ItemClickListener;
import com.example.aadityasuri.awaam.R;

/**
 * Created by aadityasuri on 23/03/18.
 */

public class UserVIewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public CardView mCardView;
    public TextView mName;
    public TextView Organisation;
    public TextView verify;
    public TextView decline;
    private ItemClickListener itemClickListener;

    public UserVIewHolder(View itemView) {
        super(itemView);
        mCardView = (CardView) itemView.findViewById(R.id.user_card);
        itemView.setOnClickListener(this);
        mName = (TextView) itemView.findViewById(R.id.username);
        Organisation = (TextView) itemView.findViewById(R.id.userorganisation);
        verify = (TextView) itemView.findViewById(R.id.verify);
        decline=(TextView)itemView.findViewById(R.id.decline);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition());
    }
}

