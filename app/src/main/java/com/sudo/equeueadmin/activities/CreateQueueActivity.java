package com.sudo.equeueadmin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.sudo.equeueadmin.NetBaseActivity;
import com.sudo.equeueadmin.NetService;
import com.sudo.equeueadmin.R;
import com.sudo.equeueadmin.models.Queue;
import com.sudo.equeueadmin.utils.CustomSnackBar;
import com.sudo.equeueadmin.utils.QueueApplication;

import java.util.List;
import java.util.StringTokenizer;

public class CreateQueueActivity extends NetBaseActivity {

    private static final String SAVED_STATE_ID_CREATE = QueueApplication.prefix + ".CreateQueueActivity.saved.id_create_queue";

    private int createQueueRequestId = -1;
    private ArrayAdapter<String> adapter;

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
        if (createQueueRequestId == -1) {
            createQueueRequestId = getServiceHelper().createQueue(
                    ((EditText) findViewById(R.id.name_field)).getText().toString(),
                    ((EditText) findViewById(R.id.description_field)).getText().toString());
        }
    }

    private void creationSuccess(Queue queue) {
        if (queue != null) {
            Intent intent = new Intent(CreateQueueActivity.this, AdminQueueActivity.class);
            intent.putExtra(AdminQueueActivity.EXTRA_QUEUE, queue);
            startActivity(intent);
            finish();
        } else {
            LinearLayout createQueueLayout = (LinearLayout) findViewById(R.id.create_queue_layout);
            if(createQueueLayout != null) {
                CustomSnackBar.show(createQueueLayout, "Ошибка: queue is null");
            }
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_STATE_ID_CREATE, createQueueRequestId);
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == createQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_QUEUE, obj -> creationSuccess((Queue) obj), null);
        }
    }
}
