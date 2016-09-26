package app.com.classmates;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ogaclejapan.arclayout.ArcLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.com.classmates.ProfileSet.CircleTransform;
import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.GlobalConstants;

@SuppressLint("NewApi")
public class Home extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    View rootView;
    ImageView home_menu_IV;
    ImageView home_pp_RIV;
    RelativeLayout home_main_RL;
    LinearLayout newevent_home_LL, myevents_home_LL, message_home_LL, hottopics_home_LL;
    Button class1_B, class2_B, class3_B, class4_B, class5_B, class6_B, class7_B, class8_B, class9_B, class10_B;

    SharedPreferences sp;
    ArrayList<HashMap<String, String>> classlist;
    ArrayList<String> classids;
    ArrayList<String> classnames;
    Global global;
    Bundle translateRight;
    Bundle translateLeft, translateUp;
    ArcLayout arc_layout;
    FrameLayout menu_layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home, container, false);
        sp = getActivity().getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
        global = (Global) getActivity().getApplicationContext();

        home_main_RL = (RelativeLayout) rootView.findViewById(R.id.home_main_RL);
        Fontchange.overrideFonts(getActivity(), home_main_RL);

        fetchbyiDs(rootView);

        return rootView;
    }

    private void fetchbyiDs(View rootView) {

        arc_layout = (ArcLayout) rootView.findViewById(R.id.arc_layout);
        menu_layout = (FrameLayout) rootView.findViewById(R.id.menu_layout);

        home_menu_IV = (ImageView) rootView.findViewById(R.id.home_menu_IV);
        home_pp_RIV = (ImageView) rootView.findViewById(R.id.home_pp_RIV);

        Log.e("Image Size --->", sp.getString(GlobalConstants.spUserImage, "").length() + "");
        if (sp.getString(GlobalConstants.spUserImage, "").contains("http"))
            Picasso.with(getActivity()).load(sp.getString(GlobalConstants.spUserImage, "")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).into(home_pp_RIV);
        else
            Picasso.with(getActivity()).load(GlobalConstants.ImageLink + sp.getString(GlobalConstants.spUserImage, "")).transform(new CircleTransform()).placeholder(R.drawable.dummy_single).into(home_pp_RIV);


        newevent_home_LL = (LinearLayout) rootView.findViewById(R.id.newevent_home_LL);
        myevents_home_LL = (LinearLayout) rootView.findViewById(R.id.myevents_home_LL);
        message_home_LL = (LinearLayout) rootView.findViewById(R.id.message_home_LL);
        hottopics_home_LL = (LinearLayout) rootView.findViewById(R.id.hottopics_home_LL);

        class1_B = (Button) rootView.findViewById(R.id.class1_B);
        class1_B.setOnClickListener(this);
        class1_B.setLongClickable(true);
        class1_B.setOnLongClickListener(this);

        class2_B = (Button) rootView.findViewById(R.id.class2_B);
        class2_B.setOnClickListener(this);
        class2_B.setOnLongClickListener(this);

        class3_B = (Button) rootView.findViewById(R.id.class3_B);
        class3_B.setOnClickListener(this);
        class3_B.setOnLongClickListener(this);

        class4_B = (Button) rootView.findViewById(R.id.class4_B);
        class4_B.setOnClickListener(this);
        class4_B.setOnLongClickListener(this);

        class5_B = (Button) rootView.findViewById(R.id.class5_B);
        class5_B.setOnClickListener(this);
        class5_B.setOnLongClickListener(this);

        class6_B = (Button) rootView.findViewById(R.id.class6_B);
        class6_B.setOnClickListener(this);
        class6_B.setOnLongClickListener(this);

        class7_B = (Button) rootView.findViewById(R.id.class7_B);
        class7_B.setOnClickListener(this);
        class7_B.setOnLongClickListener(this);

        class8_B = (Button) rootView.findViewById(R.id.class8_B);
        class8_B.setOnClickListener(this);
        class8_B.setOnLongClickListener(this);

        class9_B = (Button) rootView.findViewById(R.id.class9_B);
        class9_B.setOnClickListener(this);
        class9_B.setOnLongClickListener(this);

        class10_B = (Button) rootView.findViewById(R.id.class10_B);
        class10_B.setOnClickListener(this);
        class10_B.setOnLongClickListener(this);

        newevent_home_LL.setOnClickListener(this);
        myevents_home_LL.setOnClickListener(this);
        message_home_LL.setOnClickListener(this);
        hottopics_home_LL.setOnClickListener(this);
        home_menu_IV.setOnClickListener(this);
        home_pp_RIV.setOnClickListener(this);

        classlist = new ArrayList<HashMap<String, String>>();
        classids = new ArrayList<>();
        classnames = new ArrayList<>();

        translateRight =
                ActivityOptions.
                        makeCustomAnimation(getActivity(),
                                R.anim.fade_in,
                                R.anim.slide_out_right).toBundle();

        translateLeft =
                ActivityOptions.
                        makeCustomAnimation(getActivity(),
                                R.anim.fade_in,
                                R.anim.slide_out_left).toBundle();

        translateUp =
                ActivityOptions.
                        makeCustomAnimation(getActivity(),
                                R.anim.fade_in,
                                R.anim.slide_up).toBundle();

        /*if (sp.getBoolean(GlobalConstants.classes,false)){

        }else {*/
        classesDetail(2);
//        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.class1_B:
                Log.e("Classs Iddd clickedd", classnames.get(0));
                global.setClassid(classnames.get(0));
                startActivity(new Intent(getActivity(), ClassdetailList.class), translateLeft);
                getActivity().finish();
                break;

            case R.id.class2_B:
                Log.e("Classs Iddd clickedd", classnames.get(1));
                global.setClassid(classnames.get(1));
                startActivity(new Intent(getActivity(), ClassdetailList.class), translateLeft);
                getActivity().finish();
                break;

            case R.id.class3_B:
                Log.e("Classs Iddd clickedd", classnames.get(2));
                global.setClassid(classnames.get(2));
                startActivity(new Intent(getActivity(), ClassdetailList.class), translateLeft);
                getActivity().finish();
                break;

            case R.id.class4_B:
                Log.e("Classs Iddd clickedd", classnames.get(3));
                global.setClassid(classnames.get(3));
                startActivity(new Intent(getActivity(), ClassdetailList.class), translateLeft);
                getActivity().finish();
                break;

            case R.id.class5_B:
                Log.e("Classs Iddd clickedd", classnames.get(4));
                global.setClassid(classnames.get(4));
                startActivity(new Intent(getActivity(), ClassdetailList.class), translateLeft);
                getActivity().finish();
                break;

            case R.id.class6_B:
                Log.e("Classs Iddd clickedd", classnames.get(5));
                global.setClassid(classnames.get(5));
                startActivity(new Intent(getActivity(), ClassdetailList.class), translateLeft);
                getActivity().finish();
                break;

            case R.id.class7_B:
                Log.e("Classs Iddd clickedd", classnames.get(6));
                global.setClassid(classnames.get(6));
                startActivity(new Intent(getActivity(), ClassdetailList.class), translateLeft);
                getActivity().finish();
                break;

            case R.id.class8_B:
                Log.e("Classs Iddd clickedd", classnames.get(7));
                global.setClassid(classnames.get(7));
                startActivity(new Intent(getActivity(), ClassdetailList.class), translateLeft);
                getActivity().finish();
                break;

            case R.id.class9_B:
                Log.e("Classs Iddd clickedd", classnames.get(8));
                global.setClassid(classnames.get(8));
                startActivity(new Intent(getActivity(), ClassdetailList.class), translateLeft);
                getActivity().finish();
                break;

            case R.id.class10_B:
                Log.e("Classs Iddd clickedd", classnames.get(9));
                global.setClassid(classids.get(9));
                startActivity(new Intent(getActivity(), ClassdetailList.class), translateLeft);
                getActivity().finish();
                break;

            case R.id.home_menu_IV:
                ((Drawer) getActivity()).open();
                break;

            case R.id.newevent_home_LL:
                startActivity(new Intent(getActivity(), EnterCourseName.class), translateUp);
                getActivity().finish();
                break;

            case R.id.myevents_home_LL:
                startActivity(new Intent(getActivity(), MyStudy.class), translateUp);
                getActivity().finish();
                break;

            case R.id.message_home_LL:
                startActivity(new Intent(getActivity(), GroupMessage.class), translateUp);
                getActivity().finish();
                break;

            case R.id.hottopics_home_LL:
                startActivity(new Intent(getActivity(), HotTopics.class), translateUp);
                getActivity().finish();
                break;

            case R.id.home_pp_RIV:
                startActivity(new Intent(getActivity(), Settings.class));
                getActivity().finish();

//                showMenu();

                break;
        }

    }

    private void classesDetail(final int i1) {

        if (classlist != null) {
            classlist.clear();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstants.mURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("REsponseeeee Home::::", response);
                        JSONObject job = null;
                        try {
                            job = new JSONObject(response);
                            String status = job.getString("status");

                            if (i1 == 1) {
                                pdia.dismiss();
                            }
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
                                if (classlist.size() != 0) {
                                    for (int i = 0; i < classlist.size(); i++) {

                                        String cname = classlist.get(i).get("name");
                                        cname = cname.replaceAll("\\s+", "");
                                        classnames.add(cname);

                                        String classid = classlist.get(i).get("classid");
                                        classids.add(classid);

                                        if (classnames.size() != 0) {

                                            StringBuffer sb = new StringBuffer(cname.trim());

                                            if (sb != null)
                                                sb.insert(3, System.getProperty("line.separator"));

                                            switch (i) {
                                                case 0:
                                                    class1_B.setVisibility(View.VISIBLE);
                                                    class1_B.setText(sb);

                                                    break;
                                                case 1:

                                                    class2_B.setVisibility(View.VISIBLE);
                                                    class2_B.setText(sb);
                                                    break;
                                                case 2:

                                                    class3_B.setVisibility(View.VISIBLE);
                                                    class3_B.setText(sb);
                                                    break;
                                                case 3:
                                                    class4_B.setVisibility(View.VISIBLE);
                                                    class4_B.setText(sb);
                                                    break;

                                                case 4:
                                                    class5_B.setVisibility(View.VISIBLE);
                                                    class5_B.setText(sb);
                                                    break;

                                                case 5:
                                                    class6_B.setVisibility(View.VISIBLE);
                                                    class6_B.setText(sb);
                                                    break;

                                                case 6:

                                                    class7_B.setVisibility(View.VISIBLE);
                                                    class7_B.setText(sb);
                                                    break;

                                                case 7:

                                                    class8_B.setVisibility(View.VISIBLE);
                                                    class8_B.setText(sb);
                                                    break;

                                                case 8:
                                                    class9_B.setVisibility(View.VISIBLE);
                                                    class9_B.setText(sb);
                                                    break;

                                                case 9:
                                                    class10_B.setVisibility(View.VISIBLE);
                                                    class10_B.setText(sb);
                                                    break;
                                            }
                                        }
                                    }
                                    SharedPreferences.Editor ed = sp.edit();
                                    ed.putBoolean(GlobalConstants.classes, true);
                                    ed.commit();
                                }


                            } else {
//                                showFailureErrorDialog("Sorry! No Class Found, \n Create New Class");
//                            Toast.makeText(getActivity(), "Sorry! No Class Found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getActivity(), "Network Problem !!", Toast.LENGTH_SHORT).show();
                        showFailureErrorDialog("Server Problem.");
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

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.class1_B:
                Log.e("LLLONNGGG CLICK ", classids.get(0));
                editClassNameDialog(classids.get(0), classnames.get(0));
                break;

            case R.id.class2_B:
                Log.e("LLLONNGGG CLICK ", classids.get(1));
                editClassNameDialog(classids.get(1), classnames.get(1));
                break;

            case R.id.class3_B:
                Log.e("LLLONNGGG CLICK ", classids.get(2));
                editClassNameDialog(classids.get(2), classnames.get(2));
                break;

            case R.id.class4_B:
                Log.e("LLLONNGGG CLICK ", classids.get(3));
                editClassNameDialog(classids.get(3), classnames.get(3));
                break;

            case R.id.class5_B:
                Log.e("LLLONNGGG CLICK ", classids.get(4));
                editClassNameDialog(classids.get(4), classnames.get(4));
                break;

            case R.id.class6_B:
                Log.e("LLLONNGGG CLICK ", classids.get(5));
                editClassNameDialog(classids.get(5), classnames.get(5));
                break;

            case R.id.class7_B:
                Log.e("LLLONNGGG CLICK ", classids.get(6));
                editClassNameDialog(classids.get(6), classnames.get(6));
                break;

            case R.id.class8_B:
                Log.e("LLLONNGGG CLICK ", classids.get(7));
                editClassNameDialog(classids.get(7), classnames.get(7));
                break;

            case R.id.class9_B:
                Log.e("LLLONNGGG CLICK ", classids.get(8));
                editClassNameDialog(classids.get(8), classnames.get(8));
                break;

            case R.id.class10_B:
                Log.e("LLLONNGGG CLICK ", classids.get(9));
                editClassNameDialog(classids.get(9), classnames.get(9));
                break;
        }

        return true;
    }

    ProgressDialog pdia;
    String changedname;
    Pattern pattern;
    Matcher matcher;
    String match = "[A-Z]{3}\\d{3}";

    public void editClassNameDialog(final String changed, final String name) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_classname);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(getActivity(), ((LinearLayout) dialog.findViewById(R.id.custom_edit_classname_main_LL)));

        final EditText edit_classname_ET = (EditText) dialog.findViewById(R.id.edit_classname_ET);
        edit_classname_ET.setText(name);

        final Button reset_classname_B = (Button) dialog.findViewById(R.id.reset_classname_B);
        final Button cancel_classname_B = (Button) dialog.findViewById(R.id.cancel_classname_B);

//        changedname= edit_classname_ET.getText().toString();
//        pattern = Pattern.compile(match);
//        final boolean yes_match = validate(changedname);

        reset_classname_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changedname = edit_classname_ET.getText().toString();
                pattern = Pattern.compile(match);
                boolean yes_match = validate(changedname);

                if (changedname.length() != 0) {
                    if (yes_match) {
                        String matchname = "";
                        if (global.getClassesnameList() != null) {
                            for (int j = 0; j < global.getClassesnameList().size(); j++) {
                                matchname = global.getClassesnameList().get(j).get("name");
                                if (changedname.equalsIgnoreCase(matchname)) {
                                    changedname = matchname;
                                    Log.e("Edited Class match", changedname);
                                    break;
                                }
                            }
                        }
                        if (changedname.equalsIgnoreCase(matchname)) {
                            Toast.makeText(getActivity(), "Class Name Already Exist. \nPlease Choose another Name", Toast.LENGTH_SHORT).show();
                        } else {
                            EditClassnameAPI(changedname, changed);
                            dialog.dismiss();
                        }

                    } else {
                        Toast.makeText(getActivity(), "Please Enter Valid Class Name!!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "Please Enter Class Name!!", Toast.LENGTH_SHORT).show();
                }


                /*if (changedname.length()!=0) {
                    if (yes_match) {
                        String matchname = "";
                        for (int j = 0; j < global.getClassesnameList().size(); j++) {
                            matchname = global.getClassesnameList().get(j).get("name");
                            if (changedname.equalsIgnoreCase(matchname)) {
                                changedname = matchname;
                                Log.e("Class match", changedname);
                                break;
                            }
                        }

                        if (changedname.equalsIgnoreCase(matchname)) {
                            Toast.makeText(getActivity(), "Class Name Already Exist. \nPlease Choose another Name", Toast.LENGTH_SHORT).show();
                        } else {
                            EditClassnameAPI(changedname,changed);
                            dialog.dismiss();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please Enter Valid Class Name!!", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getActivity(), "Please Enter Class Name!!", Toast.LENGTH_SHORT).show();
                }

*/

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

    public boolean validate(final String classna) {
        matcher = pattern.matcher(classna);
        return matcher.matches();
    }


    private void EditClassnameAPI(final String msg, final String classid) {
        Log.e("Classss IDDD", classid);

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
                                pdia = ProgressDialog.show(getActivity(), "Please Wait", "Changing Class Name..");
                                classesDetail(1);
                            } else {
                                showFailureErrorDialog(job.getString("message"));
//                                Toast.makeText(getActivity(),job.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Network Problem!!", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("classid", classid);
                params.put("name", msg);
                params.put("Editclass", "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    Dialog dialog;

    private void showFailureErrorDialog(final String error_msg) {

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Fontchange.overrideFonts(getActivity(), ((LinearLayout) dialog.findViewById(R.id.dia_show_error_msg_LL)));

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

}
