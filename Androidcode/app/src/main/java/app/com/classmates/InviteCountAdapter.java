package app.com.classmates;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.com.classmates.ProfileSet.CircleTransform;
import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.GlobalConstants;

/**
 * Created by user on 14/7/16.
 */
public class InviteCountAdapter extends BaseAdapter {
    Context c;
    ArrayList<HashMap<String, String>> invitelist;
    LayoutInflater inflater;
    SharedPreferences mpref;
    ProgressDialog pd;
    Global global;

    public InviteCountAdapter(Context c, ArrayList<HashMap<String, String>> invitelist) {
        inflater = LayoutInflater.from(c);
        this.c = c;
        this.invitelist = invitelist;
        mpref = c.getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
        global = (Global) c.getApplicationContext();
    }

    @Override
    public int getCount() {
        return invitelist.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        Viewholder holder;
        holder = new Viewholder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_invite, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.customname_TV1);
            holder.topic = (TextView) convertView.findViewById(R.id.topicdetail_TV1);
            holder.invite_accept_TV = (TextView) convertView.findViewById(R.id.invite_accept_TV);
            holder.invite_reject_TV = (TextView) convertView.findViewById(R.id.invite_reject_TV);
            holder.custom_contacts_main_LL = (LinearLayout) convertView.findViewById(R.id.custom_contacts_main_LL);
            Fontchange.overrideFonts(c, holder.custom_contacts_main_LL);

            holder.ivites_RIV = (ImageView) convertView.findViewById(R.id.ivites_RIV);

            convertView.setTag(holder);
        } else {
            holder = (Viewholder) convertView.getTag();
        }

        holder.name.setText(invitelist.get(position).get("fname") + " " + invitelist.get(position).get("lname"));
        holder.topic.setText(invitelist.get(position).get("name") + " (" + invitelist.get(position).get("topicname") + ")");

        if (invitelist.get(position).get("userpic").contains("http")) {
            Picasso.with(c).load(invitelist.get(position).get("userpic")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).error(R.drawable.dummy_single).into(holder.ivites_RIV);
        } else {
            Picasso.with(c).load(GlobalConstants.ImageLink + invitelist.get(position).get("userpic")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).error(R.drawable.dummy_single).into(holder.ivites_RIV);
        }

        holder.invite_accept_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("Event id", invitelist.get(position).get("event_id"));
                Log.e("frnds id", invitelist.get(position).get("createduser"));
                Log.e("userr id", mpref.getString(GlobalConstants.user_id, ""));

                Acceptrequest(invitelist.get(position).get("createduser"), invitelist.get(position).get("event_id"));
            }
        });

        holder.invite_reject_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("Event id", invitelist.get(position).get("event_id"));
                Log.e("frnds id", invitelist.get(position).get("createduser"));
                Log.e("userr id", mpref.getString(GlobalConstants.user_id, ""));

                RejectDialog(invitelist.get(position).get("event_id"));
            }
        });

        return convertView;
    }

    private void Acceptrequest(final String createuser, final String event_id) {

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
                                Toast.makeText(c, "Your invitation has been accepted successfully", Toast.LENGTH_SHORT).show();
                                c.startActivity(new Intent(c, GroupMessage.class));
                                ((Activity) c).finish();
                            } else {
                                showFailureErrorDialog(job.getString("message"));
//                            Log.e("status Fail", status);
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
                        Toast.makeText(c, "Server Problem!!", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("inviteid", event_id);
                params.put("friendsid", mpref.getString(GlobalConstants.user_id, ""));
                params.put("accept", "");
                Log.e("Accept Paramerter", params + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringRequest);

    }

    private void RejectDialog(final String eventid) {

        final Dialog dialog = new Dialog(c);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_unjoin_study);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Fontchange.overrideFonts(c, ((LinearLayout) dialog.findViewById(R.id.unjoin_dia_main_LL)));

        final TextView reset_classname_B = (TextView) dialog.findViewById(R.id.dia_unjoin_class);
        final TextView cancel_classname_B = (TextView) dialog.findViewById(R.id.dia_unjoin_cancel);

        reset_classname_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = ProgressDialog.show(c, "Please Wait", "Reject Event..");
                UNJoinRejectEventAPI(eventid);
                dialog.dismiss();
            }

        });
        cancel_classname_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void UNJoinRejectEventAPI(final String eventid) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstants.mURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Response_____>>", response);
                        pd.dismiss();
                        JSONObject job = null;
                        try {
                            job = new JSONObject(response);
                            String status = job.getString("status");
                            String msgg = job.getString("message");
                            Log.e("Status", status);
                            Log.e("message", msgg);

                            if (status.equalsIgnoreCase("success")) {
                                Toast.makeText(c, "You have join this event successfully", Toast.LENGTH_SHORT).show();
                                c.startActivity(new Intent(c, InviteCount.class));
                                ((Activity) c).finish();
                            } else {
                                Log.e("status Fail", status);
                                Toast.makeText(c, msgg, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(c, "Network Problem!!", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("eventid", eventid);
                params.put("userid", mpref.getString(GlobalConstants.user_id, ""));
                params.put("reject", "");
                Log.e("Unjoin parameter", params + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringRequest);
    }

    class Viewholder {
        TextView name, topic, invite_accept_TV, invite_reject_TV;
        LinearLayout custom_contacts_main_LL;
        ImageView ivites_RIV;
    }

    private void showFailureErrorDialog(final String error_msg) {

        final Dialog dialog = new Dialog(c);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(c, ((LinearLayout) dialog.findViewById(R.id.dia_show_error_msg_LL)));

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


/*

if (status.equalsIgnoreCase("success")) {
        Toast.makeText(c,"You have join this event successfully",Toast.LENGTH_SHORT).show();
        }else {
        Log.e("status Fail", status);
        Toast.makeText(c,msgg,Toast.LENGTH_SHORT).show();
        }*/
