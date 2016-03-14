package com.sudo.equeue.activities;

import android.content.Intent;
import android.os.Bundle;
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

    private int createRequestId;
    private int getQueueRequestId;
    private int saveInfoRequestId;
    private int callRequestId;

    private Queue queueInfo;

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
        }

        findViewById(R.id.btn_save_info).setOnClickListener(v -> saveInfo());
        findViewById(R.id.btn_next).setOnClickListener(v -> callNext());
        findViewById(R.id.btn_terminal).setOnClickListener(v -> openTerminal());
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
        intent.putExtra(QueueAdminActivity.EXTRA_QUEUE_ID, getQueueRequestId);
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
        } else {
            findViewById(R.id.empty_lbl).setVisibility(View.VISIBLE);
        }
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
