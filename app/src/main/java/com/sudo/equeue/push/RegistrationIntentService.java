package com.sudo.equeue.push;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.utils.QueueApplication;
import com.sudo.equeue.utils.ServiceCallbackListener;

public class RegistrationIntentService extends IntentService implements ServiceCallbackListener {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private int updateGcmRequestId = -1;
    private SharedPreferences prefs;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        prefs = getSharedPreferences(QueueApplication.APP_PREFS, Context.MODE_PRIVATE);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
//            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
//            prefs.edit().putBoolean(QueueApplication.PREFS_SENT_TOKEN, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            prefs.edit().putBoolean(QueueApplication.PREFS_SENT_TOKEN, false).apply();
//            Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.

    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        updateGcmRequestId = ((QueueApplication) getApplicationContext()).getServiceHelper().updateGcm(token);
    }

    // [START subscribe_topics]
//    private void subscribeTopics(String token) throws IOException {
//        GcmPubSub pubSub = GcmPubSub.getInstance(this);
//        for (String topic : TOPICS) {
//            pubSub.subscribe(token, "/topics/" + topic, null);
//        }
//    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (updateGcmRequestId == requestId) {
            if (resultCode == NetService.CODE_OK) {
                if (data.getInt(NetService.RETURN_CODE) == NetService.CODE_OK) {
                    prefs.edit().putBoolean(QueueApplication.PREFS_SENT_TOKEN, true).apply();
                } else {
                    prefs.edit().putBoolean(QueueApplication.PREFS_SENT_TOKEN, false).apply();
                }
            } else {
                prefs.edit().putBoolean(QueueApplication.PREFS_SENT_TOKEN, false).apply();
            }
//            Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        }
    }
    // [END subscribe_topics]

}