package com.sudo.equeue.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;

//import com.sudo.equeue.fragments.AboutFragment;
//import com.sudo.equeue.fragments.PrefsFragment;
//import com.sudo.equeue.fragments.SearchFormFragment;
//import com.sudo.equeue.fragments.SearchResultsFragment;
import com.sudo.equeue.fragments.FindQueueFragment;
import com.sudo.equeue.fragments.LoginFragment;
import com.sudo.equeue.fragments.MyQueuesFragment;
//import com.sudo.equeue.fragments.StartFragment;
import com.sudo.equeue.fragments.ProfileFragment;
import com.sudo.equeue.models.User;
import com.sudo.equeue.utils.QueueApplication;
//import com.sudo.equeue.utils.ThemeUtils;


public class MainActivity extends NetBaseActivity implements /*StartFragment.StartFragmentListener,*/ LoginFragment.LoginFinishedCallback, ProfileFragment.LogoutListener /*, QueueListAbstractFragment.SaveIdListener*/ {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferences prefs;
    private Button authButton;

//    private int findQueuesRequestId = -1;
    private int createUserRequestId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {};
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        prefs = getSharedPreferences(QueueApplication.APP_PREFS, Context.MODE_PRIVATE);

        if (savedInstanceState == null) {
            FindQueueFragment findQueueFragment = (FindQueueFragment) getSupportFragmentManager().findFragmentByTag(FindQueueFragment.TAG);
            if (findQueueFragment == null) {
                findQueueFragment = new FindQueueFragment();
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content_frame, findQueueFragment, FindQueueFragment.TAG)
//                    .addToBackStack(null)
                    .commit();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> navigationItemClicked(item));

        authButton = (Button) navigationView.getHeaderView(0).findViewById(R.id.btn_auth);
        authButton.setOnClickListener(v -> onAuthCLick());

        if (prefs.getBoolean(QueueApplication.PREFS_USER_IS_LOGGED_IN, false)) {
            authButton.setText("Мой профиль");
        }

        navigationView.getMenu().findItem(R.id.nav_btn).getActionView().findViewById(R.id.btn_create_queue).setOnClickListener(v -> createQueue());
    }

    private void createQueue() {
        mDrawerLayout.closeDrawers();
        Intent intent = new Intent(this, QueueAdminActivity.class);
        intent.putExtra(QueueAdminActivity.EXTRA_IS_NEW_QUEUE, true);
        startActivity(intent);
    }

    //    TODO: вынести инициализацию юзера в Application
    private void initUserPref(User user) {
        if (user != null && user.getToken() != null && !user.getToken().equals("")) {
            prefs.edit()
                    .putString(QueueApplication.PREFS_USER_TOKEN_KEY, user.getToken())
                    .putInt(QueueApplication.PREFS_USER_ID_KEY, user.getUid())
                    .commit();
        } else {
            Toast.makeText(this, "Error in request", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == createUserRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> initUserPref((User) obj), NetService.RETURN_USER);
        }
    }
//
//    @Override
//    public void findQueueButtonCallback() {
//        queueListFragment = (QueueListAbstractFragment) getSupportFragmentManager().findFragmentByTag(QueueListAbstractFragment.TAG);
//        if (queueListFragment == null) {
//            queueListFragment = new QueueListAbstractFragment();
//        }
//
//        Bundle args = new Bundle();
//        args.putBoolean(QueueListAbstractFragment.ARGS_IS_MY, false);
//        queueListFragment.setArguments(args);
//
//        getFragmentManager().beginTransaction()
//                .replace(R.id.main_content_frame, queueListFragment)
//                .addToBackStack(null)
//                .commit();
//    }

    private void onAuthCLick() {
        mDrawerLayout.closeDrawers();
        if (!prefs.getBoolean(QueueApplication.PREFS_USER_IS_LOGGED_IN, false)) {
            LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(LoginFragment.TAG);
            if (loginFragment == null) {
                loginFragment = new LoginFragment();
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content_frame, loginFragment, LoginFragment.TAG)
//                    .addToBackStack(null)
                    .commit();
        } else {
            ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(ProfileFragment.TAG);
            if (profileFragment == null) {
                profileFragment = new ProfileFragment();
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content_frame, profileFragment, ProfileFragment.TAG)
//                    .addToBackStack(null)
                    .commit();
        }
    }

//    private void updateUserStatus() {
//
//    }

    @Override
    public void onLoginFinished() {
        if (prefs.getBoolean(QueueApplication.PREFS_USER_IS_LOGGED_IN, false)) {
            authButton.setText("Мой профиль");
        }

        ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(ProfileFragment.TAG);
        if (profileFragment == null) {
            profileFragment = new ProfileFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content_frame, profileFragment, ProfileFragment.TAG)
//                    .addToBackStack(null)
                .commit();
    }

    @Override
    public void onLogout() {
        if (!prefs.getBoolean(QueueApplication.PREFS_USER_IS_LOGGED_IN, false)) {
            authButton.setText("Войти");
        }

        createUserRequestId = getServiceHelper().createUser(null, null, null, false);

        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(LoginFragment.TAG);
        if (loginFragment == null) {
            loginFragment = new LoginFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content_frame, loginFragment, LoginFragment.TAG)
//                    .addToBackStack(null)
                .commit();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_profile, menu);
//
//        return true;
//    }

    private boolean navigationItemClicked(MenuItem item) {
        mDrawerLayout.closeDrawers();

        if (!item.isChecked()) {
            switch (item.getItemId()) {
//                case R.id.nav_create_queue: {
//                    Intent intent = new Intent(this, QueueAdminActivity.class);
//                    intent.putExtra(QueueAdminActivity.EXTRA_IS_NEW_QUEUE, true);
//                    startActivity(intent);
//                    break;
//                }
                case R.id.nav_find_queue: {
                    FindQueueFragment findQueueFragment = (FindQueueFragment) getSupportFragmentManager().findFragmentByTag(FindQueueFragment.TAG);
                    if (findQueueFragment == null) {
                        findQueueFragment = new FindQueueFragment();
                    }

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_content_frame, findQueueFragment, FindQueueFragment.TAG)
//                            .addToBackStack(null)
                            .commit();
                    break;
//                    queueListFragment = (QueueListFragment) getFragmentManager().findFragmentByTag(QueueListFragment.TAG);
//                    if (queueListFragment == null) {
//                        queueListFragment = new QueueListFragment();
//                    }
//
//                    Bundle args = new Bundle();
//                    args.putBoolean(QueueListAbstractFragment.ARGS_IS_MY, false);
//                    queueListFragment.setArguments(args);
//
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.main_content_frame, queueListFragment)
//                            .addToBackStack(null)
//                            .commit();
//                    break;
                }
                case R.id.nav_my_queues: {
                    MyQueuesFragment myQueuesFragment = (MyQueuesFragment) getSupportFragmentManager().findFragmentByTag(MyQueuesFragment.TAG);
                    if (myQueuesFragment == null) {
                        myQueuesFragment = new MyQueuesFragment();
                    }

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_content_frame, myQueuesFragment, MyQueuesFragment.TAG)
//                            .addToBackStack(null)
                            .commit();
                    break;
                }
//                case R.id.nav_login: {
//                    Intent intent = new Intent(this, LoginActivity.class);
//                    startActivity(intent);
//                    break;
//                }
//                case R.id.nav_register: {
//                    Intent intent = new Intent(this, RegisterActivity.class);
//                    startActivity(intent);
//                    break;
//                }
//                case R.id.nav_logout: {
//                    prefs.edit().remove(QueueApplication.PREFS_USER_IS_LOGGED_IN).remove(QueueApplication.PREFS_USER_TOKEN_KEY).remove(QueueApplication.PREFS_USER_ID_KEY).commit();
//                    createUserRequestId = getServiceHelper().createUser(null, null, false);
//                    updateMenu();
//                    break;
//                }
            }
        }

        return true;
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        updateUserStatus();
//    }

//    private void updateMenu() {
//        Menu menu = navigationView.getMenu();
//
//        if (prefs.getBoolean(QueueApplication.PREFS_USER_IS_LOGGED_IN, false)) {
//            menu.findItem(R.id.nav_login).setVisible(false);
//            menu.findItem(R.id.nav_register).setVisible(false);
//            menu.findItem(R.id.nav_logout).setVisible(true);
//        } else {
//            menu.findItem(R.id.nav_login).setVisible(true);
//            menu.findItem(R.id.nav_register).setVisible(true);
//            menu.findItem(R.id.nav_logout).setVisible(false);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
