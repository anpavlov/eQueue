package com.sudo.equeueadmin.activities;//package com.sudo.equeue.activities;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
//import android.view.MenuItem;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.sudo.equeue.NetBaseActivity;
//import com.sudo.equeue.NetService;
//import com.sudo.equeue.R;
//import com.sudo.equeue.models.User;
//import com.sudo.equeue.utils.QueueApplication;
//
//public class RegisterActivity extends NetBaseActivity {
//
//    private int createUserRequestId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle("Регистрация");
//        }
//
//        findViewById(R.id.btn_register).setOnClickListener(v -> register());
//    }
//
//    private void register() {
//        String email = ((EditText) findViewById(R.id.field_email)).getText().toString();
//        String name = ((EditText) findViewById(R.id.field_name)).getText().toString();
//        String passwordOne = ((EditText) findViewById(R.id.field_password_one)).getText().toString();
//        String passwordTwo = ((EditText) findViewById(R.id.field_password_two)).getText().toString();
//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            Toast.makeText(this, "It's not an email", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (!passwordOne.equals(passwordTwo)) {
//            Toast.makeText(this, "Different passwords", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (email.equals("") || passwordOne.equals("") || passwordTwo.equals("")) {
//            Toast.makeText(this, "Empty field", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        createUserRequestId = getServiceHelper().createUser(email, passwordOne, name, true);
//    }
//
//    //    TODO: вынести инициализацию юзера в Application
//    private void initUserPref(User user) {
////        ((QueueApplication) getApplicationContext()).
//        if (user != null && user.getToken() != null && !user.getToken().equals("")) {
//            SharedPreferences prefs = getSharedPreferences(QueueApplication.APP_PREFS, Context.MODE_PRIVATE);
//            prefs.edit()
//                    .putString(QueueApplication.PREFS_USER_TOKEN_KEY, user.getToken())
//                    .putString(QueueApplication.PREFS_USER_NAME, user.getUsername())
//                    .putString(QueueApplication.PREFS_USER_EMAIL, user.getEmail())
//                    .putInt(QueueApplication.PREFS_USER_ID_KEY, user.getUid())
//                    .putBoolean(QueueApplication.PREFS_USER_IS_LOGGED_IN, true)
//                    .commit();
//            setResult(RESULT_OK, null);
//            finish();
//        } else {
//            Toast.makeText(this, "Error in request", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
//        if (requestId == createUserRequestId) {
//            getServiceHelper().handleResponse(this, resultCode, data, obj -> initUserPref((User) obj), NetService.RETURN_USER);
//        }
//    }
//}
