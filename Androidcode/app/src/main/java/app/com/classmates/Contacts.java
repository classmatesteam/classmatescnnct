package app.com.classmates;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.Toast;

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
import app.com.classmates.multipleclasses.ConnectivityReceiver;
import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.GlobalConstants;

@SuppressLint("NewApi")
public class Contacts extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    ListView contacts_LV;
    RelativeLayout contacts_back_RL;
    ArrayList<HashMap<String, String>> contactslist;
    SharedPreferences pref;
    Global global;
    private Bundle translateRight;
    private PullRefreshLayout layout;
    String eventida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);
        global = (Global) getApplicationContext();
        pref = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);

        Fontchange.overrideFonts(Contacts.this, ((RelativeLayout) findViewById(R.id.contacts_main_RL)));

        checkConnection();
        fetchbyids();
        eventida = pref.getString("Contact_event_id", "");
        System.out.println("contact_event -Id > " + eventida);
        GetContactsAPI(eventida);

        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (contactslist != null) {
                    contactslist.clear();
                }
                GetContactsAPI(eventida);
            }
        });

    }


    private void fetchbyids() {
        contacts_LV = (ListView) findViewById(R.id.contacts_LV);

        contacts_back_RL = (RelativeLayout) findViewById(R.id.contacts_back_RL);
        contacts_back_RL.setOnClickListener(this);

        translateRight =
                ActivityOptions.
                        makeCustomAnimation(Contacts.this,
                                R.anim.fade_in,
                                R.anim.slide_out_right).toBundle();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.contacts_back_RL:
                startActivity(new Intent(Contacts.this, MyStudy.class), translateRight);
                finish();
                break;
        }
    }

    class ContactsAdapter extends BaseAdapter {

        Context c;
        LayoutInflater mInflater;
        ArrayList<HashMap<String, String>> contacts;
        SharedPreferences c_sp;
        ProgressDialog c_pd;
        String eventids;

        public ContactsAdapter(Context c, ArrayList<HashMap<String, String>> contacts, String eventids) {
            this.c = c;
            this.contacts = contacts;
            this.eventids = eventids;
            mInflater = LayoutInflater.from(c);
            c_sp = c.getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
        }

        @Override
        public int getCount() {
            return contacts.size();
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (view == null) {

                holder = new ViewHolder();
                view = mInflater.inflate(R.layout.custom_contacts, viewGroup, false);

                holder.custom_contacts_main_LL = (LinearLayout) view.findViewById(R.id.custom_contacts_main_LL);
                Fontchange.overrideFonts(c, holder.custom_contacts_main_LL);

                holder.customcontactname_TV = (TextView) view.findViewById(R.id.customcontactname_TV);
//                holder.eventdetail_TV = (TextView)view.findViewById(R.id.eventdetail_TV);
                holder.contacts_invite_TV = (TextView) view.findViewById(R.id.contacts_invite_TV);
                holder.customcontact_pic_RIV = (ImageView) view.findViewById(R.id.customcontact_pic_RIV);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.customcontactname_TV.setText(contacts.get(i).get("fname") + " " + contacts.get(i).get("lname"));
//            holder.eventdetail_TV.setText(contacts.get(i).get(""));
            if (contacts.get(i).get("userpic").contains("http"))
                Picasso.with(c).load(contacts.get(i).get("userpic")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).error(R.drawable.cm_logo).into(holder.customcontact_pic_RIV);
            else
                Picasso.with(c).load(GlobalConstants.ImageLink + contacts.get(i).get("userpic")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).error(R.drawable.cm_logo).into(holder.customcontact_pic_RIV);

            if (contacts.get(i).get("invite").equalsIgnoreCase("Yes")) {
                holder.contacts_invite_TV.setText("Invited");
            } else {
                holder.contacts_invite_TV.setText("Invite");
                holder.contacts_invite_TV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        c_pd = ProgressDialog.show(c, "Please Wait", "Sending Invitation..");
                        SendInvite(eventids, contacts.get(i).get("userid"));
                    }
                });
            }


            return view;
        }

        class ViewHolder {
            LinearLayout custom_contacts_main_LL;
            TextView customcontactname_TV, contacts_invite_TV;
            ImageView customcontact_pic_RIV;
        }

        private void SendInvite(final String eventid, final String friendid) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstants.mURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("Response_____>>", response);
                            c_pd.dismiss();
                            JSONObject job = null;
                            try {
                                job = new JSONObject(response);
                                String status = job.getString("status");

                                Log.e("Status", status);

                                if (status.equalsIgnoreCase("success")) {
                                    Toast.makeText(c, "Invitation send Successfully!!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(c, Contacts.class));
                                    ((Activity) c).finish();
                                } else {
                                    Toast.makeText(c, job.getString("message"), Toast.LENGTH_SHORT).show();
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
//                            Toast.makeText(c,"Network Problem!!",Toast.LENGTH_SHORT).show();
                            showSnack(false);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userid", c_sp.getString(GlobalConstants.user_id, ""));
                    params.put("eventid", eventid);
                    params.put("friendsid", friendid);
                    params.put("invite", "");
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(c);
            requestQueue.add(stringRequest);
        }

    }

    private void GetContactsAPI(final String eventids) {
        if (contactslist != null) {
            contactslist.clear();
        }
        Log.e("Usre id ...", pref.getString(GlobalConstants.user_id, ""));
        Log.e("Name of course ...", global.getRecommendedlist().get(global.getPosition()).get("name"));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstants.mURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        contactslist = new ArrayList<HashMap<String, String>>();
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
                                    hmap.put("userid", obj.getString("userid"));
                                    hmap.put("fname", obj.getString("fname"));
                                    hmap.put("lname", obj.getString("lname"));
                                    hmap.put("userpic", obj.getString("userpic"));
                                    hmap.put("invite", obj.getString("invite"));
                                    contactslist.add(hmap);
                                }
                                layout.setRefreshing(false);
                                contacts_LV.setAdapter(new ContactsAdapter(Contacts.this, contactslist, eventids));
                            } else {
//                            showFailureErrorDialog(job.getString("message"));
//                            Toast.makeText(Contacts.this, job.getString("message"), Toast.LENGTH_SHORT).show();
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
//                        Toast.makeText(Contacts.this,"Network Problem!!",Toast.LENGTH_SHORT).show();
                        showFailureErrorDialog("Failed to Connect Server");
                        showSnack(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", pref.getString(GlobalConstants.user_id, ""));
                params.put("eventid", eventids);
                params.put("name", global.getRecommendedlist().get(global.getPosition()).get("name"));
                params.put("contactlist", "");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
            Snackbar snackbar = Snackbar.make(((RelativeLayout) findViewById(R.id.contacts_main_RL)), message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    private void showFailureErrorDialog(final String error_msg) {

        final Dialog dialog = new Dialog(Contacts.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(Contacts.this, ((LinearLayout) dialog.findViewById(R.id.dia_show_error_msg_LL)));

        final TextView ok_error_msg_TV = (TextView) dialog.findViewById(R.id.ok_error_msg_TV);
        final TextView show_error_TV = (TextView) dialog.findViewById(R.id.show_error_TV);
        show_error_TV.setText(error_msg);

        ok_error_msg_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(Contacts.this, MyStudy.class), translateRight);
                finish();
            }
        });

        dialog.show();
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
        startActivity(new Intent(Contacts.this, MyStudy.class), translateRight);
        finish();
    }

    @Override
    protected void onResume() {
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean("appinbackground", false);
        ed.commit();
        Log.e("in Activity", "in resume");
        super.onResume();
        Global.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean("appinbackground", false);
        ed.commit();
        Log.e("in Activity", "in pause");
        super.onPause();
    }
}
