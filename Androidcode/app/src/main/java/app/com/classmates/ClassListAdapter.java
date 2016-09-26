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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
 * Created by user on 21/6/16.
 */
class ClassListAdapter extends BaseAdapter implements Filterable {

    LayoutInflater mInflater;
    Context c;
    ArrayList<HashMap<String, String>> hottopicslist;
    SharedPreferences mpref;
    ProgressDialog pd;
    ValueFilter1 valueFilter;
    private ArrayList<HashMap<String, String>> filter_lists;
    ArrayList<HashMap<String, String>> mStringFilterList;
    Global global;
    int ii;

    public ClassListAdapter(Context c, ArrayList<HashMap<String, String>> hottopicslist, int ii) {
        this.c = c;
        mInflater = LayoutInflater.from(c);
        this.hottopicslist = hottopicslist;
        mStringFilterList = hottopicslist;
        this.ii = ii;
        mpref = c.getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
        global = (Global) c.getApplicationContext();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return hottopicslist.size();
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
        ViewHolder holder;
        if (view == null) {

            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.custom_classlist, null);

            holder.custom_classlist_LL = (LinearLayout) view.findViewById(R.id.custom_classlist_LL);
            holder.totalparticipants_RL = (RelativeLayout) view.findViewById(R.id.totalparticipants_RL);

            holder.hot_name_TV = (TextView) view.findViewById(R.id.hot_name_TV);
//            holder.hot_datecreated_TV = (TextView)view.findViewById(R.id.hot_datecreated_TV);
            holder.hot_coursename_TV = (TextView) view.findViewById(R.id.hot_coursename_TV);
            holder.hot_topicname_TV = (TextView) view.findViewById(R.id.hot_topicname_TV);
            holder.hot_locationame_TV = (TextView) view.findViewById(R.id.hot_locationame_TV);
            holder.hot_parti_count_TV = (TextView) view.findViewById(R.id.hot_parti_count_TV);
            holder.join_study_TV = (TextView) view.findViewById(R.id.join_study_TV);
            holder.classlist_time_TV = (TextView) view.findViewById(R.id.classlist_time_TV);

            holder.hot_classuser_event_RIV = (ImageView) view.findViewById(R.id.hot_classuser_event_RIV);

            Fontchange.overrideFonts(c, holder.custom_classlist_LL);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        try {
            if (ii == 1) {//check for joined and unjoin events

                String uid = hottopicslist.get(i).get("user_id");
                Log.e("Userid comes from class", uid);

                String join = hottopicslist.get(i).get("join");
                Log.e("******Join status*****", join);

                if (join != null) {
                    if (join.equalsIgnoreCase("No")) {
                        System.out.println("USerrrrr Idddd______>>" + uid);
                        System.out.println("My Idddd______>>" + mpref.getString(GlobalConstants.user_id, ""));

                        if (uid.equalsIgnoreCase(mpref.getString(GlobalConstants.user_id, ""))) {
                            holder.join_study_TV.setText("JOINED");
                        } else {
                            holder.join_study_TV.setText("JOIN");
                            holder.join_study_TV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e("Event id", hottopicslist.get(i).get("event_id"));
                                    Log.e("frnds id", hottopicslist.get(i).get("user_id"));
                                    Log.e("userr id", mpref.getString(GlobalConstants.user_id, ""));

                                    JOINstudyDialog(hottopicslist.get(i).get("user_id"), hottopicslist.get(i).get("event_id"));
                                }
                            });
                        }
                    } else {
                        System.out.println("USerrrrr Idddd______>>" + uid);
                        holder.join_study_TV.setText("UNJOIN");
                        holder.join_study_TV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("Event id", hottopicslist.get(i).get("event_id"));
                                Log.e("userr id", mpref.getString(GlobalConstants.user_id, ""));

                                UNJOINstudyDialog(hottopicslist.get(i).get("event_id"));
                            }
                        });
                    }
                }
            } else {
                holder.join_study_TV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("Event id", hottopicslist.get(i).get("event_id"));
                        Log.e("frnds id", hottopicslist.get(i).get("user_id"));
                        Log.e("userr id", mpref.getString(GlobalConstants.user_id, ""));

                        JOINstudyDialog(hottopicslist.get(i).get("user_id"), hottopicslist.get(i).get("event_id"));
                    }
                });

            }
            holder.hot_name_TV.setText(hottopicslist.get(i).get("fname") + " " + hottopicslist.get(i).get("lname"));

            holder.hot_coursename_TV.setText(hottopicslist.get(i).get("Course_Name"));
            holder.hot_topicname_TV.setText(hottopicslist.get(i).get("topic"));
            holder.hot_locationame_TV.setText(hottopicslist.get(i).get("location"));
            holder.hot_parti_count_TV.setText(hottopicslist.get(i).get("participants"));
            holder.classlist_time_TV.setText(" " + hottopicslist.get(i).get("starttime"));

            String userimage = hottopicslist.get(i).get("userpic");
            if (userimage.contains("http")) {
                Picasso.with(c).load(userimage).placeholder(R.drawable.dummy_single).transform(new CircleTransform()).centerCrop().error(R.drawable.cm_logo).into(holder.hot_classuser_event_RIV);
            } else {
                String nam = hottopicslist.get(i).get("fname") + " " + hottopicslist.get(i).get("lname");
                Picasso.with(c).load(GlobalConstants.ImageLink + userimage).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).error(R.drawable.cm_logo).into(holder.hot_classuser_event_RIV);
            }


            holder.totalparticipants_RL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor e = mpref.edit();
                    e.putString("Chat_event_id", hottopicslist.get(i).get("event_id"));
                    e.commit();
                    Log.e("Paricipants Event id", hottopicslist.get(i).get("event_id"));
                    c.startActivity(new Intent(c, ParticipantsDetail.class));
                }
            });
        } catch (Exception e) {
            System.out.println("ClassList Exception  >>" + e);
        }

        return view;
    }

    private void JOINstudyDialog(final String friend, final String eventid) {

        final Dialog dialog = new Dialog(c);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_join_study);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Fontchange.overrideFonts(c, ((LinearLayout) dialog.findViewById(R.id.join_dia_main_LL)));

        final TextView reset_classname_B = (TextView) dialog.findViewById(R.id.dia_join_class);
        final TextView cancel_classname_B = (TextView) dialog.findViewById(R.id.dia_join_cancel);

        reset_classname_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = ProgressDialog.show(c, "Please Wait", "Join Event..");
                JoinEventAPI(eventid, friend);
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


    private void UNJOINstudyDialog(final String eventid) {

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
                pd = ProgressDialog.show(c, "Please Wait", "Unjoin Event..");
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
                                Toast.makeText(c, "You have unjoin this event successfully", Toast.LENGTH_SHORT).show();
                                c.startActivity(new Intent(c, ClassdetailList.class));
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

    private void JoinEventAPI(final String eventid, final String friend) {

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
                                c.startActivity(new Intent(c, GroupMessage.class));
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
                params.put("friendsid", mpref.getString(GlobalConstants.user_id, ""));
                params.put("userid", friend);
                params.put("join", "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringRequest);
    }


    class ViewHolder {
        RelativeLayout totalparticipants_RL;
        LinearLayout custom_classlist_LL;
        TextView classlist_time_TV;
        TextView hot_name_TV, hot_coursename_TV, hot_topicname_TV, hot_locationame_TV, hot_parti_count_TV, join_study_TV;
        ImageView hot_classuser_event_RIV;

    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter1();
        }
        return valueFilter;
    }

    private class ValueFilter1 extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {

                filter_lists = new ArrayList<HashMap<String, String>>();
                String name = "";
                Log.e("iiiiiiiiiiiii", ii + "");

                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if (ii == 2) {
                        name = (mStringFilterList.get(i).get("Course_Name"));
                        Log.e("Hott Topics ", name);

                        if (name.toUpperCase()
                                .contains(constraint.toString().toUpperCase())) {

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("event_id", mStringFilterList.get(i)
                                    .get("event_id"));
                            map.put("user_id", mStringFilterList.get(i)
                                    .get("user_id"));
                            map.put("Course_Name", mStringFilterList.get(i)
                                    .get("Course_Name"));
                            map.put("topic", mStringFilterList.get(i)
                                    .get("topic"));
                            map.put("starttime", mStringFilterList.get(i)
                                    .get("starttime"));
                            map.put("location", mStringFilterList.get(i)
                                    .get("location"));
                            map.put("participants", mStringFilterList.get(i)
                                    .get("participants"));
                            map.put("fname", mStringFilterList.get(i).get("fname"));
                            map.put("lname", mStringFilterList.get(i).get("lname"));
                            map.put("userpic", mStringFilterList.get(i).get("userpic"));
                            map.put("eventdate", mStringFilterList.get(i).get("eventdate"));

                            filter_lists.add(map);

                        }

                    } else {
                        name = (mStringFilterList.get(i).get("fname"));
                        Log.e("Class List", name);

                        if (name.toUpperCase()
                                .contains(constraint.toString().toUpperCase())) {

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("event_id", mStringFilterList.get(i)
                                    .get("event_id"));
                            map.put("user_id", mStringFilterList.get(i)
                                    .get("user_id"));
                            map.put("Course_Name", mStringFilterList.get(i)
                                    .get("Course_Name"));
                            map.put("topic", mStringFilterList.get(i)
                                    .get("topic"));
                            map.put("starttime", mStringFilterList.get(i)
                                    .get("starttime"));
                            map.put("location", mStringFilterList.get(i)
                                    .get("location"));
                            map.put("participants", mStringFilterList.get(i)
                                    .get("participants"));
                            map.put("fname", mStringFilterList.get(i).get("fname"));
                            map.put("lname", mStringFilterList.get(i).get("lname"));
                            map.put("userpic", mStringFilterList.get(i).get("userpic"));

                            filter_lists.add(map);
                        }

                    }


                }
                results.count = filter_lists.size();
                results.values = filter_lists;
            } else {
                //no constraint given, just return all the data. (no search)
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            hottopicslist = (ArrayList<HashMap<String, String>>) results.values;
            notifyDataSetChanged();
        }
    }
}

