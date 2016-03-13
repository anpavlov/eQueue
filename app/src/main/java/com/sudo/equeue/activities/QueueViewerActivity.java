package com.sudo.equeue.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.models.Queue;
import com.sudo.equeue.utils.QueueApplication;

public class QueueViewerActivity extends NetBaseActivity {

//    public static final String EXTRA_IS_NEW_QUEUE = QueueApplication.prefix + ".extra.is_new_queue";
    public static final String EXTRA_QUEUE_ID = QueueApplication.prefix + ".extra.queue_id";

//    private int createRequestId;
    private int getQueueRequestId;
//    private int saveInfoRequestId;
    private int joinRequestId;

    private Queue queueInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_viewer);

        if (savedInstanceState == null) {
            getQueueRequestId = getServiceHelper().getQueue(getIntent().getIntExtra(EXTRA_QUEUE_ID, -1));
        }

        findViewById(R.id.btn_join).setOnClickListener(v -> join());
    }

//    private void saveInfo() {
//        queueInfo.setName(((EditText) findViewById(R.id.name_field)).getText().toString());
//        queueInfo.setDescription(((EditText) findViewById(R.id.description_field)).getText().toString());
//        saveInfoRequestId = getServiceHelper().saveQueueInfo(queueInfo);
//    }

    private void join() {
        if (queueInfo != null) {
            joinRequestId = getServiceHelper().joinQueue(queueInfo.getQid());
        }
    }

    private void updateQueueView() {
        ((TextView) findViewById(R.id.name_field)).setText(queueInfo.getName());
        ((TextView) findViewById(R.id.description_field)).setText(queueInfo.getDescription());

        LinearLayout list = (LinearLayout) findViewById(R.id.list);
        list.removeAllViews();
        if (!queueInfo.getUserlist().isEmpty()) {
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
        if (requestId == getQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> {
                queueInfo = (Queue) obj;
                updateQueueView();
            }, NetService.RETURN_QUEUE);

//            if (resultCode == NetService.CODE_OK) {
//                queueInfo = (Queue) data.getSerializable(NetService.RETURN_QUEUE);
//                updateQueueView();
//            } else {
//                Toast.makeText(this, "Error in request", Toast.LENGTH_SHORT).show();
//            }
        } else
        if (requestId == joinRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> {
//                TODO: переделать
                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                TextView userTextView = new TextView(this);
                userTextView.setText("me");
                findViewById(R.id.list).setVisibility(View.VISIBLE);
                findViewById(R.id.empty_lbl).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.list)).addView(userTextView);
            }, NetService.RETURN_QUEUE);

//            if (resultCode == NetService.CODE_OK) {
//                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
//                TextView userTextView = new TextView(this);
//                userTextView.setText("me");
//                findViewById(R.id.list).setVisibility(View.VISIBLE);
//                findViewById(R.id.empty_lbl).setVisibility(View.GONE);
//                ((LinearLayout) findViewById(R.id.list)).addView(userTextView);
//            } else {
//                Toast.makeText(this, "Error in request", Toast.LENGTH_SHORT).show();
//            }
        }
    }
}
