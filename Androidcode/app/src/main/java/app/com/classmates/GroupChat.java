package app.com.classmates;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import app.com.classmates.multipleclasses.ChatArrayAdapter;
import app.com.classmates.multipleclasses.ConnectivityReceiver;
import app.com.classmates.multipleclasses.GlobalConstants;
import app.com.classmates.services.MyGcmListenerService;
import app.com.classmates.fonts.Fontchange;

@SuppressLint("NewApi")
public class GroupChat extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    EditText writechattext_ET;
    ImageView send_written_msg_IV;
    ListView chat_LV;

    int i = 0;
    String chatMessage, eventid, createdtime;

    private SharedPreferences sharedpreferences;
    private Global global;

    ArrayList<HashMap<String, String>> listing = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map = new HashMap<String, String>();

    ChatArrayAdapter chatArrayAdapter;
    String chatID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_chat);
        Fontchange.overrideFonts(GroupChat.this, ((RelativeLayout) findViewById(R.id.groupchat_main_RL)));
        sharedpreferences = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);

        global = (Global) getApplicationContext();
        chatinit();
        checkConnection();
        cancelNotification(this, MyGcmListenerService.NOTI_ID);


    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    private void chatinit() {

        SharedPreferences.Editor ee = sharedpreferences.edit();
        ee.putInt("back", 4);
        ee.commit();

        ((RelativeLayout) findViewById(R.id.chat_participantdetail_RL)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.show_participants_IV)).setOnClickListener(this);

        writechattext_ET = (EditText) findViewById(R.id.writechattext_ET);
        chat_LV = (ListView) findViewById(R.id.chat_LV);

        send_written_msg_IV = (ImageView) findViewById(R.id.send_written_msg_IV);
        send_written_msg_IV.setOnClickListener(this);

        if (global.getGroupmsgList() != null) {
            String title = global.getGroupmsgList().get(global.getPosition()).get("name") + "(" + global.getGroupmsgList().get(global.getPosition()).get("topicname") + ")";
            createdtime = global.getGroupmsgList().get(global.getPosition()).get("lastmsgtime");
            Log.e("Created Msg time", createdtime);

            eventid = global.getGroupmsgList().get(global.getPosition()).get("eventid");
            SharedPreferences.Editor e = sharedpreferences.edit();
            e.putString("Chat_event_id", eventid);
            e.putString("title", title);
            e.putString("created_time", createdtime);
            e.commit();
        }

        ((TextView) findViewById(R.id.groupchat_title_name_TV)).setText(sharedpreferences.getString("title", ""));
        if (sharedpreferences.getString("created_time", "").equalsIgnoreCase("NULL")) {
            ((TextView) findViewById(R.id.groupchat_datetime_TV)).setText("You were added.");
        } else
            ((TextView) findViewById(R.id.groupchat_datetime_TV)).setText(createdtime);


        Log.e("Event id for chat msgs ", eventid);
        GetAllChatMessage(eventid);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_participantdetail_RL:
                startActivity(new Intent(GroupChat.this, GroupMessage.class));
                finish();
                break;

            case R.id.show_participants_IV:
                startActivity(new Intent(GroupChat.this, ParticipantsDetail.class));
                finish();
                break;

            case R.id.send_written_msg_IV:
                if (writechattext_ET.getText().toString().equalsIgnoreCase(null) || writechattext_ET.getText().toString().equalsIgnoreCase("")) {
                } else {
                    if (createdtime.equalsIgnoreCase("NULL")) {
                        try {
                            chatMessage = writechattext_ET.getText().toString();
                            sharedpreferences = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
                            String userids = sharedpreferences.getString(GlobalConstants.user_id, "");
                            map.put("user_id", userids);
                            map.put("message", chatMessage);
                            listing.add(map);

                            global.setChatList(listing);

                            chatArrayAdapter = new ChatArrayAdapter(GroupChat.this, global.getChatList());
                            scrollMyListViewToBottom();
                            chat_LV.setAdapter(chatArrayAdapter);
                            Log.e("Adapter Set in ", "First Insert of msg empty");
                            chat_LV.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                            chat_LV.setAdapter(chatArrayAdapter);

                            // to scroll the list view to bottom on data change
                            chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
                                @Override
                                public void onChanged() {
                                    super.onChanged();
                                    chat_LV.setSelection(chatArrayAdapter.getCount() - 1);
                                }
                            });
                            InsertChatMessage(chatMessage);
                        } catch (Exception e) {
                            Log.e("Exception while sending msg", e + "");
                        }
                    } else {

                        try {
                            chatMessage = writechattext_ET.getText().toString();
                            listing = global.getChatList();
                            Log.e("s", listing + "");

                            sharedpreferences = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
                            String userids = sharedpreferences.getString(GlobalConstants.user_id, "");
                            map.put("user_id", userids);
                            map.put("message", chatMessage);
                            listing.add(map);
                            global.setChatList(listing);

                            chatArrayAdapter = new ChatArrayAdapter(GroupChat.this, listing);
                            scrollMyListViewToBottom();
                            chat_LV.setAdapter(chatArrayAdapter);
                            chat_LV.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                            chat_LV.setAdapter(chatArrayAdapter);

                            // to scroll the list view to bottom on data change
                            chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
                                @Override
                                public void onChanged() {
                                    super.onChanged();
                                    chat_LV.setSelection(chatArrayAdapter.getCount() - 1);
                                }
                            });
                            writechattext_ET.setText("");
                            InsertChatMessage(chatMessage);
                        } catch (Exception e) {
                            Log.e("Exception while sending msg", e + "");
                        }
                    }
                }
                break;
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
                                listing.clear();
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

                                    chatID = obj.getString("chatid");
//                                Log.e("Last Chat IDD",chatID);

                                    listing.add(hmap);
                                }

                                MsgStateREAD_UNREAD_API(chatID);

                                global.setChatList(listing);
                                Log.e("CLeasr List", global.getChatList() + "");
                                chatArrayAdapter = new ChatArrayAdapter(GroupChat.this, global.getChatList());
                                chat_LV.setAdapter(chatArrayAdapter);
                                chat_LV.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                                chat_LV.setAdapter(chatArrayAdapter);
                                scrollMyListViewToBottom();
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
                        showSnack(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("eventid", eventid);
                params.put("userid", sharedpreferences.getString(GlobalConstants.user_id, ""));
                params.put("messageall", "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void InsertChatMessage(final String schatMessage) {

        final Calendar cal = Calendar.getInstance();
        SimpleDateFormat tf = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
        final String date_time = tf.format(cal.getTime());
        String str = date_time.replace("AM", "am").replace("PM", "pm");
        Log.e("Insert chat TIme", str);
        Log.e("Upper chat TIme", date_time.toUpperCase());

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
                                writechattext_ET.setText("");
                                chatMessage = "";
                                MsgStateREAD_UNREAD_API(chatID);
                                GetAllChatMessage(eventid);
                            } else {
                                writechattext_ET.setText("");
                                chatMessage = "";
                                Toast.makeText(GroupChat.this, "Message not Send!", Toast.LENGTH_SHORT).show();
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

                        writechattext_ET.setText("");
                        chatMessage = "";
                        showSnack(false);
                        showFailureErrorDialog("Message sending Failed!");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("eventid", sharedpreferences.getString("Chat_event_id", ""));
                params.put("userid", sharedpreferences.getString(GlobalConstants.user_id, ""));
                params.put("msg", schatMessage);
                params.put("date", date_time);
                params.put("message", "");
                Log.e("Imsert Chat Event", params + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

//        int socketTimeout = 9000;//20 seconds - change to what you want
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        stringRequest.setRetryPolicy(policy);
//        requestQueue.add(stringRequest);

    }


    private void MsgStateREAD_UNREAD_API(final String chatID) {

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

                            } else {
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

                        writechattext_ET.setText("");
                        chatMessage = "";
                        showSnack(false);
                        showFailureErrorDialog("Message sending Failed!");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("eventid", sharedpreferences.getString("Chat_event_id", ""));
                params.put("userid", sharedpreferences.getString(GlobalConstants.user_id, ""));
                params.put("chatid", chatID);
                params.put("read", "");
                Log.e("read read Event", params + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

//        int socketTimeout = 9000;//20 seconds - change to what you want
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        stringRequest.setRetryPolicy(policy);
//        requestQueue.add(stringRequest);

    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message = "";
        int color = 0;
        if (!isConnected) {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
            Snackbar snackbar = Snackbar.make(((RelativeLayout) findViewById(R.id.groupchat_main_RL)), message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void scrollMyListViewToBottom() {
        chat_LV.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view
                chat_LV.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GetAllChatMessage(eventid);
        }
    };

    @Override
    protected void onResume() {
        this.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
        SharedPreferences.Editor ed = sharedpreferences.edit();
        ed.putBoolean("appinbackground", true);
        ed.commit();
        Log.e("in Activity", "in resume");
        super.onResume();
        Global.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        this.unregisterReceiver(mMessageReceiver);
        SharedPreferences.Editor ed = sharedpreferences.edit();
        ed.putBoolean("appinbackground", false);
        ed.commit();
        Log.e("in Activity", "in pause");
        super.onPause();
    }

    public void CallAPI(Context context) {
        Intent intent = new Intent("unique_name");
        context.sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(GroupChat.this, GroupMessage.class));
        finish();
    }

    Dialog dialog;

    private void showFailureErrorDialog(final String error_msg) {
        dialog = new Dialog(GroupChat.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(GroupChat.this, ((LinearLayout) dialog.findViewById(R.id.dia_show_error_msg_LL)));

        final TextView ok_error_msg_TV = (TextView) dialog.findViewById(R.id.ok_error_msg_TV);
        final TextView show_error_TV = (TextView) dialog.findViewById(R.id.show_error_TV);
        show_error_TV.setText(error_msg);

        ok_error_msg_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}