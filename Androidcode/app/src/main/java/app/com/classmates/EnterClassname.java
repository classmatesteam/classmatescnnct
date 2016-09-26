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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.com.classmates.multipleclasses.ConnectivityReceiver;
import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.GlobalConstants;

@SuppressLint("NewApi")
public class EnterClassname extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    Button enroll_next_TV;
    RelativeLayout enroll_main_RL;
    RelativeLayout classname_back_RL;
    Global global;
    EditText class_name_ET;
    String classname = "";
    SharedPreferences sp;
    Pattern pattern;
    Matcher matcher;
    String match = "[A-Z]{3}\\d{3}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_classname);
        global = (Global) getApplicationContext();
        sp = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);

        enroll_main_RL = (RelativeLayout) findViewById(R.id.enroll_main_RL);
        Fontchange.overrideFonts(EnterClassname.this, enroll_main_RL);
        checkConnection();
        init();
    }

    private void init() {
        class_name_ET = (EditText) findViewById(R.id.class_name_ET);

        enroll_next_TV = (Button) findViewById(R.id.enroll_next_TV);
        enroll_next_TV.setOnClickListener(this);

        classname_back_RL = (RelativeLayout) findViewById(R.id.classname_back_RL);
        classname_back_RL.setOnClickListener(this);

        class_name_ET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                classname = class_name_ET.getText().toString();
                pattern = Pattern.compile(match);
                boolean yes_match = validate(classname);

                if (classname.length() != 0) {
                    if (yes_match) {
                        String matchname = "";
                        if (global.getClassesnameList() != null) {
                            for (int j = 0; j < global.getClassesnameList().size(); j++) {
                                matchname = global.getClassesnameList().get(j).get("name");
                                if (classname.equalsIgnoreCase(matchname)) {
                                    classname = matchname;
                                    Log.e("Class match", classname);
                                    break;
                                }
                            }
                        }
                        if (classname.equalsIgnoreCase(matchname)) {
                            Toast.makeText(EnterClassname.this, "Class Name Already Exist. \nPlease Choose another Name", Toast.LENGTH_SHORT).show();
                        } else {
                            global.setClassname(classname);
                            Bundle translateBundle =
                                    ActivityOptions.
                                            makeCustomAnimation(EnterClassname.this,
                                                    R.anim.fade_in,
                                                    R.anim.slide_out_left).toBundle();
                            classname = "";
                            startActivity(new Intent(EnterClassname.this, EnterProffname.class), translateBundle);
                            finish();
                        }

                    } else {
                        Toast.makeText(EnterClassname.this, "Please Enter Valid Class Name!!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(EnterClassname.this, "Please Enter Class Name!!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }


    public boolean validate(final String classna) {
        matcher = pattern.matcher(classna);
        return matcher.matches();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.enroll_next_TV:
                classname = class_name_ET.getText().toString();
                pattern = Pattern.compile(match);
                boolean yes_match = validate(classname);

                if (classname.length() != 0) {
                    if (yes_match) {
                        String matchname = "";
                        if (global.getClassesnameList() != null) {
                            for (int j = 0; j < global.getClassesnameList().size(); j++) {
                                matchname = global.getClassesnameList().get(j).get("name");
                                if (classname.equalsIgnoreCase(matchname)) {
                                    classname = matchname;
                                    Log.e("Class match", classname);
                                    break;
                                }
                            }
                        }
                        if (classname.equalsIgnoreCase(matchname)) {
                            Toast.makeText(EnterClassname.this, "Class Name Already Exist. \nPlease Choose another Name", Toast.LENGTH_SHORT).show();
                        } else {
                            global.setClassname(classname);
                            Bundle translateBundle =
                                    ActivityOptions.
                                            makeCustomAnimation(EnterClassname.this,
                                                    R.anim.fade_in,
                                                    R.anim.slide_out_left).toBundle();
                            classname = "";
                            startActivity(new Intent(EnterClassname.this, EnterProffname.class), translateBundle);
                            finish();
                        }

                    } else {
                        Toast.makeText(EnterClassname.this, "Please Enter Valid Class Name!!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(EnterClassname.this, "Please Enter Class Name!!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.classname_back_RL:
                Bundle translateBundle =
                        ActivityOptions.
                                makeCustomAnimation(EnterClassname.this,
                                        R.anim.fade_in,
                                        R.anim.slide_out_right).toBundle();
                startActivity(new Intent(EnterClassname.this, LoginOptions.class), translateBundle);
                finish();

                break;
        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message = "";
        int color = 0;
        if (!isConnected) {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
            Snackbar snackbar = Snackbar.make(enroll_main_RL, message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    public void onBackPressed() {
        Bundle translateBundle =
                ActivityOptions.
                        makeCustomAnimation(EnterClassname.this,
                                R.anim.fade_in,
                                R.anim.slide_out_right).toBundle();
        startActivity(new Intent(EnterClassname.this, LoginOptions.class), translateBundle);
        finish();
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
