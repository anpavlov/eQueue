package com.sudo.equeueadmin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.sudo.equeueadmin.NetBaseActivity;
import com.sudo.equeueadmin.NetService;
import com.sudo.equeueadmin.R;
import com.sudo.equeueadmin.models.Queue;
import com.sudo.equeueadmin.utils.QueueApplication;

public class CreateQueueActivity extends NetBaseActivity {

    private static final String SAVED_STATE_ID_CREATE = QueueApplication.prefix + ".CreateQueueActivity.saved.id_create_queue";

    private int createQueueRequestId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_queue);

        if (savedInstanceState != null) {
            createQueueRequestId = savedInstanceState.getInt(SAVED_STATE_ID_CREATE, -1);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Создать очередь");
        }

        findViewById(R.id.btn_create).setOnClickListener(v -> createQueue());
    }

    private void createQueue() {
//        TODO: pass name and description
        createQueueRequestId = getServiceHelper().createQueue();
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
        outState.putInt(SAVED_STATE_ID_CREATE, createQueueRequestId);
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == createQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> {
                Queue queueInfo = (Queue) obj;
                if (queueInfo != null) {
                    Intent intent = new Intent(CreateQueueActivity.this, AdminQueueActivity.class);
                    intent.putExtra(AdminQueueActivity.EXTRA_QUEUE, queueInfo);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Error: queue is null", Toast.LENGTH_LONG).show();
                }
            }, NetService.RETURN_QUEUE);
        }
    }
}
