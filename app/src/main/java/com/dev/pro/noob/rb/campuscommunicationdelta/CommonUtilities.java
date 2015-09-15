package com.dev.pro.noob.rb.campuscommunicationdelta;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public final class CommonUtilities {

    // give your server registration url here
    static final String SERVER_URL = "http://10.0.0.109/~kousik/campuscomm/register.php";
    static final String NEW_URL = "http://10.0.0.109/~kousik/campuscomm/message.php";

    // Google project id
    static final String SENDER_ID = "835229264934";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "AndroidHive GCM";
    static boolean start1 = false, start2 = false, start3 = false, apprun = false;

    static final String DISPLAY_MESSAGE_ACTION =
            "com.androidhive.pushnotifications.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p/>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    static ArrayList<String> level1 = new ArrayList<String>() {
        {
            add("btech");
            add("mtech");
            add("select year");
        }
    };
    static ArrayList<String> level2_2 = new ArrayList<String>() {
        {
            add("1");
            add("2");
            add("phd");
            add("select dept");
        }
    };
    static ArrayList<String> level2_1 = new ArrayList<String>() {
        {
            add("11");
            add("12");
            add("13");
            add("14");
            add("select dept");
        }
    };
    static ArrayList<String> level3 = new ArrayList<String>() {
        {
            add("archi");
            add("chemical");
            add("civil");
            add("cse");
            add("ece");
            add("eee");
            add("ice");
            add("mech");
            add("meata");
            add("prod");
            add("done");
        }
    };
}
