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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.com.classmates.multipleclasses.ConnectivityReceiver;
import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.GlobalConstants;

@SuppressLint("NewApi")
public class EnterProffname extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    Button profclass_done_TV;
    RelativeLayout profclass_main_RL, addanother_class_RL;
    RelativeLayout proff_back_RL;
    EditText profclass_name_ET;
    String profname = "";
    Global global;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_proffname);
        global = (Global) getApplicationContext();
        pref = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);

        profclass_main_RL = (RelativeLayout) findViewById(R.id.profclass_main_RL);
        Fontchange.overrideFonts(EnterProffname.this, profclass_main_RL);

        init();
        checkConnection();
    }

    private void init() {

        profclass_done_TV = (Button) findViewById(R.id.profclass_done_TV);
        profclass_done_TV.setOnClickListener(this);

        proff_back_RL = (RelativeLayout) findViewById(R.id.proff_back_RL);
        proff_back_RL.setOnClickListener(this);

        profclass_name_ET = (EditText) findViewById(R.id.profclass_name_ET);

        addanother_class_RL = (RelativeLayout) findViewById(R.id.addanother_class_RL);
        addanother_class_RL.setOnClickListener(this);

        profclass_name_ET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                profname = profclass_name_ET.getText().toString();
                if (profname.length() == 0) {
                    Toast.makeText(EnterProffname.this, "Please Enter Proffessor Name !!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("User idd", pref.getString(GlobalConstants.user_id, ""));
                    Log.e("Profsr name", profname);
                    pd = ProgressDialog.show(EnterProffname.this, "Please wait..", "Creating Class...");
                    AddclassesAPI(1);
                }
                return true;
            }
        });

    }

    ProgressDialog pd;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profclass_done_TV:
                profname = profclass_name_ET.getText().toString();
                if (profname.length() == 0) {
                    Toast.makeText(EnterProffname.this, "Please Enter Proffessor Name !!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("User idd", pref.getString(GlobalConstants.user_id, ""));
                    Log.e("Profsr name", profname);
                    pd = ProgressDialog.show(EnterProffname.this, "Please wait..", "Creating Class...");
                    AddclassesAPI(1);
                }
                break;

            case R.id.proff_back_RL:
                Bundle translateBundle =
                        ActivityOptions.
                                makeCustomAnimation(EnterProffname.this,
                                        R.anim.fade_in,
                                        R.anim.slide_out_right).toBundle();
                global.setClassname("");
                startActivity(new Intent(EnterProffname.this, EnterClassname.class), translateBundle);
                finish();
                break;

            case R.id.addanother_class_RL:
                profname = profclass_name_ET.getText().toString();
                if (profname.length() == 0) {
                    Toast.makeText(EnterProffname.this, "Please Enter Proffessor Name !!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("User idd", pref.getString(GlobalConstants.user_id, ""));
                    Log.e("Profsr name", profname);
                    pd = ProgressDialog.show(EnterProffname.this, "Please wait..", "Creating Class...");
                    AddclassesAPI(2);
                }
                break;
        }
    }

    private void AddclassesAPI(final int i) {

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

                                SharedPreferences.Editor ed = pref.edit();
                                ed.putBoolean(GlobalConstants.classes, false);
                                ed.commit();

                                Bundle translateBundle =
                                        ActivityOptions.
                                                makeCustomAnimation(EnterProffname.this,
                                                        R.anim.fade_in,
                                                        R.anim.slide_out_left).toBundle();

                                if (i == 1) {
                                    startActivity(new Intent(EnterProffname.this, Drawer.class), translateBundle);
                                    finish();
                                } else {
                                    startActivity(new Intent(EnterProffname.this, EnterClassname.class), translateBundle);
                                    finish();
                                }


                            } else {
                                Log.e("status Fail", status);

//                            Toast.makeText(EnterProffname.this,"Login Error",Toast.LENGTH_SHORT).show();
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
//                        Toast.makeText(EnterProffname.this,"Network Problem!!",Toast.LENGTH_SHORT).show();
                        showFailureErrorDialog("Failed to Connect with Server");
                        showSnack(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put("userid", pref.getString(GlobalConstants.user_id, ""));
                params.put("name", global.getClassname());
                params.put("pname", profname);
                params.put("Addclass", "");
                return params;

            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showFailureErrorDialog(final String error_msg) {

        final Dialog dialog = new Dialog(EnterProffname.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(EnterProffname.this, ((LinearLayout) dialog.findViewById(R.id.dia_show_error_msg_LL)));

        final TextView ok_error_msg_TV = (TextView) dialog.findViewById(R.id.ok_error_msg_TV);
        final TextView show_error_TV = (TextView) dialog.findViewById(R.id.show_error_TV);
        show_error_TV.setText(error_msg);

        ok_error_msg_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Bundle translateBundle =
                        ActivityOptions.
                                makeCustomAnimation(EnterProffname.this,
                                        R.anim.fade_in,
                                        R.anim.slide_out_right).toBundle();
                startActivity(new Intent(EnterProffname.this, Drawer.class), translateBundle);
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

            Snackbar snackbar = Snackbar.make(profclass_main_RL, message, Snackbar.LENGTH_LONG);
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

    @Override
    public void onBackPressed() {
        Bundle translateBundle =
                ActivityOptions.
                        makeCustomAnimation(EnterProffname.this,
                                R.anim.fade_in,
                                R.anim.slide_out_right).toBundle();
        startActivity(new Intent(EnterProffname.this, EnterClassname.class), translateBundle);
        finish();
    }
}
