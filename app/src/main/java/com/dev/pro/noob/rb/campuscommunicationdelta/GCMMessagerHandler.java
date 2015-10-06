package com.dev.pro.noob.rb.campuscommunicationdelta;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import static com.dev.pro.noob.rb.campuscommunicationdelta.CommonUtilities.apprun;


public class GCMMessagerHandler extends IntentService {

    String mes;
    ArrayList<String> refreshmes;
    JSONObject tempjson;
    int new_id;
    int done;
    Handler toasty=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), "New messages available", Toast.LENGTH_LONG).show();
        }
    };
    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String msg = "";
                    String serverUrl = NEW_URL;
                    MyDBHandler d = new MyDBHandler(getApplicationContext(), null, null, 1);
                    SQLiteDatabase db = d.getDB();
                    String query = "SELECT * FROM " + "posts" + " WHERE 1 ORDER BY " + "_id" + " DESC;";
                    Cursor c = db.rawQuery(query, null);
                    //Move to the first row in your results
                    c.moveToFirst();
                    db.close();
                    if (c.getCount() != 0) {
                        new_id = c.getInt(c.getColumnIndex("_id"));
                    }
                    Map<String, String> paramss = new HashMap<String, String>();
                    paramss.put("action_id", "2");
                    paramss.put("latest_msg_id", new_id + "");
                    for (int i = 1; i <= 1; i++) {
                        Log.d(TAG, "Attempt #" + i + " to register");
                        try {
                            posta(serverUrl, paramss);
                            return msg;
                        } catch (IOException e) {
                            Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                        }
                    }
                    return msg;
                }

                @Override
                protected void onPostExecute(String msg) {
                    done=1;
                }
            }.execute(null, null, null);
        }
    };

    public GCMMessagerHandler() {
        super("GCMMessagerHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        mes = extras.getString("data");
        if (mes == null) return;
        if (apprun == true) {
            toasty.sendEmptyMessage(0);
        } else {
            done=0;
            h.sendEmptyMessage(0);
            while(done==0);
            generateNotification(getApplicationContext(), mes);
        }

    }

    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.plane;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;
        notification = new Notification(icon, "CampusComm", when);
        String title = message;

        Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context,"CampusComm", message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(666, notification);
    }

    public void clear() {
        MyDBHandler dbHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
        int sizeof = refreshmes.size();
        for (int i = 0; i < sizeof; i++) {
            try {
                tempjson = new JSONObject(refreshmes.get(i));
                if (tempjson.getString("sender").equals("fest")) {
                    dbHandler.addName(refreshmes.get(i), "fposts");
                } else if (tempjson.getString("sender").equals("director")) {
                    dbHandler.addName(refreshmes.get(i), "dposts");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dbHandler.addName(refreshmes.get(i), "posts");
        }
    }

    private void posta(String endpoint, Map<String, String> params)
            throws IOException {

        URL url;
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
                while ((line = reader.readLine()) != null) {
                    if (line.contains(charSequence))
                        break;
                }
                Log.d("check", line);
                JSONObject js = new JSONObject(line);
                int l = Integer.parseInt(js.getString("no_of_messages"));
                if (l == 0) return;
                JSONArray jsonArray = new JSONArray(js.get("messages").toString());
                refreshmes = new ArrayList<>();
                int i = 0;
                for (; i < l; i++) {
                    tempjson = jsonArray.getJSONObject(i);
                    refreshmes.add(tempjson.toString());
                }
                clear();
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
    }
}
