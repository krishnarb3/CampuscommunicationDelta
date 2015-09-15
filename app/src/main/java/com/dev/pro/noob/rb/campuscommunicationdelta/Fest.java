package com.dev.pro.noob.rb.campuscommunicationdelta;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.dev.pro.noob.rb.campuscommunicationdelta.CommonUtilities.NEW_URL;
import static com.dev.pro.noob.rb.campuscommunicationdelta.CommonUtilities.TAG;
import static com.dev.pro.noob.rb.campuscommunicationdelta.CommonUtilities.start2;

public class Fest extends Fragment
{

    private static final int MAX_ATTEMPTS = 1;

    private OnFragmentInteractionListener mListener;
    String username;
    View v;
    ArrayAdapter cheenisAdapter;

    int update;

    ListView cheenisListView;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<String> posts = new ArrayList<String>(), refreshmes = new ArrayList<>();
    JSONObject tempjson;

    Handler mtoast = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getActivity(), "No messages", Toast.LENGTH_SHORT).show();
        }
    };
    Handler toast = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getActivity(), "Loading", Toast.LENGTH_SHORT).show();
        }
    };
    Handler failtoast = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getActivity(), "Failed connection", Toast.LENGTH_SHORT).show();
        }
    };

    Handler handler = new Handler() {
        @Override
        public synchronized void handleMessage(Message msg) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cheenisAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    int old_id = 0, new_id = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (container == null) {
            return null;
        }
        v = inflater.inflate(R.layout.fragment_fest, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout_1);
        username = mListener.getusername3();

        cheenisListView = (ListView) v.findViewById(R.id.listView_1);
        cheenisAdapter = new CustomAdapter(getActivity(), posts);
        cheenisListView.setAdapter(cheenisAdapter);

        if (start2) {
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    MyDBHandler d = new MyDBHandler(getActivity(), null, null, 1);
                    SQLiteDatabase db = d.getDB();
                    String query = "SELECT * FROM " + "fposts" + " WHERE 1 ORDER BY " + "_id" + " DESC;";
                    Cursor c = db.rawQuery(query, null);
                    //Move to the first row in your results
                    c.moveToFirst();
                    db.close();
                    while (!c.isAfterLast()) {
                        if (c.getString(c.getColumnIndex("post")) != null) {
                            posts.add(c.getString(c.getColumnIndex("post")));
                            handler.sendEmptyMessage(0);
                        }
                        c.moveToNext();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String msg) {
                }
            }.execute(null, null, null);
            start2 = false;
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                swipeRefreshLayout.setRefreshing(true);
                new AsyncTask<Void, Void, String>()
                {
                    @Override
                    protected String doInBackground(Void... params)
                    {
                        update = 1;
                        String msg = "";
                        String serverUrl = NEW_URL;
                        MyDBHandler d = new MyDBHandler(getActivity(), null, null, 1);
                        SQLiteDatabase db = d.getDB();
                        String query = "SELECT * FROM " + "fposts" + " WHERE 1 ORDER BY " + "_id" + " DESC;";
                        Cursor c = db.rawQuery(query, null);
                        //Move to the first row in your results
                        c.moveToFirst();
                        db.close();
                        if (c.getCount() != 0)
                        {
                            new_id = c.getInt(c.getColumnIndex("_id"));
                        }
                        Map<String, String> paramss = new HashMap<String, String>();
                        paramss.put("action_id", "2");
                        paramss.put("latest_msg_id", new_id + "");
                        for (int i = 1; i <= MAX_ATTEMPTS; i++)
                        {
                            Log.d(TAG, "Attempt #" + i + " to register");
                            try
                            {
                                posta(serverUrl, paramss);
                                return msg;
                            } catch (IOException e)
                            {
                                Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                                failtoast.sendEmptyMessage(0);
                                if (i == MAX_ATTEMPTS)
                                {
                                    break;
                                }
                            }
                        }
                        return msg;
                    }

                    @Override
                    protected void onPostExecute(String msg)
                    {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }.execute(null, null, null);

            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        public String getusername3();
    }

    public void clear() {
        MyDBHandler dbHandler = new MyDBHandler(getActivity(), null, null, 1);
        int sizeof = refreshmes.size();
        for (int i = 0; i < sizeof; i++) {
            dbHandler.addName(refreshmes.get(i), "fposts");
        }
    }

    private void posta(String endpoint, Map<String, String> params)
            throws IOException {

        URL url;
        boolean wak = false;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            Log.e("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            CharSequence charSequence = "no_of";

            String line = "";
            try {
                wak = true;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(charSequence))
                        break;
                }
                Log.d("check", line);
                JSONObject js = new JSONObject(line);
                int l = Integer.parseInt(js.getString("no_of_messages"));
                if (l == 0) {
                    mtoast.sendEmptyMessage(0);
                    return;
                }
                JSONArray jsonArray = new JSONArray(js.get("messages").toString());
                refreshmes = new ArrayList<>();
                int i = 0;
                for (; i < l; i++) {
                    tempjson = jsonArray.getJSONObject(i);
                    if (update == 1 && tempjson.getString("sender").equals("fest")) {
                        refreshmes.add(tempjson.toString());
                        posts.add(0, tempjson.toString());
                    }
                }
                handler.sendEmptyMessage(0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        if (wak) {
            if (update == -1) {
                try {
                    old_id = Integer.parseInt(tempjson.getString("msg_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (update == 1) {
                clear();
            }
        } else failtoast.sendEmptyMessage(0);
    }

}
