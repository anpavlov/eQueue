package com.sudo.equeue.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;

//import com.sudo.equeue.fragments.AboutFragment;
//import com.sudo.equeue.fragments.PrefsFragment;
//import com.sudo.equeue.fragments.SearchFormFragment;
//import com.sudo.equeue.fragments.SearchResultsFragment;
import com.sudo.equeue.fragments.QueueListFragment;
import com.sudo.equeue.fragments.StartFragment;
import com.sudo.equeue.models.basic.Queue;
import com.sudo.equeue.models.basic.QueueList;
import com.sudo.equeue.models.basic.User;
import com.sudo.equeue.utils.QueueApplication;
//import com.sudo.equeue.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends NetBaseActivity implements StartFragment.StartFragmentListener, QueueListFragment.SaveIdListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private QueueListFragment queueListFragment;
    private NavigationView navigationView;
    private SharedPreferences prefs;

    private int findQueuesRequestId;
    private int createUserRequestId;

//    private Map<String, Fragment> fragmentMap;
//    private final Fragment mSearchFormFragment = new SearchFormFragment();
//    private final Fragment mSearchResultsFragment = new SearchResultsFragment();
//    private final Fragment mAboutFragment = new AboutFragment();
//    private final Fragment mPrefFragment = new PrefsFragment();

//    private ProgressBar progressBar;
//    private List<Integer> searchRequestIds;

//    private final String searchFormFragmentKey = "search_form_fragment_key";
//    private final String searchResultsFragmentKey = "search_results_fragment_key";
//    private final String aboutFragmentKey = "about_fragment_key";
//    private final String prefFragmentKey = "pref_fragment_key";

//    private final String CURRENT_FRAGMENT_EXTRA = "CURRENT_FRAGMENT_EXTRA";
//    private final String CURRENT_FRAGMENT_SAVED_EXTRA = "CURRENT_FRAGMENT_SAVED_EXTRA";
//    private String currentFragmentKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        searchRequestIds = new ArrayList<>();
//        progressBar = (ProgressBar) findViewById(R.id.toolbar_progress);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {};
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

//        fragmentMap = new HashMap<>();
//        fragmentMap.put(searchFormFragmentKey, mSearchFormFragment);
//        fragmentMap.put(searchResultsFragmentKey, mSearchResultsFragment);
//        fragmentMap.put(aboutFragmentKey, mAboutFragment);
//        fragmentMap.put(prefFragmentKey, mPrefFragment);

        if (savedInstanceState == null) {
            StartFragment startFragment = (StartFragment) getFragmentManager().findFragmentByTag(StartFragment.TAG);
            if (startFragment == null) {
                startFragment = new StartFragment();
            }
            getFragmentManager().beginTransaction()
                    .add(R.id.main_content_frame, startFragment)
                    .commit();
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> navigationItemClicked(item));

        prefs = getSharedPreferences(QueueApplication.APP_PREFS, Context.MODE_PRIVATE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        updateMenu();
    }

    private void updateMenu() {
        Menu menu = navigationView.getMenu();

        if (prefs.getBoolean(QueueApplication.PREFS_USER_IS_LOGGED_IN, false)) {
            menu.findItem(R.id.nav_login).setVisible(false);
            menu.findItem(R.id.nav_register).setVisible(false);
            menu.findItem(R.id.nav_logout).setVisible(true);
        } else {
            menu.findItem(R.id.nav_login).setVisible(true);
            menu.findItem(R.id.nav_register).setVisible(true);
            menu.findItem(R.id.nav_logout).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

//    @Override
//    public void onSearchButtonClick() {
//        getFragmentManager().beginTransaction()
//                .replace(R.id.main_content_frame, mSearchResultsFragment)
//                .commit();
////        ((SearchResultsFragment) mSearchResultsFragment).onLoaderReset(null);
//    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == findQueuesRequestId) {
            if (resultCode == NetService.CODE_OK) {
                if (queueListFragment != null) {
                    queueListFragment.updateQueueList((QueueList) data.getSerializable(NetService.RETURN_QUEUE_LIST));
                }
            } else {
                Toast.makeText(this, "Error in request", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestId == createUserRequestId) {
            if (resultCode == NetService.CODE_OK) {
                User user = (User) data.getSerializable(NetService.RETURN_USER);
                if (user != null && user.getToken() != null && !user.getToken().equals("")) {
                    prefs.edit()
                            .putString(QueueApplication.PREFS_USER_TOKEN_KEY, user.getToken())
                            .putInt(QueueApplication.PREFS_USER_ID_KEY, user.getUid())
                            .commit();
                } else {
                    Toast.makeText(this, "Error in request", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error in request", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void findQueueButtonCallback() {
        queueListFragment = (QueueListFragment) getFragmentManager().findFragmentByTag(QueueListFragment.TAG);
        if (queueListFragment == null) {
            queueListFragment = new QueueListFragment();
        }

        Bundle args = new Bundle();
        args.putBoolean(QueueListFragment.ARGS_IS_MY, false);
        queueListFragment.setArguments(args);

        getFragmentManager().beginTransaction()
                .replace(R.id.main_content_frame, queueListFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void saveFindRequestId(int id) {
        findQueuesRequestId = id;
    }

    private boolean navigationItemClicked(MenuItem item) {
        mDrawerLayout.closeDrawers();

        if (!item.isChecked()) {
            switch (item.getItemId()) {
                case R.id.nav_create_queue: {
                    Intent intent = new Intent(this, QueueAdminActivity.class);
                    intent.putExtra(QueueAdminActivity.EXTRA_IS_NEW_QUEUE, true);
                    startActivity(intent);
                    break;
                }
                case R.id.nav_find_queue: {
                    queueListFragment = (QueueListFragment) getFragmentManager().findFragmentByTag(QueueListFragment.TAG);
                    if (queueListFragment == null) {
                        queueListFragment = new QueueListFragment();
                    }

                    Bundle args = new Bundle();
                    args.putBoolean(QueueListFragment.ARGS_IS_MY, false);
                    queueListFragment.setArguments(args);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.main_content_frame, queueListFragment)
                            .addToBackStack(null)
                            .commit();
                    break;
                }
                case R.id.nav_my_queues: {
                    queueListFragment = (QueueListFragment) getFragmentManager().findFragmentByTag(QueueListFragment.TAG);
                    if (queueListFragment == null) {
                        queueListFragment = new QueueListFragment();
                    }

                    Bundle args = new Bundle();
                    args.putBoolean(QueueListFragment.ARGS_IS_MY, true);
                    queueListFragment.setArguments(args);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.main_content_frame, queueListFragment)
                            .addToBackStack(null)
                            .commit();
                    break;
                }
                case R.id.nav_login: {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    break;
                }
                case R.id.nav_register: {
                    Intent intent = new Intent(this, RegisterActivity.class);
                    startActivity(intent);
                    break;
                }
                case R.id.nav_logout: {
                    prefs.edit().remove(QueueApplication.PREFS_USER_IS_LOGGED_IN).remove(QueueApplication.PREFS_USER_TOKEN_KEY).remove(QueueApplication.PREFS_USER_ID_KEY).commit();
                    createUserRequestId = getServiceHelper().createUser(null, null, false);
                    updateMenu();
                    break;
                }
            }
        }

        return true;
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString(CURRENT_FRAGMENT_EXTRA, currentFragmentKey);
////        outState.putParcelable(CURRENT_FRAGMENT_SAVED_EXTRA, getFragmentManager().saveFragmentInstanceState(fragmentMap.get(currentFragmentKey)));
//    }
}
