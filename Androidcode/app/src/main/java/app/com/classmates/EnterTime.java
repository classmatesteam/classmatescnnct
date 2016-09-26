package app.com.classmates;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import app.com.classmates.multipleclasses.ConnectivityReceiver;
import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.GlobalConstants;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class EnterTime extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    int pYear;
    int pMonth;
    int pDay;
    static final int TIME_DIALOG_ID = 1111;
    static final int DATE_DIALOG_ID = 0;
    TimePickerDialog td;
    EditText startime_name_ET;
    private int min, hour, sec;
    int dateint = 0;
    Global global;
    SharedPreferences pref;
    String time;
    ProgressDialog pd;
    Calendar cal;
    DatePickerDialog dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_time);
        global = (Global) getApplicationContext();
        pref = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
        Fontchange.overrideFonts(EnterTime.this, ((RelativeLayout) findViewById(R.id.startime_main_RL)));

        cal = Calendar.getInstance();
        pYear = cal.get(Calendar.YEAR);
        pMonth = cal.get(Calendar.MONTH);
        pDay = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);
        sec = cal.get(Calendar.SECOND);


        fetchtimeids();
        checkConnection();
    }

    private void fetchtimeids() {
        startime_name_ET = (EditText) findViewById(R.id.startime_name_ET);
        startime_name_ET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (startime_name_ET.getText().toString().length() != 0) {

//                    time = startime_name_ET.getText().toString();
                    Log.e("Timee Sending", time);

//                    Log.e("COursse Naemee", global.getCoursename());
//                    Log.e("tname Naemee", global.getProfname());
//                    Log.e("location Naemee" , global.getClassname());
//                    Log.e("time Naemee", startime_name_ET.getText().toString() + " * "+time);

                    pd = ProgressDialog.show(EnterTime.this, "Please Wait..", "Creating Your Studies Event...");
                    AddEventsAPI();
                } else {
                    Toast.makeText(EnterTime.this, "Select Start time Please!!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        startime_name_ET.setOnClickListener(this);

        ((RelativeLayout) findViewById(R.id.startime_back_RL)).setOnClickListener(this);
        ((Button) findViewById(R.id.starttime_next_TV)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startime_name_ET:
                showDialog(DATE_DIALOG_ID);
                break;

            case R.id.startime_back_RL:
                Bundle translateBundle = ActivityOptions.makeCustomAnimation(EnterTime.this,
                        R.anim.fade_in,
                        R.anim.slide_out_right).toBundle();
                startActivity(new Intent(EnterTime.this, EnterLocation.class), translateBundle);
                finish();
                break;

            case R.id.starttime_next_TV:
                if (startime_name_ET.getText().toString().length() != 0) {
                    pd = ProgressDialog.show(EnterTime.this, "Please Wait..", "Creating Your Studies Event...");
                    AddEventsAPI();
                } else {
                    Toast.makeText(EnterTime.this, "Select Start time Please!!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            hour = hourOfDay;
            min = minutes;

            String end = String.valueOf(new StringBuilder()
                    .append(pMonth + 1)
                    .append("/").append(pDay).append("/").append(pYear)
            );

            updateTime(hour, min, sec, end, serversendDate);
        }

    };


//    {Addevent=, tname=Hcfjdfj, name=CSE444, time_zone=Asia/Calcutta, time=2016/9/16  3:00:00 PM, location=N nxxhxh, userid=44}

    /*private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {

            String end = String.valueOf(new StringBuilder()
                    .append(pMonth + 1)
                    .append("/").append(pDay).append("/").append(pYear)
            );

            Calendar c = Calendar.getInstance();

            Log.e("timeee current", c.get(Calendar.HOUR_OF_DAY) + "---" + c.get(Calendar.MINUTE));
            Log.e("timeee", hour + "---" +min);
            Log.e("timeee selected", hourOfDay + "---" +minutes);

            hour = hourOfDay;
            min = minutes;
            System.out.println("Hourss " + hour + "Mintues " + "Date " + end);
            updateTime(hour, min, sec, end, serversendDate);

*//*
            if (hourOfDay == 0){
                hourOfDay = 12;
            }

            if (hourOfDay >= c.get(Calendar.HOUR_OF_DAY) && minutes >= c.get(Calendar.MINUTE)) {
                hour = hourOfDay;
                min = minutes;
                System.out.println("Hourss " + hour + "Mintues " + "Date " + end);
                updateTime(hour, min, sec, end, serversendDate);
            } else {
                startime_name_ET.setText("");
                view.setCurrentHour(hour);
                view.setCurrentHour(min);
//                invalidDateTime("Please select valid date and time");
                Toast.makeText(getApplicationContext(), "INVALID TIME", Toast.LENGTH_SHORT).show();
            }*//*
        }
    };
*/
    String timeZone;

    private void updateTime(int hours, int mins, int sec, String date, String serversendDate) {
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes = "";

        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(':').append("00").append(" ").append(timeSet).toString();

        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        Log.d("Time zone", "=" + tz.getDisplayName());
        timeZone = tz.getID();

        time = serversendDate + "  " + aTime;
        Log.e("Server send time", time);

        System.out.println("~~~~~~~~~~~~~~~~~~" + timeZone);
        startime_name_ET.setText(date + " " + aTime);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                Calendar calll;
                DatePickerDialog da = new DatePickerDialog(this, pDateSetListener, pYear, pMonth, pDay);
                calll = Calendar.getInstance();
                calll.add(Calendar.DATE, 0);
                Date newDate = cal.getTime();
                calll.set(pYear, pMonth, pDay);
                da.getDatePicker().setMinDate(newDate.getTime());

                return da;

            case TIME_DIALOG_ID:
                // set time picker as current time
                TimePickerDialog td = new TimePickerDialog(this, timePickerListener, hour, min, false);
                return td;
        }
        return null;
    }

    String serversendDate = "";
    DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            pYear = year;
            pMonth = monthOfYear;
            pDay = dayOfMonth;
            //  Calendar cal = Calendar.getInstance();
            // dp.getDatePicker().setMinDate(cal.getTimeInMillis());

            serversendDate = String.valueOf(new StringBuilder()
                    .append(pYear).append("/")
                    .append(pMonth + 1)
                    .append("/").append(pDay));

            showDialog(TIME_DIALOG_ID);

        }
    };


    private void AddEventsAPI() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstants.mURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        Log.e("Response_____>>", response);

                        JSONObject job = null;
                        try {
                            job = new JSONObject(response);
                            String status = job.getString("status");
                            String msgg = job.getString("message");
                            Log.e("Status", status);
                            Log.e("message", msgg);
                            if (status.equalsIgnoreCase("success")) {
                                Bundle translateBundle =
                                        ActivityOptions.
                                                makeCustomAnimation(EnterTime.this,
                                                        R.anim.fade_in,
                                                        R.anim.slide_out_right).toBundle();
                                startActivity(new Intent(EnterTime.this, Drawer.class), translateBundle);
                                finish();

                            } else {
                                Log.e("status Fail", status);

//                                Toast.makeText(EnterTime.this,msgg,Toast.LENGTH_SHORT).show();
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
//                        Toast.makeText(EnterTime.this,error.toString(),Toast.LENGTH_SHORT).show();
                        showFailureErrorDialog("Failed to Connect with Server");
                        showSnack(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("userid", pref.getString(GlobalConstants.user_id, ""));
                params.put("name", global.getCoursename());//Course Name
                params.put("tname", global.getProfname());//Topic NAme
                params.put("location", global.getClassname());
                params.put("time", time);
                params.put("time_zone", timeZone);
                params.put("Addevent", "");
                Log.e("Create Class parameters", params + "");

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showFailureErrorDialog(final String error_msg) {

        final Dialog dialog = new Dialog(EnterTime.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Fontchange.overrideFonts(EnterTime.this, ((LinearLayout) dialog.findViewById(R.id.dia_show_error_msg_LL)));

        final TextView ok_error_msg_TV = (TextView) dialog.findViewById(R.id.ok_error_msg_TV);
        final TextView show_error_TV = (TextView) dialog.findViewById(R.id.show_error_TV);
        show_error_TV.setText(error_msg);

        ok_error_msg_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Bundle translateBundle =
                        ActivityOptions.
                                makeCustomAnimation(EnterTime.this,
                                        R.anim.fade_in,
                                        R.anim.slide_out_right).toBundle();
                startActivity(new Intent(EnterTime.this, Drawer.class), translateBundle);
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
            Snackbar snackbar = Snackbar.make(((RelativeLayout) findViewById(R.id.startime_main_RL)), message, Snackbar.LENGTH_LONG);
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
                        makeCustomAnimation(EnterTime.this,
                                R.anim.fade_in,
                                R.anim.slide_out_right).toBundle();
        startActivity(new Intent(EnterTime.this, EnterLocation.class), translateBundle);
        finish();
    }
}
