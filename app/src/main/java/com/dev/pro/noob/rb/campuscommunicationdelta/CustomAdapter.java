package com.dev.pro.noob.rb.campuscommunicationdelta;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;


public class CustomAdapter extends ArrayAdapter<String> {
    private Context cont;
    Random r = new Random();

    public CustomAdapter(Context context, ArrayList<String> resource) {
        super(context, R.layout.custom, resource);
        cont = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater cheenisInflater = LayoutInflater.from(getContext());
        View customview = cheenisInflater.inflate(R.layout.custom, parent, false);
        String complete = getItem(position);
        Calendar c = Calendar.getInstance();
        TextView identitytext = (TextView) customview.findViewById(R.id.identitytext);
        TextView posttext = (TextView) customview.findViewById(R.id.posttext);
        TextView times = (TextView) customview.findViewById(R.id.timestamp);
        ImageView v = (ImageView) customview.findViewById(R.id.strip);
        int color = Color.argb(255, r.nextInt(256), r.nextInt(256), r.nextInt(256));
        v.setBackgroundColor(color);
        try {
            JSONObject j = new JSONObject(complete);
            identitytext.setText(j.getString("sender") + " posted");
            posttext.setText(j.getString("message"));
            String[] timestamp = j.getString("timestamp").split(" ");
            String[] date = timestamp[0].split("-");
            String[] time = timestamp[1].split(":");
            if (c.get(Calendar.YEAR) != Integer.parseInt(date[0])) {
                int t = c.get(Calendar.YEAR) - Integer.parseInt(date[0]);
                times.setText(t + " years ago");
            } else if (1 + c.get(Calendar.MONTH) != Integer.parseInt(date[1])) {
                int t = 1 + c.get(Calendar.MONTH) - Integer.parseInt(date[1]);
                times.setText(t + " months ago");
            } else if (c.get(Calendar.DAY_OF_MONTH) != Integer.parseInt(date[2])) {
                int t = c.get(Calendar.DAY_OF_MONTH) - Integer.parseInt(date[2]);
                if (t == 1) times.setText("yesterday");
                else times.setText(t + " days ago");
            } else {
                times.setText("today at " + time[0] + ":" + time[1]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return customview;
    }

}