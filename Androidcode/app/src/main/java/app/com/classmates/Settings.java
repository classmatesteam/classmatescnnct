package app.com.classmates;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.com.classmates.ProfileSet.CircleTransform;
import app.com.classmates.ProfileSet.Constants;
import app.com.classmates.ProfileSet.ImageCropActivity;
import app.com.classmates.ProfileSet.ImageViewActivity;
import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.ConnectivityReceiver;
import app.com.classmates.multipleclasses.GlobalConstants;
import app.com.classmates.multipleclasses.HorizontalListView;
import app.com.classmates.multipleclasses.VolleyMultipartRequest;
import app.com.classmates.multipleclasses.VolleySingleton;

@SuppressLint("NewApi")
public class Settings extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private static int MAX_IMAGE_DIMENSION = 90;
    ImageView setting_back_IV, nextlist_IV;
    HorizontalListView removeclass_HLV;
    RelativeLayout setting_addclass_RL, setting_logout_RL, userprofilepic_RL, editdate_RL, remove_sett_RL, invite_frnd_RL;
    ImageView setting_pp_RIV;
    Global global;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    EditText settings_username_TV;
    TextView removeclass_TV;
    private ArrayList<HashMap<String, String>> classlist;
    ProgressDialog pd;
    String imagePath = "";
    Bundle translateLeft;
    String editedname = "";
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    byte[] inputData;
    boolean Camerapermission, Storagepermission;
    private static final int REQUEST_WRITE_STORAGE = 112, Cameraget = 110;
    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;
    private String ss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.settings);

        Fontchange.overrideFonts(Settings.this, ((RelativeLayout) findViewById(R.id.setting_main_RL)));
        checkConnection();

        global = (Global) getApplicationContext();
        sp = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);

        String StoragePermission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        String CameraPermission = android.Manifest.permission.CAMERA;

        if (android.os.Build.VERSION.SDK_INT < 23) {

        } else {

            int hasStoragePermission = Settings.this.checkSelfPermission(StoragePermission);
            int hasCameraPermission = Settings.this.checkSelfPermission(CameraPermission);
            List<String> permissions = new ArrayList<String>();
            if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(StoragePermission);
            }
            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(CameraPermission);
            }
            if (!permissions.isEmpty()) {
                String[] params = permissions.toArray(new String[permissions.size()]);
                requestPermissions(params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            } else {
                // We already have permission, so handle as normal
            }
        }

        init();

        Intent kk = getIntent();
        ss = kk.getStringExtra(Constants.IntentExtras.IMAGE_PATH);
        System.out.println("IMMMMMMMMMMM >> " + ss);
        if (ss != null && !ss.isEmpty()) {
            Picasso.with(Settings.this).load(new File(ss)).transform(new CircleTransform()).into(setting_pp_RIV);

            imagePath = ss;

            Log.e("~~~~~~~~ImagePath", imagePath);
            try {
                InputStream inputStream = new FileInputStream(imagePath);
                inputData = getBytes(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("~~~~~~~~inputData~~>", inputData + "");

            ((TextView) findViewById(R.id.saveprofile_TV)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.saveprofile_TV)).setOnClickListener(this);
        }

    }


    private void init() {

        removeclass_HLV = (HorizontalListView) findViewById(R.id.removeclass_HLV);

        nextlist_IV = (ImageView) findViewById(R.id.nextlist_IV);
        setting_back_IV = (ImageView) findViewById(R.id.setting_back_IV);
        setting_back_IV.setOnClickListener(this);

        setting_addclass_RL = (RelativeLayout) findViewById(R.id.setting_addclass_RL);
        setting_addclass_RL.setOnClickListener(this);

        invite_frnd_RL = (RelativeLayout) findViewById(R.id.invite_frnd_RL);
        invite_frnd_RL.setOnClickListener(this);

        remove_sett_RL = (RelativeLayout) findViewById(R.id.remove_sett_RL);
        userprofilepic_RL = (RelativeLayout) findViewById(R.id.userprofilepic_RL);
        setting_logout_RL = (RelativeLayout) findViewById(R.id.setting_logout_RL);
        removeclass_TV = (TextView) findViewById(R.id.removeclass_TV);

        userprofilepic_RL.setOnClickListener(this);
        setting_logout_RL.setOnClickListener(this);

        remove_sett_RL.setOnClickListener(this);
        nextlist_IV.setOnClickListener(this);

        setting_pp_RIV = (ImageView) findViewById(R.id.setting_pp_RIV);


        if (sp.getString(GlobalConstants.spUserImage, "").contains("http")) {
            Log.e("Image come hhtp", sp.getString(GlobalConstants.spUserImage, ""));
            Picasso.with(Settings.this).load(sp.getString(GlobalConstants.spUserImage, "")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).into(setting_pp_RIV);
        } else {
            Log.e("Image come simply", GlobalConstants.ImageLink + sp.getString(GlobalConstants.spUserImage, ""));
            Picasso.with(Settings.this).load(GlobalConstants.ImageLink + sp.getString(GlobalConstants.spUserImage, "")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).error(R.drawable.cm_logo).into(setting_pp_RIV);
        }

        editdate_RL = (RelativeLayout) findViewById(R.id.editdate_RL);
        editdate_RL.setOnClickListener(this);

        settings_username_TV = (EditText) findViewById(R.id.settings_username_TV);
        settings_username_TV.setText(sp.getString(GlobalConstants.spUserName, ""));
        settings_username_TV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(textView.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
                editedname = settings_username_TV.getText().toString();
                pd = ProgressDialog.show(Settings.this, "Please Wait", "Loading...");
                EditProfilewithImageAPI(editedname);
                return true;
            }
        });

        translateLeft =
                ActivityOptions.
                        makeCustomAnimation(Settings.this,
                                R.anim.fade_in,
                                R.anim.slide_out_left).toBundle();

        classesDetailAPI();

        removeclass_HLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String id = classlist.get(i).get("classid");
                Log.e("REmove Classs Method", id);
                Log.e("REmove Classs Userid", sp.getString(GlobalConstants.user_id, ""));
                removeClassDialog(id);
            }
        });

    }

    private void removeClassDialog(final String id) {
        final Dialog removeDia = new Dialog(Settings.this);
        removeDia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        removeDia.setContentView(R.layout.dialog_remove_class);
        removeDia.setCanceledOnTouchOutside(false);
        removeDia.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(Settings.this, ((LinearLayout) removeDia.findViewById(R.id.dia_remove_class_main_LL)));

        TextView dia_cancel_class_TV = (TextView) removeDia.findViewById(R.id.dia_cancel_class_TV);
        TextView dia_remove_class_TV = (TextView) removeDia.findViewById(R.id.dia_remove_class_TV);

        dia_remove_class_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeDia.dismiss();
                pd = ProgressDialog.show(Settings.this, "Wait..", "Removing Class...");
                removeClass(id, sp.getString(GlobalConstants.user_id, ""));
            }
        });

        dia_cancel_class_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeDia.dismiss();
            }
        });
        removeDia.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_back_IV:
                startActivity(new Intent(Settings.this, Drawer.class), translateLeft);
                finish();
                break;

            case R.id.setting_addclass_RL:
                startActivity(new Intent(Settings.this, LoginOptions.class), translateLeft);
                global.setmVal("Settings");
                finish();
                break;

            case R.id.userprofilepic_RL:
                checkPermissions();
                break;

            case R.id.nextlist_IV:
                removeclass_TV.setVisibility(View.VISIBLE);
                break;

            case R.id.invite_frnd_RL:
                shareapp();
                break;

            case R.id.remove_sett_RL:
                removeclass_TV.setVisibility(View.GONE);
                break;

            case R.id.saveprofile_TV:
                pd = ProgressDialog.show(Settings.this, "Please Wait", "Loading...");

                editedname = settings_username_TV.getText().toString();
                Log.e("Print Name ::::>>", editedname);
                EditProfilewithImageAPI(editedname);
                break;

            case R.id.setting_logout_RL:
                ed = sp.edit();
                ed.putBoolean(GlobalConstants.login_app, false);
                ed.putString(GlobalConstants.fb_token, "");
                ed.commit();
                ed.clear();

                LoginManager.getInstance().logOut();

                startActivity(new Intent(Settings.this, LoginwithFB.class), translateLeft);
                finish();
                break;
        }
    }

    private void classesDetailAPI() {

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
                                removeclass_HLV.setVisibility(View.VISIBLE);
                                removeclass_HLV.setAdapter(new Removeadapter(Settings.this, classlist));

                            } else {
//                            Toast.makeText(Settings.this, "Sorry! No Class Found", Toast.LENGTH_SHORT).show();
                                removeclass_HLV.setVisibility(View.INVISIBLE);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            removeclass_HLV.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response ERROR_____>>", error.toString());
                        removeclass_HLV.setVisibility(View.INVISIBLE);
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

        RequestQueue requestQueue = Volley.newRequestQueue(Settings.this);
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

    public void shareapp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: https://play.google.com/store/apps/details?id=com.google.android.apps.plus");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void removeClass(final String id, final String userid) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstants.mURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        classlist = new ArrayList<HashMap<String, String>>();
                        Log.e("Response_____>>", response);
                        pd.dismiss();
                        JSONObject job = null;
                        try {
                            job = new JSONObject(response);
                            String status = job.getString("status");
                            Log.e("Status", status);
                            if (status.equalsIgnoreCase("success")) {
                                Toast.makeText(Settings.this, "Class Successfully Remove.", Toast.LENGTH_SHORT).show();
                                removeclass_TV.setVisibility(View.VISIBLE);
                                classesDetailAPI();
                            } else {
                                Toast.makeText(Settings.this, job.getString("message"), Toast.LENGTH_SHORT).show();
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
//                        Toast.makeText(getApplicationContext(),"Network Problem !!",Toast.LENGTH_SHORT).show();
                        showSnack(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", userid);
                params.put("classid", id);
                params.put("removeclass", "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Settings.this);
        requestQueue.add(stringRequest);
    }

    class Removeadapter extends BaseAdapter {

        LayoutInflater mInflater;
        ArrayList<HashMap<String, String>> list;
        Context context;
        SharedPreferences pref;


        public Removeadapter(Context context, ArrayList<HashMap<String, String>> list) {
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
                view = mInflater.inflate(R.layout.custom_remove_hlv, null);

                Fontchange.overrideFonts(context, ((RelativeLayout) view.findViewById(R.id.custom_remove_hlv_main_RL)));
                holder.remove_class_TV = (TextView) view.findViewById(R.id.remove_class_TV);

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

    private void checkPermissions() {
        Camerapermission = (ContextCompat.checkSelfPermission(Settings.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        Storagepermission = (ContextCompat.checkSelfPermission(Settings.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!Camerapermission) {
            ActivityCompat.requestPermissions(Settings.this, new String[]{android.Manifest.permission.CAMERA}, Cameraget);
        }
        if (!Storagepermission) {
            ActivityCompat.requestPermissions(Settings.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        } else {
            Intent intent = new Intent(Settings.this, ImageViewActivity.class);
            startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC);
            //  startActivityForResult(getPickImageChooserIntent(), 200);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        Log.e("REQUEST_CODE_UPDATE_PIC", REQUEST_CODE_UPDATE_PIC + "");

        if (requestCode == REQUEST_CODE_UPDATE_PIC) {

            Log.e("RESULT_OK", RESULT_OK + "");

            if (resultCode == RESULT_OK) {
                imagePath = result.getStringExtra(Constants.IntentExtras.IMAGE_PATH);
                Log.e("~~~~~~~~ImagePath", imagePath);
                showCroppedImage(imagePath);


            } else if (resultCode == RESULT_CANCELED) {

            } else {
                String errorMsg = result.getStringExtra(ImageCropActivity.ERROR_MSG);
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }
    private void showCroppedImage(String mImagePath) {
        Log.e("~~~~~~~~ImagePath", mImagePath);
        if (mImagePath != null) {
            Picasso.with(Settings.this).load(new File(mImagePath)).transform(new CircleTransform()).into(setting_pp_RIV);
//            setting_pp_RIV.setImageBitmap(myBitmap);
        }
    }


    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }



    public void EditProfilewithImageAPI(String editedname) {

        String[] name = sp.getString(GlobalConstants.spUserName, "").split(" ");
        final String fname = name[0];
        Log.e("Print Name length", name.length - 1 + "");
        final String lname = name[name.length - 1];

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, GlobalConstants.mURL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.e("Edit profile response", resultResponse);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");
                    pd.dismiss();
                    if (status.equals("success")) {

                        JSONObject json = new JSONObject(message);
                        String userpic = json.getString("userpic");

                        String fname = json.getString("fname");
                        String lname = json.getString("lname");

                        imagePath = "";
                        SharedPreferences.Editor e;
                        e = sp.edit();
                        e.putString(GlobalConstants.spUserName, fname + " " + lname);
                        e.putString(GlobalConstants.spUserImage, userpic);
                        e.commit();


                        Log.i("Messsage", message);
                    } else {

                        Log.i("Unexpected", message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                showFailureErrorDialog("Failed to Connect with Server");
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", sp.getString(GlobalConstants.user_id, ""));
                params.put("profileedit", "");
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
//                params.put("image", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mAvatarImage.getDrawable()), "image/jpeg"));

                params.put("image", new VolleyMultipartRequest.DataPart(imagePath, inputData, "image/jpeg"));

                return params;
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(Settings.this, Drawer.class), translateLeft);
        finish();
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
            Snackbar snackbar = Snackbar.make(((RelativeLayout) findViewById(R.id.setting_main_RL)), message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                } else {
//                    Toast.makeText(Settings.this, "Some Permission are Denied", Toast.LENGTH_SHORT).show();
                    Log.e("Permissiona", "Some Permission are Denied");
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showFailureErrorDialog(final String error_msg) {

        final Dialog dialog = new Dialog(Settings.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(Settings.this, ((LinearLayout) dialog.findViewById(R.id.dia_show_error_msg_LL)));

        final TextView ok_error_msg_TV = (TextView) dialog.findViewById(R.id.ok_error_msg_TV);
        final TextView show_error_TV = (TextView) dialog.findViewById(R.id.show_error_TV);
        show_error_TV.setText(error_msg);

        ok_error_msg_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(Settings.this, Drawer.class), translateLeft);
                finish();
            }
        });

        dialog.show();
    }

}
