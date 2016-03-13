package com.sudo.equeue.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.sudo.equeue.NetBaseFragment;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.models.User;
import com.sudo.equeue.utils.QueueApplication;

public class ProfileFragment extends NetBaseFragment {

    public interface LogoutListener {
        void onLogout();
    }

    public static final String TAG = "com.sudo.fragments.ProfileFragment";

    private int updateUserRequest = -1;
    private SharedPreferences prefs;

    private EditText emailEdit;
    private EditText nameEdit;

    private String oldEmail;
    private String oldName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle("Профиль");
        }

        emailEdit = (EditText) view.findViewById(R.id.field_email);
        nameEdit = (EditText) view.findViewById(R.id.field_name);

        prefs = getActivity().getSharedPreferences(QueueApplication.APP_PREFS, Context.MODE_PRIVATE);

        oldEmail = prefs.getString(QueueApplication.PREFS_USER_EMAIL, "");
        oldName = prefs.getString(QueueApplication.PREFS_USER_NAME, "");

        emailEdit.setText(oldEmail);
        nameEdit.setText(oldName);

        view.findViewById(R.id.btn_save_profile).setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String email = emailEdit.getText().toString();
        String name = nameEdit.getText().toString();
//        String passwordTwo = ((EditText) findViewById(R.id.field_password_two)).getText().toString();
        if (!email.equals(oldEmail)) {
            if (email.equals("")) {
                Toast.makeText(getActivity(), "Empty field", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getActivity(), "It's not an email", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            email = null;
        }
        if (name.equals(oldName)) {
            name = null;
        }

        updateUserRequest = getServiceHelper().updateUser(email, name);
    }

    private void updateUser(User user) {
        if (user != null) {
            prefs.edit()
                    .putString(QueueApplication.PREFS_USER_EMAIL, user.getEmail())
                    .putString(QueueApplication.PREFS_USER_NAME, user.getUsername())
                    .putInt(QueueApplication.PREFS_USER_ID_KEY, user.getUid())
                    .commit();
            Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Error in request", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean logoutClick(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            prefs.edit()
                    .remove(QueueApplication.PREFS_USER_EMAIL)
                    .remove(QueueApplication.PREFS_USER_NAME)
                    .remove(QueueApplication.PREFS_USER_ID_KEY)
                    .remove(QueueApplication.PREFS_USER_TOKEN_KEY)
                    .remove(QueueApplication.PREFS_USER_IS_LOGGED_IN)
                    .commit();
            ((LogoutListener) getActivity()).onLogout();
            return true;
        }
        return false;
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == updateUserRequest) {
            getServiceHelper().handleResponse(getActivity(), resultCode, data, obj -> updateUser((User) obj), NetService.RETURN_USER);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return logoutClick(item) || super.onOptionsItemSelected(item);
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_toolbar, menu);
//        menu.getItem(0).setVisible(false);
//
//        return true;
//    }

}
