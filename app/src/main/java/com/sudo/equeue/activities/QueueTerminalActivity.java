package com.sudo.equeue.activities;

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

public class QueueTerminalActivity extends NetBaseActivity {

    public static final String EXTRA_QUEUE_ID = QueueApplication.prefix + ".extra.queue_id";

//    private int createRequestId;
    private int getQueueRequestId;
//    private int saveInfoRequestId;
//    private int callRequestId;
//
    private Queue queueInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_terminal);

        if (savedInstanceState == null) {
            getQueueRequestId = getServiceHelper().getQueue(getIntent().getIntExtra(EXTRA_QUEUE_ID, -1));
        }

        findViewById(R.id.btn_join).setOnClickListener(v -> join());
        findViewById(R.id.btn_hide).setOnClickListener(v -> hide());
    }

    private void join() {
        findViewById(R.id.number_lbl).setVisibility(View.VISIBLE);
        findViewById(R.id.number_field).setVisibility(View.VISIBLE);
        findViewById(R.id.code_lbl).setVisibility(View.VISIBLE);
        findViewById(R.id.code_field).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_hide).setVisibility(View.VISIBLE);

    }

    private void hide() {
        findViewById(R.id.number_lbl).setVisibility(View.INVISIBLE);
        findViewById(R.id.number_field).setVisibility(View.INVISIBLE);
        findViewById(R.id.code_lbl).setVisibility(View.INVISIBLE);
        findViewById(R.id.code_field).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_hide).setVisibility(View.INVISIBLE);
    }

    private void updateQueueView() {
        ((TextView) findViewById(R.id.name_field)).setText(queueInfo.getName());
        ((TextView) findViewById(R.id.description_field)).setText(queueInfo.getDescription());


        if (queueInfo.getUserlist() != null && !queueInfo.getUserlist().isEmpty()) {
            int count = queueInfo.getUserlist().size();
            int current = queueInfo.getUserlist().get(0);
            ((TextView) findViewById(R.id.count_field)).setText(String.valueOf(count));

            findViewById(R.id.current_lbl).setVisibility(View.VISIBLE);
            findViewById(R.id.current_field).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.current_field)).setText(String.valueOf(current));
        } else {
            findViewById(R.id.current_lbl).setVisibility(View.INVISIBLE);
            findViewById(R.id.current_field).setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.count_field)).setText("0");
        }
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == getQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> {
                queueInfo = (Queue) obj;
                updateQueueView();
            }, NetService.RETURN_QUEUE);
        }
//        else
//        if (requestId == saveInfoRequestId || requestId == callRequestId) {
//            getServiceHelper().handleResponse(this, resultCode, data, obj -> {
//                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
//            }, null);
//        }
    }
}
