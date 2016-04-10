package com.sudo.equeueadmin.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sudo.equeueadmin.NetBaseActivity;
import com.sudo.equeueadmin.NetService;
import com.sudo.equeueadmin.R;
import com.sudo.equeueadmin.models.Queue;
import com.sudo.equeueadmin.models.QueueList;
import com.sudo.equeueadmin.utils.QueueApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;


public class MainActivity extends NetBaseActivity {

//    private SharedPreferences prefs;
    private Button addButton;

//    private int createUserRequestId = -1;
    private int getQueueListRequestId = -1;
    private int getQueueRequestId = -1;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    protected List<String> queueNamesList = new ArrayList<>();
    protected List<Queue> queueList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ваши очереди");
        }
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//        prefs = getSharedPreferences(QueueApplication.APP_PREFS, Context.MODE_PRIVATE);

        addButton = (Button) findViewById(R.id.add_queue);
        addButton.setOnClickListener(v -> onAddCLick());

        listView = (ListView) findViewById(R.id.queue_list);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, queueNamesList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> onQueueCLick(position));

        getQueueListRequestId = makeRequest();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_queue_list);
        swipeRefreshLayout.setOnRefreshListener(() -> getQueueListRequestId = makeRequest());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getQueueListRequestId = makeRequest();
        swipeRefreshLayout.setRefreshing(true);
    }

    public void updateQueueList(QueueList queues) {
        queueList = queues.getQueues();
        RelativeLayout no_queues = (RelativeLayout) findViewById(R.id.no_queue_layout);
        if(queueList.size() != 0) {
            no_queues.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            no_queues.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
        }
        queueNamesList.clear();
        for (Queue queue : queueList) {
            queueNamesList.add(queue.getName());
        }
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    protected int makeRequest() {
        return getServiceHelper().myQueues();
    }

    protected void onQueueCLick(int position) {
        getQueueRequestId = getServiceHelper().getQueue(queueList.get(position).getQid());
    }

    private void openQueue(Queue queue) {
        Intent intent = new Intent(this, AdminQueueActivity.class);
        intent.putExtra(AdminQueueActivity.EXTRA_QUEUE, queue);
        startActivity(intent);
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == getQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_QUEUE, obj -> openQueue((Queue) obj), null);
        } else
        if (requestId == getQueueListRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_QUEUE_LIST, obj -> updateQueueList((QueueList) obj), null);
        }
    }

    private void onAddCLick() {
        Intent intent = new Intent(this, CreateQueueActivity.class);
        startActivity(intent);
    }

//    private void openProfile() {
//        startActivity(new Intent(this, ProfileActivity.class));
//    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences(QueueApplication.APP_PREFS, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(QueueApplication.PREFS_USER_TOKEN_KEY)
                .remove(QueueApplication.PREFS_USER_ID_KEY)
                .remove(QueueApplication.PREFS_USER_IS_LOGGED_IN)
                .commit();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                onAddCLick();
                return true;
//            case R.id.profile:
//                openProfile();
//                return true;
            case R.id.logout:
                logout();
                return true;
        }

        return false;
    }
}
