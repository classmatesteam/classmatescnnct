package app.com.classmates;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.widget.PullRefreshLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.com.classmates.ProfileSet.CircleTransform;
import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.ConnectivityReceiver;
import app.com.classmates.multipleclasses.GlobalConstants;


@SuppressLint("NewApi")
public class ParticipantsDetail extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    RelativeLayout usersdetail_back_RL, userdetails_main_RL;
    SharedPreferences sp;
    Global global;
    ListView usersdetail_LL;
    private GroupUsersAdapter adpter;
    private ArrayList<HashMap<String, String>> groupmsgslist;
    private PullRefreshLayout layout;
    Bundle translateRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participant_detail);
        userdetails_main_RL = (RelativeLayout) findViewById(R.id.userdetails_main_RL);
        Fontchange.overrideFonts(this, userdetails_main_RL);

        sp = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
        global = (Global) getApplicationContext();

        checkConnection();
        usersdetailinit();
    }


    private void usersdetailinit() {
        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout_usersdetail);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (groupmsgslist != null)
                    groupmsgslist.clear();
                GetParticipantsDetailAPI();
            }
        });

        translateRight =
                ActivityOptions.
                        makeCustomAnimation(ParticipantsDetail.this,
                                R.anim.slide_down,
                                R.anim.slide_up).toBundle();

        usersdetail_back_RL = (RelativeLayout) findViewById(R.id.usersdetail_back_RL);
        usersdetail_back_RL.setOnClickListener(this);

        usersdetail_LL = (ListView) findViewById(R.id.usersdetail_LL);

        groupmsgslist = new ArrayList<HashMap<String, String>>();

        startAnim();
        GetParticipantsDetailAPI();
    }

    public void startAnim() {
        findViewById(R.id.avloadingIndicatorViewR_parti).setVisibility(View.VISIBLE);
    }

    public void stopAnim() {
        findViewById(R.id.avloadingIndicatorViewR_parti).setVisibility(View.GONE);
    }

    class GroupUsersAdapter extends BaseAdapter {

        Context c;
        LayoutInflater mInflater;
        ArrayList<HashMap<String, String>> groupmsgs;
        SharedPreferences pref;

        public GroupUsersAdapter(Context c, ArrayList<HashMap<String, String>> groupmsgs) {
            this.c = c;
            this.groupmsgs = groupmsgs;
            mInflater = LayoutInflater.from(c);
            pref = c.getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
        }

        @Override
        public int getCount() {
            return groupmsgs.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = mInflater.inflate(R.layout.custom_users_detial, viewGroup, false);

                holder.userdetails_pic_RIV = (ImageView) view.findViewById(R.id.userdetails_pic_RIV);
                holder.usernamedetail_TV = (TextView) view.findViewById(R.id.usernamedetail_TV);
                holder.custom_users_main_LL = (LinearLayout) view.findViewById(R.id.custom_users_main_LL);

                Fontchange.overrideFonts(c, holder.custom_users_main_LL);
                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.usernamedetail_TV.setText(groupmsgs.get(i).get("fname") + " " + groupmsgs.get(i).get("lname"));
            if (groupmsgs.get(i).get("userpic").contains("http")) {
                Picasso.with(c).load(groupmsgs.get(i).get("userpic")).error(R.drawable.dummy_single).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).into(holder.userdetails_pic_RIV);
            } else {
                Picasso.with(c).load(GlobalConstants.ImageLink + groupmsgs.get(i).get("userpic")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).fit().error(R.drawable.dummy_single).into(holder.userdetails_pic_RIV);
            }

            return view;
        }

        class ViewHolder {
            ImageView userdetails_pic_RIV;
            TextView usernamedetail_TV;
            LinearLayout custom_users_main_LL;
        }
    }

    private void GetParticipantsDetailAPI() {

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
                                    hmap.put("fname", obj.getString("fname"));
                                    hmap.put("lname", obj.getString("lname"));
                                    hmap.put("userpic", obj.getString("userpic"));
                                    groupmsgslist.add(hmap);
                                }

                            /*JSONObject jjarr = job.getJSONObject("eventcreater");

                                HashMap<String,String> hmap= new HashMap<String,String>();
                                hmap.put("fname",jjarr.getString("fname"));
                                hmap.put("lname",jjarr.getString("lname"));
                                hmap.put("userpic",jjarr.getString("userpic"));

                                Log.e("Created event name ",jjarr.getString("fname") + " -- "+ jjarr.getString("lname"));

                                groupmsgslist.add(hmap);
*/

                                layout.setRefreshing(false);
                                stopAnim();
                                adpter = new GroupUsersAdapter(ParticipantsDetail.this, groupmsgslist);
                                usersdetail_LL.setAdapter(adpter);
                            } else {
                                layout.setRefreshing(false);
                                stopAnim();
//                            Toast.makeText(ParticipantsDetail.this, job.getString("message"), Toast.LENGTH_SHORT).show();
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
                        showFailureErrorDialog("Failed to Connect with Server");
//                        Toast.makeText(GroupMessage.this,error.toString(),Toast.LENGTH_SHORT).show();
                        showSnack(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("eventid", sp.getString("Chat_event_id", ""));
                params.put("participantslist", "");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.usersdetail_back_RL:
                if (sp.getInt("back", 0) == 1) {
                    SharedPreferences.Editor ee = sp.edit();
                    ee.putInt("mystudy", 1);
                    ee.commit();
                    startActivity(new Intent(ParticipantsDetail.this, MyStudy.class));
                    finish();
                } else if (sp.getInt("back", 0) == 2) {
                    startActivity(new Intent(ParticipantsDetail.this, ClassdetailList.class));
                    finish();
                } else if (sp.getInt("back", 0) == 3) {
                    startActivity(new Intent(ParticipantsDetail.this, HotTopics.class));
                    finish();
                } else {
                    startActivity(new Intent(ParticipantsDetail.this, GroupChat.class));
                    finish();
                }
                break;

          /*  case R.id.:

                break;*/
        }

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
            Snackbar snackbar = Snackbar.make(userdetails_main_RL, message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("appinbackground", true);
        ed.commit();
        Log.e("in Activity", "in resume");
        super.onResume();
        Global.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("appinbackground", false);
        ed.commit();
        Log.e("in Activity", "in pause");
        super.onPause();
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    public void onBackPressed() {
        if (sp.getInt("back", 0) == 1) {
            startActivity(new Intent(ParticipantsDetail.this, MyStudy.class));
            finish();
        } else if (sp.getInt("Class_list", 0) == 2) {
            startActivity(new Intent(ParticipantsDetail.this, ClassdetailList.class));
            finish();
        } else if (sp.getInt("hot_topics", 0) == 3) {
            startActivity(new Intent(ParticipantsDetail.this, HotTopics.class));
            finish();
        } else {
            startActivity(new Intent(ParticipantsDetail.this, GroupChat.class));
            finish();
        }
    }

    private void showFailureErrorDialog(final String error_msg) {

        final Dialog dialog = new Dialog(ParticipantsDetail.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(ParticipantsDetail.this, ((LinearLayout) dialog.findViewById(R.id.dia_show_error_msg_LL)));

        final TextView ok_error_msg_TV = (TextView) dialog.findViewById(R.id.ok_error_msg_TV);
        final TextView show_error_TV = (TextView) dialog.findViewById(R.id.show_error_TV);
        show_error_TV.setText(error_msg);

        ok_error_msg_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(ParticipantsDetail.this, HotTopics.class));
                finish();
            }
        });

        dialog.show();
    }

}
