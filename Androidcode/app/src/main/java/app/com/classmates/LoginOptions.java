package app.com.classmates;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import app.com.classmates.ProfileSet.CircleTransform;
import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.ConnectivityReceiver;
import app.com.classmates.multipleclasses.GlobalConstants;

@SuppressLint("NewApi")
@SuppressWarnings("WrongConstant")
public class LoginOptions extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    ImageView profile_pic_IV;
    TextView username_TV;
    Global global;
    SharedPreferences spref;
    private Bundle translateRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_options);

        Fontchange.overrideFonts(LoginOptions.this, ((RelativeLayout) findViewById(R.id.loginoption_Main_RL)));
        checkConnection();

        global = (Global) getApplicationContext();
        spref = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
        translateRight =
                ActivityOptions.
                        makeCustomAnimation(LoginOptions.this,
                                R.anim.fade_in,
                                R.anim.slide_out_left).toBundle();
        optionsfetchids();
    }

    private void optionsfetchids() {

        profile_pic_IV = (ImageView) findViewById(R.id.profile_pic_IV);
        username_TV = (TextView) findViewById(R.id.username_TV);
        username_TV.setText("Hello " + spref.getString(GlobalConstants.spUserName, "") + " !");

        if (spref.getString(GlobalConstants.spUserImage, "").contains("http"))
            Picasso.with(LoginOptions.this).load(spref.getString(GlobalConstants.spUserImage, "")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).into(profile_pic_IV);
        else
            Picasso.with(LoginOptions.this).load(GlobalConstants.ImageLink + spref.getString(GlobalConstants.spUserImage, "")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).error(R.drawable.cm_logo).into(profile_pic_IV);

        ((ImageView) findViewById(R.id.login_manually_IV)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.login_uni_IV)).setOnClickListener(this);

        if (global.getmVal().equalsIgnoreCase("Drawer")) {
            ((ImageView) findViewById(R.id.login_uni_IV)).setBackgroundResource(R.drawable.through);
            ((ImageView) findViewById(R.id.login_option_back_IV)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.login_option_back_IV)).setOnClickListener(this);

        } else if (global.getmVal().equalsIgnoreCase("Settings")) {
            ((ImageView) findViewById(R.id.login_uni_IV)).setBackgroundResource(R.drawable.through);
            ((ImageView) findViewById(R.id.login_option_back_IV)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.login_option_back_IV)).setOnClickListener(this);

        } else {
            ((ImageView) findViewById(R.id.login_uni_IV)).setBackgroundResource(R.drawable.enter_uni);
            ((ImageView) findViewById(R.id.login_option_back_IV)).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_manually_IV:
                startActivity(new Intent(LoginOptions.this, EnterClassname.class), translateRight);
                finish();
                break;
            case R.id.login_uni_IV:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().setTransition(R.anim.slide_in_left);
                ft.replace(R.id.your_placeholder, new LoginviaUni());
                ft.commit();
                break;

            case R.id.login_option_back_IV:
                Bundle translateBundle =
                        ActivityOptions.
                                makeCustomAnimation(LoginOptions.this,
                                        R.anim.fade_in,
                                        R.anim.slide_out_right).toBundle();

                if (global.getmVal().equalsIgnoreCase("Drawer")) {
                    startActivity(new Intent(LoginOptions.this, Drawer.class), translateBundle);
                    finish();
                } else if (global.getmVal().equalsIgnoreCase("Settings")) {
                    startActivity(new Intent(LoginOptions.this, Settings.class), translateBundle);
                    finish();
                } else {

                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Bundle translateBundle =
                ActivityOptions.
                        makeCustomAnimation(LoginOptions.this,
                                R.anim.fade_in,
                                R.anim.slide_out_right).toBundle();

        if (global.getmVal().equalsIgnoreCase("Drawer")) {
            startActivity(new Intent(LoginOptions.this, Drawer.class), translateBundle);
            finish();
        } else if (global.getmVal().equalsIgnoreCase("Settings")) {
            startActivity(new Intent(LoginOptions.this, Settings.class), translateBundle);
            finish();
        } else {

        }
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
            Snackbar snackbar = Snackbar.make(((RelativeLayout) findViewById(R.id.loginoption_Main_RL)), message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        SharedPreferences.Editor ed = spref.edit();
        ed.putBoolean("appinbackground", false);
        ed.commit();
        Log.e("in Activity", "in resume");
        super.onResume();
        Global.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        SharedPreferences.Editor ed = spref.edit();
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

}
