package app.com.classmates;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.ConnectivityReceiver;
import app.com.classmates.multipleclasses.GlobalConstants;
import app.com.classmates.services.RegistrationIntentService;


@SuppressWarnings("deprecation")
public class LoginwithFB extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    LoginButton Fb_login_button;
    ImageView fb_IV;
    String id, name, url, token, fname, lname;
    CallbackManager callbackManager;
    SharedPreferences sp;
    SharedPreferences.Editor e;

    Global global;
    boolean connected = false;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        sp = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
        boolean login = sp.getBoolean(GlobalConstants.login_app, false);


        if (login) {
            startActivity(new Intent(LoginwithFB.this, Drawer.class));
            finish();
        }

        setContentView(R.layout.loginwith_fb);
        callbackManager = CallbackManager.Factory.create();

        String key = printKeyHash(LoginwithFB.this);
        Log.e("Hash Key Genrated", key);

        Fontchange.overrideFonts(LoginwithFB.this, ((RelativeLayout) findViewById(R.id.loginwithfb_main_RL)));
        checkConnection();


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            Log.e("connected ---->>", connected + "");

            Fb_login_button = (LoginButton) findViewById(R.id.Fb_login_button);
            Fb_login_button.setReadPermissions(Arrays.asList("public_profile,email,user_birthday,user_friends,read_custom_friendlists"));

            fb_IV = (ImageView) findViewById(R.id.fb_IV);
            fb_IV.setOnClickListener(this);

            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    pd = ProgressDialog.show(LoginwithFB.this, "", "Loading...");
                    token = loginResult.getAccessToken().getToken();
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.e("RESSSSPONSEEEE", response.toString());

                            try {
                                //                            Log.e("token", token);
                                name = object.getString("name");
                                id = object.getString("id");
                                fname = object.getString("first_name");
                                lname = object.getString("last_name");
                                url = object.getJSONObject("picture").getJSONObject("data").getString("url");

                                Log.e("Name", name);
                                Log.e("id", id);
                                Log.e("first_name", fname);
                                Log.e("lname", lname);
                                Log.e("url", url);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            registerUser();
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "picture.type(large),first_name,last_name,bio,id,name,link,gender,email, birthday");
                    request.setParameters(parameters);
                    request.executeAsync();

                }

                @Override
                public void onCancel() {
                    // App code
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                }
            });

        } else {

            connected = false;
            Toast.makeText(LoginwithFB.this, "No Internet Connection!!", Toast.LENGTH_SHORT).show();
            Log.e("connected ---->>", connected + "");
        }

        System.out.println(" RegistrationIntentService.TOKEN check  " + RegistrationIntentService.TOKEN + "");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fb_IV:
                Fb_login_button.performClick();
                break;
        }
    }

    private void registerUser() {


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

                                String msgg = job.getString("msg");
                                Log.e("message", msgg);

                                JSONObject json = new JSONObject(msgg);
                                String userid = json.getString("userid");
                                String userpic = json.getString("userpic");
                                Log.e("Usere id fbb", userid);

                                String fname = json.getString("fname");
                                String lname = json.getString("lname");

                                Log.e("MMMMYYY NAmeeeee", fname + " " + lname);

                                e = sp.edit();
                                e.putString(GlobalConstants.spUserName, fname + " " + lname);
                                e.putString(GlobalConstants.spUserImage, userpic);
                                e.putString(GlobalConstants.user_id, userid);
                                e.putBoolean(GlobalConstants.login_app, true);
                                e.putString(GlobalConstants.fb_token, token);
                                e.putString(GlobalConstants.count, json.getString("count"));
                                e.commit();

                                pd.dismiss();
                                if (json.getString("count").equalsIgnoreCase("0")) {
                                    startActivity(new Intent(LoginwithFB.this, LoginOptions.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(LoginwithFB.this, Drawer.class));
                                    finish();
                                }

                            } else {
                                pd.dismiss();
                                LoginManager.getInstance().logOut();
//                                showFailureErrorDialog("Login Error! Please try again.");
//                                Toast.makeText(LoginwithFB.this,"Login Error",Toast.LENGTH_SHORT).show();
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
//                        Toast.makeText(LoginwithFB.this,"Network Problem !!",Toast.LENGTH_SHORT).show();
                        showSnack(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("Fname", fname);
                params.put("Lname", lname);
                params.put("Image", url);
                params.put("access_token", id);
                params.put("DEVICE_ID", RegistrationIntentService.TOKEN);
                params.put("DEVICE_TYPE", "A");
                params.put("Fb_login", "");
                Log.e("Login Parameters", params + "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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

            Snackbar snackbar = Snackbar.make(((RelativeLayout) findViewById(R.id.loginwithfb_main_RL)), message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        Global.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    private void showFailureErrorDialog(final String error_msg) {

        final Dialog dialog = new Dialog(LoginwithFB.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(LoginwithFB.this, ((LinearLayout) dialog.findViewById(R.id.dia_show_error_msg_LL)));

        final TextView ok_error_msg_TV = (TextView) dialog.findViewById(R.id.ok_error_msg_TV);
        final TextView show_error_TV = (TextView) dialog.findViewById(R.id.show_error_TV);
        show_error_TV.setText(error_msg);

        ok_error_msg_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                System.exit(0);
            }
        });

        dialog.show();
    }

}
