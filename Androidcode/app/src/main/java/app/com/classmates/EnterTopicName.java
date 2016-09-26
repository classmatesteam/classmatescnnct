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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import app.com.classmates.multipleclasses.ConnectivityReceiver;
import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.GlobalConstants;

@SuppressLint("NewApi")
public class EnterTopicName extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    SharedPreferences mpref;
    RelativeLayout topic_back_RL;
    Button topic_name_next_TV;
    EditText topic_name_ET;
    String topicnameString;
    Global global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_name);
        mpref = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
        global = (Global) getApplicationContext();
        Fontchange.overrideFonts(EnterTopicName.this, ((RelativeLayout) findViewById(R.id.topic_name_main_RL)));
        topicinit();
        checkConnection();
    }

    private void topicinit() {
        topic_back_RL = (RelativeLayout) findViewById(R.id.topic_back_RL);
        topic_back_RL.setOnClickListener(this);

        topic_name_next_TV = (Button) findViewById(R.id.topic_name_next_TV);
        topic_name_next_TV.setOnClickListener(this);

        topic_name_next_TV.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                topic_name_next_TV.requestLayout();
                EnterTopicName.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                return false;
            }
        });

        topic_name_ET = (EditText) findViewById(R.id.topic_name_ET);
        topic_name_ET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                topicnameString = topic_name_ET.getText().toString();
                if (topicnameString.length() != 0) {
                    global.setProfname(topicnameString);
                    Bundle translateBundle =
                            ActivityOptions.
                                    makeCustomAnimation(EnterTopicName.this,
                                            R.anim.fade_in,
                                            R.anim.slide_out_left).toBundle();
                    startActivity(new Intent(EnterTopicName.this, EnterLocation.class), translateBundle);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter Topic Name..", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.topic_back_RL:

                Bundle translateBundle =
                        ActivityOptions.
                                makeCustomAnimation(EnterTopicName.this,
                                        R.anim.fade_in,
                                        R.anim.slide_out_right).toBundle();
                if (mpref.getInt("Class_list", 0) == 2) {
                    startActivity(new Intent(EnterTopicName.this, ClassdetailList.class), translateBundle);
                    finish();
                } else {
                    startActivity(new Intent(EnterTopicName.this, EnterCourseName.class), translateBundle);
                    finish();
                }
                break;

            case R.id.topic_name_next_TV:
                topicnameString = topic_name_ET.getText().toString();
                if (topicnameString.length() != 0) {
                    global.setProfname(topicnameString);
                    Bundle translateBundle1 =
                            ActivityOptions.
                                    makeCustomAnimation(EnterTopicName.this,
                                            R.anim.fade_in,
                                            R.anim.slide_out_left).toBundle();
                    startActivity(new Intent(EnterTopicName.this, EnterLocation.class), translateBundle1);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter Topic Name..", Toast.LENGTH_SHORT).show();
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
            Snackbar snackbar = Snackbar.make(((RelativeLayout) findViewById(R.id.topic_name_main_RL)), message, Snackbar.LENGTH_LONG);
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
                        makeCustomAnimation(EnterTopicName.this,
                                R.anim.fade_in,
                                R.anim.slide_out_right).toBundle();
        startActivity(new Intent(EnterTopicName.this, EnterCourseName.class), translateBundle);
        finish();
    }
}
