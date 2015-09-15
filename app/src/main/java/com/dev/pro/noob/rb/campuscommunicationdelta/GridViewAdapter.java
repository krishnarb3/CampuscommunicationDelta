package com.dev.pro.noob.rb.campuscommunicationdelta;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<String>
{
    public static String TAG="TAG";
    public ArrayList<String> tags;
    deletebuttonlistener listener;



    public interface deletebuttonlistener
    {
        public void onButtonclicklistener(String value);
    }
    public void setButtonclicklistener(deletebuttonlistener listener)
    {
        this.listener=listener;
    }
    public GridViewAdapter(Context context, ArrayList<String> tags)
    {
        super(context, R.layout.gridadapter,tags);
        this.tags=tags;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.gridadapter,parent,false);
        TextView textview = (TextView)view.findViewById(R.id.tag);
        textview.setText(tags.get(position));
        Button del_button = (Button)view.findViewById(R.id.delete_button);
        del_button.setTag(position);
        del_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int pos = (int)view.getTag();
                listener.onButtonclicklistener(tags.get(pos));
            }
        });
        Log.d(TAG,"Item created "+position);
        return view;
    }
}
