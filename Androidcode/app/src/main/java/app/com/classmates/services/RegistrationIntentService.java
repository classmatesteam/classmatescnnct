package app.com.classmates.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

/**
 * Created by pia on 23/07/16.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "GGCCCMM RegIntentService";
    public static String TOKEN = "";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            TOKEN = instanceID.getToken("770058111757", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.e(TAG, "Registration Token:" + TOKEN);

            sharedPreferences.edit().putBoolean("GGGGCCCMMM server", true).apply();


        } catch (Exception e) {

        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
    }

}