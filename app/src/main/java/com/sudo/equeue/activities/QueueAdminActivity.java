package com.sudo.equeue.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.models.basic.Queue;
import com.sudo.equeue.utils.QueueApplication;
import com.sudo.equeue.utils.ServiceHelper;

public class QueueAdminActivity extends NetBaseActivity {

    public static final String EXTRA_IS_NEW_QUEUE = QueueApplication.prefix + ".extra.is_new_queue";
    public static final String EXTRA_QUEUE_ID = QueueApplication.prefix + ".extra.queue_id";

    private int createRequestId;
    private int getQueueRequestId;
    private int saveInfoRequestId;
    private int callRequestId;

    private Queue queueInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        if (savedInstanceState != null) {
            if (getIntent().getBooleanExtra(EXTRA_IS_NEW_QUEUE, false)) {
                createRequestId = getServiceHelper().createQueue();
            } else {
                getQueueRequestId = getServiceHelper().getQueue(getIntent().getIntExtra(EXTRA_QUEUE_ID, -1));
            }
        }

        ((Button) findViewById(R.id.btn_save_info)).setOnClickListener(v -> saveInfo());
        ((Button) findViewById(R.id.btn_next)).setOnClickListener(v -> callNext());
    }

    private void saveInfo() {
        queueInfo.setName(((EditText) findViewById(R.id.name_field)).getText().toString());
        queueInfo.setDescription(((EditText) findViewById(R.id.description_field)).getText().toString());
        saveInfoRequestId = getServiceHelper().saveQueueInfo(queueInfo);
    }

    private void callNext() {
        callRequestId = getServiceHelper().callNext();
    }

    private void updateQueueView() {
        ((EditText) findViewById(R.id.name_field)).setText(queueInfo.getName());
        ((TextView) findViewById(R.id.description_field)).setText(queueInfo.getDescription());
        if (!queueInfo.getUserlist().isEmpty()) {
            for (String username : queueInfo.getUserlist()) {
                TextView userTextView = new TextView(this);
                userTextView.setText(username);
                ((LinearLayout) findViewById(R.id.list)).addView(userTextView);
            }
        }
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == createRequestId) {
            if (resultCode == NetService.CODE_OK) {
                queueInfo = (Queue) data.getSerializable(NetService.RETURN_QUEUE);
                updateQueueView();
            } else {
                Toast.makeText(this, "Error in request", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
