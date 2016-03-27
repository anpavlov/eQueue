package com.sudo.equeue.activities;//package com.sudo.equeue.activities;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.sudo.equeue.NetBaseActivity;
//import com.sudo.equeue.NetService;
//import com.sudo.equeue.R;
//import com.sudo.equeue.models.User;
//import com.sudo.equeue.utils.QueueApplication;
//
//public class LoginActivity extends NetBaseActivity {
//
//    private static final int WEB_VIEW_REQUEST_ID = 5135;
//
//    private int loginRequestId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_login);
//
//        findViewById(R.id.btn_login_email).setOnClickListener(v -> loginViaEmail());
//        findViewById(R.id.btn_login_vk).setOnClickListener(v -> loginViaVk());
//    }
//
//    private void loginViaEmail() {
//        String email = ((EditText) findViewById(R.id.field_email)).getText().toString();
//        String password = ((EditText) findViewById(R.id.field_password)).getText().toString();
//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            Toast.makeText(this, "It's not an email", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (email.equals("") || password.equals("")) {
//            Toast.makeText(this, "Empty field", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        loginRequestId = getServiceHelper().loginEmail(email, password);
//    }
//
//    private void loginViaVk() {
//        Intent intent = new Intent(this, WebViewActivity.class);
//        startActivityForResult(intent, WEB_VIEW_REQUEST_ID);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == WEB_VIEW_REQUEST_ID) {
//            if (data == null || resultCode != RESULT_OK) {
//                Toast.makeText(this, "Error while logging", Toast.LENGTH_SHORT).show();
//            } else {
//                int vkuid = data.getIntExtra(WebViewActivity.EXTRA_VKUID, -1);
//                if (vkuid == -1) {
//                    Toast.makeText(this, "Error while logging", Toast.LENGTH_SHORT).show();
//                } else {
//                    loginRequestId = getServiceHelper().loginVk(vkuid);
//                }
//            }
//        }
//    }
//
//    //    TODO: вынести инициализацию юзера в Application
//    private void initUserPref(User user) {
//        if (user != null && user.getToken() != null && !user.getToken().equals("")) {
//            SharedPreferences prefs = getSharedPreferences(QueueApplication.APP_PREFS, Context.MODE_PRIVATE);
//            prefs.edit()
//                    .putString(QueueApplication.PREFS_USER_TOKEN_KEY, user.getToken())
//                    .putInt(QueueApplication.PREFS_USER_ID_KEY, user.getUid())
//                    .putBoolean(QueueApplication.PREFS_USER_IS_LOGGED_IN, true)
//                    .commit();
//            finish();
//        } else {
//            Toast.makeText(this, "Error in request", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
//        if (requestId == loginRequestId) {
//            getServiceHelper().handleResponse(this, resultCode, data, obj -> initUserPref((User) obj), NetService.RETURN_USER);
//        }
//    }
//}
