package app.com.classmates.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.com.classmates.Global;
import app.com.classmates.GroupMessage;
import app.com.classmates.LoginwithFB;
import app.com.classmates.R;
import app.com.classmates.multipleclasses.GlobalConstants;

/**
 * Created by user on 23/07/16.
 */
public class MyGcmListenerService extends GcmListenerService {

    SharedPreferences mpref;
    boolean appinbackground;
    private ArrayList<HashMap<String, String>> clisting;
    Global global;
    String event_id;
    public static int NOTI_ID = 01;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

        global = (Global) getApplicationContext();
        mpref = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
        appinbackground = mpref.getBoolean("appinbackground", false);
        clisting = new ArrayList<HashMap<String, String>>();

        Log.e("GGGGCCCMMM resp receive", data.toString());
        Log.e("GGGCCC  MMM msgggIDdddd", data.getString("google.message_id"));
        Log.e("Froommmmmm ", from);

        event_id = data.getString("eventid");
        sendNotification(data.getString("message"));

        // [END_EXCLUDE]
    }


    private void sendNotification(String message) {

        if (!appinbackground) {
            Intent intent;

            boolean tru = mpref.getBoolean("IS_TRUE", false);
            Log.e("Print true or nott", tru + "");
            if (tru) {
                intent = new Intent(this, LoginwithFB.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else {
                intent = new Intent(this, GroupMessage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.cm_logo)
                    .setContentTitle("classMates")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(01, notificationBuilder.build());
        } else {
            GetAllChatMessage(event_id);
        }
    }

    private void GetAllChatMessage(final String eventid) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstants.mURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Response_____>>", response);
                        JSONObject job = null;
                        try {
                            job = new JSONObject(response);
                            String status = job.getString("status");
                            Log.e("Status", status);

                            if (status.equalsIgnoreCase("success")) {
                                JSONArray jarr = job.getJSONArray("message");
                                for (int i = 0; i < jarr.length(); i++) {
                                    JSONObject obj = jarr.getJSONObject(i);
                                    HashMap<String, String> hmap = new HashMap<String, String>();
                                    hmap.put("chatid", obj.getString("chatid"));
                                    hmap.put("user_id", obj.getString("user_id"));
                                    hmap.put("eventid", obj.getString("eventid"));
                                    hmap.put("message", obj.getString("message"));
                                    hmap.put("created", obj.getString("created"));
                                    hmap.put("fname", obj.getString("fname"));
                                    hmap.put("lname", obj.getString("lname"));
                                    hmap.put("topicname", obj.getString("topicname"));
                                    hmap.put("event_created", obj.getString("event_created"));
                                    hmap.put("type", obj.getString("type"));

                                    clisting.add(hmap);
                                }

                                Log.e("CLeasr List", global.getChatList() + "");
                                global.setChatList(clisting);

                                Intent intent = new Intent("unique_name");
                                sendBroadcast(intent);

                            } else {
                                Log.e("All Msgs APi not Hit", job.getString("message"));
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response ERROR_____>>", error.toString());
//                    Toast.makeText(GroupChat.this,"Network Problem!!",Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("eventid", eventid);
                params.put("userid", mpref.getString(GlobalConstants.user_id, ""));
                params.put("messageall", "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
