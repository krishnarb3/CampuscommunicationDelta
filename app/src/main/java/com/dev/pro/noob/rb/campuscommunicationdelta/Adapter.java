package com.dev.pro.noob.rb.campuscommunicationdelta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class Adapter extends ArrayAdapter<String> {
    private Context cont;
    Random r = new Random();

    public Adapter(Context context, ArrayList<String> resource) {
        super(context, R.layout.taggy, resource);
        cont = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater cheenisInflater = LayoutInflater.from(getContext());
        View customview = cheenisInflater.inflate(R.layout.taggy, parent, false);
        String complete = getItem(position);
        TextView t = (TextView) customview.findViewById(R.id.textView4);
        t.setText(complete);
        return customview;
    }

}