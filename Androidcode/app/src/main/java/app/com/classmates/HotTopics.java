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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.ConnectivityReceiver;
import app.com.classmates.multipleclasses.GlobalConstants;

@SuppressLint("NewApi")
public class HotTopics extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    ListView hottopics_LV;
    ArrayList<HashMap<String, String>> hottopicslist;
    private PullRefreshLayout layout;
    private Bundle translateRight;
    SharedPreferences mpref;
    EditText filter_hottopic_ET;
    LinearLayout filter_hotyopic_LL, sort_hottopics_LL, hottopics_searchbar_LL;
    boolean click = false;
    private ArrayList<HashMap<String, String>> arrayList = null;
    ClassListAdapter adapter;
    private String timeZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hot_topics);
        Fontchange.overrideFonts(HotTopics.this, ((RelativeLayout) findViewById(R.id.hottopics_main_RL)));
        checkConnection();
        mpref = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);

        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout_hottpics);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (hottopicslist != null)
                    hottopicslist.clear();
                GetHotTopics();
            }
        });
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        Log.d("Time zone", "=" + tz.getDisplayName());
        timeZone = tz.getID();

        hottopicInIt();
        startAnim();
        GetHotTopics();
    }

    private void hottopicInIt() {

        SharedPreferences.Editor e = mpref.edit();
        e.putInt("back", 3);
        e.commit();


        hottopics_searchbar_LL = (LinearLayout) findViewById(R.id.hottopics_searchbar_LL);
        hottopics_LV = (ListView) findViewById(R.id.hottopics_LV);
        translateRight =
                ActivityOptions.
                        makeCustomAnimation(HotTopics.this, R.anim.slide_down,
                                R.anim.slide_up).toBundle();

        hottopicslist = new ArrayList<HashMap<String, String>>();
        ((RelativeLayout) findViewById(R.id.Recommended_back_RL)).setOnClickListener(this);

        if (hottopicslist != null) {
            hottopicslist.clear();
        }

        filter_hottopic_ET = (EditText) findViewById(R.id.filter_hottopic_ET);
        filter_hottopic_ET.addTextChangedListener(new TextWatcher() {
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

        filter_hottopic_ET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                filter_hottopic_ET.setText("");
                hottopics_searchbar_LL.setVisibility(View.GONE);
                GetHotTopics();
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(textView.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
                return true;
            }
        });

        filter_hotyopic_LL = (LinearLayout) findViewById(R.id.filter_hotyopic_LL);
        filter_hotyopic_LL.setOnClickListener(this);

        sort_hottopics_LL = (LinearLayout) findViewById(R.id.sort_hottopics_LL);
        sort_hottopics_LL.setOnClickListener(this);

        ((TextView) findViewById(R.id.hot_editcancel_TV)).setOnClickListener(this);
    }


    private void GetHotTopics() {
        if (hottopicslist != null) {
            hottopicslist.clear();
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
                                    hmap.put("user_id", obj.getString("user_id"));
                                    hmap.put("Course_Name", obj.getString("Course_Name"));
                                    hmap.put("topic", obj.getString("topic"));
                                    hmap.put("starttime", obj.getString("starttime"));
                                    hmap.put("location", obj.getString("location"));
                                    hmap.put("participants", obj.getString("participants"));
                                    hmap.put("fname", obj.getString("fname"));
                                    hmap.put("lname", obj.getString("lname"));
                                    hmap.put("userpic", obj.getString("userpic"));
                                    hmap.put("eventdate", obj.getString("eventdate"));
                                    hottopicslist.add(hmap);
                                }
                                layout.setRefreshing(false);
                                stopAnim();
                                adapter = new ClassListAdapter(HotTopics.this, hottopicslist, 2);
                                hottopics_LV.setAdapter(adapter);
                            } else {
                                layout.setRefreshing(false);
                                stopAnim();

//                                Toast.makeText(HotTopics.this, "Sorry! No Recommended List Found.", Toast.LENGTH_SHORT).show();
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
//                        Toast.makeText(HotTopics.this,"Network Problem!!",Toast.LENGTH_SHORT).show();
                        showSnack(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("HotTopics", "");
                params.put("time_zone", timeZone);
                params.put("userid", mpref.getString(GlobalConstants.user_id, ""));
                Log.e("Class  hotopics", params + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void showSortDialog() {

        final Dialog dialog = new Dialog(HotTopics.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_sort);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(HotTopics.this, ((LinearLayout) dialog.findViewById(R.id.choose_sort_LL)));

        final TextView sort_changes_TV = (TextView) dialog.findViewById(R.id.sort_changes_TV);
        final TextView sort_DT_TV = (TextView) dialog.findViewById(R.id.sort_DT_TV);
        final TextView sort_cancel_TV = (TextView) dialog.findViewById(R.id.sort_cancel_TV);

        sort_changes_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayList = hottopicslist;

                Collections.sort(arrayList, new Comparator<HashMap<String, String>>() {
                    @Override
                    public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                        return lhs.get("Course_Name").compareTo(rhs.get("Course_Name"));
                    }
                });
                adapter = new ClassListAdapter(HotTopics.this, arrayList, 2);
                hottopics_LV.setAdapter(adapter);
                dialog.dismiss();
            }
        });

        sort_DT_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetHotTopics();
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

    public void startAnim() {
        findViewById(R.id.avloadingIndicatorViewR).setVisibility(View.VISIBLE);
    }

    public void stopAnim() {
        findViewById(R.id.avloadingIndicatorViewR).setVisibility(View.GONE);
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
            Snackbar snackbar = Snackbar.make(((RelativeLayout) findViewById(R.id.hottopics_main_RL)), message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
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
        startActivity(new Intent(HotTopics.this, Drawer.class), translateRight);
        finish();
    }

    private void showFailureErrorDialog(final String error_msg) {

        final Dialog dialog = new Dialog(HotTopics.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(HotTopics.this, ((LinearLayout) dialog.findViewById(R.id.dia_show_error_msg_LL)));

        final TextView ok_error_msg_TV = (TextView) dialog.findViewById(R.id.ok_error_msg_TV);
        final TextView show_error_TV = (TextView) dialog.findViewById(R.id.show_error_TV);
        show_error_TV.setText(error_msg);

        ok_error_msg_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(HotTopics.this, Drawer.class), translateRight);
                finish();
            }
        });

        dialog.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.Recommended_back_RL:
                startActivity(new Intent(HotTopics.this, Drawer.class), translateRight);
                finish();
                break;

            case R.id.filter_hotyopic_LL:
                if (!click) {
                    hottopics_searchbar_LL.setVisibility(View.VISIBLE);
                    click = true;
                } else {
                    hottopics_searchbar_LL.setVisibility(View.GONE);
                    click = false;
                }
                break;

            case R.id.sort_hottopics_LL:
                filter_hottopic_ET.setText("");
                hottopics_searchbar_LL.setVisibility(View.GONE);
                click = false;

                showSortDialog();
                break;

            case R.id.hot_editcancel_TV:
                filter_hottopic_ET.setText("");
                hottopics_searchbar_LL.setVisibility(View.GONE);
                break;
        }

    }
}
