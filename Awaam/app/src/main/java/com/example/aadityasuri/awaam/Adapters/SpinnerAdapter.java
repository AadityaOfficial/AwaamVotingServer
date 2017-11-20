package com.example.aadityasuri.awaam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.aadityasuri.awaam.R;

import java.util.ArrayList;

/**
 * Created by aadityasuri on 26/10/17.
 */

public class SpinnerAdapter extends BaseAdapter{

    Context context;
    ArrayList<String> orgNames;
    LayoutInflater inflter;

    public SpinnerAdapter(Context applicationContext,ArrayList<String> orgnames){
        this.context=applicationContext;
        this.orgNames=orgnames;
        inflter=(LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return orgNames.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.inside_spinner, null);
        TextView names=(TextView)convertView.findViewById(R.id.spinnerText);
        names.setText(orgNames.get(position));
        return convertView;
    }
}