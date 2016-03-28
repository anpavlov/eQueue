package com.sudo.equeueadmin.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.sudo.equeueadmin.NetBaseActivity;
import com.sudo.equeueadmin.NetService;
import com.sudo.equeueadmin.R;
import com.sudo.equeueadmin.models.IsTokenOkModel;
import com.sudo.equeueadmin.models.User;
import com.sudo.equeueadmin.push.RegistrationIntentService;
import com.sudo.equeueadmin.utils.QueueApplication;

public class StartActivity extends NetBaseActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private int createUserRequestId = -1;
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
            startLogin();
        } else {
            checkTokenRequestId = getServiceHelper().checkToken();
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

    private void startApp() {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startLogin() {
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void isValidToken(IsTokenOkModel isValid) {
        if (isValid.isValid()) {
            startApp();
        } else {
            startLogin();
        }
    }

//    TODO: вынести инициализацию юзера в Application
    private void initUserPref(User user) {
        if (user != null && user.getToken() != null && !user.getToken().equals("")) {
            prefs = getSharedPreferences(QueueApplication.APP_PREFS, Context.MODE_PRIVATE);
            prefs.edit()
                    .putString(QueueApplication.PREFS_USER_TOKEN_KEY, user.getToken())
                    .putInt(QueueApplication.PREFS_USER_ID_KEY, user.getUid())
                    .commit();
            startApp();
        } else {
            Toast.makeText(this, "Error in request", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == checkTokenRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> isValidToken((IsTokenOkModel) obj), NetService.RETURN_IS_TOKEN_OK);
        }
    }
}
