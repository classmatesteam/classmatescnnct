package app.com.classmates;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import app.com.classmates.multipleclasses.ConnectivityReceiver;
import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.GlobalConstants;

@SuppressLint("NewApi")
public class EnterLocation extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    RelativeLayout enterloc_back_RL;
    Button enterloc_name_next_TV;
    Global global;
    EditText Enterlocation_name_ET;
    String location;
    SharedPreferences mpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_location);
        global = (Global) getApplicationContext();
        mpref = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);

        Fontchange.overrideFonts(EnterLocation.this, ((RelativeLayout) findViewById(R.id.enterloc_main_RL)));

        locationfetchids();
        checkConnection();
    }

    private void locationfetchids() {

        enterloc_back_RL = (RelativeLayout) findViewById(R.id.enterloc_back_RL);
        enterloc_back_RL.setOnClickListener(this);

        enterloc_name_next_TV = (Button) findViewById(R.id.enterloc_name_next_TV);
        enterloc_name_next_TV.setOnClickListener(this);

        Enterlocation_name_ET = (EditText) findViewById(R.id.Enterlocation_name_ET);
        Enterlocation_name_ET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                location = Enterlocation_name_ET.getText().toString();
                Bundle translateBundle1 =
                        ActivityOptions.
                                makeCustomAnimation(EnterLocation.this,
                                        R.anim.fade_in,
                                        R.anim.slide_out_left).toBundle();
                if (location.length() == 0) {
                    Toast.makeText(EnterLocation.this, "Please Enter Location Name !!", Toast.LENGTH_SHORT).show();
                } else {
                    global.setClassname(location);
                    startActivity(new Intent(EnterLocation.this, EnterTime.class), translateBundle1);
                    finish();
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.enterloc_back_RL:
                Bundle translateBundle =
                        ActivityOptions.
                                makeCustomAnimation(EnterLocation.this,
                                        R.anim.fade_in,
                                        R.anim.slide_out_right).toBundle();
                startActivity(new Intent(EnterLocation.this, EnterTopicName.class), translateBundle);
                finish();
                break;

            case R.id.enterloc_name_next_TV:
                location = Enterlocation_name_ET.getText().toString();
                Bundle translateBundle1 =
                        ActivityOptions.
                                makeCustomAnimation(EnterLocation.this,
                                        R.anim.fade_in,
                                        R.anim.slide_out_left).toBundle();
                if (location.length() == 0) {
                    Toast.makeText(EnterLocation.this, "Please Enter Location Name !!", Toast.LENGTH_SHORT).show();
                } else {
                    global.setClassname(location);
                    startActivity(new Intent(EnterLocation.this, EnterTime.class), translateBundle1);
                    finish();
                }

                break;
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
            Snackbar snackbar = Snackbar.make(((RelativeLayout) findViewById(R.id.enterloc_main_RL)), message, Snackbar.LENGTH_LONG);
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
        Bundle translateBundle =
                ActivityOptions.
                        makeCustomAnimation(EnterLocation.this,
                                R.anim.fade_in,
                                R.anim.slide_out_right).toBundle();
        startActivity(new Intent(EnterLocation.this, EnterTopicName.class), translateBundle);
        finish();
    }
}
