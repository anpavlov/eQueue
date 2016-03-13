package com.sudo.equeue.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.sudo.equeue.NetBaseFragment;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.activities.RegisterActivity;
import com.sudo.equeue.activities.WebViewActivity;
import com.sudo.equeue.models.User;
import com.sudo.equeue.utils.QueueApplication;

public class LoginFragment extends NetBaseFragment {

    public interface LoginFinishedCallback {
        void onLoginFinished();
    }

    private static final int WEB_VIEW_REQUEST_ID = 5135;
    private static final int REGISTER_REQUEST_ID = 2352;

    private int loginRequestId;

    public static final String TAG = "com.sudo.fragments.LoginFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_login_email).setOnClickListener(v -> loginViaEmail(view));
        view.findViewById(R.id.btn_login_vk).setOnClickListener(v -> loginViaVk());
        view.findViewById(R.id.btn_register).setOnClickListener(v -> register());

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Вход");
        }
    }

    private void loginViaEmail(View frag) {
        String email = ((EditText) frag.findViewById(R.id.field_email)).getText().toString();
        String password = ((EditText) frag.findViewById(R.id.field_password)).getText().toString();
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getActivity(), "It's not an email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.equals("") || password.equals("")) {
            Toast.makeText(getActivity(), "Empty field", Toast.LENGTH_SHORT).show();
            return;
        }

        loginRequestId = getServiceHelper().loginEmail(email, password);
    }

    private void loginViaVk() {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        startActivityForResult(intent, WEB_VIEW_REQUEST_ID);
    }

    private void register() {
        Intent intent = new Intent(getActivity(), RegisterActivity.class);
        startActivityForResult(intent, REGISTER_REQUEST_ID);
    }

    private void finishLogin() {
        ((LoginFinishedCallback) getActivity()).onLoginFinished();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WEB_VIEW_REQUEST_ID) {
            if (data == null || resultCode != Activity.RESULT_OK) {
                Toast.makeText(getActivity(), "Error while logging", Toast.LENGTH_SHORT).show();
            } else {
                int vkuid = data.getIntExtra(WebViewActivity.EXTRA_VKUID, -1);
                if (vkuid == -1) {
                    Toast.makeText(getActivity(), "Error while logging", Toast.LENGTH_SHORT).show();
                } else {
                    loginRequestId = getServiceHelper().loginVk(vkuid);
                }
            }
        } else if (requestCode == REGISTER_REQUEST_ID) {
            if (resultCode == Activity.RESULT_OK) {
                finishLogin();
            } else {
                Toast.makeText(getActivity(), "Error while register", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //    TODO: вынести инициализацию юзера в Application
    private void initUserPref(User user) {
        if (user != null && user.getToken() != null && !user.getToken().equals("")) {
            SharedPreferences prefs = getActivity().getSharedPreferences(QueueApplication.APP_PREFS, Context.MODE_PRIVATE);
            prefs.edit()
                    .putString(QueueApplication.PREFS_USER_TOKEN_KEY, user.getToken())
                    .putString(QueueApplication.PREFS_USER_NAME, user.getUsername())
                    .putString(QueueApplication.PREFS_USER_EMAIL, user.getEmail())
                    .putInt(QueueApplication.PREFS_USER_ID_KEY, user.getUid())
                    .putBoolean(QueueApplication.PREFS_USER_IS_LOGGED_IN, true)
                    .commit();
            finishLogin();
        } else {
            Toast.makeText(getActivity(), "Error in request", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == loginRequestId) {
            getServiceHelper().handleResponse(getActivity(), resultCode, data, obj -> initUserPref((User) obj), NetService.RETURN_USER);
        }
    }

}
