package app.com.classmates;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.ConnectivityReceiver;
import app.com.classmates.multipleclasses.GlobalConstants;

/**
 * Created by user on 14/7/16.
 */

@SuppressLint("NewApi")
public class InviteCount extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    ListView invite_LL;
    RelativeLayout groupmsg_back_RL;
    Bundle translateRight;
    ArrayList<HashMap<String, String>> invitelist;
    SharedPreferences mpref;
    private PullRefreshLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_count);
        Fontchange.overrideFonts(InviteCount.this, ((RelativeLayout) findViewById(R.id.invite_main_RL)));
        checkConnection();
        mpref = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
        translateRight =
                ActivityOptions.
                        makeCustomAnimation(this,
                                R.anim.fade_in,
                                R.anim.slide_out_right).toBundle();

        invite_LL = (ListView) findViewById(R.id.invite_LL);
        invitelist = new ArrayList<HashMap<String, String>>();

        groupmsg_back_RL = (RelativeLayout) findViewById(R.id.groupmsg_back_RL);
        groupmsg_back_RL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InviteCount.this, Drawer.class), translateRight);
                finish();
            }
        });

        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (invitelist != null) {
                    invitelist.clear();
                }
                Invitecountlist();
            }
        });

        Invitecountlist();

    }

    private void Invitecountlist() {
        if (invitelist != null) {
            invitelist.clear();
        }

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
                                    hmap.put("event_id", obj.getString("event_id"));
                                    hmap.put("name", obj.getString("name"));
                                    hmap.put("createduser", obj.getString("createduser"));
                                    hmap.put("topicname", obj.getString("topicname"));
                                    hmap.put("userid", obj.getString("userid"));
                                    hmap.put("inviteid", obj.getString("inviteid"));
                                    hmap.put("fname", obj.getString("fname"));
                                    hmap.put("lname", obj.getString("lname"));
                                    hmap.put("userpic", obj.getString("userpic"));
                                    invitelist.add(hmap);
                                }

                                layout.setRefreshing(false);
                                invite_LL.setAdapter(new InviteCountAdapter(InviteCount.this, invitelist));
                            } else {

//                                Toast.makeText(InviteCount.this, "Sorry! No Invite List Found.", Toast.LENGTH_SHORT).show();
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
//                        showFailureErrorDialog("Failed to Connect with Server");
                        Toast.makeText(InviteCount.this, "Network Problem!!", Toast.LENGTH_SHORT).show();
                        showSnack(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", mpref.getString(GlobalConstants.user_id, ""));
                params.put("invitecount", "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showFailureErrorDialog(final String error_msg) {

        final Dialog dialog = new Dialog(InviteCount.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(InviteCount.this, ((LinearLayout) dialog.findViewById(R.id.dia_show_error_msg_LL)));

        final TextView ok_error_msg_TV = (TextView) dialog.findViewById(R.id.ok_error_msg_TV);
        final TextView show_error_TV = (TextView) dialog.findViewById(R.id.show_error_TV);
        show_error_TV.setText(error_msg);

        ok_error_msg_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(InviteCount.this, Drawer.class), translateRight);
                finish();
            }
        });

        dialog.show();
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
            Snackbar snackbar = Snackbar.make(((RelativeLayout) findViewById(R.id.invite_main_RL)), message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(InviteCount.this, Drawer.class), translateRight);
        finish();
    }

    @Override
    protected void onResume() {
        SharedPreferences.Editor ed = mpref.edit();
        ed.putBoolean("appinbackground", false);
        ed.commit();
        Log.e("in Activity", "in resume");
        super.onResume();
        Global.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        SharedPreferences.Editor ed = mpref.edit();
        ed.putBoolean("appinbackground", false);
        ed.commit();
        Log.e("in Activity", "in pause");
        super.onPause();
    }
}
