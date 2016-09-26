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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.com.classmates.multipleclasses.ConnectivityReceiver;
import app.com.classmates.multipleclasses.GlobalConstants;
import app.com.classmates.multipleclasses.HorizontalListView;
import app.com.classmates.fonts.Fontchange;

@SuppressLint("NewApi")
@SuppressWarnings("WrongConstant")
public class EnterCourseName extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    RelativeLayout event_main_RL;
    RelativeLayout event_back_RL;
    Global global;
    private Bundle translateDown;
    private ArrayList<HashMap<String, String>> classlist;
    HorizontalListView select_class_HLV;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_course_name);

        global = (Global) getApplicationContext();
        sp = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);

        event_main_RL = (RelativeLayout) findViewById(R.id.event_main_RL);
        Fontchange.overrideFonts(EnterCourseName.this, event_main_RL);

        checkConnection();
        translateDown =
                ActivityOptions.
                        makeCustomAnimation(getApplicationContext(),
                                R.anim.slide_down,
                                R.anim.slide_up).toBundle();

        entercourseinit();
    }

    private void entercourseinit() {

        event_back_RL = (RelativeLayout) findViewById(R.id.event_back_RL);
        event_back_RL.setOnClickListener(this);

        select_class_HLV = (HorizontalListView) findViewById(R.id.select_class_HLV);
        select_class_HLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String id = classlist.get(i).get("name");
                global.setCoursename(id);
                Log.e("REmove Classs Method", id);
                Log.e("REmove Classs Userid", sp.getString(GlobalConstants.user_id, ""));

                Bundle translate = ActivityOptions.
                        makeCustomAnimation(getApplicationContext(),
                                R.anim.fade_in,
                                R.anim.slide_out_left).toBundle();

                startActivity(new Intent(EnterCourseName.this, EnterTopicName.class), translate);
                finish();
            }
        });
        startAnim();
        classesDetail();
    }

    class SelectClassAdapter extends BaseAdapter {

        LayoutInflater mInflater;
        ArrayList<HashMap<String, String>> list;
        Context context;
        SharedPreferences pref;

        public SelectClassAdapter(Context context, ArrayList<HashMap<String, String>> list) {
            this.context = context;
            this.list = list;
            pref = context.getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
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
                view = mInflater.inflate(R.layout.custom_course_hlv, null);

                Fontchange.overrideFonts(context, ((RelativeLayout) view.findViewById(R.id.custom_course_hlv_main_RL)));
                holder.remove_class_TV = (TextView) view.findViewById(R.id.course_class_TV);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            String name = list.get(i).get("name").trim();
            StringBuffer sbuff = new StringBuffer(name);
            sbuff.insert(3, "\n");
            holder.remove_class_TV.setText(sbuff);

            return view;
        }

        class ViewHolder {
            TextView remove_class_TV;
        }
    }

    private void classesDetail() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstants.mURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        classlist = new ArrayList<HashMap<String, String>>();

                        JSONObject job = null;
                        try {
                            job = new JSONObject(response);
                            String status = job.getString("status");

                            if (status.equalsIgnoreCase("success")) {

                                JSONArray jarr = job.getJSONArray("message");
                                for (int i = 0; i < jarr.length(); i++) {
                                    JSONObject jobj = jarr.getJSONObject(i);
                                    HashMap<String, String> hmap = new HashMap<String, String>();
                                    if (!jobj.getString("name").replaceAll("\\s+", "").equalsIgnoreCase("")) {
                                        hmap.put("classid", jobj.getString("classid"));
                                        hmap.put("rollno", jobj.getString("rollno"));
                                        hmap.put("name", jobj.getString("name").replaceAll("\\s+", ""));
                                        hmap.put("proffessor", jobj.getString("proffessor"));
                                        hmap.put("user_id", jobj.getString("user_id"));
                                        classlist.add(hmap);
                                    }else {
                                        Log.e("Oncee","Oncee");
                                    }
                                }
                                global.setClassesnameList(classlist);
                                stopAnim();
                                select_class_HLV.setAdapter(new SelectClassAdapter(EnterCourseName.this, classlist));

                            } else {
                                stopAnim();
                                showFailureErrorDialog("Sorry! No Class Found. \n Create New Class.");
//                                Toast.makeText(EnterCourseName.this, "Sorry! No Class Found", Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(getApplicationContext(),"Network Problem !!",Toast.LENGTH_SHORT).show();
                        showSnack(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("userid", sp.getString(GlobalConstants.user_id, ""));
                params.put("classlist", "");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(EnterCourseName.this);
        requestQueue.add(stringRequest);

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.event_back_RL:
                startActivity(new Intent(EnterCourseName.this, Drawer.class), translateDown);
                finish();
                break;
        }
    }

    public void startAnim() {
        findViewById(R.id.loadingIndicatorViewR).setVisibility(View.VISIBLE);
    }

    public void stopAnim() {
        findViewById(R.id.loadingIndicatorViewR).setVisibility(View.GONE);
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
            Snackbar snackbar = Snackbar.make(event_main_RL, message, Snackbar.LENGTH_LONG);
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
    public void onBackPressed() {
        startActivity(new Intent(EnterCourseName.this, Drawer.class), translateDown);
        finish();
    }

    private void showFailureErrorDialog(final String error_msg) {

        final Dialog dialog = new Dialog(EnterCourseName.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(EnterCourseName.this, ((LinearLayout) dialog.findViewById(R.id.dia_show_error_msg_LL)));

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

    @Override
    protected void onResume() {
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("appinbackground", false);
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
}
