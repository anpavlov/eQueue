package com.sudo.equeueadmin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.sudo.equeueadmin.NetBaseActivity;
import com.sudo.equeueadmin.NetService;
import com.sudo.equeueadmin.R;
import com.sudo.equeueadmin.models.Queue;
import com.sudo.equeueadmin.utils.QueueApplication;

public class AdminQueueActivity extends NetBaseActivity {

    public static final String EXTRA_QUEUE = QueueApplication.prefix + ".extra.queue";

    private static final String SAVED_STATE_QUEUE = QueueApplication.prefix + ".QueueAdminActivity.saved.queue";
//    private static final String SAVED_STATE_ID_CREATE = QueueApplication.prefix + ".QueueAdminActivity.saved.id_create";
    private static final String SAVED_STATE_ID_GET_QUEUE = QueueApplication.prefix + ".QueueAdminActivity.saved.id_get_queue";
//    private static final String SAVED_STATE_ID_SAVE = QueueApplication.prefix + ".QueueAdminActivity.saved.id_save";
    private static final String SAVED_STATE_ID_CALL = QueueApplication.prefix + ".QueueAdminActivity.saved.id_call";

    private static final int EDIT_QUEUE_REQ_ID = 5135;

    private int getQueueRequestId = -1;
    private int callRequestId = -1;

    private Queue queueInfo;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Управление очередью");
        }

        if (savedInstanceState == null) {
            queueInfo = (Queue) getIntent().getSerializableExtra(EXTRA_QUEUE);
        } else {
            queueInfo = (Queue) savedInstanceState.getSerializable(SAVED_STATE_QUEUE);
            callRequestId = savedInstanceState.getInt(SAVED_STATE_ID_CALL, -1);
            getQueueRequestId = savedInstanceState.getInt(SAVED_STATE_ID_GET_QUEUE, -1);
        }

        if (queueInfo == null) {
            Toast.makeText(this, "Error: queue is null", Toast.LENGTH_LONG).show();
            finish();
        } else {
            findViewById(R.id.btn_next).setOnClickListener(v -> callNext());

            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_queue_view);
            swipeRefreshLayout.setOnRefreshListener(() -> {
                refresh();
            });
            updateQueueView();
        }
    }

    private void refresh() {
        getQueueRequestId = getServiceHelper().getQueue(queueInfo.getQid());
    }

    private void callNext() {
        if (queueInfo != null) {
            callRequestId = getServiceHelper().callNext(queueInfo.getQid());
        }
    }

//    private void openTerminal() {
//        Intent intent = new Intent(this, QueueTerminalActivity.class);
//        intent.putExtra(AdminActivity.EXTRA_IS_NEW_QUEUE, false);
//        intent.putExtra(AdminActivity.EXTRA_QUEUE_ID, queueInfo.getQid());
//        startActivity(intent);
//    }

    private void updateQueueView() {
        ((TextView) findViewById(R.id.name)).setText(queueInfo.getName());
        ((TextView) findViewById(R.id.description)).setText(queueInfo.getDescription());
        ((TextView) findViewById(R.id.inqueue)).setText("в очереди\n" + Integer.toString(queueInfo.getUserlist() == null ? 0 : queueInfo.getUserlist().size()));
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_edit:
                editQueue();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editQueue() {
        Intent intent = new Intent(this, EditQueueActivity.class);
        intent.putExtra(EXTRA_QUEUE, queueInfo);
        startActivityForResult(intent, EDIT_QUEUE_REQ_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (data == null) {return;}
//        String name = data.getStringExtra("name");
//        tvName.setText("Your name is " + name);
        if (requestCode == EDIT_QUEUE_REQ_ID) {
            if (data == null || resultCode != RESULT_OK) {
                Toast.makeText(this, "Error while editing", Toast.LENGTH_SHORT).show();
            } else {
                Queue queue = (Queue) data.getSerializableExtra(EXTRA_QUEUE);
                if (queue == null) {
                    Toast.makeText(this, "Error while editing", Toast.LENGTH_SHORT).show();
                } else {
                    queueInfo = queue;
                    updateQueueView();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVED_STATE_QUEUE, queueInfo);
        outState.putInt(SAVED_STATE_ID_CALL, callRequestId);
        outState.putInt(SAVED_STATE_ID_GET_QUEUE, getQueueRequestId);
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == getQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> {
                queueInfo = (Queue) obj;
                updateQueueView();
            }, NetService.RETURN_QUEUE);
        } else
        if (requestId == callRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> {
                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
            }, null);
        }
    }
}
