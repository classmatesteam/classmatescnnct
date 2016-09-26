package app.com.classmates;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.com.classmates.ProfileSet.CircleTransform;
import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.GlobalConstants;

@SuppressWarnings("ResourceType")
@SuppressLint("NewApi")
public class Drawer extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawerRelativeLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    FrameLayout content_frame;
    Fragment mfrag;
    private float lastTranslate = 0.0f;
    Global global;
    ImageView drawer_pp_RIV;
    SharedPreferences mpref;
    Bundle translateRight;
    private ArrayList<HashMap<String, String>> invitelist;
    TextView red_invite_count_TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
        Fontchange.overrideFonts(Drawer.this, ((LinearLayout) findViewById(R.id.draweritem_main_LL)));
        mfrag = new Fragment();
        global = (Global) getApplicationContext();
        mpref = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerRelativeLayout = (RelativeLayout) findViewById(R.id.left_drawer);
        content_frame = (FrameLayout) findViewById(R.id.content_frame);

        initContentWithFirstFragment();
        invalidateOptionsMenu();
        initMenu();
        Invitecountlist();


        mDrawerToggle = new ActionBarDrawerToggle(Drawer.this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float moveFactor = (mDrawerRelativeLayout.getWidth() * slideOffset);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    content_frame.setTranslationX(moveFactor);
                } else {
                    TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    content_frame.startAnimation(anim);
                    lastTranslate = moveFactor;
                }
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void initMenu() {
        red_invite_count_TV = (TextView) findViewById(R.id.red_invite_count_TV);

        ((LinearLayout) findViewById(R.id.drawer_settings_LL)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.drawer_addclass_LL)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.drawer_invita_LL)).setOnClickListener(this);

        invitelist = new ArrayList<HashMap<String, String>>();
        drawer_pp_RIV = (ImageView) findViewById(R.id.drawer_pp_RIV);

        if (mpref.getString(GlobalConstants.spUserImage, "").contains("http"))
            Picasso.with(Drawer.this).load(mpref.getString(GlobalConstants.spUserImage, "")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).into(drawer_pp_RIV);
        else {
            Picasso.with(Drawer.this).load(GlobalConstants.ImageLink + mpref.getString(GlobalConstants.spUserImage, "")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).into(drawer_pp_RIV);
        }

        translateRight =
                ActivityOptions.
                        makeCustomAnimation(Drawer.this,
                                R.anim.fade_in,
                                R.anim.slide_out_right).toBundle();

    }

    private void initContentWithFirstFragment() {
        Fragment frame;
        frame = new Home();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, frame);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.drawer_settings_LL:
                changeback(2);
                startActivity(new Intent(Drawer.this, Settings.class), translateRight);
                finish();
                break;
            case R.id.drawer_addclass_LL:
                changeback(1);
                startActivity(new Intent(Drawer.this, LoginOptions.class), translateRight);
                global.setmVal("Drawer");
                finish();
                break;

            case R.id.drawer_invita_LL:
                changeback(3);
                startActivity(new Intent(Drawer.this, InviteCount.class), translateRight);
                finish();
                break;

        }
    }

    public void open() {
        mDrawerLayout.openDrawer(mDrawerRelativeLayout);
    }

    private void switchFragment(Fragment fragment) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        mDrawerLayout.closeDrawer(mDrawerRelativeLayout);
    }

    public void changeback(int colorchange) {
        if (colorchange == 1) {
            ((LinearLayout) findViewById(R.id.drawer_addclass_LL)).setBackgroundResource(R.drawable.selectoption);
            ((ImageView) findViewById(R.id.drawer_addclass_IV)).setImageResource(R.drawable.white_addclass);
            ((TextView) findViewById(R.id.drawer_addclass_TV)).setTextColor(Color.WHITE);

            ((LinearLayout) findViewById(R.id.drawer_settings_LL)).setBackgroundResource(Color.TRANSPARENT);
            ((ImageView) findViewById(R.id.drawer_settings_IV)).setImageResource(R.drawable.settings);
            ((TextView) findViewById(R.id.drawer_setting_TV)).setTextColor(Color.parseColor("#6d6d6d"));

            ((LinearLayout) findViewById(R.id.drawer_invita_LL)).setBackgroundResource(Color.TRANSPARENT);
            ((ImageView) findViewById(R.id.drawer_invita_IV)).setImageResource(R.drawable.invitation);
            ((TextView) findViewById(R.id.drawer_invita_TV)).setTextColor(Color.parseColor("#6d6d6d"));


        } else if (colorchange == 2) {
            ((LinearLayout) findViewById(R.id.drawer_settings_LL)).setBackgroundResource(R.drawable.selectoption);
            ((ImageView) findViewById(R.id.drawer_settings_IV)).setImageResource(R.drawable.white_settings);
            ((TextView) findViewById(R.id.drawer_setting_TV)).setTextColor(Color.WHITE);

            ((LinearLayout) findViewById(R.id.drawer_addclass_LL)).setBackgroundResource(Color.TRANSPARENT);
            ((ImageView) findViewById(R.id.drawer_addclass_IV)).setImageResource(R.drawable.addclass);
            ((TextView) findViewById(R.id.drawer_addclass_TV)).setTextColor(Color.parseColor("#6d6d6d"));

            ((LinearLayout) findViewById(R.id.drawer_invita_LL)).setBackgroundResource(Color.TRANSPARENT);
            ((ImageView) findViewById(R.id.drawer_invita_IV)).setImageResource(R.drawable.invitation);
            ((TextView) findViewById(R.id.drawer_invita_TV)).setTextColor(Color.parseColor("#6d6d6d"));


        } else if (colorchange == 3) {
            ((LinearLayout) findViewById(R.id.drawer_invita_LL)).setBackgroundResource(R.drawable.selectoption);
            ((ImageView) findViewById(R.id.drawer_invita_IV)).setImageResource(R.drawable.white_invite);
            ((TextView) findViewById(R.id.drawer_invita_TV)).setTextColor(Color.WHITE);

            ((LinearLayout) findViewById(R.id.drawer_addclass_LL)).setBackgroundResource(Color.TRANSPARENT);
            ((ImageView) findViewById(R.id.drawer_addclass_IV)).setImageResource(R.drawable.addclass);
            ((TextView) findViewById(R.id.drawer_addclass_TV)).setTextColor(Color.parseColor("#6d6d6d"));

            ((LinearLayout) findViewById(R.id.drawer_settings_LL)).setBackgroundResource(Color.TRANSPARENT);
            ((ImageView) findViewById(R.id.drawer_settings_IV)).setImageResource(R.drawable.settings);
            ((TextView) findViewById(R.id.drawer_setting_TV)).setTextColor(Color.parseColor("#6d6d6d"));
        }

    }

    private void Invitecountlist() {
        Log.e("Drawer user id check", mpref.getString(GlobalConstants.user_id, ""));
        if (invitelist != null) {
            invitelist.clear();
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
                                    hmap.put("name", obj.getString("name"));
                                    hmap.put("createduser", obj.getString("createduser"));
                                    hmap.put("topicname", obj.getString("topicname"));
                                    hmap.put("userid", obj.getString("userid"));
                                    hmap.put("inviteid", obj.getString("inviteid"));
                                    invitelist.add(hmap);
                                }

                                if (invitelist.size() == 0) {
                                    red_invite_count_TV.setVisibility(View.GONE);
                                } else {
                                    int size = invitelist.size();
                                    Log.e("Sizee", size + "");
                                    red_invite_count_TV.setVisibility(View.VISIBLE);
                                    red_invite_count_TV.setText(size + "");
                                }

                            } else {

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
                        Toast.makeText(Drawer.this, "Server Problem!!", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", mpref.getString(GlobalConstants.user_id, ""));
                params.put("invitecount", "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }


    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(Drawer.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exit_app);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(Drawer.this, ((LinearLayout) dialog.findViewById(R.id.dia_exit_main_LL)));

        final TextView reset_classname_B = (TextView) dialog.findViewById(R.id.diaExit_class_TV);
        final TextView cancel_classname_B = (TextView) dialog.findViewById(R.id.dia_Exit_cancel_class_TV);

        reset_classname_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
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

    @Override
    protected void onResume() {
        SharedPreferences.Editor ed = mpref.edit();
        ed.putBoolean("appinbackground", false);
        ed.commit();
        Log.e("in Activity", "in resume");
        super.onResume();
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
