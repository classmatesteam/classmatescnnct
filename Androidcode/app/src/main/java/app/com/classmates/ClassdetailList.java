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

import app.com.classmates.multipleclasses.ConnectivityReceiver;
import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.GlobalConstants;

@SuppressLint("NewApi")
public class ClassdetailList extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    ListView class_LV;
    RelativeLayout classdetail_back_RL;
    SharedPreferences mpref;
    Global global;
    ArrayList<HashMap<String, String>> classdetaillist;
    Bundle translateRight;
    private PullRefreshLayout layout;
    EditText filter_classdetail_ET;
    ImageView add_again_class_IV;
    LinearLayout filter_classdetail_LL, sort_classdetail_LL, classdetail_searchbar_LL, show_add_study_LL;
    boolean click = false;
    private ArrayList<HashMap<String, String>> arrayList = null;
    private ClassListAdapter adapter;
    TextView for_TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classdetail_list);
        Fontchange.overrideFonts(ClassdetailList.this, ((RelativeLayout) findViewById(R.id.classdetail_main_RL)));
        global = (Global) getApplicationContext();
        mpref = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
        checkConnection();

        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (classdetaillist != null)
                    classdetaillist.clear();
                GetClassdetail();
            }
        });

        translateRight =
                ActivityOptions.
                        makeCustomAnimation(ClassdetailList.this,
                                R.anim.fade_in,
                                R.anim.slide_out_right).toBundle();

        class_LV = (ListView) findViewById(R.id.class_LV);
        classdetaillist = new ArrayList<HashMap<String, String>>();
        ((TextView) findViewById(R.id.classdetail_titlename_TV)).setText(global.getClassid());

        classdetail_back_RL = (RelativeLayout) findViewById(R.id.classdetail_back_RL);
        classdetail_back_RL.setOnClickListener(this);

        filter_classdetail_ET = (EditText) findViewById(R.id.filter_classdetail_ET);
        filter_classdetail_ET.addTextChangedListener(new TextWatcher() {
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

        filter_classdetail_ET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                filter_classdetail_ET.setText("");
                classdetail_searchbar_LL.setVisibility(View.GONE);
                GetClassdetail();
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(textView.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
                return true;
            }
        });

        show_add_study_LL = (LinearLayout) findViewById(R.id.show_add_study_LL);
        classdetail_searchbar_LL = (LinearLayout) findViewById(R.id.classdetail_searchbar_LL);
        ((TextView) findViewById(R.id.editcancel_TV)).setOnClickListener(this);

        filter_classdetail_LL = (LinearLayout) findViewById(R.id.filter_classdetail_LL);
        filter_classdetail_LL.setOnClickListener(this);


        sort_classdetail_LL = (LinearLayout) findViewById(R.id.sort_classdetail_LL);
        sort_classdetail_LL.setOnClickListener(this);


        add_again_class_IV = (ImageView) findViewById(R.id.adgainclass_IV);

        for_TV = (TextView) findViewById(R.id.for_TV);
        for_TV.setText("NO STUDY FOUND FOR \n" + global.getClassid() + "");

        if (classdetaillist != null) {
            classdetaillist.clear();
        }

        SharedPreferences.Editor e = mpref.edit();
        e.putInt("back", 2);
        e.commit();

        GetClassdetail();
    }

    private void showSortDialog() {

        final Dialog dialog = new Dialog(ClassdetailList.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_sort);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(ClassdetailList.this, ((LinearLayout) dialog.findViewById(R.id.choose_sort_LL)));

        final TextView sort_changes_TV = (TextView) dialog.findViewById(R.id.sort_changes_TV);
        final TextView sort_DT_TV = (TextView) dialog.findViewById(R.id.sort_DT_TV);
        final TextView sort_cancel_TV = (TextView) dialog.findViewById(R.id.sort_cancel_TV);
        sort_changes_TV.setText("UserName");

        sort_changes_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayList = classdetaillist;

                Collections.sort(arrayList, new Comparator<HashMap<String, String>>() {
                    @Override
                    public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                        return lhs.get("fname").compareTo(rhs.get("fname"));
                    }
                });
                adapter = new ClassListAdapter(ClassdetailList.this, arrayList, 1);
                class_LV.setAdapter(adapter);
                dialog.dismiss();
            }
        });

        sort_DT_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetClassdetail();
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

    String timeZone = "";

    private void GetClassdetail() {

        if (classdetaillist != null) {
            classdetaillist.clear();
        }

        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        Log.d("Time zone", "=" + tz.getDisplayName());
        timeZone = tz.getID();

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
                                show_add_study_LL.setVisibility(View.GONE);
                                JSONArray jarr = job.getJSONArray("message");
                                for (int i = 0; i < jarr.length(); i++) {
                                    JSONObject obj = jarr.getJSONObject(i);
                                    HashMap<String, String> hmap = new HashMap<String, String>();
                                    hmap.put("event_id", obj.getString("eventid"));
                                    hmap.put("user_id", obj.getString("userid"));
                                    hmap.put("Course_Name", obj.getString("Course_Name").replaceAll("\\s+", ""));
                                    hmap.put("topic", obj.getString("topic"));
                                    hmap.put("starttime", obj.getString("starttime"));
                                    hmap.put("location", obj.getString("location"));
                                    hmap.put("participants", obj.getString("participants"));
                                    hmap.put("fname", obj.getString("fname"));
                                    hmap.put("lname", obj.getString("lname"));
                                    hmap.put("userpic", obj.getString("userpic"));
                                    hmap.put("join", obj.getString("join"));
                                    hmap.put("eventdate", obj.getString("eventdate"));
                                    classdetaillist.add(hmap);

                                }
                                layout.setRefreshing(false);

                                global.setClassname(null);
                                adapter = new ClassListAdapter(ClassdetailList.this, classdetaillist, 1);
                                class_LV.setAdapter(adapter);

                            } else {
                                layout.setRefreshing(false);
                                layout.setVisibility(View.INVISIBLE);
                                show_add_study_LL.setVisibility(View.VISIBLE);
                                add_again_class_IV.setOnClickListener(ClassdetailList.this);
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
//                        Toast.makeText(ClassdetailList.this,"Network Failed, Try Again!!",Toast.LENGTH_SHORT).show();
                        showSnack(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", global.getClassid());
                params.put("time_zone", timeZone);
                params.put("userid", mpref.getString(GlobalConstants.user_id, ""));
                params.put("searchevent", "");
                Log.e("Class list detail", params + "");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ClassdetailList.this, Drawer.class), translateRight);
        finish();
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        Snackbar snackbar;
        int color;

        if (!isConnected) {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
            snackbar = Snackbar.make(((RelativeLayout) findViewById(R.id.classdetail_main_RL)), message, Snackbar.LENGTH_LONG);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.adgainclass_IV:
                global.setCoursename(global.getClassid());
                SharedPreferences.Editor e = mpref.edit();
                e.putInt("Class_list", 2);
                e.commit();
                startActivity(new Intent(ClassdetailList.this, EnterTopicName.class), translateRight);
                finish();
                break;

            case R.id.editcancel_TV:
                filter_classdetail_ET.setText("");
                classdetail_searchbar_LL.setVisibility(View.GONE);
                break;

            case R.id.classdetail_back_RL:
                startActivity(new Intent(ClassdetailList.this, Drawer.class), translateRight);
                finish();
                break;

            case R.id.filter_classdetail_LL:
                if (!click) {
                    classdetail_searchbar_LL.setVisibility(View.VISIBLE);
                    click = true;
                } else {
                    classdetail_searchbar_LL.setVisibility(View.GONE);
                    click = false;
                }
                break;

            case R.id.sort_classdetail_LL:
                filter_classdetail_ET.setText("");
                classdetail_searchbar_LL.setVisibility(View.GONE);
                click = false;
                showSortDialog();
                break;
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

}
