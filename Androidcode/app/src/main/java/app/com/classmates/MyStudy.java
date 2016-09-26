package app.com.classmates;

import android.annotation.SuppressLint;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import app.com.classmates.ProfileSet.CircleTransform;
import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.ConnectivityReceiver;
import app.com.classmates.multipleclasses.GlobalConstants;

@SuppressLint("NewApi")
public class MyStudy extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    ListView mystudy_LV;
    RelativeLayout mystudy_back_RL;
    TextView select_mystudy_TV, select_joinstudy_TV, study_editcancel_TV;
    LinearLayout sort_mystudy_LL, filter_study_LL, studyoptions_LL, mystudy_searchbar_LL;
    EditText filter_mystudies_ET;
    private ArrayList<HashMap<String, String>> recommendedlist;
    private ArrayList<HashMap<String, String>> Joinedlist;
    ArrayList<HashMap<String, String>> arrayList = null;
    SharedPreferences pref;
    Global global;
    ProgressDialog pd;
    PullRefreshLayout layout;
    int flag = 1;
    private Bundle translateRight;
    private Bundle translateLeft;
    MystudyAdapter adapter;
    boolean click = false;
    ProgressDialog pdia;
    private String timeZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_study);
        global = (Global) getApplicationContext();

        Fontchange.overrideFonts(MyStudy.this, ((RelativeLayout) findViewById(R.id.mystudy_main_RL)));
        checkConnection();
        pref = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);

        mystudiesinit();
        startAnim();

        if (pref.getInt("back", 0) == 1) {
            if (pref.getInt("mystudy", 0) != pref.getInt("flag", 0)) {
                selectoption(2);
                JoinedAPi(2);
            } else {
                GetMyStudy(2);
                selectoption(1);
            }
        } else {
            GetMyStudy(2);
        }

        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        Log.d("Time zone", "=" + tz.getDisplayName());
        timeZone = tz.getID();

    }

    private void mystudiesinit() {
        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (flag == 1)
                    GetMyStudy(2);
                else
                    JoinedAPi(2);
            }
        });

        filter_mystudies_ET = (EditText) findViewById(R.id.filter_mystudies_ET);
        filter_mystudies_ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapter.getFilter().filter(charSequence);
                } catch (Exception e) {

                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        filter_mystudies_ET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                studyoptions_LL.setVisibility(View.VISIBLE);
                filter_mystudies_ET.setText("");
                mystudy_searchbar_LL.setVisibility(View.GONE);

                if (flag == 1)
                    GetMyStudy(2);
                else
                    JoinedAPi(2);
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(textView.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
                return true;
            }
        });

        mystudy_searchbar_LL = (LinearLayout) findViewById(R.id.mystudy_searchbar_LL);
        studyoptions_LL = (LinearLayout) findViewById(R.id.studyoptions_LL);
        filter_study_LL = (LinearLayout) findViewById(R.id.filter_study_LL);
        sort_mystudy_LL = (LinearLayout) findViewById(R.id.sort_mystudy_LL);
        filter_study_LL.setOnClickListener(this);
        sort_mystudy_LL.setOnClickListener(this);

        mystudy_back_RL = (RelativeLayout) findViewById(R.id.mystudy_back_RL);
        mystudy_back_RL.setOnClickListener(this);

        mystudy_LV = (ListView) findViewById(R.id.mystudy_LV);

        select_mystudy_TV = (TextView) findViewById(R.id.select_mystudy_TV);
        select_joinstudy_TV = (TextView) findViewById(R.id.select_joinstudy_TV);
        study_editcancel_TV = (TextView) findViewById(R.id.study_editcancel_TV);

        select_mystudy_TV.setOnClickListener(this);
        select_joinstudy_TV.setOnClickListener(this);
        study_editcancel_TV.setOnClickListener(this);

        translateRight =
                ActivityOptions.
                        makeCustomAnimation(MyStudy.this,
                                R.anim.slide_down,
                                R.anim.slide_up).toBundle();

        translateLeft =
                ActivityOptions.
                        makeCustomAnimation(MyStudy.this,
                                R.anim.fade_in,
                                R.anim.slide_out_left).toBundle();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mystudy_back_RL:
                startActivity(new Intent(MyStudy.this, Drawer.class), translateRight);
                finish();
                break;

            case R.id.study_editcancel_TV:
                studyoptions_LL.setVisibility(View.VISIBLE);
                filter_mystudies_ET.setText("");
                mystudy_searchbar_LL.setVisibility(View.GONE);
                break;

            case R.id.select_mystudy_TV:
                flag = 1;
                SharedPreferences.Editor ee = pref.edit();
                ee.putInt("flag", flag);
                ee.commit();

                selectoption(1);
                pdia = ProgressDialog.show(MyStudy.this, "", "Loading...");
                GetMyStudy(1);
//                Method parsing parameter 1 means progress Dialog shows or not
                break;

            case R.id.select_joinstudy_TV:
                flag = 2;
                SharedPreferences.Editor ee1 = pref.edit();
                ee1.putInt("flag", flag);
                ee1.commit();

                selectoption(2);
                pdia = ProgressDialog.show(MyStudy.this, "", "Loading...");
//                Method parsing parameter 1 means progress Dialog shows or not and 2 means no progress dialog
                recommendedlist.clear();
                JoinedAPi(1);
                break;

            case R.id.filter_study_LL:
                if (!click) {
                    studyoptions_LL.setVisibility(View.GONE);
                    mystudy_searchbar_LL.setVisibility(View.VISIBLE);
                    click = true;
                } else {
                    mystudy_searchbar_LL.setVisibility(View.GONE);
                    studyoptions_LL.setVisibility(View.VISIBLE);
                    click = false;
                }
                break;

            case R.id.sort_mystudy_LL:
                filter_mystudies_ET.setText("");
                mystudy_searchbar_LL.setVisibility(View.GONE);
                studyoptions_LL.setVisibility(View.VISIBLE);
                click = false;

                Log.e("Flaaaaggggg Checkkkkk ", flag + "");
                if (flag == 1) {
                    showSortDialog("TIME");
                } else {
                    showSortDialog("CNAME");
                }

                break;
        }
    }

    private void showSortDialog(final String time) {

        final Dialog dialog = new Dialog(MyStudy.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_sort);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(MyStudy.this, ((LinearLayout) dialog.findViewById(R.id.choose_sort_LL)));

        final TextView sort_changes_TV = (TextView) dialog.findViewById(R.id.sort_changes_TV);
        final TextView sort_DT_TV = (TextView) dialog.findViewById(R.id.sort_DT_TV);
        final TextView sort_cancel_TV = (TextView) dialog.findViewById(R.id.sort_cancel_TV);

        sort_changes_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (time.equalsIgnoreCase("TIME")) {
                    arrayList = recommendedlist;
                    Collections.sort(arrayList, new Comparator<HashMap<String, String>>() {
                        @Override
                        public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                            return lhs.get("name").compareTo(rhs.get("name"));
                        }
                    });
                    adapter = new MystudyAdapter(MyStudy.this, arrayList, 1);
                    mystudy_LV.setAdapter(adapter);

                } else {
                    arrayList = Joinedlist;

                    Collections.sort(arrayList, new Comparator<HashMap<String, String>>() {
                        @Override
                        public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                            return lhs.get("Course_Name").compareTo(rhs.get("Course_Name"));
                        }
                    });
                    adapter = new MystudyAdapter(MyStudy.this, arrayList, 1);
                    mystudy_LV.setAdapter(adapter);
                }
                dialog.dismiss();
            }
        });

        sort_DT_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (time.equalsIgnoreCase("TIME")) {
                    GetMyStudy(2);
                } else {
                    JoinedAPi(2);
                }
                dialog.dismiss();
            }
        });

        sort_cancel_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void JoinedAPi(final int i1) {

        mystudy_LV.setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.show_add_study_LL)).setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstants.mURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Joinedlist = new ArrayList<HashMap<String, String>>();
                        Log.e("Response__Studyyyy___>>", response);

                        JSONObject job = null;
                        try {
                            job = new JSONObject(response);
                            String status = job.getString("status");

                            Log.e("Status", status);
                            if (i1 == 1) {
                                pdia.dismiss();
                            }

                            if (status.equalsIgnoreCase("success")) {

                                JSONArray jarr = job.getJSONArray("message");

                                for (int i = 0; i < jarr.length(); i++) {
                                    JSONObject obj = jarr.getJSONObject(i);
                                    HashMap<String, String> hmap = new HashMap<String, String>();
                                    hmap.put("event_id", obj.getString("event_id"));
                                    hmap.put("userid", obj.getString("userid"));
                                    hmap.put("Course_Name", obj.getString("Course_Name"));
                                    hmap.put("topic", obj.getString("topic"));
                                    hmap.put("starttime", obj.getString("starttime"));
                                    hmap.put("location", obj.getString("location"));
                                    hmap.put("participants", obj.getString("participants"));
                                    hmap.put("fname", obj.getString("fname"));
                                    hmap.put("lname", obj.getString("lname"));
                                    hmap.put("userpic", obj.getString("userpic"));
                                    hmap.put("eventdate", obj.getString("eventdate"));
                                    Joinedlist.add(hmap);
                                }
                                layout.setRefreshing(false);
                                stopAnim();
                                Log.e("Joineddd :List ", Joinedlist + "");
                                adapter = new MystudyAdapter(MyStudy.this, Joinedlist, 2);
                                mystudy_LV.setAdapter(adapter);
                            } else {
                                layout.setRefreshing(false);
                                stopAnim();
                                mystudy_LV.setVisibility(View.GONE);
                                ((LinearLayout) findViewById(R.id.show_add_study_LL)).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.SEE_TV)).setText("SEE RECOMMENDED STUDY \nTHAT YOU CAN JOIN");
                                ((ImageView) findViewById(R.id.adgainjoin_IV)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(MyStudy.this, HotTopics.class));
                                        finish();
                                    }
                                });
//                                showFailureErrorDialog("Sorry! No Join Study Found.");
//                                Toast.makeText(MyStudy.this, "Sorry! No Study Event Found.", Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(MyStudy.this,"Network Problem!!",Toast.LENGTH_SHORT).show();
                        showSnack(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", pref.getString(GlobalConstants.user_id, ""));
                params.put("time_zone", timeZone);
                params.put("joineventlist", "");
                Log.e("Class joineventlist", params + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void selectoption(int i) {

        if (i == 1) {
            select_mystudy_TV.setTextColor(Color.parseColor("#004a5e"));
            select_mystudy_TV.setBackgroundResource(R.drawable.blueborder);

            select_joinstudy_TV.setTextColor(Color.WHITE);
            select_joinstudy_TV.setBackgroundResource(R.drawable.loginborder);

        } else if (i == 2) {
            select_joinstudy_TV.setTextColor(Color.parseColor("#004a5e"));
            select_joinstudy_TV.setBackgroundResource(R.drawable.blueborder);

            select_mystudy_TV.setTextColor(Color.WHITE);
            select_mystudy_TV.setBackgroundResource(R.drawable.loginborder);
        }

    }

    class MystudyAdapter extends BaseAdapter implements Filterable {
        Context c;
        LayoutInflater mInflater;
        ArrayList<HashMap<String, String>> recommendeds;
        int ii;
        ValueFilter1 valueFilter;
        private ArrayList<HashMap<String, String>> filter_lists;
        ArrayList<HashMap<String, String>> mStringFilterList;

        public MystudyAdapter(Context c, ArrayList<HashMap<String, String>> recommendeds, int ii) {
            this.c = c;
            this.recommendeds = recommendeds;
            mStringFilterList = recommendeds;
            this.ii = ii;
            mInflater = LayoutInflater.from(c);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return recommendeds.size();
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
                view = mInflater.inflate(R.layout.custom_myevents, viewGroup, false);

                holder.event_invite_LL = (LinearLayout) view.findViewById(R.id.event_invite_LL);
                holder.event_delete_LL = (LinearLayout) view.findViewById(R.id.event_delete_LL);

                holder.recom_name_TV = (TextView) view.findViewById(R.id.recom_name_TV);
//                holder.recom_datecreated_TV = (TextView)view.findViewById(R.id.recom_datecreated_TV);
                holder.recom_coursename_TV = (TextView) view.findViewById(R.id.recom_coursename_TV);
                holder.recom_topicname_TV = (TextView) view.findViewById(R.id.recom_topicname_TV);
                holder.recom_loc_TV = (TextView) view.findViewById(R.id.recom_loc_TV);
                holder.recom_parti_count_TV = (TextView) view.findViewById(R.id.recom_parti_count_TV);
                holder.study_time_TV = (TextView) view.findViewById(R.id.study_time_TV);

                holder.recom_classuser_event_RIV = (ImageView) view.findViewById(R.id.recom_classuser_event_RIV);
                holder.myevent_parti_RL = (RelativeLayout) view.findViewById(R.id.myevent_parti_RL);

                holder.custom_mystudy_main_LL = (LinearLayout) view.findViewById(R.id.custom_myevents_main_LL);
                Fontchange.overrideFonts(c, holder.custom_mystudy_main_LL);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (ii == 1) {
                holder.recom_name_TV.setText(pref.getString(GlobalConstants.spUserName, ""));

                holder.recom_coursename_TV.setText(recommendeds.get(i).get("name").trim());
                holder.recom_topicname_TV.setText(recommendeds.get(i).get("topicname"));
                holder.recom_loc_TV.setText(recommendeds.get(i).get("location"));
                holder.recom_parti_count_TV.setText(recommendeds.get(i).get("participants"));

                if (pref.getString(GlobalConstants.spUserImage, "").contains("http")) {
                    Picasso.with(c).load(pref.getString(GlobalConstants.spUserImage, "")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).error(R.drawable.cm_logo).into(holder.recom_classuser_event_RIV);
                } else {
                    String img = GlobalConstants.ImageLink + pref.getString(GlobalConstants.spUserImage, "");
                    Picasso.with(c).load(img).placeholder(R.drawable.dummy_single).transform(new CircleTransform()).into(holder.recom_classuser_event_RIV);
                }

                holder.event_invite_LL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("study list position", i + "");
                        global.setPosition(i);
                        SharedPreferences.Editor e = pref.edit();
                        e.putString("Contact_event_id", recommendeds.get(i).get("eventid"));
                        e.commit();
                        c.startActivity(new Intent(c, Contacts.class), translateLeft);
                        finish();
                    }
                });

                holder.event_delete_LL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("study list position", recommendeds.get(i).get("eventid"));
                        DeletestudyDialog(recommendeds.get(i).get("eventid"));
                    }
                });

                holder.myevent_parti_RL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor e = pref.edit();
                        e.putString("Chat_event_id", recommendeds.get(i).get("eventid"));
                        e.putInt("back", 1);
                        e.commit();
                        System.out.println("Paricipants Event id  " + recommendedlist.get(i).get("eventid"));
                        c.startActivity(new Intent(c, ParticipantsDetail.class));
                    }
                });

            } else {

                holder.event_invite_LL.setVisibility(View.GONE);
                holder.event_delete_LL.setVisibility(View.GONE);

                holder.recom_name_TV.setText(recommendeds.get(i).get("fname") + " " + recommendeds.get(i).get("lname"));
                holder.recom_coursename_TV.setText(recommendeds.get(i).get("Course_Name"));
                holder.recom_topicname_TV.setText(recommendeds.get(i).get("topic"));
                holder.recom_loc_TV.setText(recommendeds.get(i).get("location"));


                holder.recom_parti_count_TV.setText(recommendeds.get(i).get("participants"));

                if (recommendeds.get(i).get("userpic").contains("http"))
                    Picasso.with(c).load(recommendeds.get(i).get("userpic")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).error(R.drawable.cm_logo).into(holder.recom_classuser_event_RIV);
                else
                    Picasso.with(c).load(GlobalConstants.ImageLink + recommendeds.get(i).get("userpic")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).error(R.drawable.cm_logo).into(holder.recom_classuser_event_RIV);

                holder.myevent_parti_RL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor e = pref.edit();
                        e.putString("Chat_event_id", recommendeds.get(i).get("event_id"));
                        e.putInt("back", 1);
                        e.commit();
//                        System.out.println("Paricipants Event id  "+  recommendedlist.get(i).get("event_id"));
                        c.startActivity(new Intent(c, ParticipantsDetail.class));
                    }
                });
            }
            holder.study_time_TV.setText(" " + recommendeds.get(i).get("starttime"));

            return view;
        }

        class ViewHolder {
            TextView recom_name_TV, recom_coursename_TV, recom_topicname_TV, recom_loc_TV, recom_parti_count_TV, study_time_TV;
            LinearLayout custom_mystudy_main_LL, event_invite_LL, event_delete_LL;
            ImageView recom_classuser_event_RIV;
            RelativeLayout myevent_parti_RL;
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

                    String name;
                    filter_lists = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < mStringFilterList.size(); i++) {
                        if (ii == 1) {
                            name = mStringFilterList.get(i).get("name");
                            if ((name.toUpperCase())
                                    .contains(constraint.toString().toUpperCase())) {

                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("name", mStringFilterList.get(i)
                                        .get("name"));
                                map.put("topicname", mStringFilterList.get(i)
                                        .get("topicname"));
                                map.put("location", mStringFilterList.get(i)
                                        .get("location"));
                                map.put("starttime", mStringFilterList.get(i)
                                        .get("starttime"));
                                map.put("participants", mStringFilterList.get(i)
                                        .get("participants"));
                                map.put("created", mStringFilterList.get(i)
                                        .get("created"));
                                map.put("eventid", mStringFilterList.get(i)
                                        .get("eventid"));
                                map.put("user_id", mStringFilterList.get(i)
                                        .get("user_id"));

                                filter_lists.add(map);
                            }
                        } else {
                            name = mStringFilterList.get(i).get("Course_Name");

                            if ((name.toUpperCase())
                                    .contains(constraint.toString().toUpperCase())) {

                                HashMap<String, String> map = new HashMap<String, String>();
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
                                map.put("fname", mStringFilterList.get(i)
                                        .get("fname"));
                                map.put("lname", mStringFilterList.get(i)
                                        .get("lname"));
                                map.put("userpic", mStringFilterList.get(i)
                                        .get("userpic"));
                                map.put("userid", mStringFilterList.get(i)
                                        .get("userid"));

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
                recommendeds = (ArrayList<HashMap<String, String>>) results.values;
                notifyDataSetChanged();
            }
        }

    }

    private void GetMyStudy(final int i1) {
        mystudy_LV.setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.show_add_study_LL)).setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstants.mURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        recommendedlist = new ArrayList<HashMap<String, String>>();
                        Log.e("Response__Studyyyy___>>", response);

                        JSONObject job = null;
                        try {
                            job = new JSONObject(response);
                            String status = job.getString("status");

                            Log.e("Status", status);
                            if (i1 == 1) {
                                pdia.dismiss();
                            }

                            if (status.equalsIgnoreCase("success")) {

                                JSONArray jarr = job.getJSONArray("message");
                                for (int i = 0; i < jarr.length(); i++) {
                                    JSONObject obj = jarr.getJSONObject(i);
                                    HashMap<String, String> hmap = new HashMap<String, String>();
                                    hmap.put("eventid", obj.getString("eventid"));
                                    hmap.put("user_id", obj.getString("user_id"));
                                    hmap.put("name", obj.getString("name"));
                                    hmap.put("topicname", obj.getString("topicname"));
                                    hmap.put("location", obj.getString("location"));
                                    hmap.put("starttime", obj.getString("starttime"));
                                    hmap.put("participants", obj.getString("participants"));
                                    hmap.put("created", obj.getString("created"));
                                    recommendedlist.add(hmap);
                                }
                                layout.setRefreshing(false);
                                stopAnim();
                                global.setRecommendedlist(recommendedlist);
                                adapter = new MystudyAdapter(MyStudy.this, recommendedlist, 1);
                                mystudy_LV.setAdapter(adapter);
                            } else {
                                layout.setRefreshing(false);
                                stopAnim();
                                mystudy_LV.setVisibility(View.GONE);
                                ((LinearLayout) findViewById(R.id.show_add_study_LL)).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.SEE_TV)).setText("NO STUDY FOUND \nCREATE NEW STUDY");

                                ((ImageView) findViewById(R.id.adgainjoin_IV)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(MyStudy.this, EnterCourseName.class));
                                        finish();
                                    }
                                });

//                            showFailureErrorDialog("Sorry! No Study Event Found. \n Create New Study.");
//                            Toast.makeText(MyStudy.this, "Sorry! No Study Event Found.", Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(MyStudy.this,"Network Problem!!",Toast.LENGTH_SHORT).show();
                        showSnack(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", pref.getString(GlobalConstants.user_id, ""));
                params.put("time_zone", timeZone);
                params.put("myeventlist", "");
                Log.e("Class  Mystudy", params + "");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void DeletestudyDialog(final String eventid) {
        final Dialog dialog = new Dialog(MyStudy.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_study);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(MyStudy.this, ((LinearLayout) dialog.findViewById(R.id.delete_dia_main_LL)));

        final TextView reset_classname_B = (TextView) dialog.findViewById(R.id.diadelete_class_TV);
        final TextView cancel_classname_B = (TextView) dialog.findViewById(R.id.dia_delete_cancel_class_TV);

        reset_classname_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = ProgressDialog.show(MyStudy.this, "Please Wait", "Deleting Your Study..");
                DeleteStudyEvent(eventid);
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

    private void DeleteStudyEvent(final String eventid) {

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
                                Toast.makeText(MyStudy.this, "Successfully Deleted!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("status Fail", status);

//                                Toast.makeText(MyStudy.this,msgg,Toast.LENGTH_SHORT).show();
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
//                        Toast.makeText(MyStudy.this,"Network Problem!!",Toast.LENGTH_SHORT).show();
                        showSnack(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("eventid", eventid);
                params.put("delete", "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void startAnim() {
        findViewById(R.id.avloadingIndicatorView).setVisibility(View.VISIBLE);
    }

    public void stopAnim() {
        findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MyStudy.this, Drawer.class), translateRight);
        finish();
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

            Snackbar snackbar = Snackbar.make(((RelativeLayout) findViewById(R.id.mystudy_main_RL)), message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
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

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showFailureErrorDialog(final String error_msg) {

        final Dialog dialog = new Dialog(MyStudy.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(MyStudy.this, ((LinearLayout) dialog.findViewById(R.id.dia_show_error_msg_LL)));

        final TextView ok_error_msg_TV = (TextView) dialog.findViewById(R.id.ok_error_msg_TV);
        final TextView show_error_TV = (TextView) dialog.findViewById(R.id.show_error_TV);
        show_error_TV.setText(error_msg);

        ok_error_msg_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(MyStudy.this, Drawer.class), translateRight);
                finish();
            }
        });

        dialog.show();
    }
}
