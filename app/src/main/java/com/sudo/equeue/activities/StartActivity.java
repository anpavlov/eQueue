package com.sudo.equeue.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.models.IsTokenOkModel;
import com.sudo.equeue.models.QueueList;
import com.sudo.equeue.models.User;
import com.sudo.equeue.push.RegistrationIntentService;
import com.sudo.equeue.utils.AlertDialogHelper;
import com.sudo.equeue.utils.CustomSnackBar;
import com.sudo.equeue.utils.QueueApplication;

import java.util.Map;

public class StartActivity extends NetBaseActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private int createUserRequestId = -1;
    private int getMyQueuesRequestId = -1;
    private int checkTokenRequestId = -1;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        prefs = getSharedPreferences(QueueApplication.APP_PREFS, Context.MODE_PRIVATE);

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        String token = prefs.getString(QueueApplication.PREFS_USER_TOKEN_KEY, null);
        if (token == null || token.equals("")) {
            createUserRequestId = getServiceHelper().createUser(null, null, null, false);
        } else {
            checkTokenRequestId = getServiceHelper().checkToken();
//            getMyQueuesRequestId = getServiceHelper().meInQueues();
        }
    }

    private void startApp(QueueList queueList) {
        if (queueList == null) {
            AlertDialogHelper.show(this, "Ошибка: qlist is null"); // TODO: remove
            Log.e(null, "Error: qlist is null");
        } else {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_QUEUE_LIST, queueList);
            startActivity(intent);
            finish();
        }
    }

    private void handleTokenCheck(IsTokenOkModel isOk) {
        if (isOk.isValid()) {
            getMyQueuesRequestId = getServiceHelper().meInQueues();
        } else {
            createUserRequestId = getServiceHelper().createUser(null, null, null, false);
        }
    }

    private void initUserPref(User user) {
        if (user != null && user.getToken() != null && !user.getToken().equals("")) {
            SharedPreferences prefs = getSharedPreferences(QueueApplication.APP_PREFS, Context.MODE_PRIVATE);
            prefs.edit()
                    .putString(QueueApplication.PREFS_USER_TOKEN_KEY, user.getToken())
                    .putInt(QueueApplication.PREFS_USER_ID_KEY, user.getUid())
                    .commit();
            getMyQueuesRequestId = getServiceHelper().meInQueues();
        } else {
            AlertDialogHelper.show(this, "Ошибка в запросе");
            Log.e(null, "Got error: user==0 -> " + Boolean.toString(user == null));
            if (user != null) {
                Log.e(null, "Got error: token='" + user.getToken() + "'");
            }
        }
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == createUserRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_USER, obj -> initUserPref((User) obj), null);
        } else if (requestId == getMyQueuesRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_QUEUE_LIST, obj -> startApp((QueueList) obj), null);
        } else if (requestId == checkTokenRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_IS_TOKEN_OK, obj -> handleTokenCheck((IsTokenOkModel) obj), null);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(null, "This device is not supported.");
            }
            return false;
        }
        return true;
    }
}
