package com.sudo.equeue.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.models.basic.User;
import com.sudo.equeue.utils.QueueApplication;

public class StartActivity extends NetBaseActivity {

    private int createUserRequestId;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        prefs = getSharedPreferences(QueueApplication.APP_PREFS, Context.MODE_PRIVATE);

        String token = prefs.getString(QueueApplication.PREFS_USER_TOKEN_KEY, null);
        if (token == null || token.equals("")) {
            createUserRequestId = getServiceHelper().createUser(null, null, false);
        } else {
            startApp();
        }

//        new Handler().postDelayed(() -> {
//
//        }, 1500);
    }

    private void startApp() {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == createUserRequestId) {
            if (resultCode == NetService.CODE_OK) {
                User user = (User) data.getSerializable(NetService.RETURN_USER);
                if (user != null && user.getToken() != null && !user.getToken().equals("")) {
                    prefs.edit()
                         .putString(QueueApplication.PREFS_USER_TOKEN_KEY, user.getToken())
                         .putInt(QueueApplication.PREFS_USER_ID_KEY, user.getUid())
                         .commit();
                    startApp();
                } else {
                    Toast.makeText(this, "Error in request", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error in request", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
