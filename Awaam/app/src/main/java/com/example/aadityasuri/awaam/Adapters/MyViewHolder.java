package com.example.aadityasuri.awaam.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.aadityasuri.awaam.ItemClickListener;
import com.example.aadityasuri.awaam.R;

/**
 * Created by aadityasuri on 22/10/17.
 */

public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public CardView mCardView;
    public TextView mName;
    public TextView mTime;
    public TextView mHeading;
    private ItemClickListener itemClickListener;

    public MyViewHolder(View itemView) {
        super(itemView);
        mCardView = (CardView) itemView.findViewById(R.id.card_view);
        itemView.setOnClickListener(this);
        mName = (TextView) itemView.findViewById(R.id.card_title);
        mTime = (TextView) itemView.findViewById(R.id.card_time);
        mHeading = (TextView) itemView.findViewById(R.id.heading);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition());
    }
}
