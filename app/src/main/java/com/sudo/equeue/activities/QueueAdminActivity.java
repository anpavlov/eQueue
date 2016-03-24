package com.sudo.equeue.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.models.Queue;
import com.sudo.equeue.utils.QueueApplication;

public class QueueAdminActivity extends NetBaseActivity {

    public static final String EXTRA_IS_NEW_QUEUE = QueueApplication.prefix + ".extra.is_new_queue";
    public static final String EXTRA_QUEUE_ID = QueueApplication.prefix + ".extra.queue_id";

    private static final String SAVED_STATE_QUEUE = QueueApplication.prefix + ".QueueAdminActivity.saved.queue";
    private static final String SAVED_STATE_ID_CREATE = QueueApplication.prefix + ".QueueAdminActivity.saved.id_create";
    private static final String SAVED_STATE_ID_GET_QUEUE = QueueApplication.prefix + ".QueueAdminActivity.saved.id_get_queue";
    private static final String SAVED_STATE_ID_SAVE = QueueApplication.prefix + ".QueueAdminActivity.saved.id_save";
    private static final String SAVED_STATE_ID_CALL = QueueApplication.prefix + ".QueueAdminActivity.saved.id_call";

    private int createRequestId = -1;
    private int getQueueRequestId = -1;
    private int saveInfoRequestId = -1;
    private int callRequestId = -1;

    private Queue queueInfo;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_admin);

        if (savedInstanceState == null) {
            if (getIntent().getBooleanExtra(EXTRA_IS_NEW_QUEUE, false)) {
                createRequestId = getServiceHelper().createQueue();
            } else {
                getQueueRequestId = getServiceHelper().getQueue(getIntent().getIntExtra(EXTRA_QUEUE_ID, -1));
            }
        } else {
            queueInfo = (Queue) savedInstanceState.getSerializable(SAVED_STATE_QUEUE);
            createRequestId = savedInstanceState.getInt(SAVED_STATE_ID_CREATE, -1);
            callRequestId = savedInstanceState.getInt(SAVED_STATE_ID_CALL, -1);
            getQueueRequestId = savedInstanceState.getInt(SAVED_STATE_ID_GET_QUEUE, -1);
            saveInfoRequestId = savedInstanceState.getInt(SAVED_STATE_ID_SAVE, -1);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Управление очередью");
        }

        findViewById(R.id.btn_save_info).setOnClickListener(v -> saveInfo());
        findViewById(R.id.btn_next).setOnClickListener(v -> callNext());
        findViewById(R.id.btn_terminal).setOnClickListener(v -> openTerminal());

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_queue_view);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refresh();
        });
    }

    private void refresh() {
        getQueueRequestId = getServiceHelper().getQueue(queueInfo.getQid());
    }

    private void saveInfo() {
        queueInfo.setName(((EditText) findViewById(R.id.name_field)).getText().toString());
        queueInfo.setDescription(((EditText) findViewById(R.id.description_field)).getText().toString());
        saveInfoRequestId = getServiceHelper().saveQueueInfo(queueInfo);
    }

    private void callNext() {
        if (queueInfo != null) {
            callRequestId = getServiceHelper().callNext(queueInfo.getQid());
        }
    }

    private void openTerminal() {
        Intent intent = new Intent(this, QueueTerminalActivity.class);
        intent.putExtra(QueueAdminActivity.EXTRA_IS_NEW_QUEUE, false);
        intent.putExtra(QueueAdminActivity.EXTRA_QUEUE_ID, queueInfo.getQid());
        startActivity(intent);
    }

    private void updateQueueView() {
        ((TextView) findViewById(R.id.name_field)).setText(queueInfo.getName());
        ((TextView) findViewById(R.id.description_field)).setText(queueInfo.getDescription());

        LinearLayout list = (LinearLayout) findViewById(R.id.list);
        list.removeAllViews();
        if (queueInfo.getUserlist() != null && !queueInfo.getUserlist().isEmpty()) {
            for (Integer userId : queueInfo.getUserlist()) {
                TextView userTextView = (TextView) getLayoutInflater().inflate(R.layout.queue_list_element, null);
                userTextView.setText(userId.toString());
                list.addView(userTextView);
            }
            list.setVisibility(View.VISIBLE);
            findViewById(R.id.empty_lbl).setVisibility(View.GONE);
        } else {
            findViewById(R.id.empty_lbl).setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setRefreshing(false);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVED_STATE_QUEUE, queueInfo);
        outState.putInt(SAVED_STATE_ID_CREATE, createRequestId);
        outState.putInt(SAVED_STATE_ID_CALL, callRequestId);
        outState.putInt(SAVED_STATE_ID_GET_QUEUE, getQueueRequestId);
        outState.putInt(SAVED_STATE_ID_SAVE, saveInfoRequestId);
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == createRequestId || requestId == getQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> {
                queueInfo = (Queue) obj;
                updateQueueView();
            }, NetService.RETURN_QUEUE);
        } else
        if (requestId == saveInfoRequestId || requestId == callRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> {
                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
            }, null);
        }
    }
}
