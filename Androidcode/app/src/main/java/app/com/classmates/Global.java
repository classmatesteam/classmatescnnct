package app.com.classmates;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.util.ArrayList;
import java.util.HashMap;

import app.com.classmates.multipleclasses.ConnectivityReceiver;

/**
 * Created by user on 28/6/16.
 */

/**
 * https://pwn5191.cloudant.com/acralyzer/_design/acralyzer/index.html
 * http://robusttechhouse.com/how-to-setup-cloudant-backend-for-acra-application-crash-reports-for-android/
 */

@ReportsCrashes(
        formUri = "https://pwn5191.cloudant.com/acra-global/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "tioneschenceltlyfingstro",
        formUriBasicAuthPassword = "28987f5759438f8a7fffdf5390641c42e5f1f3b9",
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE,
                ReportField.DISPLAY,
                ReportField.TOTAL_MEM_SIZE,
                ReportField.AVAILABLE_MEM_SIZE
        },
        mode = ReportingInteractionMode.SILENT
)

@SuppressLint("NewApi")
public class Global extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    private static Global mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }

    public static synchronized Global getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    String mVal;
    String classname;
    String profname;
    String coursename;
    String classid;
    int position;
    ArrayList<HashMap<String, String>> recommendedlist;
    ArrayList<HashMap<String, String>> classesnameList;
    ArrayList<HashMap<String, String>> groupmsgList;

    public ArrayList<HashMap<String, String>> getGroupmsgList() {
        return groupmsgList;
    }

    public void setGroupmsgList(ArrayList<HashMap<String, String>> groupmsgList) {
        this.groupmsgList = groupmsgList;
    }

    public ArrayList<HashMap<String, String>> getClassesnameList() {
        return classesnameList;
    }

    public void setClassesnameList(ArrayList<HashMap<String, String>> classesnameList) {
        this.classesnameList = classesnameList;
    }

    ArrayList<HashMap<String, String>> ChatList;

    public ArrayList<HashMap<String, String>> getChatList() {
        return ChatList;
    }

    public void setChatList(ArrayList<HashMap<String, String>> chatList) {
        ChatList = chatList;
    }

    public ArrayList<HashMap<String, String>> getRecommendedlist() {
        return recommendedlist;
    }

    public void setRecommendedlist(ArrayList<HashMap<String, String>> recommendedlist) {
        this.recommendedlist = recommendedlist;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    //    String fbuserImage;

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getProfname() {
        return profname;
    }

    public void setProfname(String profname) {
        this.profname = profname;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getmVal() {
        return mVal;
    }

    public void setmVal(String mVal) {
        this.mVal = mVal;
    }

}
