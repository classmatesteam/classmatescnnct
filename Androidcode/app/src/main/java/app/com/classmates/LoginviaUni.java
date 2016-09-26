package app.com.classmates;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.com.classmates.fonts.Fontchange;
import app.com.classmates.multipleclasses.GlobalConstants;


@SuppressLint("SetJavaScriptEnabled")
public class LoginviaUni extends Fragment implements View.OnClickListener {
    View rootView;
    WebView uni_WV;
    RelativeLayout uni_back_RL;
    RelativeLayout loginvia_main_RL;
    ProgressDialog mPdia;
    String mURL = "https://webapp4.asu.edu/myasu/student/schedule?term=2167";
    ArrayList<String> classname;
    ArrayList<String> profname;
    ArrayList<String> subname;
    SharedPreferences mpref;
    ArrayList<Elements> arr_ele = new ArrayList<Elements>();
    ArrayList<Elements> arr_prof = new ArrayList<Elements>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.loginvia_uni, container, false);
        mpref = getActivity().getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);

        mPdia = ProgressDialog.show(getActivity(), "Loading", "Please Wait..");

        loginvia_main_RL = (RelativeLayout) rootView.findViewById(R.id.loginvia_main_RL);
        Fontchange.overrideFonts(getActivity(), loginvia_main_RL);

        uni_back_RL = (RelativeLayout) rootView.findViewById(R.id.uni_back_RL);
        uni_back_RL.setOnClickListener(this);

        uni_WV = (WebView) rootView.findViewById(R.id.uni_WV);
        uni_WV.getSettings().setLoadsImagesAutomatically(true);
        uni_WV.getSettings().setJavaScriptEnabled(true);
        uni_WV.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        uni_WV.loadUrl(mURL);

        uni_WV.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mPdia.show();

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mPdia.dismiss();
                Log.e("URL", url);
                if (mURL.equalsIgnoreCase(url)) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        uni_WV.evaluateJavascript(
                                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                                new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String html) {
                                        String newcode = StringEscapeUtils.unescapeJava(html);
                                        classname = new ArrayList<String>();
                                        profname = new ArrayList<String>();
                                        subname = new ArrayList<String>();
                                        Log.e("HTML", newcode);
                                        Document doc = Jsoup.parse(newcode, "UTF-8");

                                        Elements elemsclassname=null;
                                        Elements elemsprof =null;

                                        for (int i = 3;i< 15;i++) {
                                            elemsclassname = doc.select("#asu_content_container > div > div.column-container > div > div.content-column > div.schedule > div.box > table > tbody:nth-child("+ i +") > tr > td:nth-child(2) > a");
                                            Log.e("!!!1",""+elemsclassname);
                                            arr_ele.add(elemsclassname);
                                            if (!elemsclassname.hasText()){
                                                break;
                                            }

                                            elemsprof = doc.select("#asu_content_container > div > div.column-container > div > div.content-column > div.schedule > div.box > table > tbody:nth-child("+ i +") > tr > td:nth-child(5) > a");
                                            Log.e("!!!Proff :",""+elemsprof);
                                            arr_prof.add(elemsprof);
                                            if (!elemsprof.hasText()){
                                                break;
                                            }
                                        }

                                        Log.e("elemsclassname", elemsclassname.size() + "~~"+ elemsclassname);

                                        for (int i = 0; i < arr_ele.size(); i++) {
                                            String s1 = isAlpha(arr_ele.get(i).text().replaceAll("\\s+", ""));
                                            if (!s1.equalsIgnoreCase("")) {
                                                Log.e("~~~~cc~~~", s1);
                                                classname.add(s1);
                                            }
                                        }

                                        Log.e("elemsclassname", elemsprof.size() + "~~"+ elemsprof);
                                        for (int i = 0; i < arr_prof.size(); i++) {
                                            String s1 = isAlpha(arr_prof.get(i).text().replaceAll("\\s+", ""));
                                            if (!s1.equalsIgnoreCase("")) {
                                                Log.e("~~~~pp~~~", s1);
                                                profname.add(s1);
                                            }
                                        }
                                        Log.e("classname", "----->" + classname);
                                        Log.e("Prof name", "----->" + profname);

                                        String c = classname.toString().replace("[", "").replace("]", "").trim();
                                        String p = profname.toString().replace("[", "").replace("]", "").trim();
                                        Log.e("classname", "----->" + c);
                                        Log.e("Prof name", "----->" + p);
                                        Log.e("user id", "----->" + mpref.getString(GlobalConstants.user_id, ""));
                                        if (c == null && p == null) {
//                                            Toast.makeText(getActivity(), "Try Again!!", 2000).show();
                                            Log.e("!!!!!!!!!!!!","NOOOOOOOOO");
                                        } else {
                                            postClassData(c, p);
                                        }

                                  /*  Elements elemssubjectname = doc.select("#asu_content_container > div > table > tbody > tr > td.content-column > div.schedule > div.box > table > tbody > tr > td:nth-child(3) ");

                                    if (elemssubjectname.size()!=0) {
                                        for (int i=0;i<elemssubjectname.size();i++) {
                                            String s1= isAlpha(elemssubjectname.get(i).text().trim());
                                            subname.add(s1);
                                            Log.e("3333333333", "" + subname + "----->" + s1);
                                        }
                                    }*/
                                    }
                                }
                        );

                    }

                } else {

                }
            }
        });

        return rootView;
    }

    public static String isAlpha(String s) {
        return s.replaceAll("[^a-zA-Z0-9\\s]", "");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.uni_back_RL:
                startActivity(new Intent(getActivity(), LoginOptions.class));
                getActivity().finish();
                break;
        }
    }
    @SuppressLint("NewApi")
    private void postClassData(final String c, final String p) {

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
                                 Bundle translateRight =
                                        ActivityOptions.
                                                makeCustomAnimation(getActivity(),
                                                        R.anim.fade_in,
                                                        R.anim.slide_out_left).toBundle();
                                startActivity(new Intent(getActivity(), Drawer.class), translateRight);
                                getActivity().finish();
                            } else {
                                Toast.makeText(getActivity(), "Try Again", Toast.LENGTH_SHORT).show();
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
                params.put("userid", mpref.getString(GlobalConstants.user_id, ""));
                params.put("name", c);
                params.put("pname", p);
                params.put("unilogin", "");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


}
