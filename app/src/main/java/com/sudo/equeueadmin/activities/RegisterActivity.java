package com.sudo.equeueadmin.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.sudo.equeueadmin.NetBaseActivity;
import com.sudo.equeueadmin.NetService;
import com.sudo.equeueadmin.R;
import com.sudo.equeueadmin.models.User;
import com.sudo.equeueadmin.utils.AlertDialogHelper;
import com.sudo.equeueadmin.utils.QueueApplication;

public class RegisterActivity extends NetBaseActivity {

    private int createUserRequestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Регистрация");
        }

        findViewById(R.id.btn_register).setOnClickListener(v -> register());
    }

    private void register() {
        String email = ((EditText) findViewById(R.id.field_email)).getText().toString();
        String name = ((EditText) findViewById(R.id.field_name)).getText().toString();
        String passwordOne = ((EditText) findViewById(R.id.field_password_one)).getText().toString();
        String passwordTwo = ((EditText) findViewById(R.id.field_password_two)).getText().toString();
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            AlertDialogHelper.show(this, "Необходимо ввести корректный email");
            return;
        }
        if (!passwordOne.equals(passwordTwo)) {
            AlertDialogHelper.show(this, "Пароли не совпадают");
            return;
        }
        if (email.equals("") || passwordOne.equals("") || passwordTwo.equals("")) {
            AlertDialogHelper.show(this, "Необходимо заполнить все поля");
            return;
        }

        createUserRequestId = getServiceHelper().createUser(email, passwordOne, name, true);
    }

    private void startApp() {
        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void initUserPref(User user) {
//        ((QueueApplication) getApplicationContext()).
        if (user != null && user.getToken() != null && !user.getToken().equals("")) {
            SharedPreferences prefs = getSharedPreferences(QueueApplication.APP_PREFS, Context.MODE_PRIVATE);
            prefs.edit()
                    .putString(QueueApplication.PREFS_USER_TOKEN_KEY, user.getToken())
//                    .putString(QueueApplication.PREFS_USER_NAME, user.getUsername())
//                    .putString(QueueApplication.PREFS_USER_EMAIL, user.getEmail())
                    .putInt(QueueApplication.PREFS_USER_ID_KEY, user.getUid())
                    .putBoolean(QueueApplication.PREFS_USER_IS_LOGGED_IN, true)
                    .commit();
//            setResult(RESULT_OK, null);
            startApp();
        } else {
            AlertDialogHelper.show(this, "Ошибка при запросе");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == createUserRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_USER, obj -> initUserPref((User) obj), null);
        }
    }
}
