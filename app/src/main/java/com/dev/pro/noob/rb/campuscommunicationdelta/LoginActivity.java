package com.dev.pro.noob.rb.campuscommunicationdelta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static com.dev.pro.noob.rb.campuscommunicationdelta.CommonUtilities.SERVER_URL;

public class LoginActivity extends ActionBarActivity
{
    public String TAG="TAG";
    ProgressDialog pd;
    Button button;
    String name,password;
    EditText edit_name,edit_passwd;
    GoogleCloudMessaging gcm;
    String regid = new String();
    String PROJECT_NUMBER = "835229264934";
    private static final int MAX_ATTEMPTS = 3;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    TelephonyManager t;
    View view;
    Boolean go=false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_login);

        edit_name = (EditText)findViewById(R.id.username);
        edit_passwd = (EditText)findViewById(R.id.password);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        button = (Button)findViewById(R.id.login_button);
        view = findViewById(R.id.view);
        t = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = ProgressDialog.show(LoginActivity.this, "Registering", "Please Wait...", true, false);
                name = edit_name.getText().toString();
                password = edit_passwd.getText().toString();
                submit(view);

            }
        });
    }
    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pd.cancel();
            edit_name.setEnabled(true);
            edit_passwd.setEnabled(true);
            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
        }
    };
    public void submit(View v)
    {
        if (name == null || name.equals("")) return;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = regid;
                    pregister(LoginActivity.this, regid);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    h.sendEmptyMessage(0);
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = store.edit();
                editor.putString("device_regid", regid);
                editor.apply();
            }
        }.execute(null, null, null);

    }
    void pregister(final Context context, final String regId)
    {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                Log.i(TAG, "Registering device (regId = " + regId + ")");
                String serverUrl = SERVER_URL;
                Map<String, String> paramss = new HashMap<String, String>();
                paramss.put("username", name);
                paramss.put("password", password);
                paramss.put("gcmid", regId);
                paramss.put("action_id", "0");
                paramss.put("ad_id", t.getDeviceId());
                long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
                for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                    Log.d(TAG, "Attempt #" + i + " to register");
                    try {
                        post(serverUrl, paramss);
                        go=true;
                        return msg;
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                        if (i == MAX_ATTEMPTS) {
                            break;
                        }
                        try {
                            Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                            Thread.sleep(backoff);
                        } catch (InterruptedException e1) {
                            // Activity finished before we complete - exit.
                            Log.d(TAG, "Thread interrupted: abort remaining retries!");
                            Thread.currentThread().interrupt();
                            return msg;
                        }
                        // increase backoff exponentially
                        backoff *= 2;
                    }
                }
                h.sendEmptyMessage(0);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if(go)
                {
                    Intent intent = new Intent(LoginActivity.this, Posts.class);
                    startActivity(intent);
                }
            }
        }.execute(null, null, null);

    }
    private void post(String endpoint, Map<String, String> params)
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
            CharSequence charSequence = "status";

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    if (line.contains(charSequence))
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                JSONObject js = new JSONObject(line);
                int s=Integer.parseInt(js.getString("status_id"));
                if (s>0) {
                    SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = store.edit();
                    editor.putString("usertext", name);
                    editor.putString("user_id", js.get("user_id").toString());
                    editor.apply();
                    finish();
                    //startActivity(i);
                    return;
                } else h.sendEmptyMessage(0);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
