package com.sudo.equeueadmin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.sudo.equeueadmin.NetBaseActivity;
import com.sudo.equeueadmin.NetService;
import com.sudo.equeueadmin.R;
import com.sudo.equeueadmin.models.Queue;
import com.sudo.equeueadmin.utils.QueueApplication;

public class EditQueueActivity extends NetBaseActivity {

    private static final String SAVED_STATE_QUEUE = QueueApplication.prefix + ".QueueAdminActivity.saved.queue";
    private static final String SAVED_STATE_ID_SAVE = QueueApplication.prefix + ".QueueAdminActivity.saved.id_save";

    private int saveInfoRequestId = -1;
    private Queue queueInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_queue);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Параметры очереди");
        }

        if (savedInstanceState == null) {
            queueInfo = (Queue) getIntent().getSerializableExtra(AdminQueueActivity.EXTRA_QUEUE);
        } else {
            queueInfo = (Queue) savedInstanceState.getSerializable(SAVED_STATE_QUEUE);
            saveInfoRequestId = savedInstanceState.getInt(SAVED_STATE_ID_SAVE, -1);
        }

        if (queueInfo == null) {
            Toast.makeText(this, "Error: queue is null", Toast.LENGTH_LONG).show();
            finish();
        } else {
            ((EditText) findViewById(R.id.name_field)).setText(queueInfo.getName());
            ((EditText) findViewById(R.id.description_field)).setText(queueInfo.getDescription());
            findViewById(R.id.btn_save).setOnClickListener(v -> saveQueue());
            findViewById(R.id.btn_coords).setOnClickListener(v -> openMap());
        }
    }

    private void saveQueue() {
        queueInfo.setName(((EditText) findViewById(R.id.name_field)).getText().toString());
        queueInfo.setDescription(((EditText) findViewById(R.id.description_field)).getText().toString());
        saveInfoRequestId = getServiceHelper().saveQueueInfo(queueInfo);
    }

    private void openMap() {
        startActivity(new Intent(this, MapActivity.class));
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
        outState.putInt(SAVED_STATE_ID_SAVE, saveInfoRequestId);
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == saveInfoRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, null, obj -> {
                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra(AdminQueueActivity.EXTRA_QUEUE, queueInfo);
                setResult(RESULT_OK, intent);
                finish();
            }, null);
        }
    }
}

//Intent intent = new Intent();
//intent.putExtra(EXTRA_VKUID, Integer.parseInt(arg.split("=")[1]));
//setResult(RESULT_OK, intent);
//finish();