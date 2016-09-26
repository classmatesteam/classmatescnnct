package app.com.classmates;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
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
public class GroupMessage extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    SwipeMenuListView groupmsg_LL;
    SharedPreferences mpref;
    ArrayList<HashMap<String, String>> groupmsgslist;
    Global global;
    private Bundle translateRight;
    private PullRefreshLayout layout;
    LinearLayout gmsg_searchbar_LL, filter_gmsg_LL;
    TextView gmsgcancel_TV;
    EditText filter_gmsg_ET;
    GroupMsgAdapter adpter;
    ArrayList<String> event_ids;
    String item;
    public static final int LTGRAY = 0xFFCCCCCC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_message);
        global = (Global) getApplicationContext();
        mpref = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);

        Fontchange.overrideFonts(GroupMessage.this, ((RelativeLayout) findViewById(R.id.groupmsg_main_RL)));
        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (groupmsgslist != null)
                    groupmsgslist.clear();
                GetGroupMessagesAPI();
            }
        });

        checkConnection();
        init();
        startAnim();
        GetGroupMessagesAPI();

    }

    private void init() {
        event_ids = new ArrayList<String>();

        groupmsgslist = new ArrayList<HashMap<String, String>>();
        groupmsg_LL = (SwipeMenuListView) findViewById(R.id.groupmsg_LL);

        gmsg_searchbar_LL = (LinearLayout) findViewById(R.id.gmsg_searchbar_LL);
        gmsg_searchbar_LL.setOnClickListener(this)
        ;
        filter_gmsg_LL = (LinearLayout) findViewById(R.id.filter_gmsg_LL);
        filter_gmsg_LL.setOnClickListener(this);

        gmsgcancel_TV = (TextView) findViewById(R.id.gmsgcancel_TV);
        gmsgcancel_TV.setOnClickListener(this);

        filter_gmsg_ET = (EditText) findViewById(R.id.filter_gmsg_ET);
        filter_gmsg_ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adpter.getFilter().filter(charSequence);
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

        filter_gmsg_ET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                filter_gmsg_ET.setText("");
                gmsg_searchbar_LL.setVisibility(View.GONE);
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(textView.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
                return true;
            }
        });

        groupmsg_LL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                global.setPosition(i);
                Bundle translatel =
                        ActivityOptions.
                                makeCustomAnimation(GroupMessage.this,
                                        R.anim.fade_in,
                                        R.anim.slide_out_left).toBundle();
                startActivity(new Intent(GroupMessage.this, GroupChat.class), translatel);
                finish();
            }
        });

        ((RelativeLayout) findViewById(R.id.groupmsg_back_RL)).setOnClickListener(this);
        translateRight =
                ActivityOptions.
                        makeCustomAnimation(GroupMessage.this,
                                R.anim.slide_down,
                                R.anim.slide_up).toBundle();

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                switch (menu.getViewType()) {
                    case 0:
                        createMenu1(menu);
                        break;
                }
            }
        };

        groupmsg_LL.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                item = event_ids.get(position);
                Log.e("Event Idd >>>>>", item);
                switch (index) {
                    case 0:
                        groupmsgslist.remove(position);
                        DeleteMsgsAPI();
                        adpter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });

        groupmsg_LL.setMenuCreator(creator);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.groupmsg_back_RL:
                startActivity(new Intent(GroupMessage.this, Drawer.class), translateRight);
                finish();
                break;

            case R.id.filter_gmsg_LL:
                gmsg_searchbar_LL.setVisibility(View.VISIBLE);
                break;

            case R.id.gmsgcancel_TV:
                filter_gmsg_ET.setText("");
                gmsg_searchbar_LL.setVisibility(View.GONE);
                break;
        }

    }

    private void createMenu1(SwipeMenu menu) {
        SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
        item1.setBackground(new ColorDrawable(Color.rgb(0xE5, 0x18, 0x5E)));
        item1.setWidth(dp2px(70));
        item1.setIcon(R.drawable.remove_swipe);
        menu.addMenuItem(item1);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @SuppressWarnings("ResourceAsColor")
    class GroupMsgAdapter extends BaseAdapter implements Filterable {

        Context c;
        LayoutInflater mInflater;
        ArrayList<HashMap<String, String>> groupmsgs;
        SharedPreferences pref;
        ValueFilter1 valueFilter;
        private ArrayList<HashMap<String, String>> filter_lists;
        ArrayList<HashMap<String, String>> mStringFilterList;
//        private Animation animBlink;

        public GroupMsgAdapter(Context c, ArrayList<HashMap<String, String>> groupmsgs) {
            this.c = c;
            this.groupmsgs = groupmsgs;
            mStringFilterList = groupmsgs;
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
                view = mInflater.inflate(R.layout.custom_msgsdetail, viewGroup, false);
                holder.custom_msgsdetail_main_LL = (LinearLayout) view.findViewById(R.id.custom_msgsdetail_main_LL);

                holder.groupusername_TV = (TextView) view.findViewById(R.id.groupusername_TV);
                holder.event_timedetail_TV = (TextView) view.findViewById(R.id.event_timedetail_TV);

                holder.groupuser_pic_RIV = (ImageView) view.findViewById(R.id.groupuser_pic_RIV);
                holder.cus_msg_detail_RL = (RelativeLayout) view.findViewById(R.id.cus_msg_detail_RL);

//                animBlink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
                Fontchange.overrideFonts(c, holder.custom_msgsdetail_main_LL);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            try {
                if (groupmsgs.get(i).get("read").equalsIgnoreCase("0")) {
                    Log.e("Umredadddd", "UNREAD");
                    holder.event_timedetail_TV.setSoundEffectsEnabled(true);
                    holder.event_timedetail_TV.setTypeface(Typeface.DEFAULT_BOLD);
                    holder.groupusername_TV.setTypeface(Typeface.DEFAULT_BOLD);
                    holder.groupusername_TV.setTextColor(Color.WHITE);
//                    holder.groupusername_TV.setAnimation(animBlink);

                    holder.event_timedetail_TV.setTextColor(Color.WHITE);
                } else {
                    holder.event_timedetail_TV.setTypeface(Typeface.DEFAULT);
//                    holder.groupusername_TV.setAnimation(null);
                }

                String msghead = groupmsgs.get(i).get("name") + "(" + groupmsgs.get(i).get("topicname") + ")";
                holder.groupusername_TV.setText(msghead);
                String lst = groupmsgs.get(i).get("lastmsg");
                if (lst.equalsIgnoreCase("NULL")) {
                    holder.event_timedetail_TV.setText("You were added.");
                    holder.event_timedetail_TV.setTypeface(Typeface.DEFAULT);
                    holder.event_timedetail_TV.setTextColor(Color.parseColor("#b6b6b6"));
                    holder.groupusername_TV.setTypeface(Typeface.DEFAULT);
                    holder.groupusername_TV.setTextColor(Color.parseColor("#b6b6b6"));
                } else
                    holder.event_timedetail_TV.setText(lst);

                if (groupmsgs.get(i).get("userpic").contains("http")) {
                    Picasso.with(c).load(groupmsgs.get(i).get("userpic")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).error(R.drawable.cm_logo).into(holder.groupuser_pic_RIV);
                } else {
                    String names = groupmsgs.get(i).get("fname") + " " + groupmsgs.get(i).get("lname");
                    Picasso.with(c).load(GlobalConstants.ImageLink + groupmsgs.get(i).get("userpic")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).error(R.drawable.cm_logo).into(holder.groupuser_pic_RIV);
//                    Log.e("Message Imagess ****", GlobalConstants.ImageLink+groupmsgs.get(i).get("userpic"));
                }
            } catch (Exception e) {
                System.out.println("Exceptionss in Group Msg>> " + e);
            }
            return view;
        }

        class ViewHolder {
            LinearLayout custom_msgsdetail_main_LL;
            TextView groupusername_TV, event_timedetail_TV;
            ImageView groupuser_pic_RIV;
            RelativeLayout cus_msg_detail_RL;
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
                    for (int i = 0; i < mStringFilterList.size(); i++) {
                        if ((mStringFilterList.get(i).get("name").toUpperCase())
                                .contains(constraint.toString().toUpperCase())) {

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("topicname", mStringFilterList.get(i)
                                    .get("topicname"));
                            map.put("lastmsgtime", mStringFilterList.get(i)
                                    .get("lastmsgtime"));
                            map.put("lastmsg", mStringFilterList.get(i)
                                    .get("lastmsg"));
                            map.put("eventid", mStringFilterList.get(i)
                                    .get("eventid"));
                            map.put("name", mStringFilterList.get(i)
                                    .get("name"));
                            map.put("userpic", mStringFilterList.get(i)
                                    .get("userpic"));
                            map.put("lname", mStringFilterList.get(i)
                                    .get("lname"));
                            map.put("fname", mStringFilterList.get(i)
                                    .get("fname"));
                            map.put("display", mStringFilterList.get(i)
                                    .get("display"));
                            map.put("read", mStringFilterList.get(i)
                                    .get("read"));
                            filter_lists.add(map);
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
                groupmsgs = (ArrayList<HashMap<String, String>>) results.values;
                notifyDataSetChanged();
            }
        }
    }

    private void GetGroupMessagesAPI() {
        if (groupmsgslist != null) {
            groupmsgslist.clear();
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
                                    hmap.put("display", obj.getString("display"));
                                    Log.e("Displayayy > > ", obj.getString("display"));

                                    if (obj.getString("display").equalsIgnoreCase("Yes")) {
                                        hmap.put("fname", obj.getString("fname"));
                                        hmap.put("lname", obj.getString("lname"));
                                        hmap.put("userpic", obj.getString("userpic"));
                                        hmap.put("name", obj.getString("name"));
                                        hmap.put("eventid", obj.getString("eventid"));
                                        hmap.put("topicname", obj.getString("topicname"));
                                        hmap.put("lastmsgtime", obj.getString("lastmsgtime"));
                                        hmap.put("lastmsg", obj.getString("lastmsg"));
                                        hmap.put("display", obj.getString("display"));
                                        hmap.put("read", obj.getString("read"));

                                        event_ids.add(obj.getString("eventid"));
                                        groupmsgslist.add(hmap);
                                    }
                                }
                                global.setGroupmsgList(groupmsgslist);
                                layout.setRefreshing(false);
                                stopAnim();
                                adpter = new GroupMsgAdapter(GroupMessage.this, groupmsgslist);
                                adpter.notifyDataSetChanged();
                                groupmsg_LL.setAdapter(adpter);
                            } else {
                                layout.setRefreshing(false);
                                stopAnim();
//                            Toast.makeText(GroupMessage.this, job.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("userid", mpref.getString(GlobalConstants.user_id, ""));
                params.put("grouplist", "");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void DeleteMsgsAPI() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstants.mURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Delete Response_____>>", response);

                        JSONObject job = null;
                        try {
                            job = new JSONObject(response);
                            String status = job.getString("status");
                            Log.e("Status", status);
                            if (status.equalsIgnoreCase("success")) {
                                GetGroupMessagesAPI();
                            } else {

//                            Toast.makeText(GroupMessage.this, job.getString("message"), Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(GroupMessage.this,error.toString(),Toast.LENGTH_SHORT).show();
                        showSnack(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("eventid", item);
                params.put("userid", mpref.getString(GlobalConstants.user_id, ""));
                params.put("deletemessage", "");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showFailureErrorDialog(final String error_msg) {

        final Dialog dialog = new Dialog(GroupMessage.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(GroupMessage.this, ((LinearLayout) dialog.findViewById(R.id.dia_show_error_msg_LL)));

        final TextView ok_error_msg_TV = (TextView) dialog.findViewById(R.id.ok_error_msg_TV);
        final TextView show_error_TV = (TextView) dialog.findViewById(R.id.show_error_TV);
        show_error_TV.setText(error_msg);

        ok_error_msg_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(GroupMessage.this, Drawer.class), translateRight);
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
            Snackbar snackbar = Snackbar.make(((RelativeLayout) findViewById(R.id.groupmsg_main_RL)), message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    public BroadcastReceiver mgetMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GetGroupMessagesAPI();
        }
    };

    @Override
    protected void onResume() {
        this.registerReceiver(mgetMessageReceiver, new IntentFilter("unique_name"));
        SharedPreferences.Editor ed = mpref.edit();
        ed.putBoolean("appinbackground", true);
        ed.commit();
        Log.e("in Activity", "in resume");
        super.onResume();
        Global.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        this.unregisterReceiver(mgetMessageReceiver);
        SharedPreferences.Editor ed = mpref.edit();
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
        startActivity(new Intent(GroupMessage.this, Drawer.class), translateRight);
        finish();
    }

    public void startAnim() {
        findViewById(R.id.avloadingIndicatorView_gm).setVisibility(View.VISIBLE);
    }

    public void stopAnim() {
        findViewById(R.id.avloadingIndicatorView_gm).setVisibility(View.GONE);
    }
}
